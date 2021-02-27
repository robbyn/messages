package org.tastefuljava.messages.expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Text {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public String getEol() {
        return "\n";
    }

    public static <T> String join(
            Collection<T> items,
            Function<T,String> toString,
            String delim) {
        return items
                .stream()
                .map(toString)
                .collect(Collectors.joining(delim));
    }

    public <T> String join(
            Collection<T> items,
            String delim) {
        return items
                .stream()
                .map(T::toString)
                .collect(Collectors.joining(delim));
    }

    public <T> String joinEnquote(
            Collection<T> items,
            String delim) {
        return items
                .stream()
                .map(T::toString)
                .map((s) -> enquote(s, '"'))
                .collect(Collectors.joining(delim));
    }

    public String enquote(Object o) {
        return enquote(o == null ? "" : o.toString(), '"');
    }

    public static String enquote(char[] chars) {
        return enquote(chars, '"');
    }

    public static String enquote(String s, char q) {
        return s == null ? null : enquote(s.toCharArray(), q);
    }

    public static String enquote(char[] chars, char q) {
        if (chars == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        buf.append('"');
        for (char c : chars) {
            switch (c) {
            case '\b':
                buf.append("\\b");
                break;
            case '\t':
                buf.append("\\t");
                break;
            case '\n':
                buf.append("\\n");
                break;
            case '\f':
                buf.append("\\f");
                break;
            default:
                if (c == q) {
                    buf.append('\\').append(q);
                } else if (c >= ' ' && c <= 127) {
                    buf.append(c);
                } else {
                    buf.append("\\u")
                            .append(hex(c & 0xFFFF, 4));
                }
                break;
            }
        }
        buf.append('"');
        return buf.toString();
    }

    public static String hex(int val, int digits) {
        char[] chars = new char[digits];
        for (int i = digits; --i >= 0;) {
            chars[i] = HEX[val & 0xF];
            val >>>= 4;
        }
        return new String(chars);
    }
}
