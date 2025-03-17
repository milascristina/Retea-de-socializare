module com.example.laborator6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.laborator6 to javafx.fxml;
    exports com.example.laborator6;
}