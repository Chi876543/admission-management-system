package com.admissionManagement.desktop.controllers.admin;

import com.admissionManagement.core.dto.UserDTO;
import com.admissionManagement.core.service.UserBUS;
import com.admissionManagement.desktop.controllers.admin.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblError;
    @FXML private TextField tfPasswordVisible;
    @FXML private Button btnTogglePass;

    private boolean passwordVisible = false;

    private final UserBUS userBUS = new UserBUS();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Xóa lỗi khi user bắt đầu gõ lại
        tfUsername.textProperty().addListener((obs, o, n) -> lblError.setText(""));
        tfPassword.textProperty().addListener((obs, o, n) -> lblError.setText(""));
    }

    @FXML
    private void onTogglePassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            // Hiện TextField, ẩn PasswordField
            tfPasswordVisible.setText(tfPassword.getText());
            tfPasswordVisible.setVisible(true);
            tfPasswordVisible.setManaged(true);
            tfPassword.setVisible(false);
            tfPassword.setManaged(false);
            btnTogglePass.setText("🙈");
        } else {
            // Hiện PasswordField, ẩn TextField
            tfPassword.setText(tfPasswordVisible.getText());
            tfPassword.setVisible(true);
            tfPassword.setManaged(true);
            tfPasswordVisible.setVisible(false);
            tfPasswordVisible.setManaged(false);
            btnTogglePass.setText("👁");
        }
    }

    @FXML
    private void onLogin() {
        String username = tfUsername.getText().trim();
        String password = passwordVisible
                ? tfPasswordVisible.getText()
                : tfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
            return;
        }

        UserDTO user = userBUS.login(username, password);

        if (user == null) {
            lblError.setText("Sai tài khoản hoặc mật khẩu");
            tfPassword.clear();
            return;
        }

        if ("Đã khóa".equals(user.getStatus())) {
            lblError.setText("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên");
            return;
        }

        // Lưu session
        SessionManager.getInstance().setCurrentUser(user);

        // Chuyển sang màn hình chính
        switchToMain();
    }

    private void switchToMain() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/admissionManagement/desktop/views/main.fxml")
            );
            Stage stage = (Stage) tfUsername.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 680);
            stage.setScene(scene);
            stage.setTitle("Hệ thống Quản lý Tuyển sinh");
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            lblError.setText("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Enter trong ô password cũng submit
    @FXML
    private void onPasswordKeyPressed(javafx.scene.input.KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
            onLogin();
        }
    }
}