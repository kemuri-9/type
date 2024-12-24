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
package net.kemuri9.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringJoiner;

/**
 * Base implementation for creating {@link Annotation} instances at runtime
 */
public class AnnotationImpl implements Annotation, java.io.Serializable {

    private static final long serialVersionUID = 6422587727334973151L;

    private interface Invoke<A> {
        A apply(AnnotationImpl self, A input, Method method) throws ReflectiveOperationException;
    }

    private static final class Equals implements Invoke<Boolean> {

        private final Object right;

        private Equals(Object right) {
            this.right = right;
        }

        @Override
        public Boolean apply(AnnotationImpl self, Boolean equals, Method method) throws ReflectiveOperationException {
            if (!equals) {
                return Boolean.FALSE;
            }
            Object left = method.invoke(self);
            Object right = method.invoke(this.right);
            /* annotations cannot have nulls in practice, but with custom implementations like this,
             * the system can become a bit broken */
            return left != null && right != null && Utils.isBasicEquals(left, right);
        }
    }

    private static final class HashCode implements Invoke<Integer> {

        static final HashCode INSTANCE = new HashCode();

        @Override
        public Integer apply(AnnotationImpl self, Integer input, Method method) throws ReflectiveOperationException {
            int thisHash = 127 * method.getName().hashCode();
            Object value = method.invoke(self);
            return input + (Utils.hashCode(value) ^ thisHash);
        }
    }

    private static final class ToString implements Invoke<StringJoiner> {

        static final ToString INSTANCE = new ToString();

        @Override
        public StringJoiner apply(AnnotationImpl self, StringJoiner input, Method method) throws ReflectiveOperationException {
            return input.add(AnnotationString.getNamePrefix(self.members, method) + AnnotationString.toString(method.invoke(self)));
        }
    }

    /** {@link Annotation} {@link Class} that is represented */
    protected final Class<? extends Annotation> annotationType;

    /** Methods that represent the attributes on {@code annotationType} */
    private transient Method[] members;

    /**
     * Create a new {@link AnnotationImpl}
     * @param annotationType {@link Annotation} {@link Class} that is represented
     * @throws IllegalArgumentException When {@code annotationType} is {@code null}
     */
    public AnnotationImpl(Class<? extends Annotation> annotationType) {
        this.annotationType = Utils.notNull(annotationType, "annotationType");
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Annotation)) {
            return false;
        }

        Annotation o = (Annotation) object;
        Class<? extends Annotation> oType = o.annotationType();
        // different annotation types are not equivalent
        /* in practice, the annotation type should not be null, but with custom implementations like this,
         * the system can become a bit broken */
        if (oType == null || !oType.equals(annotationType)) {
            return false;
        }

        return processProperties(Boolean.TRUE, new Equals(o));
    }

    @Override
    public int hashCode() {
        return processProperties(0, HashCode.INSTANCE);
    }

    /**
     * Process over the properties of the annotation
     * @param <A> Type of accumulated value
     * @param accumulative accumulated value
     * @param process process to perform on all the properties of the annotation
     * @return accumulated value
     */
    private <A> A processProperties(A accumulative, Invoke<A> process) {
        if (members == null) {
            /* it would be nice to get the same order as the JVM, but this is impossible without
             * AnnotationType or the ConstantPool. And attempting to access either gets into a bit of a tricky situation
             * with accesses as they are not part of the JDK API, but internal APIs.
             * So order the members by name to get a consistent ordering across runs, which will likely be different
             * from the order of the JVM */
            members = annotationType.getDeclaredMethods();
            Arrays.sort(members, Comparator.comparing(Method::getName));
        }
        for (Method method : members) {
            try {
                // TODO: need to evaluate when, if it all, it is necessary to setAccessible(true)
                accumulative = process.apply(this, accumulative, method);
            } catch (ReflectiveOperationException | SecurityException ex) {
                throw new UnsupportedOperationException("invalid annotation " + ex);
            }
        }
        return accumulative;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "@" + annotationType.getName() + "(", ")");
        processProperties(joiner, ToString.INSTANCE);
        return joiner.toString();
    }
}
