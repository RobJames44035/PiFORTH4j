/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth.util;

public class DoubleUtil {

    public static int[] longToInts(final long value) {
        final int low = (int) (value & 0xFFFFFFFFL); // get the lower 32 bits
        final int high = (int) (value >> 32); // get the upper 32 bits
        return new int[]{low, high};
    }

    public static long intsToLong(final int low, final int high) {
        return ((long) high << 32) | (low & 0xFFFFFFFFL);
    }

    public static int[] addDouble(final int[] a, final int[] b) {
        final long aValue = intsToLong(a[0], a[1]);
        final long bValue = intsToLong(b[0], b[1]);
        final long result = aValue + bValue;
        return longToInts(result);
    }

    public static int[] subtractDouble(final int[] a, final int[] b) {
        final long aValue = intsToLong(a[0], a[1]);
        final long bValue = intsToLong(b[0], b[1]);
        final long result = aValue - bValue;
        return longToInts(result);
    }

    public static int[] multiplyDouble(final int[] a, final int[] b) {
        final long aValue = intsToLong(a[0], a[1]);
        final long bValue = intsToLong(b[0], b[1]);
        final long result = aValue * bValue;
        return longToInts(result);
    }

    public static int[] divideDouble(final int[] a, final int[] b) {
        final long aValue = intsToLong(a[0], a[1]);
        final long bValue = intsToLong(b[0], b[1]);
        final long result = aValue / bValue;
        return longToInts(result);
    }

    public static int[] modDouble(final int[] a, final int[] b) {
        final long aValue = intsToLong(a[0], a[1]);
        final long bValue = intsToLong(b[0], b[1]);
        final long result = aValue % bValue;
        return longToInts(result);
    }

    public static boolean lessThanDouble(int[] a, int[] b) {
        long aLong = intsToLong(a[0], a[1]);
        long bLong = intsToLong(b[0], b[1]);
        return aLong < bLong;
    }

    public static int[] negateDouble(final int[] a) {
        final long aValue = intsToLong(a[0], a[1]);
        final long result = -aValue;
        return longToInts(result);
    }
}
