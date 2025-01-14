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
            }
        });

        nodeList.add(createNode(0, 0, 15, "F"));
        nodeList.add(createNode(0, 0, 15, "A"));
        nodeList.add(createNode(0, 0, 15, "B"));
        nodeList.add(createNode(0, 0, 15, "C"));
        nodeList.add(createNode(0, 0, 15, "T"));
        nodeList.add(createNode(0, 0, 15, "D"));
        
        // lineList.add(new Line(50, 50, 150, 150));

        for (Line edge: lineList) {
            graphPane.getChildren().add(edge); // aggiunta di tutti gli edge nel foglio
        }

        for (Group node: nodeList) {
            graphPane.getChildren().add(node); // aggiunta di tutti i nodi nel foglio
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

    private Label createMenuLabel(Group node) {
        Label currentLabel = new Label(getNodeText(node));
        currentLabel.setTextAlignment(TextAlignment.LEFT);
        currentLabel.getStyleClass().add("label1");
        currentLabel.setMaxWidth(Double.MAX_VALUE);
        currentLabel.setPadding(new Insets(0, 0, 0, 10));
        
        Button button = new Button();
        button.getStyleClass().add("deleteButton");
        button.setVisible(false);
        // button.setFocusTraversable(false);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(currentLabel, button);
        StackPane.setAlignment(button, Pos.TOP_RIGHT);
        // stackPane.setStyle("-fx-padding: 5;");

        currentLabel.setOnMouseEntered(event -> {
            button.setVisible(true);
            for (var child: node.getChildren()) {
                if (child instanceof Circle circle) {
                    if (isStart(circle)) {
                        currentLabel.getStyleClass().remove("start");
                        currentLabel.getStyleClass().add("startHover");
                    } else {
                        if (!currentLabel.getStyleClass().contains("labelHover")) {
                            currentLabel.getStyleClass().add("labelHover");
                        }
                    }
                    if (circle.getRadius() == 15 && isStart(circle)) {
                        circle.setFill(Color.GOLD);
                    }
                    else if (circle.getRadius() == 15 && !isStart(circle)) {
                        circle.setFill(Color.LIGHTGRAY);
                    }
                    if (circle.getRadius() == 20 && circle.getStroke() != Color.TRANSPARENT) {
                        circle.setFill(Color.LIGHTGRAY);
                    }
                }
            }
        });
        
        currentLabel.setOnMouseExited(event -> {
            button.setVisible(false);
            currentLabel.getStyleClass().remove("startHover");
            currentLabel.getStyleClass().remove("labelHover");
            for (var child: node.getChildren()) {
                if (child instanceof Circle circle) {
                    if (isStart(circle)){
                        circle.setFill(Color.YELLOW);
                        currentLabel.getStyleClass().add("start");
                    } else {
                        circle.setFill(Color.TRANSPARENT);
                    }
                }
            }
        });

        // Evento per sostituire la Label con un TextField
        currentLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TextField textField = new TextField(currentLabel.getText());
                textField.setPrefWidth(currentLabel.getWidth()); // Mantieni la larghezza
                textField.setStyle("-fx-min-height: 30px;");
                int labelIndex = nodeEdgeList.getChildren().indexOf(stackPane); // Posizione dello StackPane

                // Sostituisci lo StackPane con il TextField
                nodeEdgeList.getChildren().set(labelIndex, textField);

                // Quando l'utente preme Invio, torna alla label
                textField.setOnAction(e -> {
                    currentLabel.setText(textField.getText()); // Aggiorna il testo
                    setNodeText(node, textField.getText());
                    nodeEdgeList.getChildren().set(labelIndex, stackPane); // Torna allo StackPane
                });
                
                // Quando il TextField perde il focus, torna alla label
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        currentLabel.setText(textField.getText()); // Aggiorna il testo
                        setNodeText(node, textField.getText());
                        nodeEdgeList.getChildren().set(labelIndex, stackPane); // Torna allo StackPane
                    }
                });

                textField.requestFocus(); // Dai il focus iniziale al TextField
            }
        });

        // il bottone viene visualizzato se il cursore e' posizionato sopra di esso
        button.setOnMouseEntered(event -> {
            button.setVisible(true);
            for (var child: node.getChildren()) {
                if (child instanceof Circle circle) {
                    if (isStart(circle)) {
                        currentLabel.getStyleClass().remove("start");
                        currentLabel.getStyleClass().add("startHover");
                    } else {
                        currentLabel.getStyleClass().add("labelHover");    
                    }
                    if (circle.getRadius() == 15 && isStart(circle)){
                        circle.setFill(Color.GOLD);
                    }
                    else if (circle.getRadius() == 15 && !isStart(circle)) {
                        circle.setFill(Color.LIGHTGRAY);
                    }
                    if (circle.getRadius() == 20 && circle.getStroke() != Color.TRANSPARENT) {
                        circle.setFill(Color.LIGHTGRAY);
                    }
                }
            }
        });

        button.setOnMouseExited(event -> {
            button.setVisible(false);
            currentLabel.getStyleClass().remove("startHover");
            while (currentLabel.getStyleClass().contains("labelHover")){
                currentLabel.getStyleClass().remove("labelHover");
            }
            for (var child: node.getChildren()) {
                if (child instanceof Circle circle) {
                    if (isStart(circle)){
                    circle.setFill(Color.YELLOW);
                    currentLabel.getStyleClass().add("start");
                    } else 
                        circle.setFill(Color.TRANSPARENT);
                }
            }
        });

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Hai cliccato bottone " + getNodeText(node));
            delete(node);
            // event.consume(); // Impedisce la propagazione del click // non ho idea del perché serva
        });

        VBox.setVgrow(stackPane, Priority.ALWAYS);
        nodeEdgeList.getChildren().add(stackPane); // Aggiungi la Label alla VBox

        return currentLabel;
    }

    private void delete(Group node) {

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
            count = count - angleNode;
        }
    }

    private Group createNode(double x, double y, double radius, String name) { // creazione Nodo con Testo inseriti in un Group
        // Controllo se esiste gia' un nodo con il nome 'name'
        // for (Group node: nodeList) {
        //     if (getNodeText(node) == name) {
        //         return null;
        //     }
        // }
        
        Circle circle = new Circle(x, y, radius, Color.TRANSPARENT);
        Circle circle2 = new Circle(x, y, radius + 5, Color.TRANSPARENT);

        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        
        circle2.setStroke(Color.TRANSPARENT);
        circle2.setStrokeWidth(2);
        circle2.setFill(Color.TRANSPARENT);

        Text text = new Text(x, y, name);
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        
        Group node = new Group(circle2, circle, text);
        Label labelAssociated = createMenuLabel(node);

        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                setNodeStartEnd(circle, circle2, labelAssociated, event.getButton());
            }
        });

        node.setOnMouseEntered(event -> {
            node.setCursor(javafx.scene.Cursor.HAND);
        
            if (isStart(circle))
                circle.setFill(Color.GOLD);
            else
                circle.setFill(Color.LIGHTGRAY);
            if (circle2.getStroke() != Color.TRANSPARENT) {
                circle2.setFill(Color.LIGHTGRAY);
            }

            if (isStart(circle)) {
                labelAssociated.getStyleClass().remove("start");
                labelAssociated.getStyleClass().add("startHover");
            } else {
                if (!labelAssociated.getStyleClass().contains("labelHover")) {
                    labelAssociated.getStyleClass().add("labelHover");
                }
            }
        });
            
        node.setOnMouseExited(event -> {
            if (isStart(circle)){
                circle.setFill(Color.YELLOW);
                labelAssociated.getStyleClass().remove("startHover");
                labelAssociated.getStyleClass().add("start");
            } else {
                circle.setFill(Color.TRANSPARENT);
                labelAssociated.getStyleClass().remove("labelHover");
            }
            circle2.setFill(Color.TRANSPARENT);
        });

        return node;
    }

    private Boolean isStart(Circle circle) {
        return circle.getStyleClass().contains("start") || circle.getStyleClass().contains("startHover");
    }

    private void setNodeStartEnd(Circle circle, Circle circle2, Label label, MouseButton command) { // metodo per nodo iniziale e terminale
        if (command == javafx.scene.input.MouseButton.PRIMARY) { // nodo iniziale
            if (isStart(circle)) {
                circle.getStyleClass().remove("start");
                label.getStyleClass().remove("start");
                circle.setFill(Color.TRANSPARENT);
            }
            else { // controllo se esiste gia' il nodo iniziale
                Boolean startAlreadyExist = false;
                for (Group node: nodeList) {
                    for (var child: node.getChildren()) {
                        if (child instanceof Circle circleList) {
                            if (isStart(circleList)) {
                                System.out.println("Start gia' presente");
                                startAlreadyExist = true;
                            }
                        }
                    }
                }
                if (!startAlreadyExist) { // nodo iniziale assegnato (e terminale tolto)
                    circle.getStyleClass().remove("End");
                    circle.getStyleClass().add("startHover");
                    label.getStyleClass().add("startHover");
                    circle.setFill(Color.GOLD);
                    circle2.setStroke(Color.TRANSPARENT);
                }
            }
        }
        else if (command == javafx.scene.input.MouseButton.SECONDARY) { 
            if (!circle.getStyleClass().contains("End")) { // nodo terminale
                circle.getStyleClass().remove("start");
                circle.getStyleClass().add("End");
                circle.setFill(Color.TRANSPARENT);
                circle2.setStroke(Color.BLACK);
            } else { // nodo NON terminale
                circle.getStyleClass().remove("End");
                circle2.setStroke(Color.TRANSPARENT);
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
