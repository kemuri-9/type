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
package net.kemuri9.type.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.AnnotatedTypeImpl;
import net.kemuri9.type.AnnotatedWildcardTypeImpl;
import net.kemuri9.type.WildcardTypeImpl;

public class AnnotatedWildcardTypeImplTest {


    public static class FreeAnnotatedWildcardType extends FreeAnnotatedType implements AnnotatedWildcardType {

        private final AnnotatedType[] lowerBounds;
        private final AnnotatedType[] upperBounds;
        public FreeAnnotatedWildcardType(Type type, Annotation[] annotations,
                AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds) {
            super(type, null, annotations);
            this.lowerBounds = lowerBounds;
            this.upperBounds = upperBounds;
        }

        @Override
        public AnnotatedType[] getAnnotatedLowerBounds() {
            return lowerBounds;
        }

        @Override
        public AnnotatedType[] getAnnotatedUpperBounds() {
            return upperBounds;
        }

    }
    private static void assertEquivalence(AnnotatedWildcardType original, AnnotatedWildcardTypeImpl clone) {
        assertEquivalence(original, clone, true);
    }

    private static void assertEquivalence(AnnotatedWildcardType original, AnnotatedWildcardTypeImpl clone, boolean checkHash) {
        Assertions.assertEquals(clone, original);
        if (original instanceof AnnotatedWildcardTypeImpl || TestUtils.isJava12Plus()) {
            Assertions.assertEquals(original, clone);
            if (checkHash) {
                Assertions.assertEquals(original.hashCode(), clone.hashCode());
            }
            Assertions.assertEquals(original.toString(), clone.toString());
        }
    }

    private static final AnnotatedType[] EMPTY_TYPES = new AnnotatedType[0];
    private static final AnnotatedType[] PLAIN_OBJECT = new AnnotatedType[] { new AnnotatedTypeImpl(Object.class) };

    public static void wc1(@Ann4("List") List<@Ann4("WC") ?> list) {}
    public static void wc2(List<@Ann4("WC") ? extends @Ann4("CS") CharSequence> list) {}
    public static void wc3(List<@Ann4("WC") ? super @Ann4("CS") CharSequence> list) {}

    private static AnnotatedWildcardType getWc(String name, Class<?>... argTypes) {
        Method method = TestUtils.getMethod(AnnotatedWildcardTypeImplTest.class, name, argTypes);
        AnnotatedParameterizedType pt = (AnnotatedParameterizedType) method.getAnnotatedParameterTypes()[0];
        return (AnnotatedWildcardType) pt.getAnnotatedActualTypeArguments()[0];
    }

    @Test
    public void testConstruction1() {
        AnnotatedWildcardType wc = getWc("wc1", List.class);
        AnnotatedWildcardTypeImpl wcClone = new AnnotatedWildcardTypeImpl(wc);
        assertEquivalence(wc, wcClone);
        AnnotatedWildcardTypeImpl wcManual = new AnnotatedWildcardTypeImpl(wcClone.getType(), new Ann4Impl("WC"));
        assertEquivalence(wcClone, wcManual);
    }

    @Test
    public void testConstruction2() {
        AnnotatedWildcardType wc = getWc("wc2", List.class);
        AnnotatedWildcardTypeImpl wcClone = new AnnotatedWildcardTypeImpl(wc);
        assertEquivalence(wc, wcClone);
        AnnotatedWildcardTypeImpl wcManual1 = new AnnotatedWildcardTypeImpl(wcClone.getType(), new Annotation[] { new Ann4Impl("WC") },
                null, new Annotation[][] { new Annotation[] { new Ann4Impl("CS") }} );
        assertEquivalence(wcClone, wcManual1);
        AnnotatedWildcardTypeImpl wcManual2 = new AnnotatedWildcardTypeImpl(wcClone.getType(), new Annotation[] { new Ann4Impl("WC") },
                null, new AnnotatedType[] { new AnnotatedTypeImpl(CharSequence.class, null, new Ann4Impl("CS")) });
        assertEquivalence(wcClone, wcManual2);
    }

    @Test
    public void testConstruction3() {
        AnnotatedWildcardType wc = getWc("wc3", List.class);
        AnnotatedWildcardTypeImpl wcClone = new AnnotatedWildcardTypeImpl(wc);
        assertEquivalence(wc, wcClone);
        AnnotatedWildcardTypeImpl wcManual1 = new AnnotatedWildcardTypeImpl(wcClone.getType(), new Annotation[] { new Ann4Impl("WC") },
                new Annotation[][] { new Annotation[] { new Ann4Impl("CS") }}, null);
        assertEquivalence(wcClone, wcManual1, TestUtils.getJavaVersion() != 8);
        AnnotatedWildcardTypeImpl wcManual2 = new AnnotatedWildcardTypeImpl(wcClone.getType(), new Annotation[] { new Ann4Impl("WC") },
                new AnnotatedType[] { new AnnotatedTypeImpl(CharSequence.class, null, new Ann4Impl("CS")) }, null);
        assertEquivalence(wcClone, wcManual2, TestUtils.getJavaVersion() != 8);
    }

