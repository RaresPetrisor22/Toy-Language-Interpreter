package interpreter.model.stmt;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CloseRFileStmtTest {
    @TempDir
    Path tmp;

    private PrgState state;
    private StringValue testIn;
    private BufferedReader reader;

    @BeforeEach
    void setUp() throws MyException, IOException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        testIn = open("test.in", "15\n50\n");
        reader = state.getFileTable().lookup(testIn);
    }

    @AfterEach
    void closeOpenedFiles() throws IOException {
        reader.close();
        for (BufferedReader r : state.getFileTable().toMap().values())
            r.close();
    }

    private StringValue open(String name, String content) throws IOException {
        Path file = tmp.resolve(name);
        Files.writeString(file, content);
        StringValue fileName = new StringValue(file.toString());
        state.getFileTable().put(fileName, Files.newBufferedReader(file));
        return fileName;
    }

    private void close(StringValue fileName) throws MyException {
        new CloseRFileStmt(new ValueExp(fileName)).execute(state);
    }

    @Test
    void removesFileFromFileTable() throws MyException {
        close(testIn);
        assertFalse(state.getFileTable().isDefined(testIn));
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void closesTheReader() throws MyException {
        close(testIn);
        assertThrows(IOException.class, () -> reader.readLine());
    }

    @Test
    void fileNameCanComeFromAVariable() throws MyException {
        state.getSymTable().put("varf", testIn);
        new CloseRFileStmt(new VarExp("varf")).execute(state);
        assertFalse(state.getFileTable().isDefined(testIn));
    }

    @Test
    void otherFilesStayOpen() throws Exception {
        StringValue other = open("other.in", "7\n");
        close(testIn);
        assertTrue(state.getFileTable().isDefined(other));
        assertEquals("7", state.getFileTable().lookup(other).readLine());
    }

    @Test
    void fileCanBeReopenedAfterClose() throws Exception {
        close(testIn);
        new OpenRFileStmt(new ValueExp(testIn)).execute(state);
        assertEquals("15", state.getFileTable().lookup(testIn).readLine());
    }

    @Test
    void doesNotChangeTheOtherStructures() throws MyException {
        close(testIn);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(0, state.getOut().size());
        assertTrue(state.getSymTable().toMap().isEmpty());
    }

    @Test
    void nonStringFileNameThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new CloseRFileStmt(new ValueExp(new IntValue(5))).execute(state));
        assertEquals("closeRFile: 5 is not a string", ex.getMessage());
        assertTrue(state.getFileTable().isDefined(testIn));
    }

    @Test
    void fileNotOpenThrows() {
        StringValue notOpen = new StringValue("missing.in");
        var ex = assertThrows(StatementException.class, () -> close(notOpen));
        assertEquals("closeRFile: \"missing.in\" is not open", ex.getMessage());
        assertTrue(state.getFileTable().isDefined(testIn));
    }

    @Test
    void closingTwiceThrows() throws MyException {
        close(testIn);
        var ex = assertThrows(StatementException.class, () -> close(testIn));
        assertEquals("closeRFile: " + testIn + " is not open", ex.getMessage());
    }

    @Test
    void undefinedVariableErrorPropagates() {
        var ex = assertThrows(ExpressionException.class,
                () -> new CloseRFileStmt(new VarExp("varf")).execute(state));
        assertEquals("variable varf is not defined", ex.getMessage());
        assertTrue(state.getFileTable().isDefined(testIn));
    }

    @Test
    void ioErrorThrowsAndKeepsFileInTable() {
        StringValue broken = new StringValue("broken.in");
        state.getFileTable().put(broken, new BufferedReader(new StringReader("")) {
            @Override
            public void close() throws IOException {
                throw new IOException("disk error");
            }
        });
        var ex = assertThrows(StatementException.class, () -> close(broken));
        assertEquals("closeRFile: cannot close \"broken.in\": disk error", ex.getMessage());
        assertTrue(state.getFileTable().isDefined(broken));
        state.getFileTable().put(broken, new BufferedReader(new StringReader("")));
    }

    @Test
    void toStringShowsTheExpression() {
        assertEquals("closeRFile(varf)", new CloseRFileStmt(new VarExp("varf")).toString());
    }
}
