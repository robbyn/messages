package org.tastefuljava.messages.expr;

import org.tastefuljava.messages.expr.Expression;
import org.tastefuljava.messages.expr.EvaluationContext;
import org.tastefuljava.messages.expr.CompilationContext;
import org.tastefuljava.messages.expr.ExpressionCompiler;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArithmeticTest {
    private CompilationContext cxt;
    private EvaluationContext eval;
    private ExpressionCompiler comp;

    @BeforeEach
    public void setUp() {
        comp = new ExpressionCompiler();
    }

    @AfterEach
    public void tearDown() throws IOException {
        comp = null;
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

    private Object eval(String s) throws IOException {
        Expression e = comp.compile(cxt, s);
        Object v = e.evaluate(eval);
        return v;
    }
}
