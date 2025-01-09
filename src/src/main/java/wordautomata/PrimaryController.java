package wordautomata;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.text.TextAlignment;

public class PrimaryController {
    @FXML private VBox nodeEdgeList;
    @FXML private VBox GraphViewBox;
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

        // Creazione di un grafico semplice per esempio
        Pane graphPane = new Pane(); // Contenitore per il grafico

        // Aggiungi nodi
        Group node1 = createNode(50, 50, 15, "CIAOOO",  Color.BLUE); // Nodo 1
        Circle node2 = new Circle(150, 150, 15, Color.RED); // Nodo 2
        Circle node3 = new Circle(150, 200, 15, Color.PURPLE); // Nodo 2

        // Aggiungi arco
        Line edge = new Line(50, 50, 150, 150);

        // Aggiungi i componenti al contenitore del grafo
        graphPane.getChildren().addAll(edge, node1, node2, node3);

        // Aggiungi il contenitore del grafo alla VBox
        GraphViewBox.getChildren().add(graphPane);
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

    private Group createNode(double x, double y, double radius, String name, Color color){
        // Crea il cerchio
        Circle circle = new Circle(x, y, radius, color);

        // Crea il testo
        Text text = new Text(x, y, name);
        text.setFill(Color.WHITE); // Colore del testo
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        // Centra il testo nel cerchio
        text.setX(x - text.getLayoutBounds().getWidth() / 2);
        text.setY(y + text.getLayoutBounds().getHeight() / 4);

        Group node = new Group(circle, text);

        return node;
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
