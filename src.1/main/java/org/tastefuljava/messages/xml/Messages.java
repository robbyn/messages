package org.tastefuljava.messages.xml;

import java.util.HashMap;
import java.util.Map;

public class Messages {
    private String language;
    private final Map<String,Message> messages = new HashMap<>();
    protected String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
