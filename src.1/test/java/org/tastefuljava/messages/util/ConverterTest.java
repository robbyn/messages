package org.tastefuljava.messages.util;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConverterTest {

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() throws IOException {
    }

    @Test
    public void testSimpleConversions() {
        Object obj = Converter.INSTANCE.convert(4, Double.TYPE);
        assertNotNull(obj);
        assertEquals(Double.class, obj.getClass());
        assertEquals(4.0, ((Double)obj).doubleValue());
    }
}
