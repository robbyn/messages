package org.tastefuljava.messages.expr;

import java.io.IOException;
import java.util.List;
import static junit.framework.Assert.assertFalse;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tastefuljava.messages.type.ArrayType;
import org.tastefuljava.messages.type.ClassType;
import org.tastefuljava.messages.type.GenericContext;
import org.tastefuljava.messages.type.ParameterizedType;
import static org.tastefuljava.messages.type.ParameterizedType.collection;
import static org.tastefuljava.messages.type.ParameterizedType.list;
import org.tastefuljava.messages.type.PrimitiveType;
import org.tastefuljava.messages.type.Type;
import org.tastefuljava.messages.type.TypeVariable;

public class TypeTest {
    private CompilationContext cc;
    private GenericContext gc;
    private Compiler comp;

    @BeforeEach
    public void setUp() {
        cc = new CompilationContext(null);
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

    @Test
    public void testGenerics() throws IOException {
        int count = comp.parseGenerics(cc, gc, "T,R");
        assertEquals(2, count);
        TypeVariable t = gc.get("T");
        assertNotNull(t);
        TypeVariable r = gc.get("R");
        assertNotNull(r);
        assertNotSame(t, r);
        List<Type> parms = comp.parseParams(
                cc, gc, "List<T> a, List<T> b, List<R> c");
        assertEquals(3, parms.size());
        Type a = parms.get(0);
        Type b = parms.get(1);
        Type c = parms.get(2);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(b, c);
        assertTrue(a instanceof ParameterizedType);
        assertSame(t, ((ParameterizedType)a).getArg(0));
        assertTrue(b instanceof ParameterizedType);
        assertSame(t, ((ParameterizedType)b).getArg(0));
        assertTrue(c instanceof ParameterizedType);
        assertSame(r, ((ParameterizedType)c).getArg(0));
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
