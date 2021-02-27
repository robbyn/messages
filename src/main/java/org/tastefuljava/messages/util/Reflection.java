package org.tastefuljava.messages.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reflection {
    private static final Logger LOG
            = Logger.getLogger(Reflection.class.getName());

    private static final Class<?>[] EMPTY_CLASS_ARRAY = {};

    public static Field[] fieldList(Class<?> clazz, String... names) {
        Field[] fields = new Field[names.length];
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = Reflection.getInstanceField(clazz, names[i]);
            if (fields[i] == null) {
                throw new IllegalArgumentException("Field " + names[i]
                        + " not found in class " + clazz.getName());
            }
        }
        return fields;
    }

    private Reflection() {
    }

    public static Class<? extends Object> loadClass(String className,
            String... packagePath) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class result = findClass(cl, className, packagePath);
        if (cl == null) {
            throw new IllegalArgumentException(
                    "Class not found: " + className);
        }
        return result;
    }

    private static Class<? extends Object> findClass(ClassLoader cl,
            String className, String... packagePath) {
        try {
            Class<?> clazz = findClass(cl, className);
            if (clazz != null) {
                return clazz;
            }
            for (String packageName: packagePath) {
                clazz = findClass(cl, packageName + '.' + className);
                if (clazz != null) {
                    return clazz;
                }
            }
            return null;
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    private static Class<? extends Object> findClass(ClassLoader cl,
            String fullName) throws ClassNotFoundException {
        String resName = fullName.replace('.', '/') + ".class";
        if (cl.getResource(resName) == null) {
            return null;
        }
        return cl.loadClass(fullName);
    }

    public static Field getInstanceField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field f;
            try {
                f = c.getDeclaredField(name);
            } catch (NoSuchFieldException | SecurityException ex) {
                f = null;
            }
            if (f != null) {
                int mods = f.getModifiers();
                if (!Modifier.isStatic(mods) && !Modifier.isTransient(mods)) {
                    f.setAccessible(true);
                    return f;
                }
            }
        }
        return null;
    }

    public static <T> T getConstant(Class<?> clazz, String name, Class<T> type) {
        try {
            Field field = clazz.getField(name);
            int mods = field.getModifiers();
            if (!Modifier.isStatic(mods) || !Modifier.isFinal(mods)) {
                throw new IllegalArgumentException(
                        "Field " + clazz.getName() + "." + name
                        + " is not a constant");
            }
            if (!type.isAssignableFrom(field.getType())) {
                throw new IllegalArgumentException("Wrong constant type "
                        + clazz.getName() + "." + name);
            }
            @SuppressWarnings("unchecked")
            T result = (T)field.get(null);
            return result;
        } catch (NoSuchFieldException | SecurityException 
                | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
 
    public static Method getPropGetter(Class<?> clazz, String name) {
        try {
            String cname = Character.toUpperCase(name.charAt(0))
                    + name.substring(1);
            Method method = clazz.getMethod("get" + cname, EMPTY_CLASS_ARRAY);
            if (method != null && !Modifier.isStatic(method.getModifiers())) {
                return method;
            }
            method = clazz.getMethod("is" + cname, EMPTY_CLASS_ARRAY);
            if (method != null && !Modifier.isStatic(method.getModifiers())
                    && method.getReturnType() == boolean.class) {
                return method;
            }
            return null;
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Object invokeMethod(Object obj, String name, Object... args) {
        if (obj == null) {
            return null;
        }
        try {
            Class<?> clazz = obj.getClass();
            OuterLoop:
            for (Method method: clazz.getMethods()) {
                if (!Modifier.isStatic(method.getModifiers())
                        && method.getName().equals(name)
                        && method.getParameterCount() == args.length) {
                    Object[] convArgs = new Object[args.length];
                    Class<?>[] argTypes = method.getParameterTypes();
                    for (int i = 0; i < args.length; ++i) {
                        Object arg = args[i];
                        Class<?> type = argTypes[i];
                        if (!Converter.INSTANCE.isConvertible(arg, type)) {
                            continue OuterLoop;
                        }
                        convArgs[i] = Converter.INSTANCE.convert(arg, type);
                    }
                    return method.invoke(obj, convArgs);
                }
            }
            return null;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static Class<?>[] getReferencedClasses(Field field, int count) {
        Type[] argTypes = getReferencedTypes(field, count);
        Class<?>[] result = new Class<?>[count];
        for (int i = 0; i < count; ++i) {
            Type atype = argTypes[i];
            if (!(atype instanceof Class)) {
                throw new IllegalArgumentException(
                        "Element type is not a class");
            }
            result[i] = (Class<?>)atype;
        }
        return result;
    }

    private static Type[] getReferencedTypes(Field field, int count) {
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Not a parameterized type");
        }
        ParameterizedType ptype = (ParameterizedType)type;
        Type[] argTypes = ptype.getActualTypeArguments();
        if (argTypes.length != count) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
        return argTypes;
    }
}
