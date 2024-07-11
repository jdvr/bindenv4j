package dev.juanvega.bindenv4j.reader;

import java.util.Optional;

public class PriorityReader implements EnvReader {
    private final EnvReader[] readers;

    PriorityReader(EnvReader[] readers) {
        this.readers = readers;
    }

    @Override
    public <T> Optional<T> read(String key, Class<T> targetType) {
        for (EnvReader reader : readers) {
            try {
                Optional<T> value = reader.read(key, targetType);
                if (value.isPresent()) {
                    return value;
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return Optional.empty();
    }
}
