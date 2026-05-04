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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ThiSinhController extends BaseController implements Initializable {
    private final ThiSinhBUS thiSinhBUS = new ThiSinhBUS();

    // Giao diện View
    @FXML private TextField tfSearch;
    @FXML private TableView<ThiSinhDTO> tblThiSinh;
    @FXML private TableColumn<ThiSinhDTO, Integer> colId;
    @FXML private TableColumn<ThiSinhDTO, String> colSbd, colCccd, colHo, colTen,
            colNgaySinh, colGioiTinh, colSdt, colEmail, colNoiSinh, colDoiTuong, colKhuVuc;
    @FXML private TableColumn<ThiSinhDTO, Void> colAction;
    @FXML private Label lblCount;
    @FXML private Pagination pagination;

    private final ObservableList<ThiSinhDTO> allData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadData(0);
    }

    // Cấu hình TableView
    private void setupTable() {
        tblThiSinh.setItems(allData);

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
                    () -> {
                        ThiSinhDTO item = getTableRow().getItem();
                        if (item != null) openDialog(item);
                    },
                    () -> {
                        ThiSinhDTO item = getTableRow().getItem();
                        if (item != null) onDelete(item);
                    }
            );
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        pagination.currentPageIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            loadData(newIndex.intValue());
        });
    }

    // Dữ liệu & Tìm kiếm
    private void loadData(int pageIndex) {
        String keyWord = tfSearch.getText();
        long totalRecords = thiSinhBUS.getTotal(keyWord);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        totalPages = Math.max(1, totalPages);
        pagination.setPageCount(totalPages);

        List<ThiSinhDTO> listThiSinh = thiSinhBUS.getAllThiSinh(keyWord, pageIndex, PAGE_SIZE);
        allData.setAll(listThiSinh);
    }

    @FXML private void onSearch() {
        if (pagination.getCurrentPageIndex() == 0) {
            loadData(0);
        } else {
            pagination.setCurrentPageIndex(0);
        }
    }
    @FXML private void onAdd() {
        openDialog(null);
    }

    private void onDelete(ThiSinhDTO row) {
        if (confirmDelete(row.getHo() + " " + row.getTen())) {
            String result = thiSinhBUS.deleteThiSinh(row.getIdThiSinh());

            if (result != null) {
                loadData(pagination.getCurrentPageIndex());
                showInfo("Thành công", result);
            } else {
                showError("Lỗi xóa: " + "Không tìm thấy thí sinh để xóa!");
            }
        }
    }

    // Quản lý Form Dialog (Thêm / Sửa)
    private void openDialog(ThiSinhDTO row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/admin/thisinh-dialog.fxml"));
            Parent root = loader.load();
            ThiSinhDialogController dialogCtrl = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Thêm thí sinh" : "Sửa thí sinh");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogCtrl.setDialogData(dialogStage, row);
            dialogStage.showAndWait();

            if (dialogCtrl.getIsSaved()) {
                loadData(pagination.getCurrentPageIndex());
            }
        } catch (IOException e) {
            showError("Lỗi khởi tạo giao diện: " + e.getMessage());
        }
    }

    // Xử lý Import CSV qua Batch
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
            loadData(0);

        } catch (Exception e) {
            showError("Lỗi không thể đọc file: " + e.getMessage());
        }
    }

    @FXML private void onImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file CSV thí sinh");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(tblThiSinh.getScene().getWindow());

        if (selectedFile != null) {
            processImport(selectedFile);
        }
    }
}