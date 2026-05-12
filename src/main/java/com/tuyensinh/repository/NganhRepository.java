package com.tuyensinh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuyensinh.entity.Nganh;

public interface NganhRepository extends JpaRepository<Nganh, Integer> {
    Optional<Nganh> findByMaNganh(String maNganh);
}