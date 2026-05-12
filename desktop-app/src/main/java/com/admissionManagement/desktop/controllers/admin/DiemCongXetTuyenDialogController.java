//package com.admissionManagement.desktop.controllers.admin;
//
//import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
//import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//
//import java.math.BigDecimal;
//
//public class DiemCongXetTuyenDialogController extends BaseController {
//
//    private final DiemCongXetTuyenBUS bus = new DiemCongXetTuyenBUS();
//    private Stage dialogStage;
//    private DiemCongXetTuyenDTO editingRow;
//    private boolean isSaved = false;
//
//    @FXML private Label lblDialogTitle, lblError;
//    @FXML private TextField tfCccd, tfMaNganh, tfMaToHop, tfPhuongThuc, tfDiemCC, tfDiemUT, tfDiemTong, tfDcKeys, tfGhiChu;
//
//    public void setDialogData(Stage stage, DiemCongXetTuyenDTO row) {
//        this.dialogStage = stage;
//        this.editingRow = row;
//
//        if (row != null) {
//            lblDialogTitle.setText("Sửa điểm cộng ID: " + row.getIdDiemCong());
//            tfCccd.setText(row.getTsCccd());
//            tfMaNganh.setText(row.getMaNganh());
//            tfMaToHop.setText(row.getMaToHop());
//            tfPhuongThuc.setText(row.getPhuongThuc());
//            tfDiemCC.setText(row.getDiemCC() != null ? row.getDiemCC().toString() : "0");
//            tfDiemUT.setText(row.getDiemUtxt() != null ? row.getDiemUtxt().toString() : "0");
//            tfDiemTong.setText(row.getDiemTong() != null ? row.getDiemTong().toString() : "0");
//            tfDcKeys.setText(row.getDcKeys());
//            tfGhiChu.setText(row.getGhiChu());
//
//            tfCccd.setEditable(false); // Giữ tính nhất quán, không cho sửa CCCD khi update
//        } else {
//            lblDialogTitle.setText("Thêm điểm cộng mới");
//        }
//    }
//
//    @FXML
//    private void onDialogSave() {
//        // Validation cơ bản các trường @NonNull trong DTO
//        if (tfCccd.getText().trim().isEmpty() || tfMaNganh.getText().trim().isEmpty() || tfDcKeys.getText().trim().isEmpty()) {
//            lblError.setText("CCCD, Mã ngành và DC Keys không được để trống.");
//            return;
//        }
//
//        try {
//            DiemCongXetTuyenDTO dto = new DiemCongXetTuyenDTO();
//            if (editingRow != null) dto.setIdDiemCong(editingRow.getIdDiemCong());
//
//            dto.setTsCccd(tfCccd.getText().trim());
//            dto.setMaNganh(tfMaNganh.getText().trim());
//            dto.setMaToHop(tfMaToHop.getText().trim());
//            dto.setPhuongThuc(tfPhuongThuc.getText().trim());
//            dto.setDiemCC(new BigDecimal(tfDiemCC.getText().trim()));
//            dto.setDiemUtxt(new BigDecimal(tfDiemUT.getText().trim()));
//            dto.setDiemTong(new BigDecimal(tfDiemTong.getText().trim()));
//            dto.setGhiChu(tfGhiChu.getText().trim());
//            dto.setDcKeys(tfDcKeys.getText().trim());
//
//            String result = (editingRow == null) ? bus.addDiemCongXetTuyen(dto) : bus.updateDiemCongXetTuyen(dto.getIdDiemCong(), dto);
//
//            if (result.contains("successfully")) {
//                showInfo("Thành công", result);
//                isSaved = true;
//                dialogStage.close();
//            } else {
//                lblError.setText(result);
//            }
//        } catch (NumberFormatException e) {
//            lblError.setText("Lỗi: Các ô điểm phải là số.");
//        }
//    }
//
//    @FXML private void onDialogCancel() { dialogStage.close(); }
//    public boolean getIsSaved() { return isSaved; }
//}