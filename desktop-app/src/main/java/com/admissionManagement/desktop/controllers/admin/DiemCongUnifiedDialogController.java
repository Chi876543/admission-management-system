package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

/**
 * Dialog hợp nhất: Thêm/Sửa cả UTXT và CC trong cùng một dialog.
 * Dùng TabPane để phân biệt loại.
 */
public class DiemCongUnifiedDialogController extends BaseController {

    private DiemCongXetTuyenBUS bus;
    private Stage dialogStage;
    private DiemCongXetTuyenDTO editingRow;
    private boolean isSaved = false;
    private DiemCongXetTuyenDTO savedData;
    private boolean isLoading = false;

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private TabPane tabPane;

    // ── Tab UTXT ─────────────────────────────────────────
    @FXML private TextField tfCccdUtxt;
    @FXML private ComboBox<String> cbMonUtxt;
    @FXML private ComboBox<String> cbPhuongThucUtxt;
    @FXML private ComboBox<String> cbLoaiGiai;
    @FXML private ComboBox<String> cbCap;
    @FXML private TextField tfGhiChuUtxt;

    // ── Tab CC ────────────────────────────────────────────
    @FXML private TextField tfCccdCc;
    @FXML private ComboBox<String> cbPhuongThucCc;
    @FXML private ComboBox<String> cbMucDiem;
    @FXML private TextField tfGhiChuCc;
    @FXML private Label lblPreviewCc;

    private static final String[] MUC_DIEM_LABELS = {
            "IELTS 6.0 / TOEIC 700 (mức 8)",
            "IELTS 6.5 / TOEIC 800 (mức 9)",
            "IELTS 7.0+ / TOEIC 900+ (mức 10)"
    };
    private static final String[] MUC_DIEM_KEYS = { "8", "9", "10" };

    public void init(Stage stage, DiemCongXetTuyenDTO row, DiemCongXetTuyenBUS bus, boolean preferCc) {
        this.dialogStage = stage;
        this.editingRow  = row;
        this.bus         = bus;

        setupUtxtTab();
        setupCcTab();

        if (row != null) {
            isLoading = true;
            lblDialogTitle.setText("Sửa điểm cộng ID: " + row.getIdDiemCong());

            // Xác định loại: nếu mon == "N1" và diemCc != 0 → CC, ngược lại → UTXT
            boolean isCc = "N1".equals(row.getMon())
                    && row.getDiemCc() != null
                    && row.getDiemCc().compareTo(BigDecimal.ZERO) != 0;

            if (isCc) {
                tabPane.getSelectionModel().select(1); // Tab CC
                tfCccdCc.setText(row.getTsCccd());
                tfCccdCc.setDisable(true);
                cbPhuongThucCc.setValue(row.getPhuongThuc());
                if (row.getDiemCc() != null) restoreMucDiem(row.getDiemCc(), row.getPhuongThuc());
                tfGhiChuCc.setText(row.getGhiChu() != null ? row.getGhiChu() : "");
            } else {
                tabPane.getSelectionModel().select(0); // Tab UTXT
                tfCccdUtxt.setText(row.getTsCccd());
                tfCccdUtxt.setDisable(true);
                cbMonUtxt.setValue(row.getMon());
                cbPhuongThucUtxt.setValue(row.getPhuongThuc());
                parseGhiChuToComboBox(row.getGhiChu());
                tfGhiChuUtxt.setText(row.getGhiChu() != null ? row.getGhiChu() : "");
            }
            // Khóa tab khi sửa (không cho chuyển sang loại khác)
            tabPane.getTabs().get(isCc ? 0 : 1).setDisable(true);
            isLoading = false;
        } else {
            lblDialogTitle.setText("Thêm điểm cộng mới");
            if (preferCc) tabPane.getSelectionModel().select(1);
        }
    }

    private void setupUtxtTab() {
        cbMonUtxt.setItems(FXCollections.observableArrayList(
                "Toán", "Văn", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Tin học"
        ));
        cbPhuongThucUtxt.setItems(FXCollections.observableArrayList("THPT", "DGNL", "VSAT"));
        cbLoaiGiai.setItems(FXCollections.observableArrayList("Nhất", "Nhì", "Ba", "Khuyến khích"));
        cbCap.setItems(FXCollections.observableArrayList("Quốc tế", "Quốc gia", "Tỉnh", "Thành phố"));

        cbLoaiGiai.valueProperty().addListener((obs, o, n) -> { if (!isLoading) autoFillGhiChuUtxt(); });
        cbCap.valueProperty().addListener((obs, o, n) -> { if (!isLoading) autoFillGhiChuUtxt(); });
    }

    private void setupCcTab() {
        cbPhuongThucCc.setItems(FXCollections.observableArrayList("THPT", "DGNL", "VSAT"));
        cbMucDiem.setItems(FXCollections.observableArrayList(MUC_DIEM_LABELS));
        cbMucDiem.valueProperty().addListener((obs, o, n) -> updatePreviewCc());
        cbPhuongThucCc.valueProperty().addListener((obs, o, n) -> updatePreviewCc());
    }

    private void autoFillGhiChuUtxt() {
        String loai = cbLoaiGiai.getValue();
        String cap  = cbCap.getValue();
        if (loai != null && cap != null) {
            tfGhiChuUtxt.setText("Giải " + loai + " - Cấp " + cap);
        }
    }

