package org.tastefuljava.messages.type;

public enum UnknownType implements Type {
    INSTANCE;

    @Override
    public boolean matches(Type other) {
        return true;
    }
}
