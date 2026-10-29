package interpreter.repository;

import interpreter.model.PrgState;

import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository {
    private final List<PrgState> prgStates = new ArrayList<>();

    public Repository(PrgState prgState) {
        prgStates.add(prgState);
    }

    @Override
    public PrgState getCrtPrg() {
        return prgStates.getFirst();
    }
}
