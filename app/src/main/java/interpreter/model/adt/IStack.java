package interpreter.model.adt;

import java.util.List;
import interpreter.exceptions.ADTException;

public interface IStack<T> {
    void push(T v);
    T pop() throws ADTException;
    boolean isEmpty();
    List<T> toList();
}
