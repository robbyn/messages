package org.tastefuljava.messages.xml;

import java.util.ArrayList;
import java.util.List;
import org.tastefuljava.messages.expr.Expression;

public class SequenceBuilder {
    private final SequenceBuilder link;
    private final List<Expression> sequence = new ArrayList<>();

    public SequenceBuilder(SequenceBuilder link) {
        this.link = link;
    }

    public SequenceBuilder getLink() {
        return link;
    }

    public void add(Expression expr) {
        sequence.add(expr);
    }

    public void addText(String text) {
        sequence.add((c) -> text);
    }

    public Expression toExpression() {
        return toExpression(sequence.toArray(new Expression[sequence.size()]));
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
