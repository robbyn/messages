package org.tastefuljava.messages.expr;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tastefuljava.messages.type.ClassType;
import org.tastefuljava.messages.type.PrimitiveType;

public class TypeTest {
    private Compiler comp;

    @BeforeEach
    public void setUp() {
        comp = new Compiler();
    }

    @AfterEach
    public void tearDown() throws IOException {
        comp = null;
    }

    @Test
    public void testPrimitive() throws IOException {
        assertEquals(PrimitiveType.BOOLEAN, comp.parseType("boolean"));
        assertEquals(PrimitiveType.CHAR, comp.parseType("char"));
        assertEquals(PrimitiveType.BYTE, comp.parseType("byte"));
        assertEquals(PrimitiveType.SHORT, comp.parseType("short"));
        assertEquals(PrimitiveType.INT, comp.parseType("int"));
        assertEquals(PrimitiveType.LONG, comp.parseType("long"));
        assertEquals(PrimitiveType.FLOAT, comp.parseType("float"));
        assertEquals(PrimitiveType.DOUBLE, comp.parseType("double"));
    }

    @Test
    public void testClasses() throws IOException {
        assertEquals(new ClassType(Float.class),
                comp.parseType("java.lang.Float"));
        assertEquals(new ClassType(Float.class),
                comp.parseType("Float"));
        assertSame(comp.parseType("java.lang.Float"),
                comp.parseType("Float"));
        assertNotSame(new ClassType(Float.class),
                comp.parseType("Float"));
    }
}
