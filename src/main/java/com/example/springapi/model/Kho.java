package com.example.springapi.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "kho")
public class Kho {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(length = 100, nullable = false)
    private String tenKho;
    @Column(length = 100, nullable = false)
    private String maKho;
    private int loaiKhoId;
    private boolean isActive;
    private boolean isMain;
}
