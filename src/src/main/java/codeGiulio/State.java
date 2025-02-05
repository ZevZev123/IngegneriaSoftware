package codeGiulio;

import java.util.HashMap;

public class State {
    private String nodeName;
    private boolean isInitial;
    private boolean isFinal;
    private HashMap<String, String> edges;

    public State(String nodeName, boolean isInitial, boolean isFinal) {
        this.nodeName = nodeName;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        edges = new HashMap<String, String>();
    }
    public State(String nodeName) {
        this(nodeName, false, false);
    }
    
    public void addEdge(String value, String nodePointed) { edges.put(value, nodePointed); }
    public void removeEdge(String value) { edges.remove(value); }

    public String getName() { return nodeName; }
    public boolean isInitial() { return isInitial; }
    public boolean isFinal() { return isFinal; }
    public HashMap<String, String> getEdges() { return edges; }

    @Override
    public String toString() { return nodeName; }
}
