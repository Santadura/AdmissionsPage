package com.tuyensinh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuyensinh.entity.BangQuyDoi;

public interface BangQuyDoiRepository extends JpaRepository<BangQuyDoi, Integer> {

    List<BangQuyDoi> findByPhuongThucOrderByToHopAscPhanViAsc(String phuongThuc);

    List<BangQuyDoi> findByPhuongThucAndToHopOrderByPhanViAsc(String phuongThuc, String toHop);
}