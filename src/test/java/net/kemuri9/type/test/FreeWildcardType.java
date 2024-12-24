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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class FreeWildcardType implements WildcardType {

    /** has null lower bounds */
    public static final FreeWildcardType INVALID1 = new FreeWildcardType(null, new Type[] { Object.class });
    /** has null lower bound */
    public static final FreeWildcardType INVALID2 = new FreeWildcardType(new Type[] { null }, new Type[] { CharSequence.class });
    /** has null upper bounds */
    public static final FreeWildcardType INVALID3 = new FreeWildcardType(new Type[] { CharSequence.class }, null);
    /** has null upper bound */
    public static final FreeWildcardType INVALID4 = new FreeWildcardType(new Type[] { CharSequence.class }, new Type[] { null });
    /** has both extends and super bindings */
    public static final FreeWildcardType INVALID5 = new FreeWildcardType(new Type[] { CharSequence.class }, new Type[] { CharSequence.class });

    private final Type[] lowerBounds;
    private final Type[] upperBounds;

    public FreeWildcardType(Type[] lowerBounds, Type[] upperBounds) {
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    @Override
    public Type[] getLowerBounds() {
        return lowerBounds;
    }

    @Override
    public Type[] getUpperBounds() {
        return upperBounds;
    }
}
