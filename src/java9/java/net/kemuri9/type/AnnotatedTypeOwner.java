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

import java.lang.reflect.AnnotatedType;
import java.util.Objects;

/**
 * In java 9, AnnotatedType owner was added as part of the standard API, so everything is simple!
 */
final class AnnotatedTypeOwner {

    static AnnotatedType getAnnotatedOwnerType(AnnotatedType type) {
        return type.getAnnotatedOwnerType();
    }

    static boolean ownersAreEquivalent(AnnotatedTypeImpl left, AnnotatedType right) {
        return Objects.equals(left.getAnnotatedOwnerType(), right.getAnnotatedOwnerType());
    }

    private AnnotatedTypeOwner() {
        // not instantiable
    }
}
