package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiemCongXetTuyenRequestDTO {
    @NonNull
    private String tsCccd;
    private ChungChiNgoaiNguDTO chungChi;
    List<GiaiThuongHsgDTO> danhSachGiaiThuong;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChungChiNgoaiNguDTO {
        private String tenChungChi;
        private String mucDiem;
        private String diemNghe;
        private String diemDoc;
        private String diemNoi;
        private String diemViet;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiaiThuongHsgDTO {
        private String mon;
        private String loaiGiai;
        private String cap;
    }
}
