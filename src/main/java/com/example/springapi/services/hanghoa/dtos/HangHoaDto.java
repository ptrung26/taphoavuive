package com.example.springapi.services.hanghoa.dtos;

import com.example.springapi.model.HangHoa;
import com.example.springapi.model.HangHoaDonViTinh;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class HangHoaDto extends HangHoa {
    private String maDonViTinhCoBan;
    private String tenDonViTinhCoBan;
    private List<HangHoaDonViTinh> lstDonViTinhClient;
}
