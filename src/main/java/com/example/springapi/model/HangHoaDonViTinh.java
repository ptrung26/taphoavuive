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
@Table(name = "hanghoa_donvitinh")
public class HangHoaDonViTinh implements ITimeStamped, ISoftDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID hangHoaId;

    @Column(length = 50, nullable = false)
    private String tenDonVi;

    private String moTa;
    private int tyLeQuyDoi;
    private double giaBan;
    private String maVach;
    private boolean isDonViCoBan;
    private boolean isActive;
    private String maDonViTinh;

    @ManyToOne
    @JoinColumn(name = "hanghoa_id")
    private HangHoa hangHoa;

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
