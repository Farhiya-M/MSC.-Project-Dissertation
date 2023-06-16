package Grammar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class FDGrammar {
    // Regular expressions used for FD syntax validation.
    // Validating an attribute (single character in upper case).
    private static final String SINGLE_ATTRIBUTE = "[A-Z]";
    //Validating a comma-separated list of attributes
    private static final String ATTRIBUTES_LIST = "(" + SINGLE_ATTRIBUTE + "(?:\\s*,\\s*" + SINGLE_ATTRIBUTE + ")*)";
    //Match for functional dependency(attributes separated by '->').
    private static final String SINGLE_FD = ATTRIBUTES_LIST + "\\s*->\\s*" + ATTRIBUTES_LIST;
    //Match for a list of functional dependencies (separated by commas).
    private static final String FD_LIST = SINGLE_FD + "(?:\\s*,\\s*" + SINGLE_FD + ")*";

    // A set for storing unique FDs. Each FD is represented using a pair object.
    private static final Set<Pair<String, String>> uniqueFDs = new HashSet<>();


    /** Checking if a given string represents a valid FD
     * @param fd the string to check
     * @return true if the string is a valid FD; false otherwise
     */
    public static boolean isValidFD(String fd) {
        // Compiling a pattern from the valid FDs in the list.
        Pattern list_pattern = Pattern.compile(FD_LIST);
        //matching entire input sequence against the pattern.
        Matcher fdListMatcher = list_pattern.matcher(fd);
        if (!fdListMatcher.matches()) {
            return false;
        }

        // Checking for duplicates in the LHS or RHS of the FD.
        //splitting the input fd into strings separated by '->', removing the whitespaces.
        String[] fds = fd.split("->");
        //before the '->' is the LHS of the FD and after is the RHS of the FD.
        String lhs = fds[0].trim();
        String rhs = fds[1].trim();
        //further splitting the LHS and RHS into individual attributes.
        String[] lhsAttributes = lhs.split(",");
        String[] rhsAttributes = rhs.split(",");

        Set<String> lhsSet = new HashSet<>();
        Set<String> rhsSet = new HashSet<>();
        // Checking for duplicate attributes in LHS
        for (String attribute : lhsAttributes) { //using a for-each loop over each lhs attribute.
            if (!lhsSet.add(attribute.trim())) {
                System.out.println("Error: Duplicate attribute found in the LHS of the functional dependency. Please correct and try again.");
                return false; // Duplicate attribute found in the LHS
            }
        }
        // Check for duplicate attributes in RHS
        for (String attribute : rhsAttributes) {//using a for-each loop over each rhs attribute.
            if (!rhsSet.add(attribute.trim())) {
                System.out.println("Error: Duplicate attribute found in the RHS of the functional dependency. Please correct and try again.");
                return false; // Duplicate attribute found in the RHS
            }
        }

        return true;
    }
    /**
     * Parses a list of FDs from a provided string.
     * Each FD in the string is identified, validated,
     * and then if valid and non-duplicative, added to a list.
     *
     * @param fdList A string containing a list of functional dependencies     *
     * @return A list of Pairs, where each Pair represents an FD
     *         with the lhs and the rhs as its elements.
     * @throws IllegalArgumentException If a duplicate functional dependency is found in the list.
     */
    public static List<Pair<String, String>> parseFDList(String fdList) {
        // Defining and compiling a Pattern for individual fds.
        Pattern fd_pattern = Pattern.compile(SINGLE_FD);

        // A Matcher for the fd_pattern against the given string.
        Matcher fdMatcher = fd_pattern.matcher(fdList);

        // Storing parsed FDs.
        List<Pair<String, String>> fds = new ArrayList<>();

        // Looping through all FDs found by the matcher.
        while (fdMatcher.find()) {
            // Sort attributes in each FD and create a new Pair object.
            String LHS = sortAttributes(fdMatcher.group(1));
            String RHS = sortAttributes(fdMatcher.group(2));
            //Pair object with LHS and RHS of the FD.
            Pair<String, String> fd = new Pair<>(LHS, RHS);

            // Checking for trivial functional dependencies and sending a note if one is found (eg. A->A)
            if (LHS.equals(RHS)) {
                System.out.println("Kindly Note: The functional dependency " + LHS + " -> " + RHS + " is a trivial functional dependency. It always true and doesn't provide additional information about the structure of the data.");
            }
            // Check for duplicate FDs and throw an error if one is found.
            if (uniqueFDs.contains(fd)) {
                throw new IllegalArgumentException("Duplicate functional dependency detected.");
            }
            // If FD is valid and not duplicated, it is added to the list and the set of unique FDs.
            uniqueFDs.add(fd);
            fds.add(fd);
        }

        // List of parsed FDs returned
        return fds;
    }


    // Method to sort attributes in aN FD in alphabetical order.
    public static String sortAttributes(String attributes) {
        String[] attrs = attributes.split("\\s*,\\s*");
        Arrays.sort(attrs);
        //concatenating the sorted attributes into a single string.
        return String.join(", ", attrs);
    }
    /**
     * Equals() method is overridden. Two Pairs are considered equal only when both their left and right elements are equal.
     * Two FDs are equal iff they have the same attribute sets on both sides of the FD.
     * HashCode() method is overridden to generate a unique hash code for each Pair, based on the lhs and rhs elements.
     */
    public record Pair<L, R>(L left, R right) {
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
