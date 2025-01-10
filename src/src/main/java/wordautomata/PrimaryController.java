package wordautomata;

import java.util.ArrayList;
import java.util.List;

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

    private List<Group> nodeList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();

    @FXML
    private void initialize(){
        updateToolTip();

        Pane graphPane = new Pane();

        nodeList.add(createNode(50, 50, 15, "F"));
        nodeList.add(createNode(150, 150, 15, "A"));
        nodeList.add(createNode(150, 200, 15 , "B"));
        nodeList.add(createNode(150, 278, 15, "C"));
        nodeList.add(createNode(200, 278, 15, "O"));
        
        lineList.add(new Line(50, 50, 150, 150));

        for (Line edge: lineList) {
            graphPane.getChildren().add(edge);
        }

        for (Group node: nodeList) {
            graphPane.getChildren().add(node);
        }

        GraphViewBox.getChildren().add(graphPane);

        Label label;
        for (Group node: nodeList) {
            label = new Label(getNodeText(node));
            label.setTextAlignment(TextAlignment.LEFT);
            nodeEdgeList.getChildren().add(label);
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

    private Group createNode(double x, double y, double radius, String name){
        // Crea il cerchio
        Circle circle = new Circle(x, y, radius, Color.WHITE);

        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // Crea il testo
        Text text = new Text(x, y, name);
        text.setFill(Color.BLACK); // Colore del testo
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        // Centra il testo nel cerchio
        text.setX(x - text.getLayoutBounds().getWidth() / 2);
        text.setY(y + text.getLayoutBounds().getHeight() / 4);

        Group node = new Group(circle, text);

        return node;
    }

    public static double[] getNodeCoordinates(Group node) {
        for (var child : node.getChildren()) {
            if (child instanceof Circle) {
                Circle circle = (Circle) child;
                return new double[]{circle.getCenterX(), circle.getCenterY()};
            }
        }
        return null;
    }

    private String getNodeText(Group node) {
        for (var child : node.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                return textNode.getText();
            }
        }
        return null;
    }

    private void setNodeText(Group node, String newText) {
        for (var child: node.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                textNode.setText(newText);
                break;
            }
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
