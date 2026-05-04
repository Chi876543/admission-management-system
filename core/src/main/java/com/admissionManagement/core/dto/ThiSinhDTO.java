package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ThiSinhDTO {
    @NonNull
    private int idThiSinh;
    @NonNull
    String soBaoDanh;
    @NonNull
    private String cccd;
    @NonNull
    private String ho;
    @NonNull
    private String ten;
    @NonNull
    private String ngaySinh;
    @NonNull
    private String gioiTinh;
    @NonNull
    private String dienThoai;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String noiSinh;
    @NonNull
    private String doiTuong;
    @NonNull
    private String khuVuc;
    @NonNull
    private String updatedAt;
}
