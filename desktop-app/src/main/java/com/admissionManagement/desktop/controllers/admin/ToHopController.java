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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ToHopController extends BaseController implements Initializable {

    @FXML private Label     lblFormTitle, lblError, lblCount;
    @FXML private TextField tfMaToHop, tfTenToHop, tfMonHoc;
    @FXML private TableView<ToHopRow>           tblToHop;
    @FXML private TableColumn<ToHopRow,String>  colMa, colTen, colMon;
    @FXML private TableColumn<ToHopRow,Void>    colAction;

    private final ObservableList<ToHopRow> allData = FXCollections.observableArrayList();
    private ToHopRow editingRow;

    @Override public void initialize(URL u, ResourceBundle r) {
        setupTable(); loadData();
    }

    private void setupTable() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maToHop"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenToHop"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("monHoc"));
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
            new ToHopRow("A00","Khối A","Toán, Vật lý, Hóa học"),
            new ToHopRow("A01","Khối A1","Toán, Vật lý, Tiếng Anh"),
            new ToHopRow("B00","Khối B","Toán, Hóa học, Sinh học"),
            new ToHopRow("D01","Khối D1","Toán, Ngữ văn, Tiếng Anh")
        );
        refresh();
    }

    private void refresh() {
        tblToHop.setItems(FXCollections.observableArrayList(allData));
        lblCount.setText(allData.size() + " tổ hợp");
    }

    @FXML private void onTableClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            ToHopRow row = tblToHop.getSelectionModel().getSelectedItem();
            if (row != null) loadToForm(row);
        }
    }

    private void loadToForm(ToHopRow row) {
        editingRow = row;
        lblFormTitle.setText("Sửa tổ hợp: " + row.getMaToHop());
        tfMaToHop.setText(row.getMaToHop()); tfMaToHop.setDisable(true);
        tfTenToHop.setText(row.getTenToHop());
        tfMonHoc.setText(row.getMonHoc());
    }

    @FXML private void onSave() {
        if (tfMaToHop.getText().trim().isEmpty()) { lblError.setText("Mã tổ hợp không được để trống."); return; }
        if (editingRow == null) {
            allData.add(new ToHopRow(tfMaToHop.getText().trim(), tfTenToHop.getText().trim(), tfMonHoc.getText().trim()));
            // TODO: toHopService.create(...)
        } else {
            editingRow.setTenToHop(tfTenToHop.getText().trim());
            editingRow.setMonHoc(tfMonHoc.getText().trim());
            // TODO: toHopService.update(...)
        }
        onReset(); refresh();
    }

    @FXML private void onReset() {
        editingRow = null; lblFormTitle.setText("Thêm tổ hợp mới"); lblError.setText("");
        tfMaToHop.clear(); tfMaToHop.setDisable(false); tfTenToHop.clear(); tfMonHoc.clear();
    }

    @FXML private void onImport() { showInfo("Import", "Import CSV sau khi kết nối BE."); }

    private void onDeleteRow(ToHopRow row) {
        if (confirmDelete(row.getTenToHop())) { allData.remove(row); refresh(); }
    }

    // expose data for NganhToHopController
    public ObservableList<ToHopRow> getAllData() { return allData; }

    public static class ToHopRow {
        private String maToHop, tenToHop, monHoc;
        public ToHopRow(String ma, String ten, String mon) { maToHop=ma; tenToHop=ten; monHoc=mon; }
        public String getMaToHop()  { return maToHop; }
        public String getTenToHop() { return tenToHop; }
        public String getMonHoc()   { return monHoc; }
        public void setTenToHop(String v) { tenToHop=v; }
        public void setMonHoc(String v)   { monHoc=v; }
    }
}
