package com.example.springapi.services.hanghoa;

import com.example.springapi.model.HangHoa;
import com.example.springapi.model.HangHoaDonViTinh;
import com.example.springapi.repository.uow.IUnitOfWork;
import com.example.springapi.services.hanghoa.dtos.HangHoaDto;
import com.example.springapi.shared.dtos.CommonResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            var checkValid = ValidHangHoa(input);
            if(!checkValid.isSuccess) {
                commonReponseDto.isSuccess = false;
                commonReponseDto.message = checkValid.message;
                return commonReponseDto;
            }

            if(input.getId().toString().isEmpty()) {
                var newId = UUID.randomUUID();
                input.setId(newId);
                _hangHoaRepos.save(_modelMapper.map(input, HangHoa.class));

                // Cập nhật đơn vị tính
                input.getLstDonViTinhClient().forEach(x -> {
                    x.setHangHoaId(newId);
                    x.setDonViCoBan(false);
                    x.setActive(true);
                });

                var lstDonViTinh = input.getLstDonViTinhClient().stream().map(x -> _modelMapper.map(x, HangHoaDonViTinh.class)).toList();
                _donViTinhRepos.saveAll(lstDonViTinh);
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

                // Cập nhật lại đơn vị tính
                var lstDonViTinhXoa = input.getHangHoaDonViTinhs().stream()
                        .filter(x -> input.getLstDonViTinhClient().stream().noneMatch(y -> y.getId() == x.getId())).toList();
                var lstDonViTinhThem = input.getLstDonViTinhClient().stream().filter(x -> x.getId().toString().isEmpty()).toList();
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
