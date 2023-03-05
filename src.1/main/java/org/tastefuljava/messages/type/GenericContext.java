package org.tastefuljava.messages.type;

import java.util.HashMap;
import java.util.Map;

public class GenericContext {
    private final GenericContext link;
    private final Map<String,TypeVariable> vars = new HashMap<>();
    private int lastIndex;

    public GenericContext(GenericContext link) {
        this.link = link;
    }

    public GenericContext getLink() {
        return link;
    }

    public TypeVariable get(String name) {
        TypeVariable v = vars.get(name);
        if (v == null && link != null) {
            v = link.get(name);
        }
        return v;
    }

    public TypeVariable makeVariable(Bound... bounds) {
        return new TypeVariable(lastIndex++, bounds);
    }

    public void define(String name, TypeVariable var) {
        vars.put(name, var);
    }
}
