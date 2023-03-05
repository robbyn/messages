package org.tastefuljava.messages.xml;

public class TextBuilder implements TextHandler {
    private final StringBuilder buf = new StringBuilder();
    private boolean wasBlank;
    private boolean skipBlank;
    private int eolCount;

    public TextBuilder(boolean trimHead) {
        this.skipBlank = trimHead;
    }

    public String end(boolean trimTail) {
        if (!trimTail) {
            addSpace();
        }
        return buf.toString();
    }

    @Override
    public void addChars(char[] ch, int start, int length) {
        int end = start + length;
        for (int i = start; i < end; ++i) {
            char c = ch[i];
            if (Character.isWhitespace(c)) {
                wasBlank = true;
                if (c == '\n') {
                    ++eolCount;
                }
            } else {
                addSpace();
                buf.append(c);
                skipBlank = false;
            }
        }
    }

    private void addSpace() {
        if (wasBlank) {
            if (!skipBlank) {
                buf.append(eolCount > 1 ? '\n' : ' ');
            }
            wasBlank = false;
            eolCount = 0;
        }
    }
}
