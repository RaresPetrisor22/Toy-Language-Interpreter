package interpreter.repository;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository {
    private final List<PrgState> prgStates = new ArrayList<>();
    private final String logFilePath;

    public Repository(PrgState initial, String logFilePath) {
        prgStates.add(initial);
        this.logFilePath = logFilePath;
    }

    @Override
    public PrgState getCrtPrg() {
        return prgStates.getFirst();
    }

    @Override
    public void logPrgStateExec() throws MyException {
        try (var logFile = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true))))
            {
            logFile.println(getCrtPrg().toLogString());
        } catch (IOException e) {
            throw new RepositoryException("cannot write the log file " + logFilePath + ": " + e.getMessage());
        }
    }
}
