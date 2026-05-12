package com.tuyensinh.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.repository.ThiSinhRepository;

@Service
public class AuthService {

    private final ThiSinhRepository thiSinhRepository;

    public AuthService(ThiSinhRepository thiSinhRepository) {
        this.thiSinhRepository = thiSinhRepository;
    }

    public ThiSinh login(String cccd, String password) {
        if (cccd == null || cccd.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập đầy đủ CCCD và mật khẩu.");
        }

        ThiSinh thiSinh = thiSinhRepository.findByCccd(cccd.trim());
        if (thiSinh == null) {
            throw new RuntimeException("Không tìm thấy thí sinh.");
        }

        if (thiSinh.getNgaySinh() == null) {
            throw new RuntimeException("Thí sinh chưa có ngày sinh trong hệ thống.");
        }

        String expectedPassword = thiSinh.getNgaySinh().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        if (!expectedPassword.equals(password.trim())) {
            throw new RuntimeException("Mật khẩu không đúng.");
        }

        return thiSinh;
    }
}