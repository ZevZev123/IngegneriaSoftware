package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class PrimaryController {
    @FXML private VBox nodeMenuList;
    @FXML private VBox edgeMenuList;
    @FXML private VBox GraphViewBox;
    @FXML private Label statusLabel;

    @FXML private Button runButton;
    @FXML private Label history;

    private List<NodeFX> nodeList = new ArrayList<>();
    private List<EdgeFX> edgeList = new ArrayList<>();

    private Pane graphPane;
    private double paneWidth = 0;
    private double paneHeight = 0;

    private double contextX = 0, contextY = 0;

    @FXML
    private void initialize() {
        updateToolTip();

        graphPane = new Pane(); // creazione del foglio
        
        // creazione menu contestuale
        ContextMenu contextMenu = new ContextMenu();
        
        // creazione nodi con doppio click del mouse sul foglio
        graphPane.setOnMouseClicked(event -> {
            contextMenu.hide();
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 2 && event.getTarget() == graphPane) {
                // creazione nodo
                NodeFX node = new NodeFX(event.getX(), event.getY(), 15, "0", this);
                node.setListFX(nodeList);
                nodeList.add(node);
                // mostra graficamente il nodo nel Pane
                graphPane.getChildren().add(node.getGroup());
                paneWidth = GraphViewBox.getWidth();
                paneHeight = GraphViewBox.getHeight();

                nodeMenuList.getChildren().add(node.getStackPane());
            }
        });

        // creazione pulsanti menu contestuale
        MenuItem newNode = new MenuItem("Nuovo nodo");
        newNode.setOnAction(event -> {
            // creazione nodo
            NodeFX node = new NodeFX(contextX, contextY, 15, "0", this);
            node.setListFX(nodeList);
            nodeList.add(node);
            // mostra graficamente il nodo nel Pane
            graphPane.getChildren().add(node.getGroup());
            paneWidth = GraphViewBox.getWidth();
            paneHeight = GraphViewBox.getHeight();

            nodeMenuList.getChildren().add(node.getStackPane());
        });
        MenuItem newEdge = new MenuItem("Nuovo edge");
        newEdge.setOnAction(event -> {
            System.out.println("Nuovo edge");
        });

        contextMenu.getItems().addAll(newNode, newEdge);

        graphPane.setOnContextMenuRequested(event -> {
            if (event.getTarget() == graphPane) {
                contextX = event.getX();
                contextY = event.getY();
                contextMenu.show(graphPane, event.getScreenX(), event.getScreenY());
            }
        });

        ContextMenu contextMenuNodi;

        
        nodeList.add(new NodeFX(0, 0, 15, "F", this));
        nodeList.add(new NodeFX(0, 0, 15, "A", this));
        nodeList.add(new NodeFX(0, 0, 15, "B", this));
        nodeList.add(new NodeFX(0, 0, 15, "C", this));
        nodeList.add(new NodeFX(0, 0, 15, "D", this));
        nodeList.add(new NodeFX(0, 0, 15, "E", this));
        nodeList.add(new NodeFX(0, 0, 15, "T", this));
        
        edgeList.add(new EdgeFX(nodeList.get(0), nodeList.get(1), "provolone"));
        edgeList.add(new EdgeFX(nodeList.get(1), nodeList.get(2), "bc"));
        edgeList.add(new EdgeFX(nodeList.get(1), nodeList.get(3), "av"));
        edgeList.add(new EdgeFX(nodeList.get(2), nodeList.get(3), "ac"));
        edgeList.add(new EdgeFX(nodeList.get(3), nodeList.get(0), "ac"));
        edgeList.add(new EdgeFX(nodeList.get(5), nodeList.get(4), "ac"));
        edgeList.add(new EdgeFX(nodeList.get(5), nodeList.get(4), "ac"));
        edgeList.add(new EdgeFX(nodeList.get(6), nodeList.get(2), "principessa"));

        for (NodeFX node: nodeList) {
            graphPane.getChildren().add(node.getGroup()); // aggiunta di tutti i nodi nel foglio
            nodeMenuList.getChildren().add(node.getStackPane());
            node.setListFX(nodeList);

            contextMenuNodi = new ContextMenu();
            MenuItem delete = new MenuItem("Cancella Nodo");
            delete.setOnAction(event -> {node.deleteNode();});    
            contextMenu.getItems().add(delete);

            node.getGroup().setOnContextMenuRequested(event -> {
                contextMenuNodi.show(graphPane, event.getScreenX(), event.getScreenY());
            });
        }
        
        for (EdgeFX edge: edgeList) {
            graphPane.getChildren().add(edge.getGroup()); // aggiunta di tutti gli edge nel foglio
            edgeMenuList.getChildren().add(edge.getStackPane());
            edge.setEdgeList(edgeList);
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
    }

    @FXML
    private void deleteAll() {
        System.out.println("Cancellato tutto il grafo");
        int val = nodeList.size();
        for (int i = 0; i < val; i++){
            nodeList.get(0).deleteNode();
        }
    }

    public void delete(NodeFX node) {
        System.out.println("Node cancellato");
        for (int i = 0; i < edgeList.size(); i++) {
            for (NodeFX edgesNode: edgeList.get(i).getNodes()) {
                if (node.equals(edgesNode)) {
                    edgeList.get(i).deleteEdge();
                    i--;
                }
            }
        }
        coordinatesChanged();
    }

    public void coordinatesChanged() {
        for (EdgeFX edge: edgeList) {
            edge.updateEdge();
        }
    }

    public void newEdge(NodeFX node) {
        System.out.println(node);
    }

    @FXML
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
        for (EdgeFX edge: edgeList) {
            edge.updateEdge();
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
