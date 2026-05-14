package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhWithRegistryCountDTO;
import com.admissionManagement.core.service.NganhBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NganhController extends BaseController implements Initializable {

    @FXML private Label     lblFormTitle, lblError, lblCount, lblImportStatus;
    @FXML private TextField tfMaNganh, tfTenNganh, tfToHopGoc, tfChiTieu;
    @FXML private TextField tfDiemSan, tfDiemTrungTuyen, tfSearch;
    @FXML private CheckBox  cbTuyenThang, cbDgnl, cbThpt, cbVsat;

    @FXML private TableView<NganhRow>           tblNganh;
    @FXML private TableColumn<NganhRow, String> colMa, colTen, colToHopGoc;
    @FXML private TableColumn<NganhRow, String> colChiTieu, colDiemSan, colDiemTrungTuyen, colPhuongThuc, colSoLuong;
    @FXML private TableColumn<NganhRow, Void>   colAction;

    private final ObservableList<NganhRow> allData = FXCollections.observableArrayList();
    private final NganhBUS nganhBUS = new NganhBUS();
    private NganhRow editingRow;

    @Override
    public void initialize(URL u, ResourceBundle r) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNganh"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenNganh"));
        colToHopGoc.setCellValueFactory(new PropertyValueFactory<>("toHopGoc"));
        colChiTieu.setCellValueFactory(new PropertyValueFactory<>("chiTieu"));
        colDiemSan.setCellValueFactory(new PropertyValueFactory<>("diemSan"));
        colDiemTrungTuyen.setCellValueFactory(new PropertyValueFactory<>("diemTrungTuyen"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuongDangKy"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> loadToForm(getTableView().getItems().get(getIndex())),
                    () -> onDeleteRow(getTableView().getItems().get(getIndex()))
            );
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : box);
            }
        });
    }

    public void loadData() {
        List<NganhWithRegistryCountDTO> list = new ArrayList<>(nganhBUS.getAllNganhWithRegistryCount());
        Collections.reverse(list);
        allData.setAll(list.stream().map(dto -> new NganhRow(
                dto.getIdNganh(), dto.getMaNganh(), dto.getTenNganh(),
                dto.getToHopGoc() != null ? dto.getToHopGoc() : "",
                String.valueOf(dto.getChiTieu()),
                dto.getDiemSan() != null ? dto.getDiemSan().toPlainString() : "0",
                dto.getDiemTrungTuyen() != null ? dto.getDiemTrungTuyen().toPlainString() : "0",
                "1".equals(dto.getTuyenThang()),
                "1".equals(dto.getDgnl()),
                "1".equals(dto.getThpt()),
                "1".equals(dto.getVsat()),
                dto.getSoLuongDangKy()
        )).collect(Collectors.toList()));
        refresh();
    }

    private void refresh() {
        String kw = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        List<NganhRow> f = allData.stream()
                .filter(r -> kw.isEmpty()
                        || r.getMaNganh().toLowerCase().contains(kw)
                        || r.getTenNganh().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        tblNganh.setItems(FXCollections.observableArrayList(f));
        lblCount.setText(f.size() + " ngành");
    }

    @FXML private void onSearch() { refresh(); }

    @FXML private void onTableClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            NganhRow row = tblNganh.getSelectionModel().getSelectedItem();
            if (row != null) loadToForm(row);
        }
    }

    private void loadToForm(NganhRow row) {
        editingRow = row;
        lblFormTitle.setText("Sửa ngành: " + row.getMaNganh());
        tfMaNganh.setText(row.getMaNganh()); tfMaNganh.setDisable(true);
        tfTenNganh.setText(row.getTenNganh());
        tfToHopGoc.setText(row.getToHopGoc());
        tfChiTieu.setText(row.getChiTieu());
        tfDiemSan.setText(row.getDiemSan());
        tfDiemTrungTuyen.setText(row.getDiemTrungTuyen());
        cbTuyenThang.setSelected(row.isTuyenThang());
        cbDgnl.setSelected(row.isDgnl());
        cbThpt.setSelected(row.isThpt());
        cbVsat.setSelected(row.isVsat());
        lblError.setText("");
    }

    @FXML private void onSave() {
        lblError.setText("");
        String ma  = tfMaNganh.getText().trim();
        String ten = tfTenNganh.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) {
            lblError.setText("Mã ngành và tên ngành không được để trống."); return;
        }
        BigDecimal diemSan, diemTT;
        int chiTieu;
        try {
            diemSan = new BigDecimal(tfDiemSan.getText().trim().isEmpty() ? "0" : tfDiemSan.getText().trim());
            diemTT  = new BigDecimal(tfDiemTrungTuyen.getText().trim().isEmpty() ? "0" : tfDiemTrungTuyen.getText().trim());
            chiTieu = Integer.parseInt(tfChiTieu.getText().trim().isEmpty() ? "0" : tfChiTieu.getText().trim());
        } catch (NumberFormatException ex) {
            lblError.setText("Điểm và chỉ tiêu phải là số."); return;
        }

        NganhDTO dto = new NganhDTO(
                editingRow != null ? editingRow.getId() : 0,
                ma, ten, tfToHopGoc.getText().trim(), chiTieu, diemSan, diemTT,
                cbTuyenThang.isSelected() ? "1" : "0",
                cbDgnl.isSelected()       ? "1" : "0",
                cbThpt.isSelected()       ? "1" : "0",
                cbVsat.isSelected()       ? "1" : "0",
                0, 0, 0, ""
        );

        String result = editingRow == null
                ? nganhBUS.addBangQuyDoi(dto)
                : nganhBUS.updateNganh(editingRow.getId(), dto);

        if (result.startsWith("Lỗi")) { lblError.setText(result); return; }
        onReset();
        loadData();
    }

    @FXML private void onReset() {
        editingRow = null;
        lblFormTitle.setText("Thêm ngành mới"); lblError.setText("");
        tfMaNganh.clear(); tfMaNganh.setDisable(false);
        tfTenNganh.clear(); tfToHopGoc.clear();
        tfChiTieu.clear(); tfDiemSan.clear(); tfDiemTrungTuyen.clear();
        cbTuyenThang.setSelected(false); cbDgnl.setSelected(false);
        cbThpt.setSelected(false); cbVsat.setSelected(false);
    }

    private void onDeleteRow(NganhRow row) {
        if (!confirmDelete(row.getTenNganh())) return;
        String result = nganhBUS.deleteNganh(row.getId());
        if (result.startsWith("Lỗi")) {
            showError(result);
        } else {
            loadData();
        }
    }

    // ── Import CSV ────────────────────────────────────────────────────────────
    @FXML private void onImportCSV() {
        lblImportStatus.setText("");
        lblImportStatus.setStyle("-fx-text-fill: #2e7d32;");

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file CSV danh sách ngành");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(tblNganh.getScene().getWindow());
        if (file == null) return; // người dùng hủy

        try {
             String result = nganhBUS.importCsvData(file);
             if (result.startsWith("Lỗi")) {
                 lblImportStatus.setStyle("-fx-text-fill: #c62828;");
                 lblImportStatus.setText(result);
             } else {
                 lblImportStatus.setText(result);
                 loadData();
             }
        } catch (Exception ex) {
            lblImportStatus.setStyle("-fx-text-fill: #c62828;");
            lblImportStatus.setText("Lỗi đọc file: " + ex.getMessage());
        }
    }

    public ObservableList<NganhRow> getAllData() { return allData; }

    // ── Row ───────────────────────────────────────────
    public static class NganhRow {
        private int id;
        private String maNganh, tenNganh, toHopGoc, chiTieu, diemSan, diemTrungTuyen;
        private boolean tuyenThang, dgnl, thpt, vsat;
        private long soLuongDangKy;

        public NganhRow(int id, String maNganh, String tenNganh, String toHopGoc,
                        String chiTieu, String diemSan, String diemTrungTuyen,
                        boolean tuyenThang, boolean dgnl, boolean thpt, boolean vsat,
                        long soLuongDangKy) {
            this.id=id; this.maNganh=maNganh; this.tenNganh=tenNganh;
            this.toHopGoc=toHopGoc; this.chiTieu=chiTieu;
            this.diemSan=diemSan; this.diemTrungTuyen=diemTrungTuyen;
            this.tuyenThang=tuyenThang; this.dgnl=dgnl; this.thpt=thpt; this.vsat=vsat;
            this.soLuongDangKy=soLuongDangKy;
        }

        public int    getId()             { return id; }
        public String getMaNganh()        { return maNganh; }
        public String getTenNganh()       { return tenNganh; }
        public String getToHopGoc()       { return toHopGoc; }
        public String getChiTieu()        { return chiTieu; }
        public String getDiemSan()        { return diemSan; }
        public String getDiemTrungTuyen() { return diemTrungTuyen; }
        public boolean isTuyenThang()     { return tuyenThang; }
        public boolean isDgnl()           { return dgnl; }
        public boolean isThpt()           { return thpt; }
        public boolean isVsat()           { return vsat; }
        public long getSoLuongDangKy()    { return soLuongDangKy; }

        public String getPhuongThuc() {
            List<String> methods = new ArrayList<>();
            if (tuyenThang) methods.add("Tuyển thẳng");
            if (dgnl)       methods.add("ĐGNL");
            if (thpt)       methods.add("THPT");
            if (vsat)       methods.add("V-SAT");
            return methods.isEmpty() ? "—" : String.join(", ", methods);
        }

        public void setTenNganh(String v)       { tenNganh=v; }
        public void setToHopGoc(String v)       { toHopGoc=v; }
        public void setChiTieu(String v)        { chiTieu=v; }
        public void setDiemSan(String v)        { diemSan=v; }
        public void setDiemTrungTuyen(String v) { diemTrungTuyen=v; }
    }
}