package com.tuyensinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_nganh_tohop")
public class NganhToHop {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "manganh")
    private String maNganh;

    @Column(name = "matohop")
    private String maToHop;

    @Column(name = "th_mon1")
    private String mon1;

    @Column(name = "hsmon1")
    private Double hsMon1;

    @Column(name = "th_mon2")
    private String mon2;

    @Column(name = "hsmon2")
    private Double hsMon2;

    @Column(name = "th_mon3")
    private String mon3;

    @Column(name = "hsmon3")
    private Double hsMon3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getMaToHop() {
        return maToHop;
    }

    public void setMaToHop(String maToHop) {
        this.maToHop = maToHop;
    }

    public String getMon1() {
        return mon1;
    }

    public void setMon1(String mon1) {
        this.mon1 = mon1;
    }

    public Double getHsMon1() {
        return hsMon1;
    }

    public void setHsMon1(Double hsMon1) {
        this.hsMon1 = hsMon1;
    }

    public String getMon2() {
        return mon2;
    }

    public void setMon2(String mon2) {
        this.mon2 = mon2;
    }

    public Double getHsMon2() {
        return hsMon2;
    }

    public void setHsMon2(Double hsMon2) {
        this.hsMon2 = hsMon2;
    }

    public String getMon3() {
        return mon3;
    }

    public void setMon3(String mon3) {
        this.mon3 = mon3;
    }

    public Double getHsMon3() {
        return hsMon3;
    }

    public void setHsMon3(Double hsMon3) {
        this.hsMon3 = hsMon3;
    }
}