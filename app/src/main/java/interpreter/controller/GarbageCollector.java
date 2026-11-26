package interpreter.controller;

import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GarbageCollector {
    private GarbageCollector() {}

    /** The addresses that occur in the RefValues of the given values (the SymTable: the roots). */
    public static Set<Integer> getAddrFromSymTable(Collection<IValue> symTableValues) {
        return symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).address())
                .collect(Collectors.toSet());
    }

    /**
     * Every address reachable from the roots: a root itself, or an address stored in a RefValue
     * inside a location that is already reachable. Repeats until no new address is found.
     */
    public static Set<Integer> reachableAddresses(Set<Integer> roots, Map<Integer, IValue> heap) {
        Set<Integer> reachable = new HashSet<>(roots);
        Set<Integer> frontier = roots;
        while (!frontier.isEmpty()) {
            Set<Integer> next = getAddrFromSymTable(frontier.stream()
                    .filter(heap::containsKey)
                    .map(heap::get)
                    .toList());
            next.removeAll(reachable);              // only the addresses seen for the first time
            reachable.addAll(next);
            frontier = next;
        }
        return reachable;
    }

    /** SAFE: keeps the heap entries reachable from the SymTable, directly or through the heap. */
    public static Map<Integer, IValue> safeGarbageCollector(Set<Integer> symTableAddr,
                                                            Map<Integer, IValue> heap) {
        Set<Integer> reachable = reachableAddresses(symTableAddr, heap);
        return heap.entrySet().stream()
                .filter(e -> reachable.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
