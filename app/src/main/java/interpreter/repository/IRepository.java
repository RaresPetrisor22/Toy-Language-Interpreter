package interpreter.repository;

import interpreter.model.PrgState;
import interpreter.exceptions.MyException;

public interface IRepository {
    PrgState getCrtPrg();
    void logPrgStateExec() throws MyException;
}
