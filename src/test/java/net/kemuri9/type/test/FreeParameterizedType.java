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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class FreeParameterizedType implements ParameterizedType {

    /** all nulls */
    public static final FreeParameterizedType INVALID1 = new FreeParameterizedType(null, null, (Type[]) null);
    /** null type arguments */
    public static final FreeParameterizedType INVALID2 = new FreeParameterizedType(null, List.class, (Type[]) null);
    /** null raw type */
    public static final FreeParameterizedType INVALID3 = new FreeParameterizedType(null, null, new Type[] { Object.class });
    /** null type argument */
    public static final FreeParameterizedType INVALID4 = new FreeParameterizedType(null, List.class, new Type[] { null });

    private final Type ownerType;
    private final Type rawType;
    private final Type[] actualTypeArguments;

    public FreeParameterizedType(Type ownerType, Type rawType, Type... actualTypeArguments) {
        this.ownerType = ownerType;
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }
}
