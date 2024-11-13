package com.example.springapi.model;

import com.example.springapi.model.base.ISoftDeleted;
import com.example.springapi.model.base.ITimeStamped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "dm_hanghoa")
public class HangHoa implements ITimeStamped, ISoftDeleted {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;
    @Column(length = 20, nullable = false)
    private String maHangHoa;

    @Column(length = 100, nullable = false)
    private String tenHangHoa;

    private boolean isActive;
    private boolean isQuanLyTheoLo;
    private boolean isDichVuCoHangHoaKemTheo;
    private String hinhAnh;
    private double giaBan;
    private String vatCode;

    @Column(nullable = false, length = 10)
    private double vat;

    @OneToMany(mappedBy = "hanghoa", fetch = FetchType.LAZY)
    private List<HangHoaDonViTinh> hangHoaDonViTinhs;

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
