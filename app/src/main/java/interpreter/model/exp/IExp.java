package interpreter.model.exp;

import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.value.IValue;

public interface IExp {
    IValue eval(IDictionary<String, IValue> symTable) throws MyException;
}