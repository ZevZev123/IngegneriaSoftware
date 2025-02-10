package AutomaZ;

import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.IOException;

public class MainPageController {
    @FXML private VBox nodeMenuList;
    @FXML private VBox edgeMenuList;
    @FXML private VBox GraphViewBox;
    @FXML private VBox history;

    @FXML private Button runButton;
    @FXML private TextField textField;
    
    private ArrayList<Node> nodeList = new ArrayList<>();
    private ArrayList<Edge> edgeList = new ArrayList<>();
    
    FileManager fileManager = null;
    String fileName = null;

    private Pane graphPane;
    private double paneWidth = 0;
    private double paneHeight = 0;

    private ContextMenu contextMenu;
    private ArrayList<ContextMenu> contextMenuNodiList = new ArrayList<>();
    private double contextX = 0, contextY = 0;

    private Node selectedNode = null;
    private Stage secondStage;
    private Stage thirdStage;
    private NewNodeController secondaryController;

    @FXML
    private void initialize() throws IOException {
        // updateToolTip();

        graphPane = new Pane(); // creazione del foglio
        
        // creazione menu contestuale
        this.contextMenu = new ContextMenu();
        
        // creazione nodi con doppio click del mouse sul foglio
        graphPane.setOnMouseClicked(event -> {
            this.contextMenu.hide();
            for (int i = 0; i < contextMenuNodiList.size(); i++)
                contextMenuNodiList.get(i).hide();
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
        
        // openFile(); // da togliere tutto il resto, non funziona ancora
        createNode(0, 0, "F", true, false);
        createNode(0, 0, "A", false, false);
        createNode(0, 0, "B", false, true);
        createNode(0, 0, "C", false, false);
        createNode(0, 0, "D", false, false);
        createNode(0, 0, "E", false, false);
        createNode(0, 0, "T", false, false);

        createEdge(nodeList.get(0), nodeList.get(1), "ab", 425, 235);
        createEdge(nodeList.get(1), nodeList.get(2), "bc", 329, 150);
        createEdge(nodeList.get(1), nodeList.get(3), "av", 291, 233);
        createEdge(nodeList.get(2), nodeList.get(3), "ac", 217, 166);
        createEdge(nodeList.get(3), nodeList.get(0), "ad", 274, 328);
        createEdge(nodeList.get(5), nodeList.get(4), "ae", 240, 325);
        createEdge(nodeList.get(5), nodeList.get(4), "af", 183, 394);
        createEdge(nodeList.get(6), nodeList.get(2), "principessa", 324, 262);
        createEdge(nodeList.get(1), nodeList.get(1), "lalala", 400, 150);

        GraphViewBox.getChildren().add(graphPane); // aggiunta del foglio nella VBox
        VBox.setVgrow(graphPane, javafx.scene.layout.Priority.ALWAYS);

        // Codice eseguito al termine della creazione della finestra
        // Serve per poter prendere le grandezze della GraphViewBox e organizzare i nodi
        Platform.runLater(() -> {
            reposition();
        });
        
        // I SEGUENTI LISTENER SONO PER ORGANIZZARE I NODI QUANDO VIENE RIDIMENSIONATA LA FINESTRA
        // graphPane.widthProperty().addListener((observable, oldValue, newValue) -> { reposition(); });
        // graphPane.heightProperty().addListener((observable, oldValue, newValue) -> { reposition(); });
    }

    @FXML
    private void changeIcon() { // metodo per il pulsante di RUN
        // if (runButton.getStyleClass().contains("RunButton"))
        //     runButton.getStyleClass().setAll("button", "loadingButton"); 
        // else
        //     runButton.getStyleClass().setAll("button", "RunButton");

        // System.out.println("Il grafico è valido? -> "+isGraphValid());

        if (isGraphValid())
            createHistory();
    }

    @FXML
    private void deleteAll() {
        System.out.println("Cancellato tutto il grafo");
        int val = nodeList.size();
        for (int i = 0; i < val; i++)
            nodeList.get(0).deleteNode();
    }

    @FXML
    private void reposition() {
        int nodeListLength = nodeList.size();
        if (nodeListLength != 0){
            double angleNode = 0;
            angleNode = 360 / nodeListLength;
            double count = 0;
            double x = 0, y = 0;
            this.paneWidth = GraphViewBox.getWidth();
            this.paneHeight = GraphViewBox.getHeight();

            for (Node node: nodeList) {
                x = (paneWidth/2) + (paneWidth/4)*cos(toRadians(count));
                y = (paneHeight/2) + (paneHeight/4)*sin(toRadians(count));
    
                node.changeCoordinates(x, y);
                count = count - angleNode;
            }
            for (Edge edge: edgeList)
                edge.updateEdge();
        }
    }

    @FXML
    private void newFile() throws IOException {
        System.out.println("newFile");
        generateFileManager();
        if (this.fileName == null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Attenzione");
            alert.setHeaderText("Attenzione");
            alert.setContentText("Il file non e' stato salvato, creare un nuovo file?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteAll();
                this.fileName = null;
            }
        } else {
            saveFile();
        }
    }

    @FXML
    private void openFile() throws IOException {
        System.out.println("openFile");
        generateFileManager();
        if (this.fileName == null) {
            this.fileName = "prova";
        }
        nodeList.clear();
        edgeList.clear();
        // fileManager.readFromFile("prova"); // non funziona
    }
    
    @FXML
    private void saveFile() throws IOException {
        generateFileManager();
        System.out.println("saveFile");
        if (this.fileName == null) {
            this.fileName = "prova";
        }
        fileManager.writeToFile(this.fileName);
    }
    
    @FXML
    private void exit() {
        System.out.println("exit");
        generateFileManager();

    }

    private void generateFileManager() {
        if (this.fileManager == null) {
            this.fileManager = new FileManager(nodeList, edgeList);
        }
    }

    private void createHistory() {
        history.getChildren().clear();
        for (Edge edge: edgeList) edge.setColor(Color.BLACK);
        WordAutomata wordAutomata = new WordAutomata(nodeList, edgeList);
        
        if (wordAutomata.run(textField.getText())) {
            ArrayList<String> historyList = wordAutomata.getStringHistory();
            ArrayList<String> stateList = wordAutomata.getStateHistory();
            Label label;
            for (int i = 0; i < historyList.size(); i++) {
                if (!equalToLast(stateList.get(i))) {
                    label = createLableNode(stateList.get(i), true);
                    history.getChildren().add(label);
                }
                label = createLableNode(historyList.get(i), false);
                history.getChildren().add(label);
            }
            label = createLableNode(stateList.get(stateList.size()-1), true);
            history.getChildren().add(label);
            changeColor();
        }
    }

    // cambia colore agli edge percorsi
    private void changeColor() {
        String nodeName = "";
        for (int i = 0; i < history.getChildren().size(); i++) {
            if (history.getChildren().get(i) instanceof Label label) {
                if (label.getStyleClass().contains("label1")) {
                    nodeName = label.getText();
                } else {
                    for (Edge edge: edgeList) {
                        if (history.getChildren().get(i) instanceof Label label2) {
                            if (edge.getStartNode().getName().equals(nodeName) && edge.getValue().equals(label2.getText())) {
                                edge.setColor(Color.BLUE);
                            }
                        }
                    }
                }
            }
        }
    }

    // controlla se l'ultimo node inserito è uguale a quello precedente
    private Boolean equalToLast(String state) {
        for (int i = history.getChildren().size()-1; i > 0; i--) {
            if (history.getChildren().get(i).getStyleClass().contains("label1")) {
                if (history.getChildren().get(i) instanceof Label label) {
                    if (label.getText().equals(state)) {
                        return true;
                    }
                }
                break;
            }
        }

        return false;
    }

    private Label createLableNode(String name, Boolean isNode) {
        Label label = new Label();
        label.setText(name);
        label.setTextAlignment(TextAlignment.LEFT);
        if (isNode) label.getStyleClass().add("label1");
        else label.getStyleClass().add("label2");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(0, 0, 0, 10));

        return label;
    }

    private Boolean isGraphValid() {
        // controllo se esiste almeno un nodo iniziale e non piu' di uno
        // controllo se esiste almeno un nodo finale
        int initialCount = 0, finalCount = 0;
        for (Node node: nodeList) {
            if (node.isNodeInitial())
                initialCount++;
            if (node.isNodeFinal())
                finalCount++;
        }
        if (initialCount != 1 || finalCount < 1) { return false; }
    
        // controllo se ci sono nodi con lo stesso nome
        for (int i = 0; i < nodeList.size(); i++)
            for (int j = i+1; j < nodeList.size(); j++)
                if (nodeList.get(i).getName().equals(nodeList.get(j).getName()))
                    return false;

        // controllo se ci sono edge con lo stesso valore che partono dallo stesso nodo
        for (int i = 0; i < edgeList.size(); i++)
            for (int j = i+1; j < edgeList.size(); j++)
                if (edgeList.get(i).getStartNode() == edgeList.get(j).getStartNode() &&
                    edgeList.get(i).getValue().equals(edgeList.get(j).getValue()))
                    return false;

        return true;
    }

    // private void updateToolTip() {
    //     Tooltip tooltip = new Tooltip(textField.getText());
    //     textField.setTooltip(tooltip);
    // }

    private void createEdge() {
        if (thirdStage == null || !thirdStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("tertiary.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/AutomaZ/style2.css").toExternalForm());

                NewEdgeController tertiaryController = loader.getController();
                tertiaryController.setPrimaryController(this);
                tertiaryController.setNodeList(nodeList);
                if (this.selectedNode != null)
                    tertiaryController.setStartNode(this.selectedNode);

                thirdStage = new Stage();
                thirdStage.setTitle("Creazione nuovo edge");
                thirdStage.setScene(scene);

                thirdStage.setMinHeight(200);
                thirdStage.setMinWidth(320);
                thirdStage.setMaxHeight(200);
                thirdStage.setMaxWidth(320);

                thirdStage.setOnCloseRequest(event -> {thirdStage = null;});
                thirdStage.show();
            } catch (Exception e) { e.printStackTrace(); }
        } else { thirdStage.toFront(); }
    }

    private void createNode(double positionX, double positionY) {
        if (secondStage == null || !secondStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/AutomaZ/style2.css").toExternalForm());

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
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            secondStage.toFront();
            secondaryController.setPositionX(positionX);
            secondaryController.setPositionY(positionY);
        }
    }

    private void createNode(Node node) {
        nodeList.add(node);
        node.setListFX(nodeList);
        
        graphPane.getChildren().add(node.getGroup());
        paneWidth = GraphViewBox.getWidth();
        paneHeight = GraphViewBox.getHeight();
        
        nodeMenuList.getChildren().add(node.getStackPane());
        
        ContextMenu contextMenuNodi = new ContextMenu();
        MenuItem delete = new MenuItem("Cancella Nodo "+node.getName());
        delete.setOnAction(event -> {node.deleteNode();});
        MenuItem newEdge = new MenuItem("Crea nuovo edge");
        newEdge.setOnAction(event -> {
            this.selectedNode = node;
            createEdge();
        });
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem rename = new MenuItem("Rinomina Nodo");
        rename.setOnAction(event -> { node.textFieldRename(); });
        MenuItem setInitial = new MenuItem("Nodo iniziale");
        setInitial.setOnAction(event -> { node.setInitial(); });
        MenuItem setFinal = new MenuItem("Nodo terminale");
        setFinal.setOnAction(event -> { node.setFinal(); });
        contextMenuNodi.getItems().addAll(delete, newEdge, separatorMenuItem, rename, setInitial, setFinal);
        
        node.getGroup().setOnContextMenuRequested(event -> {
            contextMenuNodi.show(graphPane, event.getScreenX(), event.getScreenY());
        });
        
        node.setContextMenuNodiList(contextMenuNodi);
        contextMenuNodiList.add(contextMenuNodi);
    }

    public void createNode(double positionX, double positionY, String name, Boolean isInitial, Boolean isFinal) {
        if (isInitial && !isThereInitial() || !isInitial && !(isInitial && isFinal)) {
            Boolean nameAlreadyExist = false;
            for (Node node: nodeList) {
                if (node.getName().equals(name)) {
                    nameAlreadyExist = true;
                    break;
                }
            }

            if (!nameAlreadyExist) {
                Node node = new Node(positionX, positionY, name, this, isInitial, isFinal);
                createNode(node);
            } else { System.out.println("Nome esistente"); }
        } else { System.out.println("Errore"); }
    }
    
    private void createEdge(Node start, Node end, String name, double positionX, double positionY) {
        Edge edge = new Edge(start, end, name, positionX, positionY);
        edgeList.add(edge);

        graphPane.getChildren().add(edge.getGroup()); // aggiunta di tutti gli edge nel foglio
        edgeMenuList.getChildren().add(edge.getStackPane());
        edge.setEdgeList(edgeList);
    }

    public void createEdge(Node start, Node end, String name) {
        if (start.equals(end)) {
            createEdge(start, end, name, 430, 430);
        } else {
            double meanWidth = end.getX() + ((start.getX() - end.getX()) / 2);
            double meanHeight = end.getY() + ((start.getY() - end.getY()) / 2);
            if (meanWidth < 0) meanWidth = -meanWidth;
            if (meanHeight < 0) meanHeight = -meanHeight;
            createEdge(start, end, name, meanWidth, meanHeight);
        }
    }

    public void delete(Node node) {
        System.out.println("Node cancellato");
        // cancello il contextMenu del nodo
        for (int i = 0; i < nodeList.size(); i++)
            if (nodeList.get(i) == node) {
                // contextMenuNodiList.get(i).hide();
                contextMenuNodiList.remove(i);
                break;
            }

        // concello tutti gli edge che partono o arrivano al nodo
        for (int i = 0; i < edgeList.size(); i++)
            if (i < edgeList.size())
                for (Node edgesNode: edgeList.get(i).getNodes())
                    if (node == edgesNode)
                        edgeList.get(i--).deleteEdge();
    }

    public Boolean isThereInitial() {
        for (Node node: this.nodeList)
            if (node.isNodeInitial())
                return true;
            
        return false;
    }

    public void coordinatesChanged() { for (Edge edge: edgeList) edge.updateEdge(); }
    public void newEdge(Node node) { System.out.println(node); }

    public ArrayList<Node> getNodeList() { return this.nodeList; }
}
