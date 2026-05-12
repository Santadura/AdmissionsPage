package com.tuyensinh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuyensinh.entity.ThiSinh;

public interface ThiSinhRepository extends JpaRepository<ThiSinh, Integer> {
    ThiSinh findByCccd(String cccd);
}