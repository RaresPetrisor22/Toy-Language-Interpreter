package interpreter.model;

import interpreter.model.adt.*;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IValue;
import interpreter.model.value.StringValue;

import java.io.BufferedReader;
import java.util.TreeMap;

public class PrgState {
    private IStack<IStmt> exeStack;
    private IDictionary<String, IValue> symTable;
    private IList<IValue> out;
    private IDictionary<StringValue, BufferedReader> fileTable;
    private IHeap heap;
    private final IStmt originalProgram;

    public PrgState(IStack<IStmt> exeStack, IDictionary<String, IValue> symTable,
                    IList<IValue> out, IDictionary<StringValue, BufferedReader> fileTable,
                    IHeap heap, IStmt program) {
        this.exeStack = exeStack;
        this.symTable = symTable;
        this.out = out;
        this.fileTable = fileTable;
        this.heap = heap;
        this.originalProgram = program;
        exeStack.push(program);
    }

    // factory method
    public static PrgState of(IStmt program) {
        return new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(),
                new MyDictionary<>(), new MyHeap(), program);
    }

    public IStack<IStmt> getExeStack() { return exeStack; }
    public IDictionary<String, IValue> getSymTable() { return symTable; }
    public IList<IValue> getOut() { return out; }
    public IDictionary<StringValue, BufferedReader> getFileTable() { return fileTable; }
    public IHeap getHeap() { return heap; }
    public IStmt getOriginalProgram() { return originalProgram; }

    public void setExeStack(IStack<IStmt> exeStack) { this.exeStack = exeStack; }
    public void setSymTable(IDictionary<String, IValue> symTable) { this.symTable = symTable; }
    public void setOut(IList<IValue> out) { this.out = out; }
    public void setFileTable(IDictionary<StringValue, BufferedReader> fileTable) { this.fileTable = fileTable; }
    public void setHeap(IHeap heap) { this.heap = heap; }

    @Override
    public String toString() {
        return "ExeStack: " + exeStack + "\n"
                + "SymTable: " + symTable + "\n"
                + "Out: " + out + "\n"
                + "Heap: " + heap + "\n";
    }

    public String toLogString() {
        var sb = new StringBuilder();
        sb.append("ExeStack:\n");
        exeStack.toList().forEach(s -> sb.append(s).append('\n'));
        sb.append("SymTable:\n");
        symTable.toMap().forEach((k, v) ->
                sb.append(k).append(" --> ").append(v).append('\n'));
        sb.append("Out:\n");
        out.toList().forEach(v -> sb.append(v).append('\n'));
        sb.append("FileTable:\n");
        fileTable.toMap().keySet().forEach(f -> sb.append(f.val()).append('\n'));
        sb.append("Heap:\n");
        new TreeMap<>(heap.getContent()).forEach((address, v) ->
                sb.append(address).append(" --> ").append(v).append('\n'));
        return sb.toString();

    }
}