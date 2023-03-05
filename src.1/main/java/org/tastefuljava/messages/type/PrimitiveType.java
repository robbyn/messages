package org.tastefuljava.messages.type;

import java.util.HashMap;
import java.util.Map;

public enum PrimitiveType implements Type {
    BOOLEAN(boolean.class, Boolean.class),
    CHAR(char.class, Character.class),
    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INT(int.class, Integer.class),
    LONG(long.class, Long.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class);

    private static final Map<Class<?>,PrimitiveType> TYPE_MAP = new HashMap<>();

    private final Class<?> type;
    private final Class<?> boxed;

    public static PrimitiveType fromClass(Class<?> clazz) {
        return TYPE_MAP.get(clazz);
    }

    private PrimitiveType(Class<?> type, Class<?> boxed) {
        this.type = type;
        this.boxed = boxed;
    }

    static {
        for (PrimitiveType pt: values()) {
            TYPE_MAP.put(pt.type, pt);
            TYPE_MAP.put(pt.boxed, pt);
        }
    }
}
