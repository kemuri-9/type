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
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.ParameterizedTypeImpl;

public class ParameterizedTypeImplTest {

    public static interface Generic<T> {
        public static interface Inner<T> {}
    }

    public static class Generic2<T> {
        public class Inner<U> {}
    }

    public static <T> Collection<T> col1(Collection<T> col) {
        return col;
    }

    public static <T> Generic<T> gen1(Generic<T> gen) {
        return gen;
    }

    public static <T> Generic.Inner<T> gen2(Generic.Inner<T> inner) {
        return inner;
    }

    public static <T, U> Generic2<T>.Inner<U> gen3(Generic2<T>.Inner<U> inner) {
        return inner;
    }

    public static <T extends Number> void list1(List<T> list) {}

    public static List<Collection<Long>> list2() {
        return null;
    }

    public void testEquivalence(ParameterizedType original, ParameterizedTypeImpl clone) {
        // equivalence should be mutual
        Assertions.assertEquals(original, clone);
        Assertions.assertEquals(clone, original);
        // details should be equivalent
        Assertions.assertEquals(original.getOwnerType(), clone.getOwnerType());
        Assertions.assertEquals(original.getRawType(), clone.getRawType());
        Assertions.assertArrayEquals(original.getActualTypeArguments(), clone.getActualTypeArguments());

        // hash code and string should be equal
        Assertions.assertEquals(original.hashCode(), clone.hashCode());
        Assertions.assertEquals(original.toString(), clone.toString());
    }

    @Test
    public void testCollection1() throws NoSuchMethodException {
        Method method = TestUtils.getMethod(getClass(), "col1", Collection.class);
        Type param1Raw = method.getGenericParameterTypes()[0];
        Assertions.assertTrue(param1Raw instanceof ParameterizedType);
        ParameterizedType param1 = (ParameterizedType) param1Raw;
        ParameterizedTypeImpl clone = new ParameterizedTypeImpl(param1);
        testEquivalence(param1, clone);
        ParameterizedTypeImpl rawCopy = new ParameterizedTypeImpl(null, Collection.class, method.getTypeParameters());
        testEquivalence(param1, rawCopy);
    }

