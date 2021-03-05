package org.tastefuljava.messages.type;

import java.util.Arrays;
import java.util.Objects;

public class ParameterizedType implements Type {
    private final ClassType baseClass;
    private final Type[] actualArgs;

    public static ParameterizedType collection(Type t) {
        return create("Collection", t);
    }

    public static ParameterizedType list(Type t) {
        return create("List", t);
    }

    public static ParameterizedType set(Type t) {
        return create("Set", t);
    }

    public static ParameterizedType sortedSet(Type t) {
        return create("SortedSet", t);
    }

    public static ParameterizedType map(Type k, Type v) {
        return create("Map", k, v);
    }

    public static ParameterizedType sortedMap(Type k, Type v) {
        return create("SortedMap", k, v);
    }

    public static ParameterizedType create(
            String className, Type... args) {
        ClassType ct = ClassType.fromName(className);
        return new ParameterizedType(ct, args);
    }

    public ParameterizedType(ClassType baseClass, Type... actualArgs) {
        this.baseClass = baseClass;
        this.actualArgs = actualArgs;
    }

    @Override
    public boolean matches(Type type) {
        if (this == type) {
            return true;
        }
        if (type == null) {
            return false;
        }
        if (getClass() != type.getClass()) {
            return false;
        }
        final ParameterizedType other = (ParameterizedType) type;
        if (!Objects.equals(this.baseClass, other.baseClass)) {
            return false;
        }
        if (actualArgs.length != other.actualArgs.length) {
            return false;
        }
        for (int i = 0; i < actualArgs.length; ++i) {
            if (!actualArgs[i].matches(other.actualArgs[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.baseClass);
        hash = 53 * hash + Arrays.deepHashCode(this.actualArgs);
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
        final ParameterizedType other = (ParameterizedType) obj;
        if (!Objects.equals(this.baseClass, other.baseClass)) {
            return false;
        }
        if (!Arrays.deepEquals(this.actualArgs, other.actualArgs)) {
            return false;
        }
        return true;
    }
}
