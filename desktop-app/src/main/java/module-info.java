module desktop.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // cần cho BarChart
    requires core;

    exports com.admissionManagement.desktop;

    opens com.admissionManagement.desktop to javafx.fxml;
    opens com.admissionManagement.desktop.controllers to javafx.fxml;
    opens com.admissionManagement.desktop.controllers.admin to javafx.fxml, javafx.base;
}
