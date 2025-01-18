package wordautomata;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class EdgeFX {
    private Line line;

    private NodeFX start;
    private NodeFX end;

    private String name;

    public EdgeFX(NodeFX start, NodeFX end, String name) {
        this.line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        
        this.line.setStroke(Color.BLACK);
        this.line.setStrokeWidth(2);
        this.start = start;
        this.end = end;
        this.name = name;
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
    }
}
