package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.service.BangQuyDoiBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.Normalizer;
import java.util.List;
import java.util.ResourceBundle;

public class BangQuyDoiController extends BaseController implements Initializable {

    private final BangQuyDoiBUS bangQuyDoiBUS = new BangQuyDoiBUS();

    private final ObservableList<BangQuyDoiDTO> allData =
            FXCollections.observableArrayList();

    private FilteredList<BangQuyDoiDTO> filteredData;

    @FXML private TextField tfSearch;
    @FXML private TableView<BangQuyDoiDTO> tblBangQuyDoi;
    @FXML private TableColumn<BangQuyDoiDTO, Integer> colId;
    @FXML private TableColumn<BangQuyDoiDTO, String> colPhuongThuc, colToHop, colMon;
    @FXML private TableColumn<BangQuyDoiDTO, BigDecimal> colDiemA, colDiemB, colDiemC, colDiemD;
    @FXML private Label lblCount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadData();

        filteredData = new FilteredList<>(allData, b -> true);

        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = normalize(newVal);
            filteredData.setPredicate(item -> {
                if (keyword.isEmpty()) return true;
                return String.valueOf(item.getIdqd()).contains(keyword)
                        || contains(item.getPhuongThuc(), keyword)
                        || contains(item.getToHop(), keyword)
                        || contains(item.getMon(), keyword);
            });
        });

        SortedList<BangQuyDoiDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblBangQuyDoi.comparatorProperty());
        tblBangQuyDoi.setItems(sortedData);
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
    }

    private void loadData() {
        allData.setAll(bangQuyDoiBUS.getAllBangQuyDoi(""));
        lblCount.setText(allData.size() + " bản ghi");
    }

    private boolean contains(String source, String keyword) {
        return normalize(source).contains(keyword);
    }

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    @FXML
    private void onAdd() {
        openDialog(null);
    }

    @FXML
    private void onEdit() {
        BangQuyDoiDTO selected = tblBangQuyDoi.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn dòng cần sửa.");
            return;
        }
        openDialog(selected);
    }

    @FXML
    private void onDeleteSelected() {
        BangQuyDoiDTO selected = tblBangQuyDoi.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn dòng cần xóa.");
            return;
        }

        if (confirmDelete("Luật quy đổi ID: " + selected.getIdqd())) {
            String result = bangQuyDoiBUS.deleteBangQuyDoi(selected.getIdqd());

            if (!result.startsWith("Lỗi")) {
                allData.remove(selected); // xóa trực tiếp khỏi list
                lblCount.setText(allData.size() + " bản ghi");
                showInfo("Thành công", "Đã xóa bản ghi.");
            } else {
                showError(result);
            }
        }
    }

    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file CSV quy đổi");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = chooser.showOpenDialog(tblBangQuyDoi.getScene().getWindow());
        if (file == null) return;

        String result = bangQuyDoiBUS.importCsv(file);
        showInfo("Kết quả Import", result);
        // Reload sau import
        loadData();
        lblCount.setText(allData.size() + " bản ghi");
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

            if (dialogCtrl.getIsSaved()) {
                if (row == null) {
                    // ADD: lấy 1 record mới nhất từ DB (id desc → index 0) thêm vào đầu list
                    // Chỉ query để lấy ID thật, không reload toàn bộ
                    List<BangQuyDoiDTO> fresh = bangQuyDoiBUS.getAllBangQuyDoi("");
                    if (!fresh.isEmpty()) {
                        BangQuyDoiDTO newRecord = fresh.get(0); // mới nhất do sắp xếp DESC
                        allData.add(0, newRecord);
                    }
                } else {
                    // EDIT: editingRow đã được sửa trực tiếp (cùng reference trong allData)
                    // Chỉ cần refresh lại UI
                    tblBangQuyDoi.refresh();
                }
                lblCount.setText(allData.size() + " bản ghi");
            }

        } catch (IOException e) {
            showError("Lỗi giao diện: " + e.getMessage());
        }
    }
}