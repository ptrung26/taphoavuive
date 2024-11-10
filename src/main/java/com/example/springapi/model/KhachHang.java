package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "crm_khachhang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID UserId;
    @Column(nullable = false, length = 100)
    private String tenKhachHang;
    private int gioiTinh;
    @Column(length = 255)
    private String diaChi;
    @Column(length = 100)
    private String email;
}
