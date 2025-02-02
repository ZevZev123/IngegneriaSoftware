package codeGiulio;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    public Controller() {}

    public static void main(String[] args) {
        // Test pdfTest = new Test();
        // pdfTest.pdfTest();
    }

    public boolean isValid(ArrayList<NodeFX> nodeList) {
        boolean result = true;

        // controllo che non ci siano nodi con lo stesso nome
        HashMap<String, Integer> nodeNames = new HashMap<>();
        for(NodeFX n : nodeList) {
            if(nodeNames.containsKey(n.getName()))
                return false;
            nodeNames.put(n.getName(), 1);
        }
        
        // controllo che ci sia un nodo iniziale
        int counter = 0;
        for(NodeFX n : nodeList) {
            if(n.isNodeInitial())
                counter++;
        }
        result = result && counter == 1;

        // controllo che ci sia almeno un nodo finale
        counter = 0;
        for(NodeFX n : nodeList) {
            if(n.isNodeFinal())
                counter++;
        }
        result = result && counter >= 1;

        // controllo che non ci siano archi con lo stesso nome uscenti dallo stesso nodo
        for(NodeFX n : nodeList) {
            HashMap<String, Integer> edgeNames = new HashMap<>();
            for(EdgeFX e : n.getEdges()) {
                if(edgeNames.containsKey(e.getValue()))
                    return false;
                edgeNames.put(e.getValue(), 1);
            }
        }

        return result;
    }
}
