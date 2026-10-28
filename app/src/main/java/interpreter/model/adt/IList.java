package interpreter.model.adt;

import interpreter.exceptions.ADTException;

public interface IList<T> {
    void add(T v);
    T get(int index) throws ADTException;
    int size();
}
