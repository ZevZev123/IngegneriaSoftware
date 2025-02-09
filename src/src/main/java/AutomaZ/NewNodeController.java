package AutomaZ;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class NewNodeController {
    @FXML private TextField textField;
    @FXML private Label errorLabel;

    private MainPageController controller;

    private ArrayList<Node> nodeList = new ArrayList<>();
    private double positionX, positionY;

    @FXML
    private void initialize() {
        // Chiude la finestra con la pressione del tasto ESC
        textField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null)
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE)
                        closePage();
                });
        });
    }

    @FXML
    private void newNode() {
        String nomeNodo = textField.getText();

        if (controller != null) {
            if (!nomeNodo.isEmpty()) {
                Boolean nameAlreadyExist = false;
                for (Node node: nodeList)
                    if (node.getName().equals(nomeNodo)) {
                        nameAlreadyExist = true;
                        break;
                    }

                if (!nameAlreadyExist) {
                    controller.createNode(positionX, positionY, nomeNodo, false, false);
                    closePage();
                } else {
                    errorLabel.setText("Nome gia' esistente!");
                    errorLabel.setTextFill(Color.RED);
                }
            } else {
                errorLabel.setText("Nome vuoto!");
                errorLabel.setTextFill(Color.RED);
            }
        }
    }

    @FXML
    private void closePage() {
        Stage stage = (Stage) this.textField.getScene().getWindow();
        stage.close();
    }

    public void setPrimaryController(MainPageController controller) { this.controller = controller; }
    public void setPositionX(double positionX) { this.positionX = positionX; }
    public void setPositionY(double positionY) { this.positionY = positionY; }
    public void setNodeList(ArrayList<Node> nodeList) { this.nodeList = nodeList; }
}
