package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.service.DiemCongXetTuyenBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
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
import java.util.ResourceBundle;

public class DiemCongXetTuyenController extends BaseController implements Initializable {

    private static final int PAGE_SIZE = 20;

    private final DiemCongXetTuyenBUS bus =
            new DiemCongXetTuyenBUS();

    private final ObservableList<DiemCongXetTuyenDTO> allData =
            FXCollections.observableArrayList();

    private final ObservableList<DiemCongXetTuyenDTO> currentPageData =
            FXCollections.observableArrayList();

    private FilteredList<DiemCongXetTuyenDTO> filteredData;

    @FXML private TextField tfSearch;

    @FXML private Label lblCount;

    @FXML private Pagination pagination;

    @FXML
    private TableView<DiemCongXetTuyenDTO> tblDiemCong;

    @FXML
    private TableColumn<DiemCongXetTuyenDTO, Integer> colId;

    @FXML
    private TableColumn<DiemCongXetTuyenDTO, String>
            colCccd,
            colMon,
            colPhuongThuc,
            colGhiChu;

    @FXML
    private TableColumn<DiemCongXetTuyenDTO, BigDecimal>
            colDiemUtxtToHop,
            colDiemUtxtKhongToHop,
            colDiemCc,
            colTongThxt,
            colTongKhongThxt;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupTable();

        loadData();

