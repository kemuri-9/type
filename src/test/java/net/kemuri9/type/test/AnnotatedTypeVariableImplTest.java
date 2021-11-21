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
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.AnnotatedTypeImpl;
import net.kemuri9.type.AnnotatedTypeVariableImpl;

public class AnnotatedTypeVariableImplTest {

    public static class FreeAnnotatedTypeVariable extends FreeAnnotatedType implements AnnotatedTypeVariable {

        private final AnnotatedType[] annotatedBounds;

        public FreeAnnotatedTypeVariable(Type type, Annotation[] annotations, AnnotatedType... annotatedBounds) {
            super(type, null, annotations);
            this.annotatedBounds = annotatedBounds;
        }

        @Override
        public AnnotatedType[] getAnnotatedBounds() {
            return annotatedBounds;
        }
    }

    public static <@Ann4("T") T> void tv1(@Ann4("tv1") T t) {}
    public static <@Ann4("T") T extends CharSequence> void tv2(@Ann4("tv2") T t) {}

    private static AnnotatedTypeVariable getATv(String name, Class<?>... argTypes) {
        Method method = TestUtils.getMethod(AnnotatedTypeVariableImplTest.class, name, argTypes);
        return (AnnotatedTypeVariable) method.getAnnotatedParameterTypes()[0];
    }

    private static TypeVariable<Method> getTv(String name, Class<?>... argTypes) {
        Method method = TestUtils.getMethod(AnnotatedTypeVariableImplTest.class, name, argTypes);
        return method.getTypeParameters()[0];
    }

    private static void assertEquivalence(AnnotatedTypeVariable original, AnnotatedTypeVariableImpl clone) {
        Assertions.assertEquals(clone, original);
        if (original instanceof AnnotatedTypeVariableImpl || TestUtils.isJava12Plus()) {
            Assertions.assertEquals(original, clone);
            Assertions.assertEquals(original.hashCode(), clone.hashCode());
            Assertions.assertEquals(original.toString(), clone.toString());
        }
    }

    @Test
    public void testConstructionBasic() {
        AnnotatedTypeVariable atv1 = getATv("tv1", Object.class);
        AnnotatedTypeVariableImpl atv1Clone = new AnnotatedTypeVariableImpl(atv1);
        assertEquivalence(atv1, atv1Clone);
        AnnotatedTypeVariableImpl atv1Manual = new AnnotatedTypeVariableImpl((TypeVariable<?>) atv1.getType(), new Ann4Impl("tv1"));
        assertEquivalence(atv1Clone, atv1Manual);

        AnnotatedTypeVariable atv2 = getATv("tv2", CharSequence.class);
        AnnotatedTypeVariableImpl atv2Clone = new AnnotatedTypeVariableImpl(atv2);
        assertEquivalence(atv2, atv2Clone);
        AnnotatedTypeVariableImpl atv2Manual = new AnnotatedTypeVariableImpl((TypeVariable<?>) atv2.getType(), new Ann4Impl("tv2"));
        assertEquivalence(atv2Clone, atv2Manual);

        // the annotations for the parameter are missing in this variation, so there is no equivalence
        AnnotatedTypeVariableImpl atv2Basic = new AnnotatedTypeVariableImpl((TypeVariable<?>) atv2.getType());
        Assertions.assertNotEquals(atv2Basic, atv2);
        // but the bounds ARE equivalent
        Assertions.assertArrayEquals(atv2Basic.getAnnotatedBounds(), atv2.getAnnotatedBounds());
    }

    @Test
    public void testConstructionBounds() {
        AnnotatedType atv2BoundDiff = new AnnotatedTypeImpl(CharSequence.class, null, new Ann4Impl("T2"));
        AnnotatedTypeVariable atv2 = getATv("tv2", CharSequence.class);
        AnnotatedTypeVariableImpl atv2Diff = new AnnotatedTypeVariableImpl(
                (TypeVariable<?>) atv2.getType(), new Annotation[] { new Ann4Impl("tv2") }, atv2BoundDiff);
        Assertions.assertNotEquals(atv2Diff, atv2);
        Assertions.assertSame(atv2BoundDiff, atv2Diff.getAnnotatedBounds()[0]);
        // specifying null for the boundary type is valid and uses the TV's boundary data
        AnnotatedTypeVariableImpl atv2Same = new AnnotatedTypeVariableImpl(
                (TypeVariable<?>) atv2.getType(), new Annotation[] { new Ann4Impl("tv2") }, (AnnotatedType) null);
        Assertions.assertEquals(atv2Same, atv2);
    }

