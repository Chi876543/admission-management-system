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

    private String mon;

    private String phuongThuc;

    private BigDecimal diemUtxtToHop;

    private BigDecimal diemUtxtKhongXetToHop;

    private BigDecimal diemCc;

    private BigDecimal diemTongThxt;

    private BigDecimal diemTongKhongXetThxt;

    private String ghiChu;
}
