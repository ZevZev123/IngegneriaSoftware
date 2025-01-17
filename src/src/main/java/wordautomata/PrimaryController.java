package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class PrimaryController {
    @FXML private VBox nodeEdgeList;
    @FXML private VBox edgeList;
    @FXML private VBox GraphViewBox;
    @FXML private Label statusLabel;

    @FXML private Button runButton;
    @FXML private Label history;

    private List<NodeFX> nodeList = new ArrayList<>();
    // private List<Line> lineList = new ArrayList<>();

    private Pane graphPane;
    private double paneWidth = 0;
    private double paneHeight = 0;

    @FXML
    private void initialize() {
        updateToolTip();

        graphPane = new Pane(); // creazione del foglio

        // creazione nodi con doppio click del mouse sul foglio
        graphPane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getTarget() == graphPane) {
                // creazione nodo
                NodeFX node = new NodeFX(event.getX(), event.getY(), 15, "0");
                nodeList.add(node);

                // mostra graficamente il nodo nel Pane
                graphPane.getChildren().add(node.getGroup());
                paneWidth = GraphViewBox.getWidth();
                paneHeight = GraphViewBox.getHeight();
                reposition();
            }
        });

        nodeList.add(new NodeFX(0, 0, 15, "F"));
        nodeList.add(new NodeFX(0, 0, 15, "A"));
        nodeList.add(new NodeFX(0, 0, 15, "B"));
        nodeList.add(new NodeFX(0, 0, 15, "C"));
        nodeList.add(new NodeFX(0, 0, 15, "D"));
        nodeList.add(new NodeFX(0, 0, 15, "E"));
        nodeList.add(new NodeFX(0, 0, 15, "T"));
        
        // lineList.add(new Line(50, 50, 150, 150));

        // for (Line edge: lineList) {
        //     graphPane.getChildren().add(edge); // aggiunta di tutti gli edge nel foglio
        // }

        for (NodeFX node: nodeList) {
            graphPane.getChildren().add(node.getGroup()); // aggiunta di tutti i nodi nel foglio
            nodeEdgeList.getChildren().add(node.getStackPane());
            node.setListFX(nodeList);
        }

        GraphViewBox.getChildren().add(graphPane); // aggiunta del foglio nella VBox
        VBox.setVgrow(graphPane, javafx.scene.layout.Priority.ALWAYS);

        // Codice eseguito al termine della creazione della finestra
        // Serve per poter prendere le grandezze della GraphViewBox e organizzare i nodi
        Platform.runLater(() -> {
            paneWidth = GraphViewBox.getWidth();
            paneHeight = GraphViewBox.getHeight();
            reposition();
        });
        
        // I SEGUENTI LISTENER SONO PER ORGANIZZARE I NODI QUANDO VIENE RIDIMENSIONATA LA FINESTRA
        graphPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            paneWidth = (double) newValue;
            reposition();
        });
        
        graphPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            paneHeight = (double) newValue;
            reposition();
        });
    }

    @FXML
    private void changeIcon() { // metodo per il pulsante di RUN
        System.out.println(runButton.getStyleClass());
        if (runButton.getStyleClass().contains("RunButton")) {
            runButton.getStyleClass().setAll("button", "loadingButton"); 
        } else {
            runButton.getStyleClass().setAll("button", "RunButton");
        }
        reposition();
    }

    @FXML
    private void deleteAll() {
        
    }

    private void delete() {

    }

    private void reposition() {
        int nodeListLength = nodeList.size();
        double angleNode = 0;
        if (nodeListLength != 0){
            angleNode = 360 / nodeListLength;
        }

        double count = 0;
        double x = 0, y = 0;
        for (NodeFX node: nodeList) {
            x = (paneWidth/2) + (paneWidth/4)*cos(toRadians(count));
            y = (paneHeight/2) + (paneHeight/4)*sin(toRadians(count));

            node.changeCoordinates(x, y);
            count = count - angleNode;
        }
                
    }

    private void updateToolTip() { // mostra l'history completa passando con il cursore sopra
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
