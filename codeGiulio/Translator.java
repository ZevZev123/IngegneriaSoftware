import java.util.ArrayList;

import codeGiulio.Node;

public class Translator {
    private ArrayList<NodeFX> listNodeFX;
    private ArrayList<EdgeFX> listEdgeFX;
    private ArrayList<Node> listNode;
    private Controller controller;

    
    public Translator(ArrayList<NodeFX> listNodeFX, ArrayList<EdgeFX> listEdgeFX) {
        this.listNodeFX = listNodeFX;
        this.listEdgeFX = listEdgeFX;
        controller = new Controller();
        translate();
    }

    public void translate() {
        System.out.println("Translating...");
        ArrayList<Node> temp = new ArrayList<>();
        for(NodeFX nFX : listNodeFX) {
            Node n = new Node(nFX.getName(), nFX.isNodeInitial(), nFX.isNodeFinal());
            temp.add(n);
        }
        for(Node n : temp) {
            for(EdgeFX eFX : listEdgeFX) {
                if(eFX.getStart().getName().equals(n.getNodeName())) {
                    n.addEdge(eFX.getValue(), new Node(eFX.getEnd().getName()));
                }
            }
        }
        if(controller.isValid(temp))
            listNode = temp;
        else
            System.out.println("Invalid graph");
    }

    public void setNodesFX(ArrayList<NodeFX> listNodeFX) {
        this.listNodeFX = listNodeFX;
    }
    public void setEdgesFX(ArrayList<EdgeFX> listEdgeFX) {
        this.listEdgeFX = listEdgeFX;
    }

    public ArrayList<Node> getNodes() {
        return listNode;
    }
}
