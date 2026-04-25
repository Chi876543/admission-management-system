package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.ThiSinhDTO;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ThiSinhController extends BaseController implements Initializable {

    // ── Giao diện TableView ──
    @FXML private TextField tfSearch;
    @FXML private TableView<ThiSinhDTO> tblThiSinh;
    @FXML private TableColumn<ThiSinhDTO, String> colId, colSbd, colCccd, colHo, colTen,
            colNgaySinh, colGioiTinh, colSdt, colEmail, colNoiSinh, colDoiTuong, colKhuVuc;
    @FXML private TableColumn<ThiSinhDTO, Void> colAction;
    @FXML private Label lblCount;
    @FXML private Pagination pagination;

    // ── Giao diện Form Dialog ──
    @FXML private Label lblDialogTitle, lblError;
    @FXML private TextField tfSbd, tfCccd, tfHo, tfTen, tfSdt, tfEmail, tfNoiSinh;
    @FXML private DatePicker dpNgaySinh;
    @FXML private ComboBox<String> cbGioiTinh, cbDoiTuong, cbKhuVuc;

    private final ObservableList<ThiSinhDTO> allData = FXCollections.observableArrayList();
    private List<ThiSinhDTO> filtered = new ArrayList<>();
    private int currentPage = 0;
    private ThiSinhDTO editingRow;
    private Stage dialogStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (tblThiSinh != null) {
            setupTable();
            loadData();
        }
        if (cbGioiTinh != null) {
            cbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
            cbDoiTuong.setItems(FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "Không"));
            cbKhuVuc.setItems(FXCollections.observableArrayList("KV1", "KV2", "KV2-NT", "KV3"));
        }
    }

    // ── Cấu hình TableView ──────────────────────────────────
    private void setupTable() {
        // Ánh xạ các cột chuẩn xác với DTO
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSbd.setCellValueFactory(new PropertyValueFactory<>("sbd"));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colHo.setCellValueFactory(new PropertyValueFactory<>("ho"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNoiSinh.setCellValueFactory(new PropertyValueFactory<>("noiSinh"));
        colDoiTuong.setCellValueFactory(new PropertyValueFactory<>("doiTuong"));
        colKhuVuc.setCellValueFactory(new PropertyValueFactory<>("khuVuc"));

        // Cột thao tác (Sửa / Xóa)
        colAction.setCellFactory(col -> new TableCell<>() {
            private final HBox box = makeActionCell(
                    () -> openDialog(getTableView().getItems().get(getIndex())),
                    () -> onDelete(getTableView().getItems().get(getIndex()))
            );
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        pagination.currentPageIndexProperty().addListener((o, ov, nv) -> {
            currentPage = nv.intValue();
            showPage();
        });
    }

    // ── Dữ liệu & Tìm kiếm ─────────────────────────────────
    private void loadData() {
        // ID là TS001, SBD là chuỗi số thực tế
        allData.setAll(
                new ThiSinhDTO("TS001", "02001542", "001234567890", "Nguyễn Văn", "An", "15/03/2006", "Nam", "0901234567", "an@gmail.com", null, "Hà Nội", "Không", "KV3", null),
                new ThiSinhDTO("TS002", "02001543", "001234567891", "Trần Thị", "Bình", "22/07/2006", "Nữ", "0912345678", "binh@gmail.com", null, "Hải Phòng", "01", "KV1", null),
                new ThiSinhDTO("TS003", "02001544", "001234567892", "Lê Minh", "Châu", "10/11/2005", "Nam", "0923456789", "chau@gmail.com", null, "Đà Nẵng", "06", "KV2", null)
        );
        applyFilter();
    }

    private void applyFilter() {
        String kw = tfSearch.getText().trim().toLowerCase();
        filtered = allData.stream()
                .filter(r -> kw.isEmpty()
                        || (r.getCccd() != null && r.getCccd().contains(kw))
                        || (r.getHo() + " " + r.getTen()).toLowerCase().contains(kw))
                .collect(Collectors.toList());

        currentPage = 0;
        pagination.setPageCount(pageCount(filtered.size()));
        pagination.setCurrentPageIndex(0);
        showPage();
    }

    private void showPage() {
        tblThiSinh.setItems(getPage(filtered, currentPage));
        lblCount.setText(filtered.size() + " thí sinh");
    }

    @FXML private void onSearch() { applyFilter(); }
    @FXML private void onAdd() { openDialog(null); }
    @FXML private void onImport() {
        showInfo("Import CSV", "Tính năng import sẽ hoạt động qua Service Layer.");
    }

    private void onDelete(ThiSinhDTO row) {
        if (confirmDelete(row.getHo() + " " + row.getTen())) {
            // TODO: Gọi API Delete truyền vào row.getId()
            allData.remove(row);
            applyFilter();
        }
    }

    // ── Quản lý Form Dialog (Thêm / Sửa) ───────────────────
    private void openDialog(ThiSinhDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/admin/thisinh-dialog.fxml"));
            Parent root = loader.load();
            ThiSinhController ctrl = loader.getController();

            dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Thêm thí sinh" : "Sửa thí sinh");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            ctrl.initDialog(dialogStage, row, allData, this);
            dialogStage.showAndWait();
            applyFilter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initDialog(Stage stage, ThiSinhDTO row, ObservableList<ThiSinhDTO> data, ThiSinhController parent) {
        this.dialogStage = stage;
        this.editingRow = row;

        if (row != null) {
            lblDialogTitle.setText("Sửa hồ sơ thí sinh: " + row.getId());
            tfSbd.setText(row.getSbd());
            tfCccd.setText(row.getCccd()); tfCccd.setDisable(true);
            tfHo.setText(row.getHo());
            tfTen.setText(row.getTen());
            tfSdt.setText(row.getSdt());
            tfEmail.setText(row.getEmail());
            tfNoiSinh.setText(row.getNoiSinh());
            cbGioiTinh.setValue(row.getGioiTinh());
            cbDoiTuong.setValue(row.getDoiTuong());
            cbKhuVuc.setValue(row.getKhuVuc());
        } else {
            lblDialogTitle.setText("Thêm thí sinh mới");
        }
    }

    @FXML private void onDialogSave() {
        if (tfHo.getText().trim().isEmpty() || tfTen.getText().trim().isEmpty()) {
            lblError.setText("Họ và Tên không được để trống.");
            return;
        }

        if (editingRow == null) {
            // Tạm thời tự sinh ID mẫu cho màn hình Desktop (Thực tế Spring Boot sẽ làm việc này)
            String generatedId = "TS" + String.format("%03d", allData.size() + 1);

            ThiSinhDTO r = new ThiSinhDTO(
                    generatedId,
                    tfSbd.getText().trim(),
                    tfCccd.getText().trim(),
                    tfHo.getText().trim(),
                    tfTen.getText().trim(),
                    dpNgaySinh.getValue() != null ? dpNgaySinh.getValue().toString() : "",
                    cbGioiTinh.getValue(),
                    tfSdt.getText().trim(),
                    tfEmail.getText().trim(),
                    "123456", // Password mặc định
                    tfNoiSinh.getText().trim(),
                    cbDoiTuong.getValue(),
                    cbKhuVuc.getValue(),
                    null
            );
            allData.add(r);
        } else {
            // Khi cập nhật, ID giữ nguyên
            editingRow.setSbd(tfSbd.getText().trim());
            editingRow.setHo(tfHo.getText().trim());
            editingRow.setTen(tfTen.getText().trim());
            editingRow.setSdt(tfSdt.getText().trim());
            editingRow.setEmail(tfEmail.getText().trim());
            editingRow.setNoiSinh(tfNoiSinh.getText().trim());
            editingRow.setGioiTinh(cbGioiTinh.getValue());
            editingRow.setDoiTuong(cbDoiTuong.getValue());
            editingRow.setKhuVuc(cbKhuVuc.getValue());
        }
        dialogStage.close();
    }

    @FXML private void onDialogCancel() { dialogStage.close(); }
}