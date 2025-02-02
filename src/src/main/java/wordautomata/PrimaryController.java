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

    private ContextMenu contextMenu;
    private List<ContextMenu> contextMenuNodiList = new ArrayList<>();
    private double contextX = 0, contextY = 0;

    @FXML
    private void initialize() {
        updateToolTip();

        graphPane = new Pane(); // creazione del foglio
        
        // creazione menu contestuale
        this.contextMenu = new ContextMenu();
        
        // creazione nodi con doppio click del mouse sul foglio
        graphPane.setOnMouseClicked(event -> {
            this.contextMenu.hide();
            for (int i = 0; i < contextMenuNodiList.size(); i++){
                contextMenuNodiList.get(i).hide();
            }
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 2 && event.getTarget() == graphPane) {
                createNode(event.getX(), event.getY(), "0");
            }
        });

        // creazione pulsanti menu contestuale
        MenuItem newNode = new MenuItem("Nuovo nodo");
        newNode.setOnAction(event -> {
            createNode(contextX, contextY, "0");
        });

        MenuItem newEdge = new MenuItem("Nuovo edge");
        newEdge.setOnAction(event -> {
            System.out.println("Nuovo edge");
        });

        this.contextMenu.getItems().addAll(newNode, newEdge);

        graphPane.setOnContextMenuRequested(event -> {
            if (event.getTarget() == graphPane) {
                contextX = event.getX();
                contextY = event.getY();
                this.contextMenu.show(graphPane, event.getScreenX(), event.getScreenY());
            }
        });
        
        createNode(0, 0, "F");
        createNode(0, 0, "A");
        createNode(0, 0, "B");
        createNode(0, 0, "C");
        createNode(0, 0, "D");
        createNode(0, 0, "E");
        createNode(0, 0, "T");

        edgeList.add(new EdgeFX(nodeList.get(0), nodeList.get(1), "provolone", 425, 235));
        edgeList.add(new EdgeFX(nodeList.get(1), nodeList.get(2), "bc", 329, 150));
        edgeList.add(new EdgeFX(nodeList.get(1), nodeList.get(3), "av", 291, 233));
        edgeList.add(new EdgeFX(nodeList.get(2), nodeList.get(3), "ac", 217, 166));
        edgeList.add(new EdgeFX(nodeList.get(3), nodeList.get(0), "ac", 274, 328));
        edgeList.add(new EdgeFX(nodeList.get(5), nodeList.get(4), "ac", 240, 325));
        edgeList.add(new EdgeFX(nodeList.get(5), nodeList.get(4), "ac", 183, 394));
        edgeList.add(new EdgeFX(nodeList.get(6), nodeList.get(2), "principessa", 324, 262));
        
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
        // graphPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        //     paneWidth = (double) newValue;
        //     reposition();
        // });
        
        // graphPane.heightProperty().addListener((observable, oldValue, newValue) -> {
        //     paneHeight = (double) newValue;
        //     reposition();
        // });
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

    @FXML
    private void reposition() {
        int nodeListLength = nodeList.size();
        if (nodeListLength != 0){
            double angleNode = 0;
            angleNode = 360 / nodeListLength;
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
    }

    private void createNode(double positionX, double positionY, String name) {
        NodeFX node = new NodeFX(positionX, positionY, 15, name, this);
        createNode(node);
    }

    private void createNode(double positionX, double positionY) {
        // Seconda pagina per chiedere il nome
        // ...
    
        NodeFX node = new NodeFX(positionX, positionY, 15, "0", this);
        createNode(node);
    }

    private void createNode(NodeFX node) {
        nodeList.add(node);
        node.setListFX(nodeList);

        graphPane.getChildren().add(node.getGroup());
        paneWidth = GraphViewBox.getWidth();
        paneHeight = GraphViewBox.getHeight();

        nodeMenuList.getChildren().add(node.getStackPane());

        ContextMenu contextMenuNodi = new ContextMenu();
        MenuItem delete = new MenuItem("Cancella Nodo "+node.getName());
        delete.setOnAction(event -> {node.deleteNode();});
        contextMenuNodi.getItems().add(delete);

        node.getGroup().setOnContextMenuRequested(event -> {
            contextMenuNodi.show(graphPane, event.getScreenX(), event.getScreenY());
        });

        contextMenuNodiList.add(new ContextMenu());
    }

    private void updateToolTip() { // mostra l'history completa passando con il cursore sopra
        Tooltip tooltip = new Tooltip(history.getText());
        history.setTooltip(tooltip);
    }

    public void delete(NodeFX node) {
        System.out.println("Node cancellato");
        for (int i = 0; i < edgeList.size(); i++) {
            for (NodeFX edgesNode: edgeList.get(i).getNodes()) {
                if (node.equals(edgesNode)) {
                    edgeList.get(i).deleteEdge();
                    contextMenuNodiList.get(i).hide();
                    contextMenuNodiList.remove(i);
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

    /*
    // in caso di aggiunta seconda pagina:
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    */
}
