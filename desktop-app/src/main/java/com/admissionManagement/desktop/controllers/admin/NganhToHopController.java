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
//  NganhToHopController
// ═══════════════════════════════════════════════════════════════
public class NganhToHopController extends BaseController implements Initializable {

    @FXML private TableView<NganhController.NganhRow>      tblNganh;
    @FXML private TableColumn<NganhController.NganhRow,String> colMaNganh, colTenNganh;

    @FXML private TableView<ToHopController.ToHopRow>      tblToHopCuaNganh;
    @FXML private TableColumn<ToHopController.ToHopRow,String> colMaTH, colTenTH, colMon;
    @FXML private TableColumn<ToHopController.ToHopRow,Void>   colXoa;

    @FXML private Label       lblSelectedNganh;
    @FXML private ComboBox<String> cbToHopGan;

    // dữ liệu ngành (dùng chung, trong thực tế lấy từ service)
    private final ObservableList<NganhController.NganhRow> danhSachNganh =
            FXCollections.observableArrayList(
                    new NganhController.NganhRow("7480201","Công nghệ thông tin","Khoa CNTT","120","87"),
                    new NganhController.NganhRow("7340301","Kế toán","Khoa Kinh tế","80","62"),
                    new NganhController.NganhRow("7510301","Kỹ thuật điện tử","Khoa Điện - Điện tử","100","45")
            );

    // map ngành -> list tổ hợp đã gán
    private final Map<String, ObservableList<ToHopController.ToHopRow>> nganhToHopMap = new HashMap<>();

    private final List<ToHopController.ToHopRow> allToHop = List.of(
            new ToHopController.ToHopRow("A00","Khối A","Toán, Vật lý, Hóa học"),
            new ToHopController.ToHopRow("A01","Khối A1","Toán, Vật lý, Tiếng Anh"),
            new ToHopController.ToHopRow("B00","Khối B","Toán, Hóa học, Sinh học"),
            new ToHopController.ToHopRow("D01","Khối D1","Toán, Ngữ văn, Tiếng Anh")
    );

    private NganhController.NganhRow selectedNganh;

    @Override public void initialize(URL u, ResourceBundle r) {
        colMaNganh.setCellValueFactory(new PropertyValueFactory<>("maNganh"));
        colTenNganh.setCellValueFactory(new PropertyValueFactory<>("tenNganh"));
        tblNganh.setItems(danhSachNganh);

        colMaTH.setCellValueFactory(new PropertyValueFactory<>("maToHop"));
        colTenTH.setCellValueFactory(new PropertyValueFactory<>("tenToHop"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("monHoc"));
        colXoa.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✕");
            { btn.getStyleClass().addAll("btn-danger","btn-sm");
                btn.setOnAction(e -> {
                    ToHopController.ToHopRow th = getTableView().getItems().get(getIndex());
                    if (selectedNganh != null) {
                        nganhToHopMap.get(selectedNganh.getMaNganh()).remove(th);
                        // TODO: service.removeToHop(selectedNganh.getMaNganh(), th.getMaToHop())
                    }
                }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty); setGraphic(empty ? null : btn);
            }
        });

        cbToHopGan.setItems(FXCollections.observableArrayList(
                allToHop.stream().map(ToHopController.ToHopRow::getMaToHop).collect(Collectors.toList())
        ));
    }

    @FXML private void onNganhClick(MouseEvent e) {
        NganhController.NganhRow row = tblNganh.getSelectionModel().getSelectedItem();
        if (row == null) return;
        selectedNganh = row;
        lblSelectedNganh.setText("Tổ hợp của ngành: " + row.getTenNganh());
        nganhToHopMap.putIfAbsent(row.getMaNganh(), FXCollections.observableArrayList());
        tblToHopCuaNganh.setItems(nganhToHopMap.get(row.getMaNganh()));
    }

    @FXML private void onGanToHop() {
        if (selectedNganh == null) { showError("Vui lòng chọn ngành trước."); return; }
        String ma = cbToHopGan.getValue();
        if (ma == null) { showError("Vui lòng chọn tổ hợp."); return; }
        ObservableList<ToHopController.ToHopRow> list = nganhToHopMap.get(selectedNganh.getMaNganh());
        boolean exists = list.stream().anyMatch(t -> t.getMaToHop().equals(ma));
        if (exists) { showError("Tổ hợp này đã được gán."); return; }
        allToHop.stream().filter(t -> t.getMaToHop().equals(ma)).findFirst().ifPresent(t -> {
            list.add(t);
            // TODO: service.addToHop(selectedNganh.getMaNganh(), ma)
        });
    }

    @FXML private void onImport() { showInfo("Import", "Import CSV sau khi kết nối BE."); }
}
