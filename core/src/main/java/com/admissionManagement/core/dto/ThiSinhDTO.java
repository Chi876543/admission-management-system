package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String dienThoai;
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String noiSinh;
    private String doiTuong;
    private String khuVuc;
    @NonNull
    private String updatedAt;
}
