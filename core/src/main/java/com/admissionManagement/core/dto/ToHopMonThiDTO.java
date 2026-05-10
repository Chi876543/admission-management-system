package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private String tenToHop;
}
