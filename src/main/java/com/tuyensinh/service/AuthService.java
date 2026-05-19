package com.tuyensinh.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.entity.User;
import com.tuyensinh.entity.UserRole;
import com.tuyensinh.repository.ThiSinhRepository;
import com.tuyensinh.repository.UserRepository;

@Service
public class AuthService {

    private final ThiSinhRepository thiSinhRepository;
    private final UserRepository userRepository;

    public AuthService(ThiSinhRepository thiSinhRepository,
            UserRepository userRepository) {
        this.thiSinhRepository = thiSinhRepository;
        this.userRepository = userRepository;
    }

    public ThiSinh login(String cccd, String password) {
        if (cccd == null || cccd.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập đầy đủ CCCD và mật khẩu.");
        }

        String username = cccd.trim();
        String rawPassword = password.trim();

        ThiSinh thiSinh = thiSinhRepository.findByCccd(username);
        if (thiSinh == null) {
            throw new RuntimeException("Không tìm thấy thí sinh.");
        }

        if (thiSinh.getNgaySinh() == null) {
            throw new RuntimeException("Thí sinh chưa có ngày sinh trong hệ thống.");
        }

        String expectedPassword = thiSinh.getNgaySinh().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        if (!expectedPassword.equals(rawPassword)) {
            throw new RuntimeException("Mật khẩu không đúng.");
        }

        taoTaiKhoanThiSinhNeuChuaCo(thiSinh, username, expectedPassword);

        return thiSinh;
    }

    private void taoTaiKhoanThiSinhNeuChuaCo(ThiSinh thiSinh, String username, String password) {
        User existed = userRepository.findByUsername(username);

        if (existed != null) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        user.setThisinhId(thiSinh.getId());

        userRepository.save(user);
    }
}