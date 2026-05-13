package com.admissionManagement.core.service;

import com.admissionManagement.core.dto.NganhWithRegistryCountDTO;
import com.admissionManagement.core.dto.ThongKeDiemDTO;

import java.util.List;

public class TestService {
    public static void main(String[] args) {
//        // 1. Khởi tạo BUS (Giả sử hàm getThongKeDiem nằm trong DiemThiBUS)
//        DiemThiXetTuyenBUS diemThiBUS = new DiemThiXetTuyenBUS();
//
//        // 2. Gọi hàm lấy dữ liệu
//        List<ThongKeDiemDTO> ketQua = diemThiBUS.getThongKeDiem();
//
//        // 3. In tiêu đề bảng ra terminal
//        System.out.println("\n--- KẾT QUẢ THỐNG KÊ ĐIỂM THÍ SINH ---");
//        System.out.printf("%-12s | %-30s | %-8s | %-8s | %-8s | %-8s%n",
//                "Loại", "Môn Học", "Số Lượng", "Min", "Max", "Avg");
//        System.out.println("---------------------------------------------------------------------------------------------");
//
//        // 4. Duyệt danh sách và in dữ liệu
//        for (ThongKeDiemDTO dto : ketQua) {
//            System.out.printf("%-12s | %-30s | %-8d | %-8.2f | %-8.2f | %-8.2f%n",
//                    dto.getLoaiKyThi(),
//                    dto.getTenMon(),
//                    dto.getSoLuong(),
//                    dto.getDiemMin(),
//                    dto.getDiemMax(),
//                    dto.getDiemTrungBinh());
//        }
//        System.out.println("---------------------------------------------------------------------------------------------");

        NganhBUS nganhBUS = new NganhBUS();

        List<NganhWithRegistryCountDTO> ketqua = nganhBUS.getAllNganhWithRegistryCount();

        System.out.println("\n--- DANH SÁCH NGÀNH TUYỂN SINH ---");
        String headerFormat = "| %-10s | %-30s | %-8s | %-8s | %-8s | %-12s |%n";
        String rowFormat    = "| %-10s | %-30s | %-8d | %-8.2f | %-8.2f | %-12d |%n";

        System.out.format("+------------+--------------------------------+----------+----------+----------+--------------+%n");
        System.out.printf(headerFormat, "Mã Ngành", "Tên Ngành", "C.Tiêu", "Đ.Sàn", "Đ.Chuẩn", "SL Đăng Ký");
        System.out.format("+------------+--------------------------------+----------+----------+----------+--------------+%n");

        // Duyệt danh sách và in dữ liệu
        for (NganhWithRegistryCountDTO dto : ketqua) {
            // Xử lý Null để tránh lỗi khi printf gặp BigDecimal null
            double diemSan = (dto.getDiemSan() != null) ? dto.getDiemSan().doubleValue() : 0.0;
            double diemChuan = (dto.getDiemTrungTuyen() != null) ? dto.getDiemTrungTuyen().doubleValue() : 0.0;

            System.out.printf(rowFormat,
                    dto.getMaNganh(),
                    dto.getTenNganh(),
                    dto.getChiTieu(),
                    diemSan,
                    diemChuan,
                    dto.getSoLuongDangKy()); // Đây là trường COUNT từ HQL
        }

        System.out.format("+------------+--------------------------------+----------+----------+----------+--------------+%n");
        System.out.printf("Tổng số ngành: %d\n", ketqua.size());
    }
}
