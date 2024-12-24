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
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

/**
 * Base type for implementing {@link AnnotatedElement}
 */
public class AnnotatedElementImpl implements AnnotatedElement {

    /** Empty array of {@link Annotation} to utilize when {@code null} arrays are provided */
    public static final Annotation[] EMPTY_ANNS = new Annotation[0];

    /**
     * Create a new {@link AnnotatedElementImpl} with no annotations
     */
    public AnnotatedElementImpl() {
        this.annotations = EMPTY_ANNS;
    }

    /**
     * Create a new {@link AnnotatedElementImpl} from an existing {@link AnnotatedElement}
     * @param element {@link AnnotatedElement} to copy parameters from
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code element} is {@code null}</li>
     *   <li>When {@code element.}{@link AnnotatedElement#getAnnotations() getAnnotations()} is {@code null}</li>
     *   <li>When {@code element.}{@link AnnotatedElement#getAnnotations() getAnnotations()} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedElementImpl(AnnotatedElement element) {
        Utils.notNull(element, "element");
        this.annotations = Utils.checkedClone(element.getAnnotations(), "element.getAnnotations()");
    }

    /**
     * Create a new {@link AnnotatedElementImpl} from existing {@link Annotation}s
     * @param annotations {@link Annotation}s that the element is annotated with
     * @throws IllegalArgumentException When {@code annotations} contains a {@code null}
     */
    public AnnotatedElementImpl(Annotation... annotations) {
        // if no annotations then default to EMPTY, otherwise validate that there are no nulls
        this.annotations = Utils.checkedClone(annotations, "annotations", EMPTY_ANNS);
    }

    /** {@link Annotation}s held by the element */
    protected final Annotation[] annotations;

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof AnnotatedElement)) {
            return false;
        }
        AnnotatedElement o = (AnnotatedElement) other;
        return Arrays.equals(annotations, o.getAnnotations());
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Utils.notNull(annotationClass, "annotationClass");
        for (Annotation ann : annotations) {
            if (annotationClass.equals(ann.annotationType())) {
                return Utils.cast(ann);
            }
        }
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        // clone to avoid modification by caller
        return Utils.clone(annotations);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        // there is no distinction here between declared and not, so return all
        return getAnnotations();
    }

    @Override
    public int hashCode() {
        return AnnotatedTypeHash.hashCode(this);
    }

    @Override
    public String toString() {
        return Utils.annsToString(annotations, null);
    }
}
