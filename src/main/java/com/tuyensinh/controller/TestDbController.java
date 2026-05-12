package com.tuyensinh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tuyensinh.service.ThiSinhService;

@Controller
public class TestDbController {

    private final ThiSinhService thiSinhService;

    public TestDbController(ThiSinhService thiSinhService) {
        this.thiSinhService = thiSinhService;
    }

    @GetMapping("/test-db")
    public String testDb(Model model) {
        model.addAttribute("count", thiSinhService.countAll());
        model.addAttribute("thiSinhs", thiSinhService.findAll());
        return "test-db";
    }
}