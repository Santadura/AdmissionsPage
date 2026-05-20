package com.tuyensinh.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tuyensinh.entity.BangQuyDoi;
import com.tuyensinh.entity.Nganh;
import com.tuyensinh.repository.BangQuyDoiRepository;
import com.tuyensinh.repository.NganhRepository;

@Service
public class TinhDiemDgnlService {

    private final NganhRepository nganhRepository;
    private final BangQuyDoiRepository bangQuyDoiRepository;

    public TinhDiemDgnlService(NganhRepository nganhRepository,
            BangQuyDoiRepository bangQuyDoiRepository) {
        this.nganhRepository = nganhRepository;
        this.bangQuyDoiRepository = bangQuyDoiRepository;
    }

    public Map<String, Object> tinhDiem(Double diemDgnl,
            String maNganh,
            String khuVuc,
            String doiTuong,
            Double diemCong) {

        Map<String, Object> result = new LinkedHashMap<>();

        if (diemDgnl == null) {
            diemDgnl = 0.0;
        }
        if (diemCong == null) {
            diemCong = 0.0;
        }
        if (khuVuc == null) {
            khuVuc = "";
        }
        if (doiTuong == null) {
            doiTuong = "";
        }

        Nganh nganh = nganhRepository.findByMaNganh(maNganh).orElse(null);

        if (nganh == null) {
            throw new RuntimeException("Không tìm thấy ngành xét tuyển.");
        }

        String tenNganh = nganh.getTenNganh();
        String toHop = "D01";
        Double diemSan = nganh.getDiemSan();
        Double diemTrungTuyen = nganh.getDiemTrungTuyen();

        if (nganh.getToHopGoc() != null && !nganh.getToHopGoc().trim().isEmpty()) {
            toHop = nganh.getToHopGoc().trim().toUpperCase();
        }

        Map<String, Object> quyDoiInfo = timDiemQuyDoiDgnlVaCongThuc(diemDgnl, toHop);

        double diemQuyDoi = (Double) quyDoiInfo.get("diemQuyDoi");
        String congThucQuyDoi = (String) quyDoiInfo.get("congThucQuyDoi");

        double mucDiemUuTien = tinhMucUuTien(khuVuc, doiTuong);

        double diemUuTienQuyDoi;

        if ((diemQuyDoi + diemCong) < 22.5) {
            diemUuTienQuyDoi = mucDiemUuTien;
        } else {
            diemUuTienQuyDoi = ((30.0 - diemQuyDoi - diemCong) / 7.5) * mucDiemUuTien;

            if (diemUuTienQuyDoi < 0) {
                diemUuTienQuyDoi = 0;
            }
        }

        diemQuyDoi = round2(diemQuyDoi);
        diemUuTienQuyDoi = round2(diemUuTienQuyDoi);

        double tongDiem = diemQuyDoi + diemCong + diemUuTienQuyDoi;
        tongDiem = round2(tongDiem);

        if (tongDiem > 30) {
            tongDiem = 30;
        }

        String dienGiaiTongDiem = String.format("%.2f + %.2f + %.2f = %.2f",
                diemQuyDoi, diemCong, diemUuTienQuyDoi, tongDiem);

        boolean datDiemSan = diemSan != null && tongDiem >= diemSan;
        boolean datDiemTrungTuyen = diemTrungTuyen != null && tongDiem >= diemTrungTuyen;

        result.put("maNganh", maNganh);
        result.put("tenNganh", tenNganh);
        result.put("toHop", toHop);
        result.put("diemDgnl", diemDgnl);
        result.put("diemQuyDoi", diemQuyDoi);
        result.put("congThucQuyDoi", congThucQuyDoi);
        result.put("diemCong", diemCong);
        result.put("diemUuTien", diemUuTienQuyDoi);
        result.put("tongDiem", tongDiem);
        result.put("dienGiaiTongDiem", dienGiaiTongDiem);
        result.put("diemSan", diemSan);
        result.put("diemTrungTuyen", diemTrungTuyen);
        result.put("datDiemSan", datDiemSan);
        result.put("datDiemTrungTuyen", datDiemTrungTuyen);
        result.put("khuVuc", khuVuc);
        result.put("doiTuong", doiTuong);

        return result;
    }

    private Map<String, Object> timDiemQuyDoiDgnlVaCongThuc(Double diemDgnl, String toHop) {
        Map<String, Object> kq = new LinkedHashMap<>();

        List<BangQuyDoi> ds = bangQuyDoiRepository.findByPhuongThucAndToHopOrderByPhanViAsc("DGNL", toHop);

        if (ds == null || ds.isEmpty()) {
            throw new RuntimeException(
                    "Ngành này chưa hỗ trợ tính điểm ĐGNL vì chưa có bảng quy đổi cho tổ hợp gốc "
                            + toHop + ".");
        }

        for (BangQuyDoi item : ds) {
            Double diemA = item.getDiemA();
            Double diemB = item.getDiemB();
            Double diemC = item.getDiemC();
            Double diemD = item.getDiemD();

            if (diemA == null || diemB == null || diemC == null || diemD == null) {
                continue;
            }

            double minGoc = Math.min(diemA, diemB);
            double maxGoc = Math.max(diemA, diemB);

            if (diemDgnl >= minGoc && diemDgnl <= maxGoc) {
                double diemQuyDoi;

                if (Math.abs(diemB - diemA) < 0.000001) {
                    diemQuyDoi = diemC;
                    kq.put("diemQuyDoi", diemQuyDoi);
                    kq.put("congThucQuyDoi",
                            String.format("%.2f", diemQuyDoi));
                    return kq;
                }

                double tyLe = (diemDgnl - diemA) / (diemB - diemA);
                diemQuyDoi = diemC + tyLe * (diemD - diemC);

                String congThuc = String.format("%.2f + (%.0f - %.2f) / (%.2f - %.2f) * (%.2f - %.2f)",
                        diemC, diemDgnl, diemA, diemB, diemA, diemD, diemC);

                kq.put("diemQuyDoi", diemQuyDoi);
                kq.put("congThucQuyDoi", congThuc);
                return kq;
            }
        }

        throw new RuntimeException(
                "Điểm ĐGNL " + String.format("%.0f", diemDgnl)
                        + " không nằm trong khoảng quy đổi của tổ hợp gốc " + toHop + ".");
    }

    private double tinhMucUuTien(String khuVuc, String doiTuong) {
        String kv = khuVuc == null ? "" : khuVuc.trim().toUpperCase();
        String dt = doiTuong == null ? "" : doiTuong.trim().toUpperCase();

        double diemKhuVuc = switch (kv) {
            case "KV1", "1", "1A" -> 0.75;
            case "KV2-NT", "2NT" -> 0.50;
            case "KV2", "2" -> 0.25;
            default -> 0.0;
        };

        double diemDoiTuong = switch (dt) {
            case "01", "01A", "02", "03", "04" -> 2.0;
            case "05", "06", "06A", "07" -> 1.0;
            default -> 0.0;
        };

        return diemKhuVuc + diemDoiTuong;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}