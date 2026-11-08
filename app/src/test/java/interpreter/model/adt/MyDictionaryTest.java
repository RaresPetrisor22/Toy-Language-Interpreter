package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putThenLookupReturnsValue() throws ADTException {
        dict.put("a", 1);
        assertTrue(dict.isDefined("a"));
        assertEquals(1, dict.lookup("a"));
    }

    @Test
    void missingKeyIsNotDefined() {
        assertFalse(dict.isDefined("a"));
    }

    @Test
    void updateChangesExistingValue() throws ADTException {
        dict.put("a", 1);
        dict.update("a", 5);
        assertEquals(5, dict.lookup("a"));
    }

    @Test
    void lookupOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.lookup("x"));
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("x", 1));
    }

    @Test
    void removeDeletesTheKey() throws ADTException {
        dict.put("a", 1);
        dict.put("b", 2);
        dict.remove("a");
        assertFalse(dict.isDefined("a"));
        assertTrue(dict.isDefined("b"));
    }

    @Test
    void removeOfMissingKeyThrows() {
        var ex = assertThrows(ADTException.class, () -> dict.remove("x"));
        assertEquals("key x is not defined", ex.getMessage());
    }

    @Test
    void toMapContainsAllEntries() {
        dict.put("a", 1);
        dict.put("b", 2);
        assertEquals(Map.of("a", 1, "b", 2), dict.toMap());
    }

    @Test
    void toMapIsReadOnly() {
        dict.put("a", 1);
        assertThrows(UnsupportedOperationException.class, () -> dict.toMap().put("b", 2));
    }

    @Test
    void toMapIsACopy() {
        dict.put("a", 1);
        Map<String, Integer> snapshot = dict.toMap();
        dict.put("b", 2);
        assertEquals(Map.of("a", 1), snapshot);
    }
}