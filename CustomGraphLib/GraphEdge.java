import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class GraphEdge {
    private GraphNode startNode;
    private GraphNode endNode;
    private Line line;
    private Text valueText;

    public GraphEdge(GraphNode startNode, GraphNode endNode, String value) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.line = new Line();
        this.valueText = new Text(value);

        // Bind the line's start and end points to the nodes' positions
        line.startXProperty().bind(Bindings.createDoubleBinding(() ->
            calculateIntersectionX(startNode, endNode), startNode.getCircle().centerXProperty(), startNode.getCircle().centerYProperty(), endNode.getCircle().centerXProperty(), endNode.getCircle().centerYProperty()));
        line.startYProperty().bind(Bindings.createDoubleBinding(() ->
            calculateIntersectionY(startNode, endNode), startNode.getCircle().centerXProperty(), startNode.getCircle().centerYProperty(), endNode.getCircle().centerXProperty(), endNode.getCircle().centerYProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() ->
            calculateIntersectionX(endNode, startNode), startNode.getCircle().centerXProperty(), startNode.getCircle().centerYProperty(), endNode.getCircle().centerXProperty(), endNode.getCircle().centerYProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() ->
            calculateIntersectionY(endNode, startNode), startNode.getCircle().centerXProperty(), startNode.getCircle().centerYProperty(), endNode.getCircle().centerXProperty(), endNode.getCircle().centerYProperty()));

        // Update the value text position
        updateValueTextPosition();

        // Add listeners to update the value text position when nodes are moved
        startNode.getCircle().centerXProperty().addListener((obs, oldVal, newVal) -> updateValueTextPosition());
        startNode.getCircle().centerYProperty().addListener((obs, oldVal, newVal) -> updateValueTextPosition());
        endNode.getCircle().centerXProperty().addListener((obs, oldVal, newVal) -> updateValueTextPosition());
        endNode.getCircle().centerYProperty().addListener((obs, oldVal, newVal) -> updateValueTextPosition());
    }

    public Line getLine() {
        return line;
    }

    public Text getValueText() {
        return valueText;
    }

    private void updateValueTextPosition() {
        valueText.setX((line.getStartX() + line.getEndX()) / 2);
        valueText.setY((line.getStartY() + line.getEndY()) / 2);
    }

    private double calculateIntersectionX(GraphNode fromNode, GraphNode toNode) {
        double dx = toNode.getCircle().getCenterX() - fromNode.getCircle().getCenterX();
        double dy = toNode.getCircle().getCenterY() - fromNode.getCircle().getCenterY();
        double angle = Math.atan2(dy, dx);
        return fromNode.getCircle().getCenterX() + Math.cos(angle) * fromNode.getCircle().getRadius();
    }

    private double calculateIntersectionY(GraphNode fromNode, GraphNode toNode) {
        double dx = toNode.getCircle().getCenterX() - fromNode.getCircle().getCenterX();
        double dy = toNode.getCircle().getCenterY() - fromNode.getCircle().getCenterY();
        double angle = Math.atan2(dy, dx);
        return fromNode.getCircle().getCenterY() + Math.sin(angle) * fromNode.getCircle().getRadius();
    }
}