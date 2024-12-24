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
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * java 9 and higher properly generate the annotated boundary to match the plain boundary.
 */
final class AnnotatedWildcardTypeEquals {

    static void checkBoundaries(Type[] lb, Type[] ub, AnnotatedType[] annLB, AnnotatedType[] annUB) {
        Utils.checkMatching(lb, annLB);
        Utils.checkMatching(ub, annUB);
    }

    static boolean isEqual(AnnotatedWildcardTypeImpl left, AnnotatedWildcardType right) {
        return Arrays.equals(left.lowerBounds, right.getAnnotatedLowerBounds()) &&
                Arrays.equals(left.upperBounds, right.getAnnotatedUpperBounds());
    }

    private AnnotatedWildcardTypeEquals() {}
}
