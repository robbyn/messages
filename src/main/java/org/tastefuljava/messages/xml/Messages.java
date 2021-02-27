package org.tastefuljava.messages.xml;

import java.util.HashMap;
import java.util.Map;
import org.tastefuljava.messages.expr.EvaluationContext;

public class Messages {
    private String prefix;
    private String defaultLanguage;
    private String description;
    private final Map<String,Message> messages = new HashMap<>();

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Message getMessage(String name) {
        return messages.get(name);
    }

    public void setMessage(String name, Message message) {
        messages.put(name, message);
    }

    public String format(String name, String language, Object... parms) {
        Message msg = messages.get(name);
        if (msg == null) {
            throw new IllegalArgumentException("Message inconnu: " + name);
        }
        return msg.format(language, defaultLanguage, parms);
    }
}
