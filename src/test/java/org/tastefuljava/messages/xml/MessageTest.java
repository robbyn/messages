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
        assertEquals("en", messages.getLanguage());
        assertEquals("Unknown application: MyApp.",
                messages.format("appNotFound", "MyApp"));
        assertEquals("no parameter", messages.format("nopar"));
    }

    @Test
    public void testChoose() {
        assertEquals("(zero three)", messages.format("choose", 0, 3));
        assertEquals("(zero something else)",
                messages.format("choose", 0, -1));
        assertEquals("(something else )",
                messages.format("choose", 4, 3));
        assertEquals("(something else four)",
                messages.format("choose", 4, 4));
    }

    @Test
    public void testList() {
        assertEquals("(1, 2, 3)", messages.format("list", Arrays.asList(1, 2, 3)));
        assertEquals("(\"1\", \"2\", \"3\")", messages.format("list2", Arrays.asList(1, 2, 3)));
        assertEquals("(1, 2, 3)", messages.format("list3", Arrays.asList(1, 2, 3)));
        assertEquals("(\"1\", \"2\", \"3\")", messages.format("list4", Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testText() {
        assertEquals(
                "---- Begin text ----\n"
                + "Line 1\nLine 2\nLine 3\n"
                + "---- End text ----",
                messages.format("text", "Line 1", "Line 2", "Line 3"));
    }

    @Test
    public void testGenerics()  {
        List<String> a = Arrays.asList("a", "b", "c");
        List<String> b = Arrays.asList("1", "2", "3");
        assertEquals("a, b, c 1, 2, 3", messages.format("generics", a, b));
    }
}
