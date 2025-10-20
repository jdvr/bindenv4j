package dev.juanvega.bindenv4j.reader;

import java.util.Optional;


/**
 * A functional interface for reading environment variables.
 *
 * <p>Implementations of this interface are responsible for reading environment variables from a
 * specific source, such as system properties or a .env file.</p>
 */
public interface EnvReader {

    /**
     * Reads an environment variable and converts it to the specified type.
     *
     * @param key        the name of the environment variable
     * @param targetType the type to convert the value to
     * @return an {@link Optional} containing the value of the environment variable, or {@link Optional#empty()} if the variable is not set
     */
    <T> Optional<T> read(String key, Class<T> targetType);

    /**
     * Reads an environment variable and converts it to the specified type, returning a default value
     * if the variable is not set.
     *
     * @param key          the name of the environment variable
     * @param targetType   the type to convert the value to
     * @param defaultValue the default value to return if the environment variable is not set
     * @return the value of the environment variable, or the default value if the variable is not set
     */
    default <T> T read(String key, Class<T> targetType, T defaultValue) {
        return read(key, targetType).orElse(defaultValue);
    }

    /**
     * Returns the default {@link EnvReader}, which reads from the system environment.
     *
     * @return the default {@link EnvReader}
     */
    static EnvReader defaultReader() {
        return new SystemEnvReader();
    }

    /**
     * Creates a new {@link EnvReader} that reads from multiple sources in order, returning the first
     * value found.
     *
     * @param readers the {@link EnvReader}s to use, in order of priority
     * @return a new {@link EnvReader} that reads from multiple sources
     */
    static EnvReader priority(EnvReader... readers) {
        return new PriorityReader(readers);
    }
}

