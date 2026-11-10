package interpreter.repository;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {
    @TempDir
    Path tmp;

    @Test
    void getCrtPrgReturnsTheStoredState() {
        PrgState state = PrgState.of(new NopStmt());
        IRepository repo = new Repository(state, tmp.resolve("log.txt").toString());
        assertSame(state, repo.getCrtPrg());
    }

    @Test
    void logContainsTheFourSections() throws MyException, IOException {
        PrgState state = PrgState.of(new PrintStmt(new VarExp("a")));
        state.getSymTable().put("a", new IntValue(5));
        state.getOut().add(new IntValue(7));
        state.getFileTable().put(new StringValue("test.in"), new BufferedReader(new StringReader("")));
        Path log = tmp.resolve("log.txt");

        new Repository(state, log.toString()).logPrgStateExec();

        assertEquals(List.of(
                "ExeStack:", "print(a)",
                "SymTable:", "a --> 5",
                "Out:", "7",
                "FileTable:", "test.in",
                ""), Files.readAllLines(log));
    }

    @Test
    void logAppendsToExistingFile() throws MyException, IOException {
        Path log = tmp.resolve("log.txt");
        Files.writeString(log, "old\n");
        IRepository repo = new Repository(PrgState.of(new NopStmt()), log.toString());

        repo.logPrgStateExec();
        repo.logPrgStateExec();

        String content = Files.readString(log);
        assertTrue(content.startsWith("old\n"));
        assertEquals(2, content.split("ExeStack:", -1).length - 1);
    }

    @Test
    void unwritableLogPathThrows() {
        IRepository repo = new Repository(PrgState.of(new NopStmt()), tmp.toString());
        var ex = assertThrows(RepositoryException.class, repo::logPrgStateExec);
        assertTrue(ex.getMessage().startsWith("cannot write the log file " + tmp + ": "), ex.getMessage());
    }
}
