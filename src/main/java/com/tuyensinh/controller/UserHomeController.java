package com.tuyensinh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tuyensinh.entity.ThiSinh;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserHomeController {

    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        ThiSinh thiSinh = (ThiSinh) session.getAttribute("loggedInThiSinh");
        if (thiSinh == null) {
            return "redirect:/login";
        }

        model.addAttribute("thiSinh", thiSinh);
        return "user-home";
    }
}