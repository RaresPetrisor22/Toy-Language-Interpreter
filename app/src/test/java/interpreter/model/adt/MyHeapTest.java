package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import interpreter.model.type.IntType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyHeapTest {
    private IHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MyHeap();
    }

    @Test
    void allocateReturnsConsecutiveAddressesStartingAtOne() {
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }

    @Test
    void getReturnsTheAllocatedValue() throws ADTException {
        int address = heap.allocate(new IntValue(20));
        assertEquals(new IntValue(20), heap.get(address));
    }

    @Test
    void addressZeroIsNeverDefined() {
        heap.allocate(new IntValue(20));
        assertFalse(heap.isDefined(0));
    }

    @Test
    void getOfMissingAddressThrows() {
        var ex = assertThrows(ADTException.class, () -> heap.get(1));
        assertEquals("address 1 is not in the heap", ex.getMessage());
    }

    @Test
    void updateChangesTheValue() throws ADTException {
        int address = heap.allocate(new IntValue(20));
        heap.update(address, new IntValue(30));
        assertEquals(new IntValue(30), heap.get(address));
    }

    @Test
    void updateOfMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.update(1, new IntValue(30)));
        assertFalse(heap.isDefined(1));
    }

    @Test
    void getContentIsAReadOnlyCopy() {
        heap.allocate(new IntValue(20));
        Map<Integer, IValue> snapshot = heap.getContent();
        heap.allocate(new IntValue(30));
        assertEquals(Map.of(1, new IntValue(20)), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.put(5, new IntValue(1)));
    }

    @Test
    void setContentReplacesTheLocations() {
        heap.allocate(new IntValue(20));
        heap.allocate(new IntValue(30));
        heap.setContent(Map.of(2, new IntValue(30)));
        assertEquals(Map.of(2, new IntValue(30)), heap.getContent());
    }

    @Test
    void addressesAreNotReusedAfterSetContent() {
        heap.allocate(new IntValue(20));
        heap.allocate(new IntValue(30));
        heap.setContent(Map.of());                    // everything collected
        assertEquals(3, heap.allocate(new IntValue(40)));
    }

    @Test
    void toStringIsSortedByAddress() {
        heap.allocate(new IntValue(20));
        heap.allocate(new RefValue(1, new IntType()));
        assertEquals("{1=20, 2=(1, int)}", heap.toString());
    }
}
