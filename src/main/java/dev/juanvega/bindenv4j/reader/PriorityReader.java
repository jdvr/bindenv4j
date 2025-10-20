package dev.juanvega.bindenv4j.reader;

import java.util.Optional;


/**
 * An {@link EnvReader} that reads from multiple sources in order, returning the first value found.
 *
 * <p>This class is useful for creating a fallback mechanism for reading environment variables. For
 * example, you could use a {@link PriorityReader} to first try reading from a .env file, and then
 * fall back to reading from system properties.</p>
 */
public class PriorityReader implements EnvReader {
    private final EnvReader[] readers;

    /**
     * Creates a new {@link PriorityReader} with the given readers.
     *
     * @param readers the {@link EnvReader}s to use, in order of priority
     */
    PriorityReader(EnvReader[] readers) {
        this.readers = readers;
    }

    /**
     * Reads an environment variable from the configured sources in order, returning the first value
     * found.
     *
     * @param key        the name of the environment variable
     * @param targetType the type to convert the value to
     * @return an {@link Optional} containing the value of the environment variable, or {@link Optional#empty()} if the variable is not found in any of the sources
     */
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
