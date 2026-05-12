package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(
        name = "xt_thisinhxettuyen25",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_thisinh_cccd", columnNames = {"cccd"})
        },
        indexes = {
                @Index(name = "idx_thisinh_ten", columnList = "ten"),
                @Index(name = "idx_thisinh_ho_ten", columnList = "ho, ten")
        }
)

public class ThiSinh {
    public enum GioiTinh {
        NAM("Nam"),
        NU("Nữ"),
        KHAC("Khác");

        private final String label;

        GioiTinh(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static GioiTinh fromLabel(String gender) {
            if (gender == null) return KHAC;

            for(GioiTinh gt : GioiTinh.values()) {
                if(gt.label.equalsIgnoreCase(gender.trim())){
                    return gt;
                }
            }
            return KHAC;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idthisinh", nullable = false)
    private int idThiSinh;

    @Column(name = "cccd", length = 20, nullable = false)
    private String cccd;

    @Column(name = "sobaodanh", length = 45)
    private String soBaoDanh;

    @Column(name = "ho", length = 100)
    private String ho;

    @Column(name = "ten", length = 100)
    private String ten;

    @Column(name = "ngay_sinh", length = 45)
    private String ngaySinh;

    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh")
    private GioiTinh gioiTinh;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "noi_sinh", length = 45)
    private String noiSinh;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "doi_tuong", length = 45)
    private String doiTuong;

    @Column(name = "khu_vuc", length = 45)
    private String khuVuc;

    @OneToOne(mappedBy = "thiSinh", cascade = CascadeType.ALL, orphanRemoval = true)
    private DiemThiXetTuyen diemThi;

    @OneToMany(mappedBy = "thiSinh", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiemCongXetTuyen> danhSachDiemCongCuaThiSinh = new ArrayList<>();

    @OneToMany(mappedBy = "thiSinh", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NguyenVongXetTuyen> danhSachNguyenVongCuaThiSinh = new ArrayList<>();

    public void asyncDiemThi(DiemThiXetTuyen diem) {
        if (diem == null) {
            if (this.diemThi != null) {
                this.diemThi.setThiSinh(null);
            }
        } else {
            diem.setThiSinh(this);
        }
        this.diemThi = diem;
    }
}
