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

import java.util.Objects;

/**
 * Starting in Java 12, the JVM AnnotatedTypes have a hash code method, so the implementations here should match
 */
final class AnnotatedTypeHash {

    static int hashCode(AnnotatedArrayTypeImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^ impl.genericComponentType.hashCode();
    }

    static int hashCode(AnnotatedElementImpl impl) {
        return Objects.hash((Object[]) impl.getAnnotations());
    }

    static int hashCode(AnnotatedParameterizedTypeImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^ Objects.hash((Object[]) impl.actualTypeArguments);
    }

    static int hashCode(AnnotatedTypeImpl impl) {
        return impl.getType().hashCode() ^ hashCode((AnnotatedElementImpl) impl) ^ Objects.hash(impl.getAnnotatedOwnerType());
    }

    static int hashCode(AnnotatedTypeVariableImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^ Objects.hash((Object[]) impl.annotatedBounds);
    }

    static int hashCode(AnnotatedWildcardTypeImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^
                Objects.hash((Object[]) impl.lowerBounds) ^
                Objects.hash((Object[]) impl.upperBounds);
    }

    private AnnotatedTypeHash() {}
}
