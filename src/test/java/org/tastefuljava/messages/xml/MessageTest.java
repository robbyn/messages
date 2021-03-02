package org.tastefuljava.messages.xml;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTest {
    private Messages messages;

    @BeforeEach
    public void setUp() throws IOException {
        messages = MessageFileLoader.loadMessages(
                MessageTest.class.getResourceAsStream("messages.xml"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        messages = null;
    }

    @Test
    public void testSimple() {
        assertEquals("Unknown application: MyApp.",
                messages.format("appNotFound", null, "MyApp"));
    }

    @Test
    public void testChoose() {
        assertEquals("(zero three)", messages.format("choose", null, 0, 3));
        assertEquals("(zero something else)",
                messages.format("choose", null, 0, -1));
        assertEquals("(something else )",
                messages.format("choose", null, 4, 3));
        assertEquals("(something else four)",
                messages.format("choose", null, 4, 4));
    }

    @Test
    public void testList() {
        assertEquals("(1, 2, 3)", messages.format(
                "list", null, Arrays.asList(1, 2, 3)));
        assertEquals("(\"1\", \"2\", \"3\")", messages.format(
                "list2", null, Arrays.asList(1, 2, 3)));
        assertEquals("(1, 2, 3)", messages.format(
                "list3", null, Arrays.asList(1, 2, 3)));
        assertEquals("(\"1\", \"2\", \"3\")", messages.format(
                "list4", null, Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testLanguages() {
        assertEquals("Hello Bob!!!", messages.format(
                "hello", null, "Bob"));
        assertEquals("Hello Bob!!!", messages.format(
                "hello", "en", "Bob"));
        assertEquals("Bonjour Bob!!!", messages.format(
                "hello", "fr", "Bob"));
    }


    @Test
    public void testText() {
        assertEquals(
                "---- Begin text ----\n"
                + "Line 1\nLine 2\nLine 3\n"
                + "---- End text ----",
                messages.format(
                        "text", null, "Line 1", "Line 2", "Line 3"));
    }
}

