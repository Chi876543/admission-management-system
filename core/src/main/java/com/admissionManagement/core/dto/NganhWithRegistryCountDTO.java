package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class NganhWithRegistryCountDTO extends NganhDTO{
    private long soLuongDangKy;

    public NganhWithRegistryCountDTO(
            int idNganh, String maNganh, String tenNganh, String toHopGoc, int chiTieu,
            BigDecimal diemSan, BigDecimal diemTrungTuyen, String tuyenThang, String dgnl,
            String thpt, String vsat, Integer slXtt, Integer slDgnl, Integer slVsat,
            String slThpt, long soLuongDangKy) {

        super(idNganh, maNganh, tenNganh, toHopGoc, chiTieu, diemSan, diemTrungTuyen,
                tuyenThang, dgnl, thpt, vsat, slXtt, slDgnl, slVsat, slThpt);

        this.soLuongDangKy = soLuongDangKy;
    }
}