        filteredData = new FilteredList<>(allData, b -> true);

        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {

            String keyword = normalize(newVal);

            filteredData.setPredicate(item -> {

                if (keyword.isEmpty()) {
                    return true;
                }

                return
                        contains(item.getTsCccd(), keyword)

                                || contains(item.getMon(), keyword)

                                || contains(item.getPhuongThuc(), keyword)

                                || contains(item.getGhiChu(), keyword);
            });

            updatePagination();
        });

        SortedList<DiemCongXetTuyenDTO> sortedData =
                new SortedList<>(filteredData);

        sortedData.comparatorProperty()
                .bind(tblDiemCong.comparatorProperty());

        pagination.currentPageIndexProperty().addListener(
                (obs, oldVal, newVal) ->
                        changePage(newVal.intValue())
        );

        updatePagination();
    }

    private void setupTable() {

        tblDiemCong.setItems(currentPageData);

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idDiemCong"));

        colCccd.setCellValueFactory(
                new PropertyValueFactory<>("tsCccd"));

        colMon.setCellValueFactory(
                new PropertyValueFactory<>("mon"));

        colPhuongThuc.setCellValueFactory(
                new PropertyValueFactory<>("phuongThuc"));

        colDiemUtxtToHop.setCellValueFactory(
                new PropertyValueFactory<>("diemUtxtToHop"));

        colDiemUtxtKhongToHop.setCellValueFactory(
                new PropertyValueFactory<>("diemUtxtKhongXetToHop"));

        colDiemCc.setCellValueFactory(
                new PropertyValueFactory<>("diemCc"));

        colTongThxt.setCellValueFactory(
                new PropertyValueFactory<>("diemTongThxt"));

        colTongKhongThxt.setCellValueFactory(
                new PropertyValueFactory<>("diemTongKhongXetThxt"));

        colGhiChu.setCellValueFactory(
                new PropertyValueFactory<>("ghiChu"));
    }

    private void loadData() {

        Task<ObservableList<DiemCongXetTuyenDTO>> task =
                new Task<>() {

                    @Override
                    protected ObservableList<DiemCongXetTuyenDTO> call() {

                        return FXCollections.observableArrayList(
                                bus.getAllDiemCongXetTuyen()
                        );
                    }
                };

        task.setOnSucceeded(e -> {

            allData.setAll(task.getValue());

            updatePagination();
        });

        task.setOnFailed(e -> {

            showError(
                    task.getException().getMessage()
            );
        });

        new Thread(task).start();
    }

    private void updatePagination() {

        int totalPages = (int) Math.ceil(
                (double) filteredData.size() / PAGE_SIZE
        );

        totalPages = Math.max(totalPages, 1);

        pagination.setPageCount(totalPages);

        changePage(0);

        lblCount.setText(
                filteredData.size() + " bản ghi"
        );
    }

    private void changePage(int pageIndex) {

        int fromIndex = pageIndex * PAGE_SIZE;

        int toIndex = Math.min(
                fromIndex + PAGE_SIZE,
                filteredData.size()
        );

        if (fromIndex > toIndex) {
            return;
        }

        currentPageData.setAll(
                filteredData.subList(fromIndex, toIndex)
        );
    }

    private boolean contains(String source, String keyword) {

        return normalize(source).contains(keyword);
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        String normalized =
                Normalizer.normalize(text, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    @FXML
    private void onAdd() {

        openDialog(null);
    }

    @FXML
    private void onEdit() {

        DiemCongXetTuyenDTO selected =
                tblDiemCong.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showError("Vui lòng chọn dòng cần sửa.");
            return;
        }

        openDialog(selected);
    }

    @FXML
    private void onDelete() {

        DiemCongXetTuyenDTO selected =
                tblDiemCong.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showError("Vui lòng chọn dòng cần xóa.");
            return;
        }

        if (confirmDelete(
                "Điểm cộng của thí sinh: "
                        + selected.getTsCccd()
        )) {

            String result =
                    bus.deleteDiemCongXetTuyen(
                            selected.getIdDiemCong()
                    );

            if (result.contains("successfully")) {

                allData.remove(selected);

                updatePagination();

                showInfo(
                        "Thành công",
                        "Đã xóa bản ghi."
                );

            } else {

                showError(result);
            }
        }
    }

    @FXML
    private void onImportUtxt() {

        handleImport(true);
    }

    @FXML
    private void onImportCc() {

        handleImport(false);
    }

    @FXML
    private void onAddCc() {
        openCcDialog(null);
    }

    @FXML
    private void onEditCc() {
        DiemCongXetTuyenDTO selected =
                tblDiemCong.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn dòng cần sửa.");
            return;
        }
        openCcDialog(selected);
    }

    private void openCcDialog(DiemCongXetTuyenDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/DiemCcDialogUI.fxml"
                    )
            );
            Parent root = loader.load();
            DiemCcDialogController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm điểm CC" : "Sửa điểm CC");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            dialogCtrl.init(stage, row, bus);
            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                DiemCongXetTuyenDTO savedData = dialogCtrl.getSavedData();
                if (row == null) {
                    allData.add(0, savedData);
                } else {
                    int index = allData.indexOf(row);
                    if (index >= 0) allData.set(index, savedData);
                }
                updatePagination();
            }
        } catch (IOException e) {
            showError("Lỗi giao diện CC: " + e.getMessage());
        }
    }

    private void handleImport(boolean isUtxt) {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(
                isUtxt
                        ? "Import UTXT"
                        : "Import CC"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV Files",
                        "*.csv"
                )
        );

        File file =
                fileChooser.showOpenDialog(
                        tblDiemCong.getScene().getWindow()
                );

        if (file == null) {
            return;
        }

        Task<String> importTask = new Task<>() {

            @Override
            protected String call() {

                return isUtxt
                        ? bus.importUtxtCsvData(file)
                        : bus.importCcCsvData(file);
            }
        };

        importTask.setOnSucceeded(e -> {

            showInfo(
                    "Kết quả Import",
                    importTask.getValue()
            );

            loadData();
        });

        importTask.setOnFailed(e -> {

            showError(
                    importTask.getException().getMessage()
            );
        });

        new Thread(importTask).start();
    }

    private void openDialog(DiemCongXetTuyenDTO row) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/DiemCongXetTuyenDialogUI.fxml"
                    )
            );

            Parent root = loader.load();

            DiemCongXetTuyenDialogController dialogCtrl =
                    loader.getController();

            Stage stage = new Stage();

            stage.setTitle(
                    row == null
                            ? "Thêm điểm cộng"
                            : "Sửa điểm cộng"
            );

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));

            dialogCtrl.init(stage, row, bus);

            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {

                DiemCongXetTuyenDTO savedData =
                        dialogCtrl.getSavedData();

                if (row == null) {
                    // savedData đã có ID thật từ addAndReturn
                    allData.add(0, savedData);

                } else {

                    int index = allData.indexOf(row);

                    if (index >= 0) {
                        allData.set(index, savedData);
                    }
                }

                updatePagination();
            }

        } catch (IOException e) {

            showError(
                    "Lỗi giao diện: " + e.getMessage()
            );
        }
    }
}