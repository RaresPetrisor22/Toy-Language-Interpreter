package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void newListHasSizeZero() {
        assertEquals(0, list.size());
    }

    @Test
    void addAppendsInOrder() throws ADTException {
        list.add(10);
        list.add(20);
        assertEquals(2, list.size());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
    }

    @Test
    void getWithIndexTooLargeThrows() {
        list.add(10);
        assertThrows(ADTException.class, () -> list.get(1));
    }

    @Test
    void getWithNegativeIndexThrows() {
        assertThrows(ADTException.class, () -> list.get(-1));
    }

    @Test
    void toListIsInInsertionOrder() {
        list.add(10);
        list.add(20);
        list.add(30);
        assertEquals(List.of(10, 20, 30), list.toList());
    }

    @Test
    void toListOfEmptyListIsEmpty() {
        assertEquals(List.of(), list.toList());
    }

    @Test
    void toListIsReadOnly() {
        assertThrows(UnsupportedOperationException.class, () -> list.toList().add(1));
    }
}