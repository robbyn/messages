package org.tastefuljava.messages.xml;

import org.xml.sax.SAXException;

public class TextBuilder {
    private final StringBuilder buf = new StringBuilder();
    private boolean wasBlank;
    private boolean skipBlank = true;
    private int eolCount;

    public void start(boolean trimHead) {
        buf.setLength(0);
        wasBlank = false;
        skipBlank = trimHead;
        eolCount = 0;
    }

    public String end(boolean trimTail) {
        if (!trimTail) {
            addSpace();
        }
        return buf.toString();
    }

    public void addChars(char[] ch, int start, int length)
            throws SAXException {
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
