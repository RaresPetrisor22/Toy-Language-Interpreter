package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();   // remove the initial program, like the controller does
    }

    @Test
    void pushesFirstOnTopOfSecond() throws MyException {
        IStmt first = new PrintStmt(new ValueExp(new IntValue(1)));
        IStmt second = new PrintStmt(new ValueExp(new IntValue(2)));
        new CompStmt(first, second).execute(state);

        assertEquals(first, state.getExeStack().pop());
        assertEquals(second, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void doesNotChangeSymTableOrOut() throws MyException {
        new CompStmt(new NopStmt(), new NopStmt()).execute(state);

        assertFalse(state.getSymTable().isDefined("v"));
        assertEquals(0, state.getOut().size());
    }
}
