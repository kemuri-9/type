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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.kemuri9.type.AnnotatedArrayTypeImpl;
import net.kemuri9.type.AnnotatedParameterizedTypeImpl;
import net.kemuri9.type.AnnotatedTypeFactory;
import net.kemuri9.type.AnnotatedTypeImpl;
import net.kemuri9.type.AnnotatedTypeVariableImpl;
import net.kemuri9.type.AnnotatedWildcardTypeImpl;
import net.kemuri9.type.ParameterizedTypeImpl;
import net.kemuri9.type.WildcardTypeImpl;

public class AnnotatedTypeFactoryTest {

    public static class AnnotatedTypeFactoryDerived extends AnnotatedTypeFactory {}

    public static void arr(@Ann4("List") List<@Ann4("WC") ?> @Ann4("Array") [] arr) {}
    public static void at(@Ann4("CS") CharSequence cs) {}
    public static void pt(@Ann4("List") List<@Ann4("WC") ? extends @Ann4("CS") CharSequence> list) {}
    public static <T extends CharSequence> void tv(@Ann4("TV") T t) {}
    public static void wc(@Ann4("List") List<@Ann4("WC") ? super @Ann4("CS") CharSequence> list) {}

    private static final List<AnnotatedType> TYPES;

    static {
        AnnotatedType arr = getType("arr", List[].class);
        AnnotatedType basic = getType("at", CharSequence.class);
        AnnotatedType pt = getType("pt", List.class);
        AnnotatedType tv = getType("tv", CharSequence.class);
        AnnotatedType wc = ((AnnotatedParameterizedType) pt).getAnnotatedActualTypeArguments()[0];
        AnnotatedType pt2 = getType("wc", List.class);
        AnnotatedType wc2 = ((AnnotatedParameterizedType) pt2).getAnnotatedActualTypeArguments()[0];
        TYPES = Collections.unmodifiableList(Arrays.asList(arr, basic, pt, tv, wc, wc2));
    }

    private static Annotation[][] getAnnotations(AnnotatedType[] types) {
        Annotation[][] ret = new Annotation[types.length][];
        for (int idx = 0; idx < types.length; ++idx) {
            ret[idx] = types[idx].getAnnotations();
        }
        return ret;
    }

    private static AnnotatedType getType(String name, Class<?>... args) {
        Method method = TestUtils.getMethod(AnnotatedTypeFactoryTest.class, name, args);
        return method.getAnnotatedParameterTypes()[0];
    }

    public static Stream<Arguments> getTypes() {
        return TYPES.stream().map(Arguments::of);
    }

    @Test
    public void testCanDerive() {
        Assertions.assertDoesNotThrow(()-> new AnnotatedTypeFactoryDerived());
    }

    @ParameterizedTest
    @MethodSource("net.kemuri9.type.test.AnnotatedTypeFactoryTest#getTypes")
    public void testNewAnnotatedTypeFromAnnotatedType(AnnotatedType type) {
        AnnotatedType clone = AnnotatedTypeFactory.newAnnotatedType(type);
        Assertions.assertEquals(clone, type);
        Assertions.assertNotEquals(clone.getClass(), type.getClass());

        AnnotatedType clone2 = AnnotatedTypeFactory.newAnnotatedType(clone);
        Assertions.assertEquals(clone2, clone);
        Assertions.assertNotSame(clone, clone2);
    }

    @Test
    public void testNewAnnotatedTypeFromAnnotatedTypeInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> AnnotatedTypeFactory.newAnnotatedType(null),
                ()-> AnnotatedTypeFactory.newAnnotatedType(new FreeAnnotatedType(null, null, new Annotation[0]))
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @ParameterizedTest
    @MethodSource("net.kemuri9.type.test.AnnotatedTypeFactoryTest#getTypes")
    public void testNewAnnotatedTypeFromNullExtra(AnnotatedType type) {
        AnnotatedType ret = AnnotatedTypeFactory.newAnnotatedType(type.getType(), type.getAnnotations(), (Object[]) null);
        Assertions.assertSame(type.getType(), ret.getType());
    }

