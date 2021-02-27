package org.tastefuljava.messages.xml;

import java.util.ArrayList;
import java.util.List;
import org.tastefuljava.messages.expr.Expression;

public class Sequence {
    private final Sequence link;
    private final List<Expression> expressions = new ArrayList<>();

    public Sequence(Sequence link) {
        this.link = link;
    }

    public Sequence getLink() {
        return link;
    }

    public void add(Expression expr) {
        expressions.add(expr);
    }

    public void addText(String text) {
        expressions.add((c) -> text);
    }

    public Expression toExpression() {
        return toExpression(expressions.toArray(
                new Expression[expressions.size()]));
    }

    private static Expression toExpression(final Expression[] list) {
        return (c) -> {
            StringBuilder buf = new StringBuilder();
            for (Expression e: list) {
                if (e != null) {
                    Object o = e.evaluate(c);
                    if (o != null) {
                        buf.append(o);
                    }
                }
            }
            return buf.toString();
        };
    }
}
