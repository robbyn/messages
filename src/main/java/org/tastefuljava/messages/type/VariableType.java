package org.tastefuljava.messages.type;

public class VariableType implements Type {
    private final Bound[] bounds;

    public VariableType(Bound...bounds) {
        this.bounds = bounds;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        for (Bound bound: bounds) {
            if (!bound.test(other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSameThan(Type other) {
        for (Bound bound: bounds) {
            if (!bound.test(other)) {
                return false;
            }
        }
        return true;
    }
}
