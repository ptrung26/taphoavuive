package com.example.springapi.services.hoadon;

import com.example.springapi.model.HoaDon;
import com.example.springapi.model.HoaDonChiTiet;
import com.example.springapi.repository.uow.IUnitOfWork;
import com.example.springapi.services.hoadon.dtos.HoaDonDto;
import com.example.springapi.shared.dtos.CommonResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HoaDonService implements IHoaDonService {
    private final IUnitOfWork _unitOfWork;
    private final ModelMapper _modelMapper;

    @Autowired
    public HoaDonService(IUnitOfWork unitOfWork, ModelMapper modelMapper) {
        _unitOfWork = unitOfWork;
        _modelMapper = modelMapper;
    }

    public CommonResponseDto<HoaDonDto> getInfoById(UUID id) {
        var _hoaDonRepos = _unitOfWork.getHoaDonRepository();
        var hoaDon = _hoaDonRepos.findById(id);

        var commonResponseDto = new CommonResponseDto<HoaDonDto>();
        commonResponseDto.isSuccess = true;
        commonResponseDto.data = _modelMapper.map(hoaDon, HoaDonDto.class);
        return commonResponseDto;
    }

    public CommonResponseDto<HoaDonDto> taoHoaDon(HoaDonDto hoaDonDto) {
        var _hoaDonRepos = _unitOfWork.getHoaDonRepository();
        var _hoaDonChiTietRepos = _unitOfWork.getHoaDonChiTietRepository();
        var _khachHangRepos  = _unitOfWork.getKhachHangRepository();

        var commonReponseDto = new CommonResponseDto<HoaDonDto>();

        // Lấy thông tin khách hàng
        if(hoaDonDto.getKhachHangId() == null) {
            hoaDonDto.setTenKhachHang("Khách lẻ");
        } else {
            var khachHang = _khachHangRepos.findById(hoaDonDto.getKhachHangId());
            if(khachHang.isEmpty()) {
                commonReponseDto.isSuccess = false;
                commonReponseDto.message = "Khách hàng không tồn tại";
                return commonReponseDto;
            }

            hoaDonDto.setTenKhachHang(khachHang.get().getTenKhachHang());
        }

        // Tính tổng tiền
        double tongGiamGiaPhanBo = 0;
        double tongVat = 0;
        double tongTienTruocThue = 0;
        double tongThanhTien = 0;
        double tongTienHang = hoaDonDto.getChiTiets().stream().mapToDouble(chiTiet -> chiTiet.getDonGia() * (double) chiTiet.getSoLuong()).sum();

        for (int i = 0; i < hoaDonDto.getChiTiets().size(); i++) {
            var chiTiet = hoaDonDto.getChiTiets().get(i);
            chiTiet.setTongTienHangTruocChietKhau(chiTiet.getDonGia() * chiTiet.getSoLuong());
            chiTiet.setTongTienHang(chiTiet.getTongTienHang() - chiTiet.getTongChietKhau());
            if(tongTienHang > 0 && hoaDonDto.getTongGiamGia() > 0) {
                if(i == hoaDonDto.getChiTiets().size() - 1) {
                    chiTiet.setGiamGiaPhanBo(hoaDonDto.getTongGiamGia() - tongGiamGiaPhanBo);
                } else {
                    chiTiet.setGiamGiaPhanBo(hoaDonDto.getTongTienHang() / tongTienHang * hoaDonDto.getTongTienHang());
                }
                tongGiamGiaPhanBo += chiTiet.getGiamGiaPhanBo();
            }

            chiTiet.setThanhTien(chiTiet.getTongTienHang() - chiTiet.getGiamGiaPhanBo());
            chiTiet.setTongVat(chiTiet.getThanhTien() * chiTiet.getVat() / (100 + chiTiet.getVat()));

            tongTienTruocThue += chiTiet.getTongTienTruocThue();
            tongThanhTien += chiTiet.getThanhTien();
            tongVat += chiTiet.getTongVat();
        }

        _hoaDonRepos.save(_modelMapper.map(hoaDonDto, HoaDon.class));

        hoaDonDto.getChiTiets().forEach(chiTiet -> {
            chiTiet.setHoaDonId(hoaDonDto.getId());
        });

        var hoaDonChiTiets = new ArrayList<HoaDonChiTiet>();
        hoaDonDto.getChiTiets().forEach(chiTiet -> {
            hoaDonChiTiets.add(_modelMapper.map(chiTiet, HoaDonChiTiet.class));
        });
        _hoaDonChiTietRepos.saveAll(hoaDonChiTiets);

        _unitOfWork.commit();
        commonReponseDto.isSuccess = true;
        commonReponseDto.data = hoaDonDto;
        return commonReponseDto;

    }
}
