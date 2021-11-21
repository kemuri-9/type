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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

import net.kemuri9.type.AnnotatedElementImpl;

public class FreeAnnotatedType extends AnnotatedElementImpl implements AnnotatedType {

    protected final Type type;
    protected final AnnotatedType ownerType;
    protected final Annotation[] annotations;

    public FreeAnnotatedType(Type type, AnnotatedType ownerType, Annotation[] annotations) {
        super(Object.class);
        this.type = type;
        this.ownerType = ownerType;
        this.annotations = annotations;
    }

    public AnnotatedType getAnnotatedOwnerType() {
        return ownerType;
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotations;
    }

    @Override
    public Type getType() {
        return type;
    }
}
