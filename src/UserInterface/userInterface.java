package UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Grammar.FDGrammar;
import Grammar.FDGrammar.Pair;

public class userInterface {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // List to hold functional dependencies
        List<Pair<String, String>> fds = new ArrayList<>();

        while (true) {
            // Prompt the user to enter functional dependencies
            System.out.println("Enter functional dependency (e.g. A,B -> C,D):");
            String fd = scanner.nextLine().trim();

            if (!FDGrammar.isValidFD(fd)) {
                System.out.println("Invalid functional dependency. Please correct and try again.");
                continue;
            }

            try {
                List<Pair<String, String>> parsedFds = FDGrammar.parseFDList(fd);
                fds.addAll(parsedFds);
                System.out.println("Valid functional dependency added.");
            } catch (IllegalArgumentException ex) {
                System.out.println("Duplicate functional dependency detected. This functional dependency already exists. Please correct and try again.");
                continue;
            }

            System.out.println("Do you want to enter another functional dependency? (Y/N)");
            String choice = scanner.nextLine().trim();

            if (!choice.equalsIgnoreCase("Y")) {
                break;
            }
        }

        // Display all functional dependencies
        System.out.println("Functional dependencies entered:");
        for (Pair<String, String> pair : fds) {
            System.out.println(pair.left() + " -> " + pair.right());
        }
    }
}
