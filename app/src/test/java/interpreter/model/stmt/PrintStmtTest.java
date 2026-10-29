package interpreter.model.stmt;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrintStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void appendsValueToOut() throws MyException {
        new PrintStmt(new ValueExp(new IntValue(2))).execute(state);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void printsVariableValue() throws MyException {
        state.getSymTable().put("a", new BoolValue(true));
        new PrintStmt(new VarExp("a")).execute(state);
        assertEquals(new BoolValue(true), state.getOut().get(0));
    }

    @Test
    void keepsPrintOrder() throws MyException {
        new PrintStmt(new ValueExp(new IntValue(1))).execute(state);
        new PrintStmt(new ValueExp(new IntValue(2))).execute(state);
        assertEquals(new IntValue(1), state.getOut().get(0));
        assertEquals(new IntValue(2), state.getOut().get(1));
    }

    @Test
    void evaluationErrorPropagates() {
        var ex = assertThrows(ExpressionException.class,
                () -> new PrintStmt(new VarExp("x")).execute(state));
        assertEquals("variable x is not defined", ex.getMessage());
        assertEquals(0, state.getOut().size());
    }
}
