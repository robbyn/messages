package org.tastefuljava.messages.type;

public interface Type {
    default public boolean isAssignableFrom(Type other) {
        return equals(other);
    }
}
