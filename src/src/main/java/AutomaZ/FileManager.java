package AutomaZ;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class FileManager {
    private ArrayList<Node> listNode;
    private ArrayList<Edge> listEdge;

    public ArrayList<String> savedFiles;

    public FileManager(ArrayList<Node> n, ArrayList<Edge> e) {
        listNode = n;
        listEdge = e;
        savedFiles = getExistingFiles();
    }
    public FileManager() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public void writeToFile(String fileName) throws IOException{
        if(listNode.isEmpty() || listEdge.isEmpty())
            throw new IOException("One or more lists are empty");
        FileWriter file = new FileWriter("graphs/"+fileName+".graph");
        file.write(toFile());
        file.close();
        savedFiles = getExistingFiles();
    }
    public void readFromFile(String fileName) throws IOException {
        FileReader file = new FileReader("graphs/"+fileName);
        if(!savedFiles.contains(fileName))
            throw new IOException("eh son mi");
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
        for(Node n : listNode) {
            result += n.getName() + ",";
            result += Double.toString(n.getCoordinates()[0]) + "," +
                Double.toString(n.getCoordinates()[1]) + ",";
            result += ((n.isNodeInitial()) ? 1 : 0) + "," +
                ((n.isNodeFinal()) ? 1 : 0) + ",";
            result += "\n";
        }
        
        result += "\nARCHI:\n";
        for(Edge e : listEdge) {
            result += e.getValue() + ",";
            result += e.getNodes()[0].getName() + "," +
                e.getNodes()[1].getName() + ",";
            result += Double.toString(e.getControlX()) + "," +
                Double.toString(e.getControlY());
            result += "\n";
        }

        return result;
    }

    private void translate(FileReader file) {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        line = scanner.nextLine();
        
        listNode = new ArrayList<>();
        while(!line.isEmpty()) {
            String[] t = line.split(",");
            Node n = new Node(Double.parseDouble(t[1]), Double.parseDouble(t[2]),
                t[0], t[3].equals("1"), t[4].equals("1"));
            listNode.add(n);
            line = scanner.nextLine();
        }

        line = scanner.nextLine();
        listEdge = new ArrayList<>();
        while(true) {
            line = scanner.nextLine();
            String[] t = line.split(",");
            Edge e = new Edge(getNode(t[1]), getNode(t[2]), t[0],
                Double.parseDouble(t[3]), Double.parseDouble(t[4]));
            listEdge.add(e);
            if(!scanner.hasNextLine())
                break;
        }

        scanner.close();
    }
    private Node getNode(String name) {
        for(Node n : listNode)
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

    public void updateFileList() { savedFiles = getExistingFiles(); }
    public void setLists(ArrayList<Node> n, ArrayList<Edge> e) { listNode = n; listEdge = e; }

    public ArrayList<Node> getListNode() { return listNode; }
    public ArrayList<Edge> getListEdge() { return listEdge; }
}