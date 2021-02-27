package org.tastefuljava.messages.expr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.messages.util.Reflection;

public abstract class AbstractParser {
    private static Object[] EMPTY_ARRAY = {};

    protected CompilationContext context;
    private int varCount;

    public abstract Expression parse() throws IOException;

    public CompilationContext getContext() {
        return context;
    }

    public void setContext(CompilationContext context) {
        this.context = context;
    }

    protected Expression equal(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a.getClass() == b.getClass()) {
                return a.equals(b);
            }
            if (a instanceof Integer && b instanceof Integer) {
                return a.equals(b);
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() == ((Number)b).doubleValue();
            }
            if (a instanceof String) {
                return ((String)a).equals(b.toString());
            }
            if (b instanceof String) {
                return a.toString().equals((String)b);
            }
            return null;
        };
    }

    protected Expression notEqual(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a.getClass() == b.getClass()) {
                return a.equals(b);
            }
            if (a instanceof Integer && b instanceof Integer) {
                return !a.equals(b);
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() != ((Number)b).doubleValue();
            }
            if (a instanceof String) {
                return !((String)a).equals(b.toString());
            }
            if (b instanceof String) {
                return !a.toString().equals((String)b);
            }
            return null;
        };
    }

    protected Expression less(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a < (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() < ((Number)b).doubleValue();
            }
            if (a instanceof String) {
                return ((String)a).compareTo(b.toString()) < 0;
            }
            if (b instanceof String) {
                return a.toString().compareTo((String)b) < 0;
            }
            return null;
        };
    }

    protected Expression lessEqual(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a <= (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() <= ((Number)b).doubleValue();
            }
            if (a instanceof String) {
                return ((String)a).compareTo(b.toString()) <= 0;
            }
            if (b instanceof String) {
                return a.toString().compareTo((String)b) <= 0;
            }
            return null;
        };
    }

    protected Expression greater(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a > (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() > ((Number)b).doubleValue();
            }
            if (a instanceof String) {
                return ((String)a).compareTo(b.toString()) > 0;
            }
            if (b instanceof String) {
                return a.toString().compareTo((String)b) > 0;
            }
            return null;
        };
    }

    protected Expression greaterEqual(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a >= (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() >= ((Number)b).doubleValue();
            }
            if (a instanceof String) {
                return ((String)a).compareTo(b.toString()) >= 0;
            }
            if (b instanceof String) {
                return a.toString().compareTo((String)b) >= 0;
            }
            return null;
        };
    }

    protected Expression neg(Expression e) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a instanceof Integer) {
                return -(Integer)a;
            }
            if (a instanceof Number) {
                return -((Number)a).doubleValue();
            }
            return null;
        };
    }

    protected Expression add(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            Object b = d.evaluate(c);
            if (a instanceof String)  {
                return b == null ? a : (String)a + b.toString();
            }
            if (b instanceof String)  {
                return a == null ? a : (String)a + b.toString();
            }
            if (a == null) {
                return b == null;
            }
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a + (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() + ((Number)b).doubleValue();
            }
            return null;
        };
    }

    protected Expression sub(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a - (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() - ((Number)b).doubleValue();
            }
            return null;
        };
    }

    protected Expression mul(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a * (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() * ((Number)b).doubleValue();
            }
            return null;
        };
    }

    protected Expression div(Expression e, Expression d) {
        return (c) -> {
            Object a = e.evaluate(c);
            if (a == null) {
                return null;
            }
            Object b = d.evaluate(c);
            if (b == null) {
                return null;
            }
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer)a / (Integer)b;
            }
            if (a instanceof Number && b instanceof Number) {
                return ((Number)a).doubleValue() / ((Number)b).doubleValue();
            }
            return null;
        };
    }

    protected Expression or(Expression e, Expression d) {
        return (c) -> {
            if (asBoolean(e.evaluate(c))) {
                return Boolean.TRUE;
            } else {
                return asBoolean(d.evaluate(c));
            }
        };
    }

    protected Expression and(Expression e, Expression d) {
        return (c) -> {
            if (!asBoolean(e.evaluate(c))) {
                return Boolean.FALSE;
            } else {
                return asBoolean(d.evaluate(c));
            }
        };
    }

    protected Expression intNumber(int n) {
        return (c) -> n;
    }

    protected Expression floatNumber(double x) {
        return (c) -> x;
    }

    protected Expression string(String s) {
        StringBuilder buf = new StringBuilder();
        if (s == null) {
            throw new IllegalArgumentException("String cannot be null");
        }
        int st = 0;
        Loop:
        for (char c: s.toCharArray()) {
            switch (st) {
                case 0:
                    switch (c) {
                        case '"':
                            st = 1;
                            break;
                        default:
                            break Loop; // '"' expected
                    }
                    break;
                case 1:
                    switch (c) {
                        case '"':
                            st = 2;
                            break;
                        case '\\':
                            st = 3;
                            break;
                        default:
                            buf.append(c);
                            break;
                    }
                    break;
                case 2:
                    throw new IllegalArgumentException("Invalid string " + s);
                case 3:
                    switch (c) {
                        case 'n':
                            buf.append('\n');
                            break;
                        case 'b':
                            buf.append('\b');
                            break;
                        case 'r':
                            buf.append('\r');
                            break;
                        case 'f':
                            buf.append('\f');
                            break;
                        case '\\':
                            buf.append('\\');
                            break;
                        case '"':
                            buf.append('"');
                            break;
                        case '\'':
                            buf.append('\'');
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Invalid string " + s);
                    }
                    st = 1;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid string " + s);
            }
        }
        if (st != 2) {
            throw new IllegalArgumentException("Invalid string " + s);
        }
        final String result = buf.toString();
        return (c) -> result;
    }

    public Expression lookup(String name) {
        return context.resolve(name);
    }

    public Expression prop(Expression e, String name) {
        return (c) -> {
            Object o = e.evaluate(c);
            if (o == null) {
                return null;
            }
            Method getter = Reflection.getPropGetter(o.getClass(), name);
            if (getter == null) {
                return null;
            }
            try {
                return getter.invoke(o, EMPTY_ARRAY);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        };
    }

    public Expression invoke(Expression e, String name,
            Expression[] parms) {
        return (c) -> {
            Object o = e.evaluate(c);
            if (o == null) {
                return null;
            }
            Object[] args = new Object[parms.length];
            for (int i = 0; i < args.length; ++i) {
                args[i] = parms[i].evaluate(c);
            }
            return Reflection.invokeMethod(o, name, args);
        };
    }

    public Expression index(Expression e, Expression d) {
        return (c) -> {
            Object o = e.evaluate(c);
            if (o == null) {
                return null;
            }
            Object n = d.evaluate(c);
            throw new UnsupportedOperationException(
                    "index not implemented yet");
        };
    }

    private boolean asBoolean(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return ((Boolean)o);
        }
        if (o instanceof Number) {
            return ((Number)o).doubleValue() == 0;
        }
        return true;
    }
}
