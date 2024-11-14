package com.example.springapi.shared.dtos;

import com.example.springapi.model.TheKho;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InsertPhieuChiTietOutputDto {
    private List<TheKho> lstTheKhoMember;
    private List<TheKho> lstTheKhoRoot;
    private double tongTienHang;
    private double tongVAT;
    private double tongTienTruocThue;
}
