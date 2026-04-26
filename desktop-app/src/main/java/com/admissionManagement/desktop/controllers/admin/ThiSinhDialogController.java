package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.service.ThiSinhBUS;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ThiSinhDialogController extends BaseController {
    private final ThiSinhBUS thiSinhBUS = new ThiSinhBUS();
    private Stage dialogStage;
    private ThiSinhDTO editingRow;
    private boolean isSaved = false;

    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfSbd, tfCccd, tfHo, tfTen, tfSdt, tfEmail, tfNoiSinh;
    @FXML private DatePicker dpNgaySinh;
    @FXML private ComboBox<String> cbGioiTinh, cbDoiTuong, cbKhuVuc;

    @FXML public void initialize() {
        cbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        cbDoiTuong.setItems(FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "Không"));
        cbKhuVuc.setItems(FXCollections.observableArrayList("KV1", "KV2", "KV2-NT", "KV3"));
    }

    public void setDialogData(Stage stage, ThiSinhDTO row) {
        this.dialogStage = stage;
        this.editingRow = row;

        if (row != null) {
            lblDialogTitle.setText("Sửa hồ sơ thí sinh ID: " + row.getIdThiSinh());
            tfSbd.setText(row.getSoBaoDanh());
            tfCccd.setText(row.getCccd());
            tfHo.setText(row.getHo());
            tfTen.setText(row.getTen());
            if (row.getNgaySinh() != null && !row.getNgaySinh().trim().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate ngaySinh = LocalDate.parse(row.getNgaySinh(), formatter);
                    dpNgaySinh.setValue(ngaySinh);
                } catch (Exception e) {
                    dpNgaySinh.setValue(null);
                }
            } else {
                dpNgaySinh.setValue(null);
            }
            tfSdt.setText(row.getDienThoai());
            tfEmail.setText(row.getEmail());
            tfNoiSinh.setText(row.getNoiSinh());
            cbGioiTinh.setValue(row.getGioiTinh());
            cbDoiTuong.setValue(row.getDoiTuong());
            cbKhuVuc.setValue(row.getKhuVuc());
        } else {
            lblDialogTitle.setText("Thêm thí sinh mới");
        }
    }

    @FXML private void onDialogSave() {
        if (tfHo.getText().trim().isEmpty() || tfTen.getText().trim().isEmpty()) {
            lblError.setText("Họ và Tên không được để trống.");
            return;
        }

        LocalDate datePicked = dpNgaySinh.getValue();
        String ngaySinh = "";
        if (datePicked != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            ngaySinh = datePicked.format(formatter);
        }
        if (editingRow == null) {
            ThiSinhDTO r = new ThiSinhDTO(
                    0,
                    tfSbd.getText().trim(),
                    tfCccd.getText().trim(),
                    tfHo.getText().trim(),
                    tfTen.getText().trim(),
                    ngaySinh,
                    cbGioiTinh.getValue(),
                    tfSdt.getText().trim(),
                    tfEmail.getText().trim(),
                    "123456",
                    tfNoiSinh.getText().trim(),
                    cbDoiTuong.getValue(),
                    cbKhuVuc.getValue(),
                    null
            );
            String result = thiSinhBUS.addThiSinh(r);
            if (result.startsWith("Lỗi:")) {
                lblError.setText(result);
                return;
            } else {
                showInfo("Thành công", result);
            }
        } else {
            editingRow.setSoBaoDanh(tfSbd.getText().trim());
            editingRow.setHo(tfHo.getText().trim());
            editingRow.setTen(tfTen.getText().trim());
            editingRow.setNgaySinh(ngaySinh);
            editingRow.setDienThoai(tfSdt.getText().trim());
            editingRow.setEmail(tfEmail.getText().trim());
            editingRow.setNoiSinh(tfNoiSinh.getText().trim());
            editingRow.setGioiTinh(cbGioiTinh.getValue());
            editingRow.setDoiTuong(cbDoiTuong.getValue());
            editingRow.setKhuVuc(cbKhuVuc.getValue());

            String result = thiSinhBUS.updateThiSinh(editingRow.getIdThiSinh(), editingRow);
            if (result.startsWith("Lỗi:")) {
                lblError.setText(result);
                return;
            } else {
                showInfo("Thành công", result);
            }
        }
        isSaved = true;
        dialogStage.close();
    }

    @FXML private void onDialogCancel() {
        dialogStage.close();
    }

    public boolean getIsSaved() {
        return isSaved;
    }
}
