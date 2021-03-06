package org.tastefuljava.messages.type;

import java.util.HashMap;
import java.util.Map;

public class GenericContext {
    private final Map<String,TypeVariable> vars = new HashMap<>();
    private int lastIndex;

    public TypeVariable get(String name) {
        return vars.get(name);
    }

    public TypeVariable makeVariable(Bound... bounds) {
        return new TypeVariable(lastIndex++, bounds);
    }

    public void define(String name, TypeVariable var) {
        vars.put(name, var);
    }
}
