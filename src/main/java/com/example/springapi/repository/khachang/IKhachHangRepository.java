package com.example.springapi.repository.khachang;

import com.example.springapi.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IKhachHangRepository extends JpaRepository<KhachHang, UUID> {
}
