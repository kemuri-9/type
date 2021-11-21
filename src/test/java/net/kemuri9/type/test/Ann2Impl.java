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

import net.kemuri9.type.AnnotationImpl;

@SuppressWarnings("all")
public class Ann2Impl extends AnnotationImpl implements Ann2 {

    private static final long serialVersionUID = 6960298009740092722L;

    private final boolean boolVal;
    private final byte byteVal;
    private final Class<?> classVal;
    private final char charVal;
    private final double doubleVal;
    private final Enum1 enumVal;
    private final float floatVal;
    private final long longVal;
    private final int intVal;
    private final short shortVal;
    private final String stringVal;

    public Ann2Impl(boolean boolVal, byte byteVal, Class<?> classVal, char charVal, double doubleVal,
            Enum1 enumVal, float floatVal, long longVal, int intVal, short shortVal, String stringVal) {
        super(Ann2.class);
        this.boolVal = boolVal;
        this.byteVal = byteVal;
        this.classVal = classVal;
        this.charVal = charVal;
        this.doubleVal = doubleVal;
        this.enumVal = enumVal;
        this.floatVal = floatVal;
        this.longVal = longVal;
        this.intVal = intVal;
        this.shortVal = shortVal;
        this.stringVal = stringVal;
    }

    public Ann2Impl(Ann2 copy) {
        this(copy.boolVal(), copy.byteVal(), copy.classVal(), copy.charVal(), copy.doubleVal(), copy.enumVal(),
                copy.floatVal(), copy.longVal(), copy.intVal(), copy.shortVal(), copy.stringVal());
    }

    @Override
    public boolean boolVal() {
        return boolVal;
    }

    @Override
    public byte byteVal() {
        return byteVal;
    }

    @Override
    public Class<?> classVal() {
        return classVal;
    }

    @Override
    public char charVal() {
        return charVal;
    }

    @Override
    public double doubleVal() {
        return doubleVal;
    }

    @Override
    public Enum1 enumVal() {
        return enumVal;
    }

    @Override
    public float floatVal() {
        return floatVal;
    }

    @Override
    public long longVal() {
        return longVal;
    }

    @Override
    public int intVal() {
        return intVal;
    }

    @Override
    public short shortVal() {
        return shortVal;
    }

    @Override
    public String stringVal() {
        return stringVal;
    }
}
