package dev.juanvega.bindenv4j.reader;

import java.util.Optional;

public interface EnvReader {
     <T> Optional<T> read(String key, Class<T> targetType);
}
