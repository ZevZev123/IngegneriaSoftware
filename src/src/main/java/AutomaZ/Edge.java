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
import javafx.scene.shape.Shape;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Edge {
    private Node start;
    private Node end;
    private Shape curve;
    private static final double ARC_LENGTH = 230;
    private Polygon arrow;
    private Group group;
    private double[] control;
    private Text text;
    private String name;

    private StackPane stackPane;

    private List<Edge> edgeList = new ArrayList<>();

    private Color color = Color.BLACK;

    public Edge(Node start, Node end, String name, double controlX, double controlY) {
        this.start = start;
        this.end = end;
        this.text = new Text(this.name);
        this.name = name;

        control = new double[]{controlX, controlY};

        arrow = new Polygon();
        arrow.setFill(this.color);

        if(start != end) {
            curve = new QuadCurve();
            ((QuadCurve)curve).setControlX(control[0]);
            ((QuadCurve)curve).setControlY(control[1]);
        }
        else {
            curve = new Arc();
            ((Arc)curve).setRadiusX(Node.RADIUS * 0.8);
            ((Arc)curve).setRadiusY(Node.RADIUS * 0.8);
            ((Arc)curve).setLength(ARC_LENGTH);
            ((Arc)curve).setType(ArcType.OPEN);
            setArc();
        }
        curve.setStroke(this.color);
        curve.setFill(null);
        curve.setStrokeWidth(3);

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

    private void updateArrow(double[] target) {
        double dx, dy;
        if(start == end) {
            dx = start.getX() - target[0];
            dy = start.getY() - target[1];
        }
        else {
            dx = target[0] - control[0];
            dy = target[1] - control[1];
        }
        double angle = Math.atan2(dy, dx);
    
        double x1 = target[0] - 10 * Math.cos(angle - Math.PI / 6);
        double y1 = target[1] - 10 * Math.sin(angle - Math.PI / 6);
    
        double x2 = target[0] - 10 * Math.cos(angle + Math.PI / 6);
        double y2 = target[1] - 10 * Math.sin(angle + Math.PI / 6);
    
        arrow.getPoints().setAll(
                target[0], target[1], // Punto della punta della freccia
                x1, y1,               // Punto alla sinistra della freccia
                x2, y2                // Punto alla destra della freccia
        );
    }

    private void addDragHandler() {
        curve.setOnMouseDragged((MouseEvent event) -> {
            setControl(event.getX(), event.getY());
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
        this.curve.setStroke(this.color);
        this.arrow.setStroke(this.color);
        this.arrow.setFill(this.color);
        this.text.setFill(this.color);
        if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().remove("labelHover");
    }

    private void edgeHover() {
        this.curve.setStroke(Color.RED);
        this.arrow.setStroke(Color.RED);
        this.arrow.setFill(Color.RED);
        this.text.setFill(Color.RED);
        if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().add("labelHover");
    }

    private double[] calculateEdgePoint(double x, double y) {
        double angle = Math.atan2(control[1] - y, control[0] - x);
        return new double[] {
                x + Node.RADIUS * Math.cos(angle),
                y + Node.RADIUS * Math.sin(angle)
        };
    }

    private void updateToolTip() {
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(this.group, tooltip);
    }

    public void updateEdge() {
        if(start == end) {
            setArc();
            updateArrow(arcEdgePoint());
            return;
        }

        double[] sourcePoint = calculateEdgePoint(start.getX(), start.getY());
        double[] targetPoint = calculateEdgePoint(end.getX(), end.getY());
    
        ((QuadCurve)curve).setStartX(sourcePoint[0]);
        ((QuadCurve)curve).setStartY(sourcePoint[1]);
        ((QuadCurve)curve).setControlX(control[0]);
        ((QuadCurve)curve).setControlY(control[1]);
        ((QuadCurve)curve).setEndX(targetPoint[0]);
        ((QuadCurve)curve).setEndY(targetPoint[1]);
    
        updateArrow(targetPoint);
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
        double stX = start.getX(), stY = start.getY();
        double dx = control[0] - stX, dy = control[1] - stY;
        double m = Math.sqrt(dx * dx + dy * dy);
        
        ((Arc)curve).setCenterX(stX + Node.RADIUS * 1.4 * (dx / m));
        ((Arc)curve).setCenterY(stY + Node.RADIUS * 1.4 * (dy / m));
        ((Arc)curve).setStartAngle(145 + Math.atan2(dx, dy) * 180.0 / Math.PI);
    }

    private double[] arcEdgePoint() {
        double centerX = ((Arc)curve).getCenterX();
        double centerY = ((Arc)curve).getCenterY();
        double startAngleRad = Math.toRadians(((Arc)curve).getStartAngle() - 10);

        return new double[] {
            centerX + Node.RADIUS * 0.8 * Math.sin(startAngleRad),
            centerY + Node.RADIUS * 0.8 * Math.cos(startAngleRad)
        };
    }

    public void setControl(double controlX, double controlY) {
        this.control[0] = controlX;
        this.control[1] = controlY;
    }
    
    private void setName(String name) { this.name = name; }
    public void setEdgeList(List<Edge> edgeList) { this.edgeList = edgeList; }
    public void setColor (Color color) {
        this.color = color;
        edgeNotHover();
    }
    
    public StackPane getStackPane() { return this.stackPane; }
    public Node[] getNodes() { return new Node[] {this.start, this.end}; }
    public Node getStartNode() { return this.start; }
    public Node getEndNode() { return this.end; }
    public Group getGroup() { return group; }
    public double getControlX() { return control[0]; }
    public double getControlY() { return control[1]; }
    public String getValue() { return this.name; }
}