    @Test
    public void testConstructionBasic() {
        WildcardType wc1 = WildcardTypeImpl.forExtends(CharSequence.class, Iterable.class);
        AnnotatedWildcardTypeImpl annWc1 = new AnnotatedWildcardTypeImpl(wc1);
        Assertions.assertEquals(0, annWc1.getAnnotations().length);
        Assertions.assertArrayEquals(new AnnotatedType[] { new AnnotatedTypeImpl(CharSequence.class), new AnnotatedTypeImpl(Iterable.class) }, annWc1.getAnnotatedUpperBounds());

    }

    @Test
    public void testConstructionInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> new AnnotatedWildcardTypeImpl((AnnotatedWildcardType) null),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(Object.class, new Annotation[0], EMPTY_TYPES, EMPTY_TYPES)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(FreeWildcardType.INVALID1, new Annotation[0], EMPTY_TYPES, EMPTY_TYPES)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(FreeWildcardType.INVALID2, new Annotation[0], EMPTY_TYPES, EMPTY_TYPES)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(FreeWildcardType.INVALID3, new Annotation[0], EMPTY_TYPES, EMPTY_TYPES)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(FreeWildcardType.INVALID4, new Annotation[0], EMPTY_TYPES, EMPTY_TYPES)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(WildcardTypeImpl.FULL_WILDCARD, null, EMPTY_TYPES, PLAIN_OBJECT)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(WildcardTypeImpl.FULL_WILDCARD, new Annotation[0], EMPTY_TYPES, EMPTY_TYPES)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(WildcardTypeImpl.FULL_WILDCARD, new Annotation[0],
                        EMPTY_TYPES, new AnnotatedType[] { new AnnotatedTypeImpl(String.class) })),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(WildcardTypeImpl.forSuper(CharSequence.class),
                        new Annotation[0], EMPTY_TYPES, PLAIN_OBJECT)),
                ()-> new AnnotatedWildcardTypeImpl(new FreeAnnotatedWildcardType(WildcardTypeImpl.forSuper(CharSequence.class),
                        new Annotation[0], new AnnotatedType[] { new AnnotatedTypeImpl(String.class) }, PLAIN_OBJECT)),

                ()-> new AnnotatedWildcardTypeImpl((WildcardType) null),
                ()-> new AnnotatedWildcardTypeImpl(FreeWildcardType.INVALID1),
                ()-> new AnnotatedWildcardTypeImpl(FreeWildcardType.INVALID2),
                ()-> new AnnotatedWildcardTypeImpl(FreeWildcardType.INVALID3),
                ()-> new AnnotatedWildcardTypeImpl(FreeWildcardType.INVALID4),
                ()-> new AnnotatedWildcardTypeImpl(WildcardTypeImpl.FULL_WILDCARD, (Annotation) null),
                ()-> new AnnotatedWildcardTypeImpl(WildcardTypeImpl.FULL_WILDCARD, new Annotation[0], new Annotation[][] {},
                        new Annotation[][] { new Annotation[] { null }}),
                ()-> new AnnotatedWildcardTypeImpl(WildcardTypeImpl.forSuper(CharSequence.class), new Annotation[0],
                        new Annotation[][] { new Annotation[] { null }}, new Annotation[][] {}),
                ()-> new AnnotatedWildcardTypeImpl(WildcardTypeImpl.FULL_WILDCARD, new Annotation[0], EMPTY_TYPES,
                        new AnnotatedType[] { new AnnotatedTypeImpl(String.class) }),
                ()-> new AnnotatedWildcardTypeImpl(WildcardTypeImpl.forSuper(CharSequence.class), new Annotation[0],
                        new AnnotatedType[] { new AnnotatedTypeImpl(String.class) }, PLAIN_OBJECT)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testEquivalence() {
        AnnotatedWildcardType wc1 = getWc("wc1", List.class);
        AnnotatedWildcardType wc2 = getWc("wc2", List.class);
        AnnotatedWildcardType wc3 = getWc("wc3", List.class);
        AnnotatedWildcardTypeImpl wc1Clone = new AnnotatedWildcardTypeImpl(wc1);
        Assertions.assertEquals(wc1Clone, wc1Clone);
        Assertions.assertNotEquals(wc1Clone, wc1Clone.getType());
        Assertions.assertNotEquals(wc1Clone, wc2);

        AnnotatedWildcardTypeImpl wc1Diff = new AnnotatedWildcardTypeImpl(wc1Clone.getType(), new Ann4Impl("WC2"));
        Assertions.assertNotEquals(wc1Clone, wc1Diff);
        FreeAnnotatedType annType = new FreeAnnotatedType(wc1.getType(), null, wc1.getAnnotations());
        Assertions.assertNotEquals(wc1Clone, annType);
        Assertions.assertNotEquals(wc1Clone, wc3);
        AnnotatedWildcardType diff = new AnnotatedWildcardTypeImpl(wc1Clone.getType(), new Annotation[] { new Ann4Impl("WC") },
                null, new AnnotatedType[] { new AnnotatedTypeImpl(Object.class, null, new Ann4Impl("Object")) });
        Assertions.assertNotEquals(wc1Clone, diff);
        AnnotatedWildcardTypeImpl wc3Clone = new AnnotatedWildcardTypeImpl(wc3);
        diff = new AnnotatedWildcardTypeImpl(wc3Clone.getType(), new Annotation[] { new Ann4Impl("WC") },
                new AnnotatedType[] { new AnnotatedTypeImpl(CharSequence.class, null, new Ann4Impl("CS2")) }, null);
        Assertions.assertNotEquals(wc3Clone, diff);
    }
}
