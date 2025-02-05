package codeGiulio;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import wordautomata.EdgeFX;
import wordautomata.NodeFX;

class FileManager {
    private ArrayList<NodeFX> listNodeFX;
    private ArrayList<EdgeFX> listEdgeFX;

    public FileManager(ArrayList<NodeFX> n, ArrayList<EdgeFX> e) {
        listNodeFX = n;
        listEdgeFX = e;
    }
    public FileManager() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public void writeToFile(String fileName) throws IOException{
        FileWriter file = new FileWriter("graphs/"+fileName+".graph");
        file.write(toFileString());
        file.close();
    }
    public void readFromFile(String fileName) throws IOException {
        FileReader file = new FileReader("graphs/"+fileName+".graph");;
        translate(file);
        file.close();
    }

    public String toFileString() {
        String result = "";
        
        result += "NODI:\n";
        for(NodeFX n : listNodeFX) {
            result += "\t";
            result += Double.toString(n.getCoordinates()[0]) + "," +
                Double.toString(n.getCoordinates()[1]) + "\n";
            result += n.getName() + ",";
            result += ((n.isNodeInitial()) ? 1 : 0) + "," +
                ((n.isNodeFinal()) ? 1 : 0) + ",";
        }
        
        result += "\nARCHI:\n";
        for(EdgeFX e : listEdgeFX) {
            result += "\t";
            result += e.getNodes()[0].getName() + "," +
                e.getNodes()[1].getName() + ",";
            result += e.getValue() + ",";
            result += Double.toString(e.getControlX()) + "," +
                Double.toString(e.getControlY()) + "\n";
        }

        return result;
    }

    public void translate(FileReader file) {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        line = scanner.nextLine();
        
        listNodeFX = new ArrayList<>();
        while(!line.isEmpty()) {
            String[] t = line.split(",");
            NodeFX n = new NodeFX(Double.parseDouble(t[0]), Double.parseDouble(t[1]),
                t[2], t[3].equals("true"), t[4].equals("true"));
            listNodeFX.add(n);
            line = scanner.nextLine();
        }

        line = scanner.nextLine();
        line = scanner.nextLine();
        listEdgeFX = new ArrayList<>();
        while(!line.isEmpty()) {
            String[] t = line.split(",");
            EdgeFX e = new EdgeFX(getNodeFX(t[0]), getNodeFX(t[1]), t[2],
                Double.parseDouble(t[3]), Double.parseDouble(t[4]));
            listEdgeFX.add(e);
            line = scanner.nextLine();
        }

        scanner.close();
    }

    private NodeFX getNodeFX(String name) {
        for(NodeFX n : listNodeFX)
            if(n.getName().equals(name))
                return n;
        return null;
    }

    public ArrayList<String> getExistingFiles() {
        final File folder = new File("graphs");
        ArrayList<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles())
            files.add(fileEntry.getName());
        return files;
    }
}