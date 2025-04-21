import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AdaptiveHuffmanTest {
    public Encoder encoder;
    public Decoder decoder;

    @Before
    public void setUp() {
        encoder = new Encoder();
        decoder = new Decoder();
    }

    @Test
    public void testEncode() {
        String message = "ABC";
        // The expected encoded string
        String expectedEncoded = "010000010010000100001000011";
        String actualEncoded = encoder.compressMessage(message);
        assertEquals("Encoded output should match expected binary string.", expectedEncoded, actualEncoded);
    }

    @Test
    public void testDecode() {
        String encodedMessage = "010000010010000100001000011";
        String expectedDecoded = "ABC";
        String actualDecoded = decoder.decompressMessage(encodedMessage);
        assertEquals("Decoded message should match the original input.", expectedDecoded, actualDecoded);
    }
}
