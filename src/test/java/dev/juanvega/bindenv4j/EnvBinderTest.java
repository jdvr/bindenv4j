package dev.juanvega.bindenv4j;


import dev.juanvega.bindenv4j.reader.EnvReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvBinderTest {

    @Test
    public void support_plain_class() {
        var binder = new EnvBinder(new PrivateFieldWithObject.CustomReader(
                99,
                "expected name"
        ));
        var result = binder.bind(PrivateFieldWithObject.class);
        assertThat(result.getMax()).isEqualTo(99);
        assertThat(result.getName()).isEqualTo("expected name");
    }

    @Test
    public void primitive_fields_are_required() {
        var binder = new EnvBinder(new PrivateFieldWithObject.CustomReader(
                null,
                "ignored"
        ));
        var illegalStateException = Assertions.catchThrowableOfType(
                () -> binder.bind(PrivateFieldWithObject.class),
                IllegalStateException.class
        );
        Assertions.assertThat(illegalStateException).hasMessage(
                "No value for max"
        );
    }

    @Test
    public void object_fields_are_optional() {
        var binder = new EnvBinder(new PrivateFieldWithObject.CustomReader(
                893,
                null
        ));
        var bound = binder.bind(PrivateFieldWithObject.class);
        Assertions.assertThat(bound.max).isEqualTo(893);
        Assertions.assertThat(bound.name).isNull();
    }

    @Test
    public void ignore_static_fields() {
        var binder = new EnvBinder(new AlwaysFailReader());
        var bound = binder.bind(IgnoreStaticFields.class);
        assertThat(bound).isNotNull();
    }

    @Test
    public void override_field_name() {
        var binder = new EnvBinder(new OverrideNameField.CustomReader());
        var result = binder.bind(OverrideNameField.class);
        assertThat(result.getMax()).isEqualTo(99);
    }

    @Test
    public void support_records() {
        var binder = new EnvBinder(new BasicRecord.CustomReader(2.55, true));
        var result = binder.bind(BasicRecord.class);
        assertThat(result.limit).isEqualTo(2.55);
        assertThat(result.isFeatureEnabled).isTrue();
    }

    @Test
    public void support_custom_name_records() {
        var binder = new EnvBinder(new BasicRecord.CustomReader(55.99, false));
        var result = binder.bind(CustomNameRecord.class);
        assertThat(result.thresholdForAlert).isEqualTo(55.99);
        assertThat(result.isFeatureEnabled).isFalse();
    }


    @Test
    public void constructor_parameter_must_have_a_matching_field() {
        var binder = new EnvBinder(new EnvReader() {
            @Override
            public <T> Optional<T> read(String key, Class<T> targetType) {
                if ("a".equals(key)) {
                    return Optional.of((T) Integer.valueOf(1));
                }
                return Optional.empty();
            }
        });

        var exception = Assertions.catchThrowableOfType(
                () -> binder.bind(MismatchedConstructorParameter.class),
                IllegalStateException.class
        );

        Assertions.assertThat(exception)
                .hasMessage("Unable to find field for constructor parameter b");
    }

    static class MismatchedConstructorParameter {
        final int a;

        MismatchedConstructorParameter(int b) {
            this.a = b;
        }
    }

    record BasicRecord(Double limit, Boolean isFeatureEnabled) {
        static class CustomReader implements EnvReader {
            Double limit;
            Boolean isFeatureEnabled;

            CustomReader(Double limit, Boolean isFeatureEnabled) {
                this.limit = limit;
                this.isFeatureEnabled = isFeatureEnabled;
            }

            @Override
            public <T> Optional<T> read(String key, Class<T> targetType) {
                return switch (key) {
                    case "limit" -> Optional.ofNullable((T) limit);
                    case "isFeatureEnabled" -> Optional.ofNullable((T) isFeatureEnabled);
                    case null, default -> new AlwaysFailReader().read(key, targetType);
                };
            }
        }
    }

    record CustomNameRecord(
            @Name("limit")
            Double thresholdForAlert,
            boolean isFeatureEnabled
    ) {
    }

    public static class PrivateFieldWithObject {
        private int max;
        private String name;

        public int getMax() {
            return max;
        }

        public String getName() {
            return name;
        }

        static class CustomReader implements EnvReader {
            Integer max;
            String name;

            CustomReader(Integer max, String name) {
                this.max = max;
                this.name = name;
            }

            @Override
            public <T> Optional<T> read(String key, Class<T> targetType) {
                return switch (key) {
                    case "name" -> Optional.ofNullable((T) name);
                    case "max" -> Optional.ofNullable((T) max);
                    case null, default -> unknown(key, targetType);
                };
            }
        }
    }

    static class OverrideNameField {
        static final String ENV_VAR_NAME = "THREAD_MAX";
        @Name(ENV_VAR_NAME)
        private int max;

        public int getMax() {
            return max;
        }

        static class CustomReader implements EnvReader {
            @Override
            public <T> Optional<T> read(String key, Class<T> targetType) {
                if (key.equals(ENV_VAR_NAME)) {
                    return Optional.of((T) Integer.valueOf(99));
                }

                return unknown(key, targetType);
            }
        }
    }

    static class IgnoreStaticFields {
        static final String IGNORE_ME = "IGNORE_ME";
    }

    static class AlwaysFailReader implements EnvReader {
        @Override
        public <T> Optional<T> read(String key, Class<T> targetType) {
            return unknown(key, targetType);
        }
    }

    private static <T> Optional<T> unknown(String key, Class<T> targetType) {
        throw new IllegalArgumentException(
                "Unknown key %s of type %s".formatted(key, targetType.getSimpleName())
        );
    }


}
