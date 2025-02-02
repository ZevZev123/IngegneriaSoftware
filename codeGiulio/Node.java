package codeGiulio;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    private String nodeName;
    private boolean isInitial;
    private boolean isFinal;
    private HashMap<String, String> edges;

    public Node(String nodeName, boolean isInitial, boolean isFinal) {
        this.nodeName = nodeName;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        edges = new HashMap<String, String>();
    }
    public Node(String nodeName) {
        this(nodeName, false, false);
    }

    public void setName(String nodeName) { this.nodeName = nodeName; }
    public void setInitial() { isInitial = true; }
    public void setFinal() { isFinal = true; }
    public void addEdge(String value, String nodePointed) { edges.put(value, nodePointed); }
    public void removeEdge(String value) { edges.remove(value); }

    public String getNodePointed(String value) { return edges.get(value); }
    public boolean containsValue(String value) { return edges.containsKey(value); }

    public String getNodeName() { return nodeName; }
    public boolean isInitial() { return isInitial; }
    public boolean isFinal() { return isFinal; }
    public HashMap<String, String> getEdges() { return edges; }

    @Override
    public String toString() { return nodeName; }
}
