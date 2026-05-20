package com.tuyensinh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RecalculateDgnlRunner {

    private static final String URL = "jdbc:mysql://localhost:3306/tuyen_sinh_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        String selectSql = """
                    SELECT
                        nv.idnv,
                        nv.diem_xettuyen AS diem_cu,
                        nv.diem_cong,
                        nv.diem_utqd,
                        d.NL1 AS diem_dgnl,
                        bq.d_diema,
                        bq.d_diemb,
                        bq.d_diemc,
                        bq.d_diemd
                    FROM xt_nguyenvongxettuyen nv
                    JOIN xt_diemthixettuyen d
                        ON d.cccd = CONCAT('TS_', nv.nn_cccd)
                    JOIN xt_nganh n
                        ON n.manganh = nv.nv_manganh
                    JOIN xt_bangquydoi bq
                        ON bq.d_phuongthuc = 'DGNL'
                       AND bq.d_tohop = n.n_tohopgoc
                       AND d.NL1 BETWEEN bq.d_diema AND bq.d_diemb
                    WHERE nv.tt_phuongthuc = 4
                """;

        String updateSql = """
                    UPDATE xt_nguyenvongxettuyen
                    SET diem_xettuyen = ?
                    WHERE idnv = ?
                """;

        int total = 0;
        int changed = 0;

        try (
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                ResultSet rs = selectStmt.executeQuery()) {
            conn.setAutoCommit(false);

            while (rs.next()) {
                total++;

                int idnv = rs.getInt("idnv");
                double diemCu = rs.getDouble("diem_cu");
                double diemCong = rs.getDouble("diem_cong");
                double mucUuTien = rs.getDouble("diem_utqd");

                double diemDgnl = rs.getDouble("diem_dgnl");
                double a = rs.getDouble("d_diema");
                double b = rs.getDouble("d_diemb");
                double c = rs.getDouble("d_diemc");
                double d = rs.getDouble("d_diemd");

                double diemQuyDoi = c + ((diemDgnl - a) / (b - a)) * (d - c);

                double diemUuTien;
                if (diemQuyDoi + diemCong < 22.5) {
                    diemUuTien = mucUuTien;
                } else {
                    diemUuTien = ((30.0 - diemQuyDoi - diemCong) / 7.5) * mucUuTien;
                    if (diemUuTien < 0) {
                        diemUuTien = 0;
                    }
                }

                double diemMoi = diemQuyDoi + diemCong + diemUuTien;
                if (diemMoi > 30) {
                    diemMoi = 30;
                }

                diemMoi = Math.round(diemMoi * 100.0) / 100.0;

                if (Math.abs(diemMoi - diemCu) > 0.001) {
                    updateStmt.setDouble(1, diemMoi);
                    updateStmt.setInt(2, idnv);
                    updateStmt.addBatch();
                    changed++;
                }

                if (changed > 0 && changed % 500 == 0) {
                    updateStmt.executeBatch();
                    conn.commit();
                    System.out.println("Đã update " + changed + " dòng...");
                }
            }

            updateStmt.executeBatch();
            conn.commit();

            System.out.println("Hoàn tất.");
            System.out.println("Tổng dòng ĐGNL đọc được: " + total);
            System.out.println("Tổng dòng được update: " + changed);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}