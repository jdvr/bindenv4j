BindEnv4J
---

Easily bind environment variables into a Java POJO.


# Features

- Support for primitive and standard library types
- Define target env var name using `@Name`
- Primitive type are considered required
- Define your custom reader
- Zero dependencies

## TODO

- Recursive binding

# Examples

There are examples in the [test](./src/test/java/dev/juanvega/bindenv4j) folder.

## Basic usage

```java
import dev.juanvega.bindenv4j.BindEnv;
import dev.juanvega.bindenv4j.Name;

public class Example {
    public static void main(String[] args) {
        BindEnv bindEnv = new BindEnv(EnvReader.defaultReader());
        ExampleConfig config = bindEnv.bind(ExampleConfig.class);
        System.out.println(config);
    }
    record ExampleConfig(@Name("MY_ENV_VAR") String myEnvVar) {}
}
```

## Custom reader

### Reading from `.env` file

See [dotenv-java](https://github.com/cdimascio/dotenv-java) to know how to read from `.env` file

```java
public class Main {
    public static void main(String[] args) {
        // imagine you have the following .env file
        // NAME=Link
        // AGE=230
        // configure Dotenv https://github.com/cdimascio/dotenv-java
        var dotenv = Dotenv.load();
        var reader = new PrimitiveTypesReader(dotenv::get);
        var binder = new Binder(reader);
        var config = binder.bind(MyConfig.class);
        // MyConfig[name=Link, age=230]
        System.out.println(config);
    }
    
    record MyConfig(
            @Name("NAME") String name,
            @Name("AGE") int age
    ) {}
}
```

### Support different type casting with Jackson

In case you want to support more complex types, you can use Jackson to parse the value.

```java
public class Main {
    public static void main(String[] args) {
        // imagine you have the following var
        // UNSUPPORTED=1
        var reader = new JacksonReader();
        var binder = new Binder(reader);
        var config = binder.bind(MyConfig.class);
        // MyConfig[value=1]
        System.out.println(config);
    }

    record MyConfig(
            @Name("UNSUPPORTED") byte value
    ) {
    }


    static class JacksonReader implements EnvReader {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public <T> Optional<T> read(String key, Class<T> targetType) {
            var convertedVal = objectMapper.convertValue(key, type);
            return Optional.ofNullable((T) convertedVal);
        }
    }
}
```

### Multiple source for values with priority

```java
class Main {
    public static void main(String[] args) {
        // imagine you have the following .env file
        // NAME=Link
        // Then you have the following env var
        // AGE=230
        // And you have a unsuported type
        // UNSUPPORTED=1
        var dotenv = Dotenv.load();
        var dotEnvReader = new PrimitiveTypesReader(dotenv::get);
        var defaultReader = EnvReader.defaultReader();
        var jacksonReader = new JacksonReader();
        var reader = EnvReader.priority(dotEnvReader, defaultReader, jacksonReader);
        var binder = new Binder(reader);
        var config = binder.bind(MyConfig.class);
        // MyConfig[name=Link, age=230. myByte=1]
        System.out.println(config);
    }
    
    record MyConfig(
            @Name("NAME") String name,
            @Name("AGE") int age,
            @Name("UNSUPPORTED") byte myByte
    ) {}
}
```

# Contributing

For any feature request or help, please open an issue. Pull Request are welcome but this library aims to be easy to use and scope limited.

Code style is a mix of standard Java Style and some patterns I wanted to test as this is a personal side project. Do not use this project as idiomatic Java code.

# License

see [LICENSE](./LICENSE)
