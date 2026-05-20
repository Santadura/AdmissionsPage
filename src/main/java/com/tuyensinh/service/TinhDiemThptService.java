package com.tuyensinh.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tuyensinh.entity.Nganh;
import com.tuyensinh.entity.NganhToHop;
import com.tuyensinh.repository.NganhRepository;
import com.tuyensinh.repository.NganhToHopRepository;

@Service
public class TinhDiemThptService {

    private final NganhRepository nganhRepository;
    private final NganhToHopRepository nganhToHopRepository;

    public TinhDiemThptService(NganhRepository nganhRepository,
            NganhToHopRepository nganhToHopRepository) {
        this.nganhRepository = nganhRepository;
        this.nganhToHopRepository = nganhToHopRepository;
    }

    public Map<String, Object> tinhDiem(
            String phuongThuc,
            String maNganh,
            Double toan,
            Double ly,
            Double hoa,
            Double sinh,
            Double van,
            Double su,
            Double dia,
            Double tiengAnh,
            Double nk1,
            Double nk2,
            Double nk3,
            Double nk4,
            String khuVuc,
            String doiTuong,
            Double diemCong) {

        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            phuongThuc = "VSAT";
        }
        phuongThuc = phuongThuc.trim().toUpperCase();

        if (toan == null)
            toan = 0.0;
        if (ly == null)
            ly = 0.0;
        if (hoa == null)
            hoa = 0.0;
        if (sinh == null)
            sinh = 0.0;
        if (van == null)
            van = 0.0;
        if (su == null)
            su = 0.0;
        if (dia == null)
            dia = 0.0;
        if (tiengAnh == null)
            tiengAnh = 0.0;
        if (diemCong == null)
            diemCong = 0.0;
        if (khuVuc == null)
            khuVuc = "";
        if (doiTuong == null)
            doiTuong = "";

        if (nk1 == null)
            nk1 = 0.0;

        if (nk2 == null)
            nk2 = 0.0;

        if (nk3 == null)
            nk3 = 0.0;

        if (nk4 == null)
            nk4 = 0.0;

        Nganh nganh = nganhRepository.findByMaNganh(maNganh).orElse(null);
        List<NganhToHop> dsToHop = nganhToHopRepository.findByMaNganh(maNganh);

        String tenNganh = "";
        String toHopGoc = "";
        Double diemSan = null;
        Double diemTrungTuyen = null;

        if (nganh != null) {
            tenNganh = nganh.getTenNganh();
            diemSan = nganh.getDiemSan();
            diemTrungTuyen = nganh.getDiemTrungTuyen();
            toHopGoc = nganh.getToHopGoc() == null ? "" : nganh.getToHopGoc().trim();
        }

        Map<String, Double> diemGoc = new LinkedHashMap<>();
        diemGoc.put("TO", toan);
        diemGoc.put("LI", ly);
        diemGoc.put("HO", hoa);
        diemGoc.put("SI", sinh);
        diemGoc.put("VA", van);
        diemGoc.put("SU", su);
        diemGoc.put("DI", dia);
        diemGoc.put("N1", tiengAnh);
        diemGoc.put("TI", tiengAnh);
        diemGoc.put("NK1", nk1);
        diemGoc.put("NK2", nk2);
        diemGoc.put("NK3", nk3);
        diemGoc.put("NK4", nk4);

        double diemKhuVuc = tinhDiemKhuVuc(khuVuc);
        double diemDoiTuong = tinhDiemDoiTuong(doiTuong);
        double mucDiemUuTien = diemKhuVuc + diemDoiTuong;

        List<Map<String, Object>> ketQuaToHop = new ArrayList<>();

