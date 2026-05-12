package com.tuyensinh.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tuyensinh.entity.NguyenVongXetTuyen;
import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.service.KetQuaXetTuyenService;

import jakarta.servlet.http.HttpSession;

@Controller
public class KetQuaXetTuyenController {

    private final KetQuaXetTuyenService ketQuaXetTuyenService;

    public KetQuaXetTuyenController(KetQuaXetTuyenService ketQuaXetTuyenService) {
        this.ketQuaXetTuyenService = ketQuaXetTuyenService;
    }

    @GetMapping("/user/ket-qua")
    public String ketQuaXetTuyen(HttpSession session, Model model) {
        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");
        if (thiSinh == null) {
            return "redirect:/login";
        }

        List<NguyenVongXetTuyen> ketQuaList = ketQuaXetTuyenService.findByCccd(thiSinh.getCccd());

        model.addAttribute("thiSinh", thiSinh);
        model.addAttribute("ketQuaList", ketQuaList);
        model.addAttribute("ketQuaService", ketQuaXetTuyenService);

        return "ket-qua-xet-tuyen";
    }
}