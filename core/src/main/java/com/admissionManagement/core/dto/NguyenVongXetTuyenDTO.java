package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguyenVongXetTuyenDTO {

    @NonNull
    private int idNv;

    @NonNull
    private String cccd;

    @NonNull
    private String maNganh;

    @NonNull
    private int thuTu;

    private BigDecimal diemThxt;

    private BigDecimal diemUtqd;

    private BigDecimal diemCong;

    private BigDecimal diemXetTuyen;

    private String ketQua;

    @NonNull
    private String nvKeys;

    private String phuongThuc;

    @NonNull
    private String thm;
}
