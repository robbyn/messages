package org.tastefuljava.messages.type;

public interface Type {
    default public boolean matches(Type other) {
        return equals(other);
    }
}
