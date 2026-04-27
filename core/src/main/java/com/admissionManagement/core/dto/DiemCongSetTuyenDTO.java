package com.admissionManagement.core.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class DiemCongSetTuyenDTO {

    @NonNull
    private int idDiemCong;

    @NonNull
    private String tsCccd;

    @NonNull
    private String maNganh;

    @NonNull
    private String maToHop;

    @NonNull
    private String phuongThuc;

    @NonNull
    private BigDecimal diemCC;

    @NonNull
    private BigDecimal diemUtxt;

    @NonNull
    private BigDecimal diemTong;

    @NonNull
    private String ghiChu;

    @NonNull
    private String dcKeys;
}
