package com.example.springapi.repository.hanghoa;


import com.example.springapi.model.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IHangHoaRepository extends JpaRepository<HangHoa, UUID> {
    boolean existsByMaHangHoa(String maHangHoa);
}
