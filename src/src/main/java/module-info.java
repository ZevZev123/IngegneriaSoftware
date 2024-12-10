module wordautomata {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    opens wordautomata to javafx.fxml;
    exports wordautomata;
}
