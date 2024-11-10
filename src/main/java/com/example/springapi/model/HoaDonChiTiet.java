package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "crm_hoadonchitiet")
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID hoaDonId;
    private UUID hangHoaId;
    @Column(nullable = false,  length = 255)
    private String tenHangHoa;
    private int soLuong;
    @Column(precision = 15)
    private double donGia;
    private double vat;
    @Column(precision = 15)
    private double tongTienHangTruocChietKhau;
    @Column(precision = 15)
    private double tongTienHang;
    @Column(precision = 15)
    private double tongTienTruocThue;
    @Column(precision = 15)
    private double tongChietKhau;
    @Column(precision = 15)
    private double thanhTien;
    @Column(precision = 15)
    private double tongVat;
    @Column(precision = 15)
    private double giamGiaPhanBo;
}
