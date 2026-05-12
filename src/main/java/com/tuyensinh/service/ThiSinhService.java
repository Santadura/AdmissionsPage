package com.tuyensinh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tuyensinh.entity.ThiSinh;
import com.tuyensinh.repository.ThiSinhRepository;

@Service
public class ThiSinhService {

    private final ThiSinhRepository thiSinhRepository;

    public ThiSinhService(ThiSinhRepository thiSinhRepository) {
        this.thiSinhRepository = thiSinhRepository;
    }

    public long countAll() {
        return thiSinhRepository.count();
    }

    public List<ThiSinh> findAll() {
        return thiSinhRepository.findAll();
    }

    public ThiSinh findByCccd(String cccd) {
        return thiSinhRepository.findByCccd(cccd);
    }
}