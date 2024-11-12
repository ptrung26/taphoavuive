package com.example.springapi.model;

import com.example.springapi.model.base.ISoftDeleted;
import com.example.springapi.model.base.ITimeStamped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "thekho")
public class TheKho implements ITimeStamped, ISoftDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID theKhoPhieuId;
    private UUID khoId;
    private UUID pId;
    @Column(length = 100, nullable = false)
    private String maPhieuGiaoDich;
    private int loaiTheKho; // 1. Xuất, 2. Nhập
    private int loaiPhieu;
    private LocalDateTime ngayGiaoDich;
    private int trangThai;

    // Đối tác
    private UUID nhanVienId;
    private String tenNhanVien;
    private int loaiDoiTac;
    private UUID doiTacId;
    private String tenDoiTac;

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
    private LocalDate hanDung;
    private UUID donViTinhId;
    private String tenDonViTinh;
    private int tyLeQuyDoi;

    private int soLuong;
    private int tonDau;
    private int tonCuoi;

    private String vatCode;
    private double vat;
    @Column(precision = 15)
    private double donGia;
    @Column(precision = 15)
    private double tongTienHangTruocChietKhau;
    @Column(precision = 15)
    private double tongChietKhau;
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
    @Column(precision = 15)
    private double tonGiamGiaPhanBo;
    @Column(precision = 15)
    private double tongTienHangThucHien;
    @Column(precision = 15)
    private double tienVon;

    // Ngày chốt tồn
    private boolean isChotTon;
    private LocalDateTime ngayChotTon;

    // Mo hinh chuoi
    private long shopId;
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
