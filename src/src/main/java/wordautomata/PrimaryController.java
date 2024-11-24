package wordautomata;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class PrimaryController {
    @FXML private VBox nodeEdgeList;
    @FXML private Label statusLabel;

    @FXML private Button runButton;
    @FXML private Label history;

    @FXML
    private void initialize(){
        updateToolTip();

        Label label;
        for (int i = 1; i <= 50; i++) {
            label = new Label("Label " + i); // Crea una nuova Label
            label.setTextAlignment(TextAlignment.LEFT); // Assicura che il testo sia allineato a sinistra
            nodeEdgeList.getChildren().add(label); // Aggiungi la Label alla VBox
        }
    }

    @FXML
    private void changeIcon(){
        System.out.println(runButton.getStyleClass());
        if (runButton.getStyleClass().contains("RunButton")) {
            runButton.getStyleClass().setAll("button", "loadingButton"); 
        } else {
            runButton.getStyleClass().setAll("button", "RunButton");
        }
    }

    private void updateToolTip(){
        Tooltip tooltip = new Tooltip(history.getText());

        history.setTooltip(tooltip);
    }

    /*
    // in caso di aggiunta seconda pagina:
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    */
}
