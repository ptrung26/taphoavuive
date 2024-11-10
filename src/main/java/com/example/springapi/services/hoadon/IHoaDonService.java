package com.example.springapi.services.hoadon;

import com.example.springapi.services.hoadon.dtos.HoaDonDto;
import com.example.springapi.shared.dtos.CommonResponseDto;

import java.util.UUID;

public interface IHoaDonService {
    CommonResponseDto<HoaDonDto> getInfoById(UUID id);
    CommonResponseDto<HoaDonDto> taoHoaDon(HoaDonDto hoaDonDto);

}
