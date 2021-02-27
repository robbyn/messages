package org.tastefuljava.messages.expr;

import org.tastefuljava.messages.expr.impl.ExpressionParser;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ExpressionCompiler {
    public Expression compile(CompilationContext cxt, String s)
            throws IOException {
        try (Reader reader = new StringReader(s)) {
            AbstractParser parser = new ExpressionParser(new StringReader(s));
            parser.setContext(cxt);
            return parser.parse();
        }
    }
}
