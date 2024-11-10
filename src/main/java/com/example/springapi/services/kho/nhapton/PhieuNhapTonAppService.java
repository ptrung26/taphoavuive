package com.example.springapi.services.kho.nhapton;

import com.example.springapi.model.TheKho;
import com.example.springapi.repository.uow.IUnitOfWork;
import com.example.springapi.services.kho.dtos.ThongTinPhieuInput;
import com.example.springapi.shared.dtos.CommonResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhieuNhapTonAppService {
    private final ModelMapper _modelMapper;
    private final IUnitOfWork _uow;

    @Autowired
    public PhieuNhapTonAppService(ModelMapper modelMapper, IUnitOfWork uow) {
        _modelMapper = modelMapper;
        _uow = uow;
    }

    public CommonResponseDto<Boolean> createOrUpdatePhieu(ThongTinPhieuInput input) {
        var commonResponseDto = new CommonResponseDto<Boolean>();
        var _theKho = _uow.getTheKhoRepository();
        var _theKhoPhieu = _uow.getTheKhoPhieuRepository();
        var _hangHoaRepos = _uow.getHangHoaRepository();
        try {
            var phieu = input.getPhieu();
            var chiTiet = input.getChiTiet();

            // Valid dữ liệu
            if(phieu.getNgayGiaoDich() != null) {
                if(phieu.getNgayGiaoDich().getTime() > System.currentTimeMillis()) {
                    commonResponseDto.isSuccess = false;
                    commonResponseDto.message = "Ngày giao dịch không lớn hơn ngày hiện tại";
                    return commonResponseDto;
                }
            } else {
                commonResponseDto.isSuccess = false;
                commonResponseDto.message = "Vui lòng nhập ngày";
                return commonResponseDto;
            }

            if(chiTiet.isEmpty()) {
                commonResponseDto.isSuccess = false;
                commonResponseDto.message = "Vui lòng chọn hàng hoá để nhập";
                return commonResponseDto;
            }

            // Lấy dữ liệu
            var lstHangHoaIds = chiTiet.stream()
                    .map(TheKho::getHangHoaId).toList();
            if (!phieu.getId().toString().isEmpty()) {
                lstHangHoaIds.addAll(_theKho.findAllByPhieuId(phieu.getId()).stream()
                        .map(TheKho::getHangHoaId).toList());
            }
            lstHangHoaIds = lstHangHoaIds.stream().distinct().toList();

            var lstHangHoa = _hangHoaRepos.findAllById(lstHangHoaIds);

        } catch (Exception e) {

        }

        return commonResponseDto;
    }

    public List<TheKho> layDanhSachKhoChiTiet(ThongTinPhieuInput input, List<UUID> lstHangHoaIds) {
        var _theKhoRepos = _uow.getTheKhoRepository();
        var listLoHangHoa = input.getChiTiet().stream()
                .collect(Collectors.groupingBy(m -> Map.of(
                        "HangHoaId", m.getHangHoaId(),
                        "SoLo", m.getSoLo(),
                        "HanDung", m.getHanDung())))
                .keySet().stream()  // Lấy Map từ entry (key là Map)
                .toList();

        var lstKhoChiTiet = _theKhoRepos.findAllByPhieuId(input.getPhieu().getId());

        // Thêm mới chi tiết root
        var lstKhoRootTaoMoi = lstKhoChiTiet.stream()
                .filter(x -> listLoHangHoa.stream().noneMatch(y -> y.get("HangHoaId").equals(x.getHangHoaId()))).toList();
        if(!lstKhoRootTaoMoi.isEmpty()) {
            lstKhoRootTaoMoi.forEach(x -> {
                x.setId(UUID.randomUUID());
            });
            _theKhoRepos.saveAll(lstKhoRootTaoMoi);
        }

        return null;
    }
}
