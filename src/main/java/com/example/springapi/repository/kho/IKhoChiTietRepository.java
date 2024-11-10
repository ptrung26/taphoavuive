package com.example.springapi.repository.kho;

import com.example.springapi.model.KhoChiTiet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IKhoChiTietRepository extends JpaRepository<KhoChiTiet, UUID> {
    @Transactional
    @Modifying
    void deleteByHangHoaId(UUID hangHoaId);
}