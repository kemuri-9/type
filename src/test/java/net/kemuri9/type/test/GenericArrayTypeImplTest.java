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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.kemuri9.type.GenericArrayTypeImpl;
import net.kemuri9.type.ParameterizedTypeImpl;

public class GenericArrayTypeImplTest {

    public static final GenericArrayType INVALID = new GenericArrayType() {
        @Override
        public Type getGenericComponentType() {
            return null;
        }
    };

    public static void gat(List<Number>[][] numberLists) {}

    public static void pt(List<Number>[] numbers) {}

    public static <T extends Number> void tv(T[] numbers) {}

    public static void wc(List<?> numbers) {}

    public static void assertEquivalence(GenericArrayType original, GenericArrayType clone) {
        Assertions.assertEquals(original, clone);
        Assertions.assertEquals(clone, original);
        Assertions.assertEquals(original.getGenericComponentType(), clone.getGenericComponentType());
        Assertions.assertEquals(original.hashCode(), clone.hashCode());
        Assertions.assertEquals(original.toString(), clone.toString());
    }

    @Test
    public void testEquivalence() {
        // self equivalent
        GenericArrayTypeImpl gat1 = new GenericArrayTypeImpl(new ParameterizedTypeImpl(null, List.class, Number.class));
        Assertions.assertEquals(gat1, gat1);

        // different components are not equivalent
        GenericArrayTypeImpl gat2 = new GenericArrayTypeImpl(new ParameterizedTypeImpl(null, List.class, Long.class));
        Assertions.assertNotEquals(gat1, gat2);

        // different types are not equivalent
        Assertions.assertNotEquals(gat1, gat1.getGenericComponentType());
    }

    @Test
    public void testGenericArrayType() {
        // can construct and utilize when the component itself is a GenericArrayType
        Method gat = TestUtils.getMethod(getClass(), "gat", List[][].class);
        Type type = gat.getGenericParameterTypes()[0];
        Assertions.assertTrue(type instanceof GenericArrayType);
        GenericArrayType arrayType = (GenericArrayType) type;
        GenericArrayTypeImpl clone = new GenericArrayTypeImpl(arrayType);
        assertEquivalence(arrayType, clone);
        GenericArrayTypeImpl copy = new GenericArrayTypeImpl(arrayType.getGenericComponentType());
        assertEquivalence(arrayType, copy);
        GenericArrayTypeImpl manual = new GenericArrayTypeImpl(
                (Type) new GenericArrayTypeImpl(new ParameterizedTypeImpl(null, List.class, Number.class)));
        assertEquivalence(arrayType, manual);
        Assertions.assertEquals("java.util.List<java.lang.Number>[][]", clone.toString());
    }

    @Test
    public void testParameterizedType1() {
        // can construct and utilize when the component is a ParameterizedType
        Method pt = TestUtils.getMethod(getClass(), "pt", List[].class);
        Type type = pt.getGenericParameterTypes()[0];
        Assertions.assertTrue(type instanceof GenericArrayType);
        GenericArrayType arrayType = (GenericArrayType) type;
        GenericArrayTypeImpl clone = new GenericArrayTypeImpl(arrayType);
        assertEquivalence(arrayType, clone);
        GenericArrayTypeImpl copy = new GenericArrayTypeImpl(arrayType.getGenericComponentType());
        assertEquivalence(arrayType, copy);
        GenericArrayTypeImpl manual = new GenericArrayTypeImpl(new ParameterizedTypeImpl(null, List.class, Number.class));
        assertEquivalence(arrayType, manual);
        Assertions.assertEquals("java.util.List<java.lang.Number>[]", clone.toString());
    }

    @Test
    public void testTypeVariable() {
        // can construct and utilize when the component is a TypeVariable
        Method tv = TestUtils.getMethod(getClass(), "tv", Number[].class);
        Type type = tv.getGenericParameterTypes()[0];
        Assertions.assertTrue(type instanceof GenericArrayType);
        GenericArrayType arrayType = (GenericArrayType) type;
        GenericArrayTypeImpl clone = new GenericArrayTypeImpl(arrayType);
        assertEquivalence(arrayType, clone);
        GenericArrayTypeImpl copy = new GenericArrayTypeImpl(arrayType.getGenericComponentType());
        assertEquivalence(arrayType, copy);
        GenericArrayTypeImpl manual = new GenericArrayTypeImpl(tv.getTypeParameters()[0]);
        assertEquivalence(arrayType, manual);
        Assertions.assertEquals("T[]", clone.toString());
    }

    @Test
    public void testInvalidConstruction1() {
        // cannot copy construct from null
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new GenericArrayTypeImpl((GenericArrayType) null));
        // cannot copy construct when component is null
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new GenericArrayTypeImpl(INVALID));
    }

    @Test
    public void testInvalidConstruction2() {
        // component of null is invalid
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new GenericArrayTypeImpl((Type) null));

        // component of plain class is invalid
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new GenericArrayTypeImpl(String.class));

        // component of wildcard type is invalid
        Method wc = TestUtils.getMethod(getClass(), "wc", List.class);
        Type wcType = ParameterizedType.class.cast(wc.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new GenericArrayTypeImpl(wcType));
    }
}
