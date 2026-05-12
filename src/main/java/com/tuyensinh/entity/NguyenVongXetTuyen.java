package com.tuyensinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
public class NguyenVongXetTuyen {

    @Id
    @Column(name = "idnv")
    private Integer id;

    @Column(name = "nn_cccd")
    private String cccd;

    @Column(name = "nv_manganh")
    private String maNganh;

    @Column(name = "nv_tt")
    private Integer thuTuNguyenVong;

    @Column(name = "diem_thxt")
    private Double diemThxt;

    @Column(name = "diem_utqd")
    private Double diemUtqd;

    @Column(name = "diem_cong")
    private Double diemCong;

    @Column(name = "diem_xettuyen")
    private Double diemXetTuyen;

    @Column(name = "nv_ketqua")
    private String ketQua;

    @Column(name = "tt_phuongthuc")
    private String phuongThuc;

    @Column(name = "tt_thm")
    private String toHop;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public Integer getThuTuNguyenVong() {
        return thuTuNguyenVong;
    }

    public void setThuTuNguyenVong(Integer thuTuNguyenVong) {
        this.thuTuNguyenVong = thuTuNguyenVong;
    }

    public Double getDiemThxt() {
        return diemThxt;
    }

    public void setDiemThxt(Double diemThxt) {
        this.diemThxt = diemThxt;
    }

    public Double getDiemUtqd() {
        return diemUtqd;
    }

    public void setDiemUtqd(Double diemUtqd) {
        this.diemUtqd = diemUtqd;
    }

    public Double getDiemCong() {
        return diemCong;
    }

    public void setDiemCong(Double diemCong) {
        this.diemCong = diemCong;
    }

    public Double getDiemXetTuyen() {
        return diemXetTuyen;
    }

    public void setDiemXetTuyen(Double diemXetTuyen) {
        this.diemXetTuyen = diemXetTuyen;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getToHop() {
        return toHop;
    }

    public void setToHop(String toHop) {
        this.toHop = toHop;
    }
}