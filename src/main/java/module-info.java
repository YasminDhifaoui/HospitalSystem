module com.example.hospitalsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.hospitalsystem to javafx.fxml;
    opens data to javafx.base;
    exports com.example.hospitalsystem;
}
