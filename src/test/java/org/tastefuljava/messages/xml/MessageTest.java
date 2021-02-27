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
        assertEquals("Application inconnue: MyApp.",
                messages.format("appNotFound", null, "MyApp"));
    }
}
