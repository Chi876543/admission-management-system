package com.admissionManagement.desktop.controllers;

import com.admissionManagement.core.dto.UserDTO;
import com.admissionManagement.desktop.controllers.admin.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * com.admissionManagement.desktop.controllers.MainController — điều phối sidebar và content area.
 *
 * Nguyên lý: mỗi lần click nav item, load FXML tương ứng
 * vào contentArea (StackPane). Cache lại sau lần đầu load
 * để tránh load lại nhiều lần.
 */
public class MainController implements Initializable {

    // ── Sidebar buttons ──────────────────────────────
    @FXML private Button btnUsers;
    @FXML private Button btnThiSinh;
    @FXML private Button btnNganh;
    @FXML private Button btnToHop;
    @FXML private Button btnNganhToHop;
    @FXML private Button btnDiem;
    @FXML private Button btnDiemCong;
    @FXML private Button btnNguyenVong;
    @FXML private Button btnQuyDoi;
    @FXML private Button btnDangXuat;

    // ── Topbar ───────────────────────────────────────
    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSub;
    @FXML private Label lblAdminName;
    @FXML private Label lblUserBadge;

    // ── Content area ─────────────────────────────────
    @FXML private StackPane contentArea;

    // ── State ────────────────────────────────────────
    private Button activeNavBtn;
    private final Map<String, Node> screenCache = new HashMap<>();

    // ── Màn hình & metadata ──────────────────────────
    private static final String[][] SCREENS = {
            // { fxml-key, fxml-path, title, subtitle }
            {"users",       "admin/user-view.fxml",         "Quản lý người dùng",         "Danh sách tài khoản hệ thống"},
            {"thisinh",     "admin/thisinh-view.fxml",      "Quản lý thí sinh",            "Import · Xem · Tìm kiếm · Sửa"},
            {"nganh",       "admin/nganh-view.fxml",        "Quản lý ngành tuyển sinh",    "Thêm, sửa, xóa, import ngành"},
            {"tohop",       "admin/tohop-view.fxml",        "Quản lý tổ hợp môn",          "Thêm, sửa, xóa tổ hợp"},
            {"nganhtohop",  "admin/nganh-tohop-view.fxml",  "Ngành - Tổ hợp",              "Gán tổ hợp xét tuyển cho từng ngành"},
            {"diem",        "admin/DiemThiXetTuyenUI.fxml",         "Quản lý điểm thí sinh",       "THPT · VSAT · ĐGNL · Thống kê"},
            {"diemcong",    "admin/DiemCongXetTuyenUI.fxml",     "Quản lý điểm cộng",           "Ưu tiên đối tượng, khu vực"},
            {"nguyenvong", "admin/NguyenVongXetTuyenUI.fxml", "Nguyện vọng & xét tuyển", "Chạy xét tuyển · Xem kết quả"},
            {"quydoi", "admin/BangQuyDoiUI.fxml", "Bảng quy đổi điểm", "Quy đổi VSAT / ĐGNL sang thang 30"},
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getHoTen() != null
                    ? currentUser.getHoTen() : currentUser.getUsername();
            lblAdminName.setText(displayName);
            lblUserBadge.setText("● " + currentUser.getRole());

            // Ẩn "Quản lý người dùng" nếu không phải Admin
            if (!"Admin".equals(currentUser.getRole())) {
                btnUsers.setVisible(false);
                btnUsers.setManaged(false);
            }
        }

        // Load màn hình mặc định tùy role
        if (SessionManager.getInstance().isAdmin()) {
            loadScreen("users", btnUsers);
        } else {
            loadScreen("thisinh", btnThiSinh);
        }
    }

    // ── Nav handlers ─────────────────────────────────
    @FXML private void onNavUsers()       { loadScreen("users",       btnUsers);       }
    @FXML private void onNavThiSinh()     { loadScreen("thisinh",     btnThiSinh);     }
    @FXML private void onNavNganh() {
        screenCache.remove("nganhtohop"); // Ngành thay đổi → invalidate cache ngành-tổ hợp
        loadScreen("nganh", btnNganh);
    }

    @FXML private void onNavToHop() {
        screenCache.remove("nganhtohop"); // Tổ hợp thay đổi → invalidate cache ngành-tổ hợp
        loadScreen("tohop", btnToHop);
    }

    @FXML private void onNavNganhToHop() {
        screenCache.remove("nganhtohop"); // Luôn reload màn hình này
        loadScreen("nganhtohop", btnNganhToHop);
    }
    @FXML private void onNavDiem()        { loadScreen("diem",        btnDiem);        }
    @FXML private void onNavDiemCong()    { loadScreen("diemcong",    btnDiemCong);    }
    @FXML private void onNavNguyenVong() {
        // Nguyện vọng thay đổi → khi quay lại Ngành cần reload soLuongDangKy
        screenCache.remove("nganh");
        loadScreen("nguyenvong", btnNguyenVong);
    }
    @FXML private void onNavQuyDoi()      { loadScreen("quydoi",      btnQuyDoi);      }

    @FXML private void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn đăng xuất không?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        // Xóa session
        SessionManager.getInstance().logout();

        // Quay về login
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/admissionManagement/desktop/views/admin/login.fxml")
            );
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 480, 560));
            stage.setTitle("Đăng nhập — Hệ thống Quản lý Tuyển sinh 2025");
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Core: load & switch screen ───────────────────
    private void loadScreen(String key, Button navBtn) {
        // Cập nhật active style cho sidebar
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().remove("nav-item-active");
        }
        navBtn.getStyleClass().add("nav-item-active");
        activeNavBtn = navBtn;

        // Cập nhật topbar title
        for (String[] screen : SCREENS) {
            if (screen[0].equals(key)) {
                lblPageTitle.setText(screen[2]);
                lblPageSub.setText(screen[3]);
                break;
            }
        }

        // Load từ cache hoặc load mới
        Node screenNode = screenCache.get(key);
        if (screenNode == null) {
            screenNode = loadFXML(key);
            if (screenNode == null) return;
            screenCache.put(key, screenNode);
        }

        // Hiển thị
        contentArea.getChildren().setAll(screenNode);
    }

    private Node loadFXML(String key) {
        // Tìm path theo key
        String path = null;
        for (String[] screen : SCREENS) {
            if (screen[0].equals(key)) {
                path = screen[1];
                break;
            }
        }
        if (path == null) return null;

        try {
            URL fxmlUrl = getClass().getResource("/com/admissionManagement/desktop/views/" + path);
            if (fxmlUrl == null) {
                System.err.println("[com.admissionManagement.desktop.controllers.MainController] Không tìm thấy FXML: " + path);
                return null;
            }
            return FXMLLoader.load(fxmlUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}