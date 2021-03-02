package org.tastefuljava.messages.xml;

import java.util.HashMap;
import java.util.Map;

public class Messages extends Described {
    private String language;
    private final Map<String,Message> messages = new HashMap<>();

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Message getMessage(String name) {
        return messages.get(name);
    }

    public void setMessage(String name, Message message) {
        messages.put(name, message);
    }

    public String format(String name, Object... parms) {
        Message msg = messages.get(name);
        if (msg == null) {
            throw new IllegalArgumentException("Message inconnu: " + name);
        }
        return msg.format(parms);
    }
}
