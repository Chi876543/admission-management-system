package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.ThiSinhDTO;
import com.admissionManagement.core.service.ThiSinhBUS;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ThiSinhController extends BaseController implements Initializable {

    // Khởi tạo tầng BUS để giao tiếp với DB
    private final ThiSinhBUS thiSinhBUS = new ThiSinhBUS();

    // ── Giao diện TableView ──
    @FXML private TextField tfSearch;
    @FXML private TableView<ThiSinhDTO> tblThiSinh;
    @FXML private TableColumn<ThiSinhDTO, Integer> colId;
    @FXML
    private TableColumn<ThiSinhDTO, String> colSbd, colCccd, colHo, colTen,
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
            cbGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
            cbDoiTuong.setItems(FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "Không"));
            cbKhuVuc.setItems(FXCollections.observableArrayList("KV1", "KV2", "KV2-NT", "KV3"));
        }
    }

    // ── Cấu hình TableView ──────────────────────────────────
    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idThiSinh"));
        colSbd.setCellValueFactory(new PropertyValueFactory<>("soBaoDanh"));
        colCccd.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colHo.setCellValueFactory(new PropertyValueFactory<>("ho"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("dienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNoiSinh.setCellValueFactory(new PropertyValueFactory<>("noiSinh"));
        colDoiTuong.setCellValueFactory(new PropertyValueFactory<>("doiTuong"));
        colKhuVuc.setCellValueFactory(new PropertyValueFactory<>("khuVuc"));

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
        List<ThiSinhDTO> dataTuDB = thiSinhBUS.getAllThiSinh();
        allData.setAll(dataTuDB);
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

    private void onDelete(ThiSinhDTO row) {
        if (confirmDelete(row.getHo() + " " + row.getTen())) {
            String result = thiSinhBUS.deleteThiSinh(row.getIdThiSinh());

            if (result.contains("successfully")) {
                allData.remove(row);
                applyFilter();
                showInfo("Thành công", "Đã xóa thí sinh!");
            } else {
                showError("Lỗi xóa: " + result);
            }
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

            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initDialog(Stage stage, ThiSinhDTO row, ObservableList<ThiSinhDTO> data, ThiSinhController parent) {
        this.dialogStage = stage;
        this.editingRow = row;

        if (row != null) {
            lblDialogTitle.setText("Sửa hồ sơ thí sinh ID: " + row.getIdThiSinh());
            tfSbd.setText(row.getSoBaoDanh());
            tfCccd.setText(row.getCccd()); tfCccd.setDisable(true);
            tfHo.setText(row.getHo());
            tfTen.setText(row.getTen());
            tfSdt.setText(row.getDienThoai());
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
            ThiSinhDTO r = new ThiSinhDTO(
                    0,
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
            String result = thiSinhBUS.addThiSinh(r);
            if (!result.contains("successfully")) {
                lblError.setText(result);
                return;
            }
        } else {
            editingRow.setSoBaoDanh(tfSbd.getText().trim());
            editingRow.setHo(tfHo.getText().trim());
            editingRow.setTen(tfTen.getText().trim());
            editingRow.setDienThoai(tfSdt.getText().trim());
            editingRow.setEmail(tfEmail.getText().trim());
            editingRow.setNoiSinh(tfNoiSinh.getText().trim());
            editingRow.setGioiTinh(cbGioiTinh.getValue());
            editingRow.setDoiTuong(cbDoiTuong.getValue());
            editingRow.setKhuVuc(cbKhuVuc.getValue());

            String result = thiSinhBUS.updateThiSinh(editingRow.getIdThiSinh(), editingRow);
            if (!result.contains("successfully")) {
                lblError.setText(result);
                return;
            }
        }
        dialogStage.close();
    }

    @FXML private void onDialogCancel() { dialogStage.close(); }

    // ── Xử lý Import CSV qua Batch ─────────────────────────
    private void processImport(File file) {
        List<ThiSinhDTO> listImport = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {
                    ThiSinhDTO dto = new ThiSinhDTO(
                            0,
                            line[0].trim(), line[1].trim(), line[2].trim(), line[3].trim(),
                            line[4].trim(), line[5].trim(), line[6].trim(), line[7].trim(),
                            "123456",
                            line[8].trim(), line[9].trim(), line[10].trim(), null
                    );
                    listImport.add(dto);
                } catch (Exception e) {
                    System.out.println("Bỏ qua 1 dòng lỗi cấu trúc.");
                }
            }

            String result = thiSinhBUS.addListThiSinh(listImport);

            showInfo("Kết quả Import", result);
            loadData();

        } catch (Exception e) {
            showError("Lỗi không thể đọc file: " + e.getMessage());
        }
    }

    @FXML
    private void onImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file CSV thí sinh");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(tblThiSinh.getScene().getWindow());

        if (selectedFile != null) {
            processImport(selectedFile);
        }
    }
}