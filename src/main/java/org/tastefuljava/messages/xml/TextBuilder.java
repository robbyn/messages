package org.tastefuljava.messages.xml;

import org.xml.sax.SAXException;

public class TextBuilder {
    private final StringBuilder buf = new StringBuilder();
    private boolean wasBlank;
    private boolean skipBlank = true;

    public void start(boolean trimHead) {
        buf.setLength(0);
        wasBlank = false;
        skipBlank = trimHead;
    }

    public String end(boolean trimTail) {
        if (wasBlank && !skipBlank && !trimTail) {
            buf.append(' ');
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
            } else {
                if (wasBlank) {
                    if (!skipBlank) {
                        buf.append(' ');
                    }
                    wasBlank = false;
                }
                buf.append(c);
                skipBlank = false;
            }
        }
    }
}
