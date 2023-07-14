package org.tastefuljava.messages.type;

public interface Type {
    default public boolean isAssignableFrom(Type other) {
        return equals(other);
    }
    default public boolean isSameAs(Type other) {
        return this.equals(other);
    }
}
