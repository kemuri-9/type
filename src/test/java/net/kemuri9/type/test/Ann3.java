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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Ann3 {

    public Ann4[] annVals();
    public boolean[] booleanVals();
    public byte[] byteVals();
    public char[] charVals();
    public Class<?>[] classVals();
    public double[] doubleVals();
    public Enum1[] enumVals();
    public float[] floatVals();
    public int[] intVals();
    public long[] longVals();
    public short[] shortVals();
    public String[] stringVals();
}
