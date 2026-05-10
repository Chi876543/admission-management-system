package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BangQuyDoiDTO {

    @NonNull
    private int idqd;

    private String phuongThuc;

    private String toHop;

    private String mon;

    private BigDecimal diemA;

    private BigDecimal diemB;

    private BigDecimal diemC;

    private BigDecimal diemD;

    @NonNull
    private String maQuyDoi;

    private String phanVi;
}
