package org.tastefuljava.messages.xml;

public class SimpleTextBuilder implements TextHandler {
    private final StringBuilder buf = new StringBuilder();

    @Override
    public void addChars(char[] ch, int start, int length) {
        buf.append(ch, start, length);
    }

    public String end() {
        return buf.toString();
    }
}
