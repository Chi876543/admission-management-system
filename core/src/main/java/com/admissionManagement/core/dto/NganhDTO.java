package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NganhDTO {
    @NonNull
    private int idNganh;

    @NonNull
    private String maNganh;

    @NonNull
    private String tenNganh;

    private String toHopGoc;

    @NonNull
    private int chiTieu;

    private BigDecimal diemSan;

    private BigDecimal diemTrungTuyen;

    private String tuyenThang;
    private String dgnl;
    private String thpt;
    private String vsat;

    private Integer slXtt;
    private Integer slDgnl;
    private Integer slVsat;
    private String slThpt;
}
