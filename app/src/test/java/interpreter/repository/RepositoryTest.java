package interpreter.repository;

import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void getCrtPrgReturnsTheStoredState() {
        PrgState state = PrgState.of(new NopStmt());
        IRepository repo = new Repository(state);
        assertSame(state, repo.getCrtPrg());
    }
}
