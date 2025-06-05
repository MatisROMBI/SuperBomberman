module com.example.superbomberman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.superbomberman to javafx.fxml;
    exports com.example.superbomberman;
}