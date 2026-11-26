package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.exp.ReadHeapExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.*;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GarbageCollectorTest {
    private static final IntType INT = new IntType();

    // the state of Ex9 after new(v, 30): v -> (3, int), a -> (2, Ref(int))
    private final List<IValue> symTable = List.of(
            new RefValue(3, INT), new RefValue(2, new RefType(INT)));
    private final Map<Integer, IValue> heap = Map.of(
            1, new IntValue(20), 2, new RefValue(1, INT),
            3, new IntValue(30), 4, new IntValue(7));

    @TempDir
    Path tmp;

    @Test
    void addressesComeOnlyFromRefValues() {
        var values = List.<IValue>of(new IntValue(5), new BoolValue(true), new RefValue(3, INT), new RefValue(2, INT));
        assertEquals(Set.of(2, 3), GarbageCollector.getAddrFromSymTable(values));
    }

    @Test
    void reachableAddressesFollowTheHeap() {
        assertEquals(Set.of(1, 2, 3), GarbageCollector.reachableAddresses(Set.of(2, 3), heap));
    }

    @Test
    void reachableAddressesFollowLongChains() {
        // 4 -> 3 -> 2 -> 1 -> 20, and 5 is garbage
        Map<Integer, IValue> chain = Map.of(
                1, new IntValue(20), 2, new RefValue(1, INT), 3, new RefValue(2, new RefType(INT)),
                4, new RefValue(3, new RefType(new RefType(INT))), 5, new IntValue(9));
        assertEquals(Set.of(1, 2, 3, 4), GarbageCollector.reachableAddresses(Set.of(4), chain));
    }

    @Test
    void reachableAddressesTerminateOnCycles() {
        Map<Integer, IValue> cycle = Map.of(1, new RefValue(2, INT), 2, new RefValue(1, INT));
        assertEquals(Set.of(1, 2), GarbageCollector.reachableAddresses(Set.of(1), cycle));
    }

    @Test
    void nullAndDanglingRootsAreIgnored() {
        assertEquals(Map.of(), GarbageCollector.safeGarbageCollector(Set.of(0, 99), heap));
    }

    @Test
    void keepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(Set.of(1, 2, 3), kept.keySet());        // 4 is garbage, 1 is not
        assertEquals(new IntValue(20), kept.get(1));
    }

    @Test
    void removesEverythingWhenNoReferencesRemain() {
        assertEquals(Map.of(), GarbageCollector.safeGarbageCollector(Set.of(), heap));
    }

    @Test
    void controllerCollectsAfterEveryStep() throws MyException {
        // Ref int v; new(v, 20); new(v, 30)  -> location 1 becomes garbage
        IStmt program = new CompStmt(new VarDeclStmt("v", new RefType(INT)),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                        new NewStmt("v", new ValueExp(new IntValue(30)))));
        PrgState state = PrgState.of(program);
        new Controller(new Repository(state, tmp.resolve("log.txt").toString()), false).allStep();
        assertEquals(Map.of(2, new IntValue(30)), state.getHeap().getContent());
    }

    @Test
    void ex9PrintsTwentyWithTheSafeCollector() throws MyException {
        // Ref int v; new(v, 20); Ref Ref int a; new(a, v); new(v, 30); print(rH(rH(a)))
        IStmt ex9 = new CompStmt(new VarDeclStmt("v", new RefType(INT)),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                new CompStmt(new VarDeclStmt("a", new RefType(new RefType(INT))),
                new CompStmt(new NewStmt("a", new VarExp("v")),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(30))),
                        new PrintStmt(new ReadHeapExp(new ReadHeapExp(new VarExp("a")))))))));
        PrgState state = PrgState.of(ex9);
        new Controller(new Repository(state, tmp.resolve("log.txt").toString()), false).allStep();
        assertEquals(List.of(new IntValue(20)), state.getOut().toList());
    }
}
