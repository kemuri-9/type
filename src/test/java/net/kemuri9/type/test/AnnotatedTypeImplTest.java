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
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.AnnotatedTypeImpl;

public class AnnotatedTypeImplTest {

    @Ann4("A")
    public static class A {

    }

    @Ann4("B")
    public static class B extends @Ann4("Parent") A {

    }

    public static void a(@Ann4("Param") A a) {}

    private void assertEquivalence(AnnotatedType original, AnnotatedTypeImpl clone) {
        Assertions.assertEquals(clone, original);
        if (original instanceof AnnotatedTypeImpl || TestUtils.isJava12Plus()) {
            Assertions.assertEquals(original, clone);
            Assertions.assertEquals(original.hashCode(), clone.hashCode());
            Assertions.assertEquals(original.toString(), clone.toString());
        }
    }

    @Test
    public void testConstructWithAnnotatedType() {
        AnnotatedType b = B.class.getAnnotatedSuperclass();
        AnnotatedTypeImpl bClone = new AnnotatedTypeImpl(b);
        assertEquivalence(b, bClone);
        Ann4 bAnn4 = bClone.getAnnotation(Ann4.class);
        Assertions.assertNotNull(bAnn4);
        Assertions.assertEquals(b.getAnnotation(Ann4.class), bAnn4);
        Assertions.assertEquals("Parent", bAnn4.value());

        AnnotatedType a = TestUtils.getMethod(getClass(), "a", A.class).getAnnotatedParameterTypes()[0];
        AnnotatedTypeImpl aClone = new AnnotatedTypeImpl(a);
        assertEquivalence(a, aClone);
        Ann4 aAnn4 = aClone.getAnnotation(Ann4.class);
        Assertions.assertNotNull(aAnn4);
        Assertions.assertEquals(a.getAnnotation(Ann4.class), aAnn4);
        Assertions.assertEquals("Param", aAnn4.value());
    }

    @Test
    public void testConstructWithClass() {
        AnnotatedTypeImpl one = new AnnotatedTypeImpl(Object.class);
        AnnotatedTypeImpl two = new AnnotatedTypeImpl(Object.class);
        AnnotatedTypeImpl three = new AnnotatedTypeImpl(A.class);
        assertEquivalence(one, two);
        Assertions.assertNull(one.getAnnotatedOwnerType());
        Assertions.assertNotNull(three.getAnnotatedOwnerType());
        // empty annotated type is created to match the owner type
        Assertions.assertEquals(AnnotatedTypeImplTest.class, three.getAnnotatedOwnerType().getType());
        Assertions.assertArrayEquals(new Annotation[0], three.getAnnotatedOwnerType().getAnnotations());
        Assertions.assertNotEquals(one, Object.class);
        Assertions.assertNotEquals(three, A.class);
        Assertions.assertNotEquals(one, three);
    }

    @Test
    public void testConstructWithType() {
        AnnotatedTypeImpl one = new AnnotatedTypeImpl(B.class.getAnnotatedSuperclass());
        AnnotatedTypeImpl two = new AnnotatedTypeImpl(one.getType(), one.getAnnotatedOwnerType(), one.getAnnotations());
        assertEquivalence(one, two);
        AnnotatedTypeImpl three = new AnnotatedTypeImpl(one.getType(), one.getAnnotatedOwnerType(), (Annotation[]) null);
        Assertions.assertNotEquals(one, three);
    }

    @Test
    public void testConstructionInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> new AnnotatedTypeImpl((AnnotatedType) null),
                // nulls are invalid
                ()-> new AnnotatedTypeImpl(new FreeAnnotatedType(null, null, null)),
                // null annotations are invalid
                ()-> new AnnotatedTypeImpl(new FreeAnnotatedType(Object.class, null, null)),
                // containing a null annotation is invalid
                ()-> new AnnotatedTypeImpl(new FreeAnnotatedType(Object.class, null, new Annotation[] { null })),
                // annotated owner not matching the type's is invalid
                ()-> new AnnotatedTypeImpl(new FreeAnnotatedType(Object.class, new AnnotatedTypeImpl(AnnotatedType.class), new Annotation[0])),
                ()-> new AnnotatedTypeImpl(new FreeAnnotatedType(A.class, new AnnotatedTypeImpl(AnnotatedType.class), new Annotation[0])),
                ()-> new AnnotatedTypeImpl((Class<?>) null),
                // array type is invalid
                ()-> new AnnotatedTypeImpl(String[].class),

                ()-> new AnnotatedTypeImpl(null, null, (Annotation[]) null),
                // containing a null annotation is invalid
                ()-> new AnnotatedTypeImpl(Object.class, null, new Annotation[] { null }),
                // annotated owner not matching the type's is invalid
                ()-> new AnnotatedTypeImpl(Object.class, new AnnotatedTypeImpl(AnnotatedType.class), (Annotation[]) null),
                ()-> new AnnotatedTypeImpl(FreeAnnotatedType.class, new AnnotatedTypeImpl(AnnotatedType.class), (Annotation[]) null)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testEquivalence() {
        AnnotatedType b = B.class.getAnnotatedSuperclass();
        AnnotatedType diffOwner = new AnnotatedTypeImpl(AnnotatedTypeImplTest.class, null, new Ann4Impl("Bob"));
        AnnotatedTypeImpl bDiff = new AnnotatedTypeImpl(b.getType(), diffOwner, b.getAnnotations());
        AnnotatedTypeImpl b2 = new AnnotatedTypeImpl(b);
        Assertions.assertEquals(b2, b);
        Assertions.assertNotEquals(bDiff, b2);
    }
}
