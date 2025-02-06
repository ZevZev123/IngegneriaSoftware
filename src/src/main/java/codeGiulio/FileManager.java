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

    public ArrayList<String> savedFiles;

    public FileManager(ArrayList<NodeFX> n, ArrayList<EdgeFX> e) {
        listNodeFX = n;
        listEdgeFX = e;
        savedFiles = getExistingFiles();
    }
    public FileManager() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public void writeToFile(String fileName) throws IOException{
        if(listNodeFX.isEmpty() || listEdgeFX.isEmpty())
            throw new IOException("One or more lists are empty");
        FileWriter file = new FileWriter("graphs/"+fileName+".graph");
        file.write(toFile());
        file.close();
        savedFiles = getExistingFiles();
    }
    public void readFromFile(String fileName) throws IOException {
        FileReader file = new FileReader("graphs/"+fileName+".graph");
        if(!savedFiles.contains(file.toString()))
            throw new IOException("File not found");
        translate(file);
        file.close();
    }
    public void deleteFile(String fileName) {
        File file = new File("graphs/"+fileName+".graph");
        if(file.delete())
            savedFiles = getExistingFiles();
        savedFiles = getExistingFiles();
    }

    private String toFile() {
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

    private void translate(FileReader file) {
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

    private ArrayList<String> getExistingFiles() {
        final File folder = new File("graphs");
        ArrayList<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles())
            files.add(fileEntry.getName());
        return files;
    }

    public void setLists(ArrayList<NodeFX> n, ArrayList<EdgeFX> e) { listNodeFX = n; listEdgeFX = e; }

    public ArrayList<NodeFX> getListNodeFX() { return listNodeFX; }
    public ArrayList<EdgeFX> getListEdgeFX() { return listEdgeFX; }
}