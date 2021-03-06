package org.tastefuljava.messages.expr;

import java.io.IOException;
import static junit.framework.Assert.assertFalse;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tastefuljava.messages.type.ArrayType;
import org.tastefuljava.messages.type.ClassType;
import org.tastefuljava.messages.type.GenericContext;
import static org.tastefuljava.messages.type.ParameterizedType.collection;
import static org.tastefuljava.messages.type.ParameterizedType.list;
import org.tastefuljava.messages.type.PrimitiveType;
import org.tastefuljava.messages.type.Type;

public class TypeTest {
    private GenericContext gc;
    private Compiler comp;

    @BeforeEach
    public void setUp() {
        gc = new GenericContext(null);
        comp = new Compiler();
    }

    @AfterEach
    public void tearDown() throws IOException {
        comp = null;
        gc = null;
    }

    @Test
    public void testPrimitive() throws IOException {
        assertEquals(PrimitiveType.BOOLEAN, parseType("boolean"));
        assertEquals(PrimitiveType.CHAR, parseType("char"));
        assertEquals(PrimitiveType.BYTE, parseType("byte"));
        assertEquals(PrimitiveType.SHORT, parseType("short"));
        assertEquals(PrimitiveType.INT, parseType("int"));
        assertEquals(PrimitiveType.LONG, parseType("long"));
        assertEquals(PrimitiveType.FLOAT, parseType("float"));
        assertEquals(PrimitiveType.DOUBLE, parseType("double"));
    }

    @Test
    public void testClasses() throws IOException {
        assertEquals(new ClassType(Float.class),
                parseType("java.lang.Float"));
        assertEquals(new ClassType(Float.class),
                parseType("Float"));
        assertSame(parseType("java.lang.Float"),
                parseType("Float"));
        assertNotSame(new ClassType(Float.class),
                parseType("Float"));
    }

    @Test
    public void testArrays() throws IOException {
        assertEquals(new ArrayType(new ArrayType(PrimitiveType.INT)),
                parseType("int[][]"));
    }

    @Test
    public void testCollections() throws IOException {
        assertEquals(collection(new ArrayType(PrimitiveType.INT)),
                parseType("Collection<int[]>"));
        assertEquals(new ArrayType(list(PrimitiveType.INT)),
                parseType("List<int>[]"));
    }

    @Test
    public void testMatches() throws IOException {
        assertMatches("Map<?,?>", "Map<String,List<String>>");
        assertNotMatches("Map<?,List<?>>", "Map<String,List<String>>");
        assertNotMatches("Map<?,List<Integer>>", "Map<String,List<String>>");
    }

    private void assertMatches(String a, String b) throws IOException {
        Type ta = parseType(a);
        Type tb = parseType(b);
        assertTrue(ta.isAssignableFrom(tb));
    }

    private void assertNotMatches(String a, String b) throws IOException {
        Type ta = parseType(a);
        Type tb = parseType(b);
        assertFalse(ta.isAssignableFrom(tb));
    }

    private Type parseType(String a) throws IOException {
        return comp.parseType(gc, a);
    }
}
