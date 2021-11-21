/**
 * Copyright 2022 Steven Walters
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kemuri9.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Internal utilities
 */
class Utils {

    static final Type[] EMPTY = new Type[0];

    /**
     * Utility function for converting an annotation array to string
     * @param annotations annotations to toString
     * @param state of adding a prefix space.
     *  {@code FALSE} indicates to add a postfix space.
     *  {@code null} indicates to add no additional space.
     * @return toString representation of the annotations
     */
    static final String annsToString(Annotation[] annotations, Boolean prefix) {
        if (annotations.length == 0) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (Annotation ann : annotations) {
            joiner.add(ann.toString());
        }
        String str = joiner.toString();
        return (prefix == null) ? str : (prefix ? (" " + str) : (str + " "));
    }

    static final String annTypesToString(AnnotatedType[] types, String delimiter, String prefix, String suffix) {
        if (types.length == 0) {
            return "";
        }
        StringJoiner sj = new StringJoiner(delimiter, prefix, suffix);
        for (AnnotatedType type : types) {
            sj.add(type.toString());
        }
        return sj.toString();
    }

    @SuppressWarnings("unchecked")
    static <T> T cast(Object value) {
        return (T) value;
    }

    /**
     * Perform a check of the incoming array parameters and clone.
     * @param <T> Type of array component
     * @param array array to check and clone
     * @param name name of the array to check
     * @return checked {@code array}
     */
    static <T> T[] checkedClone(T[] array, String name) {
        return Utils.clone(Utils.noNullContained(array, name));
    }

    /**
     * Perform a check of the incoming array parameters and clone.
     *  But if the incoming array is {@code null} then utilize a default value
     * @param <T> Type of array component
     * @param array array to check and clone
     * @param name name of the array to check
     * @param defaultValue default value to utilize if the array is {@code null}
     * @return checked {@code array} or {@code defaultValue}
     */
    static <T> T[] checkedClone(T[] array, String name, T[] defaultValue) {
        if (array == null) {
            return defaultValue;
        }
        return Utils.clone(Utils.noNullContained(array, name));
    }

    /**
     * Verify that the type defined by the annotated type matches the expected plain type
     * @param <T> type of {@link AnnotatedType}
     * @param expected {@link Type} that the annotated type should represent
     * @param annotated {@link AnnotatedType} to check
     * @return verified {@link AnnotatedType}
     * @throws IllegalArgumentException When {@code annotated} does not represent {@code expected}
     */
    static <T extends AnnotatedType> T checkMatching(Type expected, T annotated) {
        isTrue(expected.equals(annotated.getType()), annotated,
                (t)-> t + "'s type does not match expected type " + expected);
        return annotated;
    }

    /**
     * Verify that the types defined by the annotated types matches the expected plain types
     * @param <T> type of {@link AnnotatedType}
     * @param expected {@link Type}s that the annotated types should represent
     * @param annotated {@link AnnotatedType}s to check
     * @return verified {@link AnnotatedType}s
     * @throws IllegalArgumentException When {@code annotated} does not represent {@code expected}
     */
    static <T extends AnnotatedType> T[] checkMatching(Type[] expected, T[] annotated) {
        if (expected.length != annotated.length) {
            throw new IllegalArgumentException("there are " + annotated.length +
                    " annotated types, when there should be " + expected.length);
        }
        for (int idx = 0; idx < expected.length; ++idx) {
            checkMatching(expected[idx], annotated[idx]);
        }
        return annotated;
    }

