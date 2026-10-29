package interpreter.model.stmt;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void updatesDeclaredVariable() throws MyException {
        state.getSymTable().put("v", new IntValue(0));
        new AssignStmt("v", new ValueExp(new IntValue(2))).execute(state);
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void evaluatesExpressionAgainstSymTable() throws MyException {
        state.getSymTable().put("a", new IntValue(17));
        state.getSymTable().put("b", new IntValue(0));
        new AssignStmt("b", new ArithExp(ArithOp.ADD, new VarExp("a"), new ValueExp(new IntValue(1))))
                .execute(state);
        assertEquals(new IntValue(18), state.getSymTable().lookup("b"));
    }

    @Test
    void assignmentToUndeclaredVariableThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new AssignStmt("x", new ValueExp(new IntValue(1))).execute(state));
        assertEquals("variable x was not declared", ex.getMessage());
    }

    @Test
    void typeMismatchThrows() {
        state.getSymTable().put("v", new IntValue(0));
        var ex = assertThrows(StatementException.class,
                () -> new AssignStmt("v", new ValueExp(new BoolValue(true))).execute(state));
        assertEquals("declared type of variable v and type of the assigned expression do not match",
                ex.getMessage());
    }

    @Test
    void typeMismatchLeavesVariableUnchanged() throws MyException {
        state.getSymTable().put("v", new IntValue(5));
        assertThrows(StatementException.class,
                () -> new AssignStmt("v", new ValueExp(new BoolValue(true))).execute(state));
        assertEquals(new IntValue(5), state.getSymTable().lookup("v"));
    }

    @Test
    void expressionErrorPropagates() {
        state.getSymTable().put("v", new IntValue(0));
        assertThrows(ExpressionException.class,
                () -> new AssignStmt("v", new VarExp("undefined")).execute(state));
    }
}