    @Test
    public void testConstructionBoundAnns() {
        AnnotatedTypeVariable atv2 = getATv("tv2", CharSequence.class);
        AnnotatedTypeVariableImpl atv2Diff = new AnnotatedTypeVariableImpl(
                (TypeVariable<?>) atv2.getType(), new Annotation[] { new Ann4Impl("tv2") }, new Annotation[][] { new Annotation[] { new Ann4Impl("T2") } });
        Assertions.assertNotEquals(atv2Diff, atv2);
        // specifying null for the specified boundary annotations is valid and uses the relevant TV's boundary data
        AnnotatedTypeVariableImpl atv2Same = new AnnotatedTypeVariableImpl(
                (TypeVariable<?>) atv2.getType(), new Annotation[] { new Ann4Impl("tv2") }, new Annotation[][] { null });
        Assertions.assertEquals(atv2Same, atv2);
        // specifying null for all of the specified boundary annotations is valid and uses the TV's boundary data
        AnnotatedTypeVariableImpl atv2Same2 = new AnnotatedTypeVariableImpl(
                (TypeVariable<?>) atv2.getType(), new Annotation[] { new Ann4Impl("tv2") }, (Annotation[][]) null);
        Assertions.assertEquals(atv2Same2, atv2);
        // specifying too short of boundary annotations is valid, and uses the TV's boundary data for all missing indices
        AnnotatedTypeVariableImpl atv2Same3 = new AnnotatedTypeVariableImpl(
                (TypeVariable<?>) atv2.getType(), new Annotation[] { new Ann4Impl("tv2") }, new Annotation[][] {});
        Assertions.assertEquals(atv2Same3, atv2);
    }

    @Test
    public void testConstructionInvalid() {
        TypeVariable<Method> tv1 = getTv("tv1", Object.class);
        TypeVariable<Method> tv2 = getTv("tv2", CharSequence.class);
        List<Executable> invalid = Arrays.asList(
                ()-> new AnnotatedTypeVariableImpl((AnnotatedTypeVariable) null),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(String.class, new Annotation[0])),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(tv1, null, new AnnotatedTypeImpl(Object.class))),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(tv1, new Annotation[] { null }, new AnnotatedTypeImpl(Object.class))),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(tv1, new Annotation[0])),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(tv1, new Annotation[0], (AnnotatedType[]) null)),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(tv1, new Annotation[0], (AnnotatedType) null)),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(FreeTypeVariable.INVALID8, new Annotation[0], new AnnotatedTypeImpl(Object.class))),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(FreeTypeVariable.INVALID9, new Annotation[0], new AnnotatedTypeImpl(Object.class))),
                ()-> new AnnotatedTypeVariableImpl(new FreeAnnotatedTypeVariable(FreeTypeVariable.INVALID10, new Annotation[0], new AnnotatedTypeImpl(Object.class))),

                ()-> new AnnotatedTypeVariableImpl((TypeVariable<?>) null),
                ()-> new AnnotatedTypeVariableImpl(FreeTypeVariable.INVALID8),
                ()-> new AnnotatedTypeVariableImpl(FreeTypeVariable.INVALID9),
                ()-> new AnnotatedTypeVariableImpl(FreeTypeVariable.INVALID10),
                ()-> new AnnotatedTypeVariableImpl(FreeTypeVariable.INVALID11),
                ()-> new AnnotatedTypeVariableImpl(FreeTypeVariable.INVALID12),

                ()-> new AnnotatedTypeVariableImpl(tv1, (Annotation) null),
                ()-> new AnnotatedTypeVariableImpl(tv1, null, new Annotation[][] { new Annotation [] { null } }),
                ()-> new AnnotatedTypeVariableImpl(tv1, null, new AnnotatedTypeImpl(String.class)),
                ()-> new AnnotatedTypeVariableImpl(tv2, null, new AnnotatedTypeImpl(Object.class))
            );
        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testBoundsMutable() {
        AnnotatedTypeVariable type1 = new AnnotatedTypeVariableImpl(getATv("tv1", Object.class));
        Assertions.assertNotSame(type1.getAnnotatedBounds(), type1.getAnnotatedBounds());
    }

    @Test
    public void testEquivalence() {
        AnnotatedTypeVariable atv1 = getATv("tv1", Object.class);
        AnnotatedTypeVariableImpl atv1Clone = new AnnotatedTypeVariableImpl(atv1);
        AnnotatedTypeVariable atv2 = getATv("tv2", CharSequence.class);

        Assertions.assertEquals(atv1Clone, atv1Clone);
        Assertions.assertNotEquals(atv1Clone, atv1Clone.getType());
        Assertions.assertNotEquals(atv1Clone, atv2);

        AnnotatedTypeVariableImpl atv1Diff = new AnnotatedTypeVariableImpl(atv1Clone.getType(), new Ann4Impl("tv2"));
        Assertions.assertNotEquals(atv1Clone, atv1Diff);
        FreeAnnotatedType annType = new FreeAnnotatedType(atv1.getType(), null, atv1.getAnnotations());
        Assertions.assertNotEquals(atv1Clone, annType);
    }
}
