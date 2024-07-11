package dev.juanvega.bindenv4j.reader;

import java.util.Optional;

public class SystemEnvReader implements EnvReader {
    private final PrimitiveTypesReader delegated = new PrimitiveTypesReader(System::getenv);

    public <T> Optional<T> read(String key, Class<T> targetType) {
        return delegated.read(key, targetType);
    }
}
