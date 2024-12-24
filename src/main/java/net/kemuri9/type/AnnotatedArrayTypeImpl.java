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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Implementation of {@link AnnotatedArrayType}.
 * {@link AnnotatedArrayType} represents an annotated array {@link Class} or an annotated {@link GenericArrayType}.
 */
public final class AnnotatedArrayTypeImpl extends AnnotatedTypeImpl implements AnnotatedArrayType {

    /** {@link AnnotatedType} that represents the annotated component of the array */
    final AnnotatedType genericComponentType;

    /**
     * Create an {@link AnnotatedArrayTypeImpl} from an existing {@link AnnotatedArrayType}
     * @param type {@link AnnotatedArrayType} to copy parameters from.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is not an array {@link Class} and not a {@link GenericArrayType}</li>
     *   <li>When {@code type.}{@link AnnotatedArrayType#getAnnotatedGenericComponentType() getAnnotatedGenericComponentType()}
     *     is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedArrayType#getAnnotatedGenericComponentType() getAnnotatedGenericComponentType()}
     *     does not match {@code type.}{@link AnnotatedType#getType() getType()}'s component type</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     */
    public AnnotatedArrayTypeImpl(AnnotatedArrayType type) {
        this(type, Utils.getAnnotations(type));
    }

    /**
     * Create an {@link AnnotatedArrayTypeImpl} from an existing {@link AnnotatedArrayType}
     * @param type {@link AnnotatedArrayType} to copy parameters from.
     * @param annotations {@link Annotation}s to utilize for the {@link AnnotatedArrayType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code type.}{@link AnnotatedType#getType() getType()} is not an array {@link Class} and not a {@link GenericArrayType}</li>
     *   <li>When {@code type.}{@link AnnotatedArrayType#getAnnotatedGenericComponentType() getAnnotatedGenericComponentType()}
     *     is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedArrayType#getAnnotatedGenericComponentType() getAnnotatedGenericComponentType()}
     *     does not match {@code type.}{@link AnnotatedType#getType() getType()}'s component type</li>
     * </ul>
     * @see AnnotatedTypeImpl#AnnotatedTypeImpl(AnnotatedType)
     * @since 1.1
     */
    public AnnotatedArrayTypeImpl(AnnotatedArrayType type, Annotation[] annotations) {
        super(type, annotations);
        AnnotatedType compType = Utils.notNull(type.getAnnotatedGenericComponentType(), "type.getAnnotatedGenericComponentType()");
        Utils.checkMatching(getComponentType(), compType);
        this.genericComponentType = AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(compType);
    }

    /**
     * Create a new {@link AnnotatedArrayTypeImpl} for the specified {@link Type}
     * @param type {@link Type} to decorate as an undecorated {@link AnnotatedArrayType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type} is a {@link Class} and is <strong>not</strong> an array type</li>
     *   <li>When {@code type} is not a {@link Class} and not a {@link GenericArrayType}</li>
     * </ul>
     */
    public AnnotatedArrayTypeImpl(Type type) {
        this(type, null, EMPTY_ANNS);
    }

    /**
     * Create a new {@link AnnotatedArrayTypeImpl} from the specified parameters.
     * @param type {@link Type} to annotate
     * @param typeAnnotations {@link Annotation}s to annotate {@code type} with
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code typeAnnotations} contains a {@code null}</li>
     * </ul>
     * @see #AnnotatedArrayTypeImpl(Type)
     */
    public AnnotatedArrayTypeImpl(Type type, Annotation... typeAnnotations) {
        this(type, null, typeAnnotations);
    }

    /**
     * Create an {@link AnnotatedArrayTypeImpl} from the specified parameters
     * @param type {@link Type} indicating the array type or {@link GenericArrayType} that is annotated
     * @param typeAnnotations {@link Annotation}s that annotate the array type
     * @param componentType {@link AnnotatedType} indicating the annotated component of the array
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is invalid</li>
     *   <li>When {@code typeAnnotations} contains a {@code null}</li>
     *   <li>When {@code componentType} is {@code null}</li>
     *   <li>When {@code type}'s component type does not equal {@code componentType}'s type</li>
     * </ul>
     * @see #AnnotatedArrayTypeImpl(Type)
     */
    public AnnotatedArrayTypeImpl(Type type, Annotation[] typeAnnotations, AnnotatedType componentType) {
        super(type, null, typeAnnotations);
        Utils.notNull(componentType, "componentType");
        Utils.checkMatching(getComponentType(), componentType);
        this.genericComponentType = AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(componentType);
    }

    /**
     * Create an {@link AnnotatedArrayTypeImpl} from the specified parameters
     * @param type {@link Type} indicating the array type or {@link GenericArrayType} that is annotated
     * @param arrayTypeAnns {@link Annotation}s that annotate the array type
     * @param componentTypeAnns {@link Annotation}s that annotate the array's component type.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code arrayType} is {@code null}</li>
     *   <li>When {@code arrayType} is not an array {@link Class} and not a {@link GenericArrayType}</li>
     *   <li>When {@code arrayTypeAnns} contains a {@code null}</li>
     *   <li>When {@code componentTypeAnns} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedArrayTypeImpl(Type type, Annotation[] arrayTypeAnns, Annotation... componentTypeAnns) {
        super(type, null, arrayTypeAnns);
        this.genericComponentType = AnnotatedTypeFactory.newAnnotatedType(getComponentType(), componentTypeAnns);
    }

    @Override
    protected void checkType(String name) {
        boolean valid = (type instanceof Class) && Class.class.cast(type).isArray();
        valid |= type instanceof GenericArrayType;
        if (!valid) {
            throw new IllegalArgumentException(name + " must be an Array Class or GenericArrayType, was " + type);
        }
        if (getComponentType() == null) {
            throw new IllegalArgumentException(name + " must have a valid component type, was null");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other) || !(other instanceof AnnotatedArrayType)) {
            return false;
        }
        AnnotatedArrayType o = (AnnotatedArrayType) other;
        // this depends on genericComponentType having a proper equals implementation!
        return Objects.equals(genericComponentType, o.getAnnotatedGenericComponentType());
    }

    @Override
    public AnnotatedType getAnnotatedGenericComponentType() {
        return genericComponentType;
    }

    private Type getComponentType() {
        return (type instanceof Class) ? Class.class.cast(type).getComponentType()
                : ((GenericArrayType) type).getGenericComponentType();
    }

    @Override
    public int hashCode() {
        return AnnotatedTypeHash.hashCode(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        AnnotatedType type = this;
        while (type instanceof AnnotatedArrayType) {
            AnnotatedArrayType arrType = (AnnotatedArrayType) type;
            sb.append(Utils.annsToString(arrType.getAnnotations(), Boolean.TRUE)).append("[]");
            type = arrType.getAnnotatedGenericComponentType();
        }

        sb.insert(0, type.toString());
        return sb.toString();
    }
}
