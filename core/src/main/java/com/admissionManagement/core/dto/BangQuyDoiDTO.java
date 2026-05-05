package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class BangQuyDoiDTO {

    @NonNull
    private int idqd;

    @NonNull
    private String phuongThuc;

    @NonNull
    private String toHop;

    @NonNull
    private String mon;

    @NonNull
    private BigDecimal diemA;

    @NonNull
    private BigDecimal diemB;

    @NonNull
    private BigDecimal diemC;

    @NonNull
    private BigDecimal diemD;

    @NonNull
    private String maQuyDoi;

    @NonNull
    private String phanVi;
}
