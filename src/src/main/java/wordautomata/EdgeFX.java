package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class EdgeFX {
    private QuadCurve curve;
    private Text text;
    private Rectangle backgroud;

    private NodeFX start;
    private NodeFX end;

    private String name;

    private StackPane stackPane;
    private List<EdgeFX> edgeList = new ArrayList<>();
    
    private Group group;
    
    public EdgeFX(NodeFX start, NodeFX end, String name) {
        this.line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        this.line.setStroke(Color.BLACK);
        this.line.setStrokeWidth(2);

        this.text = new Text(meanValue(start.getX(), end.getX()), meanValue(start.getY(), end.getY())+3, name);
        this.text.setFill(Color.BLACK);
        
        this.backgroud = new Rectangle(meanValue(start.getX(), end.getX()), meanValue(start.getY(), end.getY())-8, Color.web("#f4f4f4"));
        this.backgroud.setWidth(name.length()*7);
        this.backgroud.setHeight(15);

        this.start = start;
        this.end = end;
        this.name = name;

        setGroup();
        setLabel();
        setListener();
    }

    private void setListener() {
        this.group.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                deleteEdge();
            }
        });

        this.group.setOnMouseEntered(event -> {
            this.group.setCursor(javafx.scene.Cursor.HAND);
            edgeHover();
        });

        this.group.setOnMouseExited(event -> {
            edgeNotHover();
        });
    }

    private void edgeNotHover() {
        this.line.setStroke(Color.BLACK);
        this.text.setFill(Color.BLACK);
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().remove("labelHover");
        }
    }
    
    private void edgeHover() {
        this.line.setStroke(Color.RED);
        this.text.setFill(Color.RED);
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("labelHover");
        }
    }

    public Boolean deleteEdge() {
        if (this.group.getParent() instanceof javafx.scene.layout.Pane parent)
        parent.getChildren().remove(this.group);
        if (this.stackPane.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.stackPane);
        if (edgeList != null && edgeList.contains(this)) edgeList.remove(this);
        return true;
    }

    public Line getLine() {
        return this.line;
    }

    public String getName() {
        return this.name;
    }

    public void setStart(NodeFX start) {
        this.start = start;
    }

    public void setEnd(NodeFX end) {
        this.end = end;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateCoordinates() {
        this.line.setStartX(start.getX());
        this.line.setStartY(start.getY());
        this.line.setEndX(end.getX());
        this.line.setEndY(end.getY());
        this.text.setX(meanValue(start.getX(), end.getX())-(3*this.name.length()));
        this.text.setY(meanValue(start.getY(), end.getY())+3);
        this.backgroud.setX(meanValue(start.getX(), end.getX())-(3.5*this.name.length()));
        this.backgroud.setY(meanValue(start.getY(), end.getY())-8);
        this.backgroud.setWidth(name.length()*7);
    }

    public void setEdgeList(List<EdgeFX> edgeList) {
        this.edgeList = edgeList;
    }

    private void setGroup() {
        this.group = new Group(this.line, this.backgroud, this.text);
    }

    public Group getGroup() {
        return this.group;
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
                    label.setText(textField.getText());
                    setName(textField.getText());
                    this.text.setText(this.name);
                    updateCoordinates();
                    this.stackPane.getChildren().clear();
                    this.stackPane.getChildren().addAll(label, button);
                    StackPane.setAlignment(button, Pos.TOP_RIGHT);
                });
    
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        label.setText(textField.getText());
                        setName(textField.getText());
                        this.text.setText(this.name);
                        updateCoordinates();
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
        });

        button.setOnMouseExited(event -> {
            button.setVisible(false);
        });

        button.setOnAction(event -> {
            deleteEdge();
        });
    }

    public StackPane getStackPane() {
        return this.stackPane;
    }

    private double meanValue(double x, double y) {
        double result = 0;
        if (x > y) result = y + (x - y) / 2;
        else result = x + (y - x) / 2;

        return result;
    }

    public NodeFX[] getNodes() {
        return new NodeFX[] {this.start, this.end};
    }
}