    @Test
    public void testConstructionInvalid() {
        List<Executable> invalid = Arrays.asList(
                // cannot copy construct from null
                ()-> new ParameterizedTypeImpl((ParameterizedType) null),
                ()-> new ParameterizedTypeImpl(FreeParameterizedType.INVALID1),
                ()-> new ParameterizedTypeImpl(FreeParameterizedType.INVALID2),
                ()-> new ParameterizedTypeImpl(FreeParameterizedType.INVALID3),
                ()-> new ParameterizedTypeImpl(FreeParameterizedType.INVALID4),
                // invalid details
                ()-> new ParameterizedTypeImpl(null, null, (Type[]) null),
                // null raw type
                ()-> new ParameterizedTypeImpl(null, null, new Type[] { Object.class }),
                // null type args
                ()-> new ParameterizedTypeImpl(null, List.class, (Type[]) null),
                // null type arg
                ()-> new ParameterizedTypeImpl(null, List.class, new Type[] { null })
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testGen1() {
        Method method = TestUtils.getMethod(getClass(), "gen1", Generic.class);
        Type param1Raw = method.getGenericParameterTypes()[0];
        Assertions.assertTrue(param1Raw instanceof ParameterizedType);
        ParameterizedType param1 = (ParameterizedType) param1Raw;
        ParameterizedTypeImpl clone = new ParameterizedTypeImpl(param1);
        testEquivalence(param1, clone);
        ParameterizedTypeImpl rawCopy = new ParameterizedTypeImpl(getClass(), Generic.class, method.getTypeParameters());
        testEquivalence(param1, rawCopy);
    }

    @Test
    public void testGen2() {
        Method method = TestUtils.getMethod(getClass(), "gen2", Generic.Inner.class);
        Type param1Raw = method.getGenericParameterTypes()[0];
        Assertions.assertTrue(param1Raw instanceof ParameterizedType);
        ParameterizedType param1 = (ParameterizedType) param1Raw;
        ParameterizedTypeImpl clone = new ParameterizedTypeImpl(param1);
        testEquivalence(param1, clone);
        ParameterizedTypeImpl rawCopy = new ParameterizedTypeImpl(Generic.class, Generic.Inner.class, method.getTypeParameters());
        testEquivalence(param1, rawCopy);
    }

    @Test
    public void testGen3() {
        Method method = TestUtils.getMethod(getClass(), "gen3", Generic2.Inner.class);
        Type param1Raw = method.getGenericParameterTypes()[0];
        TypeVariable<Method>[] methodTypeVars = method.getTypeParameters();
        Assertions.assertTrue(param1Raw instanceof ParameterizedType);
        ParameterizedType param1 = (ParameterizedType) param1Raw;
        ParameterizedTypeImpl clone = new ParameterizedTypeImpl(param1);
        testEquivalence(param1, clone);
        ParameterizedTypeImpl owner = new ParameterizedTypeImpl(new ParameterizedTypeImpl(getClass(), Generic2.class, methodTypeVars[0]));
        ParameterizedTypeImpl rawCopy = new ParameterizedTypeImpl(owner, Generic2.Inner.class, methodTypeVars[1]);
        testEquivalence(param1, rawCopy);
    }

    @Test
    public void testEquivalence() {
        ParameterizedTypeImpl pt1 = new ParameterizedTypeImpl(null, List.class, Number.class);
        ParameterizedTypeImpl pt2 = new ParameterizedTypeImpl(null, List.class, Long.class);
        ParameterizedTypeImpl pt3 = new ParameterizedTypeImpl(null, Collection.class, Number.class);
        ParameterizedTypeImpl pt4 = new ParameterizedTypeImpl(getClass(), List.class, Number.class);

        List<ParameterizedTypeImpl> pts = Arrays.asList(pt1, pt2, pt3, pt4);
        for (ParameterizedTypeImpl left : pts) {
            // should always equal itself
            Assertions.assertEquals(left, left);
            // should not be equivalent to its owner type
            Assertions.assertNotEquals(left, left.getOwnerType());
            // should not be equivalent to its raw type
            Assertions.assertNotEquals(left, left.getRawType());
            // should not be equivalent to its type args
            Assertions.assertNotEquals(left, left.getActualTypeArguments());
            for (ParameterizedTypeImpl right : pts) {
                if (left == right) {
                    continue;
                }
                // should not be equivalent to different types
                Assertions.assertNotEquals(left, right);
                Assertions.assertNotEquals(right, left);
            }
        }
    }

    @Test
    public void testList1() {
        Method method = TestUtils.getMethod(getClass(), "list1", List.class);
        Type param1Raw = method.getGenericParameterTypes()[0];
        Assertions.assertTrue(param1Raw instanceof ParameterizedType);
        ParameterizedType param1 = (ParameterizedType) param1Raw;
        ParameterizedTypeImpl clone = new ParameterizedTypeImpl(param1);
        testEquivalence(param1, clone);
        ParameterizedTypeImpl rawCopy = new ParameterizedTypeImpl(null, List.class, method.getTypeParameters());
        testEquivalence(param1, rawCopy);
    }

    @Test
    public void testList2() {
        Method method = TestUtils.getMethod(getClass(), "list2");
        Type param1Raw = method.getGenericReturnType();
        Assertions.assertTrue(param1Raw instanceof ParameterizedType);
        ParameterizedType param1 = (ParameterizedType) param1Raw;
        ParameterizedTypeImpl clone = new ParameterizedTypeImpl(param1);
        testEquivalence(param1, clone);
        ParameterizedTypeImpl inner = new ParameterizedTypeImpl(null, Collection.class, Long.class);
        ParameterizedTypeImpl rawCopy = new ParameterizedTypeImpl(null, List.class, inner);
        testEquivalence(param1, rawCopy);
    }

    @Test
    public void testTypeArgumentsMutable() {
        ParameterizedType type1 = new ParameterizedTypeImpl(null, Collection.class, Long.class);
        Assertions.assertNotSame(type1.getActualTypeArguments(), type1.getActualTypeArguments());

        ParameterizedType type2 = new ParameterizedTypeImpl(null, Object.class);
        Assertions.assertSame(type2.getActualTypeArguments(), type2.getActualTypeArguments());
    }
}
