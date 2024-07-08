package dev.juanvega.bindenv4j.reader;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SystemEnvReaderTest {

    @Test
    @SetEnvironmentVariable(key = "MY_ENV_STRING", value = "a full string")
    @SetEnvironmentVariable(key = "MY_ENV_INT", value = "1993")
    @SetEnvironmentVariable(key = "MY_ENV_SHORT", value = "123")
    @SetEnvironmentVariable(key = "MY_ENV_FLOAT", value = "13.45")
    @SetEnvironmentVariable(key = "MY_ENV_BOOLEAN_TRUE", value = "trUe")
    @SetEnvironmentVariable(key = "MY_ENV_BOOLEAN_TRUTHY", value = "ENABLED")
    @SetEnvironmentVariable(key = "MY_ENV_BOOLEAN_FALSE", value = "faLse")
    @SetEnvironmentVariable(key = "MY_ENV_BOOLEAN_FALSY", value = "")
    void key_and_type_are_correctly_read() {
        var reader = new SystemEnvReader();
        assertThat(reader.read("MY_ENV_EMPTY", String.class)).isEmpty();
        assertThat(reader.read("MY_ENV_STRING", String.class)).contains("a full string");
        assertThat(reader.read("MY_ENV_INT", Integer.class)).contains(1993);
        assertThat(reader.read("MY_ENV_SHORT", Short.class)).contains((short) 123);
        assertThat(reader.read("MY_ENV_FLOAT", Float.class)).contains(13.45f);
        assertThat(reader.read("MY_ENV_BOOLEAN_TRUE", Boolean.class)).contains(true);
        assertThat(reader.read("MY_ENV_BOOLEAN_TRUTHY", Boolean.class)).contains(true);
        assertThat(reader.read("MY_ENV_BOOLEAN_FALSE", Boolean.class)).contains(false);
        assertThat(reader.read("MY_ENV_BOOLEAN_FALSY", Boolean.class)).contains(false);
    }

}
