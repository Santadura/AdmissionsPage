package com.tuyensinh.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.repository.NganhRepository;
import com.tuyensinh.service.TinhDiemThptService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TinhDiemThptController {

    private final NganhRepository nganhRepository;
    private final TinhDiemThptService tinhDiemThptService;

    public TinhDiemThptController(NganhRepository nganhRepository,
            TinhDiemThptService tinhDiemThptService) {
        this.nganhRepository = nganhRepository;
        this.tinhDiemThptService = tinhDiemThptService;
    }

    @GetMapping("/user/tinh-diem/thpt")
    public String showForm(HttpSession session, Model model) {
        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");

        if (thiSinh == null) {
            return "redirect:/login";
        }

        model.addAttribute("thiSinh", thiSinh);
        model.addAttribute("dsNganh", nganhRepository.findAll());
        model.addAttribute("phuongThuc", "VSAT");

        return "tinh-diem-thpt";
    }

    @PostMapping("/user/tinh-diem/thpt")
    public String tinhDiem(
            @RequestParam(value = "phuongThuc", required = false, defaultValue = "VSAT") String phuongThuc,
            @RequestParam("maNganh") String maNganh,

            @RequestParam(value = "toan", required = false) Double toan,
            @RequestParam(value = "ly", required = false) Double ly,
            @RequestParam(value = "hoa", required = false) Double hoa,
            @RequestParam(value = "sinh", required = false) Double sinh,
            @RequestParam(value = "van", required = false) Double van,
            @RequestParam(value = "su", required = false) Double su,
            @RequestParam(value = "dia", required = false) Double dia,
            @RequestParam(value = "tiengAnh", required = false) Double tiengAnh,

            @RequestParam(value = "nk1", required = false) Double nk1,
            @RequestParam(value = "nk2", required = false) Double nk2,
            @RequestParam(value = "nk3", required = false) Double nk3,
            @RequestParam(value = "nk4", required = false) Double nk4,

            @RequestParam(value = "khuVuc", required = false) String khuVuc,
            @RequestParam(value = "doiTuong", required = false) String doiTuong,
            @RequestParam(value = "diemCong", required = false) Double diemCong,

            HttpSession session,
            Model model) {

        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");

        if (thiSinh == null) {
            return "redirect:/login";
        }

        Map<String, Object> ketQua = tinhDiemThptService.tinhDiem(
                phuongThuc,
                maNganh,
                toan,
                ly,
                hoa,
                sinh,
                van,
                su,
                dia,
                tiengAnh,
                nk1,
                nk2,
                nk3,
                nk4,
                khuVuc,
                doiTuong,
                diemCong);

        model.addAttribute("thiSinh", thiSinh);
        model.addAttribute("dsNganh", nganhRepository.findAll());

        model.addAttribute("ketQua", ketQua);
        model.addAttribute("selectedMaNganh", maNganh);

        model.addAttribute("phuongThuc", phuongThuc);

        model.addAttribute("toan", toan);
        model.addAttribute("ly", ly);
        model.addAttribute("hoa", hoa);
        model.addAttribute("sinh", sinh);
        model.addAttribute("van", van);
        model.addAttribute("su", su);
        model.addAttribute("dia", dia);
        model.addAttribute("tiengAnh", tiengAnh);

        model.addAttribute("nk1", nk1);
        model.addAttribute("nk2", nk2);
        model.addAttribute("nk3", nk3);
        model.addAttribute("nk4", nk4);

        model.addAttribute("khuVuc", khuVuc);
        model.addAttribute("doiTuong", doiTuong);
        model.addAttribute("diemCong", diemCong);

        return "tinh-diem-thpt";
    }
}