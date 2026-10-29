package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;

import java.util.List;
import java.util.Scanner;

public class TextMenu {
    private final List<IStmt> programs = List.of(Examples.EX1, Examples.EX2, Examples.EX3);
    private final Scanner scanner = new Scanner(System.in);
    private IStmt selected;

    public void show() {
        while (true) {
            System.out.println();
            System.out.println("1. Select a program");
            System.out.println("2. Run the selected program");
            System.out.println("0. Exit");
            System.out.print("> ");
            if (!scanner.hasNextLine()) return;
            switch (scanner.nextLine().trim()) {
                case "1" -> selectProgram();
                case "2" -> runSelected();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void selectProgram() {
        for (int i = 0; i < programs.size(); i++)
            System.out.println((i + 1) + ". " + programs.get(i));
        System.out.print("Program number: ");
        if (!scanner.hasNextLine()) return;
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= programs.size()) {
                System.out.println("No program with that number.");
                return;
            }
            selected = programs.get(index);
            System.out.println("Selected: " + selected);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
        }
    }

    private void runSelected() {
        if (selected == null) {
            System.out.println("Select a program first.");
            return;
        }

        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), selected);
        try {
            new Controller(new Repository(state), true).allStep();
        } catch (MyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
