package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class NguyenVongXetTuyenDTO {

    @NonNull
    private int idNv;

    @NonNull
    private String cccd;

    @NonNull
    private String maNganh;

    @NonNull
    private int thuTu;

    @NonNull
    private BigDecimal diemThxt;

    @NonNull
    private BigDecimal diemUtqd;

    @NonNull
    private BigDecimal diemCong;

    @NonNull
    private BigDecimal diemXetTuyen;

    @NonNull
    private String ketQua;

    @NonNull
    private String nvKeys;

    @NonNull
    private String phuongThuc;

    @NonNull
    private String thm;
}
