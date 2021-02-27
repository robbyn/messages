package org.tastefuljava.messages.expr;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArithmeticTest {
    private CompilationContext cxt;
    private EvaluationContext eval;
    private ExpressionCompiler comp;

    @BeforeEach
    public void setUp() {
        cxt = new StandardContext();
        comp = new ExpressionCompiler();
        eval = new EvaluationContext();
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
        eval.add(0, test);
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

    private Object eval(String s) throws IOException {
        Expression e = comp.compile(cxt, s);
        Object v = e.evaluate(eval);
        return v;
    }

}
