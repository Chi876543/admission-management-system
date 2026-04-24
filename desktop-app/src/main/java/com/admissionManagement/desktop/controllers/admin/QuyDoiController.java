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
//  QuyDoiController
// ═══════════════════════════════════════════════════════════════
public class QuyDoiController extends BaseController implements Initializable {

    @FXML private Label     lblFormTitle, lblError, lblCount;
    @FXML private TextField tfTu, tfDen, tfQuyDoi, tfSearch;
    @FXML private ComboBox<String> cbLoai, cbFilter;
    @FXML private TableView<QuyDoiRow>           tblQuyDoi;
    @FXML private TableColumn<QuyDoiRow,String>  colLoai, colTu, colDen, colQuyDoi;
    @FXML private TableColumn<QuyDoiRow,Void>    colAction;

    private final ObservableList<QuyDoiRow> allData = FXCollections.observableArrayList();
    private QuyDoiRow editingRow;

    @Override public void initialize(URL u, ResourceBundle r) {
        List<String> loai = List.of("VSAT (1000)","ĐGNL (1200)");
        cbLoai.setItems(FXCollections.observableArrayList(loai));
        cbFilter.setItems(FXCollections.observableArrayList(loai));
        cbFilter.setOnAction(e -> refresh());

        colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));
        colTu.setCellValueFactory(new PropertyValueFactory<>("tu"));
        colDen.setCellValueFactory(new PropertyValueFactory<>("den"));
        colQuyDoi.setCellValueFactory(new PropertyValueFactory<>("quyDoi"));
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
                new QuyDoiRow("VSAT (1000)","900","1000","28.0 - 30.0"),
                new QuyDoiRow("VSAT (1000)","800","899","24.0 - 27.9"),
                new QuyDoiRow("ĐGNL (1200)","1000","1200","26.0 - 30.0"),
                new QuyDoiRow("ĐGNL (1200)","800","999","20.0 - 25.9")
        );
        refresh();
    }

    private void refresh() {
        String kw  = tfSearch != null ? tfSearch.getText().trim() : "";
        String fil = cbFilter != null ? cbFilter.getValue() : null;
        List<QuyDoiRow> f = allData.stream()
                .filter(row -> kw.isEmpty() || row.getTu().contains(kw) || row.getDen().contains(kw))
                .filter(row -> fil == null || row.getLoai().equals(fil))
                .collect(Collectors.toList());
        tblQuyDoi.setItems(FXCollections.observableArrayList(f));
        lblCount.setText(f.size() + " bản ghi");
    }

    @FXML private void onSearch() { refresh(); }
    @FXML private void onFilter() { refresh(); }

    @FXML private void onTableClick(javafx.scene.input.MouseEvent e) {
        if (e.getClickCount() == 2) {
            QuyDoiRow row = tblQuyDoi.getSelectionModel().getSelectedItem();
            if (row != null) loadToForm(row);
        }
    }

    private void loadToForm(QuyDoiRow row) {
        editingRow = row;
        lblFormTitle.setText("Sửa bảng quy đổi");
        cbLoai.setValue(row.getLoai());
        tfTu.setText(row.getTu()); tfDen.setText(row.getDen()); tfQuyDoi.setText(row.getQuyDoi());
    }

    @FXML private void onSave() {
        if (cbLoai.getValue()==null||tfTu.getText().trim().isEmpty()||tfDen.getText().trim().isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin."); return;
        }
        if (editingRow == null) {
            allData.add(new QuyDoiRow(cbLoai.getValue(),tfTu.getText().trim(),tfDen.getText().trim(),tfQuyDoi.getText().trim()));
        } else {
            editingRow.setLoai(cbLoai.getValue()); editingRow.setTu(tfTu.getText().trim());
            editingRow.setDen(tfDen.getText().trim()); editingRow.setQuyDoi(tfQuyDoi.getText().trim());
        }
        // TODO: quyDoiService.save(...)
        onReset(); refresh();
    }

    @FXML private void onReset() {
        editingRow = null; lblFormTitle.setText("Thêm bảng quy đổi"); lblError.setText("");
        cbLoai.setValue(null); tfTu.clear(); tfDen.clear(); tfQuyDoi.clear();
    }

    @FXML private void onImport() { showInfo("Import","Import CSV sau khi kết nối BE."); }

    private void onDeleteRow(QuyDoiRow row) {
        if (confirmDelete(row.getLoai()+" ["+row.getTu()+"-"+row.getDen()+"]")) { allData.remove(row); refresh(); }
    }

    public static class QuyDoiRow {
        private String loai, tu, den, quyDoi;
        public QuyDoiRow(String loai,String tu,String den,String qd){this.loai=loai;this.tu=tu;this.den=den;this.quyDoi=qd;}
        public String getLoai()   {return loai;}
        public String getTu()     {return tu;}
        public String getDen()    {return den;}
        public String getQuyDoi() {return quyDoi;}
        public void setLoai(String v)   {loai=v;}
        public void setTu(String v)     {tu=v;}
        public void setDen(String v)    {den=v;}
        public void setQuyDoi(String v) {quyDoi=v;}
    }
}
