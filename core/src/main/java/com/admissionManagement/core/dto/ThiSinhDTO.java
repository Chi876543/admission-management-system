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
    String soBaoDanh;
    @NonNull
    private String cccd;
    private String ho;
    private String ten;
    private String ngaySinh;
    private String gioiTinh;
    private String dienThoai;
    private String email;
    @NonNull
    private String password;
    private String noiSinh;
    private String doiTuong;
    private String khuVuc;
    private String updatedAt;
}
