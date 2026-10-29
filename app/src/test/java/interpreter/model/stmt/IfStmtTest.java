package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
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

class IfStmtTest {
    private PrgState state;
    private final IStmt thenS = new AssignStmt("v", new ValueExp(new IntValue(2)));
    private final IStmt elseS = new AssignStmt("v", new ValueExp(new IntValue(3)));

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void trueConditionPushesThenBranch() throws MyException {
        new IfStmt(new ValueExp(new BoolValue(true)), thenS, elseS).execute(state);
        assertEquals(thenS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void falseConditionPushesElseBranch() throws MyException {
        new IfStmt(new ValueExp(new BoolValue(false)), thenS, elseS).execute(state);
        assertEquals(elseS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void conditionIsEvaluatedAgainstSymTable() throws MyException {
        state.getSymTable().put("a", new BoolValue(true));
        new IfStmt(new VarExp("a"), thenS, elseS).execute(state);
        assertEquals(thenS, state.getExeStack().pop());
    }

    @Test
    void nonBooleanConditionThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new IfStmt(new ValueExp(new IntValue(1)), thenS, elseS).execute(state));
        assertEquals("condition is not a boolean", ex.getMessage());
        assertTrue(state.getExeStack().isEmpty());
    }
}
