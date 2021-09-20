package com.globallogic.bcttt;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class MessageParserTest {

    @Test
    public void testSuccessful() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/message1.json")));
        MessageParser parser = new MessageParser();
        Optional<Message> maybeMessage = parser.parse(content);
        assertTrue(maybeMessage.isPresent());
        maybeMessage.ifPresent(msg -> {
            assertTrue(msg.getPrice().isPresent());
            msg.getPrice().ifPresent(price -> assertEquals(18058.68, price, 0.01));
        });
    }

    @Test
    public void testFailure() {
        String content = "{aaa-";
        MessageParser parser = new MessageParser();
        assertFalse(parser.parse(content).isPresent());
    }
}
