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

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

/**
 * Factory for creating instances of {@link AnnotatedType}
 */
public class AnnotatedTypeFactory {

    /** registry of types and whether they have a usable equals on them */
    private static final Map<Class<?>, Boolean> USABLE_EQUALS = new java.util.concurrent.ConcurrentHashMap<>();

    private static final Object[] EMPTY = new Object[0];

    static {
        // the types in this package have usable equals methods
        for (Class<?> type : new Class<?>[] { AnnotatedArrayType.class, AnnotatedParameterizedTypeImpl.class,
            AnnotatedTypeImpl.class, AnnotatedTypeVariableImpl.class, AnnotatedWildcardTypeImpl.class}) {
            USABLE_EQUALS.put(type, Boolean.TRUE);
        }
    }

    /**
     * Check that any specified {@link AnnotatedType} represents the corresponding {@link Type}.
     * When the {@link AnnotatedType} is {@code null}, create an empty one from the {@link Type}.
     * @param types {@link Type} array to check being annotated by {@code annotated}
     * @param annotated {@link AnnotatedType} array to check being an annotation of {@code types}
     * @return verified {@code annotated}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code types} is {@code null} or contains a {@code null}</li>
     *   <li>When {@code annotated} is not {@code null} and does not represent the corresponding {@code type}</li>
     * </ul>
     */
    static AnnotatedType[] checkAnnotated(Type[] types, AnnotatedType[] annotated) {
        AnnotatedType[] annTypes = new AnnotatedType[types.length];
        Utils.noNullContained(types, "types");
        for (int idx = 0; idx < types.length; ++idx) {
            Type type = types[idx];
            AnnotatedType override = Utils.get(annotated, idx);
            if (override != null) {
                Utils.checkMatching(type, override);
                override = recreateAnnotatedTypeForEquals(override);
            } else {
                override = newAnnotatedType(type);
            }
            annTypes[idx] = override;
        }
        return annTypes;
    }

    /**
     * Create a new {@link AnnotatedType} from the specified {@link AnnotatedType}
     * @param <T> type of {@link AnnotatedType}
     * @param type {@link AnnotatedType} to create a new {@link AnnotatedType} for
     * @return new {@link AnnotatedType} from {@code type}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type} does not represent a supported {@link AnnotatedType}</li>
     * </ul>
     */
    public static <T extends AnnotatedType> T newAnnotatedType(T type) {
        Utils.notNull(type, "type");
        return newAnnotatedType(type, Utils.getAnnotations(type));
    }

    /**
     * Create a new {@link AnnotatedType} from the specified {@link AnnotatedType}
     * @param <T> type of {@link AnnotatedType}
     * @param type {@link AnnotatedType} to create a new {@link AnnotatedType} for
     * @param annotations {@link Annotation}s to utilize for the annotations on the {@link AnnotatedType}
     * @return new {@link AnnotatedType} from {@code type}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type} does not represent a supported {@link AnnotatedType}</li>
     * </ul>
     * @since 1.1
     */
    public static <T extends AnnotatedType> T newAnnotatedType(T type, Annotation[] annotations) {
        Utils.notNull(type, "type");
        if (type instanceof AnnotatedArrayType) {
            return Utils.cast(new AnnotatedArrayTypeImpl((AnnotatedArrayType) type, annotations));
        } else if (type instanceof AnnotatedParameterizedType) {
            return Utils.cast(new AnnotatedParameterizedTypeImpl((AnnotatedParameterizedType) type, annotations));
        } else if (type instanceof AnnotatedTypeVariable) {
            return Utils.cast(new AnnotatedTypeVariableImpl((AnnotatedTypeVariable) type, annotations));
        } else if (type instanceof AnnotatedWildcardType) {
            return Utils.cast(new AnnotatedWildcardTypeImpl((AnnotatedWildcardType) type, annotations));
        }
        return Utils.cast(new AnnotatedTypeImpl(type, annotations));
    }

