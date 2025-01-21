package wordautomata;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class NodeFX {
    private Circle circle;
    private Circle circle2;
    private Text text;

    private PrimaryController controller;

    private StackPane stackPane = new StackPane();
    private List<NodeFX> nodeList = new ArrayList<>();

    private Boolean isInitial = false;
    private Boolean isFinal = false;

    private String name;

    private Group group;

    public NodeFX(Circle circle, Circle circle2, Text text, Boolean isInitial, Boolean isFinal, PrimaryController controller) {
        this.circle = circle;
        this.circle2 = circle2;
        this.text = text;
        this.name = name;

        this.controller = controller;

        if (isInitial != null) this.isInitial = isInitial;
        if (isFinal != null) this.isFinal = isFinal;

        setUpAll();
    }
    
    public NodeFX(double x, double y, double radius, String name, PrimaryController controller) {
        this.circle = new Circle(x, y, radius + 5, Color.TRANSPARENT);
        this.circle2 = new Circle(x, y, radius, Color.TRANSPARENT);
        this.text = new Text(x-4, y+4, "");
        this.name = name;

        this.controller = controller;

        setUpAll();
    }
    
    private void setUpAll() {
        setName(this.name);
        setGroup();
        setLabel();
        listenerAdd();
        
        updateNodeColor();
        
        updateToolTip();
    }

    private void listenerAdd() {
        this.group.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 ){
                Boolean isThereInitial = false;
                for (NodeFX node: this.nodeList) {
                    if (node.isNodeInitial()) isThereInitial = true;
                }
                if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && (!isThereInitial  || this.isInitial)) {
                    updateNodeColor();
                    if (isFinal) isFinal = false;
                    isInitial = !isInitial;
                    if (isInitial) initialNodeHover();
                    else normalNodeHover();
                }
                else if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    updateNodeColor();
                    if (isInitial) isInitial = false;
                    isFinal = !isFinal;
                    if (isFinal) finalNodeHover();
                    else normalNodeHover();
                }
            }
        });

        this.group.setOnMouseEntered(event -> {
            this.group.setCursor(javafx.scene.Cursor.HAND);
            if (isInitial) {
                initialNodeHover();
            } else if (isFinal) {
                finalNodeHover();
            } else {
                normalNodeHover();
            }
        });
            
        this.group.setOnMouseExited(event -> {
            if (isInitial) {
                initialNode();
            } else if (isFinal) {
                finalNode();
            } else {
                updateNodeColor();
            }
        });

        this.group.setOnMouseDragged(event -> {
            changeCoordinates(event.getX(), event.getY());
        });
    }

    private void resetLabelStyleClass() {
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            while (label.getStyleClass().contains("initial")) label.getStyleClass().remove("initial");
            while (label.getStyleClass().contains("initialHover")) label.getStyleClass().remove("initialHover");
            while (label.getStyleClass().contains("labelHover")) label.getStyleClass().remove("labelHover");
            while (label.getStyleClass().contains("final")) label.getStyleClass().remove("final");
        }
    }

    private void updateNodeColor() {
        resetLabelStyleClass();
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
        this.circle.setStroke(Color.BLACK);
        this.circle2.setStroke(Color.TRANSPARENT);
        this.circle.setStrokeWidth(2);
        this.circle2.setStrokeWidth(2);
    }
    
    private void normalNodeHover(){
        resetLabelStyleClass();
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("labelHover");
        }
        if (isFinal) this.circle2.setFill(Color.LIGHTGRAY);
        else this.circle.setFill(Color.LIGHTGRAY);
    }
    
    private void initialNode() {
        resetLabelStyleClass();
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("initial");
        }
        this.circle.setFill(Color.YELLOW);
        this.circle2.setFill(Color.TRANSPARENT);
    }
    
    private void initialNodeHover() {
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("initialHover");
        }
        this.circle.setFill(Color.GOLD);
    }
    
    private void finalNode() {
        resetLabelStyleClass();
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("final");
        }
        this.circle2.setStroke(Color.BLACK);
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
    }
    
    private void finalNodeHover() {
        resetLabelStyleClass();
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("final");
            label.getStyleClass().add("labelHover");
        }
        this.circle2.setStroke(Color.BLACK);
        this.circle2.setFill(Color.LIGHTGRAY);
    }

    public void setCircle(Circle cirle) {
        this.circle = circle;
    }

    public void setCircle2(Circle circle2) {
        this.circle2 = circle2;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Circle getCircle() {
        return this.circle;
    }

    public Circle getCircle2() {
        return this.circle2;
    }

    public Text getText() {
        return this.text;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        text.setX(circle.getCenterX()-6);
        if (name.length() > 2) {
            text.setText(name.substring(0,1)+"...");
        } else if (name.length() == 2) {
            text.setText(name);
        } else if (name.length() == 1) {
            text.setText(name);
            text.setX(circle.getCenterX()-4);
        }
    }

    private void setGroup() {
        this.group = new Group(this.circle2, this.circle, this.text);
    }

    public Group getGroup() {
        return this.group;
    }

    public void changeCoordinates(double x, double y) {
        this.circle.setCenterX(x);
        this.circle.setCenterY(y);
        this.circle2.setCenterX(x);
        this.circle2.setCenterY(y);
        if (name.length() > 1) this.text.setX(x-6);
        else this.text.setX(x-4);
        this.text.setY(y+4);
        controller.coordinatesChanged();
    }

    public double[] getCoordinates() {
        return new double[] {this.circle.getCenterX(), this.circle.getCenterY()};
    }

    public double getX() {
        return this.circle.getCenterX();
    }

    public double getY() {
        return this.circle.getCenterY();
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
            if (isInitial) initialNodeHover();
            else if (isFinal) finalNodeHover();
            else normalNodeHover();
        });

        label.setOnMouseExited(event -> {
            button.setVisible(false);
            if (isInitial) initialNode();
            else if (isFinal) finalNode();
            else updateNodeColor();
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
                    this.stackPane.getChildren().clear();
                    this.stackPane.getChildren().addAll(label, button);
                    StackPane.setAlignment(button, Pos.TOP_RIGHT);
                });
    
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        label.setText(textField.getText());
                        setName(textField.getText());
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
            if (isInitial) initialNodeHover();
            else if (isFinal) finalNodeHover();
            else normalNodeHover();
        });

        button.setOnMouseExited(event -> {
            button.setVisible(false);
            if (isInitial) initialNode();
            else if (isFinal) finalNode();
            else updateNodeColor();
        });

        button.setOnAction(event -> {
            deleteNode();
        });
    }

    public Boolean deleteNode() {
        controller.delete(this);
        if (this.group.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.group);
        if (this.stackPane.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.stackPane);    
        if(nodeList != null && nodeList.contains(this)) {
            nodeList.remove(this);
        }
        return true;
    }

    public StackPane getStackPane() {
        return this.stackPane;
    }

    private void updateToolTip() {
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(this.group, tooltip);
    }

    public Boolean isNodeInitial() {
        return this.isInitial;
    }

    public Boolean isNodeFinal() {
        return this.isFinal;
    }

    public void setListFX(List<NodeFX> nodeList) {
        this.nodeList = nodeList;
    }

    public List<NodeFX> getListFX() {
        return this.nodeList;
    }
}
