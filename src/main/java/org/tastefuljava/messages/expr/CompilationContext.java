package org.tastefuljava.messages.expr;

import java.util.HashMap;
import java.util.Map;

public class CompilationContext {
    private final Map<String,Expression> scope = new HashMap<>();
    private final CompilationContext link;
    private int varCount;

    public CompilationContext(CompilationContext link) {
        this.link = link;
    }

    public CompilationContext() {
        this(null);
    }

    public int level() {
        return link == null ? 0 : link.level()+1;
    }

    public Expression resolve(String name) {
        Expression result = scope.get(name);
        if (result != null) {
            return result;
        }
        return link == null ? null : link.resolve(name);
    }

    public void define(String name, Expression value) {
        scope.put(name, value);
    }

    public Expression addVariable(String name) {
        int level = level();
        int addr = varCount++;
        Expression expr = (c) -> {
            return c.get(level, addr);
        };
        define(name, expr);
        return expr;
    }
}
