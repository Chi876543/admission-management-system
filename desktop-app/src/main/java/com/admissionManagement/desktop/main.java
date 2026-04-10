package com.admissionManagement.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admissionManagement/desktop/views/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 500, 600);

        primaryStage.setTitle("Hệ thống Quản lý Tuyển sinh");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Lệnh khởi chạy ứng dụng JavaFX
    }
}
