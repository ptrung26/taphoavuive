package com.example.springapi.services.hanghoa;

import com.example.springapi.model.HangHoa;
import com.example.springapi.model.HangHoaDonViTinh;
import com.example.springapi.repository.uow.IUnitOfWork;
import com.example.springapi.services.hanghoa.dtos.HangHoaDto;
import com.example.springapi.shared.dtos.CommonResponseDto;
import com.example.springapi.shared.helpers.Factory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class HangHoaAppService implements IHangHoaAppService{

    private final IUnitOfWork _uow;
    private final ModelMapper _modelMapper;

    @Autowired
    public HangHoaAppService(IUnitOfWork uow, ModelMapper modelMapper) {
        _uow = uow;
        _modelMapper = modelMapper;
    }


    public CommonResponseDto<HangHoaDto> getInfoById(UUID id) {
        var hangHoa =  _uow.getHangHoaRepository().findById(id);
        var commonReponseDto = new CommonResponseDto<HangHoaDto>();

        if(hangHoa.isEmpty()) {
            commonReponseDto.isSuccess = false;
            commonReponseDto.message = "Hàng hoá tồn tại";
            return commonReponseDto;
        }

        var hangHoaDto = _modelMapper.map(hangHoa, HangHoaDto.class);
        hangHoaDto.setLstDonViTinhClient(hangHoa.get().getHangHoaDonViTinhs());

        commonReponseDto.data = hangHoaDto;
        commonReponseDto.isSuccess = true;
        return commonReponseDto;
    }

    public CommonResponseDto<HangHoaDto> createOrUpdateHangHoa(HangHoaDto input) {
        var commonReponseDto = new CommonResponseDto<HangHoaDto>();
        var _hangHoaRepos = _uow.getHangHoaRepository();
        var _donViTinhRepos = _uow.getHangHoaDonViTinhRepository();

        try {
            // Valid dto
            var checkValid = ValidHangHoa(input);
            if(!checkValid.isSuccess) {
                commonReponseDto.isSuccess = false;
                commonReponseDto.message = checkValid.message;
                return commonReponseDto;
            }

            // Create
            if(Factory.isNullOrEmptyId(input.getId())) {
                var newId = UUID.randomUUID();
                input.setId(newId);
                input.setShopId(1);
                _hangHoaRepos.save(_modelMapper.map(input, HangHoa.class));

                // Tao don vi tinh co ban
                var dvtCoBan = new HangHoaDonViTinh();
                dvtCoBan.setHangHoaId(newId);
                dvtCoBan.setTenDonVi(input.getTenDonViTinhCoBan());
                dvtCoBan.setMaDonViTinh(input.getMaDonViTinhCoBan());
                dvtCoBan.setTyLeQuyDoi(1);
                dvtCoBan.setGiaBan(input.getGiaBan());
                dvtCoBan.setDonViCoBan(true);
                dvtCoBan.setActive(true);

                // Tao don vi tinh phu
                input.getLstDonViTinhClient().forEach(x -> {
                    x.setHangHoaId(newId);
                    x.setDonViCoBan(false);
                    x.setActive(true);
                });
                input.getHangHoaDonViTinhs().add(dvtCoBan);
                var lstDonViTinh = input.getLstDonViTinhClient().stream().map(x -> _modelMapper.map(x, HangHoaDonViTinh.class)).toList();
                _donViTinhRepos.saveAll(lstDonViTinh);

                // Dong bo tu chi nhanh chinh

            } else {
                var hanghoa = _hangHoaRepos.findById(input.getId());
                if(hanghoa.isEmpty()) {
                    commonReponseDto.isSuccess = false;
                    commonReponseDto.message = "Hàng hoá không tồn tại";
                    return commonReponseDto;
                }

                // Nếu thay đổi quản lý lô, cập nhật lại kho chi tiết
                if(input.isQuanLyTheoLo() != hanghoa.get().isQuanLyTheoLo()) {
                    var _khoChiTietRepos = _uow.getKhoChiTietRepository();
                    _khoChiTietRepos.deleteByHangHoaId(input.getId());
                }

                // Cập nhật hàng hoá
                _hangHoaRepos.save(_modelMapper.map(input, HangHoa.class));

                // Cap nhat don vi tinh co ban
                var dvtCoBan = input.getHangHoaDonViTinhs().stream()
                        .filter(HangHoaDonViTinh::isDonViCoBan).findFirst().orElse(null);
                if(dvtCoBan != null) {
                    dvtCoBan.setGiaBan(input.getGiaBan());
                    dvtCoBan.setTenDonVi(input.getTenDonViTinhCoBan());
                    dvtCoBan.setMaDonViTinh(input.getMaDonViTinhCoBan());
                    _donViTinhRepos.save(_modelMapper.map(dvtCoBan, HangHoaDonViTinh.class));
                }

                // Cập nhật lại đơn vị tính phu
                var lstDonViTinhXoa = input.getHangHoaDonViTinhs().stream()
                        .filter(x -> input.getLstDonViTinhClient().stream().noneMatch(y -> y.getId() == x.getId())).toList();
                var lstDonViTinhThem = input.getLstDonViTinhClient().stream().filter(x -> x.getId().toString().isEmpty()).toList();
                var lstDonViTinhSua = input.getHangHoaDonViTinhs()
                        .stream()
                        .filter(x -> lstDonViTinhXoa.stream()
                            .anyMatch(y -> !Factory.isNullOrEmptyId(y.getId()) && y.getId() != x.getId()))
                        .toList();

                if(!lstDonViTinhSua.isEmpty()) {
                    _donViTinhRepos.saveAll(lstDonViTinhSua.stream().map(x -> _modelMapper.map(x, HangHoaDonViTinh.class)).toList());
                }

                if(!lstDonViTinhXoa.isEmpty()) {
                    _donViTinhRepos.deleteByDonViTinhIdIn(lstDonViTinhXoa.stream().map(HangHoaDonViTinh::getId).toList());
                }

                if(!lstDonViTinhThem.isEmpty()) {
                    lstDonViTinhThem.forEach(x -> {
                        x.setHangHoaId(hanghoa.get().getId());
                    });
                    _donViTinhRepos.saveAll(lstDonViTinhThem.stream().map(x -> _modelMapper.map(x, HangHoaDonViTinh.class)).toList());
                }
            }

            _uow.commit();
        } catch (Exception ex) {
            commonReponseDto.isSuccess = false;
            commonReponseDto.message = "Something wrong!!";
        }

        return commonReponseDto;

    }

    private CommonResponseDto<Boolean> ValidHangHoa(HangHoaDto input) {
        var _hangHoaRepos = _uow.getHangHoaRepository();
        var commonReponseDto = new CommonResponseDto<Boolean>();

        // Kiểm tra mã hàng hoá
        if(input.getMaHangHoa().trim().isEmpty()) {
            input.setMaHangHoa(String.format("HH%d", _hangHoaRepos.count() + 1));
        } else {
            var checkTrungMa = _hangHoaRepos.existsByMaHangHoa(input.getMaHangHoa());
            if(checkTrungMa) {
                commonReponseDto.isSuccess = false;
                commonReponseDto.message = "Mã hàng hoà đã tồn tại";
                return commonReponseDto;
            }
        }

        // check VAT
        if((input.getVat() > 0 && input.getVat() < 10) || input.getVat() > 100) {
            commonReponseDto.isSuccess = false;
            commonReponseDto.message = "Hàng hoá có VAT không hợp lệ";
            return commonReponseDto;
        }

        // cập nhật vat theo vatCode
        if(input.getVatCode().trim().isEmpty())
        {
            input.setVat(0);
        }
        else
        {
           try {
               double vat = Double.parseDouble(input.getVatCode());
               if(vat < 0) {
                   commonReponseDto.isSuccess = false;
                   commonReponseDto.message = "Hàng hoá có VAT không hợp lệ";
                   return commonReponseDto;
               }
               input.setVat(vat);
           }
           catch(NumberFormatException e) {
               if(!input.getVatCode().equals("KTT") && !input.getVatCode().equals("KCT")) {
                   commonReponseDto.isSuccess = false;
                   commonReponseDto.message = "Hàng hoá có VAT không hợp lệ";
                   return commonReponseDto;
               } else {
                   input.setVat(0);
               }
           }
        }

        // Kiểm tra đơn vị tính
        var lstDonViTinhXoa = input.getHangHoaDonViTinhs().stream()
                .filter(x -> input.getLstDonViTinhClient().stream()
                        .noneMatch(y -> y.getId() == x.getId()))
                .toList();
        if(!lstDonViTinhXoa.isEmpty()) {
            var _theKhoRepos = _uow.getTheKhoRepository();
            if(_theKhoRepos.existsByDonViTinhIdIn(lstDonViTinhXoa.stream().map(HangHoaDonViTinh::getId).toList())){
                commonReponseDto.isSuccess = false;
                commonReponseDto.message = "Đơn vị tính đã được dùng, không thể xoá";
                return commonReponseDto;
            }
        }

        commonReponseDto.isSuccess = true;
        return commonReponseDto;
    }
}
