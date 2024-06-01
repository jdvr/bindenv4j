package dev.juanvega.bindenv4j;

import java.util.Optional;

public interface EnvReader {
     <T> Optional<T> read(String key, T targetType);
}
