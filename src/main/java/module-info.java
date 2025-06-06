module com.bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.bomberman.controller to javafx.fxml;

    exports com.bomberman;
}
