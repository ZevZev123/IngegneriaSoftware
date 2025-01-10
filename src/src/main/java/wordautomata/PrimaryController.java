package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PrimaryController {
    @FXML private VBox nodeEdgeList;
    @FXML private VBox GraphViewBox;
    @FXML private Label statusLabel;

    @FXML private Button runButton;
    @FXML private Label history;

    private List<Group> nodeList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();

    @FXML
    private void initialize(){
        updateToolTip();

        Pane graphPane = new Pane(); // creazione del foglio

        nodeList.add(createNode(50, 50, 15, "F"));
        nodeList.add(createNode(150, 150, 15, "A"));
        nodeList.add(createNode(150, 200, 15 , "B"));
        nodeList.add(createNode(150, 278, 15, "C"));
        nodeList.add(createNode(200, 278, 15, "O"));
        
        lineList.add(new Line(50, 50, 150, 150));

        for (Line edge: lineList) {
            graphPane.getChildren().add(edge); // aggiunta di tutti gli edge nel foglio
        }

        for (Group node: nodeList) {
            graphPane.getChildren().add(node); // aggiunta di tutti i nodi nel foglio
        }

        GraphViewBox.getChildren().add(graphPane); // aggiunta del foglio nella VBox

        // CREAZIONE LISTA DI NODI E EDGE
        Label label;
        for (Group node: nodeList) {
            label = new Label(getNodeText(node));
            label.setTextAlignment(TextAlignment.LEFT);
            label.setStyle("-fx-min-height: 30px; -fx-border-color: gray; -fx-border-size: 1px;");
            label.setMaxWidth(Double.MAX_VALUE);
            label.setPadding(new Insets(0, 0, 0, 10));
            
            VBox.setVgrow(label, Priority.ALWAYS);
            nodeEdgeList.getChildren().add(label);
        }
    }

    @FXML
    private void changeIcon(){ // metodo per il pulsante di RUN
        System.out.println(runButton.getStyleClass());
        if (runButton.getStyleClass().contains("RunButton")) {
            runButton.getStyleClass().setAll("button", "loadingButton"); 
        } else {
            runButton.getStyleClass().setAll("button", "RunButton");
        }
    }

    private Group createNode(double x, double y, double radius, String name){ // creazione Nodo con Testo inseriti in un Group
        Circle circle = new Circle(x, y, radius, Color.WHITE);

        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        Text text = new Text(x, y, name);
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        text.setX(x - text.getLayoutBounds().getWidth() / 2);
        text.setY(y + text.getLayoutBounds().getHeight() / 4);
        
        Group node = new Group(circle, text);
        
        node.setOnMouseEntered(event -> node.setCursor(javafx.scene.Cursor.HAND));

        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                setNodeStart(circle, event.getButton());
            }
        });
        
        return node;
    }

    private void setNodeStart(Circle circle, MouseButton command) { // metodo per nodo Start e End
        if (command == javafx.scene.input.MouseButton.PRIMARY){
            if (!circle.getStyleClass().contains("Start")) {
                circle.getStyleClass().remove("End");
                circle.getStyleClass().add("Start");
                circle.setFill(Color.YELLOW);
            } else {
                circle.getStyleClass().remove("Start");
                circle.setFill(Color.WHITE);
            }
        }
        else if (command == javafx.scene.input.MouseButton.SECONDARY){
            if (!circle.getStyleClass().contains("End")){
                circle.getStyleClass().remove("Start");
                circle.getStyleClass().add("End");
                circle.setFill(Color.GREEN);
            } else {
                circle.getStyleClass().remove("End");
                circle.setFill(Color.WHITE);
            }
        }
    }

    private double[] getNodeCoordinates(Group node) { // metodo per avere le coordinate di un nodo
        for (var child : node.getChildren()) {
            if (child instanceof Circle) {
                Circle circle = (Circle) child;
                return new double[]{circle.getCenterX(), circle.getCenterY()};
            }
        }
        return null;
    }

    private String getNodeText(Group node) { // restituisce il nome di un nodo
        for (var child : node.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                return textNode.getText();
            }
        }
        return null;
    }

    private void setNodeText(Group node, String newText) { // cambia il nome di un nodo
        for (var child: node.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                textNode.setText(newText);
                break;
            }
        }
    }

    private void updateToolTip(){ // mostra l'history completa passando con il cursore sopra
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
