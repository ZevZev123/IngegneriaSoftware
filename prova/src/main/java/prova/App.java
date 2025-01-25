package prova;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    private EdgeFX edge;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #f0f0f0;");

        NodeFX node = new NodeFX(100, 100, 20, "A", this);
        NodeFX node2 = new NodeFX(300, 200, 20, "B", this);

        edge = new EdgeFX(node2, node);
        graphPane.getChildren().addAll(node.getGroup(), node2.getGroup(), edge.getGroup());

        root.setCenter(graphPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("GraphPane Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void move() {
        edge.coordinatesChanged();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
