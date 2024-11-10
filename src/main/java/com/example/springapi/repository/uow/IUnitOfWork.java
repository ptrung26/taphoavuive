package com.example.springapi.repository.uow;

import com.example.springapi.repository.hanghoa.IHangHoaDonViTinhRepository;
import com.example.springapi.repository.hanghoa.IHangHoaRepository;
import com.example.springapi.repository.hoadon.IHoaDonChiTietRepository;
import com.example.springapi.repository.hoadon.IHoaDonRepository;
import com.example.springapi.repository.khachang.IKhachHangRepository;
import com.example.springapi.repository.kho.IKhoChiTietRepository;
import com.example.springapi.repository.kho.IKhoRepository;
import com.example.springapi.repository.kho.IKhoTheKhoPhieuRepository;
import com.example.springapi.repository.kho.IKhoTheKhoRepository;
import com.example.springapi.repository.user.IUserRepository;

public interface IUnitOfWork {
    void commit();
    void rollback();
    IUserRepository getUserRepository();
    IHoaDonRepository getHoaDonRepository();
    IHoaDonChiTietRepository getHoaDonChiTietRepository();
    IKhachHangRepository getKhachHangRepository();
    IHangHoaRepository getHangHoaRepository();
    IHangHoaDonViTinhRepository getHangHoaDonViTinhRepository();
    IKhoTheKhoRepository getTheKhoRepository();
    IKhoTheKhoPhieuRepository getTheKhoPhieuRepository();
    IKhoRepository getKhoRepository();
    IKhoChiTietRepository getKhoChiTietRepository();
}