        for (NganhToHop th : dsToHop) {
            String maToHop = safe(th.getMaToHop());
            String mon1 = safe(th.getMon1());
            String mon2 = safe(th.getMon2());
            String mon3 = safe(th.getMon3());

            double hs1 = th.getHsMon1() == null ? 1.0 : th.getHsMon1();
            double hs2 = th.getHsMon2() == null ? 1.0 : th.getHsMon2();
            double hs3 = th.getHsMon3() == null ? 1.0 : th.getHsMon3();
            double W = hs1 + hs2 + hs3;

            double g1 = diemGoc.getOrDefault(mon1, 0.0);
            double g2 = diemGoc.getOrDefault(mon2, 0.0);
            double g3 = diemGoc.getOrDefault(mon3, 0.0);

            QuyDoiMon q1 = xuLyDiemMon(phuongThuc, mon1, g1);
            QuyDoiMon q2 = xuLyDiemMon(phuongThuc, mon2, g2);
            QuyDoiMon q3 = xuLyDiemMon(phuongThuc, mon3, g3);

            double xetNguong = q1.diemQuyDoi + q2.diemQuyDoi + q3.diemQuyDoi + mucDiemUuTien;

            double dthxt = ((q1.diemQuyDoi * hs1) + (q2.diemQuyDoi * hs2) + (q3.diemQuyDoi * hs3)) / W * 3.0;

            double doLech = layDoLech(toHopGoc, maToHop);
            double dthgxt = dthxt - doLech;

            double dut;
            if ((dthgxt + diemCong) < 22.5) {
                dut = mucDiemUuTien;
            } else {
                dut = ((30.0 - dthxt - diemCong) / 7.5) * mucDiemUuTien;
                if (dut < 0)
                    dut = 0;
            }

            double dxt = dthgxt + diemCong + dut;
            if (dxt > 30)
                dxt = 30;

            boolean datSan = diemSan != null && dxt >= diemSan;
            boolean datTrungTuyen = diemTrungTuyen != null && diemTrungTuyen > 0 && dxt >= diemTrungTuyen;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("maToHop", maToHop);
            item.put("mon1", tenMon(mon1));
            item.put("mon2", tenMon(mon2));
            item.put("mon3", tenMon(mon3));
            item.put("maMon1", mon1);
            item.put("maMon2", mon2);
            item.put("maMon3", mon3);

            item.put("diemMon1Text", String.format("%.2f", g1));
            item.put("diemMon2Text", String.format("%.2f", g2));
            item.put("diemMon3Text", String.format("%.2f", g3));

            item.put("ctMon1", q1.congThuc);
            item.put("ctMon2", q2.congThuc);
            item.put("ctMon3", q3.congThuc);

            item.put("xetNguongText", String.format(
                    "(%.2f + %.2f + %.2f + %.2f) = <span class='badge-yellow'>%.2f</span>",
                    q1.diemQuyDoi, q2.diemQuyDoi, q3.diemQuyDoi, mucDiemUuTien, xetNguong));

            item.put("congThucToHop", String.format("(%s * %.0f + %s * %.0f + %s * %.0f) / %.0f * 3",
                    mon1, hs1, mon2, hs2, mon3, hs3, W));

            item.put("diemToHopText", String.format(
                    "(%.2f * %.0f + %.2f * %.0f + %.2f * %.0f) / %.0f * 3 = <span class='badge-blue'>%.3f</span>",
                    q1.diemQuyDoi, hs1, q2.diemQuyDoi, hs2, q3.diemQuyDoi, hs3, W, dthxt));

            item.put("doLechText", String.format("%.2f", doLech));
            item.put("dthgxtText", String.format("%.3f - %.2f = %.2f", dthxt, doLech, dthgxt));
            item.put("diemUuTienText", String.format("%.2f", dut));
            item.put("diemXetTuyen", dxt);
            item.put("diemXetTuyenText",
                    String.format("%.3f + %.2f + %.2f - %.2f = <span class='badge-red'>%.2f</span>",
                            dthgxt, diemCong, dut, doLech, dxt));

            item.put("datSan", datSan);
            item.put("datTrungTuyen", datTrungTuyen);

            ketQuaToHop.add(item);
        }

        ketQuaToHop.sort(Comparator.comparing((Map<String, Object> m) -> (Double) m.get("diemXetTuyen")).reversed());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("phuongThuc", phuongThuc);
        result.put("maNganh", maNganh);
        result.put("tenNganh", tenNganh);
        result.put("toHopGoc", toHopGoc);
        result.put("diemSan", diemSan);
        result.put("diemTrungTuyen", diemTrungTuyen);
        result.put("diemUuTien", mucDiemUuTien);
        result.put("khuVucText", hienThiKhuVuc(khuVuc));
        result.put("doiTuongText", hienThiDoiTuong(doiTuong));
        result.put("diemKhuVuc", diemKhuVuc);
        result.put("diemDoiTuong", diemDoiTuong);
        result.put("ketQuaToHop", ketQuaToHop);

