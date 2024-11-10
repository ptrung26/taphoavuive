package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "dm_hanghoa")
public class HangHoa {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;
    private int loaiHangHoaId;
    @Column(length = 20, nullable = false)
    private String maHangHoa;
    @Column(length = 100, nullable = false)
    private String tenHangHoa;
    private boolean isActive;
    private boolean isQuanLyTheoLo;
    private String hinhAnh;
    private double giaBan;
    private String vatCode;
    @Column(nullable = false, length = 10)
    private double vat;

    @OneToMany(mappedBy = "hanghoa", fetch = FetchType.LAZY)
    private List<HangHoaDonViTinh> hangHoaDonViTinhs;



}
