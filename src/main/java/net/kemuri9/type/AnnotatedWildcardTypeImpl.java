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
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Implementation of {@link AnnotatedWildcardType}.
 * {@link AnnotatedWildcardType} represents an annotated {@link WildcardType}.
 */
public final class AnnotatedWildcardTypeImpl extends AnnotatedTypeImpl implements AnnotatedWildcardType {

    /** {@link AnnotatedType}s representing the annotated lower bounds ({@code super}) */
    final AnnotatedType[] lowerBounds;

    /** {@link AnnotatedType}s representing the annotated upper bounds ({@code extends}) */
    final AnnotatedType[] upperBounds;

    /**
     * Create an {@link AnnotatedWildcardTypeImpl} from an existing {@link AnnotatedWildcardType}
     * @param type {@link AnnotatedWildcardType} to copy parameters from.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is not a {@link WildcardType}</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedLowerBounds() getAnnotatedLowerBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedLowerBounds() getAnnotatedLowerBounds()}
     *     does not match {@code type.}{@link AnnotatedWildcardType#getType() getType()}{@code .}{@link WildcardType#getLowerBounds() getLowerBounds()} typing</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedUpperBounds() getAnnotatedUpperBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedUpperBounds() getAnnotatedUpperBounds()}
     *     does not match {@code type.}{@link AnnotatedWildcardType#getType() getType()}{@code .}{@link WildcardType#getUpperBounds() getUpperBounds()} typing</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     * @see #AnnotatedWildcardTypeImpl(WildcardType)
     */
    public AnnotatedWildcardTypeImpl(AnnotatedWildcardType type) {
        this(type, Utils.getAnnotations(type));
    }

    /**
     * Create an {@link AnnotatedWildcardTypeImpl} from an existing {@link AnnotatedWildcardType}
     * @param type {@link AnnotatedWildcardType} to copy parameters from.
     * @param annotations {@link Annotation}s to utilize for this {@link AnnotatedWildcardType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is not a {@link WildcardType}</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedLowerBounds() getAnnotatedLowerBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedLowerBounds() getAnnotatedLowerBounds()}
     *     does not match {@code type.}{@link AnnotatedWildcardType#getType() getType()}{@code .}{@link WildcardType#getLowerBounds() getLowerBounds()} typing</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedUpperBounds() getAnnotatedUpperBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedWildcardType#getAnnotatedUpperBounds() getAnnotatedUpperBounds()}
     *     does not match {@code type.}{@link AnnotatedWildcardType#getType() getType()}{@code .}{@link WildcardType#getUpperBounds() getUpperBounds()} typing</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType, Annotation[])
     * @see #AnnotatedWildcardTypeImpl(WildcardType)
     * @since 1.1
     */
    public AnnotatedWildcardTypeImpl(AnnotatedWildcardType type, Annotation[] annotations) {
        super(type, annotations);
        WildcardType wc = getType();
        AnnotatedType[] annLb = Utils.noNullContained(type.getAnnotatedLowerBounds(), "type.getAnnotatedLowerBounds()");
        AnnotatedType[] annUb = Utils.noNullContained(type.getAnnotatedUpperBounds(), "type.getAnnotatedUpperBounds()");
        Type[] ub = wc.getUpperBounds();
        Type[] lb = wc.getLowerBounds();
        AnnotatedWildcardTypeEquals.checkBoundaries(lb, ub, annLb, annUb);
        this.lowerBounds = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(annLb);
        this.upperBounds = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(annUb);
    }

    /**
     * Create a new {@link AnnotatedWildcardTypeImpl} for the specified {@link WildcardType}
     * @param type {@link WildcardType} to decorate as an undecorated {@link AnnotatedWildcardType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type.}{@link WildcardType#getLowerBounds() getLowerBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link WildcardType#getLowerBounds() getLowerBounds()} contains a {@code null}</li>
     *   <li>When {@code type.}{@link WildcardType#getUpperBounds() getUpperBounds()} is {@code null}</li>
     *   <li>When {@code type.}{@link WildcardType#getUpperBounds() getUpperBounds()} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedWildcardTypeImpl(WildcardType type) {
        this(type, EMPTY_ANNS);
    }

    /**
     * Create a new {@link AnnotatedWildcardTypeImpl} with the specified parameters
     * @param type {@link WildcardType} to decorate as an {@link AnnotatedWildcardType}
     * @param annotations {@link Annotation}s to decorate {@code type} with
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     * </ul>
     * @see #AnnotatedWildcardTypeImpl(WildcardType)
     */
    public AnnotatedWildcardTypeImpl(WildcardType type, Annotation... annotations) {
        this(type, annotations, (Annotation[][]) null, null);
    }

