package org.tastefuljava.messages.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum Converter {
    INSTANCE;

    private static final Map<Class<?>,Map<Class<?>,Function<?,?>>> CONVERT
            = new HashMap<>();

    public boolean isConvertible(Object value, Class<?> toType) {
        if (value == null) {
            return !toType.isPrimitive();
        }
        Class<?> fromType = value.getClass();
        if (toType.isAssignableFrom(fromType)) {
            return true;
        }
        ConverterNode node = converterChain(fromType, toType);
        return node != null;
    }

    public <T> T convert(Object value, Class<T> toType) {
        if (value == null) {
            throw new NullPointerException(
                    "Cannot convert null to a primitive type.");
        }
        Class<?> fromType = value.getClass();
        if (toType.isAssignableFrom(fromType)) {
            return (T)value;
        }
        ConverterNode node = converterChain(fromType, toType);
        if (node == null) {
            throw new IllegalArgumentException(
                    "Type " + fromType.getName() + " is not convertible to "
                    + toType.getName());
        }
        while (node != null) {
            value = node.convert.apply(value);
            node = node.link;
        }
        return (T)value;
    }

    public static <T,U> void register(Class<T> fromType, Class<U> toType,
            Function<T,U> convert) {
        Map<Class<?>,Function<?,?>> map = CONVERT.get(toType);
        if (map == null) {
            map = new HashMap<>();
            CONVERT.put(toType, map);
        }
        map.put(fromType, convert);
    }

    private ConverterNode converterChain(Class<?> fromType, Class<?> toType) {
        List<ConverterNode> list = new ArrayList<>();
        int pos = 0;
        ConverterNode cur = null;
        while (!toType.isAssignableFrom(fromType)) {
            final ConverterNode cc = cur;
            Map<Class<?>,Function<?,?>> map = CONVERT.get(toType);
            if (map != null) {
                map.forEach((f,c) -> {
                    boolean found = false;
                    for (ConverterNode lc: list) {
                        found = lc.from.isAssignableFrom(f);
                        if (found) {
                            break;
                        }
                    }
                    if (!found) {
                        ConverterNode lc = new ConverterNode(f, c, cc);
                        list.add(lc);
                    }
                });
            }
            if (pos >= list.size()) {
                return null;
            }
            cur = list.get(pos++);
            toType = cur.from;
        }
        return cur;
    }

    private static class ConverterNode {
        private final Class<?> from;
        private final Function<Object,Object> convert;
        private final ConverterNode link;

        private ConverterNode(Class<?> from, Function<?,?> convert,
                ConverterNode link) {
            this.from = from;
            this.convert = (Function<Object,Object>)convert;
            this.link = link;
        }
    }

    static {
        register(Character.class, char.class, Function.identity());
        register(Byte.class, byte.class, Function.identity());
        register(Short.class, short.class, Function.identity());
        register(Integer.class, int.class, Function.identity());
        register(Long.class, long.class, Function.identity());
        register(Float.class, float.class, Function.identity());
        register(Double.class, double.class, Function.identity());

        register(Character.class, Integer.class, (c) -> Integer.valueOf(c));
        register(Byte.class, Integer.class, (b) -> b.intValue());
        register(Short.class, Integer.class, (s) -> s.intValue());
        register(Integer.class, Long.class, (i) -> i.longValue());
        register(Integer.class, Float.class, (i) -> i.floatValue());
        register(Long.class, Double.class, (l) -> l.doubleValue());
        register(Float.class, Double.class, (f) -> f.doubleValue());
    }
}
