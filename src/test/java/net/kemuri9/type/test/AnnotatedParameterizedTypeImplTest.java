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
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.AnnotatedParameterizedTypeImpl;
import net.kemuri9.type.AnnotatedTypeImpl;
import net.kemuri9.type.AnnotatedTypeVariableImpl;
import net.kemuri9.type.ParameterizedTypeImpl;

public class AnnotatedParameterizedTypeImplTest {

    private static final class FreeAnnotatedParameterizedType extends FreeAnnotatedType implements AnnotatedParameterizedType {

        private final AnnotatedType[] typeArgs;

        public FreeAnnotatedParameterizedType(Type type, AnnotatedType ownerType, Annotation[] annotations, AnnotatedType[] typeArgs) {
            super(type, ownerType, annotations);
            this.typeArgs = typeArgs;
        }

        @Override
        public AnnotatedType[] getAnnotatedActualTypeArguments() {
            return typeArgs;
        }
    }

    public static <T extends Number> void one(@Ann4("list") List<@Ann4("Number") T> list) {}

    public static void two(@Ann4("Collection") Collection<Number> collection) {}

    private static AnnotatedParameterizedType getType(String name, Class<?>... argTypes) {
        AnnotatedType type = TestUtils.getMethod(AnnotatedParameterizedTypeImplTest.class, name, argTypes).getAnnotatedParameterTypes()[0];
        return (AnnotatedParameterizedType) type;
    }

    private void assertEquivalence(AnnotatedParameterizedType original, AnnotatedParameterizedTypeImpl clone) {
        Assertions.assertEquals(clone, original);
        if (original instanceof AnnotatedParameterizedTypeImpl || TestUtils.isJava12Plus()) {
            Assertions.assertEquals(original, clone);
            Assertions.assertEquals(original.hashCode(), clone.hashCode());
            Assertions.assertEquals(original.toString(), clone.toString());
        }
    }

    @Test
    public void testConstructionAnnotatedType() {
        AnnotatedParameterizedType one = getType("one", List.class);
        AnnotatedParameterizedTypeImpl oneClone = new AnnotatedParameterizedTypeImpl(one);
        assertEquivalence(one, oneClone);
        Method oneMethod = TestUtils.getMethod(getClass(), "one", List.class);
        AnnotatedParameterizedTypeImpl oneManual = new AnnotatedParameterizedTypeImpl(
                new ParameterizedTypeImpl(null, List.class, oneMethod.getTypeParameters()),
                null, new Annotation[] { new Ann4Impl("list") },
                new Annotation[][] { new Annotation[] { new Ann4Impl("Number") } });
        assertEquivalence(oneClone, oneManual);
        AnnotatedParameterizedTypeImpl oneManual2 = new AnnotatedParameterizedTypeImpl(
                new ParameterizedTypeImpl(null, List.class, oneMethod.getTypeParameters()),
                null, new Annotation[] { new Ann4Impl("list") },
                new AnnotatedTypeVariableImpl(oneMethod.getTypeParameters()[0], new Ann4Impl("Number")));
        assertEquivalence(oneManual, oneManual2);

        AnnotatedParameterizedType two = getType("two", Collection.class);
        AnnotatedParameterizedTypeImpl twoClone = new AnnotatedParameterizedTypeImpl(two);
        assertEquivalence(two, twoClone);
        AnnotatedParameterizedTypeImpl twoManual = new AnnotatedParameterizedTypeImpl(
                new ParameterizedTypeImpl(null, Collection.class, Number.class),
                new Ann4Impl("Collection"));
        assertEquivalence(twoClone, twoManual);
    }

    @Test
    public void testConstructionBasic() {
        ParameterizedType one = (ParameterizedType) getType("one", List.class).getType();
        AnnotatedParameterizedTypeImpl oneAnn = new AnnotatedParameterizedTypeImpl(one);
        Assertions.assertEquals(one, oneAnn.getType());
        Assertions.assertNull(oneAnn.getAnnotatedOwnerType());
        Assertions.assertArrayEquals(new Annotation[0], oneAnn.getAnnotations());
        Assertions.assertArrayEquals(one.getActualTypeArguments(), TestUtils.getTypes(oneAnn.getAnnotatedActualTypeArguments()));
        Assertions.assertArrayEquals(new Annotation[0], oneAnn.getAnnotatedActualTypeArguments()[0].getAnnotations());
    }

