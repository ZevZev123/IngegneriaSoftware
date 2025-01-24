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

        NodeFX node = new NodeFX(100, 100, 15, "A");

        EdgeFX edge = new EdgeFX();
        graphPane.getChildren().addAll(node.getGroup(), edge);

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
