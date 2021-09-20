package com.globallogic.bcttt;

import java.util.Map;
import java.util.Optional;

public class Message {

    private final Map<?, ?> map;

    public Message(Map<?, ?> map) {
        this.map = map;
    }

    public Optional<Double> getPrice() {
        return get(map, "data", Map.class)
                .flatMap(data -> get(data, "price", Double.class));
    }

    private <R> Optional<R> get(Map<?, ?> map, String key, Class<R> clazz) {
        Object value = map.get(key);
        if (value != null) {
            try {
                return Optional.of(clazz.cast(value));
            } catch (ClassCastException e) {
                // return empty optional
            }
        }
        return Optional.empty();
    }
}
