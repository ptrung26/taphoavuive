package com.example.springapi.repository.kho;

import com.example.springapi.model.TheKhoPhieu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IKhoTheKhoPhieuRepository extends JpaRepository<TheKhoPhieu, UUID> {

}
