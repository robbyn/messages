package org.tastefuljava.messages.expr;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArithmeticTest {
    private CompilationContext cxt;
    private EvaluationContext eval;
    private Compiler comp;

    @BeforeEach
    public void setUp() {
        cxt = new StandardContext();
        comp = new Compiler();
        eval = new EvaluationContext(0);
        TestData test = new TestData();
        test.setByteVal((byte)-4);
        test.setCharVal((char)4);
        test.setDoubleVal(4);
        test.setFloatVal(4);
        test.setIntVal(4);
        test.setLongVal(4);
        test.setShortVal((short)4);
        test.setStringVal("Hello world!!!");
        cxt.addVariable("test");
        eval.add(test);
    }

    @AfterEach
    public void tearDown() throws IOException {
        eval = null;
        comp = null;
        cxt = null;
    }

    @Test
    public void testInts() throws IOException {
        assertEquals(1, eval("1"));
        assertEquals(-1, eval("-1"));
        assertEquals(-1+2, eval("-1+2"));
        assertEquals(-3*2, eval("-3*2"));
        assertEquals(-3/2, eval("-3/2"));
        assertEquals(-3*(2+2), eval("-3*(2+2)"));
    }

    @Test
    public void testFloats() throws IOException {
        assertNotEquals(1., eval("1"));
        assertEquals(1., eval("1.0"));
        assertEquals(-3+(2*6.)/(1-1), eval("-3+(2*6.)/(1-1)"));
    }

    @Test
    public void testStrings() throws IOException {
        assertEquals("abc\r\n", eval("\"abc\\r\\n\""));
        assertEquals("abc\r\n", eval("\"abc\"+\"\\r\\n\""));
    }

    @Test
    public void testVars() throws IOException {
        assertEquals((byte)-4, eval("test.byteVal"));
        assertEquals((char)4, eval("test.charVal"));
        assertEquals(4.0, eval("test.doubleVal"));
        assertEquals(4.0f, eval("test.floatVal"));
        assertEquals(4, eval("test.intVal"));
        assertEquals(4l, eval("test.longVal"));
        assertEquals((short)4, eval("test.shortVal"));
        assertEquals("Hello world!!!", eval("test.stringVal"));
        assertEquals(true, eval("true"));
        assertEquals(false, eval("false"));

        assertTrue((boolean)eval("test.byteVal==-4"));
        assertTrue((boolean)eval("test.charVal==4"));
        assertTrue((boolean)eval("test.charVal>3"));
        assertTrue((boolean)eval("test.charVal<=5"));
        assertTrue((boolean)eval("test.doubleVal==4"));
        assertTrue((boolean)eval("test.floatVal==4"));
        assertTrue((boolean)eval("test.intVal==4"));
        assertTrue((boolean)eval("test.longVal==4"));
        assertTrue((boolean)eval("test.shortVal==4"));
        assertTrue((boolean)eval("test.stringVal==\"Hello world!!!\""));
        assertTrue((boolean)eval("test.charVal==test.getCharVal()"));
        assertTrue((boolean)eval("(test.charVal==test.getCharVal())==true"));
        assertTrue((boolean)eval("(test.charVal==test.getCharVal())!=false"));
        assertTrue((boolean)eval("true!=false"));
    }

    @Test
    public void testCollections() throws IOException {
        int[] intArray = {1, 2, 3, 4};
        Integer[] integerArray = {1, 2, 3, 4};
        List<Integer> intList = Arrays.asList(integerArray);
        Map<String,Integer> intMap = new HashMap<>();
        intMap.put("one", 1);
        intMap.put("two", 2);
        intMap.put("three", 3);
        intMap.put("four", 4);
        assertEquals(2, evaln("a[b-1]", intArray, 2));
        assertEquals(3, evaln("a[b-1]", integerArray, 3));
        assertEquals(4, evaln("a[b-1]", intList, 4));
        assertEquals(1, evaln("a[b]", intMap, "one"));
    }

    @Test
    public void testBooleans() throws IOException {
        assertEquals(true, evaln("a", true));
        assertEquals(false, evaln("a", false));
        assertEquals(false, evaln("!a", true));
        assertEquals(true, evaln("!a", false));
        assertEquals(false, evaln("not a", true));
        assertEquals(true, evaln("not a", false));

        assertEquals(false, evaln("a && b", false, false));
        assertEquals(false, evaln("a && b", false, true));
        assertEquals(true, evaln("a && b", true, true));
        assertEquals(false, evaln("a && b", true, false));

        assertEquals(false, evaln("a || b", false, false));
        assertEquals(true, evaln("a || b", false, true));
        assertEquals(true, evaln("a || b", true, true));
        assertEquals(true, evaln("a || b", true, false));
    }

    private Object eval(String s) throws IOException {
        Expression e = comp.compile(cxt, s);
        Object v = e.evaluate(eval);
        return v;
    }

    private Object evaln(String s, Object...parms) throws IOException {
        CompilationContext cc = new CompilationContext(this.cxt);
        char c = 'a';
        for (int i = 0; i < parms.length; ++i) {
            cc.addVariable(Character.toString(c));
            ++c;
        }
        Expression e = comp.compile(cc, s);
        EvaluationContext ec = new EvaluationContext(this.eval);
        for (Object p: parms) {
            ec.add(p);
        }
        Object r = e.evaluate(ec);
        return r;
    }
}
