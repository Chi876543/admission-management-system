package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ThongKeDTO {
    @NonNull
    private String loaiKyThi;   // VSAT, THPT, ĐGNL
    @NonNull
    private String tenMon;
    @NonNull
    private long soLuong;
    @NonNull
    private double diemMin;
    @NonNull
    private double diemMax;
    @NonNull
    private double diemTrungBinh;
}
