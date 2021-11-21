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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.kemuri9.type.AnnotationImpl;

public class AnnotationImplTest {

    @SuppressWarnings("all")
    private static class Ann2Impl2 extends AnnotationImpl implements Ann2 {

        private static final long serialVersionUID = -2724086738521973766L;

        public Ann2Impl2() {
            super(Ann2.class);
        }

        @Override
        public boolean boolVal() {
            return true;
        }

        @Override
        public byte byteVal() {
            return 34;
        }

        @Override
        public char charVal() {
            return 't';
        }

        @Override
        public Class<?> classVal() {
            return null;
        }

        @Override
        public double doubleVal() {
            return Double.NaN;
        }

        @Override
        public Enum1 enumVal() {
            return null;
        }

        @Override
        public float floatVal() {
            return Float.MIN_VALUE;
        }

        @Override
        public int intVal() {
            return 79098700;
        }

        @Override
        public long longVal() {
            return 80808450580845l;
        }

        @Override
        public short shortVal() {
            return 25509;
        }

        @Override
        public String stringVal() {
            return null;
        }
    }

    public static final Annotation INVALID = new Annotation() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    };

    @Ann1
    public static void ann1() {}

    @Ann2(boolVal = true, byteVal = 34, charVal = 't', classVal = StringBuilder.class,
            doubleVal = Double.NaN, enumVal = Enum1.VALUE3, floatVal = Float.MIN_VALUE,
            intVal = 79098700, longVal = 80808450580845l, shortVal = 25509, stringVal = "booo\"hoo")
    public static void ann21() {}

    @Ann3(annVals = {@Ann4("one"), @Ann4("two"), @Ann4("\"five\"")},
            booleanVals = {true, true, false},
            byteVals = {3, 5, 7, 9, 11},
            charVals = {'a', 'd', 'e', 'z'},
            classVals = {Integer.class, Long.class, Number.class, StringBuilder.class, boolean[].class, String[][].class},
            doubleVals = {Double.NaN, Math.PI},
            enumVals = {Enum1.VALUE1, Enum1.VALUE3, Enum1.VALUE2},
            floatVals = {1.0f, 2.0f, 3.0f, 4.0f},
            intVals = {16816168, 214861861, 1687468137},
            longVals = {900808007700L, 907304720489450L, 830087978070L},
            shortVals = {400, 500, 600},
            stringVals = {"one", "seven", "eleven"})
    public static void ann31() {}

    public static void assertEquals(Annotation copy, Annotation original) {
        // self equivalent
        Assertions.assertEquals(copy, copy);
        // copy equals the original
        Assertions.assertEquals(copy, original);
        // original equals the copy
        Assertions.assertEquals(original, copy);
        // annotation types are equivalent
        Assertions.assertEquals(copy.annotationType(), original.annotationType());
    }

    public static void testStrings(Annotation left, Annotation right) {
        /* by declaring the annotations in the same order as the alphabetic order,
         * then the to strings will match. if not in the same order then ka-boom.
         * Do not declare in any non alphabetic order as this gets into a mess of parsing annotation strings
         * to determine if it's an appropriate alternate ordering or not */
        Assertions.assertEquals(left.toString(), right.toString());
    }

    @Test
    public void testAnn1() throws NoSuchMethodException, SecurityException {
        Ann1 ann = getClass().getDeclaredMethod("ann1").getAnnotation(Ann1.class);
        Assertions.assertNotNull(ann);
        AnnotationImpl clone = new Ann1Impl();
        assertEquals(clone, ann);
        testStrings(ann, clone);
        Assertions.assertEquals(ann.hashCode(), clone.hashCode());
    }

    @Test
    public void testAnn1_2() throws NoSuchMethodException, SecurityException {
        // for annotations without attributes, the basic implementation may be used with some limitations
        Ann1 ann = getClass().getDeclaredMethod("ann1").getAnnotation(Ann1.class);
        Assertions.assertNotNull(ann);
        AnnotationImpl clone = new AnnotationImpl(Ann1.class);
        // in such cases the clone will be equivalent to the original
        Assertions.assertEquals(clone, ann);
        // but the original will not be equivalent to the clone
        Assertions.assertNotEquals(ann, clone);
        testStrings(ann, clone);
        Assertions.assertEquals(ann.hashCode(), clone.hashCode());
    }

    @Test
    public void testAnn2() throws NoSuchMethodException, SecurityException {
        Ann2 ann = getClass().getDeclaredMethod("ann21").getAnnotation(Ann2.class);
        Assertions.assertNotNull(ann);
        Ann2Impl clone = new Ann2Impl(ann);
        assertEquals(clone, ann);
        testStrings(ann, clone);
        Assertions.assertEquals(ann.hashCode(), clone.hashCode());
    }

    @Test
    public void testAnn3() throws NoSuchMethodException, SecurityException {
        Ann3 ann = getClass().getDeclaredMethod("ann31").getAnnotation(Ann3.class);
        Assertions.assertNotNull(ann);
        Ann3Impl clone = new Ann3Impl(ann);
        assertEquals(clone, ann);
        testStrings(ann, clone);
        Assertions.assertEquals(ann.hashCode(), clone.hashCode());
    }

    @Test
    public void testInequivalence() throws NoSuchMethodException, SecurityException {
        Ann1 ann1 = getClass().getDeclaredMethod("ann1").getAnnotation(Ann1.class);
        Ann2 ann2 = getClass().getDeclaredMethod("ann21").getAnnotation(Ann2.class);
        AnnotationImpl clone1 = new Ann1Impl();
        AnnotationImpl clone2 = new Ann2Impl(ann2);
        AnnotationImpl ann2_2 = new Ann2Impl(false, (byte) 0, String.class, 'c', 5, Enum1.VALUE3, 5f, 5l, 5, (short) 5, "s");
        Assertions.assertNotEquals(clone1, clone2);
        Assertions.assertNotEquals(clone2, ann1);
        Assertions.assertNotEquals(clone2, ann2_2);
        Assertions.assertNotEquals(clone1, "5");
        Assertions.assertNotEquals(clone1, INVALID);
        Ann2 ann2Invalid = new Ann2Impl2();
        Assertions.assertNotEquals(clone2, ann2Invalid);
        Assertions.assertNotEquals(ann2Invalid, clone2);
        // nulls are invalid values in an annotation, so encountering a null anywhere counts as an inequality
        Assertions.assertNotEquals(ann2Invalid, new Ann2Impl2());
    }

    @Test
    public void testInvalidConstruction() {
        // a null annotation type is invalid
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new AnnotationImpl(null));
    }
}
