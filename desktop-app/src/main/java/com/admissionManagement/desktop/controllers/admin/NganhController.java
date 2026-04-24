package com.admissionManagement.desktop.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════════════════
//  NganhController
// ═══════════════════════════════════════════════════════════════
class NganhController extends BaseController implements Initializable {

    @FXML private Label    lblFormTitle, lblError, lblCount;
    @FXML private TextField tfMaNganh, tfTenNganh, tfChiTieu, tfKhoa, tfSearch;
    @FXML private TableView<NganhRow>           tblNganh;
    @FXML private TableColumn<NganhRow,String>  colMa, colTen, colKhoa, colChiTieu, colDaDangKy;
    @FXML private TableColumn<NganhRow,Void>    colAction;

    private final ObservableList<NganhRow> allData = FXCollections.observableArrayList();
    private NganhRow editingRow;

    @Override public void initialize(URL u, ResourceBundle r) {
        setupTable(); loadData();
    }

    private void setupTable() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNganh"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenNganh"));
        colKhoa.setCellValueFactory(new PropertyValueFactory<>("khoa"));
        colChiTieu.setCellValueFactory(new PropertyValueFactory<>("chiTieu"));
        colDaDangKy.setCellValueFactory(new PropertyValueFactory<>("daDangKy"));
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

    private void loadData() {
        allData.setAll(
            new NganhRow("7480201","Công nghệ thông tin","Khoa CNTT","120","87"),
            new NganhRow("7340301","Kế toán","Khoa Kinh tế","80","62"),
            new NganhRow("7510301","Kỹ thuật điện tử","Khoa Điện - Điện tử","100","45")
        );
        refresh();
    }

    private void refresh() {
        String kw = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        List<NganhRow> f = allData.stream()
            .filter(r -> kw.isEmpty() || r.getMaNganh().contains(kw) || r.getTenNganh().toLowerCase().contains(kw))
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
        tfKhoa.setText(row.getKhoa());
        tfChiTieu.setText(row.getChiTieu());
    }

    @FXML private void onSave() {
        if (tfMaNganh.getText().trim().isEmpty() || tfTenNganh.getText().trim().isEmpty()) {
            lblError.setText("Mã ngành và tên ngành không được để trống."); return;
        }
        if (editingRow == null) {
            allData.add(new NganhRow(tfMaNganh.getText().trim(), tfTenNganh.getText().trim(),
                    tfKhoa.getText().trim(), tfChiTieu.getText().trim(), "0"));
            // TODO: nganhService.create(...)
        } else {
            editingRow.setTenNganh(tfTenNganh.getText().trim());
            editingRow.setKhoa(tfKhoa.getText().trim());
            editingRow.setChiTieu(tfChiTieu.getText().trim());
            // TODO: nganhService.update(...)
        }
        onReset(); refresh();
    }

    @FXML private void onReset() {
        editingRow = null; lblFormTitle.setText("Thêm ngành mới"); lblError.setText("");
        tfMaNganh.clear(); tfMaNganh.setDisable(false);
        tfTenNganh.clear(); tfKhoa.clear(); tfChiTieu.clear();
    }

    @FXML private void onImport() { showInfo("Import", "Import CSV sau khi kết nối BE."); }

    private void onDeleteRow(NganhRow row) {
        if (confirmDelete(row.getTenNganh())) {
            allData.remove(row);
            // TODO: nganhService.delete(row.getMaNganh())
            refresh();
        }
    }

    public static class NganhRow {
        private String maNganh, tenNganh, khoa, chiTieu, daDangKy;
        public NganhRow(String ma, String ten, String khoa, String chiTieu, String da) {
            this.maNganh=ma; this.tenNganh=ten; this.khoa=khoa; this.chiTieu=chiTieu; this.daDangKy=da;
        }
        public String getMaNganh()   { return maNganh; }
        public String getTenNganh()  { return tenNganh; }
        public String getKhoa()      { return khoa; }
        public String getChiTieu()   { return chiTieu; }
        public String getDaDangKy()  { return daDangKy; }
        public void setTenNganh(String v)  { tenNganh=v; }
        public void setKhoa(String v)      { khoa=v; }
        public void setChiTieu(String v)   { chiTieu=v; }
    }
}
