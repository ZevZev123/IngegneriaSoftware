import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordAutomata {
    private Graph graph;
    public WordAutomata(Graph graph) {
        this.graph = graph;
    }
    public void setGraph(Graph newGraph) { this.graph = newGraph; }

    private ArrayList<Node> nodeHistory;
    private ArrayList<String> stringHistory;

    public void run(String word) {
        nodeHistory = new ArrayList<>();
        stringHistory = new ArrayList<>();

        ArrayList<Node> nodeList = graph.getNodes();
        int numberOfNodes = nodeList.size();

        Node startingNode = null;
        ArrayList<Node> endingNodes = new ArrayList<>();
        for (Node n : nodeList) {
            if (n.isInitial())
                startingNode = n;
            if (n.isFinal())
                endingNodes.add(n);
        }
        int startingNodeListIndex = nodeList.indexOf(startingNode);

        List<String>[][] graphMatrix = matrixInitialiser(numberOfNodes, nodeList);

        int currentNode = startingNodeListIndex;
        String remainingWord, subWord;
        remainingWord = word;
        while(!remainingWord.isEmpty()) {
            // da completare
        }
    }

    private List<String>[][] matrixInitialiser(int n, ArrayList<Node> nodeList) {
        List<String>[][] graphMatrix = new List[n][n];

        Node temp;
        for (int i = 0; i < n; i++) {
            temp = nodeList.get(i);
            for (int j = 0; j < n; j++) {
                graphMatrix[i][j] = new ArrayList<>();
                HashMap<String, Node> nodeEdges = temp.getEdges();
                for (String s : nodeEdges.keySet())
                    if (nodeEdges.get(s) == nodeList.get(j))
                        graphMatrix[i][j].add(s);
            }
        }

        return graphMatrix;
    }
}
