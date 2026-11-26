package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    @TempDir
    Path tmp;

    private Path log() {
        return tmp.resolve("log.txt");
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        new Controller(new Repository(state, log().toString()), false).allStep();   // display off
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = tmp.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState state = run(Examples.fileExample(in.toString()));
        assertEquals(List.of(new IntValue(15), new IntValue(50)), state.getOut().toList());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithEmptyFilePrintsZeros() throws Exception {
        Path in = tmp.resolve("test.in");
        Files.writeString(in, "");
        PrgState state = run(Examples.fileExample(in.toString()));
        assertEquals(List.of(new IntValue(0), new IntValue(0)), state.getOut().toList());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void logFileContainsTheFiveSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(log());
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:"), e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState state = run(Examples.EX5);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(List.of(new IntValue(5)), state.getOut().toList());
    }

    // ----- A4 examples: the results of section 1.4 -----

    private static final IntType INT = new IntType();

    @Test
    void example6PrintsTheReferences() throws MyException {
        PrgState state = run(Examples.EX6);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(Map.of(1, new IntValue(20), 2, new RefValue(1, INT)), state.getHeap().getContent());
        assertEquals(Map.of("v", new RefValue(1, INT), "a", new RefValue(2, new RefType(INT))),
                state.getSymTable().toMap());
        assertEquals(List.of(new RefValue(1, INT), new RefValue(2, new RefType(INT))), state.getOut().toList());
    }

    @Test
    void example7ReadsThroughTheHeap() throws MyException {
        PrgState state = run(Examples.EX7);
        assertEquals(Map.of(1, new IntValue(20), 2, new RefValue(1, INT)), state.getHeap().getContent());
        assertEquals(Map.of("v", new RefValue(1, INT), "a", new RefValue(2, new RefType(INT))),
                state.getSymTable().toMap());
        assertEquals(List.of(new IntValue(20), new IntValue(25)), state.getOut().toList());
    }

    @Test
    void example8WritesTheHeap() throws MyException {
        PrgState state = run(Examples.EX8);
        assertEquals(Map.of(1, new IntValue(30)), state.getHeap().getContent());
        assertEquals(Map.of("v", new RefValue(1, INT)), state.getSymTable().toMap());
        assertEquals(List.of(new IntValue(20), new IntValue(35)), state.getOut().toList());
    }

    @Test
    void example9PrintsTwentyWithTheSafeCollector() throws MyException {
        PrgState state = run(Examples.EX9);
        assertEquals(List.of(new IntValue(20)), state.getOut().toList());
    }

    @Test
    void example10CountsDownWithWhile() throws MyException {
        PrgState state = run(Examples.EX10);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2), new IntValue(1), new IntValue(0)),
                state.getOut().toList());
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        Controller controller = new Controller(new Repository(state, log().toString()), false);
        controller.oneStep(state);   // executes the nop, leaving the stack empty
        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertEquals("program state stack is empty", ex.getMessage());
    }

    @Test
    void allStepLogsInitialStateAndEveryStepBeforeAndAfterCollection() throws MyException, IOException {
        run(Examples.EX1);                          // 5 steps
        String content = Files.readString(log());
        assertEquals(1 + 2 * 5, content.split("ExeStack:", -1).length - 1);

        List<String> lines = Files.readAllLines(log());
        int lastExeStack = lines.lastIndexOf("ExeStack:");
        assertEquals("SymTable:", lines.get(lastExeStack + 1));
        assertEquals(List.of("Out:", "2"), lines.subList(lines.lastIndexOf("Out:"), lines.lastIndexOf("Out:") + 2));
    }
}