    /**
     * Create a new {@link AnnotatedWildcardTypeImpl} with the specified parameters
     * @param type {@link WildcardType} to decorate as an {@link AnnotatedWildcardType}
     * @param typeAnnotations {@link Annotation}s to decorate {@code type} with
     * @param lowerBoundAnns {@link Annotation} sets to decorate individual boundaries from
     *  {@code type.}{@link WildcardType#getLowerBounds() getLowerBounds()}.
     *  {@code null} {@link Annotation}{@code []} entries indicate no annotations for the relevant lower boundary.
     * @param upperBoundAnns {@link Annotation} sets to decorate individual boundaries from
     *  {@code type.}{@link WildcardType#getUpperBounds() getUpperBounds()}.
     *  {@code null} {@link Annotation}{@code []} entries indicate no annotations for the relevant upper boundary.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     *   <li>When any element of {@code lowerBoundAnns} contains a {@code null}</li>
     *   <li>When any element of {@code upperBoundAnns} contains a {@code null}</li>
     * </ul>
     * @see #AnnotatedWildcardTypeImpl(WildcardType)
     */
    public AnnotatedWildcardTypeImpl(WildcardType type, Annotation[] typeAnnotations,
            Annotation[][] lowerBoundAnns, Annotation[][] upperBoundAnns) {
        super(type, null, typeAnnotations);
        lowerBounds = AnnotatedTypeFactory.newAnnotatedTypes(type.getLowerBounds(), lowerBoundAnns);
        upperBounds = AnnotatedTypeFactory.newAnnotatedTypes(type.getUpperBounds(), upperBoundAnns);
    }

    /**
     * Create a new {@link AnnotatedWildcardTypeImpl} with the specified parameters
     * @param type {@link WildcardType} to decorate as an {@link AnnotatedWildcardType}
     * @param typeAnnotations {@link Annotation}s to decorate {@code type} with
     * @param lowerBounds {@link AnnotatedType}s indicating the annotated lower bounds of {@code type}.
     *   {@code null} entries indicate to create undecorated entries from associated lower bounds.
     * @param upperBounds {@link AnnotatedType}s indicating the annotated upper bounds of {@code type}.
     *   {@code null} entries indicate to create undecorated entries from associated upper bounds.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     *   <li>When {@code lowerBounds} specifies any annotated type that does not match the respective lower boundary on {@code type}</li>
     *   <li>When {@code upperBounds} specifies any annotated type that does not match the respective upper boundary on {@code type}</li>
     * </ul>
     * @see #AnnotatedWildcardTypeImpl(WildcardType)
     */
    public AnnotatedWildcardTypeImpl(WildcardType type, Annotation[] typeAnnotations,
            AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds) {
        super(type, null, typeAnnotations);
        this.lowerBounds = AnnotatedTypeFactory.checkAnnotated(type.getLowerBounds(), lowerBounds);
        this.upperBounds = AnnotatedTypeFactory.checkAnnotated(type.getUpperBounds(), upperBounds);
    }

    @Override
    protected void checkType(String name) {
        if (!(type instanceof WildcardType)) {
            throw new IllegalArgumentException(name + " must be a WildcardType, was " + type);
        }
        WildcardType wc = getType();
        Utils.noNullContained(wc.getLowerBounds(), name + ".getLowerBounds()");
        Utils.noNullContained(wc.getUpperBounds(), name + ".getUpperBounds()");
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other) || !(other instanceof AnnotatedWildcardType)) {
            return false;
        }
        AnnotatedWildcardType o = (AnnotatedWildcardType) other;
        return AnnotatedWildcardTypeEquals.isEqual(this, o);
    }

    @Override
    public AnnotatedType[] getAnnotatedLowerBounds() {
        // clone to avoid modification by caller
        return Utils.clone(lowerBounds);
    }

    @Override
    public AnnotatedType[] getAnnotatedUpperBounds() {
        // clone to avoid modification by caller
        return Utils.clone(upperBounds);
    }

    @Override
    public WildcardType getType() {
        return (WildcardType) type;
    }

    @Override
    public int hashCode() {
        return AnnotatedTypeHash.hashCode(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(Utils.annsToString(annotations, Boolean.FALSE)).append("?");
        AnnotatedType[] bounds = lowerBounds;
        if (bounds.length > 0) {
            sb.append(" super ");
        } else {
            bounds = upperBounds;
            // if the sole upper bound is a bare Object, then there is no further output
            if (bounds.length == 1 && bounds[0].getType().equals(Object.class) && bounds[0].getAnnotations().length == 0) {
                return sb.toString();
            }
            sb.append(" extends ");
        }

        sb.append(Utils.annTypesToString(bounds, " & ", "", ""));
        return sb.toString();
    }
}
