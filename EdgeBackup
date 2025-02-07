package AutomaZ;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Edge {
    private Node start;
    private Node end;
    private QuadCurve curve;
    private Polygon arrow;
    private Group group;

    private double controlX, controlY;

    private Text text;

    private String name;

    private StackPane stackPane;

    private List<Edge> edgeList = new ArrayList<>();

    public Edge(Node start, Node end, String name, double controlX, double controlY) {
        this.start = start;
        this.end = end;
        this.text = new Text(this.name);
        this.name = name;

        this.controlX = controlX;
        this.controlY = controlY;

        // Crea la curva
        curve = new QuadCurve();
        curve.setStroke(Color.BLACK);
        curve.setFill(null);
        curve.setStrokeWidth(3);

        curve.setControlX(this.controlX);
        curve.setControlY(this.controlY);

        // Crea la freccia
        arrow = new Polygon();
        arrow.setFill(Color.BLACK);

        // Gruppo per curva + freccia
        group = new Group(curve, arrow);

        updateEdge();
        addDragHandler();
        setLabel();
        updateToolTip();
    }

    public Edge(Node start, Node end, String name) {
        this(start, end, name, 0, 0);
    }
    
    private void setLabel() {
        Label label = new Label();
        label.setText(this.name);
        label.setTextAlignment(TextAlignment.LEFT);
        label.getStyleClass().add("label1");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(0, 0, 0, 10));

        Button button = new Button();
        button.getStyleClass().add("deleteButton");
        button.setVisible(false);
        
        this.stackPane = new StackPane();
        this.stackPane.getChildren().addAll(label, button);
        StackPane.setAlignment(button, Pos.TOP_RIGHT);
        
        label.setOnMouseEntered(event -> {
            button.setVisible(true);
            edgeHover();
        });

        label.setOnMouseExited(event -> {
            button.setVisible(false);
            edgeNotHover();
        });
        
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TextField textField = new TextField(this.name);
                textField.setPrefWidth(label.getWidth());
                textField.setStyle("-fx-min-height: 30px;");
                textField.setPadding(new Insets(0, 0, 0, 10));
                
                this.stackPane.getChildren().clear();
                this.stackPane.getChildren().add(textField);
    
                textField.setOnAction(e -> {
                    if (!textField.getText().isEmpty()) {
                        label.setText(textField.getText());
                        setName(textField.getText());
                        this.text.setText(this.name);
                        updateToolTip();
                    }
                    updateEdge();
                    this.stackPane.getChildren().clear();
                    this.stackPane.getChildren().addAll(label, button);
                    StackPane.setAlignment(button, Pos.TOP_RIGHT);
                });
    
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        if (!textField.getText().isEmpty()) {
                            label.setText(textField.getText());
                            setName(textField.getText());
                            this.text.setText(this.name);
                            updateToolTip();
                        }
                        updateEdge();
                        this.stackPane.getChildren().clear();
                        this.stackPane.getChildren().addAll(label, button);
                        StackPane.setAlignment(button, Pos.TOP_RIGHT);
                    }
                });
    
                textField.requestFocus();
            }
        });

        button.setOnMouseEntered(event -> {
            button.setVisible(true);
            edgeHover();
        });

        button.setOnMouseExited(event -> {
            button.setVisible(false);
            edgeNotHover();
        });

        button.setOnAction(event -> {
            deleteEdge();
        });
    }

    private void updateArrow(double[] target, double[] source) {
        // Calcola la direzione basata sulla tangente della curva nel punto finale
        double dx = target[0] - curve.getControlX();
        double dy = target[1] - curve.getControlY();
        double angle = Math.atan2(dy, dx);
    
        double arrowLength = 10; // Lunghezza della freccia
    
        // Calcola i vertici della freccia
        double x1 = target[0] - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = target[1] - arrowLength * Math.sin(angle - Math.PI / 6);
    
        double x2 = target[0] - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = target[1] - arrowLength * Math.sin(angle + Math.PI / 6);
    
        // Aggiorna i punti del poligono della freccia
        arrow.getPoints().setAll(
                target[0], target[1], // Punto della punta della freccia
                x1, y1,               // Punto alla sinistra della freccia
                x2, y2                // Punto alla destra della freccia
        );
    }

    private void addDragHandler() {
        curve.setOnMouseDragged((MouseEvent event) -> {
            // Aggiorna la posizione del punto di controllo
            this.controlX = event.getX();
            this.controlY = event.getY();
            curve.setControlX(this.controlX);
            curve.setControlY(this.controlY);

            // Aggiorna dinamicamente la curva e i punti di contatto
            updateEdge();
        });
    
        curve.setOnMouseEntered(event -> {
            curve.setCursor(javafx.scene.Cursor.HAND);
            edgeHover();
        });
    
        curve.setOnMouseExited(event -> {
            curve.setCursor(javafx.scene.Cursor.HAND);
            edgeNotHover();
        });
    }
    
    private void edgeNotHover() {
        this.curve.setStroke(Color.BLACK);
        this.arrow.setStroke(Color.BLACK);
        this.arrow.setFill(Color.BLACK);
        this.text.setFill(Color.BLACK);
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().remove("labelHover");
        }
    }

    private void edgeHover() {
        this.curve.setStroke(Color.RED);
        this.arrow.setStroke(Color.RED);
        this.arrow.setFill(Color.RED);
        this.text.setFill(Color.RED);
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("labelHover");
        }
    }

    private double[] calculateEdgePoint(double x1, double y1, double x2, double y2, double radius) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        return new double[]{
                x1 + radius * Math.cos(angle),
                y1 + radius * Math.sin(angle)
        };
    }

    private void updateToolTip() {
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(this.group, tooltip);
    }

    public void updateEdge() {
        // Calcola i punti iniziali e finali ai bordi dei nodi
        double[] sourcePoint = calculateEdgePoint(
                start.getX(), start.getY(),
                curve.getControlX(), curve.getControlY(),
                20
        );
    
        double[] targetPoint = calculateEdgePoint(
                end.getX(), end.getY(),
                curve.getControlX(), curve.getControlY(),
                20
        );
    
        // Imposta i punti di partenza, controllo e fine della curva
        curve.setStartX(sourcePoint[0]);
        curve.setStartY(sourcePoint[1]);
        curve.setEndX(targetPoint[0]);
        curve.setEndY(targetPoint[1]);
    
        // Aggiorna la freccia
        updateArrow(targetPoint, sourcePoint);
    }

    public Boolean deleteEdge() {
        if (this.group.getParent() instanceof javafx.scene.layout.Pane parent)
        parent.getChildren().remove(this.group);
        if (this.stackPane.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.stackPane);
        if (edgeList != null && edgeList.contains(this)) edgeList.remove(this);
        return true;
    }

    public void setControl(double controlX, double controlY) {
        this.controlX = controlX;
        this.controlY = controlY;
        this.curve.setControlX(this.controlX);
        this.curve.setControlY(this.controlY);

        updateEdge();
    }
    private void setName(String name) { this.name = name; }
    public void setEdgeList(List<Edge> edgeList) { this.edgeList = edgeList; }
    
    public StackPane getStackPane() { return this.stackPane; }
    public Node[] getNodes() { return new Node[] {this.start, this.end}; }
    public Node getStartNode() { return this.start; }
    public Node getEndNode() { return this.end; }
    public Group getGroup() { return group; }
    public double getControlX() { return this.controlX; }
    public double getControlY() { return this.controlY; }
    public String getValue() { return this.name; }
}
