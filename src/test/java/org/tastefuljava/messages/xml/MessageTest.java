package org.tastefuljava.messages.xml;

import java.io.IOException;
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
    public void testSelect() {
        assertEquals("(zero three)", messages.format("select", null, 0, 3));
        assertEquals("(zero something else)",
                messages.format("select", null, 0, -1));
        assertEquals("(something else)",
                messages.format("select", null, 4, 3));
    }
}
