package dev.juanvega.bindenv4j.reader;

import java.util.Optional;

public interface EnvReader {
     <T> Optional<T> read(String key, Class<T> targetType);

     default <T> T read(String key, Class<T> targetType, T defaultValue) {
         return read(key, targetType).orElse(defaultValue);
     }

     static EnvReader defaultReader() {
          return new SystemEnvReader();
     }
}
