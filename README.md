# Toy Language Interpreter

An interpreter for a small, custom-designed programming language that turns high-level programs into individual statements and runs them one step at a time. The project is both a hands-on exploration of language design and a showcase of core interpreter techniques: execution stacks, symbol and file tables, dynamic memory management with garbage collection, and multithreading.

## Overview

The interpreter decomposes a program into individual statements and executes them in order, keeping track of the program state through several internal structures:
- **Execution Stack**: Holds the statements that are still waiting to be executed.
- **Symbol Table**: Maps variable names to their current values.
- **Heap**: Stores dynamically allocated values and is cleaned up by a garbage collector.
- **File Table**: Keeps track of the files opened by the running program.
- **Output List**: Gathers everything the program prints, in order.

On top of this, the language is concurrent: the `fork` statement starts a new thread, and every thread is a program state of its own. Each thread has a private execution stack and a copy of its parent's symbol table, while the heap, file table and output list are shared by all threads.

## Features

- **Step-by-Step Execution**
  - Breaks high-level programs down into discrete statements.
  - Relies on an execution stack to decide which statement runs next.
- **Memory & Resource Management**
  - **Symbol Table**: Stores variables and their values.
  - **Heap**: Handles dynamic memory allocation, with a garbage collector that frees unreachable locations.
  - **File Table**: Manages open files and file operations.
  - **Output List**: Collects and presents the program's output.
- **Multithreading and Concurrency**
  - **Fork Support**: `fork(stmt)` creates a new thread with a unique id, its own execution stack and a copy of the parent's symbol table.
  - **Parallel Scheduler**: The controller runs the program in rounds, executing one step of every active thread in parallel with a Java `ExecutorService`.
  - **Thread-Safe Shared State**: The heap, file table and output list are shared by all threads and protected by synchronized methods.
  - **Concurrent Garbage Collection**: Between rounds, the garbage collector keeps every heap location reachable from the symbol table of any thread.
- **Error Handling**
  - Types are checked while the program runs: assigning, allocating or writing a value of the wrong type stops execution with a descriptive message.
  - A custom exception hierarchy separates expression, statement, data structure and repository errors, all reported to the menu instead of crashing the interpreter.
- **Extensible Design**
  - Every statement and expression is an immutable record implementing a single interface (`IStmt` or `IExp`), so a new language feature is one new class.

## Architecture

The project follows the **Model-View-Controller (MVC)** pattern, with a repository layer between the controller and the program states:
- **Model**: The language itself (types, values, expressions, statements), the program state, and the data structures that hold it.
- **View**: A command-based text menu from which the example programs are selected and run.
- **Controller**: Acts as a scheduler: it removes finished threads, runs the garbage collector, and executes one step of all remaining threads in parallel.
- **Repository**: Holds the list of program states (one per thread) and writes a snapshot of every thread's state, tagged with its id, to a log file.

## Testing

200+ JUnit tests cover the language from the bottom up:
- the data structures (stack, dictionary, list, heap), including concurrent heap allocations;
- every expression and statement, with each of its error cases;
- the `fork` statement: a copied symbol table and shared heap, file table and output;
- the garbage collector, including locations reachable only through the heap or from a child thread;
- the scheduler and the log file, by running the example programs end to end.

Run them with `./gradlew test`.

## Project Structure

```
app/src/
├── main/java/interpreter/
│   ├── Main.java            # entry point: builds the menu with the example programs
│   ├── controller/          # the parallel scheduler and the garbage collector
│   ├── exceptions/          # interpreter exception hierarchy
│   ├── model/
│   │   ├── PrgState.java    # one thread: id, stack, symbol table, output, file table, heap
│   │   ├── adt/             # stack, dictionary, list and heap (the shared ones thread-safe)
│   │   ├── exp/             # expressions (arithmetic, logical, relational, heap reading, ...)
│   │   ├── stmt/            # statements (assignment, if, while, print, files, heap, fork, ...)
│   │   ├── type/            # language types (int, bool, string, Ref)
│   │   └── value/           # runtime values
│   ├── repository/          # the list of program states and log file writing
│   └── view/                # text menu, commands and example programs
└── test/java/interpreter/   # JUnit tests, mirroring the main package structure
```
