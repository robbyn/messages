package org.tastefuljava.messages.xml;

import java.util.Arrays;
import org.tastefuljava.messages.expr.EvaluationContext;
import org.tastefuljava.messages.expr.Expression;

public class ListBuilder {
    private final ListBuilder link;
    private final Expression expr;
    private final String var;
    private final String sep;

    public ListBuilder(Expression expr, String var, String sep,
            ListBuilder link) {
        this.expr = expr;
        this.var = var;
        this.sep = sep;
        this.link = link;
    }

    public ListBuilder getLink() {
        return link;
    }

    public Expression toExpression(Expression text) {
        return listExpr(expr, var, sep, text);
    }

    private static Expression listExpr(Expression expr, String var, String sep,
            Expression text) {
        return (c) -> {
            Object o = expr.evaluate(c);
            StringBuilder buf = new StringBuilder();
            if (o != null) {
                Class<?> type = o.getClass();
                if (type.isArray()) {
                    o = Arrays.asList((Object[])o);
                }
                if (o instanceof Iterable) {
                    for (Object e: ((Iterable)o)) {
                        if (buf.length() > 0) {
                            buf.append(sep);
                        }
                        eval(c, text, e, buf);
                    }
                } else {
                    eval(c, text, o, buf);
                }
            }
            return buf.toString();
        };
    }

    private static void eval(EvaluationContext c, Expression expr, Object e,
            StringBuilder buf) {
        c = new EvaluationContext(c);
        c.add(e);
        Object o = expr.evaluate(c);
        if (o != null) {
            buf.append(o);
        }
    }
}
