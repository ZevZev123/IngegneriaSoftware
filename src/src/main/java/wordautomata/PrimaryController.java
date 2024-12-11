package wordautomata;

import wordautomata.containers.SmartGraphDemoContainer;
import wordautomata.graph.*;
import wordautomata.graphview.*;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PrimaryController {
    @FXML private VBox nodeEdgeList;
    @FXML private VBox GraphViewBox;
    @FXML private Label statusLabel;

    @FXML private Button runButton;
    @FXML private Label history;

    @FXML
    private void initialize(){
        updateToolTip();

        Label label;
        for (int i = 1; i <= 50; i++) {
            label = new Label("Label " + i); // Crea una nuova Label
            label.setTextAlignment(TextAlignment.LEFT); // Assicura che il testo sia allineato a sinistra
            nodeEdgeList.getChildren().add(label); // Aggiungi la Label alla VBox
        }

        Graph<String, String> g = build_flower_graph();
        //Graph<String, String> g = build_flower_graph();
        System.out.println(g);
        
        SmartPlacementStrategy initialPlacement = new SmartCircularSortedPlacementStrategy();
        //SmartPlacementStrategy initialPlacement = new SmartRandomPlacementStrategy();

        ForceDirectedLayoutStrategy<String> automaticPlacementStrategy = new ForceDirectedSpringGravityLayoutStrategy<>();
        //ForceDirectedLayoutStrategy<String> automaticPlacementStrategy = new ForceDirectedSpringSystemLayoutStrategy<>();

        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(g, initialPlacement, automaticPlacementStrategy);

        /*
        After creating, you can change the styling of some element.
        This can be done at any time afterwards.
        */
        if (g.numVertices() > 0) {
            graphView.getStylableVertex("A").setStyleInline("-fx-fill: gold; -fx-stroke: brown;");
        }

        /*
        Basic usage:            
        Use SmartGraphDemoContainer if you want zoom capabilities and automatic layout toggling
        */
        //Scene scene = new Scene(graphView, 1024, 768);
        Scene scene = new Scene(new SmartGraphDemoContainer(graphView), 1024, 768);

        //Stage stage = new Stage(StageStyle.DECORATED);
        //stage.setTitle("JavaFX SmartGraph Visualization");
        //stage.setMinHeight(400);
        //stage.setMinWidth(600);
        //stage.setScene(scene);
        //stage.show();

        GraphViewBox.setVgrow(scene.getRoot(), Priority.ALWAYS);

        GraphViewBox.getChildren().add(scene.getRoot());

        /*
        IMPORTANT: Must call init() after scene is displayed, so we can have width and height values
        to initially place the vertices according to the placement strategy.
        */
        graphView.init();

        /*
        Bellow you can see how to attach actions for when vertices and edges are double-clicked
         */        
        graphView.setVertexDoubleClickAction((SmartGraphVertex<String> graphVertex) -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());
                      
            //toggle different styling
            if( !graphVertex.removeStyleClass("myVertex") ) {
                /* for the golden vertex, this is necessary to clear the inline
                css class. Otherwise, it has priority. Test and uncomment. */
                //graphVertex.setStyle(null);
                
                graphVertex.addStyleClass("myVertex");
            }
            
            //want fun? uncomment below with automatic layout
            //g.removeVertex(graphVertex.getUnderlyingVertex());
            //graphView.update();
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().element());
            //dynamically change the style when clicked; style propagated to the arrows
            if ( !graphEdge.removeStyleClass("myEdge")) {
                graphEdge.addStyleClass("myEdge");
            }
            
            // can apply different styling to the arrows programmatically.
            //graphEdge.getStylableArrow().setStyleClass("arrow");
            
            //uncomment to see edges being removed after click
            //Edge<String, String> underlyingEdge = graphEdge.getUnderlyingEdge();
            //g.removeEdge(underlyingEdge);
            //graphView.update();
        });

        /*
        Should proceed with automatic layout or keep original placement?
        If using SmartGraphDemoContainer you can toggle this in the UI 
         */
        graphView.setAutomaticLayout(true);

    }

    @FXML
    private void changeIcon(){
        System.out.println(runButton.getStyleClass());
        if (runButton.getStyleClass().contains("RunButton")) {
            runButton.getStyleClass().setAll("button", "loadingButton"); 
        } else {
            runButton.getStyleClass().setAll("button", "RunButton");
        }
    }

    private void updateToolTip(){
        Tooltip tooltip = new Tooltip(history.getText());

        history.setTooltip(tooltip);
    }

    /*
    // in caso di aggiunta seconda pagina:
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    */

    private Graph<String, String> build_flower_graph() {

        Graph<String, String> g = new GraphEdgeList<>();

        g.insertVertex("A");
        g.insertVertex("B");
        g.insertVertex("C");
        g.insertVertex("D");
        g.insertVertex("E");
        g.insertVertex("F");
        g.insertVertex("G");

        g.insertEdge("A", "B", "1");
        g.insertEdge("A", "C", "2");
        g.insertEdge("A", "D", "3");
        g.insertEdge("A", "E", "4");
        g.insertEdge("A", "F", "5");
        g.insertEdge("A", "G", "6");

        g.insertVertex("H");
        g.insertVertex("I");
        g.insertVertex("J");
        g.insertVertex("K");
        g.insertVertex("L");
        g.insertVertex("M");
        g.insertVertex("N");

        g.insertEdge("H", "I", "7");
        g.insertEdge("H", "J", "8");
        g.insertEdge("H", "K", "9");
        g.insertEdge("H", "L", "10");
        g.insertEdge("H", "M", "11");
        g.insertEdge("H", "N", "12");

        g.insertEdge("A", "H", "0");

        //g.insertVertex("ISOLATED");
        
        return g;
    }
}
