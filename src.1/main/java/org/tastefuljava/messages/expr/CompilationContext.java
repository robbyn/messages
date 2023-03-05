package org.tastefuljava.messages.expr;

import java.util.HashMap;
import java.util.Map;

public class CompilationContext {
    private final Map<String,Expression> scope = new HashMap<>();
    private final CompilationContext link;
    private final int level;
    private int varCount;

    public CompilationContext(CompilationContext link) {
        this.link = link;
        this.level = link == null ? 0 : link.level+1;
    }

    public CompilationContext() {
        this(null);
    }

    public int getLevel() {
        return level;
    }

    public CompilationContext getLink() {
        return link;
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

    public void defineConst(String name, Object value) {
        scope.put(name, (c) -> value);
    }

    public int addVariable(String name) {
        int addr = varCount++;
        define(name, varExpression(level, addr));
        return addr;
    }

    private static Expression varExpression(int level, int addr) {
        return (c) -> c.get(level, addr);
    }
}
