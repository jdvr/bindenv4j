package dev.juanvega.bindenv4j.reader;

import dev.juanvega.bindenv4j.EnvBinder;
import dev.juanvega.bindenv4j.Name;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.util.Map;

import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_BOOLEAN;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_CHAR;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_DOUBLE;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_FLOAT;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_INT;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_LONG;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_SHORT;
import static dev.juanvega.bindenv4j.reader.AllTypesObject.MY_ENV_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class SystemEnvReaderTest {
    private static final String MY_ENV_BOOLEAN_TRUE = "MY_ENV_BOOLEAN_TRUE";
    private static final String MY_ENV_BOOLEAN_TRUTHY = "MY_ENV_BOOLEAN_TRUTHY";
    private static final String MY_ENV_BOOLEAN_FALSE = "MY_ENV_BOOLEAN_FALSE";
    private static final String MY_ENV_BOOLEAN_FALSY = "MY_ENV_BOOLEAN_FALSY";

    private static final String STRING_VALUE = "a full string";
    private static final int INT_VALUE = 1993;
    private static final String INT_VALUE_RAW = "1993";
    private static final long LONG_VALUE = 1993_1994L;
    private static final String longValueRaw = "19931994";
    private static final short SHORT_VALUE = 123;
    private static final String SHORT_VALUE_RAW = "123";
    private static final float FLOAT_VALUE = 13.45f;
    private static final String FLOAT_VALUE_RAW = "13.45";
    private static final double DOUBLE_VALUE = 45.99999d;
    private static final String doubleValueRaw = "45.99999";
    private static final boolean BOOLEAN_VALUE = true;
    private static final String BOOLEAN_VALUE_RAW = "true";
    private static final char CHAR_VALUE = 'r';
    private static final String charValueRaw = "r";


    @Test
    @SetEnvironmentVariable(key = MY_ENV_STRING, value = STRING_VALUE)
    @SetEnvironmentVariable(key = MY_ENV_INT, value = INT_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_SHORT, value = SHORT_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_FLOAT, value = FLOAT_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_BOOLEAN_TRUE, value = BOOLEAN_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_BOOLEAN_TRUTHY, value = "ENABLED")
    @SetEnvironmentVariable(key = MY_ENV_BOOLEAN_FALSE, value = "false")
    @SetEnvironmentVariable(key = MY_ENV_BOOLEAN_FALSY, value = "")
    void key_and_type_are_correctly_read() {
        var reader = new SystemEnvReader();
        assertThat(reader.read("MY_ENV_EMPTY", String.class)).isEmpty();
        assertThat(reader.read("MY_ENV_EMPTY", String.class, "defaultValue")).isEqualTo("defaultValue");
        assertThat(reader.read("MY_ENV_STRING", String.class)).contains("a full string");
        assertThat(reader.read("MY_ENV_INT", Integer.class)).contains(1993);
        assertThat(reader.read("MY_ENV_SHORT", Short.class)).contains((short) 123);
        assertThat(reader.read("MY_ENV_FLOAT", Float.class)).contains(13.45f);
        assertThat(reader.read("MY_ENV_BOOLEAN_TRUE", Boolean.class)).contains(true);
        assertThat(reader.read("MY_ENV_BOOLEAN_TRUTHY", Boolean.class)).contains(true);
        assertThat(reader.read("MY_ENV_BOOLEAN_FALSE", Boolean.class)).contains(false);
        assertThat(reader.read("MY_ENV_BOOLEAN_FALSY", Boolean.class)).contains(false);
    }

    @Test
    @SetEnvironmentVariable(key = MY_ENV_STRING, value = STRING_VALUE)
    @SetEnvironmentVariable(key = MY_ENV_INT, value = INT_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_SHORT, value = SHORT_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_FLOAT, value = FLOAT_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_LONG, value = longValueRaw)
    @SetEnvironmentVariable(key = MY_ENV_DOUBLE, value = doubleValueRaw)
    @SetEnvironmentVariable(key = MY_ENV_BOOLEAN, value = BOOLEAN_VALUE_RAW)
    @SetEnvironmentVariable(key = MY_ENV_CHAR, value = charValueRaw)
    public void integration_support_primitive_and_objects() {
        var binder = new EnvBinder(EnvReader.defaultReader());
        var allTypesResult = binder.bind(AllTypesObject.class);
        AllTypesObject.AllTypesObjectAssert.assertThat(allTypesResult)
                .allIsFilled()
                .sameTypeValueMatches()
                .contentMatchesEnvironment(Map.of(
                        MY_ENV_STRING, STRING_VALUE,
                        MY_ENV_INT, INT_VALUE,
                        MY_ENV_SHORT, SHORT_VALUE,
                        MY_ENV_FLOAT, FLOAT_VALUE,
                        MY_ENV_LONG, LONG_VALUE,
                        MY_ENV_DOUBLE, DOUBLE_VALUE,
                        MY_ENV_BOOLEAN, BOOLEAN_VALUE,
                        MY_ENV_CHAR, CHAR_VALUE
                ));
    }

    @Test
    @SetEnvironmentVariable(key = "UNSUPPORTED", value = "UNSUPPORTED")
    public void custom_types_arent_supported() {
        var binder = new EnvBinder(EnvReader.defaultReader());
        var illegalArgumentException = catchThrowableOfType(
                () -> binder.bind(Parent.class),
                IllegalArgumentException.class
        );
        assertThat(illegalArgumentException)
                .isNotNull()
                .hasMessage("Unsupported type: AllTypesObject");
    }

    public static class Parent {
        @Name("UNSUPPORTED")
        public AllTypesObject allTypesObject;
    }
}
