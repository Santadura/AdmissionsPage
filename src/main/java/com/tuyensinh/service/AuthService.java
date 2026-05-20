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

        if (cccd == null || cccd.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            throw new RuntimeException("Vui lòng nhập đầy đủ CCCD và mật khẩu.");
        }

        String username = cccd.trim();
        String rawPassword = password.trim();

        /*
         * =========================================
         * BƯỚC 1: CHECK xt_users TRƯỚC
         * =========================================
         */
        User existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {

            // check khóa tài khoản
            if (!existingUser.isEnabled()) {
                throw new RuntimeException("Tài khoản đã bị khóa.");
            }

            // check password
            if (!existingUser.getPassword().equals(rawPassword)) {
                throw new RuntimeException("Mật khẩu không đúng.");
            }

            // lấy lại thông tin thí sinh
            ThiSinh thiSinh = thiSinhRepository.findByCccd(username);

            if (thiSinh == null) {
                throw new RuntimeException("Không tìm thấy thông tin thí sinh.");
            }

            return thiSinh;
        }

        /*
         * =========================================
         * BƯỚC 2: LOGIN LẦN ĐẦU
         * CHECK BẢNG THÍ SINH
         * =========================================
         */

        ThiSinh thiSinh = thiSinhRepository.findByCccd(username);

        if (thiSinh == null) {
            throw new RuntimeException("Không tìm thấy thí sinh.");
        }

        if (thiSinh.getNgaySinh() == null) {
            throw new RuntimeException("Thí sinh chưa có ngày sinh trong hệ thống.");
        }

        String expectedPassword = thiSinh.getNgaySinh()
                .format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        if (!expectedPassword.equals(rawPassword)) {
            throw new RuntimeException("Mật khẩu không đúng.");
        }

        /*
         * =========================================
         * TẠO USER MỚI CHO THÍ SINH
         * =========================================
         */

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPassword(expectedPassword);
        newUser.setRole(UserRole.USER);
        newUser.setEnabled(true);
        newUser.setThisinhId(thiSinh.getId());

        userRepository.save(newUser);

        return thiSinh;
    }
}