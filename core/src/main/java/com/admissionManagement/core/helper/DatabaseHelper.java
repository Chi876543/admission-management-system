package com.admissionManagement.core.helper;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NganhToHopDTO;
import com.admissionManagement.core.dto.ThiSinhDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DatabaseHelper {
    public static BigDecimal quyDoiDiemVSATVaDGNL(BigDecimal diem, BangQuyDoiDTO bangQuyDoi) {
        if (bangQuyDoi == null || diem == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal x = diem;
        BigDecimal a = bangQuyDoi.getDiemA();
        BigDecimal b = bangQuyDoi.getDiemB();
        BigDecimal c = bangQuyDoi.getDiemC();
        BigDecimal d = bangQuyDoi.getDiemD();

        BigDecimal tuSo = x.subtract(a);
        BigDecimal mauSo = b.subtract(a);

        if (mauSo.compareTo(BigDecimal.ZERO) == 0) {
            return c;
        }

        BigDecimal tyLe = tuSo.divide(mauSo, 5, RoundingMode.HALF_UP);
        BigDecimal khoangQuyDoi = d.subtract(c);
        BigDecimal phanBu = tyLe.multiply(khoangQuyDoi);
        BigDecimal ketQua = c.add(phanBu);

        return ketQua.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhDiemVSATVaTHPT(NganhToHopDTO nganhToHop, BigDecimal d1, BigDecimal d2, BigDecimal d3) {
        BigDecimal diem1 = d1.multiply(BigDecimal.valueOf(nganhToHop.getHsMon1()));
        BigDecimal diem2 = d2.multiply(BigDecimal.valueOf(nganhToHop.getHsMon2()));
        BigDecimal diem3 = d3.multiply(BigDecimal.valueOf(nganhToHop.getHsMon3()));
        BigDecimal w = BigDecimal.valueOf(nganhToHop.getHsMon1()).add(BigDecimal.valueOf(nganhToHop.getHsMon2())).add(BigDecimal.valueOf(nganhToHop.getHsMon3()));

        BigDecimal tuSo = diem1.add(diem2).add(diem3);
        BigDecimal ketQua = tuSo.multiply(BigDecimal.valueOf(3))
                .divide(w, 2, RoundingMode.HALF_UP);

        return ketQua.subtract(nganhToHop.getDoLech());
    }

    public static BigDecimal tinhDiemUuTien (ThiSinhDTO thiSinh, BigDecimal diemCong, BigDecimal dthgxt) {
        String doiTuong = thiSinh.getDoiTuong();
        String khuVuc = thiSinh.getKhuVuc();

        BigDecimal mucDiemUuTienKV = switch (khuVuc) {
            case "KV1" -> new BigDecimal("0.75");
            case "KV2-NT" -> new BigDecimal("0.50");
            case "KV2" -> new BigDecimal("0.25");
            default -> BigDecimal.ZERO;
        };

        BigDecimal mucDiemUuTienDT = switch (doiTuong) {
            case "UT1" -> new BigDecimal("2.00");
            case "UT2" -> new BigDecimal("1.00");
            default -> BigDecimal.ZERO;
        };

        BigDecimal mucDiemUuTienTong = mucDiemUuTienKV.add(mucDiemUuTienDT);

        BigDecimal tongDiem = dthgxt.add(diemCong);
        BigDecimal mocGiamTru = new BigDecimal("22.5");

        if (tongDiem.compareTo(mocGiamTru) < 0) {
            return mucDiemUuTienTong;
        } else {
            BigDecimal tuSo = new BigDecimal("30").subtract(tongDiem);

            if (tuSo.compareTo(BigDecimal.ZERO) < 0) {
                return BigDecimal.ZERO;
            }

            return tuSo.multiply(mucDiemUuTienTong)
                    .divide(new BigDecimal("7.5"), 5, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal parseDiem(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            String cleanedValue = value.trim().replace(",", ".");
            return new BigDecimal(cleanedValue);
        } catch (Exception e) {
            return null;
        }
    }
}
