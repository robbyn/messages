package org.tastefuljava.messages.xml;

import org.tastefuljava.messages.expr.Expression;
import org.tastefuljava.messages.util.Converter;

public class SelectBuilder {
    private final SelectBuilder link;
    private When lastWhen = null;
    private Expression whenCondition;
    private Expression otherwise;

    public SelectBuilder(SelectBuilder link) {
        this.link = link;
    }

    public SelectBuilder getLink() {
        return link;
    }

    public void startWhen(Expression condition) {
        whenCondition = condition;
    }

    public void endWhen(Expression text) {
        lastWhen = new When(whenCondition, text, lastWhen);
    }

    public void othewise(Expression otherwise) {
        this.otherwise = otherwise;
    }

    public Expression toExpression() {
        Expression expr = otherwise == null ? blank() : otherwise;
        for (When when = lastWhen; when != null; when = when.link) {
            expr = conditional(when.condition, when.text, expr);
        }
        return expr;
    }

    private static Expression blank() {
        return (c) -> "";
    }

    private static Expression conditional(Expression condition,
            Expression ifTrue, Expression ifFalse) {
        return (c) -> {
            Object cond = condition.evaluate(c);
            Boolean r = Converter.INSTANCE.convert(cond, Boolean.class);
            if (r != null && r) {
                return ifTrue.evaluate(c);
            } else {
                return ifFalse.evaluate(c);
            }
        };
    }

    private static class When {
        private final Expression condition;
        private final Expression text;
        private final When link;

        private When(Expression condition, Expression text, When link) {
            this.condition = condition;
            this.text = text;
            this.link = link;
        }
    }
}