    /**
     * Create a new {@link AnnotatedType} from the specified parameters
     * @param <T> Type of {@link AnnotatedType} that is generated from {@code type}
     * @param type {@link Type} "plain" type to annotate
     * @param annotations {@link Annotation} array that annotates {@code type}
     * @return new {@link AnnotatedType} from the parameters
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     *   <li>When {@code extraArgs} represents a usable argument, but is invalid</li>
     * </ul>
     * @throws UnsupportedOperationException When {@code type} is an unrecognized {@link Type}
     * @see AnnotatedArrayTypeImpl
     * @see AnnotatedParameterizedTypeImpl
     * @see AnnotatedTypeImpl
     * @see AnnotatedTypeVariableImpl
     * @see AnnotatedWildcardTypeImpl
     */
    public static <T extends AnnotatedType> T newAnnotatedType(Type type, Annotation... annotations) {
        return newAnnotatedType(type, annotations, EMPTY);
    }

    /**
     * Create a new {@link AnnotatedType} from the specified parameters
     * @param <T> Type of {@link AnnotatedType} that is generated from {@code type}
     * @param type {@link Type} "plain" type to annotate
     * @param annotations {@link Annotation} array that annotates {@code type}
     * @param extraArgs any extra arguments that can be utilized to create the {@link AnnotatedType}
     * @return new {@link AnnotatedType} from the parameters
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code annotations} contains a {@code null}</li>
     *   <li>When {@code extraArgs} represents a usable argument, but is invalid</li>
     * </ul>
     * @throws UnsupportedOperationException When {@code type} is an unrecognized {@link Type}
     * @see AnnotatedArrayTypeImpl
     * @see AnnotatedParameterizedTypeImpl
     * @see AnnotatedTypeImpl
     * @see AnnotatedTypeVariableImpl
     * @see AnnotatedWildcardTypeImpl
     */
    public static <T extends AnnotatedType> T newAnnotatedType(Type type, Annotation[] annotations, Object... extraArgs) {
        Utils.notNull(type, "type");
        extraArgs = Utils.defaultValue(extraArgs, EMPTY);
        // in general an AnnotatedType being specified has greater priority over Annotations being specified
        if (Utils.isArray(type)) {
            AnnotatedType componentType = Utils.get(extraArgs, 0, AnnotatedType.class, null);
            if (componentType != null) {
                return Utils.cast(new AnnotatedArrayTypeImpl(type, annotations, componentType));
            }
            Annotation[] componentAnns = Utils.get(extraArgs, 0, Annotation[].class, null);
            return Utils.cast(new AnnotatedArrayTypeImpl(type, annotations, componentAnns));
        } else if (type instanceof Class) {
            AnnotatedType owner = Utils.get(extraArgs, 0, AnnotatedType.class, null);
            return Utils.cast(new AnnotatedTypeImpl(type, owner, annotations));
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = Utils.cast(type);
            AnnotatedType ownerType = null;
            AnnotatedType[] argTypes = Utils.get(extraArgs, 0, AnnotatedType[].class, null);
            if (!extraArgs.getClass().equals(AnnotatedType[].class)) {
                ownerType = Utils.get(extraArgs, 0, AnnotatedType.class, null);
                argTypes = Utils.get(extraArgs, 1, AnnotatedType[].class, argTypes);
            }
            if (argTypes != null) {
                return Utils.cast(new AnnotatedParameterizedTypeImpl(pt, ownerType, annotations, argTypes));
            }
            // owner type may be unspecified, or specified as null
            Annotation[][] argAnns = Utils.get(extraArgs, 0, Annotation[][].class, null);
            argAnns = Utils.get(extraArgs, 1, Annotation[][].class, argAnns);
            return Utils.cast(new AnnotatedParameterizedTypeImpl(pt, ownerType, annotations, argAnns));
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> typeVar = Utils.cast(type);
            Annotation[][] boundAnns = Utils.get(extraArgs, 0, Annotation[][].class, null);
            AnnotatedType[] boundTypes = Utils.get(extraArgs, 0, AnnotatedType[].class, null);
            if (boundTypes != null) {
                return Utils.cast(new AnnotatedTypeVariableImpl(typeVar, annotations, boundTypes));
            }
            return Utils.cast(new AnnotatedTypeVariableImpl(typeVar, annotations, boundAnns));
        } else if (type instanceof WildcardType) {
            WildcardType wc = Utils.cast(type);
            AnnotatedType[] lbTypes = Utils.get(extraArgs, 0, AnnotatedType[].class, null);
            AnnotatedType[] ubTypes = Utils.get(extraArgs, 1, AnnotatedType[].class, null);
            if (ubTypes != null || lbTypes != null) {
                return Utils.cast(new AnnotatedWildcardTypeImpl(wc, annotations, lbTypes, ubTypes));
            }
            Annotation[][] lbAnns = Utils.get(extraArgs, 0, Annotation[][].class, null);
            Annotation[][] ubAnns = Utils.get(extraArgs, 1, Annotation[][].class, null);
            return Utils.cast(new AnnotatedWildcardTypeImpl(wc, annotations, lbAnns, ubAnns));
        }
        throw new UnsupportedOperationException("Unsupported type " + type);
    }

