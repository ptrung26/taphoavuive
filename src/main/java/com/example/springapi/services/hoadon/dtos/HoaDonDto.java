package com.example.springapi.services.hoadon.dtos;

import com.example.springapi.model.HoaDon;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter()
@Setter()
public class HoaDonDto extends HoaDon {
    private List<HoaDonChiTietDto> chiTiets;
}
