package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "thekho")
public class TheKho {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID khoId;
    @Column(length = 100, nullable = false)
    private String maPhieuGiaoDich;
    private int loaiTheKho; // 1. Xuất, 2. Nhập
    private int loaiPhieu;

    // Đối tác
    private UUID nhanVienId;
    private UUID doiTacId;
    private String tenDoiTac;
    private String tenNhanVien;

    // Chi tiết
    private int loaiHangHoa;
    private UUID hangHoaId;
    private String tenHangHoa;
    private UUID khoChiTietId;
    private UUID khoChiTietRootId;
    private double giaVon;

    private int soLuongGiaoDich;
    private int soLuongChenhLech;
    private int soLuongDaDoiTra;

    private UUID phieuId; // Id của phiếu
    private UUID phieuChiTietId; // Id của phiếu chi tiết

    private boolean isQuanLyTheoLo;
    private String soLo;
    private Date hanDung;
    private UUID donViTinhId;
    private String tenDonViTinh;

    private int soLuong;
    private int tonDau;
    private int tonCuoi;

    private String vatCode;
    private double vat;
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

    // Ngày chốt tồn
    private boolean isChotTon;
    private Date ngayChotTon;


}
