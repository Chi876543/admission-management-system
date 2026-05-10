package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private String thMon1;
    private Byte hsMon1; // tinyint dùng Byte

    private String thMon2;
    private Byte hsMon2;

    private String thMon3;
    private Byte hsMon3;

    @NonNull
    private String tbKeys;

    // boolean
    private Boolean anh;
    private Boolean toan;
    private Boolean ly;
    private Boolean hoa;
    private Boolean sinh;
    private Boolean van;
    private Boolean su;
    private Boolean dia;
    private Boolean tin;
    private Boolean khac;
    private Boolean ktpl;

    private BigDecimal doLech;
}
