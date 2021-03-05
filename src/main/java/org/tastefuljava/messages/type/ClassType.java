package org.tastefuljava.messages.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class ClassType implements Type {
    private static final Map<String,ClassType> CACHE = new WeakHashMap<>();

    private static final String[] EXPOSED_CLASS_NAMES = {
        "java.util.Collection",
        "java.util.List",
        "java.util.Set",
        "java.util.SortedSet",
        "java.util.Map",
        "java.util.SortedMap",
    };
    private static final Map<String,ClassType> EXPOSED_CLASSES;
    static {
        EXPOSED_CLASSES = new HashMap<>();
        for (String cn: EXPOSED_CLASS_NAMES) {
            exposeClass(cn);
        }
    }
    private static final String[] EXPOSED_PACKAGES = {
        "java.lang"
    };
    private final Class<?> clazz;

    public static ClassType fromName(String name) {
        ClassType type = findClass(name);
        if (type == null && name.indexOf('.') < 0) {
            type = EXPOSED_CLASSES.get(name);
            if (type != null) {
                return type;
            }
            for (String pkg: EXPOSED_PACKAGES) {
                type = findClass(pkg + '.' + name);
                if (type != null) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Class not found: " + name);
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

    private static void exposeClass(String name) {
        int ix = name.lastIndexOf('.');
        EXPOSED_CLASSES.put(name.substring(ix+1), fromName(name));
    }
}
