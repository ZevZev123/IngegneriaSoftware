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

    public void translate(FileReader file) {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        line = scanner.nextLine();
        
        listNodeFX = new ArrayList<>();
        while(!line.isEmpty()) {
            String[] t = line.split(",");
            NodeFX n = new NodeFX(t[0], Double.parseDouble(t[3]), Double.parseDouble(t[4]));
            n.setNodeInitial(t[1].equals("1"));
            n.setNodeFinal(t[2].equals("1"));
            listNodeFX.add(n);
            line = scanner.nextLine();
        }

        line = scanner.nextLine();
        line = scanner.nextLine();
        listEdgeFX = new ArrayList<>();
        while(!line.isEmpty()) {
            String[] t = line.split(",");
            EdgeFX e = new EdgeFX(this.getNodeFX(t[0]), this.getNodeFX(t[1]), tokens[2], Double.parseDouble(t[3]), Double.parseDouble(t[4]));
            listEdgeFX.add(e);
            line = scanner.nextLine();
        }
    }

    public String toFileString() {
        String result = "";
        
        result += "NODI:\n";
        for(NodeFX n : listNodeFX) {
            result += "\t";
            result += n.getName() + ",";
            result += ((n.isNodeInitial()) ? 1 : 0) + ",";
            result += ((n.isNodeFinal()) ? 1 : 0) + ",";
            result += Double.toString(n.getCoordinates()[0]) + ",";
            result += Double.toString(n.getCoordinates()[1]) + "\n";
        }
        
        result += "\nARCHI:\n";
        for(EdgeFX e : listEdgeFX) {
            result += "\t";
            result += e.getStart().getName() + ",";
            result += e.getEnd().getName() + ",";
            result += e.getValue() + ",";
            result += Double.toString(e.getControlX()) + ",";
            result += Double.toString(e.getControlY()) + "\n";
        }

        return result;
    }

    private NodeFX getNodeFx(String name) {
        for(NodeFX n : listNodeFX)
            if(n.getName().equals(name))
                return n;
        return null;
    }

    public void setNodesFX(ArrayList<NodeFX> listNodeFX) { this.listNodeFX = listNodeFX; }
    public void setEdgesFX(ArrayList<EdgeFX> listEdgeFX) { this.listEdgeFX = listEdgeFX; }

    public ArrayList<Node> getNodes() { return listNode; }
    public ArrayList<NodeFX> getNodesFX() { return listNodeFX; }
    public ArrayList<EdgeFX> getEdgesFX() { return listEdgeFX; }
}
