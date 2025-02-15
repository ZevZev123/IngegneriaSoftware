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
import javafx.scene.image.Image;
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
    @FXML private Label remainingWord;

    @FXML private Button runButton;
    @FXML private TextField textField;
    
    private ArrayList<Node> nodeList = new ArrayList<>();
    private ArrayList<Edge> edgeList = new ArrayList<>();
    
    private FileManager fileManager = null;
    private String fileName = null;
    public boolean isSaved = false;

    private Pane graphPane;

    private ContextMenu contextMenu;
    private ArrayList<ContextMenu> contextMenuNodiList = new ArrayList<>();
    private ArrayList<ContextMenu> contextMenuEdgeList = new ArrayList<>();
    private double contextX = 0, contextY = 0;

    private Node selectedNode = null;
    private Stage secondStage;
    private NewNodeController secondaryController;

    @FXML
    private void initialize() throws IOException {
        changePageTitle();
        graphPane = new Pane(); // creazione del foglio
        
        textField.setOnAction(event -> {
            runButton();
        });

        textField.setOnKeyTyped(event ->  {
            if (!event.getCharacter().equals("\r")) {
                textField.setStyle("");
                remainingWord.setStyle("");
                remainingWord.setText("");
            }
        });

        // creazione menu contestuale
        this.contextMenu = new ContextMenu();
        
        // creazione nodi con doppio click del mouse sul foglio
        graphPane.setOnMouseClicked(event -> {
            this.contextMenu.hide();
            for (ContextMenu menu : contextMenuNodiList) menu.hide();
            for (ContextMenu menu : contextMenuEdgeList) menu.hide();
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
        
        GraphViewBox.getChildren().add(graphPane); // aggiunta del foglio nella VBox
        VBox.setVgrow(graphPane, javafx.scene.layout.Priority.ALWAYS);
    }

    @FXML
    private void runButton() { // metodo per il pulsante di RUN
        if (isGraphValid()) createHistory();
        else { remainingWord.setText("GRAFO NON VALIDO"); remainingWord.setStyle("-fx-background-color:rgba(255, 0, 0, 0.2);"); }
    }

    @FXML
    private void deleteAll() {
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
            angleNode = (double) 360 / nodeListLength;
            double count = 0;
            double x = 0, y = 0;
            double paneWidth = GraphViewBox.getWidth();
            double paneHeight = GraphViewBox.getHeight();

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
        generateFileManager();
        if (!isSaved && (!nodeList.isEmpty() || !edgeList.isEmpty())) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Attenzione");
            alert.setHeaderText("Attenzione");
            alert.setContentText("Il file non e' stato salvato, sicuro di voler procedere?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteAll();
                this.history.getChildren().clear();
                this.fileName = null;
                changePageTitle();
            }
            return result.get();
        } else {
            deleteAll();
            this.history.getChildren().clear();
            this.fileName = null;
            changePageTitle();
            return ButtonType.OK;
        }
    }

    @FXML
    private void openFile() throws IOException {
        generateFileManager();
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
                    return (button == okButtonType) ? comboBox.getValue() : null;
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
        try {
            fileManager.readFromFile(fileName);
            this.edgeList = new ArrayList<>(){{addAll(fileManager.getListEdge());}};
            this.nodeList = new ArrayList<>(){{addAll(fileManager.getListNode());}};
            this.fileName = fileName;
            changePageTitle();
            isSaved = true;
            updateGraphPane();
        } catch (Exception e) {
            newFile();
        }
    }
    
    @FXML
    private void saveFile() throws IOException {
        generateFileManager();
        if (nodeList.isEmpty()) return;

        if (this.fileName != null) {
            fileManager.setLists(nodeList, edgeList);
            fileManager.writeToFile(this.fileName);
            isSaved = true;
        } else {
            TextInputDialog dialog = new TextInputDialog("defaultFileName");
            dialog.setTitle("Salva File");
            dialog.setHeaderText("Inserisci il nome del file da salvare:");
            dialog.setContentText("Nome file:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(fileName -> {
                this.fileName = fileName;
                changePageTitle();
                try {
                    fileManager.setLists(nodeList, edgeList);
                    fileManager.writeToFile(fileName+".graph");
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
    private void deleteFile() throws IOException {
        if (this.fileName != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Cancella File");
            alert.setHeaderText("Cancella File");
            alert.setContentText("Sei sicuro di voler cancellare il file "+this.fileName+"?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                fileManager.deleteFile(this.fileName);
                this.fileName = null;
                newFile();
            }
        } else {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Cancella file");
            dialog.setHeaderText("Seleziona il file da cancellare:");

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
                    return (button == okButtonType) ? comboBox.getValue() : null;
                }
            });

            // Mostra il dialogo e attendi la risposta dell'utente
            Optional<String> result2 = dialog.showAndWait();
            result2.ifPresent(fileName -> {
                fileManager.deleteFile(this.fileName);
                this.fileName = null;
                try {
                    newFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    private void exit() throws IOException {
        ButtonType result = newFile();
        if (result == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    private void updateGraphPane() {
        for (Edge edge: edgeList) {
            Edge.edgeList = edgeList;

            graphPane.getChildren().add(edge.getGroup());
            edgeMenuList.getChildren().add(edge.getStackPane());

            setContextMenuEdge(edge);
        }

        for (Node node: nodeList) {
            Node.nodeList = nodeList;
            node.setController(this);

            graphPane.getChildren().add(node.getGroup());
            nodeMenuList.getChildren().add(node.getStackPane());

            setContextMenuNode(node);
        }
    }

    private void generateFileManager() {
        if (this.fileManager == null) {
            this.fileManager = new FileManager(nodeList, edgeList);
            fileManager.updateFileList();
        }
    }

    private void createHistory() {
        this.history.getChildren().clear();
        for (Edge edge: edgeList) edge.setColor(Color.BLACK);
        WordAutomata wordAutomata = new WordAutomata(nodeList, edgeList);
        
        remainingWord.setText("");
        remainingWord.setStyle("");
        if (wordAutomata.run(textField.getText()))
            textField.setStyle("-fx-background-color: rgba(4, 255, 0, 0.2);");    
        else {
            textField.setStyle("-fx-background-color:rgba(255, 0, 0, 0.2);");
            remainingWord.setStyle("-fx-background-color:rgba(255, 0, 0, 0.2);");
            remainingWord.setText(wordAutomata.getRemainingWord());
        }

        ArrayList<String> historyList = wordAutomata.getStringHistory();
        ArrayList<String> stateList = wordAutomata.getStateHistory();
        if (!historyList.isEmpty() && !stateList.isEmpty()) {
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
        for (int i = 0; i < history.getChildren().size(); i++)
            if (history.getChildren().get(i) instanceof Label label)
                if (label.getStyleClass().contains("label1"))
                    nodeName = label.getText();
                else
                    for (Edge edge: edgeList)
                        if (history.getChildren().get(i) instanceof Label label2 &&
                            (edge.getStartNode().getName().equals(nodeName) &&
                            edge.getValue().equals(label2.getText())))
                            edge.setColor(Color.BLUE);
    }

    // controlla se l'ultimo node inserito è uguale a quello precedente
    private boolean equalToLast(String state) {
        for (int i = history.getChildren().size()-1; i > 0; i--)
            if (history.getChildren().get(i).getStyleClass().contains("label1")) {
                if (history.getChildren().get(i) instanceof Label label)
                    if (label.getText().equals(state))
                        return true;
                break;
            }

        return false;
    }

    private Label createLableNode(String name, boolean isNode) {
        Label label = new Label();
        label.setText(name);
        label.setTextAlignment(TextAlignment.LEFT);
        if (isNode) label.getStyleClass().add("label1");
        else label.getStyleClass().add("label2");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(0, 0, 0, 10));

        return label;
    }

    private boolean isGraphValid() {
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
        if (secondStage == null || !secondStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("newEdge.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/AutomaZ/style2.css").toExternalForm());
                
                NewEdgeController tertiaryController = loader.getController();
                tertiaryController.setPrimaryController(this);
                if (this.selectedNode != null)
                tertiaryController.setStartNode(this.selectedNode);
                
                secondStage = new Stage();
                secondStage.getIcons().add(new Image(getClass().getResourceAsStream("/AutomaZ/images/icon.png")));
                secondStage.setTitle("Creazione nuovo edge");
                secondStage.setScene(scene);

                secondStage.setMinHeight(200);
                secondStage.setMinWidth(320);
                secondStage.setMaxHeight(200);
                secondStage.setMaxWidth(320);

                secondStage.setOnCloseRequest(event -> {secondStage = null;});
                secondStage.show();
            } catch (Exception e) { e.printStackTrace(); }
        } else secondStage.toFront();
    }

    private void createNode(double positionX, double positionY) {
        if (secondStage == null || !secondStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("newNode.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/AutomaZ/style2.css").toExternalForm());

                this.secondaryController = loader.getController();
                secondaryController.setPrimaryController(this);
                secondaryController.setPosition(positionX, positionY);

                secondStage = new Stage();
                secondStage.getIcons().add(new Image(getClass().getResourceAsStream("/AutomaZ/images/icon.png")));
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
            secondaryController.setPosition(positionX, positionY);
        }
    }

    private void createNode(Node node) {
        nodeList.add(node);
        Node.nodeList = nodeList;
        
        graphPane.getChildren().add(node.getGroup());
        
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
        
        node.setContextMenuNodesList(contextMenuNodi);
        contextMenuNodiList.add(contextMenuNodi);
    }

    private void setContextMenuEdge(Edge edge) {
        ContextMenu contextMenuEdge = new ContextMenu();

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem delete = new MenuItem("Cancella Edge");
        delete.setOnAction(event -> {edge.deleteEdge();});
        MenuItem revalue = new MenuItem("Cambia valore");
        revalue.setOnAction(event -> { edge.textFieldRename(); });
        contextMenuEdge.getItems().addAll(delete, separatorMenuItem, revalue);
        
        edge.getGroup().setOnContextMenuRequested(event -> {
            contextMenuEdge.show(graphPane, event.getScreenX(), event.getScreenY());
        });

        contextMenuEdgeList.add(contextMenuEdge);
    }

    private void changePageTitle() {
        Platform.runLater(() -> {
            Stage stage = (Stage) graphPane.getScene().getWindow();
            if (this.fileName != null) stage.setTitle("AutomaZ - " + this.fileName);
            else stage.setTitle("AutomaZ");
        });
    }

    public void createNode(double positionX, double positionY, String name, boolean isInitial, boolean isFinal) {
        if (!isInitial || !isThereInitial()) {
            boolean nameAlreadyExist = false;
            for (Node node: nodeList)
                if (node.getName().equals(name)) {
                    nameAlreadyExist = true;
                    break;
                }

            if (!nameAlreadyExist) createNode(new Node(positionX, positionY, name, this, isInitial, isFinal));
        }
    }
    
    private void createEdge(Node start, Node end, String name, double positionX, double positionY) {
        Edge edge = new Edge(start, end, name, positionX, positionY);
        edgeList.add(edge);

        graphPane.getChildren().add(edge.getGroup()); // aggiunta di tutti gli edge nel foglio
        edgeMenuList.getChildren().add(edge.getStackPane());
        Edge.edgeList = edgeList;
        setContextMenuEdge(edge);
        isSaved = false;
    }

    public void createEdge(Node start, Node end, String name) {
        if (start.equals(end)) createEdge(start, end, name, 430, 430);
        else {
            double[] endCoords = end.getCoordinates(), startCoords = start.getCoordinates();
            double meanWidth = endCoords[0] + ((startCoords[0] - endCoords[0]) / 2);
            double meanHeight = endCoords[1] + ((startCoords[1] - endCoords[1]) / 2);
            if (meanWidth < 0) meanWidth = -meanWidth;
            if (meanHeight < 0) meanHeight = -meanHeight;
            createEdge(start, end, name, meanWidth, meanHeight);
        }
    }

    public void delete(Node node) {
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
            for (Node edgesNode: edgeList.get(i).getNodes())
                if (node == edgesNode) {
                    edgeList.get(i--).deleteEdge();
                    break;
                }
    }

    public boolean isThereInitial() {
        for (Node node: this.nodeList)
            if (node.isNodeInitial())
                return true;
        return false;
    }

    public void coordinatesChanged() { for (Edge edge: edgeList) edge.updateEdge(); isSaved = false; }

    public ArrayList<Node> getNodeList() { return this.nodeList; }
}
