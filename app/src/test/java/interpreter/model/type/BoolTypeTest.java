package interpreter.model.type;

import interpreter.model.value.BoolValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoolTypeTest {
    @Test
    void defaultValueIsFalse() {
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void defaultValueHasBoolType() {
        assertEquals(new BoolType(), new BoolType().defaultValue().getType());
    }
}