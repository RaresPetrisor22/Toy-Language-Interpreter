package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationalExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private IValue eval(RelOp op, int a, int b) throws MyException {
        return new RelationalExp(op, num(a), num(b)).eval(symTable);
    }

    @Test
    void lessThan() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.LT, 3, 4));
        assertEquals(new BoolValue(false), eval(RelOp.LT, 4, 4));
    }

    @Test
    void lessOrEqual() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.LE, 4, 4));
        assertEquals(new BoolValue(false), eval(RelOp.LE, 5, 4));
    }

    @Test
    void equal() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.EQ, 4, 4));
        assertEquals(new BoolValue(false), eval(RelOp.EQ, 3, 4));
    }

    @Test
    void notEqual() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.NE, 3, 4));
        assertEquals(new BoolValue(false), eval(RelOp.NE, 4, 4));
    }

    @Test
    void greaterThan() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.GT, 5, 4));
        assertEquals(new BoolValue(false), eval(RelOp.GT, 4, 4));
    }

    @Test
    void greaterOrEqual() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.GE, 4, 4));
        assertEquals(new BoolValue(false), eval(RelOp.GE, 3, 4));
    }

    @Test
    void evaluatesVariablesFromSymTable() throws MyException {
        symTable.put("a", new IntValue(5));
        symTable.put("b", new IntValue(7));
        IExp e = new RelationalExp(RelOp.LT, new VarExp("a"), new VarExp("b"));
        assertEquals(new BoolValue(true), e.eval(symTable));
    }

    @Test
    void nonIntegerFirstOperandThrows() {
        IExp e = new RelationalExp(RelOp.LT, new ValueExp(new BoolValue(true)), num(1));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("first operand is not an integer", ex.getMessage());
    }

    @Test
    void nonIntegerSecondOperandThrows() {
        IExp e = new RelationalExp(RelOp.EQ, num(1), new ValueExp(new StringValue("1")));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("second operand is not an integer", ex.getMessage());
    }

    @Test
    void toStringIsInfix() {
        assertEquals("(3 >= 4)", new RelationalExp(RelOp.GE, num(3), num(4)).toString());
    }
}
