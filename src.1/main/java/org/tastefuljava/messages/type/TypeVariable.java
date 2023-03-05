package org.tastefuljava.messages.type;

public class TypeVariable implements Type {
    private final int index;
    private final Bound[] bounds;

    TypeVariable(int index, Bound...bounds) {
        this.index = index;
        this.bounds = bounds;
    }

    public int getIndex() {
        return index;
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
