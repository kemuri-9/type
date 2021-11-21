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

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.kemuri9.type.TypeVariableImpl;

public class TypeVariableImplTest {

    public static <T> void one(T t) {}
    public static <T extends Number> void two(T t) {}
    public static <A extends Number, B extends Number> void three(A a, B b) {}

    private static void assertEquals(TypeVariable<?> original, TypeVariableImpl<?> clone) {
        Assertions.assertEquals(clone, original);
        Assertions.assertEquals(original.hashCode(), clone.hashCode());
        // this does not work because the JVM creates new AnnotatedTypes on every invocation! OUCH!
        //Assertions.assertArrayEquals(original.getAnnotatedBounds(), clone.getAnnotatedBounds());
        Assertions.assertEquals(original.getAnnotatedBounds().length, clone.getAnnotatedBounds().length);
        Assertions.assertArrayEquals(original.getAnnotations(), clone.getAnnotations());
        Assertions.assertArrayEquals(original.getBounds(), clone.getBounds());
        if (original instanceof TypeVariableImpl) {
            Assertions.assertEquals(original, clone);
        }
    }

    @Test
    public void testConstruction() {
        Method oneMethod = TestUtils.getMethod(getClass(), "one", Object.class);
        TypeVariable<Method> one = oneMethod.getTypeParameters()[0];
        TypeVariableImpl<Method> oneClone = new TypeVariableImpl<>(one);
        assertEquals(one, oneClone);
        TypeVariableImpl<Method> oneManual = new TypeVariableImpl<>(oneMethod, "T", oneMethod.getAnnotations(), one.getAnnotatedBounds());
        assertEquals(oneClone, oneManual);
        TypeVariableImpl<Method> oneManual2 = new TypeVariableImpl<>(oneMethod, "T", oneMethod.getAnnotations(), one.getBounds());
        assertEquals(oneClone, oneManual2);

        Method twoMethod = TestUtils.getMethod(getClass(), "two", Number.class);
        TypeVariable<Method> two = twoMethod.getTypeParameters()[0];
        TypeVariableImpl<Method> twoClone = new TypeVariableImpl<>(two);
        assertEquals(two, twoClone);
        TypeVariableImpl<Method> twoManual = new TypeVariableImpl<>(twoMethod, "T", twoMethod.getAnnotations(), two.getAnnotatedBounds());
        assertEquals(twoClone, twoManual);
        TypeVariableImpl<Method> twoManual2 = new TypeVariableImpl<>(twoMethod, "T", twoMethod.getAnnotations(), two.getBounds());
        assertEquals(twoClone, twoManual2);
    }

    @Test
    public void testConstructionInvalid() {
        List<Executable> invalid = new ArrayList<>();
        invalid.add(()-> new TypeVariableImpl<>(null));
        FreeTypeVariable.INVALID.stream().map(f-> { return (Executable) ()-> new TypeVariableImpl<>(f); }).forEach(invalid::add);
        // null annotations are valid, as it defaults to empty
        FreeTypeVariable.INVALID.stream().filter(f -> f.getAnnotations() != null).map(f-> { return (Executable) ()-> new TypeVariableImpl<>(f.getGenericDeclaration(), f.getName(), f.getAnnotations(), f.getAnnotatedBounds()); }).forEach(invalid::add);

        TestUtils.assertThrows(IllegalArgumentException.class, invalid);
    }

    @Test
    public void testBoundsMutable() {
        TypeVariableImpl<Method> type1 = new TypeVariableImpl<>(TestUtils.getMethod(getClass(), "one", Object.class).getTypeParameters()[0]);
        Assertions.assertNotSame(type1.getAnnotatedBounds(), type1.getAnnotatedBounds());
        Assertions.assertNotSame(type1.getBounds(), type1.getBounds());
    }

    @Test
    public void testEquivalence() {
        TypeVariableImpl<Method> oneClone = new TypeVariableImpl<>(TestUtils.getMethod(getClass(), "one", Object.class).getTypeParameters()[0]);
        TypeVariableImpl<Method> twoClone = new TypeVariableImpl<>(TestUtils.getMethod(getClass(), "two", Number.class).getTypeParameters()[0]);
        // same name, but different generic decls, so not equivalent
        Assertions.assertNotEquals(oneClone, twoClone);

        Method threeMethod = TestUtils.getMethod(getClass(), "three", Number.class, Number.class);
        TypeVariable<Method>[] tvs = threeMethod.getTypeParameters();
        TypeVariable<Method> three = tvs[0];
        TypeVariableImpl<Method> threeClone = new TypeVariableImpl<>(three);
        Assertions.assertNotEquals(three, threeClone);
        Assertions.assertNotEquals(threeClone, Object.class);
        Assertions.assertEquals(threeClone, threeClone);

        // same generic decl, but different names, so not equivalent
        TypeVariableImpl<Method> fourClone = new TypeVariableImpl<>(tvs[1]);
        Assertions.assertNotEquals(threeClone, fourClone);

    }
}
