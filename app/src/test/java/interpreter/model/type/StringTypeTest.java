package interpreter.model.type;

import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringTypeTest {
    @Test
    void defaultValueHasStringType() {
        assertEquals(new StringType(), new StringType().defaultValue().getType());
    }

    @Test
    void defaultValueIsEmptyString() {
        assertEquals(new StringValue(""), new StringType().defaultValue());
    }

    @Test
    void equalStringsAreEqual() {
        assertTrue(new StringValue("equal").equals(new StringValue("equal")));
    }

    @Test
    void notEqualStringsAreNotEqual() {
        assertFalse(new StringValue("notEqual").equals(new StringValue("equal")));
    }

    @Test
    void equalStringsHaveEqualHashCode() {
        assertEquals(new StringValue("equal").hashCode(), new StringValue("equal").hashCode());
    }

    @Test
    void notEqualStringsHaveDiffHashCode() {
        assertNotEquals(new StringValue("notEqual").hashCode(), new StringValue("equal").hashCode());
    }
}