package com.example.springapi.repository.hoadon;

import com.example.springapi.model.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IHoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, UUID> {
}
