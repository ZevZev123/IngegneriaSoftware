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
import javafx.scene.text.TextAlignment;

public class Edge {
    private Node start;
    private Node end;
    private Shape curve;
    private Shape hoverCurve;
    private Polygon arrow;
    private Group group;
    private double[] control;
    private String name;

    private StackPane stackPane;

    private List<Edge> edgeList = new ArrayList<>();

    private Color color = Color.BLACK;

    public Edge(Node start, Node end, String name, double controlX, double controlY) {
        this.start = start;
        this.end = end;
        this.name = name;

        control = new double[]{controlX, controlY};

        arrow = new Polygon();
        arrow.setFill(this.color);

        if(start != end) {
            curve = initQuad((QuadCurve)curve);
            hoverCurve = initQuad((QuadCurve)hoverCurve);
        }
        else {
            curve = initArc((Arc)curve);
            hoverCurve = initArc((Arc)hoverCurve);
        }

        curve.setStroke(this.color);
        curve.setFill(null);
        curve.setStrokeWidth(3);

        hoverCurve.setStroke(Color.TRANSPARENT);
        hoverCurve.setFill(null);
        hoverCurve.setStrokeWidth(15);

        group = new Group(curve, arrow, hoverCurve);

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
            toggleHover(true);
        });

        label.setOnMouseExited(event -> {
            button.setVisible(false);
            toggleHover(false);
        });
        
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                textFieldRename();
            }
        });

        button.setOnMouseEntered(event -> {
            button.setCursor(javafx.scene.Cursor.HAND);
            button.setVisible(true);
            toggleHover(true);
        });

        button.setOnMouseExited(event -> {
            button.setVisible(false);
            toggleHover(false);
        });

        button.setOnAction(event -> {
            deleteEdge();
        });
    }

    public void textFieldRename() {
        Label label;
        Button button;
        if (this.stackPane.getChildren().get(0) instanceof Label labelPane) {
            label = labelPane;
        } else { return; }
        if (this.stackPane.getChildren().get(1) instanceof Button buttonPane) {
            button = buttonPane;
        } else { return; }

        TextField textField = new TextField(this.name);
        textField.setPrefWidth(label.getWidth());
        textField.getStyleClass().add("textField");
        textField.setPadding(new Insets(0, 0, 0, 10));
        
        this.stackPane.getChildren().clear();
        this.stackPane.getChildren().add(textField);

        textField.setOnAction(e -> {
            if (!textField.getText().isEmpty()) {
                label.setText(textField.getText());
                setName(textField.getText());
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

    private QuadCurve initQuad(QuadCurve q) {
        q = new QuadCurve();
        q.setControlX(control[0]);
        q.setControlY(control[1]);
        return q;
    }
    private Arc initArc(Arc a) {
        a = new Arc();
        a.setRadiusX(Node.RADIUS * 0.8);
        a.setRadiusY(Node.RADIUS * 0.8);
        a.setLength(230);
        a.setType(ArcType.OPEN);
        a = setArc(a);
        return a;
    }

    private void addDragHandler() {
        hoverCurve.setOnMouseDragged((MouseEvent event) -> {
            this.control[0] = event.getX();
            this.control[1] = event.getY();
            updateEdge();
        });
    
        hoverCurve.setOnMouseEntered(event -> {
            hoverCurve.setCursor(javafx.scene.Cursor.HAND);
            toggleHover(true);
        });
    
        hoverCurve.setOnMouseExited(event -> {
            toggleHover(false);
        });
    }
    private void toggleHover(boolean flag) {
        Color c = (flag) ? Color.RED : color;
        curve.setStroke(c);
        arrow.setStroke(c);
        arrow.setFill(c);
        if (this.stackPane.getChildren().get(0) instanceof Label label && flag) 
            label.getStyleClass().add("labelHover");
        else if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().remove("labelHover");
    }

    private void updateToolTip() {
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(this.group, tooltip);
    }

    public void updateEdge() {
        if(start == end) {
            curve = setArc((Arc)curve);
            hoverCurve = setArc((Arc)hoverCurve);
            updateArrow(arcEdgePoint());
        }
        else {
            double[] sourcePoint = calculateEdgePoint(start.getX(), start.getY());
            double[] targetPoint = calculateEdgePoint(end.getX(), end.getY());
            
            curve = setQuad((QuadCurve)curve, sourcePoint, targetPoint);
            hoverCurve = setQuad((QuadCurve)hoverCurve, sourcePoint, targetPoint);
    
            updateArrow(targetPoint);
        }
    }
    private Arc setArc(Arc a) {
        double stX = start.getX(), stY = start.getY();
        double dx = control[0] - stX, dy = control[1] - stY;
        double m = Math.sqrt(dx * dx + dy * dy);
        
        a.setCenterX(stX + Node.RADIUS * 1.4 * (dx / m));
        a.setCenterY(stY + Node.RADIUS * 1.4 * (dy / m));
        a.setStartAngle(145 + Math.atan2(dx, dy) * 180.0 / Math.PI);

        return a;
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
    private double[] arcEdgePoint() {
        double centerX = ((Arc)curve).getCenterX();
        double centerY = ((Arc)curve).getCenterY();
        double startAngleRad = Math.toRadians(((Arc)curve).getStartAngle() - 10);

        return new double[] {
            centerX + Node.RADIUS * 0.8 * Math.sin(startAngleRad),
            centerY + Node.RADIUS * 0.8 * Math.cos(startAngleRad)
        };
    }
    private double[] calculateEdgePoint(double x, double y) {
        double angle = Math.atan2(control[1] - y, control[0] - x);
        return new double[] {
                x + Node.RADIUS * Math.cos(angle),
                y + Node.RADIUS * Math.sin(angle)
        };
    }
    private QuadCurve setQuad(QuadCurve q, double[] s, double[] t) {
        q.setStartX(s[0]);
        q.setStartY(s[1]);
        q.setControlX(control[0]);
        q.setControlY(control[1]);
        q.setEndX(t[0]);
        q.setEndY(t[1]);
        return q;
    }

    public Boolean deleteEdge() {
        if (this.group.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.group);
        if (this.stackPane.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.stackPane);
        if (edgeList != null && edgeList.contains(this)) edgeList.remove(this);
        return true;
    }
    
    private void setName(String name) { this.name = name; }
    public void setEdgeList(List<Edge> edgeList) { this.edgeList = edgeList; }
    public void setColor (Color color) {
        this.color = color;
        toggleHover(false);
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
