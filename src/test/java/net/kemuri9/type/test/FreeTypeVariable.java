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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.kemuri9.type.AnnotatedElementImpl;
import net.kemuri9.type.AnnotatedTypeImpl;

public class FreeTypeVariable<D extends GenericDeclaration> extends AnnotatedElementImpl implements TypeVariable<D> {

    public static final class GenericDeclarationImpl extends AnnotatedElementImpl implements GenericDeclaration {

        protected final List<TypeVariable<?>> typeParameters = new ArrayList<>();

        public void addTypeParameters(TypeVariable<?>... typeParameters) {
            Collections.addAll(this.typeParameters, typeParameters);
        }

        @Override
        public TypeVariable<?>[] getTypeParameters() {
            return typeParameters.toArray(new TypeVariable<?>[typeParameters.size()]);
        }
    }

    public static final class GenericDeclarationNull extends AnnotatedElementImpl implements GenericDeclaration {

        @Override
        public TypeVariable<?>[] getTypeParameters() {
            return null;
        }
    }

    /** GenericDeclaration is {@code null} */
    public static final FreeTypeVariable<?> INVALID1 = new FreeTypeVariable<>(new Annotation[0], null, "test");
    /** name is {@code null} */
    public static final FreeTypeVariable<?> INVALID2;
    /** name is empty */
    public static final FreeTypeVariable<?> INVALID3;
    /** GenericDeclaration has {@code null} type parameters */
    public static final FreeTypeVariable<?> INVALID4 = new FreeTypeVariable<>(new Annotation[0], new GenericDeclarationNull(), "T");
    /** GenericDeclaration does not include the TypeVariable */
    public static final FreeTypeVariable<?> INVALID5 = new FreeTypeVariable<>(new Annotation[0], new GenericDeclarationImpl(), "T");
    /** annotations are {@code null} */
    public static final FreeTypeVariable<?> INVALID6;
    /** annotations contains a {@code null} */
    public static final FreeTypeVariable<?> INVALID7;
    /** annotated bounds is null */
    public static final FreeTypeVariable<?> INVALID8;
    /** annotated bounds is empty */
    public static final FreeTypeVariable<?> INVALID9;
    /** annotated bounds contains a null */
    public static final FreeTypeVariable<?> INVALID10;
    /** unrecognized annotated bound */
    public static final FreeTypeVariable<?> INVALID11;
    /** invalid annotated bound */
    public static final FreeTypeVariable<?> INVALID12;

    public static final List<FreeTypeVariable<?>> INVALID;

    static {
        GenericDeclarationImpl genDecl = new GenericDeclarationImpl();
        INVALID2 = new FreeTypeVariable<>(new Annotation[0], genDecl, null, new AnnotatedTypeImpl(Object.class));
        genDecl.addTypeParameters(INVALID2);

        genDecl = new GenericDeclarationImpl();
        INVALID3 = new FreeTypeVariable<>(new Annotation[0], genDecl, "", new AnnotatedTypeImpl(Object.class));
        genDecl.addTypeParameters(INVALID3);

        genDecl = new GenericDeclarationImpl();
        INVALID6 = new FreeTypeVariable<>(null, genDecl, "T", new AnnotatedTypeImpl(Object.class));
        genDecl.addTypeParameters(INVALID6);

        genDecl = new GenericDeclarationImpl();
        INVALID7 = new FreeTypeVariable<>(new Annotation[] { null }, genDecl, "T", new AnnotatedTypeImpl(Object.class));
        genDecl.addTypeParameters(INVALID7);

        genDecl = new GenericDeclarationImpl();
        INVALID8 = new FreeTypeVariable<>(new Annotation[0], genDecl, "T", (AnnotatedType[]) null);
        genDecl.addTypeParameters(INVALID8);

        genDecl = new GenericDeclarationImpl();
        INVALID9 = new FreeTypeVariable<>(new Annotation[0], genDecl, "T");
        genDecl.addTypeParameters(INVALID9);

        genDecl = new GenericDeclarationImpl();
        INVALID10 = new FreeTypeVariable<>(new Annotation[0], genDecl, "T", (AnnotatedType) null);
        genDecl.addTypeParameters(INVALID10);

        genDecl = new GenericDeclarationImpl();
        INVALID11 = new FreeTypeVariable<>(new Annotation[0], genDecl, "T", new UnknownAnnotatedType(null));
        genDecl.addTypeParameters(INVALID11);

        genDecl = new GenericDeclarationImpl();
        INVALID12 = new FreeTypeVariable<>(new Annotation[0], genDecl, "T", new FreeAnnotatedType(new UnknownType(), null, null));
        genDecl.addTypeParameters(INVALID12);

        INVALID = Arrays.asList(INVALID1, INVALID2, INVALID3, INVALID4, INVALID5, INVALID6,
                INVALID7, INVALID8, INVALID9, INVALID10, INVALID11, INVALID12);
    }

    protected final Annotation[] annotations;
    protected final D genericDeclaration;
    protected final String name;
    protected final AnnotatedType[] annotatedBounds;

    public FreeTypeVariable(Annotation[] annotations, D genericDeclaration, String name, AnnotatedType... annotatedBounds) {
        super();
        this.annotations = annotations;
        this.genericDeclaration = genericDeclaration;
        this.name = name;
        this.annotatedBounds = annotatedBounds;
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotations;
    }

    @Override
    public Type[] getBounds() {
        return TestUtils.getTypes(annotatedBounds);
    }

    @Override
    public D getGenericDeclaration() {
        return genericDeclaration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        return annotatedBounds;
    }
}
