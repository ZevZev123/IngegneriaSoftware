package prova;

import javafx.scene.Group;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Polygon;

public class EdgeFX extends Group {
    private QuadCurve curve;
    private Polygon arrow;

    private double controlOffset; // Offset per controllare il raggio di curvatura
    private double arrowLength = 10; // Lunghezza della freccia
    private double arrowWidth = 7;   // Larghezza della freccia

    public EdgeFX(NodeFX sourceNode, NodeFX targetNode, double controlOffset) {
        this.controlOffset = controlOffset; // Raggio di curvatura iniziale

        Point2D start = calculateEdgeEndpoint(
            sourceNode.getX(),
            sourceNode.getY(),
            targetNode.getX(),
            targetNode.getY(),
            20
        );

        Point2D end = calculateEdgeEndpoint(
            targetNode.getCircle().getCenterX(),
            targetNode.getCircle().getCenterY(),
            sourceNode.getCircle().getCenterX(),
            sourceNode.getCircle().getCenterY(),
            20
        );

        // Creazione della curva
        this.curve = new QuadCurve();
        this.curve.setStartX(start.getX());
        this.curve.setStartY(start.getY());
        this.curve.setEndX(end.getX());
        this.curve.setEndY(end.getY());
        updateControlPoint(); // Calcola il punto di controllo
        this.curve.setStroke(Color.BLACK);
        this.curve.setStrokeWidth(2);
        this.curve.setFill(Color.TRANSPARENT);

        // Creazione della freccia
        this.arrow = new Polygon();
        updateArrow(); // Calcola i punti della freccia

        // Aggiungi curva e freccia al gruppo
        this.getChildren().addAll(this.curve, this.arrow);

        this.setOnMouseEntered(event -> {
            this.setCursor(javafx.scene.Cursor.HAND);
        });

        // Evento per cambiare la curvatura dinamicamente in base alla posizione del mouse
        this.setOnMouseDragged(event -> {
            // Calcola la distanza del cursore dal centro della curva
            double midX = (curve.getStartX() + curve.getEndX()) / 2;
            double midY = (curve.getStartY() + curve.getEndY()) / 2;

            double dx = event.getX() - midX;
            double dy = event.getY() - midY;
            
            // Usa la distanza come nuovo offset di controllo
            this.controlOffset = Math.sqrt(dx * dx + dy * dy);
            if (dy < 0) this.controlOffset = -this.controlOffset;


            updateControlPoint(); // Aggiorna il punto di controllo
            updateArrow();        // Aggiorna la posizione della freccia
        });
    }

    private Point2D calculateEdgeEndpoint(double x1, double y1, double x2, double y2, double radius) {
        // Calcola la direzione dal nodo di partenza (x1, y1) a quello di arrivo (x2, y2)
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Calcola la lunghezza del vettore direzione
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normalizza il vettore direzione
        double unitX = dx / distance;
        double unitY = dy / distance;

        // Calcola il punto di intersezione con il bordo del cerchio
        double edgeX = x2 - unitX * radius; // Sottraiamo il raggio per fermarci al bordo
        double edgeY = y2 - unitY * radius;

        return new Point2D(edgeX, edgeY);
    }

    private void updateControlPoint() {
        // Calcola il punto di controllo della curva
        double midX = (curve.getStartX() + curve.getEndX()) / 2;
        double midY = (curve.getStartY() + curve.getEndY()) / 2;

        // Direzione ortogonale
        double dx = curve.getEndX() - curve.getStartX();
        double dy = curve.getEndY() - curve.getStartY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double offsetX = -dy / length * controlOffset;
        double offsetY = dx / length * controlOffset;

        // Imposta il punto di controllo
        curve.setControlX(midX + offsetX);
        curve.setControlY(midY + offsetY);
    }

    private void updateArrow() {
        // Calcola la posizione finale della curva
        double endX = curve.getEndX();
        double endY = curve.getEndY();
        double controlX = curve.getControlX();
        double controlY = curve.getControlY();

        // Deriva l'angolo della curva vicino alla fine
        double dx = endX - controlX;
        double dy = endY - controlY;
        double angle = Math.atan2(dy, dx);

        // Punti della freccia
        double arrowPoint1X = endX - arrowLength * Math.cos(angle - Math.PI / 6);
        double arrowPoint1Y = endY - arrowLength * Math.sin(angle - Math.PI / 6);
        double arrowPoint2X = endX - arrowLength * Math.cos(angle + Math.PI / 6);
        double arrowPoint2Y = endY - arrowLength * Math.sin(angle + Math.PI / 6);

        // Imposta i punti della freccia
        arrow.getPoints().clear();
        arrow.getPoints().addAll(
            endX, endY,
            arrowPoint1X, arrowPoint1Y,
            arrowPoint2X, arrowPoint2Y
        );
        arrow.setFill(Color.BLACK);
    }
}
