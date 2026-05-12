package com.tuyensinh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuyensinh.entity.NganhToHop;

public interface NganhToHopRepository extends JpaRepository<NganhToHop, Integer> {
    List<NganhToHop> findByMaNganh(String maNganh);
}   