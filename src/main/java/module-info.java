module com.bomberman {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bomberman.controller to javafx.fxml;

    exports com.bomberman;
}
