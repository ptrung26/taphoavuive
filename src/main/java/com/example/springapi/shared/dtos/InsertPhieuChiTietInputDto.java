package com.example.springapi.shared.dtos;

import com.example.springapi.model.KhoChiTiet;
import com.example.springapi.services.kho.dtos.PhieuChiTietDto;
import com.example.springapi.services.kho.dtos.PhieuDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InsertPhieuChiTietInputDto {
    private List<UUID> lstHangHoaIds;
    private PhieuDto phieu;
    private List<PhieuChiTietDto> chiTiet;
    private List<KhoChiTiet> lstKhoChiTiet;
}
