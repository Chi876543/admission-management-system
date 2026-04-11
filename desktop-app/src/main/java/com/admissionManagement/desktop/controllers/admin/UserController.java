package com.admissionManagement.desktop.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * UserController — Màn hình Quản lý Người dùng.
 *
 * Phần logic hiện tại dùng data giả (dummy) để test UI.
 * Khi BE sẵn sàng: thay các phương thức loadData(), saveUser(),
 * deleteUser() bằng gọi service/API thực.
 */
public class UserController implements Initializable {

    // ── FXML bindings ─────────────────────────────────
    @FXML private TextField   tfSearch;
    @FXML private ComboBox<String> cbRole;
    @FXML private ComboBox<String> cbStatus;

    @FXML private TableView<UserRow>       tblUsers;
    @FXML private TableColumn<UserRow, String> colStt;
    @FXML private TableColumn<UserRow, String> colUsername;
    @FXML private TableColumn<UserRow, String> colHoTen;
    @FXML private TableColumn<UserRow, String> colEmail;
    @FXML private TableColumn<UserRow, String> colRole;
    @FXML private TableColumn<UserRow, String> colStatus;
    @FXML private TableColumn<UserRow, Void>   colAction;

    @FXML private Label      lblRecordCount;
    @FXML private Pagination pagination;

    // ── State ────────────────────────────────────────
    private ObservableList<UserRow> allData    = FXCollections.observableArrayList();
    private ObservableList<UserRow> filtered   = FXCollections.observableArrayList();

    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;

    // ── Dialog fields (dùng khi edit/add) ───────────
    @FXML private Label         lblDialogTitle;
    @FXML private TextField     tfUsername;
    @FXML private TextField     tfHoTen;
    @FXML private TextField     tfEmail;
    @FXML private PasswordField tfPassword;
    @FXML private VBox vboxPassword;   // ẩn khi Edit
    @FXML private ComboBox<String> cbRole2;     // trong dialog (fx:id="cbRole" bị trùng, dùng alias)
    @FXML private ComboBox<String> cbStatusDialog;
    @FXML private Label         lblError;

    private UserRow editingRow = null;  // null = chế độ thêm mới
    private Stage   dialogStage;

