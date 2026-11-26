package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;

public record LogicExp(LogicOp op, IExp left, IExp right) implements IExp {
    @Override
    public IValue eval(IDictionary<String, IValue> symTable, IHeap heap) throws MyException {
        IValue v1 = left.eval(symTable, heap);
        if (!(v1 instanceof BoolValue))
            throw new ExpressionException("first operand is not a boolean");
        boolean b1 = ((BoolValue) v1).val();

        IValue v2 = right.eval(symTable, heap);
        if (!(v2 instanceof BoolValue))
            throw new ExpressionException("second operand is not a boolean");
        boolean b2 = ((BoolValue) v2).val();

        return switch (op) {
            case AND -> new BoolValue(b1 && b2);
            case OR -> new BoolValue(b1 || b2);
        };
    }

    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }
}