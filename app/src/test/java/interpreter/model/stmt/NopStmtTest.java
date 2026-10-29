package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NopStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void leavesStateUnchanged() throws MyException {
        state.getSymTable().put("v", new IntValue(2));
        new NopStmt().execute(state);

        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
        assertEquals(0, state.getOut().size());
    }

    @Test
    void returnsTheSameState() throws MyException {
        assertSame(state, new NopStmt().execute(state));
    }
}
