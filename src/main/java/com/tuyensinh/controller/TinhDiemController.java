package com.tuyensinh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.repository.NganhRepository;
import com.tuyensinh.service.TinhDiemDgnlService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TinhDiemController {

    private final NganhRepository nganhRepository;
    private final TinhDiemDgnlService tinhDiemDgnlService;

    public TinhDiemController(NganhRepository nganhRepository,
                              TinhDiemDgnlService tinhDiemDgnlService) {
        this.nganhRepository = nganhRepository;
        this.tinhDiemDgnlService = tinhDiemDgnlService;
    }

    @GetMapping("/user/tinh-diem/dgnl")
    public String showDgnlForm(HttpSession session, Model model) {
        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");
        if (thiSinh == null) {
            return "redirect:/login";
        }

        model.addAttribute("thiSinh", thiSinh);
        model.addAttribute("dsNganh", nganhRepository.findAll());
        return "tinh-diem-dgnl";
    }

    @PostMapping("/user/tinh-diem/dgnl")
    public String tinhDiemDgnl(@RequestParam("diemDgnl") Double diemDgnl,
                               @RequestParam("maNganh") String maNganh,
                               @RequestParam(value = "khuVuc", required = false) String khuVuc,
                               @RequestParam(value = "doiTuong", required = false) String doiTuong,
                               @RequestParam(value = "diemCong", required = false) Double diemCong,
                               HttpSession session,
                               Model model) {
        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");
        if (thiSinh == null) {
            return "redirect:/login";
        }

        var ketQua = tinhDiemDgnlService.tinhDiem(diemDgnl, maNganh, khuVuc, doiTuong, diemCong);

        model.addAttribute("thiSinh", thiSinh);
        model.addAttribute("dsNganh", nganhRepository.findAll());
        model.addAttribute("ketQua", ketQua);

        model.addAttribute("selectedMaNganh", maNganh);
        model.addAttribute("diemDgnl", diemDgnl);
        model.addAttribute("diemCong", diemCong);
        model.addAttribute("khuVuc", khuVuc);
        model.addAttribute("doiTuong", doiTuong);

        return "tinh-diem-dgnl";
    }
}