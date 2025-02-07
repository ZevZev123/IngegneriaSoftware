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
import javafx.scene.shape.Shape;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Edge {
    private Node start;
    private Node end;
    private Shape curve;
    private static final int ARC_LENGTH = 120;
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
        if(start != end) {
            curve = new QuadCurve();

            ((QuadCurve) curve).setControlX(this.controlX);
            ((QuadCurve) curve).setControlY(this.controlY);
        }
        else {
            curve = new Arc();

            ((Arc) curve).setRadiusX(Node.RADIUS);
            ((Arc) curve).setRadiusY(Node.RADIUS);
            ((Arc) curve).setLength(ARC_LENGTH);
            ((Arc) curve).setType(ArcType.OPEN);
            setArc();
        }
        curve.setStroke(Color.BLACK);
        curve.setFill(null);
        curve.setStrokeWidth(3);

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

    private void updateArrow() {
        double[] target;
        double dx, dy;
        if(curve instanceof QuadCurve) {
            target = new double[] {((QuadCurve)curve).getStartX(),
                ((QuadCurve)curve).getStartY()};
            dx = target[0] - controlX;
            dy = target[1] - controlY;
        }
        else {
            // TO DO
            target = new double[] {0, 0};
            dx = 0;
            dy = 0;
        }
        // Calcola la direzione basata sulla tangente della curva nel punto finale
        double angle = Math.atan2(dy, dx);
        
        double arrowLength = 10; // Lunghezza della freccia
        
        // Calcola i vertici della freccia
        double x1 = target[0] - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = target[1] - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = target[0] - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = target[1] - arrowLength * Math.sin(angle + Math.PI / 6);
        
        // Aggiorna i punti del poligono della freccia
        arrow.getPoints().setAll(
                target[0], target[1], // Punta della freccia
                x1, y1,               // Punto alla sinistra della freccia
                x2, y2                // Punto alla destra della freccia
        );
    }

    private void addDragHandler() {
        curve.setOnMouseDragged((MouseEvent event) -> {
            // Aggiorna la posizione del punto di controllo
            this.controlX = event.getX();
            this.controlY = event.getY();
            if(curve instanceof QuadCurve)
                setQuadControl();

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

    private void calculateEdgePoints() {
        double x1 = start.getX();
        double y1 = start.getY();
        double x2 = end.getX();
        double y2 = end.getY();
        double angle1 = Math.atan2(controlY - y1, controlX - x1);
        double angle2 = Math.atan2(controlY - y2, controlX - x2);
        ((QuadCurve)curve).setStartX(x1 + Node.RADIUS * Math.cos(angle1));
        ((QuadCurve)curve).setStartY(y1 + Node.RADIUS * Math.sin(angle1));
        ((QuadCurve)curve).setEndX(x2 + Node.RADIUS * Math.cos(angle2));
        ((QuadCurve)curve).setEndY(y2 + Node.RADIUS * Math.sin(angle2));
    }

    private void updateToolTip() {
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(this.group, tooltip);
    }

    public void updateEdge() {
        // Calcola i punti iniziali e finali ai bordi dei nodi
        if (curve instanceof QuadCurve)
            calculateEdgePoints();
        else
            setArc();
    
        // Aggiorna la freccia
        updateArrow();
    }

    public Boolean deleteEdge() {
        if (this.group.getParent() instanceof javafx.scene.layout.Pane parent)
        parent.getChildren().remove(this.group);
        if (this.stackPane.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.stackPane);
        if (edgeList != null && edgeList.contains(this)) edgeList.remove(this);
        return true;
    }

    private void setArc() {
        // Center
        double dx = controlX - start.getX();
        double dy = controlY - start.getY();

        double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        double x = start.getX() + Node.RADIUS * ( dx / magnitude );
        double y = start.getY() + Node.RADIUS * ( dy / magnitude );

        ((Arc) curve).setCenterX(x);
        ((Arc) curve).setCenterY(y);

        // Start Angle
        double startAngle = Math.toDegrees(Math.atan2(controlY - y, controlX - x));
        if( startAngle < 0 ) startAngle += 360;
        ((Arc) curve).setStartAngle(startAngle);
    }

    public void setControl(double controlX, double controlY) {
        this.controlX = controlX;
        this.controlY = controlY;
        if(curve instanceof QuadCurve)
            setQuadControl();

        updateEdge();
    }

    public void setQuadControl() {
        ((QuadCurve)curve).setControlX(this.controlX);
        ((QuadCurve)curve).setControlY(this.controlY);
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
