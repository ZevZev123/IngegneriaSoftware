package codeGiulio;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import wordautomata.*;

public class Translator {
    private ArrayList<NodeFX> listNodeFX;
    private ArrayList<EdgeFX> listEdgeFX;
    private ArrayList<Node> listNode;
    private Controller controller;
    
    public Translator(ArrayList<NodeFX> listNodeFX, ArrayList<EdgeFX> listEdgeFX) throws InvalidGraphException {
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
                if(eFX.getNodes()[0].getName().equals(n.getNodeName()))
                    n.addEdge(eFX.getValue(), eFX.getNodes()[1].getName());

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
            NodeFX n = new NodeFX(Double.parseDouble(t[0]), Double.parseDouble(t[1]), t[2],
                t[3].equals("true"), t[4].equals("true"));
            listNodeFX.add(n);
            line = scanner.nextLine();
        }

        line = scanner.nextLine();
        line = scanner.nextLine();
        listEdgeFX = new ArrayList<>();
        while(!line.isEmpty()) {
            String[] t = line.split(",");
            EdgeFX e = new EdgeFX(getNodeFX(t[0]), getNodeFX(t[1]), t[2], Double.parseDouble(t[3]), Double.parseDouble(t[4]));
            listEdgeFX.add(e);
            line = scanner.nextLine();
        }

        scanner.close();
    }

    public String toFileString() {
        String result = "";
        
        result += "NODI:\n";
        for(NodeFX n : listNodeFX) {
            result += "\t";
            result += Double.toString(n.getCoordinates()[0]) + ",";
            result += Double.toString(n.getCoordinates()[1]) + "\n";
            result += n.getName() + ",";
            result += ((n.isNodeInitial()) ? 1 : 0) + ",";
            result += ((n.isNodeFinal()) ? 1 : 0) + ",";
        }
        
        result += "\nARCHI:\n";
        for(EdgeFX e : listEdgeFX) {
            result += "\t";
            result += e.getNodes()[0].getName() + ",";
            result += e.getNodes()[1].getName() + ",";
            result += e.getValue() + ",";
            result += Double.toString(e.getControlX()) + ",";
            result += Double.toString(e.getControlY()) + "\n";
        }

        return result;
    }

    private NodeFX getNodeFX(String name) {
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
