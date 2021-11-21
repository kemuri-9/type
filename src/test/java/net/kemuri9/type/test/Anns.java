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
package net.kemuri9.type.test;

@Ann1
public class Anns {

    @Ann1
    public static void ann1() {}

    @Ann2(boolVal = true, byteVal = 34, charVal = 't', classVal = StringBuilder.class,
            doubleVal = Double.NaN, enumVal = Enum1.VALUE3, floatVal = Float.MIN_VALUE,
            intVal = 79098700, longVal = 80808450580845l, shortVal = 25509, stringVal = "booo\"hoo")
    public static void ann21() {}

    @Ann3(annVals = {@Ann4("one"), @Ann4("two"), @Ann4("\"five\"")},
            booleanVals = {true, true, false},
            byteVals = {3, 5, 7, 9, 11},
            charVals = {'a', 'd', 'e', 'z'},
            classVals = {Integer.class, Long.class, Number.class, StringBuilder.class, boolean[].class, String[][].class},
            doubleVals = {Double.NaN, Math.PI},
            enumVals = {Enum1.VALUE1, Enum1.VALUE3, Enum1.VALUE2},
            floatVals = {1.0f, 2.0f, 3.0f, 4.0f},
            intVals = {16816168, 214861861, 1687468137},
            longVals = {900808007700L, 907304720489450L, 830087978070L},
            shortVals = {400, 500, 600},
            stringVals = {"one", "seven", "eleven"})
    public static void ann31() {}
}
