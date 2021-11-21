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

/**
 * Prior to Java 12, the JVM AnnotatedTypes did not have a hash code method,
 * so using an arbitrary formula is sufficient
 */
final class AnnotatedTypeHash {

    private AnnotatedTypeHash() {}

    static final int hashCode(AnnotatedArrayTypeImpl impl) {
        return Utils.hash(hashCode((AnnotatedTypeImpl) impl), 127, impl.genericComponentType);
    }

    static final int hashCode(AnnotatedElementImpl impl) {
        return Utils.hash(0, 127, impl.getClass(), impl.getAnnotations());
    }

    static final int hashCode(AnnotatedParameterizedTypeImpl impl) {
        return Utils.hash(hashCode((AnnotatedTypeImpl) impl), 127, (Object[]) impl.actualTypeArguments);
    }

    static final int hashCode(AnnotatedTypeImpl impl) {
        return Utils.hash(hashCode((AnnotatedElementImpl) impl), 127, impl.getType(), impl.getAnnotatedOwnerType());
    }

    static final int hashCode(AnnotatedTypeVariableImpl impl) {
        return Utils.hash(hashCode((AnnotatedTypeImpl) impl), 127, (Object[]) impl.annotatedBounds);
    }

    static final int hashCode(AnnotatedWildcardTypeImpl impl) {
        return Utils.hash(hashCode((AnnotatedTypeImpl) impl), 127, impl.lowerBounds, impl.upperBounds);
    }
}
