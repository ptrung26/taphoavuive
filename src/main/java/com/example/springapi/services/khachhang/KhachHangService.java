package com.example.springapi.services.khachhang;

import com.example.springapi.repository.uow.IUnitOfWork;
import com.example.springapi.services.khachhang.dtos.KhachHangDto;
import com.example.springapi.shared.dtos.CommonResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KhachHangService implements IKhachHangService{
    private final IUnitOfWork _unitOfWork;
    private final ModelMapper _modelMapper;

    @Autowired
    public KhachHangService(IUnitOfWork unitOfWork, ModelMapper modelMapper) {
        _unitOfWork = unitOfWork;
        _modelMapper = modelMapper;
    }

    @Override
    public CommonResponseDto<KhachHangDto> getKhachHangById(UUID id) {
        var commonResponseDto = new CommonResponseDto<KhachHangDto>();
        var _khachHangRepos = _unitOfWork.getKhachHangRepository();
        var khachHang = _khachHangRepos.findById(id);
        if(khachHang.isEmpty()) {
            commonResponseDto.isSuccess = false;
            commonResponseDto.message = "Khách hàng không tồn tại";
            return commonResponseDto;
        }

        commonResponseDto.isSuccess = true;
        commonResponseDto.data = _modelMapper.map(khachHang, KhachHangDto.class);
        return commonResponseDto;
    }

    @Override
    public CommonResponseDto<KhachHangDto> taoKhachHang(KhachHangDto khachHangDto) {
        var commonResponseDto = new CommonResponseDto<KhachHangDto>();
        var _khachHangRepos = _unitOfWork.getKhachHangRepository();
        _khachHangRepos.save(khachHangDto);
        commonResponseDto.isSuccess = true;
        commonResponseDto.data = _modelMapper.map(khachHangDto, KhachHangDto.class);
        return commonResponseDto;
    }
}
