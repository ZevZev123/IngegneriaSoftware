package prova;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class NodeFX {
    private Circle circle;
    private Circle circle2;
    private Text text;

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

        normalNode();
        setName(text.getText());
        listenerAdd();
    }
    
    public NodeFX(double x, double y, double radius, String name) {
        this.circle = new Circle(x, y, radius, Color.TRANSPARENT);
        this.circle2 = new Circle(x, y, radius + 5, Color.TRANSPARENT);
        
        this.text = new Text(x-4, y+4, "");
        
        setGroup();

        normalNode();
        setName(name);
        listenerAdd();
    }
    
    private void listenerAdd() {
        this.getGroup().setOnMouseClicked(event -> {
            normalNode();
            if (event.getClickCount() == 2 && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                isInitial = true;
                isFinal = false;
                initialNode();
            }
            if (event.getClickCount() == 2 && event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                isInitial = false;
                isFinal = true;
                finalNode();
            }
        });

        this.text.setOnMouseEntered(event -> {
            this.getGroup().setCursor(javafx.scene.Cursor.HAND);
            if (isInitial) {
                initialNodeHover();
            } else if (isFinal) {
                finalNodeHover();
            } else {
                normalNodeHover();
            }
        });
            
        this.getGroup().setOnMouseExited(event -> {
            if (isInitial) {
                initialNode();
            } else if (isFinal) {
                finalNode();
            } else {
                normalNode();
            }
        });
    }

    private void normalNode() {
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
        this.circle.setStroke(Color.BLACK);
        this.circle2.setStroke(Color.TRANSPARENT);
        this.circle.setStrokeWidth(2);
        this.circle2.setStrokeWidth(2);

        if (this.isInitial) initialNode();
        if (this.isFinal) finalNode();
    }
    
    private void normalNodeHover(){
        if (isFinal) this.circle2.setStroke(Color.LIGHTGRAY);
        else this.circle.setStroke(Color.LIGHTGRAY);
    }
    
    private void initialNode() {
        this.circle.setFill(Color.YELLOW);
    }
    
    private void initialNodeHover() {
        this.circle.setFill(Color.GOLD);
    }
    
    private void finalNode() {
        this.circle2.setStroke(Color.BLACK);
    }
    
    private void finalNodeHover() {
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
        
    }
}
