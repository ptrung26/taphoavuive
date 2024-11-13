package com.example.springapi.model;

import com.example.springapi.model.base.ISoftDeleted;
import com.example.springapi.model.base.ITimeStamped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "thekhophieu")
public class TheKhoPhieu implements ITimeStamped, ISoftDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID khoId;
    @Column(length = 50, nullable = false)
    private String MaPhieu;
    private int loaiPhieu;
    private LocalDateTime ngayGiaoDich;
    private String ghiChu;
    private int trangThaiPhieu;

    // Hoá đơn
    private String maHoaDon;
    private LocalDateTime ngayHoaDon;

    // Đối tác
    private String tenDoiTac;
    private UUID doiTacId;
    private int loaiDoiTac;

    private UUID nguoiLapPhieuId;
    private String tenNguoiLapPhieu;
    private UUID nguoiHuyPhieuId;
    private String tenNguoiHuyPhieu;
    private LocalDateTime ngayHuyPhieu;
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
    private LocalDateTime ngayChotTon;

    // Mo hinh chuoi
    private int shopId;
    private int tenantId;

    // crud tracking
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Override
    public void setDeleted(boolean deleted) {
        this.isDeleted = true;
        onDelete();
    }

    @PrePersist
    public void prePersist() {
        onCreate();
    }

    @PreUpdate
    public void preUpdate() {
        onUpdate();
    }


}
