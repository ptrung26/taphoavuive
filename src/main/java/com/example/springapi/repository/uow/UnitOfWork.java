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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnitOfWork implements IUnitOfWork {

    private final IUserRepository _userRepo;
    private final IHoaDonRepository _hoaDonRepo;
    private final IHoaDonChiTietRepository _hoaDonChiTietRepo;
    private final IKhachHangRepository _khachHangRepo;
    private final IKhoTheKhoPhieuRepository _theKhoPhieuRepo;
    private final IKhoTheKhoRepository _theKhoRepo;
    private final IHangHoaRepository _hangHoaRepo;
    private final IHangHoaDonViTinhRepository _hangHoaDonViTinhRepo;
    private final IKhoRepository _khoRepo;
    private final IKhoChiTietRepository _khoChiTietRepo;

    @Autowired
    public UnitOfWork(IUserRepository userRepo, IHoaDonRepository hoaDonRepo, IHoaDonChiTietRepository hoaDonChiTietRepo, IKhachHangRepository khachHangRepo,
     IKhoTheKhoRepository theKhoRepo, IKhoTheKhoPhieuRepository theKhoPhieuRepo, IHangHoaRepository hangHoaRepo,
                      IHangHoaDonViTinhRepository hangHoaDonViTinhRepo, IKhoRepository khoRepo, IKhoChiTietRepository khoChiTietRepo) {
        _userRepo = userRepo;
        _hoaDonRepo = hoaDonRepo;
        _hoaDonChiTietRepo = hoaDonChiTietRepo;
        _khachHangRepo = khachHangRepo;
        _theKhoRepo = theKhoRepo;
        _theKhoPhieuRepo = theKhoPhieuRepo;
        _hangHoaRepo = hangHoaRepo;
        _hangHoaDonViTinhRepo = hangHoaDonViTinhRepo;
        _khoRepo = khoRepo;
        _khoChiTietRepo = khoChiTietRepo;
    }

    @Override
    @Transactional
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public IUserRepository getUserRepository() {
        return _userRepo;
    }

    @Override
    public IHoaDonRepository getHoaDonRepository() {
        return _hoaDonRepo;
    }

    @Override
    public IHoaDonChiTietRepository getHoaDonChiTietRepository() {
        return _hoaDonChiTietRepo;
    }

    @Override
    public IKhachHangRepository getKhachHangRepository() {
        return _khachHangRepo;
    }

    @Override
    public IHangHoaRepository getHangHoaRepository() {
        return _hangHoaRepo;
    }

    @Override
    public IHangHoaDonViTinhRepository getHangHoaDonViTinhRepository() {
        return _hangHoaDonViTinhRepo;
    }

    @Override
    public IKhoTheKhoRepository getTheKhoRepository() {
        return _theKhoRepo;
    }

    @Override
    public IKhoTheKhoPhieuRepository getTheKhoPhieuRepository() {
        return _theKhoPhieuRepo;
    }

    @Override
    public IKhoRepository getKhoRepository() {
        return _khoRepo;
    }

    @Override
    public IKhoChiTietRepository getKhoChiTietRepository() {
        return _khoChiTietRepo;
    }

}
