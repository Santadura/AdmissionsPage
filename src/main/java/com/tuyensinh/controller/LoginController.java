package com.tuyensinh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("cccd") String cccd,
                          @RequestParam("password") String password,
                          HttpSession session,
                          Model model) {
        try {
            ThiSinh thiSinh = authService.login(cccd, password);
            session.setAttribute("loggedInThiSinh", thiSinh);
            return "redirect:/user/home";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cccd", cccd);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}