module wordautomata {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens wordautomata to javafx.fxml;
    exports wordautomata;
}
