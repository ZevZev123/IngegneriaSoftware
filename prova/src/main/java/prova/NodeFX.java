package prova;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class NodeFX {
    private Circle circle;
    private Circle circle2;
    private Text text;

    private StackPane stackPane = new StackPane();

    private Boolean isInitial = false;
    private Boolean isFinal = false;

    private String name;

    private Group group;

    public NodeFX(Circle circle, Circle circle2, Text text, Boolean isInitial, Boolean isFinal) {
        this.circle = circle;
        this.circle2 = circle2;
        this.text = text;

        if (isInitial != null) this.isInitial = isInitial;
        if (isFinal != null) this.isFinal = isFinal;

        setGroup();
        setLabel();

        updateNodeColor();
        setName(text.getText());
        listenerAdd();
    }
    
    public NodeFX(double x, double y, double radius, String name) {
        this.circle = new Circle(x, y, radius, Color.TRANSPARENT);
        this.circle2 = new Circle(x, y, radius + 5, Color.TRANSPARENT);
        this.text = new Text(x-4, y+4, "");

        setGroup();
        setLabel();

        updateNodeColor();
        setName(name);
        listenerAdd();
    }
    
    private void listenerAdd() {
        this.group.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 ){
                updateNodeColor();
                if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    if (isFinal) isFinal = false;
                    isInitial = !isInitial;
                    if (isInitial) initialNodeHover();
                    else normalNodeHover();
                }
                else if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
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

    private void updateNodeColor() {
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
        this.circle.setStroke(Color.BLACK);
        this.circle2.setStroke(Color.TRANSPARENT);
        this.circle.setStrokeWidth(2);
        this.circle2.setStrokeWidth(2);
    }
    
    private void normalNodeHover(){
        if (isFinal) this.circle2.setFill(Color.LIGHTGRAY);
        else this.circle.setFill(Color.LIGHTGRAY);
    }
    
    private void initialNode() {
        this.circle.setFill(Color.YELLOW);
        this.circle2.setFill(Color.TRANSPARENT);
    }
    
    private void initialNodeHover() {
        this.circle.setFill(Color.GOLD);
    }
    
    private void finalNode() {
        this.circle2.setStroke(Color.BLACK);
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
    }
    
    private void finalNodeHover() {
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
        if (name.length() > 1) {
            text.setText(name.substring(0,1)+"...");
            text.setX(circle.getCenterX()-6);
        } else {
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
    }

    private void setLabel() {
        Label label = new Label(this.name);
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
    }

    public StackPane getStackPane() {
        return this.stackPane;
    }
}
