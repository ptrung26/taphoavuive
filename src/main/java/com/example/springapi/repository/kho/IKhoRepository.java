package com.example.springapi.repository.kho;

import com.example.springapi.model.Kho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IKhoRepository extends JpaRepository<Kho, UUID> {
}
