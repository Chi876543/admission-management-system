package com.admissionManagement.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load màn hình login trước
        // Tạm thời load thẳng main.fxml để test UI
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/com/admissionManagement/desktop/views/main.fxml"))
        );

        Scene scene = new Scene(root, 1100, 680);
        primaryStage.setTitle("Hệ thống Quản lý Tuyển sinh 2025");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
