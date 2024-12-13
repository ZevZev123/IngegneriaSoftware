import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;

public class GraphNode {
    private String name;
    private boolean isFinal;
    private boolean isInitial;
    
    private Circle circle;
    private Text text;
    private double mouseX;
    private double mouseY;

    public GraphNode(String name, double x, double y) {
        this.name = name;
        this.circle = new Circle(x, y, 20);
        this.circle.setFill(Color.WHITE); // Set fill color to white
        this.circle.setStroke(Color.BLACK); // Set stroke color to black
        this.text = new Text(x - 10, y + 5, name);

        // Add mouse event handlers for dragging
        circle.setOnMousePressed(this::onMousePressed);
        circle.setOnMouseDragged(this::onMouseDragged);
    }

    public String getName() {
        return name;
    }

    public Circle getCircle() {
        return circle;
    }

    public Text getText() {
        return text;
    }

    private void onMousePressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    private void onMouseDragged(MouseEvent event) {
        double deltaX = event.getSceneX() - mouseX;
        double deltaY = event.getSceneY() - mouseY;

        circle.setCenterX(circle.getCenterX() + deltaX);
        circle.setCenterY(circle.getCenterY() + deltaY);
        text.setX(text.getX() + deltaX);
        text.setY(text.getY() + deltaY);

        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }
}