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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Implementation of {@link WildcardType}
 */
public class WildcardTypeImpl implements WildcardType {

    private static final Type[] EXTENDS_OBJECT = new Type[] { Object.class };

    /** Constant that represents the full wildcard that is simply {@code ?} */
    public static final WildcardTypeImpl FULL_WILDCARD = new WildcardTypeImpl(null, null);

    /**
     * Create a new {@link WildcardTypeImpl} that represents a {@code ? extends ...} binding.
     * @param types {@link Type}s that are bound as {@code extends} ("upper bounds")
     * @return new {@link WildcardTypeImpl} representing the specified extends (upper) bounds.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code types} is {@code null}</li>
     *   <li>When {@code types} is empty</li>
     *   <li>When {@code types} contains a {@code null}</li>
     * </ul>
     */
    public static WildcardTypeImpl forExtends(Type... types) {
        Utils.notEmpty(types, "types");
        return isFullWCExtends(types) ? FULL_WILDCARD : new WildcardTypeImpl(null, types);
    }

    /**
     * Create a new {@link WildcardTypeImpl} that represents a {@code ? super ...} binding.
     * @param types {@link Type}s that are bound as {@code super} ("lower bounds")
     * @return new {@link WildcardTypeImpl} representing the specified super (lower) bounds.
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code types} is {@code null}</li>
     *   <li>When {@code types} is empty</li>
     *   <li>When {@code types} contains a {@code null}</li>
     * </ul>
     */
    public static WildcardTypeImpl forSuper(Type... types) {
        Utils.notEmpty(types, "types");
        return new WildcardTypeImpl(types, null);
    }

    private static boolean isFullWCExtends(Type[] types) {
        return types.length == 1 && types[0].equals(Object.class);
    }

    /** {@link Type}s representing the lower bounds ({@code super}) */
    protected final Type[] lowerBounds;

    /** {@link Type}s representing the upper bounds ({@code extends}) */
    protected final Type[] upperBounds;

    /**
     * Create a new {@link WildcardTypeImpl} copying details from an existing one
     * @param wcType {@link WildcardType} to copy details from
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code wcType} is {@code null}</li>
     *   <li>When {@code wcType} is invalid</li>
     * </ul>
     * @see #WildcardTypeImpl(Type[], Type[])
     */
    public WildcardTypeImpl(WildcardType wcType) {
        this(Utils.notNull(wcType, "wcType").getLowerBounds(), wcType.getUpperBounds());
    }

    /**
     * Create a new {@link WildcardTypeImpl}
     * @param lowerBounds {@link Type}s representing the lower bounds ({@code super})
     * @param upperBounds {@link Type}s representing the upper bounds ({@code extends})
     * @throws IllegalArgumentException <ul>
     *   <li>When {@code lowerBounds} contains a {@code null}</li>
     *   <li>When {@code upperBounds} contains a {@code null}</li>
     *   <li>When {@code lowerBounds} is specified and {@code upperBounds} represents
     *      something other than solely extending from {@code Object}</li>
     * </ul>
     */
    public WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
        // clone non-null values, but otherwise use the base empty
        lowerBounds = Utils.checkedClone(lowerBounds, "lowerBounds", Utils.EMPTY);
        upperBounds = Utils.checkedClone(upperBounds, "upperBounds", Utils.EMPTY);
        // there should always be at least one upper bound specified, so default to Object if nothing is specified
        if (upperBounds.length == 0) {
            upperBounds = EXTENDS_OBJECT;
        }

        if (lowerBounds.length > 0 && !isFullWCExtends(upperBounds)) {
            // as extension of object is a basic requirement, ignore this in the check
            throw new IllegalArgumentException("cannot have both super and extends in one binding");
        }
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof WildcardType)) {
            return false;
        }

        WildcardType o = (WildcardType) other;
        return Arrays.equals(lowerBounds, o.getLowerBounds()) &&
            Arrays.equals(upperBounds, o.getUpperBounds());
    }

    @Override
    public Type[] getUpperBounds() {
        // clone to avoid modification by caller
        return Utils.clone(upperBounds);
    }

    @Override
    public Type[] getLowerBounds() {
        // clone to avoid modification by caller
        return Utils.clone(lowerBounds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
    }

    @Override
    public String toString() {
        Type[] bounds = lowerBounds;
        String prefix = null;
        if (bounds.length > 0)
            prefix = "? super ";
        else if (isFullWCExtends(upperBounds)) {
            // if the full wildcard, then just ?
            return "?";
        } else {
            bounds = upperBounds;
            prefix = "? extends ";
        }

        StringJoiner sj = new StringJoiner(" & ", prefix, "");
        for (Type bound : bounds) {
            sj.add(bound.getTypeName());
        }
        return sj.toString();
    }
}
