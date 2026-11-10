package interpreter.model.stmt;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class OpenRFileStmtTest {
    @TempDir
    Path tmp;

    private PrgState state;
    private StringValue testIn;

    @BeforeEach
    void setUp() throws MyException, IOException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();

        Path file = tmp.resolve("test.in");
        Files.writeString(file, "15\n50\n");
        testIn = new StringValue(file.toString());
    }

    @AfterEach
    void closeOpenedFiles() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    // rule S8: successful open

    @Test
    void addsFileNameAndReaderToFileTable() throws MyException {
        new OpenRFileStmt(new ValueExp(testIn)).execute(state);

        assertTrue(state.getFileTable().isDefined(testIn));
        assertEquals(1, state.getFileTable().toMap().size());
    }

    @Test
    void readerStartsAtTheBeginningOfTheFile() throws MyException, IOException {
        new OpenRFileStmt(new ValueExp(testIn)).execute(state);

        BufferedReader reader = state.getFileTable().lookup(testIn);
        assertEquals("15", reader.readLine());
    }

    @Test
    void fileNameCanComeFromAVariable() throws MyException {
        state.getSymTable().put("varf", testIn);

        new OpenRFileStmt(new VarExp("varf")).execute(state);

        assertTrue(state.getFileTable().isDefined(testIn));
    }

    @Test
    void lookupWorksWithAnEqualButDifferentStringValue() throws MyException {
        new OpenRFileStmt(new ValueExp(testIn)).execute(state);

        assertTrue(state.getFileTable().isDefined(new StringValue(testIn.val())));
    }

    @Test
    void doesNotChangeTheOtherStructures() throws MyException {
        new OpenRFileStmt(new ValueExp(testIn)).execute(state);

        assertTrue(state.getExeStack().isEmpty());
        assertEquals(0, state.getOut().size());
        assertTrue(state.getSymTable().toMap().isEmpty());
    }

    // ---- rule S8: errors ----

    @Test
    void intFileNameThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new OpenRFileStmt(new ValueExp(new IntValue(5))).execute(state));
        assertEquals("openRFile: 5 is not a string", ex.getMessage());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void boolVariableAsFileNameThrows() {
        state.getSymTable().put("varf", new BoolValue(true));

        var ex = assertThrows(StatementException.class,
                () -> new OpenRFileStmt(new VarExp("varf")).execute(state));
        assertEquals("openRFile: varf is not a string", ex.getMessage());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileAlreadyOpenThrowsAndKeepsTheFirstReader() throws MyException {
        new OpenRFileStmt(new ValueExp(testIn)).execute(state);
        BufferedReader first = state.getFileTable().lookup(testIn);

        var ex = assertThrows(StatementException.class,
                () -> new OpenRFileStmt(new ValueExp(testIn)).execute(state));
        assertEquals("openRFile: " + testIn + " is already opened", ex.getMessage());
        assertSame(first, state.getFileTable().lookup(testIn));
        assertEquals(1, state.getFileTable().toMap().size());
    }

    @Test
    void missingFileThrows() {
        StringValue missing = new StringValue(tmp.resolve("missing.in").toString());

        var ex = assertThrows(StatementException.class,
                () -> new OpenRFileStmt(new ValueExp(missing)).execute(state));
        assertTrue(ex.getMessage().startsWith("openRFile: cannot open " + missing + ": "),
                ex.getMessage());
        assertFalse(state.getFileTable().isDefined(missing));
    }

    // ---- infix form ----

    @Test
    void toStringShowsTheExpression() {
        assertEquals("openRFile(varf)", new OpenRFileStmt(new VarExp("varf")).toString());
        assertEquals("openRFile(\"test.in\")",
                new OpenRFileStmt(new ValueExp(new StringValue("test.in"))).toString());
    }
}
