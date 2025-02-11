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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    
    private FileManager fileManager = null;
    private String fileName = null;
    public Boolean isSaved = false;

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
        
        openFile("prova.graph"); // da togliere tutto il resto, non funziona ancora

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
            isSaved = false;
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
    private ButtonType newFile() throws IOException {
        System.out.println("newFile");
        generateFileManager();
        if (!isSaved && (nodeList.size() != 0 || edgeList.size() != 0)) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Attenzione");
            alert.setHeaderText("Attenzione");
            alert.setContentText("Il file non e' stato salvato, sicuro di voler procedere?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteAll();
                this.history.getChildren().clear();
                this.fileName = null;
            }
            return result.get();
        } else {
            deleteAll();
            this.history.getChildren().clear();
            this.fileName = null;
            return ButtonType.OK;
        }
    }

    @FXML
    private void openFile() throws IOException {
        System.out.println("openFile");
        generateFileManager();
        if (this.fileName == null) {
            this.fileName = "prova";
        }
        ButtonType result = newFile();
        if (result == ButtonType.OK) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Apri File");
            dialog.setHeaderText("Seleziona il file da aprire:");

            ButtonType okButtonType = new ButtonType("OK", ButtonType.OK.getButtonData());
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            // Crea la ComboBox e aggiungi gli elementi
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(fileManager.savedFiles);

            // Aggiungi la ComboBox al layout del dialogo
            VBox vbox = new VBox(comboBox);
            dialog.getDialogPane().setContent(vbox);

            // Imposta il risultato del dialogo quando l'utente preme OK
            dialog.setResultConverter(new Callback<ButtonType, String>() {
                @Override
                public String call(ButtonType button) {
                    if (button == okButtonType) {
                        return comboBox.getValue();
                    }
                    return null;
                }
            });

            // Mostra il dialogo e attendi la risposta dell'utente
            Optional<String> result2 = dialog.showAndWait();
            result2.ifPresent(fileName -> {
                this.fileName = fileName;
                nodeList.clear();
                edgeList.clear();
                try {
                    openFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void openFile(String fileName) throws IOException {
        generateFileManager();
        fileManager.readFromFile(fileName);
        this.edgeList = new ArrayList<>(){{addAll(fileManager.getListEdge());}};
        this.nodeList = new ArrayList<>(){{addAll(fileManager.getListNode());}};
        isSaved = true;
        updateGraphPane();
    }
    
    @FXML
    private void saveFile() throws IOException {
        generateFileManager();
        System.out.println("saveFile");
        if (nodeList.size() == 0 && edgeList.size() == 0) {
            return;
        }
        if (this.fileName != null) {
            fileManager.writeToFile(this.fileName);
            isSaved = true;
        } else {
            TextInputDialog dialog = new TextInputDialog("defaultFileName");
            dialog.setTitle("Salva File");
            dialog.setHeaderText("Inserisci il nome del file da aprire:");
            dialog.setContentText("Nome file:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(fileName -> {
                this.fileName = fileName;
                try {
                    fileManager.writeToFile(fileName);
                    System.out.println(fileManager.getListEdge()+"\n"+fileManager.getListNode());
                    updateGraphPane();
                    isSaved = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    @FXML
    private void saveNewFile() throws IOException {
        this.fileName = null;
        saveFile();
    }

    @FXML
    private void exit() throws IOException {
        System.out.println("exit");
        ButtonType result = newFile();
        if (result == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    private void updateGraphPane() {
        for (Edge edge: edgeList) {
            edge.setEdgeList(edgeList);

            graphPane.getChildren().add(edge.getGroup());
            edgeMenuList.getChildren().add(edge.getStackPane());
            edge.setEdgeList(edgeList);
        }

        for (Node node: nodeList) {
            node.setListFX(nodeList);
            node.setController(this);

            graphPane.getChildren().add(node.getGroup());
            nodeMenuList.getChildren().add(node.getStackPane());
            node.setListFX(nodeList);

            setContextMenuNode(node);
        }
    }

    private void generateFileManager() {
        if (this.fileManager == null) {
            this.fileManager = new FileManager(nodeList, edgeList);
            fileManager.updateFileList();
            for(String s : fileManager.savedFiles)
                System.out.println(s);
        }
    }

    private void createHistory() {
        this.history.getChildren().clear();
        for (Edge edge: edgeList) edge.setColor(Color.BLACK);
        WordAutomata wordAutomata = new WordAutomata(nodeList, edgeList);
        
        System.out.println(wordAutomata.run(textField.getText()));
        ArrayList<String> historyList = wordAutomata.getStringHistory();
        ArrayList<String> stateList = wordAutomata.getStateHistory();
        if (historyList.size() != 0 && stateList.size() != 0) {
            Label label;
            for (int i = 0; i < historyList.size(); i++) {
                if (!equalToLast(stateList.get(i))) {
                    label = createLableNode(stateList.get(i), true);
                    this.history.getChildren().add(label);
                }
                label = createLableNode(historyList.get(i), false);
                this.history.getChildren().add(label);
            }
            label = createLableNode(stateList.get(stateList.size()-1), true);
            this.history.getChildren().add(label);
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
        
        setContextMenuNode(node);
        isSaved = false;
    }

    private void setContextMenuNode(Node node) {
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
        isSaved = false;
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
        isSaved = false;
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
                    if (node == edgesNode) {
                        edgeList.get(i--).deleteEdge();
                        break;
                    }
                        
    }

    public Boolean isThereInitial() {
        for (Node node: this.nodeList)
            if (node.isNodeInitial())
                return true;
            
        return false;
    }

    public void coordinatesChanged() { for (Edge edge: edgeList) edge.updateEdge(); isSaved = false; }
    public void newEdge(Node node) { System.out.println(node); }

    public ArrayList<Node> getNodeList() { return this.nodeList; }
}
