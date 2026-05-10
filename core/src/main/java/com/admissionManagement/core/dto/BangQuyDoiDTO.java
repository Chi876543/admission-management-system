package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
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
