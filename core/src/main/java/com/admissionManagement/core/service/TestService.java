package com.admissionManagement.core.service;

import com.admissionManagement.core.dto.ThongKeDTO;

import java.util.List;

public class TestService {
    public static void main(String[] args) {
        // 1. Khởi tạo BUS (Giả sử hàm getThongKeDiem nằm trong DiemThiBUS)
        DiemThiXetTuyenBUS diemThiBUS = new DiemThiXetTuyenBUS();

        // 2. Gọi hàm lấy dữ liệu
        List<ThongKeDTO> ketQua = diemThiBUS.getThongKeDiem();

        // 3. In tiêu đề bảng ra terminal
        System.out.println("\n--- KẾT QUẢ THỐNG KÊ ĐIỂM THÍ SINH ---");
        System.out.printf("%-12s | %-30s | %-8s | %-8s | %-8s | %-8s%n",
                "Loại", "Môn Học", "Số Lượng", "Min", "Max", "Avg");
        System.out.println("---------------------------------------------------------------------------------------------");

        // 4. Duyệt danh sách và in dữ liệu
        for (ThongKeDTO dto : ketQua) {
            System.out.printf("%-12s | %-30s | %-8d | %-8.2f | %-8.2f | %-8.2f%n",
                    dto.getLoaiKyThi(),
                    dto.getTenMon(),
                    dto.getSoLuong(),
                    dto.getDiemMin(),
                    dto.getDiemMax(),
                    dto.getDiemTrungBinh());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
    }
}
