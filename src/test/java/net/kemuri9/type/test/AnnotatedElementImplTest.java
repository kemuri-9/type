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
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.kemuri9.type.AnnotatedElementImpl;

public class AnnotatedElementImplTest {

    private static class FreeAnnotatedElement extends AnnotatedElementImpl {

        private final Annotation[] annotations;

        public FreeAnnotatedElement(Annotation[] annotations) {
            super();
            this.annotations = annotations;
        }

        @Override
        public Annotation[] getAnnotations() {
            return annotations;
        }
    };

    public static Stream<Arguments> getElements() {
        return Stream.of(
                Arguments.of(Anns.class, Ann1.class),
                Arguments.of(TestUtils.getMethod(Anns.class, "ann1"), Ann1.class),
                Arguments.of(TestUtils.getMethod(Anns.class, "ann1"), Ann2.class),
                Arguments.of(TestUtils.getMethod(Anns.class, "ann21"), Ann2.class),
                Arguments.of(TestUtils.getMethod(Anns.class, "ann31"), Ann3.class),
                Arguments.of(TestUtils.getMethod(Anns.class, "ann31"), Ann2.class)
                );
    }

    private static void assertEquivalence(AnnotatedElement original, AnnotatedElementImpl clone) {
        // for most cases here equivalence is uni-directional as AnnotatedElementImpl is selective in its comparison
        Assertions.assertEquals(clone, original);
        Assertions.assertArrayEquals(original.getAnnotations(), clone.getAnnotations());
        Assertions.assertArrayEquals(original.getDeclaredAnnotations(), clone.getDeclaredAnnotations());
        if (original instanceof AnnotatedElementImpl) {
            Assertions.assertEquals(original.hashCode(), clone.hashCode());
            Assertions.assertEquals(original, clone);
        }
    }

    @ParameterizedTest(name = "testConstruction " + ParameterizedTest.DEFAULT_DISPLAY_NAME)
    @MethodSource(value = "net.kemuri9.type.test.AnnotatedElementImplTest#getElements")
    public void testConstruction(AnnotatedElement element, Class<? extends Annotation> annType) {
        AnnotatedElementImpl clone = new AnnotatedElementImpl(element);
        assertEquivalence(element, clone);
        AnnotatedElementImpl clone2 = new AnnotatedElementImpl(element);
        assertEquivalence(clone, clone2);
    }

    @Test
    public void testConstructionEmpty() {
        AnnotatedElementImpl empty = new AnnotatedElementImpl();
        Assertions.assertArrayEquals(new Annotation[0], empty.getAnnotations());
        Assertions.assertArrayEquals(new Annotation[0], empty.getDeclaredAnnotations());

        empty = new AnnotatedElementImpl((Annotation[]) null);
        Assertions.assertArrayEquals(new Annotation[0], empty.getAnnotations());
        Assertions.assertArrayEquals(new Annotation[0], empty.getDeclaredAnnotations());
    }

    @Test
    public void testConstructionInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> new AnnotatedElementImpl((AnnotatedElement) null),
                ()-> new AnnotatedElementImpl(new FreeAnnotatedElement(new Annotation[] { null })),
                ()-> new AnnotatedElementImpl(new Annotation[] { null })
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testEquivalence() {
        AnnotatedElementImpl one = new AnnotatedElementImpl(Anns.class);
        AnnotatedElementImpl two = new AnnotatedElementImpl(TestUtils.getMethod(Anns.class, "ann1"));
        AnnotatedElementImpl three = new AnnotatedElementImpl(new Ann1Impl());
        AnnotatedElementImpl four = new AnnotatedElementImpl(TestUtils.getMethod(Anns.class, "ann21"));
        // self equivalent
        Assertions.assertEquals(one, one);
        // equivalent to others when annotations are the same
        Assertions.assertEquals(two, one);
        Assertions.assertEquals(one, two);
        // equivalent to others when annotations are the same
        Assertions.assertEquals(three, one);
        Assertions.assertEquals(one, three);
        // not equivalent to others when annotations are different
        Assertions.assertNotEquals(four, one);
        Assertions.assertNotEquals(one, four);
        // not equivalent to different types
        Assertions.assertNotEquals(one, one.getAnnotation(Ann1.class));
    }

    @ParameterizedTest(name = "testGetAnnotation " + ParameterizedTest.DEFAULT_DISPLAY_NAME)
    @MethodSource(value = "net.kemuri9.type.test.AnnotatedElementImplTest#getElements")
    public void testGetAnnotation(AnnotatedElement element, Class<? extends Annotation> annType) {
        AnnotatedElementImpl clone = new AnnotatedElementImpl(element);
        Annotation origAnn = element.getAnnotation(annType);
        Annotation cloneAnn = clone.getAnnotation(annType);
        Assertions.assertSame(origAnn, cloneAnn);
        origAnn = element.getDeclaredAnnotation(annType);
        cloneAnn = clone.getDeclaredAnnotation(annType);
        Assertions.assertSame(origAnn, cloneAnn);
    }

    @ParameterizedTest(name = "testGetAnnotationsByType " + ParameterizedTest.DEFAULT_DISPLAY_NAME)
    @MethodSource(value = "net.kemuri9.type.test.AnnotatedElementImplTest#getElements")
    public void testGetAnnotationsByType(AnnotatedElement element, Class<? extends Annotation> annType) {
        AnnotatedElementImpl clone = new AnnotatedElementImpl(element);
        Annotation[] origAnn = element.getAnnotationsByType(annType);
        Annotation[] cloneAnn = clone.getAnnotationsByType(annType);
        Assertions.assertArrayEquals(origAnn, cloneAnn);
        origAnn = element.getDeclaredAnnotationsByType(annType);
        cloneAnn = clone.getDeclaredAnnotationsByType(annType);
        Assertions.assertArrayEquals(origAnn, cloneAnn);
    }

    @ParameterizedTest(name = "testIsAnnotationPresent " + ParameterizedTest.DEFAULT_DISPLAY_NAME)
    @MethodSource(value = "net.kemuri9.type.test.AnnotatedElementImplTest#getElements")
    public void testIsAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annType) {
        AnnotatedElementImpl clone = new AnnotatedElementImpl(element);
        Assertions.assertEquals(element.isAnnotationPresent(annType), clone.isAnnotationPresent(annType));
    }

    @Test
    public void testAnnotationsMutable() {
        // when there are annotations, then clones are always returned to allow for mutation by the caller
        AnnotatedElementImpl test = new AnnotatedElementImpl(new Ann4Impl("Test"));
        Assertions.assertNotSame(test.getAnnotations(), test.getAnnotations());

        // but when there are no annotations, then it is not a clone, because an empty array is immutable in nature.
        AnnotatedElementImpl empty = new AnnotatedElementImpl();
        Assertions.assertSame(empty.getAnnotations(), empty.getAnnotations());
    }
}
