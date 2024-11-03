import java.util.ArrayList;

public class WordAutomata {
    private final int numberOfNodes;
    private final String[][] edgesValues;
    private final int startingNode;
    private final ArrayList<Integer> endingNodes;
    private static ArrayList<Integer> nodeHistory;
    private static String remainingSubWord;

    public WordAutomata(int numberOfNodes, String[][] edgesValues, int startingNode, ArrayList<Integer> endingNodes) {
        this.numberOfNodes = numberOfNodes;
        this.edgesValues = edgesValues;
        this.startingNode = startingNode;
        this.endingNodes = endingNodes;
    }

    public int run(String word) {
        nodeHistory = new ArrayList<>();
        remainingSubWord = "";
        int currentNode = startingNode;
        nodeHistory.add(startingNode);
        int nextNode = -1;
        String temp = word;
        String subWord, currentValue = null;
        boolean subStringFound = false;
        while (!temp.isEmpty()) {
            subStringFound = false;
            for (int i = temp.length() - 1; i >= 0 && !subStringFound; i--) { // grants the longest subword is chosen
                subWord = temp.substring(0, i + 1);
                for (int j = 0; j < numberOfNodes && !subStringFound; j++) { // no two edges from the same node can be equal
                    currentValue = edgesValues[currentNode][j];
                    if (currentValue != null && currentValue.equals(subWord)) {
                        nextNode = j;
                        temp = temp.replaceFirst(subWord, "");
                        subStringFound = true;
                    }
                }
            }
            if (!subStringFound) {
                remainingSubWord = temp;
                return -1;
            }
            currentNode = nextNode;
            nodeHistory.add(currentNode);
        }
        return (endingNodes.contains(currentNode)) ? 1 : -1;
    }

    public ArrayList<Integer> getNodeHistory() {return nodeHistory;}
    public String getRemainingSubWord() {return remainingSubWord;}
}
