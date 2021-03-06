package org.tastefuljava.messages.type;

public enum UnknownType implements Type {
    INSTANCE;

    @Override
    public boolean isAssignableFrom(Type other) {
        return true;
    }
}
