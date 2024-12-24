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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Implementation of {@link ParameterizedType}
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    /** Raw {@link Type} that is parameterized */
    protected final Type rawType;

    /** {@link Type} that this {@link Type} is owned by (is nested within) */
    protected final Type ownerType;

    /** {@link Type} arguments that parameterize {@link #rawType} */
    protected final Type[] actualTypeArguments;

    /**
     * Create a {@link ParameterizedTypeImpl} with the specified parameters
     * @param parameterizedType {@link ParameterizedType} to copy details from
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code parameterizedType} is {@code null}</li>
     *   <li>When {@code parameterizedType} is invalid</li>
     * </ul>
     * @see #ParameterizedTypeImpl(Type, Type, Type...)
     */
    public ParameterizedTypeImpl(ParameterizedType parameterizedType) {
        this(Utils.notNull(parameterizedType, "parameterizedType").getOwnerType(), parameterizedType.getRawType(),
                parameterizedType.getActualTypeArguments());
    }

    /**
     * Create a {@link ParameterizedTypeImpl} with the specified parameters
     * @param ownerType {@link Type} that owns this type.
     *  In different terms, the {@link Type} that this type is nested within. may be {@code null}
     * @param rawType {@link Type} that is being parameterized
     * @param actualTypeArguments {@link Type} arguments that parameterize {@code rawType}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code rawType} is {@code null}</li>
     *   <li>When {@code actualTypeArguments} is {@code null}</li>
     *   <li>When {@code actualTypeArguments} contains a {@code null}</li>
     * </ul>
     */
    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... actualTypeArguments) {
        this.ownerType = ownerType;
        this.rawType = Utils.notNull(rawType, "rawType");
        // clone to avoid modifications by caller
        this.actualTypeArguments = Utils.checkedClone(actualTypeArguments, "actualTypeArguments");
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType o2 = (ParameterizedType) object;
        return Objects.equals(o2.getOwnerType(), ownerType) &&
                Objects.equals(o2.getRawType(), rawType) &&
                Arrays.equals(o2.getActualTypeArguments(), actualTypeArguments);
    }

    @Override
    public Type[] getActualTypeArguments() {
        // clone to avoid callers modifying the data
        return Utils.clone(actualTypeArguments);
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(actualTypeArguments) ^ Objects.hashCode(ownerType) ^ Objects.hashCode(rawType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (ownerType != null) {
            sb.append(ownerType.getTypeName());
            sb.append("$");

            /* Find simple name of nested type by removing the shared prefix with owner.
             * if the owner is a parameterized type, unwrap it once to get to the root type */
            Type ownerType2 = ownerType;
            if (ownerType2 instanceof ParameterizedType) {
                ownerType2 = ((ParameterizedType) ownerType2).getRawType();
            }

            String rawTypeName = rawType.getTypeName().replace(ownerType2.getTypeName() + "$", "");
            sb.append(rawTypeName);
        } else {
            sb.append(rawType.getTypeName());
        }

        // add type args
        StringJoiner sj = new StringJoiner(", ", "<", ">");
        sj.setEmptyValue("");
        for (Type t : actualTypeArguments) {
            sj.add(t.getTypeName());
        }
        sb.append(sj);

        return sb.toString();
    }
}
