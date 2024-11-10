package com.example.springapi.services.hanghoa;

import com.example.springapi.services.hanghoa.dtos.HangHoaDto;
import com.example.springapi.shared.dtos.CommonResponseDto;

import java.util.UUID;

public interface IHangHoaAppService {
    CommonResponseDto<HangHoaDto> getInfoById(UUID id);
}
