package codeGiulio;

import java.io.IOException;
import java.util.ArrayList;
import wordautomata.*;

public class Test {
    public Test() {}

    public static void pdfTest() {
        // test esempio nel pdf
        Node q0 = new Node("q0");
        Node q1 = new Node("q1");
        Node q2 = new Node("q2");
        Node q3 = new Node("q3");
        Node q4 = new Node("q4");

        q0.setInitial();
        q3.setFinal();
        q4.setFinal();

        q0.addEdge("aab", "q1");
        q0.addEdge("abbb", "q2");
        q1.addEdge("b", "q2");
        q1.addEdge("a", "q3");
        q2.addEdge("aba", "q1");
        q2.addEdge("b", "q3");
        q3.addEdge("abb", "q3");
        q3.addEdge("bb", "q4");
        q4.addEdge("a", "q1");
        q4.addEdge("b", "q2");

        ArrayList<Node> nodeList = new ArrayList<>();
        nodeList.add(q0);
        nodeList.add(q1);
        nodeList.add(q2);
        nodeList.add(q3);
        nodeList.add(q4);

        WordAutomata automaTest = new WordAutomata(nodeList);

        System.out.println("\nTest Results:\n");
        System.out.println("Word: \"aababbbb\"\nTerminated: " + automaTest.run("aababbbb") +
                "\nNode History: " + automaTest.getNodeHistory().toString() + "\nString History: " +
                automaTest.getStringHistory().toString() + "\n");
        System.out.println("Word: \"abbbaba\"\nTerminated: " + automaTest.run("abbbaba") +
                "\nNode History: " + automaTest.getNodeHistory().toString() + "\nString History: " +
                automaTest.getStringHistory().toString() + "\n");
        System.out.println("Word: \"abbbabaaba\"\nTerminated: " + automaTest.run("abbbabaaba") +
                "\nNode History: " + automaTest.getNodeHistory().toString() + "\nString History: " +
                automaTest.getStringHistory().toString() + "\n");
    }
    public static void fileTest() throws IOException, InvalidGraphException {
        ArrayList<NodeFX> nodeList = new ArrayList<>();
        nodeList.add(new NodeFX(0, 0, "F", true, false));
        nodeList.add(new NodeFX(0, 0, "A", false, true));
        nodeList.add(new NodeFX(0, 0, "B", false, false));
        nodeList.add(new NodeFX(0, 0, "C", false, false));
        nodeList.add(new NodeFX(0, 0, "D", false, false));
        nodeList.add(new NodeFX(0, 0, "E", false, false));
        nodeList.add(new NodeFX(0, 0, "T", false, false));

        ArrayList<EdgeFX> edgeList = new ArrayList<>();
        edgeList.add(new EdgeFX(nodeList.get(0), nodeList.get(1), "provolone", 425, 235));
        edgeList.add(new EdgeFX(nodeList.get(1), nodeList.get(2), "bc", 329, 150));
        edgeList.add(new EdgeFX(nodeList.get(1), nodeList.get(3), "av", 291, 233));
        edgeList.add(new EdgeFX(nodeList.get(2), nodeList.get(3), "ac", 217, 166));
        edgeList.add(new EdgeFX(nodeList.get(3), nodeList.get(0), "ad", 274, 328));
        edgeList.add(new EdgeFX(nodeList.get(5), nodeList.get(4), "ae", 240, 325));
        edgeList.add(new EdgeFX(nodeList.get(5), nodeList.get(4), "af", 183, 394));
        edgeList.add(new EdgeFX(nodeList.get(6), nodeList.get(2), "principessa", 324, 262));

        Translator translator = new Translator(nodeList, edgeList);
        Controller controller = new Controller(translator);
        controller.writeToFile("test1");
        controller.readFromFile("test1");
        ArrayList<NodeFX> compareNodes = new ArrayList<>();
        ArrayList<EdgeFX> compareEdges = new ArrayList<>();
        compareNodes = translator.getNodesFX();
        compareEdges = translator.getEdgesFX();
        for(NodeFX n : nodeList)
            for(NodeFX n2 : compareNodes)
                if(n.getName().equals(n2.getName()))
                    if(n.isNodeInitial() != n2.isNodeInitial() || n.isNodeFinal() != n2.isNodeFinal())
                        System.out.println("Error: NodeFX not equal");
        for(EdgeFX e : edgeList)
            for(EdgeFX e2 : compareEdges)
                if(e.getNodes()[0].getName().equals(e2.getNodes()[0].getName()) && e.getNodes()[1].getName().equals(e2.getNodes()[1].getName()))
                    if(e.getValue().equals(e2.getValue()) && e.getControlX() == e2.getControlX() && e.getControlY() == e2.getControlY())
                        System.out.println("Error: EdgeFX not equal");
    }
    
}
