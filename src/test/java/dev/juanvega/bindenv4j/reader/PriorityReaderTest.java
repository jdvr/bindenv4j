package dev.juanvega.bindenv4j.reader;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PriorityReaderTest {

    @Test
    public void priority_check_available_reader_in_order() {
        EnvReader reader = EnvReader.priority(
                new SingleValueReader("first", "first"),
                new SingleValueReader("second", "second"),
                new SingleValueReader("second", "ignored")
        );
        assertThat(reader.read("first", String.class)).contains("first");
        assertThat(reader.read("second", String.class)).contains("second");
        assertThat(reader.read("third", String.class)).isEmpty();
    }

    @Test
    public void priority_ignore_exception() {
        EnvReader reader = EnvReader.priority(
                new PrimitiveTypesReader(key -> {
                    throw  new IllegalArgumentException("Not found");
                }),
                new SingleValueReader("available", "value")
        );
        assertThat(reader.read("available", String.class)).contains("value");
    }

    static class SingleValueReader implements EnvReader {
        private final String key;
        private final String value;

        SingleValueReader(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public <T> Optional<T> read(String key, Class<T> targetType) {
            return this.key.equals(key) ? Optional.of(targetType.cast(value)) : Optional.empty();
        }
    }

}
