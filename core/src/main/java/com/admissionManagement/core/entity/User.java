package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "xt_users",
        indexes = {
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_ho_ten", columnList = "ho_ten"),
                @Index(name = "idx_email", columnList = "email")
        }
)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "ho_ten", length = 100)
    private String hoTen;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "role", length = 20)
    private String role; // "Admin" hoặc "User"

    @Column(name = "status", length = 20)
    private String status; // "Hoạt động" hoặc "Đã khóa"
}
