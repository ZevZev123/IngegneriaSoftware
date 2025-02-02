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

    public void translate() throws InvalidGraphException {
        System.out.println("Translating...");
        ArrayList<Node> temp = new ArrayList<>();
        for(NodeFX nFX : listNodeFX) {
            Node n = new Node(nFX.getName(), nFX.isNodeInitial(), nFX.isNodeFinal());
            for(EdgeFX eFX : listEdgeFX)
                if(eFX.getStart().getName().equals(n.getNodeName()))
                    n.addEdge(eFX.getValue(), eFX.getEnd().getName());

            temp.add(n);
        }
        if(controller.isValid(temp))
            listNode = temp;
        else
            throw new InvalidGraphException("Invalid Graph");
    }

    public void setNodesFX(ArrayList<NodeFX> listNodeFX) { this.listNodeFX = listNodeFX; }
    public void setEdgesFX(ArrayList<EdgeFX> listEdgeFX) { this.listEdgeFX = listEdgeFX; }

    public ArrayList<Node> getNodes() { return listNode; }
}
