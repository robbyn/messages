package org.tastefuljava.messages.xml;

import java.util.ArrayList;
import java.util.List;
import org.tastefuljava.messages.expr.Expression;

public class MessageBuilder {
    private final String name;
    private final List<String> parameters = new ArrayList<>();
    private Expression text;

    public MessageBuilder(String name) {
        this.name = name;
    }

    public MessageBuilder addParam(String name) {
        parameters.add(name);
        return this;
    }

    public MessageBuilder addParams(String... names) {
        for (String name: names) {
            parameters.add(name);
        }
        return this;
    }

    public MessageBuilder setText(Expression text) {
        this.text = text;
        return this;
    }

    public Message build() {
        String[] parms = parameters.toArray(new String[parameters.size()]);
        return new Message(name, parms, text);
    }
}
