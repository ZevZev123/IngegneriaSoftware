package codeGiulio;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
// qui si controlla che il grafo inserito sia corretto e si trasformano
// gli oggetti usati per jfx in nodo utilizzabile da WordAutomata.java
    public Controller() {

    }

    public static void main(String[] args) {
        // test esempio nel pdf
        Node q0 = new Node("q0");
        Node q1 = new Node("q1");
        Node q2 = new Node("q2");
        Node q3 = new Node("q3");
        Node q4 = new Node("q4");

        q0.setInitial();
        q3.setFinal();
        q4.setFinal();

        q0.addEdge("aab", q1);
        q0.addEdge("abbb", q2);
        q1.addEdge("b", q2);
        q1.addEdge("a", q3);
        q2.addEdge("aba", q1);
        q2.addEdge("b", q3);
        q3.addEdge("abb", q3);
        q3.addEdge("bb", q4);
        q4.addEdge("a", q1);
        q4.addEdge("b", q2);

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
}