    // ─────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupTable();
        loadData();
    }

    // ── Setup ─────────────────────────────────────────
    private void setupComboBoxes() {
        cbRole.setItems(FXCollections.observableArrayList("Admin", "User"));
        cbStatus.setItems(FXCollections.observableArrayList("Đang hoạt động", "Đã khóa"));
        cbRole.setOnAction(e -> applyFilter());
        cbStatus.setOnAction(e -> applyFilter());
    }

    private void setupTable() {
        // Cột đơn giản map property
        colStt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStt()));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Cột Quyền — hiển thị badge màu
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            {
                badge.getStyleClass().add("badge");
                setAlignment(Pos.CENTER);
                setGraphic(badge);
            }
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) { badge.setVisible(false); return; }
                badge.setVisible(true);
                badge.setText(role);
                badge.getStyleClass().removeAll("badge-amber", "badge-blue");
                badge.getStyleClass().add(role.equals("Admin") ? "badge-amber" : "badge-blue");
            }
        });

        // Cột Trạng thái — badge màu
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            {
                badge.getStyleClass().add("badge");
                setAlignment(Pos.CENTER);
                setGraphic(badge);
            }
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { badge.setVisible(false); return; }
                badge.setVisible(true);
                badge.setText(status);
                badge.getStyleClass().removeAll("badge-green", "badge-red");
                badge.getStyleClass().add(status.equals("Hoạt động") ? "badge-green" : "badge-red");
            }
        });

        // Cột Thao tác — nút Sửa, Khóa/Mở, Đổi quyền
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Sửa");
            private final Button btnToggle = new Button("Khóa");
            private final HBox   box       = new HBox(6, btnEdit, btnToggle);
            {
                box.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().addAll("btn-default", "btn-sm");
                btnToggle.getStyleClass().addAll("btn-warning", "btn-sm");

                btnEdit.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    openDialog(row);
                });
                btnToggle.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    toggleStatus(row);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                UserRow row = getTableView().getItems().get(getIndex());
                boolean locked = "Đã khóa".equals(row.getStatus());
                btnToggle.setText(locked ? "Mở khóa" : "Khóa");
                btnToggle.getStyleClass().removeAll("btn-warning", "btn-success");
                btnToggle.getStyleClass().add(locked ? "btn-success" : "btn-warning");
                setGraphic(box);
            }
        });

        // Pagination
        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            currentPage = newIdx.intValue();
            showPage();
        });
    }

    // ── Data ──────────────────────────────────────────
    private void loadData() {
        // ══ DUMMY DATA — thay bằng service.getAllUsers() ══
        allData.setAll(
            new UserRow("1",  "admin01", "Nguyễn Văn An",   "an@edu.vn",   "Admin", "Hoạt động"),
            new UserRow("2",  "user01",  "Trần Thị Bình",   "binh@edu.vn", "User",  "Hoạt động"),
            new UserRow("3",  "user02",  "Lê Minh Châu",    "chau@edu.vn", "User",  "Đã khóa"),
            new UserRow("4",  "user03",  "Phạm Quốc Dũng",  "dung@edu.vn", "User",  "Hoạt động"),
            new UserRow("5",  "user04",  "Hoàng Thị Lan",   "lan@edu.vn",  "User",  "Hoạt động")
        );
        applyFilter();
    }

    private void applyFilter() {
        String keyword = tfSearch.getText().trim().toLowerCase();
        String role    = cbRole.getValue();
        String status  = cbStatus.getValue();

        List<UserRow> result = allData.stream()
            .filter(r -> keyword.isEmpty()
                || r.getUsername().toLowerCase().contains(keyword)
                || r.getHoTen().toLowerCase().contains(keyword)
                || r.getEmail().toLowerCase().contains(keyword))
            .filter(r -> role == null   || r.getRole().equals(role))
            .filter(r -> status == null || r.getStatus().contains(status.split(" ")[0]))
            .collect(Collectors.toList());

        // Cập nhật STT sau filter
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setStt(String.valueOf(i + 1));
        }

        filtered.setAll(result);
        currentPage = 0;
        int pageCount = Math.max(1, (int) Math.ceil((double) filtered.size() / PAGE_SIZE));
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        showPage();
    }

    private void showPage() {
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, filtered.size());
        tblUsers.setItems(FXCollections.observableArrayList(
                filtered.subList(from, Math.max(from, to))));
        lblRecordCount.setText(filtered.size() + " bản ghi");
    }

    // ── Search handler ────────────────────────────────
    @FXML private void onSearch() { applyFilter(); }

    // ── Thêm mới ──────────────────────────────────────
    @FXML private void onAdd() { openDialog(null); }

    // ── Dialog ────────────────────────────────────────
    private void openDialog(UserRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/user-dialog.fxml"));
            Parent root = loader.load();
            UserController ctrl = loader.getController();

            dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Thêm người dùng" : "Sửa người dùng");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            ctrl.initDialog(dialogStage, row, allData, this);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gọi từ dialog controller để khởi tạo dữ liệu.
     */
    public void initDialog(Stage stage, UserRow row,
                           ObservableList<UserRow> data, UserController parent) {
        this.dialogStage = stage;
        this.editingRow  = row;
        this.allData     = data;

        cbRole2 = cbRole;           // alias (trong dialog FXML dùng fx:id="cbRole")
        cbRole.setItems(FXCollections.observableArrayList("Admin", "User"));
        cbStatusDialog.setItems(FXCollections.observableArrayList("Hoạt động", "Đã khóa"));

        if (row != null) {
            // Chế độ sửa
            lblDialogTitle.setText("Sửa người dùng");
            tfUsername.setText(row.getUsername());
            tfUsername.setDisable(true);    // không cho đổi username
            tfHoTen.setText(row.getHoTen());
            tfEmail.setText(row.getEmail());
            cbRole.setValue(row.getRole());
            cbStatusDialog.setValue(row.getStatus());
            if (vboxPassword != null) vboxPassword.setVisible(false);
        } else {
            lblDialogTitle.setText("Thêm người dùng");
            cbRole.setValue("User");
            cbStatusDialog.setValue("Hoạt động");
        }
    }

    @FXML private void onDialogSave() {
        if (!validateDialog()) return;

        if (editingRow == null) {
            // Thêm mới
            String newStt = String.valueOf(allData.size() + 1);
            UserRow newRow = new UserRow(
                    newStt, tfUsername.getText().trim(), tfHoTen.getText().trim(),
                    tfEmail.getText().trim(), cbRole.getValue(), cbStatusDialog.getValue());
            allData.add(newRow);
            // TODO: userService.create(newRow);
        } else {
            // Sửa
            editingRow.setHoTen(tfHoTen.getText().trim());
            editingRow.setEmail(tfEmail.getText().trim());
            editingRow.setRole(cbRole.getValue());
            editingRow.setStatus(cbStatusDialog.getValue());
            // TODO: userService.update(editingRow);
        }

        applyFilter();
        if (dialogStage != null) dialogStage.close();
    }

    @FXML private void onDialogCancel() {
        if (dialogStage != null) dialogStage.close();
    }

    private boolean validateDialog() {
        if (tfUsername != null && tfUsername.getText().trim().isEmpty()) {
            showError("Username không được để trống."); return false;
        }
        if (tfHoTen != null && tfHoTen.getText().trim().isEmpty()) {
            showError("Họ tên không được để trống."); return false;
        }
        if (tfEmail != null && !tfEmail.getText().contains("@")) {
            showError("Email không hợp lệ."); return false;
        }
        if (editingRow == null && tfPassword != null && tfPassword.getText().length() < 6) {
            showError("Mật khẩu tối thiểu 6 ký tự."); return false;
        }
        if (lblError != null) lblError.setText("");
        return true;
    }

    private void showError(String msg) {
        if (lblError != null) lblError.setText(msg);
    }

    // ── Toggle khóa/mở ───────────────────────────────
    private void toggleStatus(UserRow row) {
        String newStatus = row.getStatus().equals("Hoạt động") ? "Đã khóa" : "Hoạt động";
        row.setStatus(newStatus);
        // TODO: userService.setStatus(row.getUsername(), newStatus);
        tblUsers.refresh();
    }

    // ════════════════════════════════════════════════
    // Inner DTO — thay bằng Model thật khi có BE
    // ════════════════════════════════════════════════
    public static class UserRow {
        private String stt, username, hoTen, email, role, status;

        public UserRow(String stt, String username, String hoTen,
                       String email, String role, String status) {
            this.stt = stt; this.username = username; this.hoTen = hoTen;
            this.email = email; this.role = role; this.status = status;
        }

        public String getStt()      { return stt; }
        public String getUsername() { return username; }
        public String getHoTen()    { return hoTen; }
        public String getEmail()    { return email; }
        public String getRole()     { return role; }
        public String getStatus()   { return status; }

        public void setStt(String v)      { stt = v; }
        public void setUsername(String v) { username = v; }
        public void setHoTen(String v)    { hoTen = v; }
        public void setEmail(String v)    { email = v; }
        public void setRole(String v)     { role = v; }
        public void setStatus(String v)   { status = v; }
    }
}
