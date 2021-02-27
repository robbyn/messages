package org.tastefuljava.messages.expr;

import java.util.ArrayList;
import java.util.List;

public class EvaluationContext {
    private final List<List<Object>> display = new ArrayList<>();

    public int openScope() {
        int result = display.size();
        display.add(new ArrayList<>());
        return result;
    }

    public void closeScope() {
        display.remove(display.size()-1);
    }

    public Object get(int level, int addr) {
        return display.get(level).get(addr);
    }

    public void set(int level, int addr, Object value) {
        display.get(level).set(addr, value);
    }

    public int add(int level, Object value) {
        List<Object> scope =  display.get(level);
        int result = scope.size();
        scope.add(value);
        return result;
    }
}
