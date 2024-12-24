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

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * In Java 8, the Annotated Type owner was not available as part of the AnnotatedType API.
 * As such it needs a bit of logic to support it for implementations that have it regardless.
 */
final class AnnotatedTypeOwner {

    /**
     * Type to store the result of the lookup as well as if the lookup operation succeeded.
     */
    private static final class OwnerTypeLookup {
        /** constant for the FAILED scenario */
        private static final OwnerTypeLookup FAILED = new OwnerTypeLookup();

        final AnnotatedType owner;
        final boolean succeded;

        private OwnerTypeLookup(AnnotatedType owner) {
            this.owner = owner;
            this.succeded = true;
        }

        private OwnerTypeLookup() {
            this.owner = null;
            this.succeded = false;
        }

    }

    static AnnotatedType getAnnotatedOwnerType(AnnotatedType type) {
        return lookupAnnotatedOwnerType(type).owner;
    }

    private static OwnerTypeLookup lookupAnnotatedOwnerType(AnnotatedType type) {
        /* annotated type owner was added in java 9, but some implementations supporting java 8 (like ours) may have it,
         * so it needs to be checked respectively */
        if (type instanceof AnnotatedTypeImpl) {
            return new OwnerTypeLookup(((AnnotatedTypeImpl) type).getAnnotatedOwnerType());
        }
        // other types may support it, so try through reflection as well
        try {
            AnnotatedType owner = (AnnotatedType) type.getClass().getMethod("getAnnotatedOwnerType").invoke(type);
            return new OwnerTypeLookup(owner);
        } catch (NoSuchMethodException | ClassCastException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
            return OwnerTypeLookup.FAILED;
        }
    }

    static boolean ownersAreEquivalent(AnnotatedTypeImpl left, AnnotatedType right) {
        // since annotated ownership was added in java9, the right type may not have an annotated owner
        AnnotatedType leftOwner = left.getAnnotatedOwnerType();
        OwnerTypeLookup rightLookup = lookupAnnotatedOwnerType(right);
        // when it does, then it can be used as-is
        if (rightLookup.succeded) {
            return Objects.equals(leftOwner, rightLookup.owner);
        }
        /* but when there is no available annotated owner, the right's type's owner
         * has to be checked against the left's annotated owner's type */
        Type rightOwner = Utils.getOwnerType(right.getType());
        if ((rightOwner == null) == (leftOwner == null)) {
            return true;
        }
        return (leftOwner != null) && Objects.equals(leftOwner.getType(), rightOwner);
    }

    private AnnotatedTypeOwner() {
        // not instantiable
    }
}
