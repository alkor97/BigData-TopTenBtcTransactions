package com.globallogic.bcttt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class MessageParser {

    private static final Logger log = Logger.getLogger(MessageParser.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<Message> parse(String message) {
        try {
            return Optional.of(new Message((mapper.readValue(message, Map.class))));
        } catch (JsonProcessingException e) {
            log.severe(e.getMessage());
            return Optional.empty();
        }
    }
}
