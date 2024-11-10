package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "thekhophieu")
public class TheKhoPhieu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID khoId;
    @Column(length = 50, nullable = false)
    private String MaPhieu;
    private int loaiPhieu;
    private Date ngayGiaoDich;
    private String ghiChu;
    private int trangThaiPhieu;

    // Hoá đơn
    private String maHoaDon;
    private Date ngayHoaDon;

    // Đối tác
    private String tenDoiTac;
    private UUID doiTacId;
    private int loaiDoiTac;

    private UUID nguoiLapPhieuId;
    private String tenNguoiLapPhieu;
    private UUID nguoiHuyPhieuId;
    private String tenNguoiHuyPhieu;
    private Date ngayHuyPhieu;
    private String noiDungHuy;

    private int phuongThucThanhToan;
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
    private double congNo;

    // Nơi nhập
    public UUID khoNhapId;
    private String tenNhoNhap;
    // Nơi xuất
    private UUID khoXuatId;
    private String tenKhoXuat;

    // Chốt tồn
    private boolean isChotTon;
    private Date ngayChotTon;


}
