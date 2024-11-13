package com.example.springapi.repository.hanghoa;

import com.example.springapi.model.HangHoaDonViTinh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IHangHoaDonViTinhRepository extends JpaRepository<HangHoaDonViTinh, UUID> {
    @Transactional
    @Modifying
    @Query("UPDATE HangHoaDonViTinh h SET h.isDeleted = true, h.deletedAt = CURRENT_TIMESTAMP WHERE h.id IN :donViTinhs")
    void deleteByDonViTinhIdIn(@Param("donViTinhs") List<UUID> donViTinhs);

}
