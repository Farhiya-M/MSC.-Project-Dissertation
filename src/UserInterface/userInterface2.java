package UserInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import Algorithms.ClosureOfAttributes;
import Grammar.FDGrammar;

public class userInterface2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // List to hold functional dependencies
        List<FDGrammar.Pair<Set<String>, Set<String>>> fds = new ArrayList<>();

        while (true) {
            // Prompt the user to enter functional dependencies
            System.out.println("Enter functional dependency (e.g. A,B -> C,D):");
            String fd = scanner.nextLine().trim();

            if (!FDGrammar.isValidFD(fd)) {
                System.out.println("Invalid functional dependency. Please correct and try again.");
                continue;
            }

            try {
                List<FDGrammar.Pair<String, String>> parsedFds = FDGrammar.parseFDList(fd);
                for (FDGrammar.Pair<String, String> pair : parsedFds) {
                    Set<String> lhs = parseAttributes(pair.left());
                    Set<String> rhs = parseAttributes(pair.right());
                    fds.add(new FDGrammar.Pair<>(lhs, rhs));
                }
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
        for (FDGrammar.Pair<Set<String>, Set<String>> pair : fds) {
            System.out.println(formatAttributes(pair.left()) + " -> " + formatAttributes(pair.right()));
        }

        // Compute closure of attributes
        System.out.println("Do you want to compute the closure of attributes? (Y/N)");
        String computeClosureChoice = scanner.nextLine().trim();
        if (computeClosureChoice.equalsIgnoreCase("Y")) {
            System.out.println("Enter the attributes for which you want to compute the closure (comma-separated):");
            String attributesInput = scanner.nextLine().trim();
            Set<String> attributes = parseAttributes(attributesInput);

            boolean allAttributesPresent = true;
            for (String attribute : attributes) {
                boolean attributePresent = false;
                for (FDGrammar.Pair<Set<String>, Set<String>> fd : fds) {
                    if (fd.left().contains(attribute) || fd.right().contains(attribute)) {
                        attributePresent = true;
                        break;
                    }
                }
                if (!attributePresent) {
                    allAttributesPresent = false;
                    System.out.println("Invalid attribute '" + attribute + "'. It is not part of any functional dependency.");
                }
            }

            if (allAttributesPresent) {
                Set<String> closure = ClosureOfAttributes.computeClosure(attributes, fds);
                System.out.println("Closure of attributes: " + formatAttributes(closure));
            } else {
                System.out.println("Unable to compute closure. Please correct the attributes and try again.");
                System.out.println("Enter the corrected attributes (comma-separated):");
                String correctedAttributesInput = scanner.nextLine().trim();
                Set<String> correctedAttributes = parseAttributes(correctedAttributesInput);
                Set<String> validAttributes = new HashSet<>();
                for (String attribute : correctedAttributes) {
                    boolean attributePresent = false;
                    for (FDGrammar.Pair<Set<String>, Set<String>> fd : fds) {
                        if (fd.left().contains(attribute) || fd.right().contains(attribute)) {
                            attributePresent = true;
                            break;
                        }
                    }
                    if (attributePresent) {
                        validAttributes.add(attribute);
                    } else {
                        System.out.println("Invalid attribute '" + attribute + "'. It is not part of any functional dependency.");
                    }
                }

                if (!validAttributes.isEmpty()) {
                    Set<String> closure = ClosureOfAttributes.computeClosure(validAttributes, fds);
                    System.out.println("Closure of attributes: " + formatAttributes(closure));
                } else {
                    System.out.println("Unable to compute closure. No valid attributes provided.");
                }
            }
        }
    }

    private static Set<String> parseAttributes(String attributes) {
        Set<String> attributeSet = new HashSet<>();
        String[] attributeArray = attributes.split(",");
        for (String attribute : attributeArray) {
            attributeSet.add(attribute.trim());
        }
        return attributeSet;
    }

    private static String formatAttributes(Set<String> attributes) {
        StringBuilder sb = new StringBuilder();
        for (String attribute : attributes) {
            sb.append(attribute).append(", ");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
