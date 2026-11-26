package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IValue;

import java.util.Map;

public interface IHeap {
    int allocate(IValue value);                      // stores value at a new address
    IValue get(int address) throws ADTException;
    void update(int address, IValue value) throws ADTException;
    boolean isDefined(int address);
    Map<Integer, IValue> getContent();               // a read-only copy
    void setContent(Map<Integer, IValue> content);   // used by the garbage collector
}
