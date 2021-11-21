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
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.kemuri9.type.AnnotatedArrayTypeImpl;
import net.kemuri9.type.AnnotatedTypeImpl;
import net.kemuri9.type.GenericArrayTypeImpl;
import net.kemuri9.type.ParameterizedTypeImpl;

public class AnnotatedArrayTypeImplTest {

    private static class FreeAnnotatedArrayType extends FreeAnnotatedType implements AnnotatedArrayType {

        private final AnnotatedType componentType;

        public FreeAnnotatedArrayType(Type type, Annotation[] annotations, AnnotatedType componentType) {
            super(type, null, annotations);
            this.componentType = componentType;
        }

        @Override
        public AnnotatedType getAnnotatedGenericComponentType() {
            return componentType;
        }
    }

    public static void array(@Ann4("array") Number @Ann4("numbers")[] numbers) {}

    @Ann4("GAT")
    public static void gat(@Ann4("List") List<@Ann4("List PT Number") Number> @Ann4("numberLists")[] @Ann4("array")[] numberLists) {}

    @Ann4("PT")
    public static void pt(@Ann4("List") List<@Ann4("List PT Number") Number> @Ann4("numbers")[] numbers) {}

    @Ann4("TV")
    public static <@Ann4("T") T extends @Ann4("Number") Number> void tv(@Ann4("array") T @Ann4("numbers")[] numbers) {}

    // all nulls are invalid
    private static final FreeAnnotatedArrayType INVALID1 = new FreeAnnotatedArrayType(null, null, null);
    // null annotations are invalid
    private static final FreeAnnotatedArrayType INVALID2 =
            new FreeAnnotatedArrayType(String[].class, null, new AnnotatedTypeImpl(String.class));
    // containing a null annotation is invalid
    private static final FreeAnnotatedArrayType INVALID3 =
            new FreeAnnotatedArrayType(String[].class, new Annotation[] { null }, new AnnotatedTypeImpl(String.class));
    // null component type is invalid
    private static final FreeAnnotatedArrayType INVALID4 =
            new FreeAnnotatedArrayType(String[].class, new Annotation[0], null);
    // component type not matching the type's is invalid
    private static final FreeAnnotatedArrayType INVALID5 =
            new FreeAnnotatedArrayType(String[].class, new Annotation[0], new AnnotatedTypeImpl(Object.class));
    // base type is invalid
    private static final FreeAnnotatedArrayType INVALID6 =
            new FreeAnnotatedArrayType(String.class, new Annotation[0], new AnnotatedTypeImpl(String.class));

    private static final GenericArrayType INVALID_GAT = new GenericArrayType() {
        @Override
        public Type getGenericComponentType() {
            return null;
        }
    };

    public static Stream<Arguments> getTestConstructionArguments() {
        ParameterizedTypeImpl listNumber = new ParameterizedTypeImpl(null, List.class, Number.class);
        GenericArrayTypeImpl listNumbers = new GenericArrayTypeImpl(listNumber);
        Method tvMethod = TestUtils.getMethod(AnnotatedArrayTypeImplTest.class, "tv", Number[].class);
        return Stream.of(
                Arguments.of(getType("array", Number[].class), Number[].class, "numbers", "array"),
                Arguments.of(getType("gat", List[][].class), GenericArrayTypeImpl.withComponent(listNumbers), "numberLists", "array"),
                Arguments.of(getType("pt", List[].class), listNumbers, "numbers", "List"),
                Arguments.of(getType("tv", Number[].class), new GenericArrayTypeImpl(tvMethod.getTypeParameters()[0]), "numbers", "array")
            );
    }

    private static AnnotatedArrayType getType(String methodName, Class<?>... argTypes) {
        Method method = TestUtils.getMethod(AnnotatedArrayTypeImplTest.class, methodName, argTypes);
        Parameter param = method.getParameters()[0];
        // parameter annotations are on the most inner component of the GAT tree!
//        System.err.println(Arrays.toString(param.getAnnotations()));
        return (AnnotatedArrayType) param.getAnnotatedType();
    }

    private void assertEquivalence(AnnotatedArrayType original, AnnotatedArrayTypeImpl clone) {
        Assertions.assertEquals(clone, original);
        if (original instanceof AnnotatedArrayTypeImpl || TestUtils.isJava12Plus()) {
            Assertions.assertEquals(original, clone);
            Assertions.assertEquals(original.hashCode(), clone.hashCode());
            Assertions.assertEquals(original.toString(), clone.toString());
        }
    }

    @ParameterizedTest(name = "testConstructionAnnotatedType " + ParameterizedTest.DEFAULT_DISPLAY_NAME)
    @MethodSource("net.kemuri9.type.test.AnnotatedArrayTypeImplTest#getTestConstructionArguments")
    public void testConstructionAnnotatedType(AnnotatedArrayType type, Type baseType, String rootAnnValue, String compAnnValue) {
        AnnotatedArrayTypeImpl clone = new AnnotatedArrayTypeImpl(type);
        Assertions.assertNull(clone.getAnnotatedOwnerType());
        Assertions.assertEquals(clone, type);
        Ann4 ann4 = clone.getAnnotation(Ann4.class);
        Assertions.assertNotNull(ann4);
        Assertions.assertEquals(rootAnnValue, ann4.value());
        ann4 = clone.getAnnotatedGenericComponentType().getAnnotation(Ann4.class);
        Assertions.assertNotNull(ann4);
        Assertions.assertEquals(compAnnValue, ann4.value());
    }

