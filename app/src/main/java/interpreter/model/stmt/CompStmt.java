package interpreter.model.stmt;

import interpreter.model.PrgState;

public record CompStmt(IStmt first, IStmt second) implements IStmt {
    @Override
    public PrgState execute(PrgState state) {
        state.getExeStack().push(second);
        state.getExeStack().push(first);
        return state;
    }

    @Override
    public String toString() {
        return "(" + first + "; " + second + ")";
    }
}