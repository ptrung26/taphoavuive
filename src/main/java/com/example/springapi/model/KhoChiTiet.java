package com.example.springapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "khochitiet")
public class KhoChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID hangHoaId;
    private UUID pId;
    private String soLo;
    private int soLuong;
    private Date ngaySanXuat;
    private Date ngayNhap;
    private Date hanDung;
    @Column(precision = 15)
    private double giaVon;
}