    /**
     * Specialization of array clone to handle {@code null} and empty cases more seamlessly
     * @param <T> type of array component
     * @param array array to clone
     * @return array with the exact same contents as the input array
     */
    static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            // empty arrays do not need to be cloned as they are effectively immutable
            return array;
        }
        return array.clone();
    }

    static <T> T defaultValue(T value, T defaultValue) {
        return (value == null) ? defaultValue : value;
    }

    static <T> T get(T[] array, int idx) {
        return (array == null || array.length <= idx) ? null : array[idx];
    }

    static <T> T get(Object[] args, int idx, Class<T> type, T defValue) {
        if (args.length <= idx) {
            return defValue;
        }
        if (type.isArray() && type.equals(args.getClass()) && idx == 0) {
            return cast(args);
        }
        Object val = args[idx];
        return type.isInstance(val) ? type.cast(val) : defValue;
    }

    static Type getOwnerType(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).getEnclosingClass();
        } else if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getOwnerType();
        }
        return null;
    }

    /**
     * Perform the hash of several values
     * @param base base value to utilize for the hash
     * @param multiplier multiplier to utilize for the hashing of individual values
     * @param values values to hash
     * @return generated hash code
     */
    static int hash(int base, int multiplier, Object... values) {
        int hash = base;
        for (Object value : values) {
            int valHash = hashCode(value);
            hash += valHash * multiplier;
        }
        return hash;
    }

    /**
     * Perform a basic hash code, handling array types
     * @param value value to generate the hash code for
     * @return hash code of the value.
     */
    static int hashCode(Object value) {
        if (value == null) {
            return 0;
        } else if (value instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) value);
        } else if (value instanceof byte[]) {
            return Arrays.hashCode((byte[]) value);
        } else if (value instanceof char[]) {
            return Arrays.hashCode((char[]) value);
        } else if (value instanceof double[]) {
            return Arrays.hashCode((double[]) value);
        } else if (value instanceof float[]) {
            return Arrays.hashCode((float[]) value);
        } else if (value instanceof int[]) {
            return Arrays.hashCode((int[]) value);
        } else if (value instanceof long[]) {
            return Arrays.hashCode((long[]) value);
        } else if (value instanceof Object[]) {
            return Arrays.hashCode((Object[]) value);
        } else if (value instanceof short[]) {
            return Arrays.hashCode((short[]) value);
        } else {
            return value.hashCode();
        }
    }

    static boolean isArray(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).isArray();
        }
        return type instanceof GenericArrayType;
    }

    /**
     * Perform a basic equals check, handling array types
     * @param left left-side comparison object
     * @param right right-side comparison object
     * @return state of {@code left} being equivalent to {@code right}
     */
    static boolean isBasicEquals(Object left, Object right) {
        if (left.getClass() != right.getClass()) {
            return false;
        } else if (left instanceof boolean[]) {
            return Arrays.equals((boolean[]) left, (boolean[]) right);
        } else if (left instanceof byte[]) {
            return Arrays.equals((byte[]) left, (byte[]) right);
        } else if (left instanceof char[]) {
            return Arrays.equals((char[]) left, (char[]) right);
        } else if (left instanceof double[]) {
            return Arrays.equals((double[]) left, (double[]) right);
        } else if (left instanceof float[]) {
            return Arrays.equals((float[]) left, (float[]) right);
        } else if (left instanceof int[]) {
            return Arrays.equals((int[]) left, (int[]) right);
        } else if (left instanceof long[]) {
            return Arrays.equals((long[]) left, (long[]) right);
        } else if (left instanceof Object[]) {
            return Arrays.equals((Object[]) left, (Object[]) right);
        } else if (left instanceof short[]) {
            return Arrays.equals((short[]) left, (short[]) right);
        } else {
            return Objects.equals(left, right);
        }
    }

    static <T> void isTrue(boolean truth, T value, Function<T, String> error) {
        if (!truth) {
            throw new IllegalArgumentException(error.apply(value));
        }
    }

    /**
     * Validate that the object array is not null, and contains no null values
     * @param <T> Type of object held by the array
     * @param value object array to validate
     * @param name name of the parameter being validated
     * @return validated object array
     * @throws IllegalArgumentException When {@code value} is {@code null} or contains a {@code null}
     */
    static <T> T[] noNullContained(T[] value, String name) {
        notNull(value, name);
        for (int idx = 0; idx < value.length; idx++) {
            if (value[idx] == null) {
                throw new IllegalArgumentException(name + "[" + idx + "] is null");
            }
        }
        return value;
    }

    /**
     * Validate that the object array is not null, contains no nulls, and is not empty
     * @param <T> Type of object held by the array
     * @param value object array to validate
     * @param name name of the parameter being validated
     * @return validated object array
     * @throws IllegalArgumentException <ul>
     * <li>When {@code value} is {@code null}</li>
     * <li>When {@code value} contains a {@code null}</li>
     * <li>When {@code value} is empty</li></ul>
     */
    static <T> T[] notEmpty(T[] value, String name) {
        notNull(value, name);
        noNullContained(value, name);
        if (value.length == 0) {
            throw new IllegalArgumentException(name + " is empty");
        }
        return value;
    }

    /**
     * Validate that an object is not null.
     * @param <T> type of object
     * @param value value to validate
     * @param name name of the parameter being validated
     * @return validated object
     * @throws IllegalArgumentException When {@code value} is {@code null}
     */
    static <T> T notNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        return value;
    }

    private Utils() {
        // not instantiable
    }
}