        return result;
    }

    private QuyDoiMon xuLyDiemMon(String phuongThuc, String mon, double diem) {

        String m = safe(mon);

        // Môn năng khiếu chỉ dùng cho THPT
        if (m.startsWith("NK")) {

            if (!"THPT".equalsIgnoreCase(phuongThuc)) {
                return new QuyDoiMon(0.0,
                        "Môn năng khiếu không áp dụng cho phương thức VSAT");
            }

            return new QuyDoiMon(diem,
                    String.format("Điểm năng khiếu dùng trực tiếp = %.2f", diem));
        }

        // THPT
        if ("THPT".equalsIgnoreCase(phuongThuc)) {
            return new QuyDoiMon(diem,
                    String.format("Điểm THPT dùng trực tiếp = %.2f", diem));
        }

        // VSAT
        return quyDoiVsat(mon, diem);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private String tenMon(String maMon) {
        return switch (safe(maMon)) {
            case "TO" -> "Toán";
            case "LI" -> "Vật lí";
            case "HO" -> "Hóa học";
            case "SI" -> "Sinh học";
            case "VA" -> "Ngữ văn";
            case "SU" -> "Lịch sử";
            case "DI" -> "Địa lí";
            case "N1", "TI" -> "Tiếng Anh";

            case "NK1" -> "Năng khiếu 1";
            case "NK2" -> "Năng khiếu 2";
            case "NK3" -> "Năng khiếu 3";
            case "NK4" -> "Năng khiếu 4";

            default -> maMon;
        };
    }

    private double layDoLech(String toHopGoc, String maToHop) {
        String goc = safe(toHopGoc);
        String toh = safe(maToHop);

        if (goc.equals(toh))
            return 0.0;

        Map<String, Double> map = new LinkedHashMap<>();
        map.put("A00_A01", -0.69);
        map.put("A00_B00", -1.21);
        map.put("A00_C00", 2.32);
        map.put("A00_C01", 0.94);
        map.put("A00_D01", -0.68);
        map.put("A00_D07", -1.62);

        map.put("A01_A00", 0.69);
        map.put("A01_B00", -0.52);
        map.put("A01_C00", 3.01);
        map.put("A01_C01", 1.63);
        map.put("A01_D01", 0.01);
        map.put("A01_D07", -0.93);

        map.put("B00_A00", 1.21);
        map.put("B00_A01", 0.52);
        map.put("B00_C00", 3.53);
        map.put("B00_C01", 2.15);
        map.put("B00_D01", 0.53);
        map.put("B00_D07", -0.41);

        map.put("C00_A00", -2.32);
        map.put("C00_A01", -3.01);
        map.put("C00_B00", -3.53);
        map.put("C00_C01", -1.38);
        map.put("C00_D01", -3.00);
        map.put("C00_D07", -3.94);

        map.put("C01_A00", -0.94);
        map.put("C01_A01", -1.63);
        map.put("C01_B00", -2.15);
        map.put("C01_C00", 1.38);
        map.put("C01_D01", -1.62);
        map.put("C01_D07", -2.56);

        map.put("D01_A00", 0.68);
        map.put("D01_A01", -0.01);
        map.put("D01_B00", -0.53);
        map.put("D01_C00", 3.00);
        map.put("D01_C01", 1.62);
        map.put("D01_D07", -0.94);

        return map.getOrDefault(goc + "_" + toh, 0.0);
    }

    private QuyDoiMon quyDoiVsat(String mon, double x) {
        String m = safe(mon);
        List<double[]> ranges = switch (m) {
            case "TO" -> List.of(
                    new double[] { 132, 150, 8.5, 10 },
                    new double[] { 128.5, 132, 8.1, 8.5 },
                    new double[] { 122.5, 128.5, 7.75, 8.1 },
                    new double[] { 114.5, 122.5, 7.0, 7.75 },
                    new double[] { 108, 114.5, 6.6, 7.0 },
                    new double[] { 102.5, 108, 6.25, 6.6 },
                    new double[] { 97, 102.5, 6.0, 6.25 },
                    new double[] { 91, 97, 5.6, 6.0 },
                    new double[] { 85, 91, 5.25, 5.6 },
                    new double[] { 77, 85, 5.0, 5.25 },
                    new double[] { 68, 77, 4.5, 5.0 },
                    new double[] { 6, 68, 1.5, 4.5 });
            case "LI" -> List.of(
                    new double[] { 123, 147, 9.5, 10 },
                    new double[] { 118.5, 123, 9.25, 9.5 },
                    new double[] { 112.5, 118.5, 9.0, 9.25 },
                    new double[] { 105, 112.5, 8.5, 9.0 },
                    new double[] { 99.5, 105, 8.0, 8.5 },
                    new double[] { 94.5, 99.5, 7.75, 8.0 },
                    new double[] { 90, 94.5, 7.5, 7.75 },
                    new double[] { 85, 90, 7.25, 7.5 },
                    new double[] { 80, 85, 6.75, 7.25 },
                    new double[] { 74, 80, 6.35, 6.75 },
                    new double[] { 66.5, 74, 5.75, 6.35 },
                    new double[] { 17, 66.5, 3.05, 5.75 });
            case "HO" -> List.of(
                    new double[] { 129, 150, 9.5, 10 },
                    new double[] { 124.5, 129, 9.25, 9.5 },
                    new double[] { 117, 124.5, 8.75, 9.25 },
                    new double[] { 107.5, 117, 8.25, 8.75 },
                    new double[] { 100.5, 107.5, 7.75, 8.25 },
                    new double[] { 94, 100.5, 7.25, 7.75 },
                    new double[] { 88, 94, 6.75, 7.25 },
                    new double[] { 81.5, 88, 6.25, 6.75 },
                    new double[] { 75.5, 81.5, 5.75, 6.25 },
                    new double[] { 68.5, 75.5, 5.25, 5.75 },
                    new double[] { 59.5, 68.5, 4.6, 5.25 },
                    new double[] { 20, 59.5, 1.35, 4.6 });
            case "SI" -> List.of(
                    new double[] { 130.5, 150, 9.0, 9.75 },
                    new double[] { 126.5, 130.5, 8.75, 9.0 },
                    new double[] { 120.5, 126.5, 8.34, 8.75 },
                    new double[] { 112.5, 120.5, 7.85, 8.34 },
                    new double[] { 105.5, 112.5, 7.5, 7.85 },
                    new double[] { 100, 105.5, 7.25, 7.5 },
                    new double[] { 94.5, 100, 6.85, 7.25 },
                    new double[] { 88.5, 94.5, 6.5, 6.85 },
                    new double[] { 82.5, 88.5, 6.25, 6.5 },
                    new double[] { 76, 82.5, 5.85, 6.25 },
                    new double[] { 66.5, 76, 5.25, 5.85 },
                    new double[] { 26.5, 66.5, 2.8, 5.25 });
            case "SU" -> List.of(
                    new double[] { 133.5, 150, 9.75, 10 },
                    new double[] { 131, 133.5, 9.5, 9.75 },
                    new double[] { 126.5, 131, 9.25, 9.5 },
                    new double[] { 120.5, 126.5, 9.0, 9.25 },
                    new double[] { 115, 120.5, 8.5, 9.0 },
                    new double[] { 110, 115, 8.25, 8.5 },
                    new double[] { 105.5, 110, 8.0, 8.25 },
                    new double[] { 101, 105.5, 7.75, 8.0 },
                    new double[] { 95.5, 101, 7.5, 7.75 },
                    new double[] { 88.5, 95.5, 7.0, 7.5 },
                    new double[] { 79.5, 88.5, 6.35, 7.0 },
                    new double[] { 36.5, 79.5, 2.95, 6.35 });
            case "DI" -> List.of(
                    new double[] { 124, 141, 10, 10 },
                    new double[] { 120.5, 124, 10, 10 },
                    new double[] { 115.5, 120.5, 9.75, 10 },
                    new double[] { 108.5, 115.5, 9.25, 9.75 },
                    new double[] { 103, 108.5, 9.0, 9.25 },
                    new double[] { 98.5, 103, 8.75, 9.0 },
                    new double[] { 94, 98.5, 8.5, 8.75 },
                    new double[] { 89.5, 94, 8.25, 8.5 },
                    new double[] { 84.5, 89.5, 7.75, 8.25 },
                    new double[] { 79, 84.5, 7.25, 7.75 },
                    new double[] { 71, 79, 6.5, 7.25 },
                    new double[] { 31, 71, 3.0, 6.5 });
            case "N1", "TI" -> List.of(
                    new double[] { 131, 150, 7.75, 9.75 },
                    new double[] { 127.5, 131, 7.5, 7.75 },
                    new double[] { 120.5, 127.5, 7.0, 7.5 },
                    new double[] { 112, 120.5, 6.5, 7.0 },
                    new double[] { 105, 112, 6.0, 6.5 },
                    new double[] { 98.5, 105, 5.75, 6.0 },
                    new double[] { 92, 98.5, 5.5, 5.75 },
                    new double[] { 85.5, 92, 5.25, 5.5 },
                    new double[] { 78.5, 85.5, 5.0, 5.25 },
                    new double[] { 70.5, 78.5, 4.5, 5.0 },
                    new double[] { 60, 70.5, 4.0, 4.5 },
                    new double[] { 20.5, 60, 1.25, 4.0 });
            case "VA" -> List.of(
                    new double[] { 129.5, 146, 9.25, 9.75 },
                    new double[] { 127.5, 129.5, 9.0, 9.25 },
                    new double[] { 124, 127.5, 9.0, 9.0 },
                    new double[] { 119.5, 124, 8.75, 9.0 },
                    new double[] { 115.5, 119.5, 8.5, 8.75 },
                    new double[] { 112.5, 115.5, 8.25, 8.5 },
                    new double[] { 109, 112.5, 8.0, 8.25 },
                    new double[] { 106, 109, 7.75, 8.0 },
                    new double[] { 102, 106, 7.5, 7.75 },
                    new double[] { 97, 102, 7.25, 7.5 },
                    new double[] { 90, 97, 6.75, 7.25 },
                    new double[] { 5, 90, 3.5, 6.75 });
            default -> List.of();
        };

        for (double[] r : ranges) {
            double a = r[0], b = r[1], c = r[2], d = r[3];
            if (x > a && x <= b) {
                double y;
                String ct;
                if (Math.abs(b - a) < 0.000001 || Math.abs(d - c) < 0.000001) {
                    y = d;
                    ct = String.format("%.2f", y);
                } else {
                    y = c + ((x - a) / (b - a)) * (d - c);
                    ct = String.format("%.2f + (%.2f - %.2f) / (%.2f - %.2f) * (%.2f - %.2f) = %.2f",
                            c, x, a, b, a, d, c, y);
                }
                return new QuyDoiMon(y, ct);
            }
        }

        return new QuyDoiMon(0.0, "Không có dữ liệu quy đổi phù hợp, tính = 0");
    }

    private String hienThiKhuVuc(String khuVuc) {
        String kv = safe(khuVuc);

        return switch (kv) {
            case "KV1" -> "KV1";
            case "KV2-NT", "2NT" -> "2NT";
            case "KV2" -> "KV2";
            case "KV3" -> "KV3";
            default -> "Không có";
        };
    }

    private String hienThiDoiTuong(String doiTuong) {
        String dt = safe(doiTuong);

        return switch (dt) {
            case "01" -> "01";
            case "02" -> "02";
            case "03" -> "03";
            case "04" -> "04";
            case "05" -> "05";
            case "06", "06A" -> "06a";
            case "07" -> "07";
            default -> "Không có";
        };
    }

    private double tinhDiemKhuVuc(String khuVuc) {
        String kv = safe(khuVuc);

        return switch (kv) {
            case "KV1", "1", "1A" -> 0.75;
            case "KV2-NT", "2NT" -> 0.50;
            case "KV2", "2" -> 0.25;
            default -> 0.0;
        };
    }

    private double tinhDiemDoiTuong(String doiTuong) {
        String dt = safe(doiTuong);

        return switch (dt) {
            case "01", "01A" -> 2.0;
            case "02", "03", "04" -> 1.0;
            case "05", "06", "07", "06A" -> 0.5;
            default -> 0.0;
        };
    }

    private static class QuyDoiMon {
        double diemQuyDoi;
        String congThuc;

        QuyDoiMon(double diemQuyDoi, String congThuc) {
            this.diemQuyDoi = diemQuyDoi;
            this.congThuc = congThuc;
        }
    }
}