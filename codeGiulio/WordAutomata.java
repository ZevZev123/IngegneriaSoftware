package codeGiulio;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordAutomata {
    private final int numberOfNodes;
    private final ArrayList<Node> nodeList;
    private Node startingNode;

    public WordAutomata(ArrayList<Node> nodeList) {
        this.nodeList = nodeList;
        numberOfNodes = nodeList.size();
        startingNode = null;
        for (Node n : nodeList)
            if (n.isInitial())
                startingNode = n;
    }

    private ArrayList<Node> nodeHistory;
    private ArrayList<String> stringHistory;

    public boolean run(String word) {
        nodeHistory = new ArrayList<>();
        stringHistory = new ArrayList<>();

        List<String>[][] graphMatrix = matrixInitialiser(numberOfNodes, nodeList);

        int tempIndex, currentNodeIndex = nodeList.indexOf(startingNode);
        String remainingWord, subWord;
        remainingWord = word;
        boolean found;
        while (!remainingWord.isEmpty()) {
            found = false;
            for (int i = remainingWord.length(); i > 0; i--) {
                subWord = remainingWord.substring(0, i);
                for (tempIndex = 0; tempIndex < numberOfNodes && !found; tempIndex++)
                    found = graphMatrix[currentNodeIndex][tempIndex].contains(subWord);
                if (found) {
                    nodeHistory.add(nodeList.get(currentNodeIndex));
                    stringHistory.add(subWord);
                    remainingWord = remainingWord.replaceFirst(subWord, "");
                    currentNodeIndex = tempIndex - 1;
                    break;
                }
            }
            if (!found) return false;
        }
        return nodeList.get(currentNodeIndex).isFinal();
    }

    private List<String>[][] matrixInitialiser(int n, ArrayList<Node> nodeList) {
        List<String>[][] graphMatrix = new List[n][n];

        Node temp;
        for (int i = 0; i < n; i++) {
            temp = nodeList.get(i);
            for (int j = 0; j < n; j++) {
                graphMatrix[i][j] = new ArrayList<>();
                HashMap<String, String> nodeEdges = temp.getEdges();
                for (String s : nodeEdges.keySet())
                    if (nodeEdges.get(s) == nodeList.get(j).getNodeName())
                        graphMatrix[i][j].add(s);
            }
        }

        return graphMatrix;
    }

    public ArrayList<Node> getNodeHistory() { return nodeHistory; }
    public ArrayList<String> getStringHistory() { return stringHistory; }
}
