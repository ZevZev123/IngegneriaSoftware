package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
    @FXML private VBox GraphViewBox;
    @FXML private Label statusLabel;

    @FXML private Button runButton;
    @FXML private Label history;

    private List<Group> nodeList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();

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
                Group nodo = createNode(0, 0, 15, "0");
                nodeList.add(nodo);

                // mostra graficamente il nodo nel Pane
                graphPane.getChildren().add(nodo);
                paneWidth = GraphViewBox.getWidth();
                paneHeight = GraphViewBox.getHeight();
                reposition();

                createMenuLabel(nodo);
            }
        });

        nodeList.add(createNode(0, 0, 15, "F"));
        nodeList.add(createNode(0, 0, 15, "A"));
        nodeList.add(createNode(0, 0, 15, "B"));
        nodeList.add(createNode(0, 0, 15, "C"));
        nodeList.add(createNode(0, 0, 15, "T"));
        nodeList.add(createNode(0, 0, 15, "F"));
        
        // lineList.add(new Line(50, 50, 150, 150));

        for (Line edge: lineList) {
            graphPane.getChildren().add(edge); // aggiunta di tutti gli edge nel foglio
        }

        for (Group node: nodeList) {
            graphPane.getChildren().add(node); // aggiunta di tutti i nodi nel foglio
        }

        GraphViewBox.getChildren().add(graphPane); // aggiunta del foglio nella VBox
        VBox.setVgrow(graphPane, javafx.scene.layout.Priority.ALWAYS);

        // CREAZIONE LISTA DI NODI E EDGE
        for (Group node : nodeList) {
            // Crea una Label locale per ogni iterazione
            createMenuLabel(node);
        }

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

    private void createMenuLabel(Group node) {
        Label currentLabel = new Label(getNodeText(node));
        currentLabel.setTextAlignment(TextAlignment.LEFT);
        currentLabel.setStyle("-fx-min-height: 30px; -fx-border-color: gray; -fx-border-size: 1px;");
        currentLabel.setMaxWidth(Double.MAX_VALUE);
        currentLabel.setPadding(new Insets(0, 0, 0, 10));
    
        // Evento per sostituire la Label con un TextField
        currentLabel.setOnMouseClicked(event -> {
            TextField textField = new TextField(currentLabel.getText());
            textField.setPrefWidth(currentLabel.getWidth()); // Mantieni la larghezza
            int labelIndex = nodeEdgeList.getChildren().indexOf(currentLabel); // Posizione della Label
    
            nodeEdgeList.getChildren().set(labelIndex, textField); // Sostituisci direttamente
    
            // Quando l'utente preme Invio, torna alla Label
            textField.setOnAction(e -> {
                currentLabel.setText(textField.getText()); // Aggiorna il testo
                nodeEdgeList.getChildren().set(labelIndex, currentLabel); // Torna alla Label
            });
    
            // Quando il TextField perde il focus, torna alla Label
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    currentLabel.setText(textField.getText()); // Aggiorna il testo
                    nodeEdgeList.getChildren().set(labelIndex, currentLabel); // Torna alla Label
                }
            });
    
            textField.requestFocus(); // Dai il focus iniziale al TextField
        });

        VBox.setVgrow(currentLabel, Priority.ALWAYS);
        nodeEdgeList.getChildren().add(currentLabel); // Aggiungi la Label alla VBox
    }

    private void reposition() {
        int nodeListLength = nodeList.size();
        double angleNode = 360 / nodeListLength;

        double count = 0;
        double x = 0, y = 0;
        for (Group node: nodeList) {
            for (var child: node.getChildren()) {
                x = (paneWidth/2) + (paneWidth/4)*cos(toRadians(count));
                y = (paneHeight/2) + (paneHeight/4)*sin(toRadians(count));

                if (child instanceof Circle circle) {
                    circle.setCenterX(x);
                    circle.setCenterY(y);
                } else if (child instanceof Text text) {
                    text.setX(x-4);
                    text.setY(y+4);
                }
            }
            count = count + angleNode;
        }
    }

    private Group createNode(double x, double y, double radius, String name) { // creazione Nodo con Testo inseriti in un Group
        Circle circle = new Circle(x, y, radius, Color.WHITE);
        Circle circle2 = new Circle(x, y, radius + 5, Color.WHITE);

        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        
        circle2.setStroke(Color.BLUE);
        circle2.setStrokeWidth(2);

        Text text = new Text(x, y, name);
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        
        Group node = new Group(circle2, circle, text);
        
        node.setOnMouseEntered(event -> node.setCursor(javafx.scene.Cursor.HAND));

        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                setNodeStartEnd(circle, event.getButton());
            }
        });
        
        return node;
    }

    private void setNodeStartEnd(Circle circle, MouseButton command) { // metodo per nodo Start e End
        if (command == javafx.scene.input.MouseButton.PRIMARY) { // nodo Start disdegnato
            if (circle.getStyleClass().contains("Start")) {
                circle.getStyleClass().remove("Start");
                circle.setFill(Color.WHITE);
            }
            else { // controllo se esiste gia' il nodo Start
                Boolean startAlreadyExist = false;
                for (Group node: nodeList) {
                    for (var child: node.getChildren()) {
                        if (child instanceof Circle) {
                            if (child.getStyleClass().contains("Start")) {
                                System.out.println("Start gia' presente");
                                startAlreadyExist = true;
                            }
                        }
                    }
                }
                if (!startAlreadyExist) { // nodo Start assegnato
                    circle.getStyleClass().remove("End");
                    circle.getStyleClass().add("Start");
                    circle.setFill(Color.YELLOW);
                }
            }
        }
        else if (command == javafx.scene.input.MouseButton.SECONDARY) { 
            if (!circle.getStyleClass().contains("End")) { // nodo End assegnato
                circle.getStyleClass().remove("Start");
                circle.getStyleClass().add("End");
                circle.setFill(Color.GREEN);
            } else { // nodo End disdegnato
                circle.getStyleClass().remove("End");
                circle.setFill(Color.WHITE);
            }
        }
    }

    private double[] getNodeCoordinates(Group node) { // metodo per avere le coordinate di un nodo
        for (var child : node.getChildren()) {
            if (child instanceof Circle) {
                Circle circle = (Circle) child;
                return new double[]{circle.getCenterX(), circle.getCenterY()};
            }
        }
        return null;
    }

    private String getNodeText(Group node) { // restituisce il nome di un nodo
        for (var child : node.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                return textNode.getText();
            }
        }
        return null;
    }

    private void setNodeText(Group node, String newText) { // cambia il nome di un nodo
        for (var child: node.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                textNode.setText(newText);
                break;
            }
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
