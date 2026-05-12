package com.tuyensinh.controller;

import java.util.List;
import java.util.Map;

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
        model.addAttribute("dsToHop", List.of("A00", "A01", "B00", "D01"));
        return "tinh-diem-dgnl";
    }

    @PostMapping("/user/tinh-diem/dgnl")
    public String tinhDiemDgnl(@RequestParam("diemDgnl") Double diemDgnl,
                               @RequestParam("maNganh") String maNganh,
                               @RequestParam("toHop") String toHop,
                               @RequestParam("diemUuTien") Double diemUuTien,
                               @RequestParam("diemCong") Double diemCong,
                               HttpSession session,
                               Model model) {
        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");
        if (thiSinh == null) {
            return "redirect:/login";
        }

        Map<String, Object> ketQua = tinhDiemDgnlService.tinhDiem(diemDgnl, maNganh, toHop, diemUuTien, diemCong);

        model.addAttribute("thiSinh", thiSinh);
        model.addAttribute("dsNganh", nganhRepository.findAll());
        model.addAttribute("dsToHop", List.of("A00", "A01", "B00", "D01"));
        model.addAttribute("ketQua", ketQua);
        model.addAttribute("selectedMaNganh", maNganh);
        model.addAttribute("selectedToHop", toHop);
        model.addAttribute("diemDgnl", diemDgnl);
        model.addAttribute("diemUuTien", diemUuTien);
        model.addAttribute("diemCong", diemCong);

        return "tinh-diem-dgnl";
    }
}