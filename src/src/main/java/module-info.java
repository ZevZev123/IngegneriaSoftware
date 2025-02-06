module AutomaZ {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens AutomaZ to javafx.fxml;
    exports AutomaZ;
}
