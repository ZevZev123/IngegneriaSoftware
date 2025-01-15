package prova;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class NodeFX {
    private Circle circle;
    private Circle circle2;
    private Text text;

    private String name;

    private Group group;

    public NodeFX(Circle circle, Circle circle2, Text text) {
        this.circle = circle;
        this.circle2 = circle2;
        this.text = text;

        this.name = text.getText();
        setGroup();
    }
    
    public NodeFX(double x, double y, double radius, String name) {
        this.circle = new Circle(x, y, radius, Color.TRANSPARENT);
        this.circle2 = new Circle(x, y, radius + 5, Color.TRANSPARENT);
        
        this.circle.setFill(Color.TRANSPARENT);
        this.circle2.setFill(Color.TRANSPARENT);
        this.circle.setStroke(Color.BLACK);
        this.circle2.setStroke(Color.BLACK);
        this.circle.setStrokeWidth(2);
        this.circle2.setStrokeWidth(2);

        this.text = new Text(x-4, y+4, "");
        
        setName(name);
        setGroup();
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
        group = new Group(this.circle, this.circle2, this.text);
    }

    public Group getGroup() {
        return this.group;
    }

    public void changeCoordinates(double x, double y) {
        
    }
}
