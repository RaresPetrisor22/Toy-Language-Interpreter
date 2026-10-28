package interpreter.model.type;

import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntTypeTest {
    @Test
    void defaultValueIsZero() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
    }

    @Test
    void defaultValueHasIntType() {
        assertEquals(new IntType(), new IntType().defaultValue().getType());
    }
}