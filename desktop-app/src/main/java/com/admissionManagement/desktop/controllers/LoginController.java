package com.admissionManagement.desktop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        lblError.setVisible(false);

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin!");
            lblError.setVisible(true);
            return;
        }

        // TODO: Đoạn này sau này sẽ gọi sang module 'core' để check Database
        if (username.equals("admin") && password.equals("123456")) {
            btnLogin.setText("Đang tải dữ liệu...");
            btnLogin.setDisable(true);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/MainLayout.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnLogin.getScene().getWindow(); // Lấy cửa sổ hiện tại

                stage.setScene(new Scene(root, 1200, 800));
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblError.setText("Sai tài khoản hoặc mật khẩu!");
            lblError.setVisible(true);
        }
    }
}
