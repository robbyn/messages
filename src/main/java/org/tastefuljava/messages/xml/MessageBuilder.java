package org.tastefuljava.messages.xml;

import java.util.ArrayList;
import java.util.List;
import org.tastefuljava.messages.expr.Expression;

public class MessageBuilder {
    private final String name;
    private int paramCount;
    private Expression text;

    public MessageBuilder(String name) {
        this.name = name;
    }

    public MessageBuilder setParamCount(int paramCount) {
        this.paramCount = paramCount;
        return this;
    }

    public MessageBuilder setText(Expression text) {
        this.text = text;
        return this;
    }

    public Message build() {
        return new Message(name, paramCount, text);
    }
}
