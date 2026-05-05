package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class NganhToHopDTO {

    @NonNull
    private int id;

    @NonNull
    private String maNganh;

    @NonNull
    private String maToHop;

    @NonNull private String thMon1;
    @NonNull private Byte hsMon1; // tinyint dùng Byte

    @NonNull private String thMon2;
    @NonNull private Byte hsMon2;

    @NonNull private String thMon3;
    @NonNull private Byte hsMon3;

    @NonNull
    private String tbKeys;

    // boolean
    @NonNull private Boolean n1;
    @NonNull private Boolean toan;
    @NonNull private Boolean ly;
    @NonNull private Boolean hoa;
    @NonNull private Boolean sinh;
    @NonNull private Boolean van;
    @NonNull private Boolean su;
    @NonNull private Boolean dia;
    @NonNull private Boolean anh;
    @NonNull private Boolean khac;
    @NonNull private Boolean ktpl;

    @NonNull
    private BigDecimal doLech;
}
