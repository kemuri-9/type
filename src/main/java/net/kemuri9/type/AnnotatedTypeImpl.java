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
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Type;

/**
 * Base class for implementing {@link AnnotatedType}
 */
public class AnnotatedTypeImpl extends AnnotatedElementImpl implements AnnotatedType {

    private static AnnotatedType checkOwnerType(Type type, Type ownerType, AnnotatedType annType) {
        if (ownerType == null && annType != null) {
                throw new IllegalArgumentException("may not have an AnnotatedType ownerType when "
                        + type + " does not declare an owner type");
        }
        // may not declare that the annotated owner type is something other than the type's owner
        if (ownerType != null && annType != null && !ownerType.equals(annType.getType())) {
            throw new IllegalArgumentException(annType + "'s type does not match expected " + ownerType);
        }
        // create an empty one to avoid being completely null
        if (ownerType != null && annType == null) {
            annType = AnnotatedTypeFactory.newAnnotatedType(ownerType);
        }
        // if the annotated type does not have a usable equals, then recreate it to have one
        if (annType != null) {
            annType = AnnotatedTypeFactory.recreateAnnotatedTypeForEquals(annType);
        }
        return annType;
    }

    /**
     * Create a new {@link AnnotatedTypeImpl} copying parameters from an existing {@link AnnotatedType}.
     * Only basic {@link Class} type {@link AnnotatedType}s may be utilized in construction.
     * @param type {@link AnnotatedType} to copy parameters from.
     * @throws IllegalArgumentException <ul>
     * <li>When {@code type} is {@code null}</li>
     * <li>When {@code type.}{@link AnnotatedType#getAnnotations() getAnnotations()} is {@code null}</li>
     * <li>When {@code type.}{@link AnnotatedType#getAnnotations() getAnnotations()} contains a {@code null}</li>
     * <li>When {@code type.}{@code getAnnotatedOwnerType()} does not match {@code type.}{@link AnnotatedType#getType() getType()}'s owner type</li>
     * <li>When instantiated directly and {@code type} does not represent a non-array {@link Class}</li></ul>
     */
    public AnnotatedTypeImpl(AnnotatedType type) {
        this(type, Utils.getAnnotations(type));
    }

    /**
     * Create a new {@link AnnotatedTypeImpl} copying parameters from an existing {@link AnnotatedType}.
     * Only basic {@link Class} type {@link AnnotatedType}s may be utilized in construction.
     * @param type {@link AnnotatedType} to copy parameters from.
     * @param annotations {@link Annotation}s to utilize for this {@link AnnotatedType} type
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedType#getAnnotations() getAnnotations()} is {@code null}</li>
     *   <li>When {@code type.}{@link AnnotatedType#getAnnotations() getAnnotations()} contains a {@code null}</li>
     *   <li>When {@code type.}{@code getAnnotatedOwnerType()} does not match {@code type.}{@link AnnotatedType#getType() getType()}'s owner type</li>
     *   <li>When instantiated directly and {@code type} does not represent a non-array {@link Class}</li>
     * </ul>
     * @since 1.1
     */
    public AnnotatedTypeImpl(AnnotatedType type, Annotation[] annotations) {
        super(annotations);
        this.type = Utils.notNull(Utils.notNull(type, "type").getType(), "type.getType()");
        checkType("type.getType()");
        this.ownerType = checkOwnerType(this.type, Utils.getOwnerType(this.type), AnnotatedTypeOwner.getAnnotatedOwnerType(type));
    }

    /**
     * Create a new {@link AnnotatedTypeImpl} for the specified {@link Class}
     * @param type {@link Class} to decorate as an undecorated {@link AnnotatedType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type} is an array type</li>
     * </ul>
     */
    public AnnotatedTypeImpl(Class<?> type) {
        super();
        this.type = Utils.notNull(type, "type");
        checkType("type");
        this.ownerType = checkOwnerType(this.type, type.getEnclosingClass(), null);
    }

    /**
     * Create a new {@link AnnotatedTypeImpl} from the specified parameters
     * @param type {@link Type} that is annotated
     * @param ownerType {@link AnnotatedType} that possibly owns this type.
     * @param annotations {@link Annotation}s that annotate {@code type}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code ownerType} is not {@code null} and does not match {@code type}'s owner type</li>
     *   <li>when {@code annotations} contains a {@code null}</li>
     * </ul>
     */
    public AnnotatedTypeImpl(Type type, AnnotatedType ownerType, Annotation... annotations) {
        super(annotations);
        this.type = Utils.notNull(type, "type");
        checkType("type");
        this.ownerType = checkOwnerType(this.type, Utils.getOwnerType(type), ownerType);
    }

    /** {@link Type} that is annotated */
    protected final Type type;

    /** {@link AnnotatedType} that possibly owns this type. may be {@code null} */
    protected final AnnotatedType ownerType;

    /**
     * Perform validations against the {@link #type} field
     * @param name name of the source of the {@link #type} field
     */
    protected void checkType(String name) {
        // it is expected that derivatives override this, so no need to check if derived or not
        if (!(type instanceof Class) || Class.class.cast(type).isArray()) {
            throw new IllegalArgumentException(name + " does not represent a basic Class type, was " + type);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other) || !(other instanceof AnnotatedType)) {
            return false;
        }
        AnnotatedType o = (AnnotatedType) other;
        // for this comparison to work, ownerType must have a usable equals
        return type.equals(o.getType()) && AnnotatedTypeOwner.ownersAreEquivalent(this, o);
    }

    /**
     * <p>Returns the potentially annotated type that this type is a member of, if this type represents a nested type.
     * For example, if this type is {@code @TA O<T>.I<S>}, return a representation of {@code @TA O<T>}.</p>
     * <p>Returns {@code null} if this {@link AnnotatedType} represents a top-level type, or a local or anonymous class, or a primitive type, or void.</p>
     * <p>Returns {@code null} if this {@link AnnotatedType} is an instance of {@link AnnotatedArrayType},
     * {@link AnnotatedTypeVariable}, or {@link AnnotatedWildcardType}.
     * @return an {@link AnnotatedType} object representing the potentially annotated type that this type is a member of, or {@code null}
     */
    public AnnotatedType getAnnotatedOwnerType() {
        return ownerType;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return AnnotatedTypeHash.hashCode(this);
    }

    @Override
    public String toString() {
        return Utils.annsToString(annotations, Boolean.FALSE) + type.getTypeName();
    }
}
