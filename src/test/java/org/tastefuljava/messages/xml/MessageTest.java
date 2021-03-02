package org.tastefuljava.messages.xml;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTest {
    private Messages messages;
    private MessageFormatter formatter;
    private MessageFormatter english;
    private MessageFormatter french;

    @BeforeEach
    public void setUp() throws IOException {
        messages = MessageFileLoader.loadMessages(
                MessageTest.class.getResourceAsStream("messages.xml"));
        formatter = messages.getFormatter(null);
        english = messages.getFormatter("en");
        french = messages.getFormatter("fr");
    }

    @AfterEach
    public void tearDown() throws IOException {
        messages = null;
    }

    @Test
    public void testSimple() {
        assertEquals("Unknown application: MyApp.",
                formatter.format("appNotFound", "MyApp"));
    }

    @Test
    public void testChoose() {
        assertEquals("(zero three)", formatter.format("choose", 0, 3));
        assertEquals("(zero something else)",
                formatter.format("choose", 0, -1));
        assertEquals("(something else )",
                formatter.format("choose", 4, 3));
        assertEquals("(something else four)",
                formatter.format("choose", 4, 4));
    }

    @Test
    public void testList() {
        assertEquals("(1, 2, 3)", formatter.format("list", Arrays.asList(1, 2, 3)));
        assertEquals("(\"1\", \"2\", \"3\")", formatter.format("list2", Arrays.asList(1, 2, 3)));
        assertEquals("(1, 2, 3)", formatter.format("list3", Arrays.asList(1, 2, 3)));
        assertEquals("(\"1\", \"2\", \"3\")", formatter.format("list4", Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testLanguages() {
        assertEquals("Hello Bob!!!", formatter.format("hello", "Bob"));
        assertEquals("Hello Bob!!!", english.format("hello", "Bob"));
        assertEquals("Bonjour Bob!!!", french.format("hello", "Bob"));
    }

    @Test
    public void testText() {
        assertEquals(
                "---- Begin text ----\n"
                + "Line 1\nLine 2\nLine 3\n"
                + "---- End text ----",
                formatter.format("text", "Line 1", "Line 2", "Line 3"));
    }
}

