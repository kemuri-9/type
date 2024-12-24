/**
 * Copyright 2022-2024 Steven Walters
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
package net.kemuri9.type.test;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

public class TestUtils {

    public static <T> void assertSameContents(T[] expected, T[] actual) {
        Assertions.assertEquals(expected.length, actual.length);
        for (int idx = 0; idx < expected.length; ++idx) {
            Assertions.assertSame(expected[idx], actual[idx]);
        }
    }

    public static void assertThrows(Class<? extends Throwable> exType, Iterable<Executable> executables) {
        int idx = -1;
        for (Executable exec : executables) {
            ++idx;
            Assertions.assertThrows(exType, exec, "Executable at index " + idx + " failed to throw " + exType);
        }
    }

    public static Method getMethod(Class<?> type, String name, Class<?>... argTypes) {
        try {
            return type.getDeclaredMethod(name, argTypes);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new AssertionFailedError("unable to find method " + type + "." + name + "(" + Arrays.toString(argTypes) + ")", ex);
        }
    }

    public static Type[] getTypes(AnnotatedType[] types) {
        Type[] ret = new Type[types.length];
        for (int idx = 0; idx < types.length; ++idx) {
            ret[idx] = types[idx].getType();
        }
        return ret;
    }

    public static int getJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        String[] parts = javaVersion.split(Pattern.quote("."));
        if (parts[0].equals("1")) {
            return Integer.parseInt(parts[1]);
        }
        return Integer.parseInt(parts[0]);
    }

    /**
     * Java 12 marks a major improvement in AnnotatedType implementations where toString, hashCode, and equals are implemented
     * @return state of the current JVM environment being Java 12 or higher.
     */
    public static boolean isJava12Plus() {
        return getJavaVersion() >= 12;
    }
}
