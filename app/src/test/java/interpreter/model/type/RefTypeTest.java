package interpreter.model.type;

import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefTypeTest {
    @Test
    void defaultValueIsNullReferenceToInnerType() {
        assertEquals(new RefValue(0, new IntType()), new RefType(new IntType()).defaultValue());
    }

    @Test
    void defaultValueHasTheRefType() {
        var type = new RefType(new RefType(new BoolType()));
        assertEquals(type, type.defaultValue().getType());
    }

    @Test
    void nestedRefTypesAreEqual() {
        assertEquals(new RefType(new RefType(new IntType())), new RefType(new RefType(new IntType())));
    }

    @Test
    void nestedRefTypeDiffersFromSingleRef() {
        assertNotEquals(new RefType(new RefType(new IntType())), new RefType(new IntType()));
    }

    @Test
    void refTypesWithDifferentInnerTypesDiffer() {
        assertNotEquals(new RefType(new IntType()), new RefType(new BoolType()));
    }

    @Test
    void toStringShowsNesting() {
        assertEquals("Ref(Ref(int))", new RefType(new RefType(new IntType())).toString());
    }
}
