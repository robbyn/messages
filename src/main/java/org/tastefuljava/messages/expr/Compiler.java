package org.tastefuljava.messages.expr;

import org.tastefuljava.messages.expr.impl.ExpressionParser;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.tastefuljava.messages.type.Type;

public class Compiler {
    public Expression compile(CompilationContext cxt, String s)
            throws IOException {
        try (Reader reader = new StringReader(s)) {
            AbstractParser parser = new ExpressionParser(reader);
            parser.setContext(cxt);
            return parser.parseExpression();
        }
    }

    public Type parseType(String s) throws IOException {
        try (Reader reader = new StringReader(s)) {
            AbstractParser parser = new ExpressionParser(reader);
            return parser.parseType();
        }
    }
}