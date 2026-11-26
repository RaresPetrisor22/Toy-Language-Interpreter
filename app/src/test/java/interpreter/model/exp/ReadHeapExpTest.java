package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadHeapExpTest {
    private IDictionary<String, IValue> symTable;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
        heap = new MyHeap();
        // the state of Ex7: heap {1 -> 20, 2 -> (1, int)}, v -> (1, int), a -> (2, Ref(int))
        heap.allocate(new IntValue(20));
        heap.allocate(new RefValue(1, new IntType()));
        symTable.put("v", new RefValue(1, new IntType()));
        symTable.put("a", new RefValue(2, new RefType(new IntType())));
    }

    @Test
    void readsTheValueAtTheAddress() throws MyException {
        assertEquals(new IntValue(20), new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
    }

    @Test
    void readOfAReferenceReturnsTheReference() throws MyException {
        assertEquals(new RefValue(1, new IntType()), new ReadHeapExp(new VarExp("a")).eval(symTable, heap));
    }

    @Test
    void nestedReadFollowsTheChain() throws MyException {
        var e = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));
        assertEquals(new IntValue(20), e.eval(symTable, heap));
    }

    @Test
    void readInsideArithmetic() throws MyException {
        var e = new ArithExp(ArithOp.ADD, new ReadHeapExp(new ReadHeapExp(new VarExp("a"))),
                new ValueExp(new IntValue(5)));
        assertEquals(new IntValue(25), e.eval(symTable, heap));
    }

    @Test
    void readOfNonReferenceThrows() {
        var e = new ReadHeapExp(new ValueExp(new IntValue(1)));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable, heap));
        assertEquals("rH: 1 is not a reference", ex.getMessage());
    }

    @Test
    void readOfAddressNotInTheHeapThrows() {
        symTable.put("n", new RefValue(7, new IntType()));
        var ex = assertThrows(ExpressionException.class, () -> new ReadHeapExp(new VarExp("n")).eval(symTable, heap));
        assertEquals("rH: address 7 is not in the heap", ex.getMessage());
    }

    @Test
    void readThroughNullReferenceThrows() {
        symTable.put("n", new RefValue(0, new IntType()));
        assertThrows(ExpressionException.class, () -> new ReadHeapExp(new VarExp("n")).eval(symTable, heap));
    }

    @Test
    void toStringShowsTheExpression() {
        assertEquals("rH(rH(a))", new ReadHeapExp(new ReadHeapExp(new VarExp("a"))).toString());
    }
}
