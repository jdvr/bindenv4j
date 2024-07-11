package dev.juanvega.bindenv4j.reader;

import dev.juanvega.bindenv4j.Name;
import org.assertj.core.api.AbstractAssert;

import java.util.Map;

public class AllTypesObject {
    public static final String MY_ENV_STRING = "MY_ENV_STRING";
    public static final String MY_ENV_INT = "MY_ENV_INT";
    public static final String MY_ENV_DOUBLE = "MY_ENV_DOUBLE";
    public static final String MY_ENV_LONG = "MY_ENV_LONG";
    public static final String MY_ENV_SHORT = "MY_ENV_SHORT";
    public static final String MY_ENV_FLOAT = "MY_ENV_FLOAT";
    public static final String MY_ENV_BOOLEAN = "MY_ENV_BOOLEAN";
    public static final String MY_ENV_CHAR = "MY_ENV_CHAR";

    @Name(MY_ENV_STRING)
    public String myEnvString;

    @Name(MY_ENV_INT)
    public Integer myEnvInteger;
    @Name(MY_ENV_INT)
    public int myEnvInt;

    @Name(MY_ENV_SHORT)
    public Short myEnvShortBox;
    @Name(MY_ENV_SHORT)
    public short myEnvShort;

    @Name(MY_ENV_FLOAT)
    public Float myEnvFloatBox;
    @Name(MY_ENV_FLOAT)
    public float myEnvFloat;

    @Name(MY_ENV_DOUBLE)
    public double myEnvDouble;
    @Name(MY_ENV_DOUBLE)
    public Double myEnvDoubleBox;

    @Name(MY_ENV_LONG)
    public long myEnvLong;
    @Name(MY_ENV_LONG)
    public Long myEnvLongBox;

    @Name(MY_ENV_BOOLEAN)
    public boolean myEnvBoolean;
    @Name(MY_ENV_BOOLEAN)
    public Boolean myEnvBooleanBox;

    @Name(MY_ENV_CHAR)
    public char myEnvChar;
    @Name(MY_ENV_CHAR)
    public Character myEnvCharBox;

    // write custom assert using assertj
    public static class AllTypesObjectAssert extends AbstractAssert<AllTypesObjectAssert, AllTypesObject> {

        protected AllTypesObjectAssert(AllTypesObject allTypesObject, Class<?> selfType) {
            super(allTypesObject, selfType);
        }

        public static AllTypesObjectAssert assertThat(AllTypesObject actual) {
            return new AllTypesObjectAssert(actual, AllTypesObjectAssert.class);
        }

        public AllTypesObjectAssert allIsFilled() {
            isNotNull();
            objects.assertNotNull(info, actual.myEnvString);
            objects.assertNotNull(info, actual.myEnvInteger);
            objects.assertNotNull(info, actual.myEnvShortBox);
            objects.assertNotNull(info, actual.myEnvFloatBox);
            objects.assertNotNull(info, actual.myEnvDoubleBox);
            objects.assertNotNull(info, actual.myEnvCharBox);
            objects.assertNotNull(info, actual.myEnvBooleanBox);
            objects.assertNotNull(info, actual.myEnvLongBox);

            return this;
        }

        // Verify that boxed and unboxed values are the same
        public AllTypesObjectAssert sameTypeValueMatches() {
            isNotNull();
            objects.assertEqual(info, actual.myEnvInteger, actual.myEnvInt);
            objects.assertEqual(info, actual.myEnvShortBox, actual.myEnvShort);
            objects.assertEqual(info, actual.myEnvFloatBox, actual.myEnvFloat);
            objects.assertEqual(info, actual.myEnvDoubleBox, actual.myEnvDouble);
            objects.assertEqual(info, actual.myEnvLongBox, actual.myEnvLong);
            objects.assertEqual(info, actual.myEnvBooleanBox, actual.myEnvBoolean);
            objects.assertEqual(info, actual.myEnvCharBox, actual.myEnvChar);
            return this;
        }

        public AllTypesObjectAssert contentMatchesEnvironment(Map<String, Object> env) {
            isNotNull();
            objects.assertEqual(info, actual.myEnvString, env.get(MY_ENV_STRING));
            objects.assertEqual(info, actual.myEnvInteger, env.get(MY_ENV_INT));
            objects.assertEqual(info, actual.myEnvShortBox, env.get(MY_ENV_SHORT));
            objects.assertEqual(info, actual.myEnvFloatBox, env.get(MY_ENV_FLOAT));
            objects.assertEqual(info, actual.myEnvDoubleBox, env.get(MY_ENV_DOUBLE));
            objects.assertEqual(info, actual.myEnvLongBox, env.get(MY_ENV_LONG));
            objects.assertEqual(info, actual.myEnvBooleanBox, env.get(MY_ENV_BOOLEAN));
            objects.assertEqual(info, actual.myEnvCharBox, env.get(MY_ENV_CHAR));
            return this;
        }
    }


}
