package interpreter.model.value;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefValueTest {
    @Test
    void typeIsRefOfLocationType() {
        assertEquals(new RefType(new IntType()), new RefValue(1, new IntType()).getType());
    }

    @Test
    void typeOfReferenceToReference() {
        var value = new RefValue(10, new RefType(new IntType()));
        assertEquals(new RefType(new RefType(new IntType())), value.getType());
    }

    @Test
    void toStringShowsAddressAndLocationType() {
        assertEquals("(2, Ref(int))", new RefValue(2, new RefType(new IntType())).toString());
    }
}