    /**
     * Create an array of {@link AnnotatedType} from arrays of parameters
     * @param types {@link Type} array to annotate
     * @param annotations {@link Annotation} arrays indicating annotations for {@code types}
     * @return array of {@link AnnotatedType} from {@code types} and {@code annotations}
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code types} is {@code null}</li>
     *   <li>When {@code types} contains a {@code null}</li>
     *   <li>When any element of {@code annotations} contains a {@code null}</li>
     * </ul>
     */
    public static AnnotatedType[] newAnnotatedTypes(Type[] types, Annotation[][] annotations) {
        Utils.noNullContained(types, "types");
        AnnotatedType[] annTypes = new AnnotatedType[types.length];
        for (int idx = 0; idx < annTypes.length; ++idx) {
            Annotation[] typeAnns = Utils.get(annotations, idx);
            if (typeAnns != null) {
                Utils.noNullContained(typeAnns, "annotations[" + idx + "]");
            }
            annTypes[idx] = newAnnotatedType(types[idx], typeAnns);
        }
        return annTypes;
    }

    /**
     * Create a new instance from or return the provided {@link AnnotatedType} based on whether it
     * has a usable {@link Object#equals(Object)} implementation.
     * One such example is the standard JDK implementations, as they do not implement {@link Object#equals(Object)} properly
     * until Java 12.
     * Any type that does not have a proper equals implementation cannot be utilized as the left hand side of an equals operation.
     * @param type {@link AnnotatedType} to recreate as necessary for a usable {@link Object#equals(Object)} implementation.
     * @return {@code type} or a copy of it with a usable {@link Object#equals(Object)} implementation.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code type} is {@code null}</li>
     *   <li>When {@code type} does not represent a supported {@link AnnotatedType}</li>
     * </ul>
     */
    public static AnnotatedType recreateAnnotatedTypeForEquals(AnnotatedType type) {
        Utils.notNull(type, "type");
        /* not all implementations of AnnotatedType have a usable equals method, which causes problems
         * in .equals implementations of the types with the large dependency of one AnnotatedType depending on others.
         * So verify */
        Class<?> typeClass = type.getClass();
        Boolean usableEquals = USABLE_EQUALS.get(typeClass);
        if (usableEquals == null) {
            // calculate and fill the cache to avoid the lookup again later
            try {
                // to be considered to have a usable equals, the type itself must implement the equals method
                Class<?> declared = typeClass.getMethod("equals", Object.class).getDeclaringClass();
                usableEquals = Boolean.valueOf(typeClass.equals(declared));
            } catch (NoSuchMethodException | SecurityException ex) {
                usableEquals = Boolean.FALSE;
            }
            USABLE_EQUALS.put(typeClass, usableEquals);
        }
        return usableEquals ? type : newAnnotatedType(type);
    }

    /**
     * Create a new instances from or return the provided {@link AnnotatedType}s based on whether they
     * have usable {@link Object#equals(Object)} implementations.
     * As the standard JDK implementations do not implement {@link Object#equals(Object)} properly they cannot
     * be utilized as the left hand side of an equals operation.
     * @param types {@link AnnotatedType}s to recreate as necessary for a usable {@link Object#equals(Object)} implementation.
     * @return {@code type}s or a copy of it with a usable {@link Object#equals(Object)} implementation.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code types} is {@code null}</li>
     *   <li>When {@code types} contains a {@code null}</li>
     *   <li>When any {@code type} element does not represent a supported {@link AnnotatedType}</li>
     * </ul>
     */
    public static AnnotatedType[] recreateAnnotatedTypesForEquals(AnnotatedType... types) {
        Utils.noNullContained(types, "types");
        AnnotatedType[] ret = Utils.clone(types);
        for (int idx = 0; idx < ret.length; ++idx) {
            ret[idx] = recreateAnnotatedTypeForEquals(ret[idx]);
        }
        return ret;
    }

    /** Create a new instance, should not be used directly */
    protected AnnotatedTypeFactory() {
        // derivable, but not instantiable
    }
}
