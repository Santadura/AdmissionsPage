package com.tuyensinh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecalculateVsatRunner {

    private static final String URL = "jdbc:mysql://localhost:3306/tuyen_sinh_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";

    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static final Map<String, List<QuyDoiRange>> BANG_QUY_DOI = new HashMap<>();

    public static void main(String[] args) {

        String sql = """
                SELECT
                    nv.idnv,
                    nv.nn_cccd,
                    nv.nv_manganh,
                    nv.nv_matohop,
                    nv.diem_cong,
                    nv.diem_utqd,
                    nv.diem_xettuyen,

                    n.n_tohopgoc,

                    th.th_mon1,
                    th.hsmon1,
                    th.th_mon2,
                    th.hsmon2,
                    th.th_mon3,
                    th.hsmon3,
                    th.dolech,

                    d.TO,
                    d.LI,
                    d.HO,
                    d.SI,
                    d.VA,
                    d.SU,
                    d.DI,
                    d.N1_THI

                FROM xt_nguyenvongxettuyen nv

                JOIN xt_nganh n
                    ON CAST(n.manganh AS CHAR) = CAST(nv.nv_manganh AS CHAR)

                JOIN xt_nganh_tohop th
                    ON CAST(th.manganh AS CHAR) = CAST(nv.nv_manganh AS CHAR)
                   AND CAST(th.matohop AS CHAR) = CAST(nv.nv_matohop AS CHAR)

                JOIN xt_diemthixettuyen d
                    ON d.cccd = CONCAT('TS_', nv.nn_cccd)

                    WHERE nv.tt_phuongthuc = 5
                """;

        String updateSql = """
                UPDATE xt_nguyenvongxettuyen
                SET diem_xettuyen = ?
                WHERE idnv = ?
                """;

        try (
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                ResultSet rs = ps.executeQuery()) {

            conn.setAutoCommit(false);

            loadBangQuyDoi(conn);

            int total = 0;
            int changed = 0;

            while (rs.next()) {

                total++;

                int idnv = rs.getInt("idnv");

                double diemCong = rs.getDouble("diem_cong");
                double diemUt = rs.getDouble("diem_utqd");

                String mon1 = rs.getString("th_mon1");
                String mon2 = rs.getString("th_mon2");
                String mon3 = rs.getString("th_mon3");

                double hs1 = rs.getDouble("hsmon1");
                double hs2 = rs.getDouble("hsmon2");
                double hs3 = rs.getDouble("hsmon3");

                double doLech = rs.getDouble("dolech");

                if (hs1 <= 0)
                    hs1 = 1.0;
                if (hs2 <= 0)
                    hs2 = 1.0;
                if (hs3 <= 0)
                    hs3 = 1.0;

                double g1 = layDiemGoc(rs, mon1);
                double g2 = layDiemGoc(rs, mon2);
                double g3 = layDiemGoc(rs, mon3);

                double d1 = quyDoiVsat(mon1, g1);
                double d2 = quyDoiVsat(mon2, g2);
                double d3 = quyDoiVsat(mon3, g3);

                double tongHeSo = hs1 + hs2 + hs3;

                double dthxt = ((d1 * hs1) + (d2 * hs2) + (d3 * hs3))
                        / tongHeSo * 3.0;

                double dthgxt = dthxt - doLech;

                double diemUuTien;

                if ((dthgxt + diemCong) < 22.5) {

                    diemUuTien = diemUt;

                } else {

                    diemUuTien = ((30.0 - dthgxt - diemCong) / 7.5) * diemUt;

                    if (diemUuTien < 0) {
                        diemUuTien = 0;
                    }
                }

                double diemMoi = dthgxt + diemCong + diemUuTien;

                if (diemMoi > 30) {
                    diemMoi = 30;
                }

                diemMoi = Math.round(diemMoi * 100.0) / 100.0;

                double diemCu = rs.getDouble("diem_xettuyen");

                if (Math.abs(diemMoi - diemCu) > 0.001) {

                    updatePs.setDouble(1, diemMoi);
                    updatePs.setInt(2, idnv);
                    updatePs.addBatch();

                    changed++;
                }

                if (changed > 0 && changed % 500 == 0) {

                    updatePs.executeBatch();
                    conn.commit();

                    System.out.println(
                            "Da update " + changed + " dong...");
                }
            }

            updatePs.executeBatch();
            conn.commit();

            System.out.println("Hoan tat.");
            System.out.println("Tong dong VSAT doc duoc: " + total);
            System.out.println("Tong dong duoc update: " + changed);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadBangQuyDoi(Connection conn) throws Exception {

        String sql = """
                SELECT
                    d_mon,
                    d_diema,
                    d_diemb,
                    d_diemc,
                    d_diemd
                FROM xt_bangquydoi
                WHERE d_phuongthuc = 'VSAT'
                """;

        try (
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String mon = safe(rs.getString("d_mon"));

                QuyDoiRange range = new QuyDoiRange(
                        rs.getDouble("d_diema"),
                        rs.getDouble("d_diemb"),
                        rs.getDouble("d_diemc"),
                        rs.getDouble("d_diemd"));

                BANG_QUY_DOI
                        .computeIfAbsent(mon,
                                k -> new ArrayList<>())
                        .add(range);
            }
        }

        System.out.println(
                "Da load bang quy doi VSAT: "
                        + BANG_QUY_DOI.size()
                        + " mon.");
    }

    private static double layDiemGoc(
            ResultSet rs,
            String mon) throws Exception {

        if (mon == null) {
            return 0.0;
        }

        Map<String, String> map = new HashMap<>();

        map.put("TO", "TO");
        map.put("LI", "LI");
        map.put("HO", "HO");
        map.put("SI", "SI");
        map.put("VA", "VA");
        map.put("SU", "SU");
        map.put("DI", "DI");
        map.put("N1", "N1_THI");
        map.put("TI", "N1_THI");

        String cot = map.get(mon.trim().toUpperCase());

        if (cot == null) {
            return 0.0;
        }

        return rs.getDouble(cot);
    }

    private static double quyDoiVsat(
            String mon,
            double diemGoc) {

        String maMon = safe(mon);

        List<QuyDoiRange> ds = BANG_QUY_DOI.get(maMon);

        if (ds == null || ds.isEmpty()) {
            return 0.0;
        }

        for (QuyDoiRange r : ds) {

            double min = Math.min(r.a, r.b);

            double max = Math.max(r.a, r.b);

            if (diemGoc >= min
                    && diemGoc <= max) {

                if (Math.abs(r.b - r.a) < 0.000001) {

                    return r.c;
                }

                return r.c
                        + ((diemGoc - r.a)
                                / (r.b - r.a))
                                * (r.d - r.c);
            }
        }

        return 0.0;
    }

    private static String safe(String s) {

        return s == null
                ? ""
                : s.trim().toUpperCase();
    }

    private static class QuyDoiRange {

        double a;
        double b;
        double c;
        double d;

        QuyDoiRange(
                double a,
                double b,
                double c,
                double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }
}