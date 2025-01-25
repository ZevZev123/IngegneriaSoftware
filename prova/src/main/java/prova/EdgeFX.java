package prova;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;

public class EdgeFX {
    private final NodeFX sourceNode;
    private final NodeFX targetNode;
    private final QuadCurve curve;
    private final Polygon arrow;
    private final Group group;

    public EdgeFX(NodeFX sourceNode, NodeFX targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;

        // Crea la curva
        curve = new QuadCurve();
        curve.setStroke(Color.BLACK);
        curve.setFill(null);
        curve.setStrokeWidth(2);

        curve.setControlX(200);
        curve.setControlY(150);

        // Crea la freccia
        arrow = new Polygon();
        arrow.setFill(Color.BLACK);

        // Gruppo per curva + freccia
        group = new Group(curve, arrow);

        updateEdge();
        addDragHandler();
    }

    private void updateEdge() {
        // Calcola i punti iniziali e finali ai bordi dei nodi
        double[] sourcePoint = calculateEdgePoint(
                sourceNode.getX(), sourceNode.getY(),
                curve.getControlX(), curve.getControlY(),
                20
        );
    
        double[] targetPoint = calculateEdgePoint(
                targetNode.getX(), targetNode.getY(),
                curve.getControlX(), curve.getControlY(),
                20
        );
    
        // Imposta i punti di partenza, controllo e fine della curva
        curve.setStartX(sourcePoint[0]);
        curve.setStartY(sourcePoint[1]);
        curve.setEndX(targetPoint[0]);
        curve.setEndY(targetPoint[1]);
    
        // Aggiorna la freccia
        updateArrow(targetPoint, sourcePoint);
    }    

    private void updateArrow(double[] target, double[] source) {
        // Calcola la direzione basata sulla tangente della curva nel punto finale
        double dx = target[0] - curve.getControlX();
        double dy = target[1] - curve.getControlY();
        double angle = Math.atan2(dy, dx);
    
        double arrowLength = 10; // Lunghezza della freccia
        double arrowWidth = 5;  // Larghezza della freccia
    
        // Calcola i vertici della freccia
        double x1 = target[0] - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = target[1] - arrowLength * Math.sin(angle - Math.PI / 6);
    
        double x2 = target[0] - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = target[1] - arrowLength * Math.sin(angle + Math.PI / 6);
    
        // Aggiorna i punti del poligono della freccia
        arrow.getPoints().setAll(
                target[0], target[1], // Punto della punta della freccia
                x1, y1,               // Punto alla sinistra della freccia
                x2, y2                // Punto alla destra della freccia
        );
    }

    private void addDragHandler() {
        curve.setOnMouseDragged((MouseEvent event) -> {
            // Aggiorna la posizione del punto di controllo
            curve.setControlX(event.getX());
            curve.setControlY(event.getY());
    
            System.out.println(curve.getControlX() + "\t" + curve.getControlY());

            // Aggiorna dinamicamente la curva e i punti di contatto
            updateEdge();
        });
    
        curve.setOnMouseEntered(event -> {
            curve.setCursor(javafx.scene.Cursor.HAND);
        });
    }

    private double[] calculateEdgePoint(double x1, double y1, double x2, double y2, double radius) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        return new double[]{
                x1 + radius * Math.cos(angle),
                y1 + radius * Math.sin(angle)
        };
    }

    public Group getGroup() {
        return group;
    }

    public void coordinatesChanged() {
        updateEdge();
    }
}
