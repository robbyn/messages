package org.tastefuljava.messages.xml;

import java.util.HashMap;
import java.util.Map;
import org.tastefuljava.messages.expr.EvaluationContext;
import org.tastefuljava.messages.expr.Expression;

public class Message {
    private final String name;
    private final String[] parameters;
    private String description;
    private final Map<String,Expression> texts = new HashMap<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setText(String language, Expression expr) {
        texts.put(language, expr);
    }

    public String format(String language, String def, Object... parms) {
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
        Expression text = null;
        if (language != null) {
            text = texts.get(language);
        }
        if (text == null) {
            text = texts.get(def);
        }
        if (text == null) {
            return null;
        }
        Object result = text.evaluate(cxt);
        return result == null ? "" : result.toString();
    }
}
