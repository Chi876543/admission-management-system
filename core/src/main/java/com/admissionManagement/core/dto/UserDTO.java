package com.admissionManagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;
    private String username;
    private String hoTen;
    private String email;
    private String password;
    private String role;    // "Admin" hoặc "User"
    private String status;  // "Hoạt động" hoặc "Đã khóa"
}
