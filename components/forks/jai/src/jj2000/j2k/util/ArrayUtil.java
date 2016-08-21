/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/*
 * $RCSfile: ArrayUtil.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:24 $
 * $State: Exp $
 *
 * Class:                   ArrayUtil
 *
 * Description:             Utillities for arrays.
 *
 *
 *
 * COPYRIGHT:
 *
 * This software module was originally developed by Raphaël Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelöf (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Félix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 *
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *
 *
 *
 */


package jj2000.j2k.util;

/**
 * This class contains a colleaction of utility static methods for arrays.
 * */
public class ArrayUtil {

    /** The maximum array size to do element by element copying, larger
     * arrays are copyied in a n optimized way. */
    public static final int MAX_EL_COPYING = 8;

    /** The number of elements to copy initially in an optimized array copy */
    public static final int INIT_EL_COPYING = 4;

    /**
     * Reinitializes an int array to the given value in an optimized way. If
     * the length of the array is less than MAX_EL_COPYING, then the array
     * is set element by element in the normal way, otherwise the first
     * INIT_EL_COPYING elements are set element by element and then
     * System.arraycopy is used to set the other parts of the array.
     *
     * @param arr The array to set.
     *
     * @param val The value to set the array to.
     *
     *
     * */
    public static void intArraySet(int arr[], int val) {
        int i,len,len2;

        len = arr.length;
        // Set array to 'val' in an optimized way
        if (len < MAX_EL_COPYING) {
            // Not worth doing optimized way
            for (i=len-1; i>=0; i--) { // Set elements
                arr[i] = val;
            }
        }
        else { // Do in optimized way
            len2 = len>>1;
            for (i=0; i<INIT_EL_COPYING; i++) { // Set first elements
                arr[i] = val;
            }
            for (; i <= len2 ; i<<=1) {
                // Copy values doubling size each time
                System.arraycopy(arr,0,arr,i,i);
            }
            if (i < len) { // Copy values to end
                System.arraycopy(arr,0,arr,i,len-i);
            }
        }
    }

    /**
     * Reinitializes a byte array to the given value in an optimized way. If
     * the length of the array is less than MAX_EL_COPYING, then the array
     * is set element by element in the normal way, otherwise the first
     * INIT_EL_COPYING elements are set element by element and then
     * System.arraycopy is used to set the other parts of the array.
     *
     * @param arr The array to set.
     *
     * @param val The value to set the array to.
     *
     *
     * */
    public static void byteArraySet(byte arr[], byte val) {
        int i,len,len2;

        len = arr.length;
        // Set array to 'val' in an optimized way
        if (len < MAX_EL_COPYING) {
            // Not worth doing optimized way
            for (i=len-1; i>=0; i--) { // Set elements
                arr[i] = val;
            }
        }
        else { // Do in optimized way
            len2 = len>>1;
            for (i=0; i<INIT_EL_COPYING; i++) { // Set first elements
                arr[i] = val;
            }
            for (; i <= len2 ; i<<=1) {
                // Copy values doubling size each time
                System.arraycopy(arr,0,arr,i,i);
            }
            if (i < len) { // Copy values to end
                System.arraycopy(arr,0,arr,i,len-i);
            }
        }
    }

}
