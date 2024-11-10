package com.example.springapi.repository.kho;

import com.example.springapi.model.TheKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IKhoTheKhoRepository extends JpaRepository<TheKho, UUID> {

    boolean existsByDonViTinhIdIn(List<UUID> donViTinhIds);
    List<TheKho> findAllByPhieuId(UUID phieuid);
}
