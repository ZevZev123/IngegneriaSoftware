package AutomaZ;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class NewEdgeController {
    @FXML private TextField textField;
    @FXML private ComboBox<String> nodeStart;
    @FXML private ComboBox<String> nodeEnd;

    @FXML private Label errorLabel;

    private MainPageController controller = null;
    private List<Node> nodeList;

    @FXML
    private void initialize() {
        // Chiude la finestra con la pressione del tasto ESC
        textField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        closePage();
                    }
                });
            }
        });
        
        if (nodeList != null) {
            for (Node node : nodeList) {
                nodeStart.getItems().add(node.getName());
                nodeEnd.getItems().add(node.getName());
            }
        }
    }

    @FXML
    private void newEdge() {
        if (nodeStart.getValue() == null || nodeEnd.getValue() == null) {
            errorLabel.setText("Seleziona due nodi!");
            errorLabel.setTextFill(Color.RED);
            return;
        } else if (textField.getText().isEmpty()) {
            errorLabel.setText("Inserisci un nome!");
            errorLabel.setTextFill(Color.RED);
            return;
        }
        
        Node start = nodeList.get(nodeStart.getSelectionModel().getSelectedIndex());
        Node end = nodeList.get(nodeEnd.getSelectionModel().getSelectedIndex());

        if (controller != null) {
            controller.createEdge(start, end, textField.getText());
            closePage();
        } else {
            errorLabel.setText("C'è stato un errore!");
            errorLabel.setTextFill(Color.RED);
            return;
        }
    }

    @FXML
    private void closePage() {
        Stage stage = (Stage) this.errorLabel.getScene().getWindow();
        stage.close();
    }

    public void setPrimaryController(MainPageController controller) { 
        this.controller = controller; 
    }

    public void setNodeList(List<Node> nodeList) { 
        this.nodeList = nodeList; 
        if (nodeStart != null && nodeEnd != null) {
            initialize();
        }
    }

    public void setStartNode(Node node) { 
        nodeStart.setValue(node.getName()); 
    }
}
