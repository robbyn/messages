package org.tastefuljava.messages.expr;

import java.util.ArrayList;
import java.util.List;

public class EvaluationContext {
    private final List<Object> locals = new ArrayList<>();
    private final EvaluationContext link;
    private final int level;

    public EvaluationContext(EvaluationContext link) {
        this(link, link == null ? 0 : link.level+1);
    }

    public EvaluationContext(int level) {
        this(null, level);
    }

    private EvaluationContext(EvaluationContext link, int level) {
        this.link = link;
        this.level = level;
    }

    public Object get(int level, int addr) {
        return getScope(level).get(addr);
    }

    public int add(Object value) {
        List<Object> scope =  getScope(level);
        int result = scope.size();
        scope.add(value);
        return result;
    }

    private List<Object> getScope(int level) {
        EvaluationContext cxt = this;
        while (level < cxt.level) {
            cxt = cxt.link;
        }
        return cxt.locals;
    }
}
