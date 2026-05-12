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
        return nguyenVongXetTuyenRepository.findByCccdOrderByThuTuNguyenVongAsc(cccd);
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

    public boolean isTrungTuyen(String ketQua) {
        if (ketQua == null) {
            return false;
        }
        String value = ketQua.trim().toLowerCase();
        return value.contains("trúng tuyển")
                || value.contains("trung tuyen")
                || value.equals("1")
                || value.equals("true");
    }
}