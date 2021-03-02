package org.tastefuljava.messages.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.tastefuljava.messages.expr.EvaluationContext;
import org.tastefuljava.messages.expr.Expression;

public class Message extends Described {
    private final String name;
    private final String[] parameters;
    private Expression text;

    public Message(String name, String[] parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters.clone();
    }

    public void setText(Expression expr) {
        text = expr;
    }

    public String format(Object... parms) {
        int expPar = parameters.length;
        if (expPar != parms.length) {
            throw new IllegalArgumentException(
                    "Mauvais nombre de paramètres: attendu " + expPar
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
