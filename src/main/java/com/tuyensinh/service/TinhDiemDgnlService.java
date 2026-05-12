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

    public Map<String, Object> tinhDiem(Double diemDgnl, String maNganh, String toHop, Double diemUuTien, Double diemCong) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (diemDgnl == null) diemDgnl = 0.0;
        if (diemUuTien == null) diemUuTien = 0.0;
        if (diemCong == null) diemCong = 0.0;

        Nganh nganh = nganhRepository.findByMaNganh(maNganh).orElse(null);

        double diemQuyDoi = timDiemQuyDoiDgnl(diemDgnl, toHop);
        double tongDiem = diemQuyDoi + diemUuTien + diemCong;

        Double diemSan = null;
        Double diemTrungTuyen = null;
        String tenNganh = "";

        if (nganh != null) {
            tenNganh = nganh.getTenNganh();
            diemSan = nganh.getDiemSan();
            diemTrungTuyen = nganh.getDiemTrungTuyen();
        }

        boolean datDiemSan = diemSan != null && tongDiem >= diemSan;
        boolean datDiemTrungTuyen = diemTrungTuyen != null && tongDiem >= diemTrungTuyen;

        result.put("maNganh", maNganh);
        result.put("tenNganh", tenNganh);
        result.put("toHop", toHop);
        result.put("diemDgnl", diemDgnl);
        result.put("diemQuyDoi", diemQuyDoi);
        result.put("diemUuTien", diemUuTien);
        result.put("diemCong", diemCong);
        result.put("tongDiem", tongDiem);
        result.put("diemSan", diemSan);
        result.put("diemTrungTuyen", diemTrungTuyen);
        result.put("datDiemSan", datDiemSan);
        result.put("datDiemTrungTuyen", datDiemTrungTuyen);

        return result;
    }

    private double timDiemQuyDoiDgnl(Double diemDgnl, String toHop) {
        List<BangQuyDoi> ds;

        if (toHop != null && !toHop.trim().isEmpty()) {
            ds = bangQuyDoiRepository.findByPhuongThucAndToHopOrderByPhanViAsc("DGNL", toHop);
        } else {
            ds = bangQuyDoiRepository.findByPhuongThucOrderByToHopAscPhanViAsc("DGNL");
        }

        if (ds == null || ds.isEmpty()) {
            return diemDgnl * 30.0 / 1200.0;
        }

        for (BangQuyDoi item : ds) {
            Double minGoc = item.getDiemA();
            Double maxGoc = item.getDiemB();
            Double minQuyDoi = item.getDiemC();
            Double maxQuyDoi = item.getDiemD();

            if (minGoc == null || maxGoc == null || minQuyDoi == null || maxQuyDoi == null) {
                continue;
            }

            if (diemDgnl >= minGoc && diemDgnl <= maxGoc) {
                if (maxGoc.equals(minGoc)) {
                    return minQuyDoi;
                }

                double tyLe = (diemDgnl - minGoc) / (maxGoc - minGoc);
                return minQuyDoi + tyLe * (maxQuyDoi - minQuyDoi);
            }
        }

        BangQuyDoi dau = ds.get(0);
        if (dau.getDiemA() != null && diemDgnl < dau.getDiemA() && dau.getDiemC() != null) {
            return dau.getDiemC();
        }

        BangQuyDoi cuoi = ds.get(ds.size() - 1);
        if (cuoi.getDiemB() != null && diemDgnl > cuoi.getDiemB() && cuoi.getDiemD() != null) {
            return cuoi.getDiemD();
        }

        return diemDgnl * 30.0 / 1200.0;
    }
}