package com.admissionManagement.core.dto;

public class ThiSinhDTO {
    private int idThiSinh;
    private String soBaoDanh, cccd, ho, ten, ngaySinh, gioiTinh, dienThoai, email, password, noiSinh, doiTuong, khuVuc, updatedAt;

    public int getIdThiSinh() {
        return idThiSinh;
    }

    public void setIdThiSinh(int idThiSinh) {
        this.idThiSinh = idThiSinh;
    }

    public String getSoBaoDanh() {
        return soBaoDanh;
    }

    public void setSoBaoDanh(String soBaoDanh) {
        this.soBaoDanh = soBaoDanh;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNoiSinh() {
        return noiSinh;
    }

    public void setNoiSinh(String noiSinh) {
        this.noiSinh = noiSinh;
    }

    public String getDoiTuong() {
        return doiTuong;
    }

    public void setDoiTuong(String doiTuong) {
        this.doiTuong = doiTuong;
    }

    public String getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(String khuVuc) {
        this.khuVuc = khuVuc;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ThiSinhDTO(int idThiSinh, String soBaoDanh, String cccd, String ho, String ten, String ngaySinh, String gioiTinh, String dienThoai, String email, String password, String noiSinh, String doiTuong, String khuVuc, String updatedAt) {
        this.idThiSinh = idThiSinh;
        this.soBaoDanh = soBaoDanh;
        this.cccd = cccd;
        this.ho = ho;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.dienThoai = dienThoai;
        this.email = email;
        this.password = password;
        this.noiSinh = noiSinh;
        this.doiTuong = doiTuong;
        this.khuVuc = khuVuc;
        this.updatedAt = updatedAt;
    }
}
