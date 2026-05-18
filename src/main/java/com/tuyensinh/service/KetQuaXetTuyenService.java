package com.tuyensinh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tuyensinh.entity.Nganh;
import com.tuyensinh.entity.NguyenVongXetTuyen;
import com.tuyensinh.repository.NganhRepository;
import com.tuyensinh.repository.NguyenVongXetTuyenRepository;

@Service
public class KetQuaXetTuyenService {

    private final NguyenVongXetTuyenRepository nguyenVongXetTuyenRepository;
    private final NganhRepository nganhRepository;

    public KetQuaXetTuyenService(NguyenVongXetTuyenRepository nguyenVongXetTuyenRepository,
            NganhRepository nganhRepository) {
        this.nguyenVongXetTuyenRepository = nguyenVongXetTuyenRepository;
        this.nganhRepository = nganhRepository;
    }

    public List<NguyenVongXetTuyen> findByCccd(String cccd) {
        String cccdNguyenVong = chuanHoaCccdChoNguyenVong(cccd);
        return nguyenVongXetTuyenRepository.findByCccdOrderByThuTuNguyenVongAsc(cccdNguyenVong);
    }

    private String chuanHoaCccdChoNguyenVong(String cccd) {
        if (cccd == null) {
            return "";
        }

        String value = cccd.trim();

        if (value.startsWith("TS_")) {
            return value.substring(3);
        }

        return value;
    }

    public String getTenNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return "";
        }

        return nganhRepository.findByMaNganh(maNganh)
                .map(Nganh::getTenNganh)
                .orElse("");
    }

    public Double getDiemTrungTuyen(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return null;
        }

        return nganhRepository.findByMaNganh(maNganh)
                .map(Nganh::getDiemTrungTuyen)
                .orElse(null);
    }

    public String getDiemTrungTuyenText(String maNganh) {
        Double diem = getDiemTrungTuyen(maNganh);

        if (diem == null || diem <= 0) {
            return "Chưa công bố";
        }

        return String.format("%.2f", diem);
    }

    public String getTenPhuongThuc(String phuongThuc) {
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            return "Không xác định";
        }

        String value = phuongThuc.trim();

        return switch (value) {
            case "2" -> "V-SAT";
            case "3" -> "THPT";
            case "4" -> "ĐGNL";
            default -> "Không xác định";
        };
    }

    public String getToHopHienThi(NguyenVongXetTuyen item) {
        if (item == null) {
            return "Không có";
        }

        if (item.getMaToHop() != null && !item.getMaToHop().trim().isEmpty()) {
            return item.getMaToHop();
        }

        if (item.getToHop() != null && !item.getToHop().trim().isEmpty()) {
            return item.getToHop();
        }

        return "Không có";
    }

    public String getKetQuaText(String ketQua) {
        if (isTrungTuyen(ketQua)) {
            return "Trúng tuyển";
        }

        return "Không trúng tuyển";
    }

    public boolean isTrungTuyen(String ketQua) {
        if (ketQua == null) {
            return false;
        }

        String value = ketQua.trim().toLowerCase();

        return value.contains("trúng tuyển")
                || value.contains("trung tuyen")
                || value.equals("yes")
                || value.equals("1")
                || value.equals("true");
    }
}