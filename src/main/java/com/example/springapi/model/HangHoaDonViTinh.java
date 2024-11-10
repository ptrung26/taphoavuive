package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "hanghoa_donvitinh")
public class HangHoaDonViTinh {
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
}
