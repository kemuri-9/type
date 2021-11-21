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
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * in java 8, the upper annotated boundaries may not generate if the upper boundary is a plain object.
 * So the standard check of checking if the annotated boundaries are equivalent length and comparable types is unusable
 */
final class AnnotatedWildcardTypeEquals {

    private static final AnnotatedType[] PLAIN_OBJECT = new AnnotatedType[] { new AnnotatedTypeImpl(Object.class) };

    static void checkBoundaries(Type[] lb, Type[] ub, AnnotatedType[] annLB, AnnotatedType[] annUB) {
        Utils.checkMatching(lb, annLB);
        if (lb.length > 0 && annUB.length == 0 && ub.length == 1 && ub[0].equals(Object.class)) {
            // then this is ok
        } else {
            Utils.checkMatching(ub, annUB);
        }
    }

    private static AnnotatedType[] updateUpperBoundsForBrokenJVM(Type[] ub, AnnotatedType[] annUB) {
        return (ub.length == 1 && Object.class.equals(ub[0]) && annUB.length == 0) ? PLAIN_OBJECT : annUB;
    }

    static boolean isEqual(AnnotatedWildcardTypeImpl left, AnnotatedWildcardType right) {
        if (!Arrays.equals(left.lowerBounds, right.getAnnotatedLowerBounds())) {
            return false;
        }
        AnnotatedType[] leftAnnUB = left.upperBounds;
        AnnotatedType[] rightAnnUB = right.getAnnotatedUpperBounds();
        if (Arrays.equals(leftAnnUB, rightAnnUB)) {
            return true;
        }
        // can be discrepancy in annotated upper bounds when the boundary is just Object.
        // due to equivalence check in AnntoatedTypeImpl, the types are already proven to be equivalent!
        Type[] ub = left.getType().getUpperBounds();
        leftAnnUB = updateUpperBoundsForBrokenJVM(ub, leftAnnUB);
        rightAnnUB = updateUpperBoundsForBrokenJVM(ub, rightAnnUB);
        return Arrays.equals(leftAnnUB, rightAnnUB);
    }

    private AnnotatedWildcardTypeEquals() {}
}
