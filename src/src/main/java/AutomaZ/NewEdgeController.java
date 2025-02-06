package AutomaZ;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
        
        Node start = null;
        Node end = null;

        for (Node node : nodeList) {
            if (node.getName().equals(nodeStart.getValue())) {
                start = node;
            }
            if (node.getName().equals(nodeEnd.getValue())) {
                end = node;
            }
        }

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
