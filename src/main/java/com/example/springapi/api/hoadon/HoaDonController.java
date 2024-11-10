package com.example.springapi.api.hoadon;

import com.example.springapi.services.hoadon.IHoaDonService;
import com.example.springapi.services.hoadon.dtos.HoaDonDto;
import com.example.springapi.shared.dtos.CommonResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/hoaDon")
@Tag(name = "Hoá đơn")
public class HoaDonController {
    private final IHoaDonService _hoaDonService;

    @Autowired
    public HoaDonController(IHoaDonService hoaDonService) {
        _hoaDonService = hoaDonService;
    }

    @GetMapping("/{id}")
    public CommonResponseDto<HoaDonDto> getHoaDonById(@PathVariable UUID id) {
        return _hoaDonService.getInfoById(id);
    }

    @PostMapping
    public CommonResponseDto<HoaDonDto> taoHoaDon(@RequestBody HoaDonDto hoaDonDto) {
        return _hoaDonService.taoHoaDon(hoaDonDto);
    }

}
