package com.admissionManagement.core.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ToHopMonThiDTO {
    @NonNull
    private int idToHop;

    @NonNull
    private String maToHop;

    @NonNull
    private String mon1;

    @NonNull
    private String mon2;

    @NonNull
    private String mon3;

    @NonNull
    private String tenToHop;
}
