package org.tastefuljava.messages.type;

public enum PrimitiveType implements Type {
    BOOLEAN(boolean.class),
    CHAR(char.class),
    BYTE(byte.class),
    SHORT(short.class),
    INT(int.class),
    LONG(long.class),
    FLOAT(float.class),
    DOUBLE(double.class);

    private final Class<?> type;

    private PrimitiveType(Class<?> type) {
        this.type = type;
    }
}
