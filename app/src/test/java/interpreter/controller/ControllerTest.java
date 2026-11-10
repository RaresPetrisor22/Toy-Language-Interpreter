package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        Controller controller = new Controller(new Repository(state, log().toString()), false);
        controller.oneStep(state);   // executes the nop, leaving the stack empty
        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertEquals("program state stack is empty", ex.getMessage());
    }

    @Test
    void allStepLogsInitialStateAndEveryStep() throws MyException, IOException {
        run(Examples.EX1);
        String content = Files.readString(log());
        assertEquals(6, content.split("ExeStack:", -1).length - 1);

        List<String> lines = Files.readAllLines(log());
        int lastExeStack = lines.lastIndexOf("ExeStack:");
        assertEquals("SymTable:", lines.get(lastExeStack + 1));
        assertEquals(List.of("Out:", "2"), lines.subList(lines.lastIndexOf("Out:"), lines.lastIndexOf("Out:") + 2));
    }
}
