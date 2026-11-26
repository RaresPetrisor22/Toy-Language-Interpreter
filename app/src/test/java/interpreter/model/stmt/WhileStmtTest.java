package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.*;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WhileStmtTest {
    private PrgState state;

    // while (v > 0) (print(v); v = v - 1)
    private final IStmt countdown = new WhileStmt(
            new RelationalExp(RelOp.GT, new VarExp("v"), new ValueExp(new IntValue(0))),
            new CompStmt(new PrintStmt(new VarExp("v")),
                    new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), new ValueExp(new IntValue(1))))));

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        new VarDeclStmt("v", new IntType()).execute(state);
    }

    /** Runs the loop the way the controller does: pop and execute until the stack is empty. */
    private List<IValue> runCountdownFrom(int start) throws MyException {
        state.getSymTable().update("v", new IntValue(start));
        state.getExeStack().push(countdown);
        while (!state.getExeStack().isEmpty())
            state.getExeStack().pop().execute(state);
        return state.getOut().toList();
    }

    @Test
    void trueConditionPushesBodyAboveTheLoop() throws MyException {
        var body = new NopStmt();
        var loop = new WhileStmt(new ValueExp(new BoolValue(true)), body);
        loop.execute(state);
        assertEquals(body, state.getExeStack().pop());
        assertEquals(loop, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void falseConditionPushesNothing() throws MyException {
        new WhileStmt(new ValueExp(new BoolValue(false)), new NopStmt()).execute(state);
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void loopThatRunsZeroTimes() throws MyException {
        assertEquals(List.of(), runCountdownFrom(0));
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void loopThatRunsOnce() throws MyException {
        assertEquals(List.of(new IntValue(1)), runCountdownFrom(1));
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void loopThatRunsSeveralTimes() throws MyException {
        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2), new IntValue(1)),
                runCountdownFrom(4));
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void whileWithANonBooleanConditionThrows() {
        var loop = new WhileStmt(new ValueExp(new IntValue(1)), new NopStmt());
        var ex = assertThrows(StatementException.class, () -> loop.execute(state));
        assertEquals("while: condition 1 is not a boolean", ex.getMessage());
    }

    @Test
    void toStringShowsTheStatement() {
        assertEquals("(while ((v > 0)) (print(v); v = (v - 1)))", countdown.toString());
    }
}
