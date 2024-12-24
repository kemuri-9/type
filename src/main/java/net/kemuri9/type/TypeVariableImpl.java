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
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * An implementation of {@link TypeVariable} for use when necessary.
 * It is unwise to actually use this in wide practice.
 * This is due to a restriction in the core {@link TypeVariable} implementation to where it is only equivalent to its own
 * type; it is not equivalent to other {@link TypeVariable} implementations that have the same attributes.
 * see JDK-8031984 and JDK-8039163 which state that this is an intended behavior of the core implementation.
 *
 * @param <D> the type of generic declaration that declared the underlying type variable.
 */
public class TypeVariableImpl<D extends GenericDeclaration> extends AnnotatedElementImpl implements TypeVariable<D> {

    /** {@link GenericDeclaration} that declared the {@link TypeVariable} */
    protected final D genericDeclaration;

    /** name of the type variable in the source code */
    protected final String name;

    /** annotated boundaries for the {@link TypeVariable} */
    protected final AnnotatedType[] annotatedBounds;

    /** boundaries for the {@link TypeVariable} */
    protected final Type[] bounds;

    /**
     * Create a new {@link TypeVariableImpl} from an existing {@link TypeVariable}
     * @param type {@link TypeVariable} to copy parameters from
     * @throws IllegalArgumentException <ul>
     * <li>When {@code type} is {@code null}</li>
     * <li>When {@code type.}{@link TypeVariable#getGenericDeclaration() getGenericDeclaration()} is {@code null}</li>
     * <li>When {@code type.}{@link TypeVariable#getName() getName()} is {@code null} or empty</li>
     * <li>When {@code type.}{@link TypeVariable#getGenericDeclaration() getGenericDeclaration()}
     *  does not declare a {@link TypeVariable} with the name from {@code type.}{@link TypeVariable#getName() getName()}</li>
     * <li>When {@code type.}{@link TypeVariable#getAnnotations() getAnnotations()} is a {@code null}</li>
     * <li>When {@code type.}{@link TypeVariable#getAnnotations() getAnnotations()} contains a {@code null}</li>
     * <li>When {@code type.}{@link TypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is {@code null}</li>
     * <li>When {@code type.}{@link TypeVariable#getAnnotatedBounds() getAnnotatedBounds()} is empty</li>
     * <li>When {@code type.}{@link TypeVariable#getAnnotatedBounds() getAnnotatedBounds()} contains a {@code null}</li></ul>
     */
    public TypeVariableImpl(TypeVariable<D> type) {
        this(Utils.notNull(type, "type").getGenericDeclaration(), type.getName(),
                Utils.notNull(type.getAnnotations(), "type.getAnnotations()"), type.getAnnotatedBounds());
    }

    /**
     * Create a new {@link TypeVariableImpl} from the specified parameters {@link TypeVariable}
     * @param genericDeclaration {@link GenericDeclaration} that declared the {@link TypeVariable}
     * @param name name of the TypeVariable, as it occurs in the source code
     * @param annotations {@link Annotation}s that decorate the {@link TypeVariable}
     * @param bounds {@link Type}s that declare the bounds of the {@link TypeVariable}
     * @throws IllegalArgumentException <ul>
     * <li>When {@code type} is {@code null}</li>
     * <li>When {@code genericDeclaration} is {@code null}</li>
     * <li>When {@code name} is {@code null} or empty</li>
     * <li>When {@code genericDeclaration} does not declare a {@link TypeVariable} with the specified {@code name}</li>
     * <li>When {@code annotations} contains a {@code null}</li>
     * <li>When {@code bounds} is {@code null}</li>
     * <li>When {@code bounds} contains a {@code null}</li>
     * <li>When {@code bounds} contains an unrecognized {@link Type}</li></ul>
     */
    public TypeVariableImpl(D genericDeclaration, String name, Annotation[] annotations, Type... bounds) {
        this(genericDeclaration, name, annotations, AnnotatedTypeFactory.newAnnotatedTypes(Utils.noNullContained(bounds, "bounds"), null));
    }

    /**
     * Create a new {@link TypeVariableImpl} from the specified parameters {@link TypeVariable}
     * @param genericDeclaration {@link GenericDeclaration} that declared the {@link TypeVariable}
     * @param name name of the TypeVariable, as it occurs in the source code
     * @param annotations {@link Annotation}s that decorate the {@link TypeVariable}
     * @param annotatedBounds {@link AnnotatedType}s that declare the bounds of the {@link TypeVariable}
     * @throws IllegalArgumentException <ul>
     * <li>When {@code type} is {@code null}</li>
     * <li>When {@code genericDeclaration} is {@code null}</li>
     * <li>When {@code name} is {@code null} or empty</li>
     * <li>When {@code genericDeclaration} does not declare a {@link TypeVariable} with the specified {@code name}</li>
     * <li>When {@code annotations} contains a {@code null}</li>
     * <li>When {@code annotatedBounds} is {@code null}</li>
     * <li>When {@code annotatedBounds} is empty</li>
     * <li>When {@code annotatedBounds} contains a {@code null}</li>
     * <li>When {@code annotatedBounds} contains an unrecognized {@link AnnotatedType}</li>
     * <li>When {@code annotatedBounds} contains an invalid {@link AnnotatedType}</li></ul>
     */
    public TypeVariableImpl(D genericDeclaration, String name, Annotation[] annotations, AnnotatedType... annotatedBounds) {
        super(annotations);
        this.genericDeclaration = Utils.notNull(genericDeclaration, "genericDeclaration");
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        this.name = name;
        // verify that the generic actual declares a TV of the same name
        TypeVariable<?>[] declTypeVars = Utils.notNull(genericDeclaration.getTypeParameters(), "genericDeclaration.getTypeParameters()");
        Arrays.stream(declTypeVars).filter(this::equals).findAny()
            .orElseThrow(()-> new IllegalArgumentException(this + " is not declared on " + genericDeclaration));
        // all type variables have bounds, though it might just be against Object
        this.annotatedBounds = Utils.notEmpty(annotatedBounds, "annotatedBounds");
        // this is primarily used not for equals but for validity checks of the incoming data
        AnnotatedTypeFactory.recreateAnnotatedTypesForEquals(annotatedBounds);
        // since bounds are constant, pre-calculate the "plain" boundaries from the annotated variants
        this.bounds = new Type[annotatedBounds.length];
        for (int idx = 0; idx < bounds.length; ++idx) {
            bounds[idx] = annotatedBounds[idx].getType();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof TypeVariable)) {
            return false;
        }

        TypeVariable<?> o = (TypeVariable<?>) other;
        return genericDeclaration.equals(o.getGenericDeclaration()) && name.equals(o.getName());
    }

    @Override
    public Type[] getBounds() {
        // clone to avoid modifications by caller
        return Utils.clone(bounds);
    }

    @Override
    public D getGenericDeclaration() {
        return genericDeclaration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        // clone to avoid modifications by caller
        return Utils.clone(annotatedBounds);
    }

    @Override
    public int hashCode() {
        return genericDeclaration.hashCode() ^ name.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
