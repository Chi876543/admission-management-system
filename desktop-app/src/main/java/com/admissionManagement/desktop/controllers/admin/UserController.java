package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.UserDTO;
import com.admissionManagement.core.service.UserBUS;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserController implements Initializable {

    // ── FXML (list view) ───────────────────────────────
    @FXML private TextField        tfSearch;
    @FXML private ComboBox<String> cbRole;
    @FXML private ComboBox<String> cbStatus;

    @FXML private TableView<UserRow>           tblUsers;
    @FXML private TableColumn<UserRow, String> colStt, colUsername, colHoTen, colEmail, colRole, colStatus;
    @FXML private TableColumn<UserRow, Void>   colAction;

    @FXML private Label      lblRecordCount;
    @FXML private Pagination pagination;

    // ── FXML (dialog) ──────────────────────────────────
    @FXML private Label            lblDialogTitle;
    @FXML private TextField        tfUsername, tfHoTen, tfEmail;
    @FXML private PasswordField    tfPassword;
    @FXML private VBox             vboxPassword;
    @FXML private ComboBox<String> cbRoleDialog, cbStatusDialog;
    @FXML private Label            lblError;

    // ── State ──────────────────────────────────────────
    // allData = trang hiện tại từ DB (server-side)
    // filtered = lọc client-side trong trang (role/status filter)
    private final ObservableList<UserRow> allData  = FXCollections.observableArrayList();
    private final ObservableList<UserRow> filtered = FXCollections.observableArrayList();

    private final UserBUS userBUS   = new UserBUS();
    private static final int PAGE_SIZE = 20;
    private long totalRecords = 0;
    private int currentPage   = 0;
    private PauseTransition searchDebounce;

    private UserRow   editingRow       = null;
    private Stage     dialogStage      = null;
    private UserController parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (tblUsers != null) {
            setupComboBoxes();
            setupTable();

            searchDebounce = new PauseTransition(Duration.millis(400));
            searchDebounce.setOnFinished(e -> loadData(0));
            tfSearch.textProperty().addListener((obs, old, val) -> searchDebounce.playFromStart());

            pagination.currentPageIndexProperty().addListener((obs, o, n) -> {
                currentPage = n.intValue();
                loadData(currentPage);
            });

            loadData(0);
        }
    }

    private void setupComboBoxes() {
        cbRole.getItems().setAll("Tất cả", "Admin", "User");
        cbRole.setValue("Tất cả");
        cbStatus.getItems().setAll("Tất cả", "Hoạt động", "Đã khóa");
        cbStatus.setValue("Tất cả");
        cbRole.setOnAction(e -> applyClientFilter());
        cbStatus.setOnAction(e -> applyClientFilter());
    }

    private void setupTable() {
        colStt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStt()));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge"); setAlignment(Pos.CENTER); setGraphic(badge); }
            @Override protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) { badge.setVisible(false); return; }
                badge.setVisible(true); badge.setText(role);
                badge.getStyleClass().removeAll("badge-amber", "badge-blue");
                badge.getStyleClass().add(role.equals("Admin") ? "badge-amber" : "badge-blue");
            }
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge"); setAlignment(Pos.CENTER); setGraphic(badge); }
            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { badge.setVisible(false); return; }
                badge.setVisible(true); badge.setText(status);
                badge.getStyleClass().removeAll("badge-green", "badge-red");
                badge.getStyleClass().add(status.equals("Hoạt động") ? "badge-green" : "badge-red");
            }
        });

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("Sửa");
            private final Button btnToggle = new Button("Khóa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox   box       = new HBox(6, btnEdit, btnToggle, btnDelete);
            {
                box.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().addAll("btn-default", "btn-sm");
                btnToggle.getStyleClass().addAll("btn-warning", "btn-sm");
                btnDelete.getStyleClass().addAll("btn-danger", "btn-sm");
                btnEdit.setOnAction(e -> openDialog(getTableView().getItems().get(getIndex())));
                btnToggle.setOnAction(e -> toggleStatus(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> doDelete(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
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

        tblUsers.setItems(filtered);
    }

    /** Load từ DB theo trang — server-side */
    private void loadData(int pageIndex) {
        Task<List<UserDTO>> task = new Task<>() {
            @Override protected List<UserDTO> call() {
                if (pageIndex == 0) totalRecords = userBUS.getTotal();
                return userBUS.getAllUsers(pageIndex, PAGE_SIZE);
            }
        };

        task.setOnSucceeded(e -> {
            List<UserDTO> users = task.getValue();
            List<UserRow> rows = new ArrayList<>();
            int startIdx = pageIndex * PAGE_SIZE + 1;
            for (int i = 0; i < users.size(); i++) {
                UserDTO u = users.get(i);
                rows.add(new UserRow(
                        String.valueOf(startIdx + i), u.getId(),
                        u.getUsername(),
                        u.getHoTen()  != null ? u.getHoTen()  : "",
                        u.getEmail()  != null ? u.getEmail()  : "",
                        u.getRole()   != null ? u.getRole()   : "User",
                        u.getStatus() != null ? u.getStatus() : "Hoạt động"
                ));
            }
            allData.setAll(rows);

            int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
            pagination.setPageCount(totalPages);
            if (pagination.getCurrentPageIndex() != pageIndex)
                pagination.setCurrentPageIndex(pageIndex);

            applyClientFilter();
        });

        task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    /** Lọc role/status trên trang hiện tại (client-side) — không gọi DB */
    private void applyClientFilter() {
        String role   = "Tất cả".equals(cbRole.getValue())   ? null : cbRole.getValue();
        String status = "Tất cả".equals(cbStatus.getValue()) ? null : cbStatus.getValue();

        List<UserRow> result = allData.stream()
                .filter(r -> role   == null || r.getRole().equals(role))
                .filter(r -> status == null || r.getStatus().equals(status))
                .collect(Collectors.toList());

        for (int i = 0; i < result.size(); i++) result.get(i).setStt(String.valueOf(currentPage * PAGE_SIZE + i + 1));
        filtered.setAll(result);
        tblUsers.refresh();
        lblRecordCount.setText(totalRecords + " bản ghi");
    }

    @FXML private void onSearch()      { loadData(0); }
    @FXML private void onAdd()         { openDialog(null); }
    @FXML private void onClearFilter() {
        tfSearch.clear();
        cbRole.setValue("Tất cả");
        cbStatus.setValue("Tất cả");
        loadData(0);
    }

    private void doDelete(UserRow row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa tài khoản: " + row.getUsername() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Xác nhận xóa"); alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            String msg = userBUS.deleteUser(row.getId());
            showAlert(msg.startsWith("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION, msg);
            loadData(currentPage);
        }
    }

    private void toggleStatus(UserRow row) {
        String newStatus = "Hoạt động".equals(row.getStatus()) ? "Đã khóa" : "Hoạt động";
        String msg = userBUS.setStatus(row.getId(), newStatus);
        if (msg.startsWith("Lỗi")) showAlert(Alert.AlertType.ERROR, msg);
        loadData(currentPage);
    }

    private void openDialog(UserRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/admissionManagement/desktop/views/admin/user-dialog.fxml"));
            Parent root = loader.load();
            UserController dialogCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(row == null ? "Thêm người dùng" : "Sửa người dùng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            dialogCtrl.initDialog(stage, row, this);
            stage.showAndWait();
            loadData(currentPage);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Không thể mở dialog: " + e.getMessage());
        }
    }

    public void initDialog(Stage stage, UserRow row, UserController parent) {
        this.dialogStage      = stage;
        this.editingRow       = row;
        this.parentController = parent;

        cbRoleDialog.setItems(FXCollections.observableArrayList("Admin", "User"));
        cbStatusDialog.setItems(FXCollections.observableArrayList("Hoạt động", "Đã khóa"));

        if (row != null) {
            lblDialogTitle.setText("Sửa người dùng");
            tfUsername.setText(row.getUsername()); tfUsername.setDisable(true);
            tfHoTen.setText(row.getHoTen()); tfEmail.setText(row.getEmail());
            cbRoleDialog.setValue(row.getRole()); cbStatusDialog.setValue(row.getStatus());
            if (vboxPassword != null) { vboxPassword.setVisible(false); vboxPassword.setManaged(false); }
        } else {
            lblDialogTitle.setText("Thêm người dùng");
            cbRoleDialog.setValue("User"); cbStatusDialog.setValue("Hoạt động");
            if (vboxPassword != null) { vboxPassword.setVisible(true); vboxPassword.setManaged(true); }
        }
    }

    @FXML private void onDialogSave() {
        if (!validateDialog()) return;
        String result;
        if (editingRow == null) {
            result = userBUS.addUser(new UserDTO(0,
                    tfUsername.getText().trim(), tfHoTen.getText().trim(),
                    tfEmail.getText().trim(), tfPassword.getText(),
                    cbRoleDialog.getValue(), cbStatusDialog.getValue()));
        } else {
            String newPass = (tfPassword != null && !tfPassword.getText().isEmpty()) ? tfPassword.getText() : null;
            result = userBUS.updateUser(editingRow.getId(), new UserDTO(editingRow.getId(),
                    editingRow.getUsername(), tfHoTen.getText().trim(),
                    tfEmail.getText().trim(), newPass,
                    cbRoleDialog.getValue(), cbStatusDialog.getValue()));
        }
        if (result.startsWith("Lỗi")) { showError(result); return; }
        dialogStage.close();
    }

    @FXML private void onDialogCancel() { if (dialogStage != null) dialogStage.close(); }

    private boolean validateDialog() {
        if (lblError != null) lblError.setText("");
        if (tfUsername != null && tfUsername.getText().trim().isEmpty()) { showError("Username không được để trống"); return false; }
        if (tfHoTen   != null && tfHoTen.getText().trim().isEmpty())   { showError("Họ tên không được để trống"); return false; }
        if (tfEmail   != null && !tfEmail.getText().trim().contains("@")) { showError("Email không hợp lệ"); return false; }
        if (editingRow == null && tfPassword != null && tfPassword.getText().length() < 6) { showError("Mật khẩu tối thiểu 6 ký tự"); return false; }
        if (cbRoleDialog != null && cbRoleDialog.getValue() == null) { showError("Vui lòng chọn quyền"); return false; }
        return true;
    }

    private void showError(String msg) { if (lblError != null) lblError.setText(msg); }
    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    // ═══════════════════════════════════════════════════
    public static class UserRow {
        private String stt, username, hoTen, email, role, status;
        private int id;

        public UserRow(String stt, int id, String username, String hoTen,
                       String email, String role, String status) {
            this.stt=stt; this.id=id; this.username=username;
            this.hoTen=hoTen; this.email=email; this.role=role; this.status=status;
        }
        public int    getId()       { return id; }
        public String getStt()      { return stt; }
        public String getUsername() { return username; }
        public String getHoTen()    { return hoTen; }
        public String getEmail()    { return email; }
        public String getRole()     { return role; }
        public String getStatus()   { return status; }
        public void setStt(String v)    { stt = v; }
        public void setUsername(String v){ username = v; }
        public void setHoTen(String v)  { hoTen = v; }
        public void setEmail(String v)  { email = v; }
        public void setRole(String v)   { role = v; }
        public void setStatus(String v) { status = v; }
    }
}
