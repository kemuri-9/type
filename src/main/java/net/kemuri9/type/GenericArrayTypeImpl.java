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
package net.kemuri9.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

/**
 * Implementation of {@link GenericArrayType}
 */
public class GenericArrayTypeImpl implements GenericArrayType {

    /** {@link Type} representing the component of the array */
    protected final Type genericComponentType;

    /**
     * Create a new {@link GenericArrayTypeImpl} with the specified component type.
     * This variation exists simplify to the scenario of nested {@link GenericArrayType} creation.
     * Without this variant, then a cast to {@link Type} is required and can be a bit cumbersome
     * @param componentType {@link Type} representing the array's component type
     * @throws IllegalArgumentException <ul>
     * <li>When {@code componentType} is {@code null}</li>
     * <li>When {@code componentType} is not a {@link GenericArrayType}, not a {@link ParameterizedType},
     *  and not a {@link TypeVariable}</li></ul>
     * @return new {@link GenericArrayTypeImpl} with the specified component type
     * @see #GenericArrayTypeImpl(Type)
     */
    public static GenericArrayTypeImpl withComponent(Type componentType) {
        return new GenericArrayTypeImpl(componentType);
    }

    /**
     * Create a new {@link GenericArrayTypeImpl} from an existing {@link GenericArrayType}
     * @param type {@link GenericArrayType} to copy details from
     * @throws IllegalArgumentException <ul>
     * <li>When {@code type} is {@code null}</li>
     * <li>When {@code type.}{@link GenericArrayType#getGenericComponentType() getGenericComponentType()} is {@code null}</li></ul>
     */
    public GenericArrayTypeImpl(GenericArrayType type) {
        genericComponentType = Utils.notNull(type, "type").getGenericComponentType();
        Utils.notNull(genericComponentType, "type.getGenericComponentType()");
    }

    /**
     * Create a new {@link GenericArrayTypeImpl} with the specified component type
     * @param componentType {@link Type} representing the array's component type
     * @throws IllegalArgumentException <ul>
     * <li>When {@code componentType} is {@code null}</li>
     * <li>When {@code componentType} is not a {@link GenericArrayType}, not a {@link ParameterizedType},
     *  and not a {@link TypeVariable}</li></ul>
     */
    public GenericArrayTypeImpl(Type componentType) {
        Utils.notNull(componentType, "componentType");
        Utils.isTrue(componentType instanceof ParameterizedType || componentType instanceof TypeVariable
                || componentType instanceof GenericArrayType, componentType,
                (t)-> t + " must be a GenericArrayType, a ParameterizedType, or a TypeVariable");
        this.genericComponentType = componentType;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof GenericArrayType)) {
            return false;
        }

        GenericArrayType o = (GenericArrayType) other;
        return genericComponentType.equals(o.getGenericComponentType());
    }

    @Override
    public Type getGenericComponentType() {
        return genericComponentType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(genericComponentType);
    }

    @Override
    public String toString() {
        return getGenericComponentType().getTypeName() + "[]";
    }
}
