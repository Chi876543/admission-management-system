package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class BangQuyDoiController extends BaseController implements Initializable {

    private final BangQuyDoiBUS bangQuyDoiBUS = new BangQuyDoiBUS();
    private final ObservableList<BangQuyDoiDTO> masterData = FXCollections.observableArrayList();
    private PauseTransition searchDebounce;

    @FXML private TextField tfSearch;
    @FXML private TableView<BangQuyDoiDTO> tblBangQuyDoi;
    @FXML private TableColumn<BangQuyDoiDTO, Integer>    colId;
    @FXML private TableColumn<BangQuyDoiDTO, String>     colPhuongThuc, colToHop, colMon, colMaQuyDoi, colPhanVi;
    @FXML private TableColumn<BangQuyDoiDTO, BigDecimal> colDiemA, colDiemB, colDiemC, colDiemD;
    @FXML private TableColumn<BangQuyDoiDTO, Void>       colAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchDebounce = new PauseTransition(Duration.millis(300));
        searchDebounce.setOnFinished(e -> loadData());
        tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idqd"));
        colPhuongThuc.setCellValueFactory(new PropertyValueFactory<>("phuongThuc"));
        colToHop.setCellValueFactory(new PropertyValueFactory<>("toHop"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("mon"));
        colDiemA.setCellValueFactory(new PropertyValueFactory<>("diemA"));
        colDiemB.setCellValueFactory(new PropertyValueFactory<>("diemB"));
        colDiemC.setCellValueFactory(new PropertyValueFactory<>("diemC"));
        colDiemD.setCellValueFactory(new PropertyValueFactory<>("diemD"));
        colMaQuyDoi.setCellValueFactory(new PropertyValueFactory<>("maQuyDoi"));
        colPhanVi.setCellValueFactory(new PropertyValueFactory<>("phanVi"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> {
                        BangQuyDoiDTO item = getTableRow().getItem();
                        if (item != null) openDialog(item);
                    },
                    () -> {
                        BangQuyDoiDTO item = getTableRow().getItem();
                        if (item != null) onDelete(item);
                    }
            );

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        tblBangQuyDoi.setItems(masterData);
    }

    private void loadData() {
        masterData.setAll(bangQuyDoiBUS.getAllBangQuyDoi(tfSearch.getText().trim()));
    }

    @FXML
    private void onAdd() {
        openDialog(null);
    }

    private void onDelete(BangQuyDoiDTO row) {
        if (confirmDelete("Luật quy đổi: " + row.getMaQuyDoi())) {
            String result = bangQuyDoiBUS.deleteBangQuyDoi(row.getIdqd());
            if (result.contains("successfully")) {
                loadData();
                showInfo("Thành công", "Đã xóa bản ghi.");
            } else {
                showError(result);
            }
        }
    }

    private void openDialog(BangQuyDoiDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/BangQuyDoiDialogUI.fxml"
                    )
            );
            Parent root = loader.load();
            BangQuyDoiDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm luật mới" : "Cập nhật luật");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            dialogCtrl.init(stage, row, bangQuyDoiBUS);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) loadData();

        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}