    @Test
    public void testConstructionInvalid() {
        ParameterizedType listString = new ParameterizedTypeImpl(null, List.class, String.class);
        ParameterizedType owned = new ParameterizedTypeImpl(AnnotatedParameterizedTypeImpl.class, List.class, String.class);
        List<Executable> invalid = Arrays.asList(
                ()-> new AnnotatedParameterizedTypeImpl((AnnotatedParameterizedType) null),
                ()-> new AnnotatedParameterizedTypeImpl((ParameterizedType) null),
                ()-> new AnnotatedParameterizedTypeImpl(FreeParameterizedType.INVALID1),
                ()-> new AnnotatedParameterizedTypeImpl(FreeParameterizedType.INVALID2),
                ()-> new AnnotatedParameterizedTypeImpl(FreeParameterizedType.INVALID3),
                ()-> new AnnotatedParameterizedTypeImpl(FreeParameterizedType.INVALID4),
                // not a ParameterizedType
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(String.class, null, new Annotation[0], null)),
                // invalid type
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(FreeParameterizedType.INVALID1, null, new Annotation[0], null)),
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(FreeParameterizedType.INVALID2, null, new Annotation[0], null)),
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(FreeParameterizedType.INVALID3, null, new Annotation[0], null)),
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(FreeParameterizedType.INVALID4, null, new Annotation[0], null)),
                // null annotations
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(listString, null, null, null)),
                // null annotation
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(listString, null, new Annotation[] { null }, null)),
                // null type args
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(listString, null, new Annotation[0], null)),
                // null type arg
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(listString, null, new Annotation[0], new AnnotatedType[] { null })),
                // mismatched type arg
                ()-> new AnnotatedParameterizedTypeImpl(new FreeAnnotatedParameterizedType(listString, null, new Annotation[0], new AnnotatedType[] { new AnnotatedTypeImpl(CharSequence.class) })),
                // null annotation
                ()-> new AnnotatedParameterizedTypeImpl(listString, new Annotation[] { null }),
                // mismatched owner
                ()-> new AnnotatedParameterizedTypeImpl(owned, new AnnotatedTypeImpl(Object.class)),
                // null type arg annotation
                ()-> new AnnotatedParameterizedTypeImpl(listString, null, null, new Annotation[][] { new Annotation[] { null } }),
                // mismatched type arg
                ()-> new AnnotatedParameterizedTypeImpl(listString, null, null, new AnnotatedTypeImpl(Object.class))
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testTypeArgumentsMutable() {
        ParameterizedType one = (ParameterizedType) getType("one", List.class).getType();
        AnnotatedParameterizedTypeImpl oneAnn = new AnnotatedParameterizedTypeImpl(one);
        Assertions.assertNotSame(oneAnn.getAnnotatedActualTypeArguments(), oneAnn.getAnnotatedActualTypeArguments());

        ParameterizedType two = new ParameterizedTypeImpl(null, Object.class);
        AnnotatedParameterizedTypeImpl twoAnn = new AnnotatedParameterizedTypeImpl(two);
        Assertions.assertSame(twoAnn.getAnnotatedActualTypeArguments(), twoAnn.getAnnotatedActualTypeArguments());
    }

    @Test
    public void testEquivalence() {
        ParameterizedType listString = new ParameterizedTypeImpl(null, List.class, String.class);
        AnnotatedParameterizedTypeImpl ann1 = new AnnotatedParameterizedTypeImpl(listString);
        FreeAnnotatedType free = new FreeAnnotatedType(listString, null, new Annotation[0]);
        Assertions.assertNotEquals(ann1, free);

        AnnotatedParameterizedTypeImpl ann2 = new AnnotatedParameterizedTypeImpl(listString, new Ann4Impl("Test"));
        Assertions.assertNotEquals(ann1, ann2);
    }
}
