package com.example.springapi.api.khachhang;

import com.example.springapi.services.khachhang.IKhachHangService;
import com.example.springapi.services.khachhang.dtos.KhachHangDto;
import com.example.springapi.shared.dtos.CommonResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/customers")
@Tag(name = "Khách hàng")
public class KhachHangController {

    private final IKhachHangService _khachHangService;

    @Autowired
    public KhachHangController(IKhachHangService khachHangService) {
        _khachHangService = khachHangService;
    }

    @GetMapping("/")
    public CommonResponseDto<KhachHangDto> getKhachHangById(@PathVariable UUID id) {
        return _khachHangService.getKhachHangById(id);
    }

    @PostMapping
    public CommonResponseDto<KhachHangDto> taoKhachHang(@RequestBody KhachHangDto khachHangDto) {
        return  _khachHangService.taoKhachHang(khachHangDto);
    }
}
