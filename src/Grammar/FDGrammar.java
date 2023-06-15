package Grammar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FDGrammar {
    // Regular expressions used for FD syntax validation.
    private static final String ATTRIBUTE_REGEX = "[A-Z]";
    private static final String ATTRIBUTES_REGEX = "(" + ATTRIBUTE_REGEX + "(?:\\s*,\\s*" + ATTRIBUTE_REGEX + ")*)";
    private static final String FD_REGEX = ATTRIBUTES_REGEX + "\\s*->\\s*" + ATTRIBUTES_REGEX;
    private static final String FD_LIST_REGEX = FD_REGEX + "(?:\\s*,\\s*" + FD_REGEX + ")*";

    // A set for storing unique FDs.
    private static final Set<Pair<String, String>> uniqueFDs = new HashSet<>();
    // Checking if a given string represents a valid FD
    public static boolean isValidFD(String fd) {
        // Compiling and applying the regex pattern for FDs.
        Pattern pattern = Pattern.compile(FD_LIST_REGEX);
        Matcher matcher = pattern.matcher(fd);
        if (!matcher.matches()) {
            return false;
        }

        // Checking for duplicates in the LHS or RHS of the FD.
        String[] fds = fd.split("->");
        String lhs = fds[0].trim();
        String rhs = fds[1].trim();
        String[] lhsAttributes = lhs.split(",");
        String[] rhsAttributes = rhs.split(",");

        Set<String> lhsSet = new HashSet<>();
        Set<String> rhsSet = new HashSet<>();
        // Checking for duplicate attributes in LHS
        for (String attribute : lhsAttributes) {
            if (!lhsSet.add(attribute.trim())) {
                System.out.println("Error: Duplicate attribute found in the LHS of the functional dependency. Please correct and try again.");
                return false; // Duplicate attribute found in the LHS
            }
        }
        // Check for duplicate attributes in RHS
        for (String attribute : rhsAttributes) {
            if (!rhsSet.add(attribute.trim())) {
                System.out.println("Error: Duplicate attribute found in the RHS of the functional dependency. Please correct and try again.");
                return false; // Duplicate attribute found in the RHS
            }
        }

        return true;
    }
    // To parse a list of FDs from a string.
    public static List<Pair<String, String>> parseFDList(String fdList) {
        Pattern pattern = Pattern.compile(FD_REGEX);
        Matcher matcher = pattern.matcher(fdList);
        // List to store parsed FDs.
        List<Pair<String, String>> fds = new ArrayList<>();

        while (matcher.find()) {
            String lhs = sortAttributes(matcher.group(1));
            String rhs = sortAttributes(matcher.group(2));
            Pair<String, String> fd = new Pair<>(lhs, rhs);

            // Check for trivial functional dependencies
            if (lhs.equals(rhs)) {
                System.out.println("Notice: The functional dependency " + lhs + " -> " + rhs + " is a trivial functional dependency. It always true and doesn't provide additional information about the structure of the data. ");
            }
            // Checking for duplicate FDs and throw an error if one is found.
            if (uniqueFDs.contains(fd)) {
                throw new IllegalArgumentException("Duplicate functional dependency detected.");
            }
            // If FD is valid, add it to the list and the set of unique FDs.
            uniqueFDs.add(fd);
            fds.add(fd);
        }

        // Return the list of parsed FDs.
        return fds;
    }

    // Method to sort attributes in a FD in alphabetical order.
    public static String sortAttributes(String attributes) {
        String[] attrs = attributes.split("\\s*,\\s*");
        Arrays.sort(attrs);
        return String.join(", ", attrs);
    }
    // A Pair class to represent an FD, with the LHS and the RHS.
    public static record Pair<L, R>(L left, R right) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Pair<?, ?> pair = (Pair<?, ?>) obj;
            return left.equals(pair.left) && right.equals(pair.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }
}
