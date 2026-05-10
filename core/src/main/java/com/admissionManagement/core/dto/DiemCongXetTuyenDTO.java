package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiemCongXetTuyenDTO {

    @NonNull
    private int idDiemCong;

    @NonNull
    private String tsCccd;

    @NonNull
    private String maNganh;

    @NonNull
    private String maToHop;

    private String phuongThuc;

    private BigDecimal diemCC;

    private BigDecimal diemUtxt;

    private BigDecimal diemTong;

    private String ghiChu;

    @NonNull
    private String dcKeys;
}
