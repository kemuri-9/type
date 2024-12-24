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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.WildcardTypeImpl;

public class WildcardTypeImplTest {

    public static void wcFull(List<?> values) {}

    public static void wcOne(List<? extends CharSequence> values, Consumer<? super CharSequence> action) {}

    public static void assertEquivalence(WildcardType original, WildcardType clone) {
        Assertions.assertEquals(original, clone);
        Assertions.assertEquals(clone, original);
        Assertions.assertArrayEquals(original.getLowerBounds(), clone.getLowerBounds());
        Assertions.assertArrayEquals(original.getUpperBounds(), clone.getUpperBounds());
        Assertions.assertEquals(original.hashCode(), clone.hashCode());
        Assertions.assertEquals(original.toString(), clone.toString());
    }

    @Test
    public void testInvalidConstruction() {
        List<Executable> invalid = Arrays.asList(
                ()-> new WildcardTypeImpl(null),
                ()-> new WildcardTypeImpl(FreeWildcardType.INVALID2),
                ()-> new WildcardTypeImpl(FreeWildcardType.INVALID4),
                ()-> new WildcardTypeImpl(FreeWildcardType.INVALID5),

                ()-> new WildcardTypeImpl(new Type[] { null }, new Type[] { CharSequence.class }),
                ()-> new WildcardTypeImpl(new Type[] { CharSequence.class }, new Type[] { null }),
                // cannot construct with both extends and super bindings
                ()-> new WildcardTypeImpl(new Type[] { CharSequence.class }, new Type[] { CharSequence.class }),
                ()-> new WildcardTypeImpl(new Type[] { CharSequence.class }, new Type[] { Iterable.class, CharSequence.class })
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testEquivalence() {
        WildcardTypeImpl ext1 = new WildcardTypeImpl(null, new Type[] { Object.class });
        // self equivalent
        Assertions.assertEquals(ext1, ext1);
        // full wildcard is simply extends object
        Assertions.assertEquals(ext1, WildcardTypeImpl.FULL_WILDCARD);
        // extending from the type does not equate to equivalence to it
        Assertions.assertNotEquals(ext1, Object.class);
        WildcardTypeImpl ext2 = WildcardTypeImpl.forExtends(CharSequence.class);
        Assertions.assertNotEquals(ext1, ext2);

        // super and extends are different, so they are not equivalent
        WildcardTypeImpl super1 = WildcardTypeImpl.forSuper(CharSequence.class);
        Assertions.assertNotEquals(ext2, super1);
    }

    @Test
    public void testForExtendsFullWC() {
        // when using for extends and providing just Object, then the Full wildcard instance is returned
        WildcardTypeImpl fullWC = WildcardTypeImpl.forExtends(Object.class);
        Assertions.assertSame(WildcardTypeImpl.FULL_WILDCARD, fullWC);
    }

    @Test
    public void testForExtendsInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> WildcardTypeImpl.forExtends((Type[]) null),
                ()-> WildcardTypeImpl.forExtends(),
                ()-> WildcardTypeImpl.forExtends((Type) null),
                ()-> WildcardTypeImpl.forExtends(CharSequence.class, null),
                ()-> WildcardTypeImpl.forExtends(null, CharSequence.class)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testForSuperInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> WildcardTypeImpl.forSuper((Type[]) null),
                ()-> WildcardTypeImpl.forSuper(),
                ()-> WildcardTypeImpl.forSuper((Type) null),
                ()-> WildcardTypeImpl.forSuper(CharSequence.class, null),
                ()-> WildcardTypeImpl.forSuper(null, CharSequence.class)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testFull() {
        Method method = TestUtils.getMethod(getClass(), "wcFull", List.class);
        ParameterizedType pt = ParameterizedType.class.cast(method.getGenericParameterTypes()[0]);
        WildcardType wc = (WildcardType) pt.getActualTypeArguments()[0];
        WildcardTypeImpl clone = new WildcardTypeImpl(wc);
        assertEquivalence(wc, clone);
        Assertions.assertEquals("?", clone.toString());
        assertEquivalence(wc, WildcardTypeImpl.FULL_WILDCARD);
    }

    @Test
    public void testOneExtends() {
        Method method = TestUtils.getMethod(getClass(), "wcOne", List.class, Consumer.class);
        ParameterizedType pt = ParameterizedType.class.cast(method.getGenericParameterTypes()[0]);
        WildcardType wc = (WildcardType) pt.getActualTypeArguments()[0];
        WildcardTypeImpl clone = new WildcardTypeImpl(wc);
        assertEquivalence(wc, clone);
        Assertions.assertEquals("? extends java.lang.CharSequence", clone.toString());
        WildcardTypeImpl manual = WildcardTypeImpl.forExtends(CharSequence.class);
        assertEquivalence(wc, manual);
    }

    @Test
    public void testOneSuper() {
        Method method = TestUtils.getMethod(getClass(), "wcOne", List.class, Consumer.class);
        ParameterizedType pt = ParameterizedType.class.cast(method.getGenericParameterTypes()[1]);
        WildcardType wc = (WildcardType) pt.getActualTypeArguments()[0];
        WildcardTypeImpl clone = new WildcardTypeImpl(wc);
        assertEquivalence(wc, clone);
        Assertions.assertEquals("? super java.lang.CharSequence", clone.toString());
        WildcardTypeImpl manual = WildcardTypeImpl.forSuper(CharSequence.class);
        assertEquivalence(wc, manual);
    }

    @Test
    public void testMultiExtends() {
        /* though the basic compiler does not support creating wildcards with multiple extends, the language supports it.
         * the basic compiler DOES support creating type variables with multiple extends however! */
        WildcardTypeImpl multiExtends = WildcardTypeImpl.forExtends(Iterable.class, CharSequence.class);
        Assertions.assertEquals("? extends java.lang.Iterable & java.lang.CharSequence", multiExtends.toString());
    }

    @Test
    public void testMultiExtendsInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> WildcardTypeImpl.forExtends((Type[]) null),
                ()-> WildcardTypeImpl.forExtends((Type) null),
                ()-> WildcardTypeImpl.forExtends(Iterable.class, null),
                ()-> WildcardTypeImpl.forExtends(null, Iterable.class)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testMultiSuper() {
        /* though the basic compiler does not support creating wildcards with multiple supers, the language supports it.
         * the basic compiler DOES support creating type variables with multiple supers however! */
        WildcardTypeImpl multiSuper = WildcardTypeImpl.forSuper(Number.class, CharSequence.class);
        Assertions.assertEquals("? super java.lang.Number & java.lang.CharSequence", multiSuper.toString());
    }

    @Test
    public void testMultiSuperInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> WildcardTypeImpl.forSuper((Type[]) null),
                ()-> WildcardTypeImpl.forSuper((Type) null),
                ()-> WildcardTypeImpl.forSuper(Iterable.class, null),
                ()-> WildcardTypeImpl.forSuper(null, Iterable.class)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testBoundsMutable() {
        WildcardType type1 = WildcardTypeImpl.forExtends(CharSequence.class);
        Assertions.assertNotSame(type1.getUpperBounds(), type1.getUpperBounds());
        Assertions.assertSame(type1.getLowerBounds(), type1.getLowerBounds());

        WildcardType type2 = WildcardTypeImpl.forSuper(CharSequence.class);
        Assertions.assertNotSame(type2.getUpperBounds(), type2.getUpperBounds());
        Assertions.assertNotSame(type2.getLowerBounds(), type2.getLowerBounds());
    }
}
