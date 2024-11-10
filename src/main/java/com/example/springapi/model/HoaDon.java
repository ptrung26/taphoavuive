package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data()
@Entity()
@Table(name = "crm_hoadon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID khachHangId;
    private UUID nhanVienId;
    @Column(nullable = false)
    private String tenKhachHang;
    @Column(precision = 15)
    private double tongTienHangTruocChietKhau;
    @Column(precision = 15)
    private double tongTienHang;
    @Column(precision = 15)
    private double tongTienTruocThue;
    @Column(precision = 15)
    private double tongGiamGia;
    @Column(precision = 15)
    private double thanhTien;
    @Column(precision = 15)
    private double tongVat;
}