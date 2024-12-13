import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.util.HashMap;
import java.util.Map;

public class GraphEditor extends Application {

    private Map<String, GraphNode> nodes = new HashMap<>();
    private Pane graphPane;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Directed Graph Editor");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        // Create UI components
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));

        TextField nodeNameField = new TextField();
        nodeNameField.setPromptText("Node Name");

        Button addNodeButton = new Button("Add Node");
        Button removeNodeButton = new Button("Remove Node");

        TextField edgeStartField = new TextField();
        edgeStartField.setPromptText("Edge Start Node");

        TextField edgeEndField = new TextField();
        edgeEndField.setPromptText("Edge End Node");

        TextField edgeValueField = new TextField();
        edgeValueField.setPromptText("Edge Value");

        Button addEdgeButton = new Button("Add Edge");
        Button removeEdgeButton = new Button("Remove Edge");

        HBox nodeButtons = new HBox(10);
        nodeButtons.setPadding(new Insets(10));
        nodeButtons.getChildren().addAll(addNodeButton, removeNodeButton);

        HBox edgeButtons = new HBox(10);
        edgeButtons.setPadding(new Insets(10));
        edgeButtons.getChildren().addAll(addEdgeButton, removeEdgeButton);

        controls.getChildren().addAll(
            new Label("Node"), nodeNameField, nodeButtons,
            new Label("Edge"), edgeStartField, edgeEndField, edgeValueField, edgeButtons
        );

        root.setLeft(controls);

        graphPane = new Pane();
        root.setCenter(graphPane);

        // Event handlers
        addNodeButton.setOnAction(e -> {
            String nodeName = nodeNameField.getText();
            if (!nodeName.isEmpty() && !nodes.containsKey(nodeName)) {
                GraphNode node = new GraphNode(nodeName, Math.random() * 600 + 100, Math.random() * 400 + 100);
                nodes.put(nodeName, node);
                graphPane.getChildren().addAll(node.getCircle(), node.getText());
                nodeNameField.clear();
            }
        });
        /*
        removeNodeButton.setOnAction(e -> {
            String nodeName = nodeNameField.getText();
            if (!nodeName.isEmpty() && nodes.containsKey(nodeName)) {
                GraphNode node = nodes.remove(nodeName);
                graphPane.getChildren().removeAll(node.getCircle(), node.getText());
                nodeNameField.clear();
            }
        });
        */

        addEdgeButton.setOnAction(e -> {
            String startNode = edgeStartField.getText();
            String endNode = edgeEndField.getText();
            String edgeValue = edgeValueField.getText();
            if (!startNode.isEmpty() && !endNode.isEmpty() && !edgeValue.isEmpty() &&
                nodes.containsKey(startNode) && nodes.containsKey(endNode)) {
                GraphEdge edge = new GraphEdge(nodes.get(startNode), nodes.get(endNode), edgeValue);
                graphPane.getChildren().addAll(edge.getLine(), edge.getValueText());
                edgeStartField.clear();
                edgeEndField.clear();
                edgeValueField.clear();
            }
        });
        /*
        removeEdgeButton.setOnAction(e -> {
            String startNode = edgeStartField.getText();
            String endNode = edgeEndField.getText();
            if (!startNode.isEmpty() && !endNode.isEmpty() &&
                nodes.containsKey(startNode) && nodes.containsKey(endNode)) {
                GraphNode start = nodes.get(startNode);
                GraphNode end = nodes.get(endNode);
                graphPane.getChildren().removeIf(node -> {
                    if (node instanceof Line) {
                        Line line = (Line) node;
                        return (line.getStartX() == start.getCircle().getCenterX() && line.getStartY() == start.getCircle().getCenterY() &&
                                line.getEndX() == end.getCircle().getCenterX() && line.getEndY() == end.getCircle().getCenterY()) ||
                               (line.getStartX() == end.getCircle().getCenterX() && line.getStartY() == end.getCircle().getCenterY() &&
                                line.getEndX() == start.getCircle().getCenterX() && line.getEndY() == start.getCircle().getCenterY());
                    }
                    return false;
                });
                edgeStartField.clear();
                edgeEndField.clear();
            }
        });
        */

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}