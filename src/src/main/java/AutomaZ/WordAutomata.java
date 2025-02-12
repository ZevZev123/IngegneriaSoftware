package AutomaZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordAutomata {
    private final ArrayList<Node> listNode;
    private final ArrayList<Edge> listEdge;

    private ArrayList<State> stateList;
    private State startingState;

    private ArrayList<String> stateHistory;
    private ArrayList<String> stringHistory;
    private String remainingWord;

    public WordAutomata(ArrayList<Node> nList, ArrayList<Edge> eList) {
        listNode = nList;
        listEdge = eList;

        stateList = translate();
        
        for (State s : stateList)
            if (s.isInitial) {
                startingState = s;
                break;
            }
    }

    private ArrayList<State> translate() {
        ArrayList<State> temp = new ArrayList<>();

        for(Node n : listNode) {
            State s = new State(n.getName(), n.isNodeInitial(), n.isNodeFinal());
            for(Edge e : listEdge)
                if(e.getNodes()[0].getName().equals(s.name))
                    s.edges.put(e.getValue(), e.getNodes()[1].getName());
            temp.add(s);
        }
        
        return temp;
    }

    public boolean run(String word) {
        stateHistory = new ArrayList<>();
        stringHistory = new ArrayList<>();

        int stateNum = stateList.size();
        List<String>[][] graphMatrix = matrixInitialiser(stateNum, stateList);

        int tempInd, currStateInd = stateList.indexOf(startingState);
        String remainingWord = word, subWord;
        boolean found;
        while (!remainingWord.isEmpty()) {
            found = false;
            for (int i = remainingWord.length(); i > 0; i--) {
                subWord = remainingWord.substring(0, i);
                for (tempInd = 0; tempInd < stateNum && !found; tempInd++)
                    found = graphMatrix[currStateInd][tempInd].contains(subWord);
                if (found) {
                    stateHistory.add(stateList.get(currStateInd).name);
                    stringHistory.add(subWord);
                    remainingWord = remainingWord.replaceFirst(subWord, "");
                    currStateInd = tempInd - 1;
                    break;
                }
            }
            this.remainingWord = remainingWord;
            if (!found) return false;
        }
        stateHistory.add(stateList.get(currStateInd).name);
        
        return stateList.get(currStateInd).isFinal;
    }

    @SuppressWarnings("unchecked")
    private List<String>[][] matrixInitialiser(int n, ArrayList<State> list) {
        List<String>[][] graphMatrix = new List[n][n];

        State temp;
        for (int i = 0; i < n; i++) {
            temp = list.get(i);
            for (int j = 0; j < n; j++) {
                graphMatrix[i][j] = new ArrayList<>();
                HashMap<String, String> nodeEdges = temp.edges;
                for (String s : nodeEdges.keySet())
                    if (nodeEdges.get(s) == list.get(j).name)
                        graphMatrix[i][j].add(s);
            }
        }

        return graphMatrix;
    }

    public ArrayList<String> getStateHistory() { return stateHistory; }
    public ArrayList<String> getStringHistory() { return stringHistory; }
    public String getRemainingWord() { return remainingWord; }

    private class State {
        private final String name;
        private final boolean isInitial;
        private final boolean isFinal;
        private HashMap<String, String> edges;
    
        private State(String name, boolean isInitial, boolean isFinal) {
            this.name = name;
            this.isInitial = isInitial;
            this.isFinal = isFinal;
            edges = new HashMap<String, String>();
        }
    }
}