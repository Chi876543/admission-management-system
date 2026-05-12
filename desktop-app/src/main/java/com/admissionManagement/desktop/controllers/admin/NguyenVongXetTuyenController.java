package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.NguyenVongXetTuyenDTO;
import com.admissionManagement.core.service.NguyenVongXetTuyenBUS;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.Normalizer;
import java.util.ResourceBundle;

public class NguyenVongXetTuyenController extends BaseController implements Initializable {

    private final NguyenVongXetTuyenBUS bus =
            new NguyenVongXetTuyenBUS();

    private final ObservableList<NguyenVongXetTuyenDTO> masterData =
            FXCollections.observableArrayList();

    private FilteredList<NguyenVongXetTuyenDTO> filteredData;

    @FXML private TextField tfSearch;
    @FXML private Label lblCount;
    @FXML private TableView<NguyenVongXetTuyenDTO> tblNguyenVong;

    @FXML private TableColumn<NguyenVongXetTuyenDTO, Integer>
            colId,
            colThuTu;

    @FXML private TableColumn<NguyenVongXetTuyenDTO, String>
            colCccd,
            colMaNganh,
            colPhuongThuc,
            colToHop,
            colKetQua,
            colNvKeys;

    @FXML private TableColumn<NguyenVongXetTuyenDTO, BigDecimal>
            colDiemTHXT,
            colDiemUTQD,
            colDiemCong,
            colDiemXT;

    @FXML private TableColumn<NguyenVongXetTuyenDTO, Void>
            colAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupTable();

        // Load dữ liệu 1 lần
        loadData();

        // Filter realtime
        filteredData = new FilteredList<>(masterData, b -> true);

        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {

            String keyword = normalize(newVal);

            filteredData.setPredicate(item -> {

                if (keyword.isEmpty()) {
                    return true;
                }

                return

                        String.valueOf(item.getIdNv()).contains(keyword)

                                || String.valueOf(item.getThuTu()).contains(keyword)

                                || contains(item.getCccd(), keyword)

                                || contains(item.getMaNganh(), keyword)

                                || contains(item.getPhuongThuc(), keyword)

                                || contains(item.getThm(), keyword)

                                || contains(item.getKetQua(), keyword)

                                || contains(item.getNvKeys(), keyword)

                                || contains(
                                String.valueOf(item.getDiemThxt()),
                                keyword
                        )

                                || contains(
                                String.valueOf(item.getDiemUtqd()),
                                keyword
                        )

                                || contains(
                                String.valueOf(item.getDiemCong()),
                                keyword
                        )

                                || contains(
                                String.valueOf(item.getDiemXetTuyen()),
                                keyword
                        );
            });

            updateCount();
        });

        // Giữ chức năng sort
        SortedList<NguyenVongXetTuyenDTO> sortedData =
                new SortedList<>(filteredData);

        sortedData.comparatorProperty()
                .bind(tblNguyenVong.comparatorProperty());

        tblNguyenVong.setItems(sortedData);

        updateCount();
    }

    private void setupTable() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idNv"));

        colCccd.setCellValueFactory(
                new PropertyValueFactory<>("cccd"));

        colMaNganh.setCellValueFactory(
                new PropertyValueFactory<>("maNganh"));

        colThuTu.setCellValueFactory(
                new PropertyValueFactory<>("thuTu"));

        colPhuongThuc.setCellValueFactory(
                new PropertyValueFactory<>("phuongThuc"));

        colToHop.setCellValueFactory(
                new PropertyValueFactory<>("thm"));

        colDiemTHXT.setCellValueFactory(
                new PropertyValueFactory<>("diemThxt"));

        colDiemUTQD.setCellValueFactory(
                new PropertyValueFactory<>("diemUtqd"));

        colDiemCong.setCellValueFactory(
                new PropertyValueFactory<>("diemCong"));

        colDiemXT.setCellValueFactory(
                new PropertyValueFactory<>("diemXetTuyen"));

        colKetQua.setCellValueFactory(
                new PropertyValueFactory<>("ketQua"));

        colNvKeys.setCellValueFactory(
                new PropertyValueFactory<>("nvKeys"));

        colAction.setCellFactory(col -> new TableCell<>() {

            private final HBox box = makeActionCell(

                    () -> {

                        NguyenVongXetTuyenDTO item =
                                getTableRow().getItem();

                        if (item != null) {
                            openDialog(item);
                        }
                    },

                    () -> {

                        NguyenVongXetTuyenDTO item =
                                getTableRow().getItem();

                        if (item != null) {
                            onDelete(item);
                        }
                    }
            );

            @Override
            protected void updateItem(Void v, boolean empty) {

                super.updateItem(v, empty);

                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadData() {

        masterData.setAll(bus.getAllNganhToHop());

        updateCount();
    }

    private void updateCount() {

        if (filteredData != null) {

            lblCount.setText(
                    filteredData.size() + " bản ghi"
            );

        } else {

            lblCount.setText(
                    masterData.size() + " bản ghi"
            );
        }
    }

    // =========================
    // SEARCH UTILS
    // =========================

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

    // =========================
    // CRUD
    // =========================

    @FXML
    private void onAdd() {

        openDialog(null);
    }

    private void onDelete(NguyenVongXetTuyenDTO row) {

        if (confirmDelete(
                "Nguyện vọng ngành "
                        + row.getMaNganh()
                        + " của thí sinh "
                        + row.getCccd()
        )) {

            String result =
                    bus.deleteNguyenVongXetTuyen(row.getIdNv());

            if (result.contains("successfully")) {

                loadData();

                showInfo(
                        "Thành công",
                        "Đã xóa nguyện vọng."
                );

            } else {

                showError(result);
            }
        }
    }

    private void openDialog(NguyenVongXetTuyenDTO row) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/admissionManagement/desktop/views/admin/NguyenVongXetTuyenDialogUI.fxml"
                    )
            );

            Parent root = loader.load();

            NguyenVongXetTuyenDialogController dialogCtrl =
                    loader.getController();

            Stage stage = new Stage();

            stage.setTitle(
                    row == null
                            ? "Thêm nguyện vọng"
                            : "Sửa nguyện vọng"
            );

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));

            dialogCtrl.init(stage, row, bus);

            stage.showAndWait();

            if (dialogCtrl.getIsSaved()) {

                loadData();
            }

        } catch (IOException e) {

            showError(
                    "Lỗi giao diện: " + e.getMessage()
            );
        }
    }
}