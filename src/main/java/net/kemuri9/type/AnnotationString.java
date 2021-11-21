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

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.StringJoiner;

/**
 * In java 8 the string representation of annotations is rather basic and cannot be reused to parse back the annotation
 */
final class AnnotationString {

    static String getNamePrefix(Member[] members, Member curMember) {
        return curMember.getName() + "=";
    }

    /**
     * Convert an annotation value to String
     * @param value annotation value to convert to a string
     * @return String representing the annotation value
     */
    static String toString(Object value) {
        if (!value.getClass().isArray()) {
            return value.toString();
        }
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        int length = Array.getLength(value);
        for (int idx = 0; idx < length; ++idx) {
            sj.add(toString(Array.get(value, idx)));
        }
        return sj.toString();
    }

    private AnnotationString() {}
}
