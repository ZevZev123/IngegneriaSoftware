package prova;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #f0f0f0;");

        NodeFX node = new NodeFX(100, 100, 20, "A");
        NodeFX node2 = new NodeFX(300, 200, 20, "B");

        EdgeFX edge = new EdgeFX(node2, node, 0);
        EdgeFX edge2 = new EdgeFX(node, node2, 50);
        EdgeFX edge3 = new EdgeFX(node, node, -50);
        graphPane.getChildren().addAll(node.getGroup(), node2.getGroup(), edge, edge2, edge3);

        root.setCenter(graphPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("GraphPane Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
