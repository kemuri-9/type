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
package net.kemuri9.type.test;

import net.kemuri9.type.AnnotationImpl;

@SuppressWarnings("all")
public class Ann3Impl extends AnnotationImpl implements Ann3 {

    private static final long serialVersionUID = 4645446255875330993L;

    private final Ann4[] annVals;
    private final boolean[] booleanVals;
    private final byte[] byteVals;
    private final char[] charVals;
    private final Class<?>[] classVals;
    private final double[] doubleVals;
    private final Enum1[] enumVals;
    private final float[] floatVals;
    private final int[] intVals;
    private final long[] longVals;
    private final short[] shortVals;
    private final String[] stringVals;

    public Ann3Impl(Ann4[] annVals, boolean[] booleanVals, byte[] byteVals, char[] charVals,
            Class<?>[] classVals, double[] doubleVals, Enum1[] enumVals, float[] floatVals,
            int[] intVals, long[] longVals, short[] shortVals, String[] stringVals) {
        super(Ann3.class);
        this.annVals = annVals;
        this.booleanVals = booleanVals;
        this.byteVals = byteVals;
        this.charVals = charVals;
        this.classVals = classVals;
        this.doubleVals = doubleVals;
        this.enumVals = enumVals;
        this.floatVals = floatVals;
        this.longVals = longVals;
        this.intVals = intVals;
        this.shortVals = shortVals;
        this.stringVals = stringVals;
    }

    public Ann3Impl(Ann3 ann) {
        this(ann.annVals(), ann.booleanVals(), ann.byteVals(), ann.charVals(), ann.classVals(),
                ann.doubleVals(), ann.enumVals(), ann.floatVals(), ann.intVals(), ann.longVals(),
                ann.shortVals(), ann.stringVals());
    }

    @Override
    public Ann4[] annVals() {
        return annVals;
    }

    @Override
    public boolean[] booleanVals() {
        return booleanVals;
    }

    @Override
    public byte[] byteVals() {
        return byteVals;
    }

    @Override
    public char[] charVals() {
        return charVals;
    }

    @Override
    public Class<?>[] classVals() {
        return classVals;
    }

    @Override
    public double[] doubleVals() {
        return doubleVals;
    }

    @Override
    public Enum1[] enumVals() {
        return enumVals;
    }

    @Override
    public float[] floatVals() {
        return floatVals;
    }

    @Override
    public int[] intVals() {
        return intVals;
    }

    @Override
    public long[] longVals() {
        return longVals;
    }

    @Override
    public short[] shortVals() {
        return shortVals;
    }

    @Override
    public String[] stringVals() {
        return stringVals;
    }
}
