package com.example.springapi.services.kho.dtos;

import com.example.springapi.model.TheKhoPhieu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ThongTinPhieuInput {
    private PhieuDto phieu;
    private List<PhieuChiTietDto> chiTiet;

}


