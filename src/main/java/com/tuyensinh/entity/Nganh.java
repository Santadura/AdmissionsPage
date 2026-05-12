package com.tuyensinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_nganh")
public class Nganh {

    @Id
    @Column(name = "idnganh")
    private Integer id;

    @Column(name = "n_diemsan")
    private Double diemSan;

    @Column(name = "manganh")
    private String maNganh;

    @Column(name = "tennganh")
    private String tenNganh;

    @Column(name = "n_diemtrungtuyen")
    private Double diemTrungTuyen;

    @Column(name = "n_tohopgoc")
    private String toHopGoc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getDiemSan() {
        return diemSan;
    }

    public void setDiemSan(Double diemSan) {
        this.diemSan = diemSan;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    public Double getDiemTrungTuyen() {
        return diemTrungTuyen;
    }

    public void setDiemTrungTuyen(Double diemTrungTuyen) {
        this.diemTrungTuyen = diemTrungTuyen;
    }

    public String getToHopGoc() {
        return toHopGoc;
    }

    public void setToHopGoc(String toHopGoc) {
        this.toHopGoc = toHopGoc;
    }
}