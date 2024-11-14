package com.example.springapi.shared.dtos;

import com.example.springapi.model.KhoChiTiet;
import com.example.springapi.model.TheKho;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChotTonHangHoaInputDto {
    private LocalDateTime ngayGiaoDich;
    private List<TheKho> lstTheKho;
    private List<KhoChiTiet> lstKhoChiTiet;
}
