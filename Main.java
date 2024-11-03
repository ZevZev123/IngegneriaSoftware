import javax.print.attribute.standard.NumberOfDocuments;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static int numberOfNodes;
    static int startingNode;
    static ArrayList<Integer> endingNodes;
    static Integer[][] graphMatrix;
    static String[][] edgesValues;

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("src/inputData.txt");
        Scanner scanner = new Scanner(file);
        initialise(scanner);

        WordAutomata wa = new WordAutomata(numberOfNodes, edgesValues, startingNode, endingNodes);


        // Test 1
        test("aababbbb", wa);

        // Test 2
        test("abbbaba", wa);

        // Test 3
        test("abbbabaaba", wa);
    }

    static void initialise(Scanner fileSc) {
        String line = fileSc.nextLine();
        numberOfNodes = Integer.parseInt(line.replace("\n", ""));
        line = fileSc.nextLine();
        startingNode = Integer.parseInt(line.replace("\n", ""));
        line = fileSc.nextLine();
        endingNodes = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ',' && line.charAt(i) != '\n')
                endingNodes.add(Integer.parseInt(line.charAt(i) + ""));
        }
        graphMatrix = new Integer[numberOfNodes][numberOfNodes];
        line = fileSc.nextLine();
        for (int i = 0; i < numberOfNodes && fileSc.hasNextLine(); i++) {
            for (int j = 0; j < line.length(); j++)
                graphMatrix[i][j] = Integer.parseInt(line.charAt(j) + "");
            line = fileSc.nextLine();
        }
        edgesValues = new String[numberOfNodes][numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++)
            for (int j = 0; j < numberOfNodes; j++)
                if (graphMatrix[i][j] == 1) {
                    edgesValues[i][j] = line.replace("\n", "");
                    line = fileSc.nextLine();
                }
    }

    public static void printEdgesValues(String[][] edgesValues) {
        for (int i = 0; i < numberOfNodes; i++) {
            for (int j = 0; j < numberOfNodes; j++)
                System.out.print(edgesValues[i][j] + " ");
            System.out.println();
        }
    }
    public static String arraylistToString(ArrayList<Integer> al) {
        String result = "";
        for(Integer i : al)
            result += i + " ";
        return result;
    }

    private static void test(String word, WordAutomata wa) {
        System.out.println("Testing word \"" + word + "\"...");
        int output = wa.run(word);
        String result = (output == 1) ? "Success" : "Failure";
        System.out.println("Result: " + result);
        System.out.println("With journey: " + arraylistToString(wa.getNodeHistory()));
        if(!wa.getRemainingSubWord().isEmpty())
            System.out.println("Remaining Subword: " + wa.getRemainingSubWord());
        System.out.println();
    }
}
