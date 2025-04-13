package io.cdap.wrangler.api.parser;

import org.junit.Test;
import static org.junit.Assert.*;

public class ByteSizeTest {
    @Test
    public void testByteSizeParsing() {
        assertEquals(1024L, new ByteSize("1KB").getBytes()); // 1 KB = 1024 bytes
        assertEquals(1024*1024L, new ByteSize("1MB").getBytes());
        assertEquals(5*1024L, new ByteSize("5KB").getBytes());
        assertEquals(1536L, new ByteSize("1.5KB").getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSize() {
        new ByteSize("invalid");
    }
}