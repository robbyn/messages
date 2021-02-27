package org.tastefuljava.messages.util;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
        Object obj = Converter.INSTANCE.convert(4, Long.TYPE);
        assertNotNull(obj);
        assertEquals(Long.class, obj.getClass());
        assertEquals(4L, ((Long)obj).longValue());
    }
}
