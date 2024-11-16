module wordautomata {
    requires javafx.controls;
    requires javafx.fxml;

    opens wordautomata to javafx.fxml;
    exports wordautomata;
}
