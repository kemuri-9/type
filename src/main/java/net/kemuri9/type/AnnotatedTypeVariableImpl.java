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
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * Implementation of {@link AnnotatedTypeVariable}.
 * {@link AnnotatedArrayType} represents an annotated {@link TypeVariable}.
 */
public final class AnnotatedTypeVariableImpl extends AnnotatedTypeImpl implements AnnotatedTypeVariable {

    private static AnnotatedType[] cloneBounds(TypeVariable<?> type) {
        return AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(type.getAnnotatedBounds());
    }

    /** {@link AnnotatedType}s representing the annotated boundaries */
    final AnnotatedType[] annotatedBounds;

    /**
     * Create a {@link AnnotatedTypeVariableImpl} from an existing {@link AnnotatedTypeVariable}
     * @param type {@link AnnotatedTypeVariable} to copy details from
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedTypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedTypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is empty</li>
     *   <li>When {@code type.}{@link AnnotatedTypeVariable#getAnnotatedBounds() getAnnotatedBounds()} contains a {@code null}</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     */
    public AnnotatedTypeVariableImpl(AnnotatedTypeVariable type) {
        this(type, Utils.getAnnotations(type));
    }

    /**
     * Create a {@link AnnotatedTypeVariableImpl} from an existing {@link AnnotatedTypeVariable}
     * @param type {@link AnnotatedTypeVariable} to copy details from
     * @param annotations {@link Annotation}s to utilize for the {@link AnnotatedTypeVariable}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedTypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedTypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is empty</li>
     *   <li>When {@code type.}{@link AnnotatedTypeVariable#getAnnotatedBounds() getAnnotatedBounds()} contains a {@code null}</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     * @since 1.1
     */
    public AnnotatedTypeVariableImpl(AnnotatedTypeVariable type, Annotation[] annotations) {
        super(type, annotations);
        AnnotatedType[] bounds = Utils.notEmpty(type.getAnnotatedBounds(), "type.getAnnotatedBounds()");
        // recreate types to have valid equals implementations
        this.annotatedBounds = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(bounds);
    }

    /**
     * Create a {@link AnnotatedTypeVariableImpl} from a {@link TypeVariable}
     * @param type {@link TypeVariable} to copy details from.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type.}{@link TypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link TypeVariable#getAnnotatedBounds() getAnnotatedBounds()} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedTypeVariableImpl(TypeVariable<?> type) {
        super(Utils.notNull(type, "type"), null, type.getAnnotations());
        // clone to avoid modification by caller
        this.annotatedBounds = cloneBounds(type);
    }

    /**
     * Create a {@link AnnotatedTypeVariableImpl} from a {@link TypeVariable}.
     * the annotated boundaries are taken directly from {@code type}.
     * @param type {@link TypeVariable} to annotate.
     * @param annotations {@link Annotation}s indicating annotations on {@code type}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     * </ul>
     * @see #AnnotatedTypeVariableImpl(TypeVariable)
     */
    public AnnotatedTypeVariableImpl(TypeVariable<?> type, Annotation... annotations) {
        super(type, null, annotations);
        // clone to avoid modification by caller
        this.annotatedBounds = cloneBounds(type);
    }

    /**
     * Create a {@link AnnotatedTypeVariableImpl} from a {@link TypeVariable}.
     * Any user-supplied boundary annotations override the associated boundaries from {@code type} when specified.
     * @param type {@link TypeVariable} to annotate
     * @param annotations {@link Annotation}s indicating annotations on {@code type}
     * @param boundsAnnotations user-supplied {@link Annotation}s for boundaries, overriding boundaries from {@code type}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     *   <li>When any element of {@code boundsAnnotations} contains a {@code null}</li>
     * </ul>
     * @see #AnnotatedTypeVariableImpl(TypeVariable)
     */
    public AnnotatedTypeVariableImpl(TypeVariable<?> type, Annotation[] annotations, Annotation[][] boundsAnnotations) {
        super(type, null, annotations);
        annotatedBounds = cloneBounds(type);
        // loop through the incoming annotations bound arrays and check against the type's boundaries
        for (int idx = 0; idx < annotatedBounds.length; ++idx) {
            AnnotatedType bound = annotatedBounds[idx];
            Annotation[] boundAnns = Utils.get(boundsAnnotations, idx);
            if (boundAnns == null) {
                continue;
            }

            // if there are user supplied annotations for a boundary, validate and use them
            Utils.noNullContained(boundAnns, "boundsAnnotations[" + idx + "]");
            bound = AnnotatedTypeFactory.newAnnotatedType(bound.getType(), boundAnns);
            annotatedBounds[idx] = bound;
        }
    }

    /**
     * Create a {@link AnnotatedTypeVariableImpl} from a {@link TypeVariable}
     * Any user-supplied annotated boundaries override the associated boundaries from {@code type} when specified.
     * @param type {@link TypeVariable} to annotated
     * @param annotations {@link Annotation}s indicating annotations on {@code type}
     * @param annotatedBoundaries user-supplied {@link AnnotatedType}s indicating the boundaries, overriding from {@code type}.
     *  {@code null} entries indicate no override for the relevant boundary
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type.}{@link TypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is invalid</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     *   <li>When {@code annotatedBoundaries} specifies an annotated type that does not match the respective boundary on {@code type}</li>
     * </ul>
     * @see #AnnotatedTypeVariableImpl(TypeVariable)
     */
    public AnnotatedTypeVariableImpl(TypeVariable<?> type, Annotation[] annotations, AnnotatedType... annotatedBoundaries) {
        super(type, null, annotations);
        annotatedBounds = cloneBounds(type);
        // loop through the incoming annotated types and check against the type's boundaries
        for (int idx = 0; idx < annotatedBounds.length; ++idx) {
            AnnotatedType override = Utils.get(annotatedBoundaries, idx);
            if (override == null) {
                continue;
            }
            override = AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(override);
            annotatedBounds[idx] = Utils.checkMatching(annotatedBounds[idx].getType(), override);
        }
    }

    @Override
    protected void checkType(String name) {
        if (!(type instanceof TypeVariable)) {
            throw new IllegalArgumentException(name + " must be a TypeVariable, was " + type);
        }
        Utils.notEmpty(getType().getAnnotatedBounds(), name + ".getAnnotatedBounds()");
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other) || !(other instanceof AnnotatedTypeVariable)) {
            return false;
        }
        AnnotatedTypeVariable o = (AnnotatedTypeVariable) other;
        // this depends on annotatedBounds having usable equals implementations!
        return Arrays.equals(annotatedBounds, o.getAnnotatedBounds());
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        // clone to avoid modification by caller
        return Utils.clone(annotatedBounds);
    }

    @Override
    public TypeVariable<?> getType() {
        return (TypeVariable<?>) type;
    }

    @Override
    public int hashCode() {
        return AnnotatedTypeHash.hashCode(this);
    }
}
