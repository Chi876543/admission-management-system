package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class NganhDTO {
    @NonNull
    private int idNganh;

    @NonNull
    private String maNganh;

    @NonNull
    private String tenNganh;

    @NonNull
    private String toHopGoc;

    @NonNull
    private int chiTieu;

    @NonNull
    private BigDecimal diemSan;

    @NonNull
    private BigDecimal diemTrungTuyen;

    @NonNull private String tuyenThang;
    @NonNull private String dgnl;
    @NonNull private String thpt;
    @NonNull private String vsat;

    @NonNull private Integer slXtt;
    @NonNull private Integer slDgnl;
    @NonNull private Integer slVsat;
    @NonNull private String slThpt;
}
