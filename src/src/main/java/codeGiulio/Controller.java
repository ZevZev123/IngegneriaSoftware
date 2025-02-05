package codeGiulio;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    Translator translator;

    public Controller() {
        this(null);
    }
    public Controller(Translator translator) {
        this.translator = translator;
    }

    public static void main(String[] args) throws IOException, InvalidGraphException {
        Test.fileTest();
    }

    public void writeToFile(String fileName) throws IOException{
        FileWriter file = new FileWriter("graphs/"+fileName+".graph");
        file.write(translator.toFileString());
        file.close();
    }
    public void readFromFile(String fileName) throws IOException {
        FileReader file = new FileReader("graphs/"+fileName+".graph");;
        translator.translate(file);
        file.close();
    }

    public ArrayList<String> getExistingFiles() {
        final File folder = new File("graphs");
        ArrayList<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles())
            files.add(fileEntry.getName());
        return files;
    }

    public boolean isValid(ArrayList<Node> nodeList) {
        boolean result = true;

        // controllo che non ci siano nodi con lo stesso nome
        HashMap<String, Integer> nodeNames = new HashMap<>();
        for(Node n : nodeList) {
            if(nodeNames.containsKey(n.getNodeName()))
                return false;
            nodeNames.put(n.getNodeName(), 1);
        }
        
        // controllo che ci sia un nodo iniziale
        int counter = 0;
        for(Node n : nodeList) {
            if(n.isInitial())
                counter++;
        }
        result = result && counter == 1;

        // controllo che ci sia almeno un nodo finale
        counter = 0;
        for(Node n : nodeList) {
            if(n.isFinal())
                counter++;
        }
        result = result && counter >= 1;

        return result;
    }
}
