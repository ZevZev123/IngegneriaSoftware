package AutomaZ;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Node {
    public static final int RADIUS = 25;
    private Circle circle;
    private Circle circle2;
    private Text text;

    private MainPageController controller;

    private StackPane stackPane = new StackPane();
    public static ArrayList<Node> nodeList = new ArrayList<>();
    private ContextMenu contextMenuNodes;

    private Boolean isInitial = false;
    private Boolean isFinal = false;

    private String name;

    private Group group;
    
    public Node(double x, double y, String name, MainPageController controller, Boolean isInitial, Boolean isFinal) {
        this.circle = new Circle(x, y, RADIUS, Color.TRANSPARENT);
        this.circle2 = new Circle(x, y, RADIUS - (RADIUS / 4), Color.TRANSPARENT);
        this.text = new Text(x-4, y+4, "");
        
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        if(controller != null)
            this.controller = controller;
        this.name = name;
        
        setUpAll();
    }
    public Node(double x, double y, String name, Boolean isInitial, Boolean isFinal) {
        this(x, y, name, null, isInitial, isFinal);
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
                if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    if (event.isShiftDown()) { setFinal(); }
                    else { setInitial(); }
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
            if (isInitial)
                initialNode();
            else if (isFinal)
                finalNode();
            else
                resetNodeColor();
        });

        this.group.setOnMouseDragged(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY)
                changeCoordinates(event.getX(), event.getY());
        });
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
            button.setCursor(javafx.scene.Cursor.HAND);
            button.setVisible(true);
            if (isInitial) initialNodeHover();
            else if (isFinal) finalNodeHover();
            else normalNodeHover();
        });

        label.setOnMouseExited(event -> {
            button.setVisible(false);
            if (isInitial) initialNode();
            else if (isFinal) finalNode();
            else resetNodeColor();
        });
        
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                textFieldRename();
            }
        });

        button.setOnMouseEntered(event -> {
            button.setCursor(javafx.scene.Cursor.HAND);
            button.setVisible(true);
            if (isInitial) initialNodeHover();
            else if (isFinal) finalNodeHover();
            else normalNodeHover();
        });

        button.setOnMouseExited(event -> {
            button.setVisible(false);
            if (isInitial) initialNode();
            else if (isFinal) finalNode();
            else resetNodeColor();
        });

        button.setOnAction(event -> {
            deleteNode();
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

    private void resetNodeColor() {
        resetLabelStyleClass();
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
        this.circle.setStroke(Color.BLACK);
        this.circle2.setStroke(Color.TRANSPARENT);
        this.circle.setStrokeWidth(3);
        this.circle2.setStrokeWidth(3);
    }

    private void updateNodeColor() {
        resetNodeColor();
        if (isFinal) finalNode();
        if (isInitial) initialNode();
    }
    
    private void normalNodeHover(){
        if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().add("labelHover");
        if (isFinal) this.circle2.setFill(Color.LIGHTGRAY);
        else this.circle.setFill(Color.LIGHTGRAY);
    }
    
    private void initialNode() {
        if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().add("initial");
        this.circle.setFill(Color.YELLOW);
        this.circle2.setFill(Color.TRANSPARENT);
    }
    
    private void initialNodeHover() {
        if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().add("initialHover");
        this.circle.setFill(Color.GOLD);
    }
    
    private void finalNode() {
        if (this.stackPane.getChildren().get(0) instanceof Label label)
            label.getStyleClass().add("final");
        this.circle2.setStroke(Color.BLACK);
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
    }
    
    private void finalNodeHover() {
        if (this.stackPane.getChildren().get(0) instanceof Label label) {
            label.getStyleClass().add("final");
            label.getStyleClass().add("labelHover");
        }
        this.circle2.setStroke(Color.BLACK);
        this.circle2.setFill(Color.LIGHTGRAY);
    }

    private void updateToolTip() {
        Tooltip tooltip = new Tooltip(this.name);
        Tooltip.install(this.group, tooltip);
    }

    private void setGroup() { this.group = new Group(this.circle, this.circle2, this.text); }

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
        textField.selectAll();
        
        this.stackPane.getChildren().clear();
        this.stackPane.getChildren().add(textField);

        textField.setOnAction(e -> {
            if (!textField.getText().isEmpty()) {
                label.setText(textField.getText());
                setName(textField.getText());
            }
            this.stackPane.getChildren().clear();
            this.stackPane.getChildren().addAll(label, button);
            StackPane.setAlignment(button, Pos.TOP_RIGHT);
        });

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                if (!textField.getText().isEmpty()) {
                    label.setText(textField.getText());
                    setName(textField.getText());
                }
                this.stackPane.getChildren().clear();
                this.stackPane.getChildren().addAll(label, button);
                StackPane.setAlignment(button, Pos.TOP_RIGHT);
            }
        });

        textField.requestFocus();
    }

    public void setName(String name) {
        if (this.controller != null) { this.controller.isSaved = false; }
        this.name = name;
        if (contextMenuNodes != null)
            contextMenuNodes.getItems().get(0).setText("Cancella Nodo "+this.name);
        text.setX(circle.getCenterX()-6);
        if (name.length() > 2) {
            text.setText(name.substring(0,2)+"...");
            text.setX(circle.getCenterX()-10);
        } else if (name.length() == 2) {
            text.setText(name);
        } else if (name.length() == 1) {
            text.setText(name);
            text.setX(circle.getCenterX()-4);
        }
        updateToolTip();
    }

    public void setInitial() {
        // Controlla tutti i node e se c'è un nodo iniziale lo toglie
        if (nodeList != null)
            for (Node node: nodeList)
                if (node.isNodeInitial() && node != this)
                    node.setNotInitial();
        resetNodeColor();
        this.isInitial = !this.isInitial;
        if (this.isFinal) finalNode();
        if (this.isInitial) initialNodeHover();
        else normalNodeHover();
    }

    public void setNotInitial() {
        resetNodeColor();
        this.isInitial = false;
        if (this.isFinal) finalNode();
    }

    public void setFinal() {
        resetNodeColor();
        isFinal = !isFinal;
        if (isFinal) {
            if (isInitial) {
                finalNode();
                initialNodeHover();
            }
            else finalNodeHover();
        }
        else {
            if (isInitial) initialNodeHover();
            else normalNodeHover();
        }
    }

    public void changeCoordinates(double x, double y) {
        this.circle.setCenterX(x);
        this.circle.setCenterY(y);
        this.circle2.setCenterX(x);
        this.circle2.setCenterY(y);
        if (name.length() > 1) this.text.setX(x-6);
        else this.text.setX(x-4);
        this.text.setY(y+4);
        if (this.controller != null) { this.controller.coordinatesChanged(); }
    }

    public Boolean deleteNode() {
        if (this.controller != null) { this.controller.delete(this); }
        if (this.group.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.group);
        if (this.stackPane.getParent() instanceof javafx.scene.layout.Pane parent)
            parent.getChildren().remove(this.stackPane);
        if(nodeList != null && nodeList.contains(this)) { nodeList.remove(this); }
        return true;
    }
    
    public void setContextMenuNodesList(ContextMenu contextMenuNodi) { this.contextMenuNodes = contextMenuNodi; }
    public void setController(MainPageController controller) { this.controller = controller; }

    public Text getText() { return this.text; }
    public String getName() { return this.name; }
    public Group getGroup() { return this.group; }
    public double[] getCoordinates() { return new double[] {this.circle.getCenterX(), this.circle.getCenterY()}; }
    public StackPane getStackPane() { return this.stackPane; }

    public boolean isNodeInitial() { return this.isInitial; }
    public boolean isNodeFinal() { return this.isFinal; }
}
