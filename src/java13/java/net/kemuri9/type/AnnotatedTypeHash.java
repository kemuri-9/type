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

import java.util.Objects;

/**
 * Starting in Java 12, the JVM AnnotatedTypes have a hash code method, so the implementations here should match
 */
final class AnnotatedTypeHash {

    static final int hashCode(AnnotatedArrayTypeImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^ impl.genericComponentType.hashCode();
    }

    static final int hashCode(AnnotatedElementImpl impl) {
        return Objects.hash((Object[]) impl.getAnnotations());
    }

    static final int hashCode(AnnotatedParameterizedTypeImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^ Objects.hash((Object[]) impl.actualTypeArguments);
    }

    static final int hashCode(AnnotatedTypeImpl impl) {
        return impl.getType().hashCode() ^ hashCode((AnnotatedElementImpl) impl) ^ Objects.hash(impl.getAnnotatedOwnerType());
    }

    static final int hashCode(AnnotatedTypeVariableImpl impl) {
        // From java 13, AnnotatedTypeVariable does not have its own hash code implementation
        return hashCode((AnnotatedTypeImpl) impl);
    }

    static final int hashCode(AnnotatedWildcardTypeImpl impl) {
        return hashCode((AnnotatedTypeImpl) impl) ^
                Objects.hash((Object[]) impl.lowerBounds) ^
                Objects.hash((Object[]) impl.upperBounds);
    }

    private AnnotatedTypeHash() {}
}
