package com.tuyensinh.service;

import java.util.ArrayList;
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

    public Map<String, Object> tinhDiem(String maNganh,
                                        Double toan,
                                        Double ly,
                                        Double hoa,
                                        Double sinh,
                                        Double van,
                                        Double su,
                                        Double dia,
                                        Double tiengAnh,
                                        Double diemUuTien,
                                        Double diemCong) {

        if (toan == null) toan = 0.0;
        if (ly == null) ly = 0.0;
        if (hoa == null) hoa = 0.0;
        if (sinh == null) sinh = 0.0;
        if (van == null) van = 0.0;
        if (su == null) su = 0.0;
        if (dia == null) dia = 0.0;
        if (tiengAnh == null) tiengAnh = 0.0;
        if (diemUuTien == null) diemUuTien = 0.0;
        if (diemCong == null) diemCong = 0.0;

        Map<String, Double> diemMap = new LinkedHashMap<>();
        diemMap.put("TO", toan);
        diemMap.put("LI", ly);
        diemMap.put("HO", hoa);
        diemMap.put("SI", sinh);
        diemMap.put("VA", van);
        diemMap.put("SU", su);
        diemMap.put("DI", dia);
        diemMap.put("N1", tiengAnh);
        diemMap.put("TI", tiengAnh);

        Nganh nganh = nganhRepository.findByMaNganh(maNganh).orElse(null);
        List<NganhToHop> dsToHop = nganhToHopRepository.findByMaNganh(maNganh);

        List<Map<String, Object>> ketQuaToHop = new ArrayList<>();

        Double diemSan = null;
        Double diemTrungTuyen = null;
        String tenNganh = "";

        if (nganh != null) {
            tenNganh = nganh.getTenNganh();
            diemSan = nganh.getDiemSan();
            diemTrungTuyen = nganh.getDiemTrungTuyen();
        }

        for (NganhToHop th : dsToHop) {
            double hs1 = th.getHsMon1() == null ? 1.0 : th.getHsMon1();
            double hs2 = th.getHsMon2() == null ? 1.0 : th.getHsMon2();
            double hs3 = th.getHsMon3() == null ? 1.0 : th.getHsMon3();

            double d1 = diemMap.getOrDefault(th.getMon1(), 0.0);
            double d2 = diemMap.getOrDefault(th.getMon2(), 0.0);
            double d3 = diemMap.getOrDefault(th.getMon3(), 0.0);

            double tong = d1 * hs1 + d2 * hs2 + d3 * hs3 + diemUuTien + diemCong;

            boolean datSan = diemSan != null && tong >= diemSan;
            boolean datTrungTuyen = diemTrungTuyen != null && tong >= diemTrungTuyen;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("maToHop", th.getMaToHop());
            item.put("mon1", th.getMon1());
            item.put("mon2", th.getMon2());
            item.put("mon3", th.getMon3());
            item.put("tongDiem", tong);
            item.put("datSan", datSan);
            item.put("datTrungTuyen", datTrungTuyen);

            ketQuaToHop.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("maNganh", maNganh);
        result.put("tenNganh", tenNganh);
        result.put("diemSan", diemSan);
        result.put("diemTrungTuyen", diemTrungTuyen);
        result.put("ketQuaToHop", ketQuaToHop);

        return result;
    }
}