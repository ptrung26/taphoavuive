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
@Table(name = "khochitiet")
public class KhoChiTiet implements ITimeStamped, ISoftDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID khoId;
    private UUID khoChiTietId;
    private UUID hangHoaId;
    private UUID pId;
    private String soLo;
    private int soLuong;
    private LocalDate ngaySanXuat;
    private LocalDate ngayNhap;
    private LocalDate hanDung;
    @Column(precision = 15)
    private double giaVon;


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