    @ParameterizedTest(name = "testConstructionAnnotatedType " + ParameterizedTest.DEFAULT_DISPLAY_NAME)
    @MethodSource("net.kemuri9.type.test.AnnotatedArrayTypeImplTest#getTestConstructionArguments")
    public void testConstructionParameters(AnnotatedArrayType type, Type baseType, String rootAnnValue, String compAnnValue) {
        AnnotatedArrayTypeImpl manaul = new AnnotatedArrayTypeImpl(type.getType(), type.getAnnotations(), type.getAnnotatedGenericComponentType());
        Assertions.assertNull(manaul.getAnnotatedOwnerType());
        Assertions.assertEquals(manaul, type);
        Ann4 ann4 = manaul.getAnnotation(Ann4.class);
        Assertions.assertNotNull(ann4);
        Assertions.assertEquals(rootAnnValue, ann4.value());
        ann4 = manaul.getAnnotatedGenericComponentType().getAnnotation(Ann4.class);
        Assertions.assertNotNull(ann4);
        Assertions.assertEquals(compAnnValue, ann4.value());
    }

    @Test
    public void testConstructionBasic() {
        AnnotatedArrayTypeImpl a = new AnnotatedArrayTypeImpl(String[].class);
        AnnotatedArrayTypeImpl a2 = new AnnotatedArrayTypeImpl(String[].class);
        AnnotatedArrayTypeImpl b = new AnnotatedArrayTypeImpl(Object[].class);
        AnnotatedArrayTypeImpl c = new AnnotatedArrayTypeImpl(new GenericArrayTypeImpl(new ParameterizedTypeImpl(null, List.class, String.class)));
        AnnotatedArrayTypeImpl d = new AnnotatedArrayTypeImpl(Object[].class, new Ann4Impl("Test"));

        assertEquivalence(a, a2);
        Assertions.assertNotEquals(a, b);
        Assertions.assertNotEquals(a, c);
        Assertions.assertNotEquals(b, c);
        Assertions.assertNotEquals(b, d);
    }

    @Test
    public void testConstructionInvalid() {
        List<Executable> invalid = Arrays.asList(
                ()-> new AnnotatedArrayTypeImpl((AnnotatedArrayType) null),
                ()-> new AnnotatedArrayTypeImpl((GenericArrayType) null),
                ()-> new AnnotatedArrayTypeImpl((Class<?>) null),
                ()-> new AnnotatedArrayTypeImpl(String.class),
                ()-> new AnnotatedArrayTypeImpl(INVALID1),
                ()-> new AnnotatedArrayTypeImpl(INVALID2),
                ()-> new AnnotatedArrayTypeImpl(INVALID3),
                ()-> new AnnotatedArrayTypeImpl(INVALID4),
                ()-> new AnnotatedArrayTypeImpl(INVALID5),
                ()-> new AnnotatedArrayTypeImpl(INVALID6),

                ()-> new AnnotatedArrayTypeImpl(INVALID1.getType(), INVALID1.getAnnotations(), INVALID1.getAnnotatedGenericComponentType()),
                ()-> new AnnotatedArrayTypeImpl(INVALID3.getType(), INVALID3.getAnnotations(), INVALID3.getAnnotatedGenericComponentType()),
                ()-> new AnnotatedArrayTypeImpl(INVALID4.getType(), INVALID4.getAnnotations(), INVALID4.getAnnotatedGenericComponentType()),
                ()-> new AnnotatedArrayTypeImpl(INVALID5.getType(), INVALID5.getAnnotations(), INVALID5.getAnnotatedGenericComponentType()),
                ()-> new AnnotatedArrayTypeImpl(INVALID6.getType(), INVALID6.getAnnotations(), INVALID6.getAnnotatedGenericComponentType()),

                ()-> new AnnotatedArrayTypeImpl(INVALID1.getType(), INVALID1.getAnnotations(), INVALID1.getAnnotations()),
                ()-> new AnnotatedArrayTypeImpl(INVALID3.getType(), INVALID3.getAnnotations(), new Annotation[0]),
                ()-> new AnnotatedArrayTypeImpl(INVALID3.getType(), new Annotation[0], INVALID3.getAnnotations()),
                ()-> new AnnotatedArrayTypeImpl(INVALID6.getType(), INVALID6.getAnnotations(), INVALID6.getAnnotations()),

                ()-> new AnnotatedArrayTypeImpl(INVALID_GAT)
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testEquals() {
        AnnotatedArrayTypeImpl array = new AnnotatedArrayTypeImpl(String[].class);
        FreeAnnotatedType basic = new FreeAnnotatedType(String[].class, null, new Annotation[0]);
        Assertions.assertNotEquals(array, basic);
    }
}
