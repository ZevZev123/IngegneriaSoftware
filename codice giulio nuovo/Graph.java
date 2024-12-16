import java.util.ArrayList;

public class Graph {
    private final ArrayList<Node> nodes;

    public Graph() {
        nodes = new ArrayList<>();
    }

    public ArrayList<Node> getNodes() { return nodes; }

    public void addNode(Node node) { nodes.add(node); }
    public void removeNode(Node node) { nodes.remove(node); }
}
