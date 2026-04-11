package com.admissionManagement.desktop.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════════════════
//  DiemCongController
// ═══════════════════════════════════════════════════════════════
public class DiemCongController extends BaseController implements Initializable {

    @FXML private Label     lblFormTitle, lblError, lblCount;
    @FXML private TextField tfCccd, tfDiemCong, tfGhiChu, tfSearch;
    @FXML private ComboBox<String> cbLoaiUuTien, cbFilter;
    @FXML private TableView<DiemCongRow>           tblDiemCong;
    @FXML private TableColumn<DiemCongRow,String>  colCccd, colHoTen, colLoai, colDiemCong, colGhiChu;
    @FXML private TableColumn<DiemCongRow,Void>    colAction;

    private final ObservableList<DiemCongRow> allData = FXCollections.observableArrayList();
    private DiemCongRow editingRow;

    @Override public void initialize(URL u, ResourceBundle r) {
        List<String> loai = List.of("KV1 - Khu vực 1","KV2 - Khu vực 2","KV2-NT - Khu vực 2NT",
                "KV3 - Khu vực 3","ĐT1 - Đối tượng 1","ĐT2 - Đối tượng 2");
        cbLoaiUuTien.setItems(FXCollections.observableArrayList(loai));
        cbFilter.setItems(FXCollections.observableArrayList(loai));
        cbFilter.setOnAction(e -> refresh());

        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));
        colDiemCong.setCellValueFactory(new PropertyValueFactory<>("diemCong"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));
        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> loadToForm(getTableView().getItems().get(getIndex())),
                    () -> onDeleteRow(getTableView().getItems().get(getIndex()))
            );
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : box);
            }
        });

        allData.setAll(
                new DiemCongRow("001234567890","Nguyễn Văn An","KV1 - Khu vực 1","0.75","Khu vực 1"),
                new DiemCongRow("001234567891","Trần Thị Bình","ĐT1 - Đối tượng 1","2.0","Đối tượng ưu tiên 1"),
                new DiemCongRow("001234567892","Lê Minh Châu","KV2 - Khu vực 2","0.25","Khu vực 2")
        );
        refresh();
    }

    private void refresh() {
        String kw  = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        String fil = cbFilter != null ? cbFilter.getValue() : null;
        List<DiemCongRow> f = allData.stream()
                .filter(r -> kw.isEmpty() || r.getCccd().contains(kw) || r.getHoTen().toLowerCase().contains(kw))
                .filter(r -> fil == null || r.getLoai().equals(fil))
                .collect(Collectors.toList());
        tblDiemCong.setItems(FXCollections.observableArrayList(f));
        lblCount.setText(f.size() + " bản ghi");
    }

    @FXML private void onSearch() { refresh(); }
    @FXML private void onFilter() { refresh(); }

    @FXML private void onTableClick(javafx.scene.input.MouseEvent e) {
        if (e.getClickCount() == 2) {
            DiemCongRow row = tblDiemCong.getSelectionModel().getSelectedItem();
            if (row != null) loadToForm(row);
        }
    }

    private void loadToForm(DiemCongRow row) {
        editingRow = row;
        lblFormTitle.setText("Sửa điểm cộng");
        tfCccd.setText(row.getCccd()); tfCccd.setDisable(true);
        cbLoaiUuTien.setValue(row.getLoai());
        tfDiemCong.setText(row.getDiemCong());
        tfGhiChu.setText(row.getGhiChu());
    }

    @FXML private void onSave() {
        if (tfCccd.getText().trim().isEmpty()) { lblError.setText("CCCD không được để trống."); return; }
        if (editingRow == null) {
            allData.add(new DiemCongRow(tfCccd.getText().trim(), "", cbLoaiUuTien.getValue(),
                    tfDiemCong.getText().trim(), tfGhiChu.getText().trim()));
        } else {
            editingRow.setLoai(cbLoaiUuTien.getValue());
            editingRow.setDiemCong(tfDiemCong.getText().trim());
            editingRow.setGhiChu(tfGhiChu.getText().trim());
        }
        // TODO: diemCongService.save(...)
        onReset(); refresh();
    }

    @FXML private void onReset() {
        editingRow = null; lblFormTitle.setText("Thêm điểm cộng"); lblError.setText("");
        tfCccd.clear(); tfCccd.setDisable(false);
        cbLoaiUuTien.setValue(null); tfDiemCong.clear(); tfGhiChu.clear();
    }

    @FXML private void onImport() { showInfo("Import", "Import CSV sau khi kết nối BE."); }

    private void onDeleteRow(DiemCongRow row) {
        if (confirmDelete(row.getCccd())) { allData.remove(row); refresh(); }
    }

    public static class DiemCongRow {
        private String cccd, hoTen, loai, diemCong, ghiChu;
        public DiemCongRow(String cccd, String hoTen, String loai, String diem, String ghi) {
            this.cccd=cccd; this.hoTen=hoTen; this.loai=loai; this.diemCong=diem; this.ghiChu=ghi;
        }
        public String getCccd()     { return cccd; }
        public String getHoTen()    { return hoTen; }
        public String getLoai()     { return loai; }
        public String getDiemCong() { return diemCong; }
        public String getGhiChu()   { return ghiChu; }
        public void setLoai(String v)     { loai=v; }
        public void setDiemCong(String v) { diemCong=v; }
        public void setGhiChu(String v)   { ghiChu=v; }
    }
}
