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
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

/**
 * Implementation of {@link AnnotatedParameterizedType}.
 * {@link AnnotatedParameterizedType} represents an annotated {@link ParameterizedType}.
 */
public final class AnnotatedParameterizedTypeImpl extends AnnotatedTypeImpl implements AnnotatedParameterizedType {

    /** {@link AnnotatedType}s representing the annotated actual type arguments */
    final AnnotatedType[] actualTypeArguments;

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} from an existing {@link AnnotatedParameterizedType}
     * @param type {@link AnnotatedParameterizedType} to copy parameters from
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is not a {@link ParameterizedType}
     *   <li>When {@code type.}{@link AnnotatedParameterizedType#getAnnotatedActualTypeArguments() getAnnotatedActualTypeArguments()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedParameterizedType#getAnnotatedActualTypeArguments() getAnnotatedActualTypeArguments()} contains a {@code null}</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     */
    public AnnotatedParameterizedTypeImpl(AnnotatedParameterizedType type) {
        this(type, Utils.getAnnotations(type));
    }

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} from an existing {@link AnnotatedParameterizedType}
     * @param type {@link AnnotatedParameterizedType} to copy parameters from
     * @param annotations {@link Annotation}s to utilize for the {@link AnnotatedParameterizedType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is not a {@link ParameterizedType}
     *   <li>When {@code type.}{@link AnnotatedParameterizedType#getAnnotatedActualTypeArguments() getAnnotatedActualTypeArguments()} is {@code null}</li>
     * <li>When {@code type.}{@link AnnotatedParameterizedType#getAnnotatedActualTypeArguments() getAnnotatedActualTypeArguments()} contains a {@code null}</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     * @since 1.1
     */
    public AnnotatedParameterizedTypeImpl(AnnotatedParameterizedType type, Annotation[] annotations) {
        super(type, annotations);
        AnnotatedType[] typeArgs = Utils.noNullContained(type.getAnnotatedActualTypeArguments(), "type.getAnnotatedActualTypeArguments()");
        Utils.checkMatching(getType().getActualTypeArguments(), typeArgs);
        this.actualTypeArguments = AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(typeArgs);
    }

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} for the specified {@link ParameterizedType}
     * @param type {@link ParameterizedType} to decorate as an undecorated {@link AnnotatedParameterizedType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type.}{@link ParameterizedType#getRawType() getRawType()} is {@code null}</li>
     *   <li>When {@code type.}{@link ParameterizedType#getActualTypeArguments() getActualTypeArguments()} is {@code null}</li>
     *   <li>When {@code type.}{@link ParameterizedType#getActualTypeArguments() getActualTypeArguments()} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedParameterizedTypeImpl(ParameterizedType type) {
        this(type, null, EMPTY_ANNS);
    }

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} from the specified parameters.
     * @param type {@link ParameterizedType} to annotate
     * @param typeAnnotations {@link Annotation}s to annotate {@code type} with
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code typeAnnotations} contains a {@code null}</li>
     * </ul>
     * @see #AnnotatedParameterizedTypeImpl(ParameterizedType)
     */
    public AnnotatedParameterizedTypeImpl(ParameterizedType type, Annotation... typeAnnotations) {
        this(type, null, typeAnnotations);
    }

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} from the specified parameters.
     * @param type {@link ParameterizedType} to annotate
     * @param ownerType {@link AnnotatedType} describing the annotated owner type
     * @param typeAnnotations {@link Annotation}s to annotate {@code type} with
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code ownerType} is not {@code null} and does not describe a type matching {@code type}'s owner type</li>
     *   <li>When {@code typeAnnotations} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedParameterizedTypeImpl(ParameterizedType type, AnnotatedType ownerType, Annotation... typeAnnotations) {
        super(type, ownerType, typeAnnotations);
        actualTypeArguments = AnnotatedTypeFactory.newAnnotatedTypes(type.getActualTypeArguments(), null);
    }

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} from the specified parameters.
     * @param type {@link ParameterizedType} to annotate
     * @param ownerType {@link AnnotatedType} describing the annotated owner type
     * @param typeAnnotations {@link Annotation}s to annotate {@code type} with
     * @param actualTypeAnns {@link Annotation} sets to annotate corresponding {@code type.}{@link ParameterizedType#getActualTypeArguments() getActualTypeArguments()} with.
     *   null {@link Annotation}{@code []} entries indicate no annotations for the corresponding type argument.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code ownerType} is not {@code null} and does not describe a type matching {@code type}'s owner type</li>
     *   <li>When {@code typeAnnotations} contains a {@code null}</li>
     *   <li>When any element of {@code actualTypeAnns} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedParameterizedTypeImpl(ParameterizedType type, AnnotatedType ownerType,
            Annotation[] typeAnnotations, Annotation[][] actualTypeAnns) {
        super(type, ownerType, typeAnnotations);
        actualTypeArguments = AnnotatedTypeFactory.newAnnotatedTypes(type.getActualTypeArguments(), actualTypeAnns);
    }

    /**
     * Create a new {@link AnnotatedParameterizedTypeImpl} from the specified parameters.
     * @param type {@link ParameterizedType} to annotate
     * @param ownerType {@link AnnotatedType} describing the annotated owner type
     * @param typeAnnotations {@link Annotation}s to annotate {@code type} with
     * @param actualTypeArgs {@link AnnotatedType}s indicating the annotated type arguments of {@code type}.
     *  {@code null} entries indicate to create undecorated entries from associated type arguments.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code ownerType} is not {@code null} and does not describe a type matching {@code type}'s owner type</li>
     *   <li>When {@code typeAnnotations} contains a {@code null}</li>
     *   <li>When {@code actualTypeArgs} specifies any annotated type that does not match the respective type argument on {@code type}</li>
     * </ul>
     */
    public AnnotatedParameterizedTypeImpl(ParameterizedType type, AnnotatedType ownerType,
            Annotation[] typeAnnotations, AnnotatedType... actualTypeArgs) {
        super(type, ownerType, typeAnnotations);
        this.actualTypeArguments = AnnotatedTypeFactory.checkAnnotated(type.getActualTypeArguments(), actualTypeArgs);
    }

    @Override
    protected void checkType(String name) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException(name + " must be a ParameterizedType, was " + type);
        }
        ParameterizedType pt = getType();
        Utils.notNull(pt.getRawType(), name + ".getRawType()");
        Utils.noNullContained(pt.getActualTypeArguments(), name + ".getActualTypeArguments()");
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other) || !(other instanceof AnnotatedParameterizedType)) {
            return false;
        }
        AnnotatedParameterizedType o = (AnnotatedParameterizedType) other;
        // this depends on actualTypeArguments having usable equals implementations!
        return Arrays.equals(actualTypeArguments, o.getAnnotatedActualTypeArguments());
    }

    @Override
    public AnnotatedType[] getAnnotatedActualTypeArguments() {
        // clone to avoid modification by caller
        return Utils.clone(actualTypeArguments);
    }

    @Override
    public ParameterizedType getType() {
        return (ParameterizedType) type;
    }

    @Override
    public int hashCode() {
        return AnnotatedTypeHash.hashCode(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(Utils.annsToString(annotations, Boolean.FALSE));
        sb.append(getType().getRawType().getTypeName());
        sb.append(Utils.annTypesToString(actualTypeArguments, ", ", "<", ">"));
        return sb.toString();
    }
}
