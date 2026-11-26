package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.IStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;

import static interpreter.controller.GarbageCollector.getAddrFromSymTable;
import static interpreter.controller.GarbageCollector.safeGarbageCollector;

public class Controller {
    private final IRepository repo;
    private final boolean displayFlag;

    public Controller(IRepository repo, boolean displayFlag) {
        this.repo = repo;
        this.displayFlag = displayFlag;
    }

    public PrgState oneStep(PrgState state) throws MyException {
        IStack<IStmt> stack = state.getExeStack();
        if (stack.isEmpty()) throw new StatementException("program state stack is empty");
        IStmt crtStmt = stack.pop();
        return crtStmt.execute(state);
    }

    public void allStep() throws MyException {
        PrgState prg = repo.getCrtPrg();
        repo.logPrgStateExec();
        display(prg);
        while (!prg.getExeStack().isEmpty()) {
            oneStep(prg);
            repo.logPrgStateExec();                 // before the collection
            collectGarbage(prg);
            repo.logPrgStateExec();                 // after the collection
            display(prg);
        }
    }

    private void collectGarbage(PrgState prg) {
        prg.getHeap().setContent(safeGarbageCollector(
                getAddrFromSymTable(prg.getSymTable().toMap().values()),
                prg.getHeap().getContent()));
    }

    private void display(PrgState prg) {
        if (displayFlag) System.out.println(prg);
    }
}
