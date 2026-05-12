package com.tuyensinh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuyensinh.entity.NguyenVongXetTuyen;

public interface NguyenVongXetTuyenRepository extends JpaRepository<NguyenVongXetTuyen, Integer> {
    List<NguyenVongXetTuyen> findByCccdOrderByThuTuNguyenVongAsc(String cccd);
}