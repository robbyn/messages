package org.tastefuljava.messages.type;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class ClassType implements Type {
    private static final Map<String,ClassType> CACHE = new WeakHashMap<>();

    private final Class<?> clazz;

    public static ClassType fromName(String name) {
        ClassType type = findClass(name);
        if (type == null) {
            type = findClass("java.lang." + name);
            if (type == null) {
                throw new IllegalArgumentException(
                        "Class not found: " + name);
            }
        }
        return type;
    }

    public ClassType(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.clazz);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassType other = (ClassType) obj;
        if (!Objects.equals(this.clazz, other.clazz)) {
            return false;
        }
        return true;
    }

    private static ClassType findClass(String name) {
        ClassType type = CACHE.get(name);
        if (type == null) {
            try {
                type = new ClassType(Class.forName(name));
                CACHE.put(name, type);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        return type;
    }
}
