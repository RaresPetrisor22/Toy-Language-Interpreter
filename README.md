# Toy Language Interpreter

An interpreter for a small, custom-designed programming language that turns high-level programs into individual statements and runs them one step at a time. The project is both a hands-on exploration of language design and a showcase of core interpreter techniques: execution stacks, symbol and file tables, dynamic memory management with garbage collection, and multithreading.

## Overview

The interpreter decomposes a program into individual statements and executes them in order, keeping track of the program state through several internal structures:
- **Execution Stack**: Holds the statements that are still waiting to be executed.
- **Symbol Table**: Maps variable names to their current values.
- **Heap**: Stores dynamically allocated values and is cleaned up by a garbage collector.
- **File Table**: Keeps track of the files opened by the running program.
- **Output List**: Gathers everything the program prints, in order.

On top of this, the interpreter supports concurrent execution. Programs can fork new threads of execution, and, depending on the branch, coordinate them using synchronization constructs such as barriers, conditional latches, procedures and semaphores.

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
  - **Fork Support**: Lets a program split into parallel execution paths.
  - **Synchronization Primitives**: Provides barriers, conditional latches and semaphores to coordinate threads safely.
- **Robust Error Handling**
  - Reports clear, descriptive messages for both type and runtime errors.
- **Extensible Architecture**
  - A modular design that makes adding new language features straightforward.
