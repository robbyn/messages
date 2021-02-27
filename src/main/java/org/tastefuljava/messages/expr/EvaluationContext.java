package org.tastefuljava.messages.expr;

import java.util.ArrayList;
import java.util.List;

public class EvaluationContext {
    private final List<Object> locals = new ArrayList<>();
    private final EvaluationContext link;
    private final int level;

    public EvaluationContext(EvaluationContext link) {
        this.link = link;
        this.level = link == null ? 0 : link.level+1;
    }

    public EvaluationContext() {
        this(null);
    }

    public Object get(int level, int addr) {
        return getScope(level).get(addr);
    }

    public void set(int level, int addr, Object value) {
        getScope(level).set(addr, value);
    }

    public int add(int level, Object value) {
        List<Object> scope =  getScope(level);
        int result = scope.size();
        scope.add(value);
        return result;
    }

    private List<Object> getScope(int level) {
        EvaluationContext cxt = this;
        int delta = this.level-level;
        while (delta > 0) {
            --delta;
            cxt = cxt.link;
        }
        return cxt.locals;
    }
}
