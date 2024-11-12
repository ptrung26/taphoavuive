package com.example.springapi.shared.enums;


import lombok.Getter;

public class Enum {

    @Getter
    public enum TRANG_THAI_PHIEU {
        DANG_SOAN(1),
        DA_HOAN_THANH(2),
        DA_HUY(3);

        private final int value;

        TRANG_THAI_PHIEU(int value) {
            this.value = value;
        }

    }

    @Getter
    public enum LOAI_PHIEU {
        NHAP_TON(1),
        NHAP_TU_NCC(2),
        DIEU_CHUYEN(3),
        XUAT_TRA_NCC(4),
        XUAT_KHAC(5),
        XUAT_HUY(6),
        KHACH_HANG_TRA(7),
        PHIEU_KIEM_KHO(8),
        HOA_DON(9),
        DIEU_CHINH_GIA_VON(10);

        private final int value;

        LOAI_PHIEU(int value) {
            this.value = value;
        }
    }

    @Getter
    public enum LOAI_DOI_TAC {
        NHA_CUNG_CAP(1);

        private final int value;

        LOAI_DOI_TAC(int value) {
            this.value = value;
        }
    }

    @Getter
    public enum LOAI_THE_KHO {
        THE_KHO_ROOT(1),
        THE_KHO_CHI_TIET(2);

        private final int value;

        LOAI_THE_KHO(int value) {
            this.value = value;
        }
    }

}