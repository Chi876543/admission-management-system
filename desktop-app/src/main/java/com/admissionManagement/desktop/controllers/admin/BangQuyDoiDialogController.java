package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.ToHopMonThiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
import com.admissionManagement.core.service.ToHopMonThiBUS;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BangQuyDoiDialogController extends BaseController {

    private BangQuyDoiBUS bangQuyDoiBUS;
    private Stage dialogStage;
    private BangQuyDoiDTO editingRow;
    private boolean isSaved = false;

    private final ToHopMonThiBUS toHopBUS = new ToHopMonThiBUS();

    @FXML private Label lblDialogTitle, lblError;
    @FXML private ComboBox<String> cbPhuongThuc, cbToHop, cbMon;
    @FXML private TextField tfDiemA, tfDiemB, tfDiemC, tfDiemD;

    public void init(Stage stage, BangQuyDoiDTO row, BangQuyDoiBUS bus) {
        this.dialogStage   = stage;
        this.editingRow    = row;
        this.bangQuyDoiBUS = bus;

        setupComboBox();

        if (row != null) {
            lblDialogTitle.setText("Sửa luật quy đổi ID: " + row.getIdqd());
            cbPhuongThuc.setValue(row.getPhuongThuc());
            cbToHop.setValue(row.getToHop());
            cbMon.setValue(row.getMon());
            tfDiemA.setText(row.getDiemA() != null ? row.getDiemA().toPlainString() : "");
            tfDiemB.setText(row.getDiemB() != null ? row.getDiemB().toPlainString() : "");
            tfDiemC.setText(row.getDiemC() != null ? row.getDiemC().toPlainString() : "");
            tfDiemD.setText(row.getDiemD() != null ? row.getDiemD().toPlainString() : "");
        } else {
            lblDialogTitle.setText("Thêm luật quy đổi mới");
        }
    }

    private void setupComboBox() {
        cbPhuongThuc.setItems(FXCollections.observableArrayList(
                "THPT", "DGNL", "VSAT", "HSA"
        ));

        // Load danh sách tổ hợp từ DB — tự cập nhật khi có tổ hợp mới
        loadToHopFromDb();

        cbMon.setItems(FXCollections.observableArrayList(
                "Toán", "Văn", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Tin học",
                "Tiếng Anh", "KTPL"
        ));
    }

    /** Load mã tổ hợp từ DB thay vì hardcode */
    private void loadToHopFromDb() {
        try {
            List<ToHopMonThiDTO> danhSach = toHopBUS.getAllToHopMonThi();
            List<String> maToHopList = danhSach.stream()
                    .map(ToHopMonThiDTO::getMaToHop)
                    .sorted()
                    .distinct()
                    .collect(Collectors.toList());
            cbToHop.setItems(FXCollections.observableArrayList(maToHopList));
        } catch (Exception e) {
            // Fallback danh sách cứng nếu DB lỗi
            cbToHop.setItems(FXCollections.observableArrayList(
                    "A00", "A01", "A02", "B00", "B03", "C00", "C01", "D01", "D07", "D14"
            ));
        }
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");
        lblError.setStyle("-fx-text-fill: red;");

        if (cbPhuongThuc.getValue() == null) { lblError.setText("Vui lòng chọn phương thức."); return; }
        if (cbToHop.getValue() == null)       { lblError.setText("Vui lòng chọn tổ hợp."); return; }
        if (cbMon.getValue() == null)          { lblError.setText("Vui lòng chọn môn."); return; }

        BigDecimal dA = parseBigDecimal(tfDiemA, "Điểm A"); if (dA == null) return;
        BigDecimal dB = parseBigDecimal(tfDiemB, "Điểm B"); if (dB == null) return;
        BigDecimal dC = parseBigDecimal(tfDiemC, "Điểm C"); if (dC == null) return;
        BigDecimal dD = parseBigDecimal(tfDiemD, "Điểm D"); if (dD == null) return;

        String result;

        if (editingRow == null) {
            BangQuyDoiDTO dto = new BangQuyDoiDTO(
                    0,
                    cbPhuongThuc.getValue(),
                    cbToHop.getValue(),
                    cbMon.getValue(),
                    dA, dB, dC, dD,
                    generateMaQuyDoi(),
                    null
            );
            result = bangQuyDoiBUS.addBangQuyDoi(dto);
        } else {
            editingRow.setPhuongThuc(cbPhuongThuc.getValue());
            editingRow.setToHop(cbToHop.getValue());
            editingRow.setMon(cbMon.getValue());
            editingRow.setDiemA(dA);
            editingRow.setDiemB(dB);
            editingRow.setDiemC(dC);
            editingRow.setDiemD(dD);
            result = bangQuyDoiBUS.updateBangQuyDoi(editingRow.getIdqd(), editingRow);
        }

        if (result.startsWith("Lỗi")) {
            lblError.setText(result);
            return;
        }

        showInfo("Thành công", result);
        isSaved = true;
        dialogStage.close();
    }

    @FXML
    private void onDialogCancel() {
        dialogStage.close();
    }

    public boolean getIsSaved() { return isSaved; }

    private BigDecimal parseBigDecimal(TextField tf, String fieldName) {
        try {
            return new BigDecimal(tf.getText().trim());
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " phải là số hợp lệ.");
            return null;
        }
    }

    private String generateMaQuyDoi() {
        return "QD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
