package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class DiemCongXetTuyenController extends BaseController implements Initializable {

    private final DiemCongXetTuyenBUS bus = new DiemCongXetTuyenBUS();
    private final ObservableList<DiemCongXetTuyenDTO> masterData = FXCollections.observableArrayList();
    private PauseTransition searchDebounce;

    @FXML private TextField tfSearch;
    @FXML private Label lblCount;
    @FXML private TableView<DiemCongXetTuyenDTO> tblDiemCong;

    @FXML private TableColumn<DiemCongXetTuyenDTO, Integer>    colId;
    @FXML private TableColumn<DiemCongXetTuyenDTO, String>     colCccd, colMon, colPhuongThuc, colGhiChu;
    @FXML private TableColumn<DiemCongXetTuyenDTO, BigDecimal> colDiemUtxtToHop, colDiemUtxtKhongToHop,
            colDiemCc, colTongThxt, colTongKhongThxt;
    @FXML private TableColumn<DiemCongXetTuyenDTO, Void>       colAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchDebounce = new PauseTransition(Duration.millis(300));
        searchDebounce.setOnFinished(e -> loadData());
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDiemCong"));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("tsCccd"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("mon"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colDiemUtxtToHop.setCellValueFactory(new PropertyValueFactory<>("diemUtxtToHop"));
        colDiemUtxtKhongToHop.setCellValueFactory(new PropertyValueFactory<>("diemUtxtKhongXetToHop"));
        colDiemCc.setCellValueFactory(new PropertyValueFactory<>("diemCc"));
        colTongThxt.setCellValueFactory(new PropertyValueFactory<>("diemTongThxt"));
        colTongKhongThxt.setCellValueFactory(new PropertyValueFactory<>("diemTongKhongXetThxt"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> {
                        DiemCongXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) openDialog(item);
                    },
                    () -> {
                        DiemCongXetTuyenDTO item = getTableRow().getItem();
                        if (item != null) onDelete(item);
                    }
            );

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        tblDiemCong.setItems(masterData);
    }

    private void loadData() {
        String key = tfSearch.getText().trim();
        if (key.isEmpty()) {
            masterData.setAll(bus.getAllDiemCongXetTuyen());
        } else {
            masterData.setAll(
                    bus.getAllDiemCongXetTuyen().stream()
                            .filter(d -> d.getTsCccd().contains(key) ||
                                    (d.getMon() != null && d.getMon().contains(key)) ||
                                    (d.getPhuongThuc() != null && d.getPhuongThuc().contains(key)))
                            .toList()
            );
        }
        lblCount.setText(masterData.size() + " bản ghi");
    }

    @FXML private void onAdd() { openDialog(null); }

    @FXML
    private void onImportUtxt() {
        handleImport("UTXT");
    }

    @FXML
    private void onImportCc() {
        handleImport("CC");
    }

    private void handleImport(String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file CSV " + type);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String msg = type.equals("UTXT")
                    ? bus.importUtxtCsvData(selectedFile)
                    : bus.importCcCsvData(selectedFile);
            showInfo("Kết quả Import", msg);
            loadData();
        }
    }

    private void onDelete(DiemCongXetTuyenDTO row) {
        if (confirmDelete("Điểm cộng của thí sinh: " + row.getTsCccd())) {
            String result = bus.deleteDiemCongXetTuyen(row.getIdDiemCong());
            if (result.contains("successfully")) {
                loadData();
                showInfo("Thành công", "Đã xóa bản ghi.");
            } else {
                showError(result);
            }
        }
    }

    private void openDialog(DiemCongXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/DiemCongXetTuyenDialogUI.fxml"
                    )
            );
            Parent root = loader.load();
            DiemCongXetTuyenDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm cộng" : "Sửa điểm cộng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            dialogCtrl.init(stage, row, bus);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) loadData();

        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}