module wordautomata {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens wordautomata to javafx.fxml;
    exports wordautomata;
}
