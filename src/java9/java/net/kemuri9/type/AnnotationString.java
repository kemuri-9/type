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

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.StringJoiner;

/**
 * In Java 9, the toString of annotations was altered to where it is able to be parsed back into the annotation data.
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
        if (value instanceof Character) {
            Character c = (Character) value;
            StringBuilder sb = new StringBuilder(4);
            sb.append('\'');
            if (c == '\'') {
                sb.append("\\'");
            } else {
                sb.append(c);
            }
            return sb.append('\'').toString();
        } else if (value instanceof Class) {
            Class<?> finalComponent = (Class<?>) value;
            StringBuilder arrayBackets = new StringBuilder();

            while (finalComponent.isArray()) {
                finalComponent = finalComponent.getComponentType();
                arrayBackets.append("[]");
            }

            return finalComponent.getName() + arrayBackets.toString() + ".class" ;
        } else if (value instanceof Double) {
            Double d = (Double) value;
            if (Double.isFinite(d)) {
                return Double.toString(d);
            } else if (Double.isInfinite(d)) {
                return (d < 0.0f) ? "-1.0/0.0": "1.0/0.0";
            } else {
                return "0.0/0.0";
            }
        } else if (value instanceof Float) {
            Float f = (Float) value;
            if (Float.isFinite(f)) {
                return Float.toString(f) + "f";
            } else if (Float.isInfinite(f)) {
                return (f < 0.0f) ? "-1.0f/0.0f": "1.0f/0.0f";
            } else {
                return "0.0f/0.0f";
            }
        } else if (value instanceof Long) {
            Long l = (Long) value;
            String str = String.valueOf(l);
            return (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) ? (str + 'L') : str;
        } else if (value instanceof String) {
            String s = (String) value;
            StringBuilder sb = new StringBuilder();
            sb.append('"');
            sb.append(s.replace("\"", "\\\""));
            sb.append('"');
            return sb.toString();
        } else if (value.getClass().isArray()) {
            StringJoiner sj = new StringJoiner(", ", "{", "}");
            int length = Array.getLength(value);
            for (int idx = 0; idx < length; ++idx) {
                sj.add(toString(Array.get(value, idx)));
            }
            return sj.toString();
        }
        return value.toString();
    }

    private AnnotationString() {}
}
