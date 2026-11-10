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

class ReadFileStmtTest {
    @TempDir
    Path tmp;

    private PrgState state;
    private StringValue testIn;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        state.getSymTable().put("varc", new IntValue(-1));
    }

    @AfterEach
    void closeOpenedFiles() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    private void open(String content) throws IOException {
        Path file = tmp.resolve("test.in");
        Files.writeString(file, content);
        testIn = new StringValue(file.toString());
        state.getFileTable().put(testIn, Files.newBufferedReader(file));
    }

    private void read() throws MyException {
        new ReadFileStmt(new ValueExp(testIn), "varc").execute(state);
    }

    private IntValue varc() throws MyException {
        return (IntValue) state.getSymTable().lookup("varc");
    }

    @Test
    void readsFirstLine() throws Exception {
        open("15\n50\n");
        read();
        assertEquals(new IntValue(15), varc());
    }

    @Test
    void consecutiveReadsAdvance() throws Exception {
        open("15\n50\n");
        read();
        read();
        assertEquals(new IntValue(50), varc());
    }

    @Test
    void endOfFileGivesZero() throws Exception {
        open("15\n");
        read();
        read();
        assertEquals(new IntValue(0), varc());
    }

    @Test
    void emptyFileGivesZero() throws Exception {
        open("");
        read();
        assertEquals(new IntValue(0), varc());
    }

    @Test
    void surroundingWhitespaceIsIgnored() throws Exception {
        open("  15 \r\n");
        read();
        assertEquals(new IntValue(15), varc());
    }

    @Test
    void lastLineWithoutNewlineIsRead() throws Exception {
        open("15\n50");
        read();
        read();
        assertEquals(new IntValue(50), varc());
    }

    @Test
    void fileNameCanComeFromAVariable() throws Exception {
        open("15\n");
        state.getSymTable().put("varf", testIn);
        new ReadFileStmt(new VarExp("varf"), "varc").execute(state);
        assertEquals(new IntValue(15), varc());
    }

    @Test
    void doesNotChangeTheOtherStructures() throws Exception {
        open("15\n");
        read();
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(0, state.getOut().size());
        assertTrue(state.getFileTable().isDefined(testIn));
    }

    @Test
    void undeclaredVariableThrows() throws Exception {
        open("15\n");
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(new ValueExp(testIn), "x").execute(state));
        assertEquals("readFile: variable x was not declared", ex.getMessage());
        read();
        assertEquals(new IntValue(15), varc());
    }

    @Test
    void nonIntVariableThrows() throws Exception {
        open("15\n");
        state.getSymTable().put("b", new BoolValue(true));
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(new ValueExp(testIn), "b").execute(state));
        assertEquals("readFile: variable b is not of type int", ex.getMessage());
        assertEquals(new BoolValue(true), state.getSymTable().lookup("b"));
        read();
        assertEquals(new IntValue(15), varc());
    }

    @Test
    void nonStringFileNameThrows() throws MyException {
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(new ValueExp(new IntValue(5)), "varc").execute(state));
        assertEquals("readFile: 5 is not a string", ex.getMessage());
        assertEquals(new IntValue(-1), varc());
    }

    @Test
    void fileNotOpenThrows() throws MyException {
        StringValue notOpen = new StringValue("test.in");
        var ex = assertThrows(StatementException.class,
                () -> new ReadFileStmt(new ValueExp(notOpen), "varc").execute(state));
        assertEquals("readFile: \"test.in\" is not open", ex.getMessage());
        assertEquals(new IntValue(-1), varc());
    }

    @Test
    void undefinedFileVariableErrorPropagates() throws MyException {
        var ex = assertThrows(ExpressionException.class,
                () -> new ReadFileStmt(new VarExp("varf"), "varc").execute(state));
        assertEquals("variable varf is not defined", ex.getMessage());
        assertEquals(new IntValue(-1), varc());
    }

    @Test
    void nonIntegerLineThrows() throws Exception {
        open("abc\n");
        var ex = assertThrows(StatementException.class, this::read);
        assertEquals("readFile: line \"abc\" in " + testIn + " is not an integer", ex.getMessage());
        assertEquals(new IntValue(-1), varc());
    }

    @Test
    void ioErrorThrows() throws Exception {
        open("15\n");
        state.getFileTable().lookup(testIn).close();
        var ex = assertThrows(StatementException.class, this::read);
        assertTrue(ex.getMessage().startsWith("readFile: cannot read " + testIn + ": "), ex.getMessage());
        assertEquals(new IntValue(-1), varc());
    }

    @Test
    void toStringShowsExpressionAndVariable() {
        assertEquals("readFile(varf, varc)", new ReadFileStmt(new VarExp("varf"), "varc").toString());
    }
}
