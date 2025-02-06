module AutomaZ {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;

    opens AutomaZ to javafx.fxml;
    exports AutomaZ;
}
