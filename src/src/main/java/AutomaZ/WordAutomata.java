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

    public WordAutomata(ArrayList<Node> nFXList, ArrayList<Edge> eFXList) {
        listNode = nFXList;
        listEdge = eFXList;

        stateList = translate();
        
        for (State s : stateList)
            if (s.isInitial()) {
                startingState = s;
                break;
            }
    }

    private ArrayList<State> translate() {
        ArrayList<State> temp = new ArrayList<>();

        for(Node nFX : listNode) {
            State s = new State(nFX.getName(), nFX.isNodeInitial(), nFX.isNodeFinal());
            for(Edge eFX : listEdge)
                if(eFX.getNodes()[0].getName().equals(s.getName()))
                    s.addEdge(eFX.getValue(), eFX.getNodes()[1].getName());
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
                    stateHistory.add(stateList.get(currStateInd).getName());
                    stringHistory.add(subWord);
                    remainingWord = remainingWord.replaceFirst(subWord, "");
                    currStateInd = tempInd - 1;
                    break;
                }
            }
            if (!found) return false;
        }

        return stateList.get(currStateInd).isFinal();
    }

    @SuppressWarnings("unchecked")
    private List<String>[][] matrixInitialiser(int n, ArrayList<State> list) {
        List<String>[][] graphMatrix = new List[n][n];

        State temp;
        for (int i = 0; i < n; i++) {
            temp = list.get(i);
            for (int j = 0; j < n; j++) {
                graphMatrix[i][j] = new ArrayList<>();
                HashMap<String, String> nodeEdges = temp.getEdges();
                for (String s : nodeEdges.keySet())
                    if (nodeEdges.get(s) == list.get(j).getName())
                        graphMatrix[i][j].add(s);
            }
        }

        return graphMatrix;
    }

    public ArrayList<String> getStateHistory() { return stateHistory; }
    public ArrayList<String> getStringHistory() { return stringHistory; }


    private class State {
        private final String nodeName;
        private final boolean isInitial;
        private final boolean isFinal;
        private HashMap<String, String> edges;
    
        private State(String nodeName, boolean isInitial, boolean isFinal) {
            this.nodeName = nodeName;
            this.isInitial = isInitial;
            this.isFinal = isFinal;
            edges = new HashMap<String, String>();
        }
        private State(String nodeName) {
            this(nodeName, false, false);
        }
        
        private void addEdge(String value, String nodePointed) { edges.put(value, nodePointed); }
    
        private String getName() { return nodeName; }
        private boolean isInitial() { return isInitial; }
        private boolean isFinal() { return isFinal; }
        private HashMap<String, String> getEdges() { return edges; }
    }
}