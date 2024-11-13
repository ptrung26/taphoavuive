package com.example.springapi.services.kho.nhapton;

import com.example.springapi.model.KhoChiTiet;
import com.example.springapi.model.TheKho;
import com.example.springapi.model.TheKhoPhieu;
import com.example.springapi.repository.uow.IUnitOfWork;
import com.example.springapi.services.kho.dtos.ThongTinPhieuInput;
import com.example.springapi.shared.dtos.*;
import com.example.springapi.shared.enums.Enum;
import com.example.springapi.shared.helpers.Factory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        var _theKhoRepos = _uow.getTheKhoRepository();
        var _theKhoPhieuRepos = _uow.getTheKhoPhieuRepository();
        try {
            var phieu = input.getPhieu();
            var chiTiet = input.getChiTiet();

            // Valid dữ liệu
            if (phieu.getNgayGiaoDich() != null) {
                if (phieu.getNgayGiaoDich().isAfter(LocalDateTime.now())) {
                    commonResponseDto.isSuccess = false;
                    commonResponseDto.message = "Ngày giao dịch không lớn hơn ngày hiện tại";
                    return commonResponseDto;
                }
            } else {
                commonResponseDto.isSuccess = false;
                commonResponseDto.message = "Vui lòng nhập ngày";
                return commonResponseDto;
            }

            if (chiTiet.isEmpty()) {
                commonResponseDto.isSuccess = false;
                commonResponseDto.message = "Vui lòng chọn hàng hoá để nhập";
                return commonResponseDto;
            }

            // Lấy dữ liệu
            phieu.setTenantId(1);
            phieu.setShopId(1);

            // Lấy danh sách hàng hóa bao gồm cả phiếu cũ nếu có
            var lstHangHoaIds = chiTiet.stream()
                    .map(TheKho::getHangHoaId)
                    .collect(Collectors.toSet());

            if (!Factory.isNullOrEmptyId(input.getPhieu().getId())) {
                lstHangHoaIds.addAll(_theKhoRepos.findAllByPhieuId(phieu.getId()).stream()
                        .map(TheKho::getHangHoaId)
                        .collect(Collectors.toSet()));
            }

            var lstKhoChiTiet = layDanhSachKhoChiTiet(input, lstHangHoaIds.stream().toList());

            // Xóa phiếu cũ
            UUID phieuCuId = null;
            if (!Factory.isNullOrEmptyId(input.getPhieu().getId())) {
                var phieuCuDto = _theKhoPhieuRepos.findById(input.getPhieu().getId());
                if (phieuCuDto.isEmpty()) {
                    commonResponseDto.isSuccess = false;
                    commonResponseDto.message = "Không tìm thấy thông tin phiếu";
                    return commonResponseDto;
                }
                var listTrangThaiHopLe = Arrays.asList(
                        Enum.TRANG_THAI_PHIEU.DANG_SOAN.getValue(),
                        Enum.TRANG_THAI_PHIEU.DA_HOAN_THANH.getValue());

                if (listTrangThaiHopLe.contains(phieuCuDto.get().getTrangThaiPhieu())) {
                    commonResponseDto.isSuccess = false;
                    commonResponseDto.message = String.format("Trạng thái của phiếu %s không hợp lệ", phieuCuDto.get().getMaPhieu());
                }

                if (phieuCuDto.get().getTrangThaiPhieu() == Enum.TRANG_THAI_PHIEU.DA_HOAN_THANH.getValue()) {
                    phieuCuId = phieuCuDto.get().getId();
                }

                // Xóa thẻ kho và thẻ kho phiếu
                _theKhoRepos.deleteAllByTheKhoPhieuId(phieuCuDto.get().getId());
                _theKhoPhieuRepos.deleteById(phieuCuDto.get().getId());
            } else {
                // Generate mã phiếu
                if (phieu.getMaPhieu().isEmpty()) {
                    var sttPhieu = _theKhoPhieuRepos.count();
                    phieu.setMaPhieu(String.format("PNT%04d", sttPhieu + 1));
                }
            }

            // Tạo mới phiếu
            phieu.setNgayGiaoDich(LocalDateTime.now());
            phieu.setLoaiPhieu(Enum.LOAI_PHIEU.NHAP_TON.getValue());
            phieu.setLoaiDoiTac(Enum.LOAI_DOI_TAC.NHA_CUNG_CAP.getValue());
            var newPhieuId = UUID.randomUUID();
            phieu.setId(newPhieuId);
            _theKhoPhieuRepos.save(_modelMapper.map(phieu, TheKhoPhieu.class));

            // Thêm phiếu chi tiết
            var inputPhieuChiTiet = new InsertPhieuChiTietInputDto();
            inputPhieuChiTiet.setPhieu(phieu);
            inputPhieuChiTiet.setChiTiet(chiTiet);
            inputPhieuChiTiet.setLstKhoChiTiet(lstKhoChiTiet);
            var outputPhieuChiTiet = TaoDuLieuPhieuChiTiet(inputPhieuChiTiet);
            if (!outputPhieuChiTiet.isSuccess) {
                commonResponseDto.isSuccess = false;
                commonResponseDto.message = outputPhieuChiTiet.message;
                return commonResponseDto;
            }

            _theKhoRepos.saveAll(outputPhieuChiTiet.data.getLstTheKhoRoot());
            _theKhoRepos.saveAll(outputPhieuChiTiet.data.getLstTheKhoMember());
            phieu.setTongTienHang(outputPhieuChiTiet.data.getTongTienHang());
            phieu.setTongTienTruocThue(outputPhieuChiTiet.data.getTongTienTruocThue());
            phieu.setTongVat(outputPhieuChiTiet.data.getTongVAT());

            // Chốt tồn (khi phiếu đã hoàn thành)
            if (phieu.getTrangThaiPhieu() == Enum.TRANG_THAI_PHIEU.DA_HOAN_THANH.getValue()) {
                var lstTheKho = _theKhoRepos.findAllByKhoIdAndShopIdAndHangHoaIdIn(phieu.getKhoId(),
                        phieu.getShopId(), lstHangHoaIds.stream().toList());

            }

        } catch (Exception e) {
            commonResponseDto.isSuccess = false;
            commonResponseDto.message = e.getMessage();
        }

        return commonResponseDto;
    }

    public List<KhoChiTiet> layDanhSachKhoChiTiet(ThongTinPhieuInput input, List<UUID> lstHangHoaIds) {
        var _khoChiTietRepos = _uow.getKhoChiTietRepository();

        // Lay danh sach lo hang hoa
        var listLoHangHoa = input.getChiTiet().stream()
                .collect(Collectors.groupingBy(m -> Map.of(
                        "HangHoaId", m.getHangHoaId().toString(),
                        "SoLo", m.getSoLo(),
                        "HanDung", m.getHanDung().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .keySet().stream()
                .toList();

        // Lay danh sach kho chi tiet
        var lstKhoChiTiet = _khoChiTietRepos.findAllByKhoIdAndHangHoaIdIn(input.getPhieu().getKhoId(), lstHangHoaIds);
        var lstKhoChiTietRoot = lstKhoChiTiet.stream().filter(x -> Factory.isNullOrEmptyId(x.getPId())).collect(Collectors.toList());
        var lstKhoChiTietMember = lstKhoChiTiet.stream().filter(x -> !Factory.isNullOrEmptyId(x.getPId())).toList();

        // Them moi chi tiet root
        var lstKhoRootTaoMoi = lstKhoChiTiet.stream().filter(x -> listLoHangHoa.stream().noneMatch(y -> {
            y.get("HangHoaId");
            return false;
        })).toList();
        if (!lstKhoRootTaoMoi.isEmpty()) {
            lstKhoRootTaoMoi.forEach(x -> {
                x.setId(UUID.randomUUID());
            });
            _khoChiTietRepos.saveAll(lstKhoRootTaoMoi);
            lstKhoChiTiet.addAll(lstKhoRootTaoMoi);
            lstKhoChiTietRoot.addAll(lstKhoRootTaoMoi);
        }

        // Them moi chi tiet member
        Map<String, KhoChiTiet> khoChiTietMemberMap = lstKhoChiTietMember.stream()
                .collect(Collectors.toMap(k -> k.getHangHoaId() + "-" + k.getSoLo() + "-" + k.getHanDung(), k -> k));

        Map<UUID, KhoChiTiet> khoChiTietRootMap = lstKhoChiTietRoot.stream()
                .collect(Collectors.toMap(KhoChiTiet::getHangHoaId, k -> k));

        List<KhoChiTiet> lstKhoMemberTaoMoi = new ArrayList<>();
        for (var loHangHoa : listLoHangHoa) {
            UUID hangHoaId = UUID.fromString(loHangHoa.get("HangHoaId"));
            String soLo = loHangHoa.get("SoLo");
            LocalDate hanDung = LocalDate.parse(loHangHoa.get("HanDung"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // khoChiTietRoot theo HangHoaId
            KhoChiTiet kctRoot = khoChiTietRootMap.get(hangHoaId);
            if (kctRoot != null) {
                String key = hangHoaId + "-" + soLo + "-" + hanDung;

                if (!khoChiTietMemberMap.containsKey(key)) {
                    // Tạo mới KhoChiTiet nếu không có
                    var khoChiTiet = new KhoChiTiet();
                    khoChiTiet.setId(UUID.randomUUID());
                    khoChiTiet.setPId(kctRoot.getId());
                    khoChiTiet.setKhoId(input.getPhieu().getKhoId());
                    khoChiTiet.setSoLo(soLo);
                    khoChiTiet.setHangHoaId(hangHoaId);
                    khoChiTiet.setHanDung(hanDung);
                    khoChiTiet.setGiaVon(0);
                    khoChiTiet.setNgayNhap(null);
                    khoChiTiet.setShopId(input.getPhieu().getShopId());
                    khoChiTiet.setTenantId(input.getPhieu().getTenantId());
                    lstKhoMemberTaoMoi.add(khoChiTiet);
                }

            }
        }

        if (!lstKhoMemberTaoMoi.isEmpty()) {
            _khoChiTietRepos.saveAll(lstKhoMemberTaoMoi);
            lstKhoChiTiet.addAll(lstKhoRootTaoMoi);
        }

        return lstKhoChiTiet;
    }

    public CommonResponseDto<InsertPhieuChiTietOutputDto> TaoDuLieuPhieuChiTiet(InsertPhieuChiTietInputDto input) {
        var commonResponseDto = new CommonResponseDto<InsertPhieuChiTietOutputDto>();
        var lstTheKhoRoot = new ArrayList<TheKho>();
        var lstTheKhoMember = new ArrayList<TheKho>();

        var _khochiTietRepos = _uow.getKhoChiTietRepository();

        // Cập nhập tổng tiền hàng trước chiết khấu
        input.getChiTiet().forEach(x -> {
            x.setTongTienHangTruocChietKhau(x.getDonGia() * x.getSoLuong());
        });

        // Tính toán tiền
        double tongTienHang = input.getChiTiet().stream()
                .mapToDouble(x -> x.getTongTienHangTruocChietKhau() - x.getTongChietKhau())
                .sum();
        double tongTienTruocThue = 0;
        double tongVAT = 0;
        double tongGiamGiaDaPhanBo = 0;

        for (int i = 0; i < input.getChiTiet().size(); i++) {
            var item = input.getChiTiet().get(i);
            item.setTheKhoPhieuId(input.getPhieu().getId());
            item.setNgayGiaoDich(input.getPhieu().getNgayGiaoDich());
            item.setMaPhieuGiaoDich(input.getPhieu().getMaPhieu());
            item.setLoaiPhieu(input.getPhieu().getLoaiPhieu());
            item.setTenDoiTac(input.getPhieu().getTenDoiTac());
            item.setDoiTacId(input.getPhieu().getDoiTacId());
            item.setLoaiDoiTac(input.getPhieu().getLoaiDoiTac());
            item.setTrangThai(input.getPhieu().getTrangThaiPhieu());
            item.setSoLuongGiaoDich(item.getSoLuong() * item.getTyLeQuyDoi());

            if (item.getLoaiPhieu() == Enum.LOAI_PHIEU.XUAT_HUY.getValue()
                    || item.getLoaiPhieu() == Enum.LOAI_PHIEU.XUAT_KHAC.getValue()
                    || item.getLoaiPhieu() == Enum.LOAI_PHIEU.XUAT_TRA_NCC.getValue()
                    || item.getLoaiPhieu() == Enum.LOAI_PHIEU.DIEU_CHUYEN.getValue()) {
                item.setSoLuongGiaoDich(-1 * item.getSoLuong() * item.getTyLeQuyDoi());
            }

            item.setTongTienHangTruocChietKhau(item.getDonGia() * item.getSoLuong());
            item.setTongTienHang(item.getTongTienHangTruocChietKhau() - item.getTongChietKhau());

            if (input.getPhieu().getTongGiamGia() > 0 && tongTienHang > 0) {
                if (i == input.getChiTiet().size() - 1) {
                    item.setTonGiamGiaPhanBo(input.getPhieu().getTongGiamGia() - tongGiamGiaDaPhanBo);
                } else {
                    item.setTonGiamGiaPhanBo(item.getTongTienHang() * item.getTongTienHang() / tongTienHang);
                }
                tongGiamGiaDaPhanBo += item.getTonGiamGiaPhanBo();
            }

            if (item.getLoaiPhieu() == Enum.LOAI_PHIEU.XUAT_KHAC.getValue()
                    || item.getLoaiPhieu() == Enum.LOAI_PHIEU.KHACH_HANG_TRA.getValue()) {
                item.setThanhTien(item.getTongTienHang() - item.getTonGiamGiaPhanBo());
                item.setTongVat(item.getThanhTien() * item.getVat() / (100 + item.getVat()));
                item.setTongTienTruocThue(item.getThanhTien() - item.getTongVat());
            } else {
                item.setTongTienTruocThue(item.getTongTienHang() - item.getTonGiamGiaPhanBo());
                item.setTongVat(item.getTongTienTruocThue() * item.getVat() / item.getVat());
                item.setThanhTien(item.getTongTienTruocThue() + item.getTongVat());
            }

            tongVAT += item.getTongVat();
            tongTienTruocThue += item.getTongTienTruocThue();

            // Lấy  kho chi tiết root
            var khoChiTietRoot = _khochiTietRepos.findByHangHoaIdAndPId(item.getHangHoaId(), null);

            if (item.isQuanLyTheoLo() && item.getLoaiPhieu() == Enum.LOAI_PHIEU.XUAT_KHAC.getValue()) {
                System.out.println("Cập nhật sau");
            } else {
                KhoChiTiet kctGiaVon = null;
                Optional<KhoChiTiet> kct;
                if (item.getLoaiPhieu() == Enum.LOAI_PHIEU.NHAP_TON.getValue()
                        || item.getLoaiPhieu() == Enum.LOAI_PHIEU.NHAP_TU_NCC.getValue()) {
                    kct = input.getLstKhoChiTiet().stream()
                            .filter(x -> x.getKhoId() == item.getKhoId()
                                    && x.getHangHoaId() == item.getHangHoaId()
                                    && x.getShopId() == item.getShopId()
                                    && !Factory.isNullOrEmptyId(x.getPId())
                                    && x.getSoLo().equals(item.getSoLo())
                                    && x.getHanDung() == item.getHanDung()).findFirst();

                } else {
                    kct = input.getLstKhoChiTiet().stream()
                            .filter(x -> x.getKhoId() == item.getKhoId()
                                    && x.getHangHoaId() == item.getHangHoaId()
                                    && x.getShopId() == item.getShopId()
                                    && !Factory.isNullOrEmptyId(x.getPId())
                                    && x.getKhoChiTietId() == item.getKhoChiTietId()
                            ).findFirst();

                }

                if (kct.isPresent()) {
                    kctGiaVon = kct.get();
                }

                if (kctGiaVon != null) {

                    // Tạo thẻ kho root và thẻ kho member
                    var theKhoRoot = _modelMapper.map(item, TheKho.class);
                    var newTheKhoRootId = UUID.randomUUID();
                    theKhoRoot.setId(newTheKhoRootId);
                    var theKho = _modelMapper.map(item, TheKho.class);
                    theKhoRoot.setLoaiTheKho(Enum.LOAI_THE_KHO.THE_KHO_ROOT.getValue());
                    theKhoRoot.setKhoChiTietId(khoChiTietRoot.getId());
                    theKhoRoot.setKhoChiTietRootId(khoChiTietRoot.getId());

                    // Báo cáo người thực hiện
                    theKhoRoot.setTongTienHangThucHien(theKhoRoot.getTongTienHang());
                    lstTheKhoRoot.add(theKhoRoot);

                    theKho.setPId(newTheKhoRootId);
                    theKho.setLoaiTheKho(Enum.LOAI_THE_KHO.THE_KHO_CHI_TIET.getValue());
                    theKho.setKhoChiTietId(kctGiaVon.getId());
                    theKho.setKhoChiTietRootId(khoChiTietRoot.getId());

                    // Báo cáo người thực hiện
                    theKho.setTongTienHangThucHien(theKho.getTongTienHang());
                    lstTheKhoMember.add(theKho);
                }
            }
        }

        var data = new InsertPhieuChiTietOutputDto();
        data.setLstTheKhoRoot(lstTheKhoRoot);
        data.setLstTheKhoMember(lstTheKhoMember);
        data.setTongTienTruocThue(tongTienTruocThue);
        data.setTongVAT(tongVAT);
        data.setTongTienHang(tongTienHang);

        commonResponseDto.isSuccess = true;
        commonResponseDto.data = data;
        return commonResponseDto;
    }

    public CommonResponseDto<ChotTonHangHoaOutputDto> chotTonHangHoa(ChotTonHangHoaInputDto input) {
        var commonResponseDto = new CommonResponseDto<ChotTonHangHoaOutputDto>();
        var output = new ChotTonHangHoaOutputDto();

        // Lấy thẻ kho mà có trạng thái đã hoàn thành
        var data = input.getLstTheKho().stream().filter(x -> x.getTrangThai() == Enum.TRANG_THAI_PHIEU.DA_HOAN_THANH.getValue()).toList();

        // tồn đầu ký (< ngayGiaoDich)
        var dataTonDauKyMember = data.stream()
                .filter(x -> x.getNgayGiaoDich().isBefore(input.getNgayGiaoDich())
                        && x.getLoaiTheKho() == Enum.LOAI_THE_KHO.THE_KHO_CHI_TIET.getValue())
                .collect(Collectors.groupingBy(x ->
                        Arrays.asList(x.getKhoChiTietId(), x.getKhoChiTietRootId(), x.getHangHoaId())))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    var keys = entry.getKey();
                    result.put("HangHoaId", keys.get(0));
                    result.put("KhoChiTietId", keys.get(1));
                    result.put("KhoChiTietRootId", keys.get(2));
                    result.put("SoLuongTon", entry.getValue().stream().mapToInt(TheKho::getSoLuongGiaoDich).sum());
                    return result;
                })
                .toList();

        // tồn trong ký (>= ngayGiaoDich)
        var dataTonTrongKyMember = data.stream().filter(x -> x.getNgayGiaoDich().isAfter(input.getNgayGiaoDich())
                || x.getNgayGiaoDich().isEqual(input.getNgayGiaoDich())
                && x.getLoaiTheKho() == Enum.LOAI_THE_KHO.THE_KHO_CHI_TIET.getValue()).toList();

        var lstKhoChiTietMember = input.getLstKhoChiTiet().stream()
                .filter(kct -> !Factory.isNullOrEmptyId(kct.getPId()))
                .toList();

        // Check tồn kho theo lô
        for (var kctMember : lstKhoChiTietMember) {
            boolean isUpdateKhoChiTiet = false;
            int tonDau = 0;
            int tonCuoi = 0;
            int soluongGiaoDich = 0;
            int soLuong = 0;

            // Tìm thông tin tồn đầu kỳ theo HangHoaId và KhoChiTietId
            var tonDauKyOfHangHoaId = dataTonDauKyMember.stream()
                    .filter(m -> m.get("HangHoaId").equals(kctMember.getHangHoaId())
                            && m.get("KhoChiTietId").equals(kctMember.getId()))
                    .findFirst().orElse(null);

            tonDau = (tonDauKyOfHangHoaId == null) ? 0 : Integer.parseInt(tonDauKyOfHangHoaId.get("SoLuongTon").toString());

            // Lọc dữ liệu tồn trong kỳ
            var lstDataHangHoaTonTrongKyMember = dataTonTrongKyMember.stream()
                    .filter(m -> m.getHangHoaId().equals(kctMember.getHangHoaId())
                            && m.getKhoChiTietId().equals(kctMember.getId()))
                    .sorted(Comparator.comparing(TheKho::getNgayGiaoDich).thenComparing(TheKho::getId))
                    .toList();

            if (!lstDataHangHoaTonTrongKyMember.isEmpty()) {
                for (var theKhoCT : lstDataHangHoaTonTrongKyMember) {
                    var theKhoOld = _modelMapper.map(theKhoCT, TheKho.class);

                    if (tonDau < 0) {
                        commonResponseDto.isSuccess = false;
                        commonResponseDto.message = String.format("Hàng hóa %s có dữ liệu không hợp lệ!", theKhoCT.getTenHangHoa());
                        return commonResponseDto;
                    }

                    soluongGiaoDich = theKhoOld.getSoLuongGiaoDich();
                    soLuong = theKhoOld.getSoLuong();

                    if (theKhoOld.getLoaiPhieu() == Enum.LOAI_PHIEU.PHIEU_KIEM_KHO.getValue()) {
                        tonCuoi = theKhoOld.getTonCuoi();
                        soluongGiaoDich = tonCuoi - tonDau;
                        soLuong = Math.abs(soluongGiaoDich);
                    } else {
                        tonCuoi = tonDau + soluongGiaoDich;
                    }

                    if (tonCuoi < 0) {
                        var thongTinSoLo = (theKhoOld.getSoLo() != null && !theKhoOld.getSoLo().isEmpty()) ?
                                "có số lô " + theKhoOld.getSoLo() + " " : "";
                        commonResponseDto.isSuccess = false;
                        commonResponseDto.message = String.format("Hàng hóa %s %s không đủ tồn kho cho phiếu %s", theKhoCT.getTenHangHoa(), thongTinSoLo, theKhoCT.getMaPhieuGiaoDich());
                        return commonResponseDto;
                    }

                    if (tonDau != theKhoOld.getTonDau() || tonCuoi != theKhoOld.getTonCuoi() ||
                            soluongGiaoDich != theKhoOld.getSoLuongGiaoDich() || soLuong != theKhoOld.getSoLuong()) {
                        theKhoCT.setTonDau(tonDau);
                        theKhoCT.setTonCuoi(tonCuoi);
                        theKhoCT.setSoLuongGiaoDich(soluongGiaoDich);
                        theKhoCT.setSoLuong(soLuong);
                        output.getListTheKhoUpdate().add(theKhoCT);
                        isUpdateKhoChiTiet = true;
                    }

                    tonDau = tonCuoi;
                }
            } else {
                tonCuoi = tonDau;
                isUpdateKhoChiTiet = true;
            }

            if (kctMember.getSoLuong() != tonDau) {
                isUpdateKhoChiTiet = true;
            }

            if (isUpdateKhoChiTiet) {
                kctMember.setSoLuong(Math.abs(tonCuoi));
                output.getListKhoChiTietUpdate().add(kctMember);
            }
        }

        // Check tồn kho root
        var lstKhoChiTietRoot = input.getLstKhoChiTiet().stream()
                .filter(kct -> kct.getPId() == null)
                .toList();

        for (var khoChiTietRoot : lstKhoChiTietRoot) {
            boolean isUpdateKhoChiTietRoot = false;

            // Lấy dữ liệu tồn đầu kỳ của thẻ kho root
            var dataTonDauKyRoot = data.stream()
                    .filter(m -> m.getNgayGiaoDich().isBefore(input.getNgayGiaoDich())
                            && m.getLoaiTheKho() == Enum.LOAI_THE_KHO.THE_KHO_ROOT.getValue()
                            && m.getHangHoaId().equals(khoChiTietRoot.getHangHoaId())
                            && m.getKhoChiTietId().equals(khoChiTietRoot.getId()))
                    .toList();

            // Lấy dữ liệu tồn trong kỳ của thẻ kho root
            var dataTonTrongKyRoot = data.stream()
                    .filter(m -> m.getNgayGiaoDich().isAfter(input.getNgayGiaoDich())
                            && m.getLoaiTheKho() == Enum.LOAI_THE_KHO.THE_KHO_ROOT.getValue()
                            && m.getHangHoaId().equals(khoChiTietRoot.getHangHoaId())
                            && m.getKhoChiTietId().equals(khoChiTietRoot.getId()))
                    .sorted(Comparator.comparing(TheKho::getNgayGiaoDich).thenComparing(TheKho::getId))
                    .toList();

            double giaVon = 0;
            int tonCuoi = 0;
            int tonDau = 0;

            // Lấy tồn đầu kỳ của thẻ kho root
            var theKhoRootDauKy = dataTonDauKyRoot.stream()
                    .filter(x -> x.getHangHoaId().equals(khoChiTietRoot.getHangHoaId())
                            && x.getKhoChiTietId().equals(khoChiTietRoot.getId()))
                    .sorted(Comparator.comparing(TheKho::getNgayGiaoDich).reversed().thenComparing(TheKho::getId))
                    .toList().stream().findFirst().orElse(null);

            tonDau = (theKhoRootDauKy != null) ? theKhoRootDauKy.getTonCuoi() : 0;
            giaVon = (theKhoRootDauKy != null) ? theKhoRootDauKy.getGiaVon() : 0;

            if (!dataTonTrongKyRoot.isEmpty()) {
                for (var theKhoRoot : dataTonTrongKyRoot) {
                    var theKhoRootOld = _modelMapper.map(theKhoRoot, TheKho.class);
                    int soluongGiaoDich = theKhoRootOld.getSoLuongGiaoDich();
                    int soLuong = theKhoRootOld.getSoLuong();

                    if (theKhoRootOld.getLoaiPhieu() == Enum.LOAI_PHIEU.PHIEU_KIEM_KHO.getValue()) {
                        // Lấy lại số lượng giao dịch của phiếu chi tiết kiểm kho tương ứng
                        var theKhoChiTietKiemKe = dataTonTrongKyMember.stream()
                                .filter(x -> x.getPId().equals(theKhoRoot.getId()))
                                .findFirst()
                                .orElse(null);
                        soluongGiaoDich = (theKhoChiTietKiemKe != null) ? theKhoChiTietKiemKe.getSoLuongGiaoDich() : 0;
                        soLuong = Math.abs(soluongGiaoDich);
                    }

                    if (tonDau < 0) {
                        commonResponseDto.isSuccess = false;
                        commonResponseDto.message = String.format("Hàng hóa %s có dữ liệu không hợp lệ!", theKhoRootOld.getTenHangHoa());
                        return commonResponseDto;
                    }

                    tonCuoi = tonDau + soluongGiaoDich;
                    if (tonCuoi < 0) {
                        commonResponseDto.isSuccess = false;
                        commonResponseDto.message = String.format("Hàng hóa %s không đủ tồn cho phiếu %s !", theKhoRootOld.getTenHangHoa(), theKhoRoot.getMaPhieuGiaoDich());
                        return commonResponseDto;
                    }

                    // Tính lại giá vốn
                    if (Arrays.asList(
                                    Enum.LOAI_PHIEU.HOA_DON.getValue(),
                                    Enum.LOAI_PHIEU.XUAT_KHAC.getValue(),
                                    Enum.LOAI_PHIEU.XUAT_HUY.getValue(),
                                    Enum.LOAI_PHIEU.DIEU_CHUYEN.getValue(),
                                    Enum.LOAI_PHIEU.PHIEU_KIEM_KHO.getValue(),
                                    Enum.LOAI_PHIEU.KHACH_HANG_TRA.getValue())
                            .contains(theKhoRootOld.getLoaiPhieu())) {

                        theKhoRootOld.setGiaVon(giaVon);

                        if (Arrays.asList(Enum.LOAI_PHIEU.PHIEU_KIEM_KHO.getValue(), Enum.LOAI_PHIEU.XUAT_HUY.getValue(), Enum.LOAI_PHIEU.XUAT_KHAC.getValue())
                                .contains(theKhoRootOld.getLoaiPhieu())) {

                            theKhoRootOld.setDonGia(theKhoRootOld.getGiaVon());
                            theKhoRootOld.setThanhTien(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRootOld.setTongTienTruocThue(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRootOld.setTongTienHangTruocChietKhau(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRootOld.setTongTienHang(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                        }
                    } else if (theKhoRootOld.getLoaiPhieu() == Enum.LOAI_PHIEU.XUAT_TRA_NCC.getValue()) {
                        theKhoRootOld.setTienVon(theKhoRootOld.getThanhTien());
                        if (tonCuoi != 0) {
                            giaVon = (giaVon * tonDau - theKhoRootOld.getThanhTien()) / tonCuoi;
                        }
                        theKhoRootOld.setGiaVon(giaVon);
                    } else if (theKhoRootOld.getLoaiPhieu() == Enum.LOAI_PHIEU.DIEU_CHINH_GIA_VON.getValue()) {
                        giaVon = theKhoRootOld.getGiaVon();
                    } else {
                        if (tonCuoi != 0) {
                            giaVon = (giaVon * tonDau + theKhoRootOld.getThanhTien()) / tonCuoi;
                        }
                        theKhoRootOld.setGiaVon(giaVon);
                        theKhoRootOld.setTienVon(theKhoRootOld.getThanhTien());
                    }
                    // Cập nhật lại giá vốn cho thẻ kho phiếu chi tiết
                    boolean isAddToListUpdate = false;
                    var theKhoPhieuChiTiet = output.getListTheKhoUpdate().stream()
                            .filter(x -> x.getPId().equals(theKhoRoot.getId()))
                            .findFirst()
                            .orElse(null);

                    if (theKhoPhieuChiTiet == null) {
                        isAddToListUpdate = true;
                        theKhoPhieuChiTiet = dataTonTrongKyMember.stream()
                                .filter(x -> x.getPId().equals(theKhoRoot.getId()))
                                .findFirst()
                                .orElse(null);
                    }

                    if (theKhoPhieuChiTiet != null && (theKhoPhieuChiTiet.getGiaVon() != theKhoRootOld.getGiaVon() ||
                            theKhoPhieuChiTiet.getTienVon() != theKhoRootOld.getTienVon())) {
                        if (Arrays.asList(Enum.LOAI_PHIEU.PHIEU_KIEM_KHO.getValue(), Enum.LOAI_PHIEU.XUAT_HUY.getValue(), Enum.LOAI_PHIEU.XUAT_KHAC.getValue())
                                .contains(theKhoRootOld.getLoaiPhieu())) {
                            theKhoRootOld.setThanhTien(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRootOld.setTongTienTruocThue(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRootOld.setTongTienHangTruocChietKhau(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRootOld.setTongTienHang(theKhoRootOld.getGiaVon() * soluongGiaoDich);
                            theKhoRoot.setTienVon(theKhoRootOld.getTienVon());

                            theKhoPhieuChiTiet.setDonGia(theKhoRootOld.getGiaVon());
                            theKhoPhieuChiTiet.setGiaVon(theKhoRootOld.getGiaVon());
                            theKhoPhieuChiTiet.setTienVon(theKhoRootOld.getTienVon());
                            theKhoPhieuChiTiet.setThanhTien(theKhoRootOld.getThanhTien());
                            theKhoPhieuChiTiet.setTongTienTruocThue(theKhoRootOld.getTongTienTruocThue());
                            theKhoPhieuChiTiet.setTongTienHangTruocChietKhau(theKhoRootOld.getTongTienHangTruocChietKhau());
                            theKhoPhieuChiTiet.setTongTienHang(theKhoRootOld.getTongTienHang());
                        } else {
                            if (soluongGiaoDich < 0 && theKhoRootOld.getLoaiPhieu() != Enum.LOAI_PHIEU.XUAT_TRA_NCC.getValue()) {
                                theKhoPhieuChiTiet.setGiaVon(theKhoRootOld.getGiaVon());
                                theKhoPhieuChiTiet.setTienVon(theKhoRootOld.getTienVon());
                            } else {
                                theKhoPhieuChiTiet.setGiaVon(theKhoRootOld.getGiaVon());
                            }
                        }

                        if (isAddToListUpdate) {
                            output.getListTheKhoUpdate().add(theKhoPhieuChiTiet);
                        }
                    }


                    // Cập nhật kho chi tiết
                    if (tonDau != theKhoRootOld.getTonDau()
                            || tonCuoi != theKhoRootOld.getTonCuoi()
                            || soluongGiaoDich != theKhoRootOld.getSoLuongGiaoDich()
                            || soLuong != theKhoRootOld.getSoLuong()
                            || theKhoRoot.getGiaVon() != theKhoRootOld.getGiaVon()) {
                        theKhoRoot.setTonDau(tonDau);
                        theKhoRoot.setTonCuoi(tonCuoi);
                        theKhoRoot.setSoLuongGiaoDich(soluongGiaoDich);
                        theKhoRoot.setSoLuong(soLuong);
                        theKhoRoot.setGiaVon(theKhoRootOld.getGiaVon());
                        theKhoRoot.setTienVon(theKhoRootOld.getTienVon());
                        output.getListTheKhoUpdate().add(theKhoRoot);
                        isUpdateKhoChiTietRoot = true;
                    }

                    tonDau = tonCuoi;
                }
            } else {
                tonCuoi = tonDau;
                isUpdateKhoChiTietRoot = true;
            }

            if (khoChiTietRoot.getSoLuong() != tonDau) {
                isUpdateKhoChiTietRoot = true;
            }

            if (isUpdateKhoChiTietRoot) {
                khoChiTietRoot.setGiaVon(giaVon);
                khoChiTietRoot.setSoLuong(Math.abs(tonCuoi));
                output.getListKhoChiTietUpdate().add(khoChiTietRoot);
            }
        }
        return commonResponseDto;
    }
}
