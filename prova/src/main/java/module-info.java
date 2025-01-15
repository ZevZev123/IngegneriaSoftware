module prova {
    requires javafx.controls;
    requires javafx.fxml;

    opens prova to javafx.fxml;
    exports prova;
}
