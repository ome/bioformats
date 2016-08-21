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
 * $RCSfile: MathUtil.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:25 $
 * $State: Exp $
 *
 * Class:                   MathUtil
 *
 * Description:             Utility mathematical methods
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
 * */


package jj2000.j2k.util;

/**
 * This class contains a collection of utility methods fro mathematical
 * operations. All methods are static.
 * */
public class MathUtil {

    /**
     * Method that calculates the floor of the log, base 2,
     * of 'x'. The calculation is performed in integer arithmetic,
     * therefore, it is exact.
     *
     * @param x The value to calculate log2 on.
     *
     * @return floor(log(x)/log(2)), calculated in an exact way.
     * */
    public static int log2(int x) {
        int y,v;
        // No log of 0 or negative
        if (x <= 0) {
            throw new IllegalArgumentException(""+x+" <= 0");
        }
        // Calculate log2 (it's actually floor log2)
        v = x;
        y = -1;
        while (v>0) {
            v >>=1;
            y++;
        }
        return y;
    }

    /** 
     * Method that calculates the Least Common Multiple (LCM) of two strictly
     * positive integer numbers.
     *
     * @param x1 First number
     *
     * @param x2 Second number
     * */
    public static final int lcm(int x1,int x2) {
        if(x1<=0 || x2<=0) {
            throw new IllegalArgumentException("Cannot compute the least "+
                                               "common multiple of two "+
                                               "numbers if one, at least,"+
                                               "is negative.");
        }
        int max,min;
        if (x1>x2) {
            max = x1;
            min = x2;
        } else {
            max = x2;
            min = x1;
        }
        for(int i=1; i<=min; i++) {
            if( (max*i)%min == 0 ) {
                return i*max;
            }
        }
        throw new Error("Cannot find the least common multiple of numbers "+
                        x1+" and "+x2);
    }

    /** 
     * Method that calculates the Least Common Multiple (LCM) of several
     * positive integer numbers.
     *
     * @param x Array containing the numbers.
     * */
    public static final int lcm(int[] x) {
        if(x.length<2) {
            throw new Error("Do not use this method if there are less than"+
                            " two numbers.");
        }
        int tmp = lcm(x[x.length-1],x[x.length-2]);
        for(int i=x.length-3; i>=0; i--) {
            if(x[i]<=0) {
                throw new IllegalArgumentException("Cannot compute the least "+
                                                   "common multiple of "+
                                                   "several numbers where "+
                                                   "one, at least,"+
                                                   "is negative.");
            }
            tmp = lcm(tmp,x[i]);
        }
        return tmp;
    }

    /** 
     * Method that calculates the Greatest Common Divisor (GCD) of two
     * positive integer numbers.
     * */
    public static final int gcd(int x1,int x2) {
        if(x1<0 || x2<0) {
            throw new IllegalArgumentException("Cannot compute the GCD "+
                                               "if one integer is negative.");
        }
        int a,b,g,z;

        if(x1>x2) {
            a = x1;
            b = x2;
        } else {
            a = x2;
            b = x1;
        }

        if(b==0) return 0;

        g = b;
        while (g!=0) {
            z= a%g;
            a = g;
            g = z;
        }
        return a;
    }

    /** 
     * Method that calculates the Greatest Common Divisor (GCD) of several
     * positive integer numbers.
     *
     * @param x Array containing the numbers.
     * */
    public static final int gcd(int[] x) {
        if(x.length<2) {
            throw new Error("Do not use this method if there are less than"+
                            " two numbers.");
        }
        int tmp = gcd(x[x.length-1],x[x.length-2]);
        for(int i=x.length-3; i>=0; i--) {
            if(x[i]<0) {
                throw new IllegalArgumentException("Cannot compute the least "+
                                                   "common multiple of "+
                                                   "several numbers where "+
                                                   "one, at least,"+
                                                   "is negative.");
            }
            tmp = gcd(tmp,x[i]);
        }
        return tmp;
    }
}
