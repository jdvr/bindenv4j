package dev.juanvega.bindenv4j.reader;

import java.util.Optional;


/**
 * An {@link EnvReader} that reads environment variables from the system environment.
 *
 * <p>This class uses a {@link PrimitiveTypesReader} to parse the raw string values of the
 * environment variables.</p>
 */
public class SystemEnvReader implements EnvReader {
    private final PrimitiveTypesReader delegated = new PrimitiveTypesReader(System::getenv);

    /**
     * Reads an environment variable from the system environment and converts it to the specified type.
     *
     * @param key        the name of the environment variable
     * @param targetType the type to convert the value to
     * @return an {@link Optional} containing the value of the environment variable, or {@link
     * Optional#empty()} if the variable is not set
     */
    public <T> Optional<T> read(String key, Class<T> targetType) {
        return delegated.read(key, targetType);
    }
}

