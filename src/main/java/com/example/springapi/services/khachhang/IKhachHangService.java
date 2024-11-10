package com.example.springapi.services.khachhang;

import com.example.springapi.services.khachhang.dtos.KhachHangDto;
import com.example.springapi.shared.dtos.CommonResponseDto;

import java.util.UUID;

public interface IKhachHangService{
    CommonResponseDto<KhachHangDto> getKhachHangById(UUID id);
    CommonResponseDto<KhachHangDto> taoKhachHang(KhachHangDto khachHangDto);

}
