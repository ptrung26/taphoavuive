package com.example.springapi.shared.dtos;

import com.example.springapi.model.HangHoaDonViTinh;
import com.example.springapi.model.KhoChiTiet;
import com.example.springapi.model.TheKho;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChotTonHangHoaOutputDto {
    private List<TheKho> listTheKhoUpdate;
    private List<KhoChiTiet> listKhoChiTietUpdate;
    private List<HangHoaDonViTinh> lstDonViTinhUpdate ;

}