    @Test
    public void testNewAnnotatedTypeFromTypeArray() {
        AnnotatedArrayType annType = (AnnotatedArrayType) TYPES.get(0);
        AnnotatedArrayType ret1 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                annType.getAnnotatedGenericComponentType());
        Assertions.assertEquals(AnnotatedArrayTypeImpl.class, ret1.getClass());
        Assertions.assertEquals(ret1, annType);
        // due to the complex component type (List<?>), using only the annotations loses information on the inner typing
        AnnotatedArrayType ret2 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) annType.getAnnotatedGenericComponentType().getAnnotations());
        Assertions.assertNotEquals(ret2, annType);
        Assertions.assertEquals(new AnnotatedParameterizedTypeImpl(new ParameterizedTypeImpl(null, List.class,
                WildcardTypeImpl.FULL_WILDCARD), new Ann4Impl("List")), ret2.getAnnotatedGenericComponentType());
        AnnotatedArrayType ret3 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object) annType.getAnnotatedGenericComponentType().getAnnotations());
        Assertions.assertEquals(ret2, ret3);
    }

    @Test
    public void testNewAnnotatedTypeFromTypeBasic() {
        AnnotatedType annType = TYPES.get(1);
        AnnotatedType ret1 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations());
        Assertions.assertEquals(AnnotatedTypeImpl.class, ret1.getClass());
        Assertions.assertEquals(ret1, annType);
    }

    @Test
    public void testNewAnnotatedTypeFromTypeInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, ()-> AnnotatedTypeFactory.newAnnotatedType((Type) null));
    }

    @Test
    public void testNewAnnotatedTypeFromTypeParameterizedType() {
        AnnotatedParameterizedType annType = (AnnotatedParameterizedType) TYPES.get(2);
        // without owner specified, because no owner is required
        AnnotatedParameterizedType ret1 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) annType.getAnnotatedActualTypeArguments());
        Assertions.assertEquals(AnnotatedParameterizedTypeImpl.class, ret1.getClass());
        Assertions.assertEquals(ret1, annType);
        // with owner specified
        AnnotatedParameterizedType ret2 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                null, annType.getAnnotatedActualTypeArguments());
        Assertions.assertEquals(ret2, annType);

        // without owner specified, because no owner is required
        AnnotatedParameterizedType ret3 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object) annType.getAnnotatedActualTypeArguments());
        Assertions.assertEquals(ret3, annType);

        // due to the complex inner typing, List<? extends CS> some information is lost just using annotations
        AnnotatedParameterizedType ret4 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) getAnnotations(annType.getAnnotatedActualTypeArguments()));
        Assertions.assertNotEquals(ret4, annType);
        Assertions.assertEquals(new AnnotatedWildcardTypeImpl(WildcardTypeImpl.forExtends(CharSequence.class),
                new Annotation[] { new Ann4Impl("WC") }), ret4.getAnnotatedActualTypeArguments()[0]);

        // with owner specified
        AnnotatedParameterizedType ret5 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                null, getAnnotations(annType.getAnnotatedActualTypeArguments()));
        Assertions.assertEquals(ret4, ret5);

        // without owner specified, because no owner is required
        AnnotatedParameterizedType ret6 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object) getAnnotations(annType.getAnnotatedActualTypeArguments()));
        Assertions.assertEquals(ret4, ret6);
    }

    @Test
    public void testNewAnnotatedTypeFromTypeTypeVariable() {
        AnnotatedTypeVariable annType = (AnnotatedTypeVariable) TYPES.get(3);
        AnnotatedTypeVariable ret1 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) annType.getAnnotatedBounds());
        Assertions.assertEquals(AnnotatedTypeVariableImpl.class, ret1.getClass());
        Assertions.assertEquals(ret1, annType);

        AnnotatedTypeVariable ret2 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object) annType.getAnnotatedBounds());
        Assertions.assertEquals(ret2, annType);

        AnnotatedTypeVariable ret3 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) getAnnotations(annType.getAnnotatedBounds()));
        Assertions.assertEquals(ret3, annType);

        AnnotatedTypeVariable ret4 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object) getAnnotations(annType.getAnnotatedBounds()));
        Assertions.assertEquals(ret4, annType);
    }

    @Test
    public void testNewAnnotatedTypeFromTypeUnknown() {
        Assertions.assertThrows(UnsupportedOperationException.class, ()-> AnnotatedTypeFactory.newAnnotatedType(new UnknownType()));
    }

    @Test
    public void testNewAnnotatedTypeFromTypeWildcardExtends() {
        AnnotatedWildcardType annType = (AnnotatedWildcardType) TYPES.get(4);
        AnnotatedWildcardType ret1 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                annType.getAnnotatedLowerBounds(), annType.getAnnotatedUpperBounds());
        Assertions.assertEquals(AnnotatedWildcardTypeImpl.class, ret1.getClass());
        Assertions.assertEquals(ret1, annType);

        AnnotatedWildcardType ret2 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                getAnnotations(annType.getAnnotatedLowerBounds()), getAnnotations(annType.getAnnotatedUpperBounds()));
        Assertions.assertEquals(ret2, annType);
    }

    @Test
    public void testNewAnnotatedTypeFromTypeWildcardSuper() {
        AnnotatedWildcardType annType = (AnnotatedWildcardType) TYPES.get(5);
        AnnotatedWildcardType ret1 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                annType.getAnnotatedLowerBounds(), annType.getAnnotatedUpperBounds());
        Assertions.assertEquals(AnnotatedWildcardTypeImpl.class, ret1.getClass());
        Assertions.assertEquals(ret1, annType);

        // only passing a single array is valid for supers, because the upper bounds has no annotations
        AnnotatedWildcardType ret2 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) annType.getAnnotatedLowerBounds());
        Assertions.assertEquals(ret2, annType);

        AnnotatedWildcardType ret3 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                getAnnotations(annType.getAnnotatedLowerBounds()), getAnnotations(annType.getAnnotatedUpperBounds()));
        Assertions.assertEquals(ret3, annType);

        // only passing a single array is valid for supers, because the upper bounds has no annotations
        AnnotatedWildcardType ret4 = AnnotatedTypeFactory.newAnnotatedType(annType.getType(), annType.getAnnotations(),
                (Object[]) getAnnotations(annType.getAnnotatedLowerBounds()));
        Assertions.assertEquals(ret4, annType);
    }

    @ParameterizedTest
    @MethodSource("net.kemuri9.type.test.AnnotatedTypeFactoryTest#getTypes")
    public void testRecreateTypeForEquals(AnnotatedType type) {
        AnnotatedType forEquals = AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(type);
        if (TestUtils.isJava12Plus()) {
            Assertions.assertSame(type, forEquals);
            return;
        }
        Assertions.assertNotSame(type, forEquals);
        Assertions.assertEquals(forEquals, type);
        Assertions.assertNotEquals(type, forEquals);

        AnnotatedType forEquals2 = AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(forEquals);
        Assertions.assertSame(forEquals, forEquals2);
    }

    @Test
    public void testRecreateTypeForEqualsInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, ()-> AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(null));
    }

    @Test
    public void testRecreateTypesForEquals() {
        AnnotatedType[] types = TYPES.toArray(new AnnotatedType[TYPES.size()]);
        AnnotatedType[] forEquals = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(types);
        Assertions.assertNotSame(types, forEquals);
        if (TestUtils.isJava12Plus()) {
            TestUtils.assertSameContents(types, forEquals);
            return;
        }
        Assertions.assertArrayEquals(forEquals, types);
        AnnotatedType[] forEquals2 = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(forEquals);
        Assertions.assertNotSame(forEquals, forEquals2);
        TestUtils.assertSameContents(forEquals, forEquals2);
    }

    @Test
    public void testRecreateTypesForEqualsEmpty() {
        AnnotatedType[] empty = new AnnotatedType[0];
        AnnotatedType[] forEquals = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(empty);
        Assertions.assertSame(empty, forEquals);
    }

    @Test
    public void testRecreateTypesForEqualsInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> AnnotatedTypeFactory.recreateAnnotatedTypesForEquals((AnnotatedType[]) null),
                ()-> AnnotatedTypeFactory.recreateAnnotatedTypesForEquals((AnnotatedType) null)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }
}
