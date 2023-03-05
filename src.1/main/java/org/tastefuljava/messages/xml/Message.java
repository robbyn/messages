package org.tastefuljava.messages.xml;

import org.tastefuljava.messages.expr.EvaluationContext;
import org.tastefuljava.messages.expr.Expression;

public class Message {
    private final String name;
    private final int paramCount;
    private final Expression text;

    public Message(String name, int paramCount, Expression text) {
        this.name = name;
        this.paramCount = paramCount;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String format(Object... parms) {
        if (parms.length != paramCount) {
            throw new IllegalArgumentException(
                    "Mauvais nombre de paramètres: attendu " + paramCount
                            + " reçu: " + parms.length);
        }
        EvaluationContext cxt = new EvaluationContext(2);
        for (int i = 0; i < parms.length; ++i) {
            cxt.add(parms[i]);
        }
        Object result = text.evaluate(cxt);
        return result == null ? "" : result.toString();
    }
}