    private void updatePreviewCc() {
        String mucLabel = cbMucDiem.getValue();
        String pt       = cbPhuongThucCc.getValue();
        if (mucLabel == null || pt == null) { lblPreviewCc.setText(""); return; }
        BigDecimal diem = DatabaseHelper.tinhDiemChungChiTiengAnh(getMucDiemKey(mucLabel), pt);
        lblPreviewCc.setText("→ Điểm CC quy đổi: " + diem.toPlainString());
    }

    private void parseGhiChuToComboBox(String ghiChu) {
        if (ghiChu == null || ghiChu.isBlank()) return;
        for (String loai : new String[]{"Nhất", "Nhì", "Ba", "Khuyến khích"}) {
            if (ghiChu.contains(loai)) { cbLoaiGiai.setValue(loai); break; }
        }
        for (String cap : new String[]{"Quốc tế", "Quốc gia", "Thành phố", "Tỉnh"}) {
            if (ghiChu.contains(cap)) { cbCap.setValue(cap); break; }
        }
    }

    private void restoreMucDiem(BigDecimal diemCc, String phuongThuc) {
        if (phuongThuc == null) return;
        for (int i = 0; i < MUC_DIEM_KEYS.length; i++) {
            BigDecimal cal = DatabaseHelper.tinhDiemChungChiTiengAnh(MUC_DIEM_KEYS[i], phuongThuc);
            if (cal.compareTo(diemCc) == 0) { cbMucDiem.setValue(MUC_DIEM_LABELS[i]); break; }
        }
    }

    private String getMucDiemKey(String label) {
        for (int i = 0; i < MUC_DIEM_LABELS.length; i++) {
            if (MUC_DIEM_LABELS[i].equals(label)) return MUC_DIEM_KEYS[i];
        }
        return "8";
    }

    @FXML
    private void onDialogSave() {
        lblError.setText("");
        lblError.setStyle("-fx-text-fill: red;");

        int selectedTab = tabPane.getSelectionModel().getSelectedIndex();
        if (selectedTab == 0) {
            saveUtxt();
        } else {
            saveCc();
        }
    }

    private void saveUtxt() {
        String cccd = tfCccdUtxt.getText().trim();
        if (cccd.isEmpty()) { lblError.setText("CCCD không được để trống."); return; }
        if (cbMonUtxt.getValue() == null) { lblError.setText("Vui lòng chọn môn."); return; }
        if (cbPhuongThucUtxt.getValue() == null) { lblError.setText("Vui lòng chọn phương thức."); return; }
        if (cbLoaiGiai.getValue() == null) { lblError.setText("Vui lòng chọn loại giải."); return; }
        if (cbCap.getValue() == null) { lblError.setText("Vui lòng chọn cấp."); return; }

        BigDecimal[] diem = DatabaseHelper.tinhDiemUtxt(
                cbCap.getValue().toLowerCase(),
                cbLoaiGiai.getValue().toLowerCase(),
                cbPhuongThucUtxt.getValue()
        );

        // Luôn dùng combobox để tạo ghi chú chuẩn (Fix lỗi 1 & 4)
        String ghiChu = "Giải " + cbLoaiGiai.getValue() + " - Cấp " + cbCap.getValue();

        DiemCongXetTuyenDTO dto = new DiemCongXetTuyenDTO(
                editingRow != null ? editingRow.getIdDiemCong() : 0,
                cccd, cbMonUtxt.getValue(), cbPhuongThucUtxt.getValue(),
                diem[0], diem[1], BigDecimal.ZERO, diem[0], diem[1], ghiChu
        );
        persistDto(dto);
    }

    private void saveCc() {
        String cccd = tfCccdCc.getText().trim();
        if (cccd.isEmpty()) { lblError.setText("CCCD không được để trống."); return; }
        if (cbPhuongThucCc.getValue() == null) { lblError.setText("Vui lòng chọn phương thức."); return; }
        if (cbMucDiem.getValue() == null) { lblError.setText("Vui lòng chọn mức điểm chứng chỉ."); return; }

        String mucKey = getMucDiemKey(cbMucDiem.getValue());
        String pt     = cbPhuongThucCc.getValue();
        BigDecimal diemCc = DatabaseHelper.tinhDiemChungChiTiengAnh(mucKey, pt);

        String ghiChu = tfGhiChuCc.getText().trim();
        if (ghiChu.isEmpty()) ghiChu = cbMucDiem.getValue();

        DiemCongXetTuyenDTO dto = new DiemCongXetTuyenDTO(
                editingRow != null ? editingRow.getIdDiemCong() : 0,
                cccd, "N1", pt,
                BigDecimal.ZERO, BigDecimal.ZERO, diemCc, BigDecimal.ZERO, diemCc, ghiChu
        );
        persistDto(dto);
    }

    private void persistDto(DiemCongXetTuyenDTO dto) {
        if (editingRow == null) {
            DiemCongXetTuyenDTO returned = bus.addAndReturn(dto);
            if (returned == null) {
                lblError.setText("Lỗi: Không thể thêm bản ghi (kiểm tra CCCD và dữ liệu).");
                return;
            }
            savedData = returned;
        } else {
            String result = bus.updateDiemCongXetTuyen(dto.getIdDiemCong(), dto);
            if (result.startsWith("Lỗi")) { lblError.setText(result); return; }
            savedData = dto;
        }
        showInfo("Thành công", editingRow == null ? "Đã thêm bản ghi." : "Đã cập nhật bản ghi.");
        isSaved = true;
        dialogStage.close();
    }

    @FXML
    private void onDialogCancel() { dialogStage.close(); }

    public boolean getIsSaved()               { return isSaved; }
    public DiemCongXetTuyenDTO getSavedData() { return savedData; }
}
