package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class MainPageController {
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

    private NodeFX selectedNode = null;
    private Stage secondStage;
    private Stage thirdStage;
    private NewNodeController secondaryController;

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
                if (event.isShiftDown()) createEdge();
                else createNode(event.getX(), event.getY());
            }
        });

        // creazione pulsanti menu contestuale
        MenuItem newNode = new MenuItem("Nuovo nodo");
        newNode.setOnAction(event -> { createNode(contextX, contextY); });

        MenuItem newEdge = new MenuItem("Nuovo edge");
        newEdge.setOnAction(event -> {
            this.selectedNode = null;
            createEdge();
        });

        this.contextMenu.getItems().addAll(newNode, newEdge);

        graphPane.setOnContextMenuRequested(event -> {
            if (event.getTarget() == graphPane) {
                contextX = event.getX();
                contextY = event.getY();
                this.contextMenu.show(graphPane, event.getScreenX(), event.getScreenY());
            }
        });
        
        createNode(0, 0, "F", true, false);
        createNode(0, 0, "A", false, false);
        createNode(0, 0, "B", false, true);
        createNode(0, 0, "C", false, false);
        createNode(0, 0, "D", false, false);
        createNode(0, 0, "E", false, false);
        createNode(0, 0, "T", false, false);

        createEdge(nodeList.get(0), nodeList.get(1), "provolone", 425, 235);
        createEdge(nodeList.get(1), nodeList.get(2), "bc", 329, 150);
        createEdge(nodeList.get(1), nodeList.get(3), "av", 291, 233);
        createEdge(nodeList.get(2), nodeList.get(3), "ac", 217, 166);
        createEdge(nodeList.get(3), nodeList.get(0), "ad", 274, 328);
        createEdge(nodeList.get(5), nodeList.get(4), "ae", 240, 325);
        createEdge(nodeList.get(5), nodeList.get(4), "af", 183, 394);
        createEdge(nodeList.get(6), nodeList.get(2), "principessa", 324, 262);

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

    private void updateToolTip() { // mostra l'history completa passando con il cursore sopra
        Tooltip tooltip = new Tooltip(history.getText());
        history.setTooltip(tooltip);
    }

    private void createEdge() {
        if (thirdStage == null || !thirdStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("tertiary.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/wordautomata/style2.css").toExternalForm());

                NewEdgeController tertiaryController = loader.getController();
                tertiaryController.setPrimaryController(this);
                tertiaryController.setNodeList(nodeList);
                if (this.selectedNode != null) {
                    tertiaryController.setStartNode(this.selectedNode);
                }

                thirdStage = new Stage();
                thirdStage.setTitle("Creazione nuovo edge");
                thirdStage.setScene(scene);

                thirdStage.setMinHeight(200);
                thirdStage.setMinWidth(320);
                thirdStage.setMaxHeight(200);
                thirdStage.setMaxWidth(320);

                thirdStage.setOnCloseRequest(event -> {thirdStage = null;});
                thirdStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            thirdStage.toFront();
        }
    }

    private void createNode(double positionX, double positionY) {
        if (secondStage == null || !secondStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/wordautomata/style2.css").toExternalForm());

                this.secondaryController = loader.getController();
                secondaryController.setPrimaryController(this);
                secondaryController.setNodeList(nodeList);
                secondaryController.setPositionX(positionX);
                secondaryController.setPositionY(positionY);

                secondStage = new Stage();
                secondStage.setTitle("Creazione nuovo nodo");
                secondStage.setScene(scene);

                secondStage.setMinHeight(140);
                secondStage.setMinWidth(320);
                secondStage.setMaxHeight(140);
                secondStage.setMaxWidth(320);

                secondStage.setOnCloseRequest(event -> {this.secondStage = null; this.secondaryController = null;});
                secondStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            secondStage.toFront();
            secondaryController.setPositionX(positionX);
            secondaryController.setPositionY(positionY);
        }
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
        MenuItem rename = new MenuItem("Rinomina Nodo");
        rename.setOnAction(event -> { node.textFieldRename(); });
        MenuItem newEdge = new MenuItem("Crea nuovo edge");
        newEdge.setOnAction(event -> {
            this.selectedNode = node;
            createEdge();
        });
        contextMenuNodi.getItems().addAll(delete, rename, newEdge);
        
        node.getGroup().setOnContextMenuRequested(event -> {
            contextMenuNodi.show(graphPane, event.getScreenX(), event.getScreenY());
        });
        
        node.setContextMenuNodiList(contextMenuNodi);
        contextMenuNodiList.add(contextMenuNodi);
    }

    public void createNode(double positionX, double positionY, String name, Boolean isInitial, Boolean isFinal) {
        if (isInitial && !isThereInitial() || !isInitial && !(isInitial && isFinal)) {
            Boolean nameAlreadyExist = false;
            for (NodeFX node: nodeList) {
                if (node.getName().equals(name)) {
                    nameAlreadyExist = true;
                    break;
                }
            }

            if (!nameAlreadyExist) {
                NodeFX node = new NodeFX(positionX, positionY, name, this, isInitial, isFinal);
                createNode(node);
            } else { System.out.println("Nome esistente"); }
        } else {
            System.out.println("Errore");
        }
    }
    
    private void createEdge(NodeFX start, NodeFX end, String name, double positionX, double positionY) {
        EdgeFX edge = new EdgeFX(start, end, name, positionX, positionY);
        edgeList.add(edge);

        graphPane.getChildren().add(edge.getGroup()); // aggiunta di tutti gli edge nel foglio
        edgeMenuList.getChildren().add(edge.getStackPane());
        edge.setEdgeList(edgeList);
    }

    public void createEdge(NodeFX start, NodeFX end, String name) {
        double meanWidth = end.getX() + ((start.getX() - end.getX()) / 2);
        double meanHeight = end.getY() + ((start.getY() - end.getY()) / 2);
        if (meanWidth < 0) meanWidth = -meanWidth;
        if (meanHeight < 0) meanHeight = -meanHeight;
        createEdge(start, end, name, meanWidth, meanHeight);
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

    public Boolean isThereInitial() {
        for (NodeFX node: this.nodeList)
            if (node.isNodeInitial())
                return true;
            
        return false;
    }

    public void coordinatesChanged() { for (EdgeFX edge: edgeList) edge.updateEdge(); }
    public void newEdge(NodeFX node) { System.out.println(node); }

    public List<NodeFX> getNodeList() { return this.nodeList; }
}
