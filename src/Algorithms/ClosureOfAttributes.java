package Algorithms;

import Grammar.FDGrammar;

import java.util.*;

public class ClosureOfAttributes {
    public static Set<String> computeClosure(Set<String> attributes, List<FDGrammar.Pair<Set<String>, Set<String>>> fds) {
        Set<FDGrammar.Pair<Set<String>, Set<String>>> unused = new HashSet<>(fds);
        Set<String> closure = new HashSet<>(attributes);

        boolean addedNewAttributes;
        do {
            addedNewAttributes = false;

            Iterator<FDGrammar.Pair<Set<String>, Set<String>>> iterator = unused.iterator();
            while (iterator.hasNext()) {
                FDGrammar.Pair<Set<String>, Set<String>> fd = iterator.next();
                Set<String> lhs = fd.left();
                Set<String> rhs = fd.right();

                if (closure.containsAll(lhs)) {
                    int initialClosureSize = closure.size();
                    closure.addAll(rhs);
                    addedNewAttributes |= (closure.size() > initialClosureSize);
                    iterator.remove(); // Remove the processed FD from the set
                }
            }
        } while (addedNewAttributes && !unused.isEmpty());

        return closure;
    }
}
