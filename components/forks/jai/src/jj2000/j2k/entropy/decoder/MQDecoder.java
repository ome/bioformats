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
 * $RCSfile: MQDecoder.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:06 $
 * $State: Exp $
 *
 * Class:                   MQDecoder
 *
 * Description:             Class that encodes a number of bits using the
 *                          MQ arithmetic decoder
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
package jj2000.j2k.entropy.decoder;

import jj2000.j2k.entropy.decoder.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.io.*;
import jj2000.j2k.util.*;
import java.io.*;

/**
 * This class implements the MQ arithmetic decoder. It is implemented using
 * the software conventions decoder for better performance (i.e. execution
 * time performance). The initial states for each context of the MQ-coder are
 * specified in the constructor.
 *
 */

// A trick to test for increased speed: merge the Qe and mPS into 1 thing by
// using the sign bit of Qe to signal mPS (positive-or-0 is 0, negative is 1),
// and doubling the Qe, nMPS and nLPS tables. This gets rid of the swicthLM
// table since it can be integrated as special cases in the doubled nMPS and
// nLPS tables. See the JPEG book, chapter 13. The decoded decision can be
// calculated as (q>>>31).

public class MQDecoder {

    /** The data structures containing the probabilities for the LPS */
    final static
        int qe[]={0x5601, 0x3401, 0x1801, 0x0ac1, 0x0521, 0x0221, 0x5601,
                  0x5401, 0x4801, 0x3801, 0x3001, 0x2401, 0x1c01, 0x1601,
                  0x5601, 0x5401, 0x5101, 0x4801, 0x3801, 0x3401, 0x3001,
                  0x2801, 0x2401, 0x2201, 0x1c01, 0x1801, 0x1601, 0x1401,
                  0x1201, 0x1101, 0x0ac1, 0x09c1, 0x08a1, 0x0521, 0x0441,
                  0x02a1, 0x0221, 0x0141, 0x0111, 0x0085, 0x0049, 0x0025,
                  0x0015, 0x0009, 0x0005, 0x0001, 0x5601 };

    /** The indexes of the next MPS */
    final static
        int nMPS[]={ 1 , 2, 3, 4, 5,38, 7, 8, 9,10,11,12,13,29,15,16,17,
                     18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,
                     35,36,37,38,39,40,41,42,43,44,45,45,46 };

    /** The indexes of the next LPS */
    final static
        int nLPS[]={ 1 , 6, 9,12,29,33, 6,14,14,14,17,18,20,21,14,14,15,
                     16,17,18,19,19,20,21,22,23,24,25,26,27,28,29,30,31,
                     32,33,34,35,36,37,38,39,40,41,42,43,46 };

    /** Whether LPS and MPS should be switched */
    final static
        int switchLM[]={ 1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,
                         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };

    /** The ByteInputBuffer used to read the compressed bit stream. */
    ByteInputBuffer in;

    /** The current most probable signal for each context */
    int[] mPS;

    /** The current index of each context */
    int[] I;

    /** The current bit code */
    int c;

    /** The bit code counter */
    int cT;

    /** The current interval */
    int a;

    /** The last byte read */
    int b;

    /** Flag indicating if a marker has been found */
    boolean markerFound;

    /** The initial state of each context */
    final int initStates[];

    /**
     * Instantiates a new MQ-decoder, with the specified number of contexts and
     * initial states. The compressed bytestream is read from the 'iStream'
     * object.
     *
     * @param iStream the stream that contains the coded bits
     *
     * @param nrOfContexts The number of contexts used
     *
     * @param initStates The initial state for each context. A reference is
     * kept to this array to reinitialize the contexts whenever 'reset()' or
     * 'resetCtxts()' is called.
     *
     *
     *
     */
    public MQDecoder(ByteInputBuffer iStream, int nrOfContexts,
                     int initStates[]){
        in = iStream;

        // Default initialization of the statistics bins is MPS=0 and
        // I=0
        I=new int[nrOfContexts];
        mPS=new int[nrOfContexts];
        // Save the initial states
        this.initStates = initStates;

        // Initialize
        init();

        // Set the contexts
        resetCtxts();
    }

    /**
     * Decodes 'n' symbols from the bit stream using the same context
     * 'ctxt'. If possible the MQ-coder speedup mode will be used to speed up
     * decoding. The speedup mode is used if Q (the LPS probability for 'ctxt'
     * is low enough) and the A and C registers permit decoding several MPS
     * symbols without renormalization.
     *
     * <P>Speedup mode should be used when decoding long runs of MPS with high
     * probability with the same context.
     *
     * <P>This methiod will return the decoded symbols differently if speedup
     * mode was used or not. If true is returned, then speedup mode was used
     * and the 'n' decoded symbols are all the same and it is returned ain
     * bits[0] only. If false is returned then speedup mode was not used, the
     * decoded symbols are probably not all the same and they are returned in
     * bits[0], bits[1], ... bits[n-1].
     *
     * @param bits The array where to put the decoded symbols. Must be of
     * length 'n' or more.
     *
     * @param ctxt The context to use in decoding the symbols.
     *
     * @param n The number of symbols to decode.
     *
     * @return True if speedup mode was used, false if not. If speedup mode
     * was used then all the decoded symbols are the same and its value is
     * returned in 'bits[0]' only (not in bits[1], bits[2], etc.).
     *
     *
     * */
    public final boolean fastDecodeSymbols(int[] bits, int ctxt, int n) {
        int q;   // LPS probability for context
        int idx; // Index of current state
        int la;  // cache for A register
        int i;   // counter

        idx = I[ctxt];
        q = qe[idx];

        // This is a first attempt to implement speedup mode, it is probably
        // not the most efficient way of doing it.

        if ((q<0x4000) && (n <= (a-(c>>>16)-1)/q) &&
            (n <= (a-0x8000)/q+1)) {
            // Q is small enough. There will be no modification of C that
            // affects decoding, and Q can be substracted from A several
            // times. We will decode all MPS.
            a -= n*q;
            if (a >= 0x8000) { // No renormalization needed
                bits[0] = mPS[ctxt];
                return true; // Done, used speedup mode
            }
            else { // renormalization needed
                I[ctxt] = nMPS[idx];
                // Renormalize (MPS: no need for while loop)
                if (cT == 0)
                    byteIn();
                a <<= 1;
                c <<= 1;
                cT--;
                // End renormalization
                bits[0] = mPS[ctxt];
                return true; // Done, used speedup mode
            }
        }
        else { // Normal mode
            la = a; // cache A register
            for (i=0; i<n; i++) {
                la -= q;
                if ((c>>>16) < la) {
                    if(la >= 0x8000){
                        bits[i] = mPS[ctxt];
                    }
                    else {
                        // -- MPS Exchange
                        if(la >= q){
                            bits[i] = mPS[ctxt];
                            idx = nMPS[idx];
                            q = qe[idx];
                            // I[ctxt] set at end of loop
                            // -- Renormalize (MPS: no need for while loop)
                            if(cT==0)
                                byteIn();
                            la<<=1;
                            c<<=1;
                            cT--;
                            // -- End renormalization
                        }
                        else{
                            bits[i] = 1-mPS[ctxt];
                            if(switchLM[idx]==1)
                                mPS[ctxt] = 1-mPS[ctxt];
                            idx =  nLPS[idx];
                            q = qe[idx];
                            // I[ctxt] set at end of loop
                            // -- Renormalize
                            do{
                                if(cT==0)
                                    byteIn();
                                la<<=1;
                                c<<=1;
                                cT--;
                            }while(la < 0x8000);
                            // -- End renormalization
                        }
                        // -- End MPS Exchange
                    }
                }
                else {
                    c -= (la<<16);
                    // -- LPS Exchange
                    if(la < q){
                        la = q;
                        bits[i] = mPS[ctxt];
                        idx = nMPS[idx];
                        q = qe[idx];
                        // I[ctxt] set at end of loop
                        // -- Renormalize (MPS: no need for while loop)
                        if(cT==0)
                            byteIn();
                        la<<=1;
                        c<<=1;
                        cT--;
                        // -- End renormalization
                    }
                    else {
                        la = q;
                        bits[i] = 1-mPS[ctxt];
                        if(switchLM[idx] == 1)
                            mPS[ctxt] = 1-mPS[ctxt];
                        idx =  nLPS[idx];
                        q = qe[idx];
                        // I[ctxt] set at end of loop
                        // -- Renormalize
                        do{
                            if(cT==0)
                                byteIn();
                            la<<=1;
                            c<<=1;
                            cT--;
                        } while (la < 0x8000);
                        // -- End renormalization
                    }
                    // -- End LPS Exchange
                }
            }
            a = la;           // save cached A register
            I[ctxt] = idx;    // save current index for context
            return false;     // done, did not use speedup mode
        } // End normal mode
    }

    /**
     * This function performs the arithmetic decoding. The function receives
     * an array in which to put the decoded symbols and an array of contexts
     * with which to decode them.
     *
     * <P>Each context has a current MPS and an index describing what the
     * current probability is for the LPS. Each bit is decoded and if the
     * probability of the LPS exceeds .5, the MPS and LPS are switched.
     *
     * @param bits The array where to place the decoded symbols. It should be
     * long enough to contain 'n' elements.
     *
     * @param cX The context to use in decoding each symbol.
     *
     * @param n The number of symbols to decode
     *
     *
     */
    public final void decodeSymbols(int[] bits, int[] cX, int n){
        int q;
        int ctxt;
        int la; // cache for A register value
        int index;
        int i;

        // NOTE: (a < 0x8000) is equivalent to ((a & 0x8000)==0)
        // since 'a' is always less than or equal to 0xFFFF

        // NOTE: conditional exchange guarantees that A for MPS is
        // always greater than 0x4000 (i.e. 0.375)
        // => one renormalization shift is enough for MPS
        // => no need to do a renormalization while loop for MPS

        for (i=0; i<n; i++) {
            ctxt = cX[i];

            index = I[ctxt];
            q = qe[index];

            a -= q;
            if ((c>>>16) < a) {
                if(a >= 0x8000){
                    bits[i] = mPS[ctxt];
                }
                else {
                    la = a;
                    // -- MPS Exchange
                    if(la >= q){
                        bits[i] = mPS[ctxt];
                        I[ctxt] = nMPS[index];
                        // -- Renormalize (MPS: no need for while loop)
                        if(cT==0)
                            byteIn();
                        la<<=1;
                        c<<=1;
                        cT--;
                        // -- End renormalization
                    }
                    else{
                        bits[i] = 1-mPS[ctxt];
                        if(switchLM[index]==1)
                            mPS[ctxt] = 1-mPS[ctxt];
                        I[ctxt] = nLPS[index];
                        // -- Renormalize
                        do{
                            if(cT==0)
                                byteIn();
                            la<<=1;
                            c<<=1;
                            cT--;
                        }while(la < 0x8000);
                        // -- End renormalization
                    }
                    // -- End MPS Exchange
                    a = la;
                }
            }
            else {
                la = a;
                c -= (la<<16);
                // -- LPS Exchange
                if(la < q){
                    la = q;
                    bits[i] = mPS[ctxt];
                    I[ctxt] = nMPS[index];
                    // -- Renormalize (MPS: no need for while loop)
                    if(cT==0)
                        byteIn();
                    la<<=1;
                    c<<=1;
                    cT--;
                    // -- End renormalization
                }
                else {
                    la = q;
                    bits[i] = 1-mPS[ctxt];
                    if(switchLM[index] == 1)
                        mPS[ctxt] = 1-mPS[ctxt];
                    I[ctxt] = nLPS[index];
                    // -- Renormalize
                    do{
                        if(cT==0)
                            byteIn();
                        la<<=1;
                        c<<=1;
                        cT--;
                    } while (la < 0x8000);
                    // -- End renormalization
                }
                // -- End LPS Exchange

                a = la;
            }
        }
    }


    /**
     * Arithmetically decodes one symbol from the bit stream with the given
     * context and returns its decoded value.
     *
     * <P>Each context has a current MPS and an index describing what the
     * current probability is for the LPS. Each bit is encoded and if the
     * probability of the LPS exceeds .5, the MPS and LPS are switched.
     *
     * @param context The context to use in decoding the symbol
     *
     * @return The decoded symbol, 0 or 1.
     *
     *
     */
    public final int decodeSymbol(int context){
        int q;
        int la;
        int index;
        int decision;

        index = I[context];
        q = qe[index];

        // NOTE: (a < 0x8000) is equivalent to ((a & 0x8000)==0)
        // since 'a' is always less than or equal to 0xFFFF

        // NOTE: conditional exchange guarantees that A for MPS is
        // always greater than 0x4000 (i.e. 0.375)
        // => one renormalization shift is enough for MPS
        // => no need to do a renormalization while loop for MPS

        a -= q;
        if ((c>>>16) < a) {
            if(a >= 0x8000){
                decision = mPS[context];
            }
            else {
                la = a;
                // -- MPS Exchange
                if(la >= q){
                    decision = mPS[context];
                    I[context] = nMPS[index];
                    // -- Renormalize (MPS: no need for while loop)
                    if(cT==0)
                        byteIn();
                    la<<=1;
                    c<<=1;
                    cT--;
                    // -- End renormalization
                }
                else{
                    decision = 1-mPS[context];
                    if(switchLM[index]==1)
                        mPS[context] = 1-mPS[context];
                    I[context] = nLPS[index];
                    // -- Renormalize
                    do{
                        if(cT==0)
                            byteIn();
                        la<<=1;
                        c<<=1;
                        cT--;
                    }while(la < 0x8000);
                    // -- End renormalization
                }
                // -- End MPS Exchange
                a = la;
            }
        }
        else {
            la = a;
            c -= (la<<16);
            // -- LPS Exchange
            if(la < q){
                la = q;
                decision = mPS[context];
                I[context] = nMPS[index];
                // -- Renormalize (MPS: no need for while loop)
                if(cT==0)
                    byteIn();
                la<<=1;
                c<<=1;
                cT--;
                // -- End renormalization
            }
            else {
                la = q;
                decision = 1-mPS[context];
                if(switchLM[index] == 1)
                    mPS[context] = 1-mPS[context];
                I[context] = nLPS[index];
                // -- Renormalize
                do{
                    if(cT==0)
                        byteIn();
                    la<<=1;
                    c<<=1;
                    cT--;
                } while (la < 0x8000);
                // -- End renormalization
            }
            // -- End LPS Exchange

            a = la;
        }
        return decision;
    }

    /**
     * Checks for past errors in the decoding process using the predictable
     * error resilient termination. This works only if the encoder used the
     * predictable error resilient MQ termination, otherwise it reports wrong
     * results. If an error is detected it means that the MQ bit stream has
     * been wrongly decoded or that the MQ terminated segment length is too
     * long. If no errors are detected it does not necessarily mean that the
     * MQ bit stream has been correctly decoded.
     *
     * @return True if errors are found, false otherwise.
     * */
    public boolean checkPredTerm() {
        int k;  // Number of bits that where added in the termination process
        int q;

        // 1) if everything has been OK, 'b' must be 0xFF if a terminating
        // marker has not yet been found
        if (b != 0xFF && !markerFound) return true;

        // 2) if cT is not 0, we must have already reached the terminating
        // marker
        if (cT != 0 && !markerFound) return true;

        // 3) If cT is 1 there where no spare bits at the encoder, this is all
        // that we can check
        if (cT == 1) return false;

        // 4) if cT is 0, then next byte must be the second byte of a
        // terminating marker (i.e. larger than 0x8F) if the terminating
        // marker has not been reached yet
        if (cT == 0) {
            if (!markerFound) {
                // Get next byte and check
                b=in.read()&0xFF;
                if (b <= 0x8F) return true;
            }
            // Adjust cT for last byte
            cT = 8;
        }

        // 5) Now we can calculate the number 'k' of bits having error
        // resilience information, which is the number of bits left to
        // normalization in the C register, minus 1.
        k = cT-1;

        // 6) The predictable termination policy is as if an LPS interval was
        // coded that caused a renormalization of 'k' bits, before the
        // termination marker started

        // We first check if an LPS is decoded, that causes a renormalization
        // of 'k' bits. Worst case is smallest LPS probability 'q' that causes
        // a renormalization of 'k' bits.
        q = 0x8000>>k;

        // Check that we can decode an LPS interval of probability 'q'
        a -= q;
        if ((c>>>16) < a) {
            // Error: MPS interval decoded
            return true;
        }
        // OK: LPS interval decoded
        c -= (a<<16);
        // -- LPS Exchange
        // Here 'a' can not be smaller than 'q' because the minimum value
        // for 'a' is 0x8000-0x4000=0x4000 and 'q' is set to a value equal
        // to or smaller than that.
        a = q;
        // -- Renormalize
        do{
            if(cT==0) byteIn();
            a<<=1;
            c<<=1;
            cT--;
        } while (a < 0x8000);
        // -- End renormalization
        // -- End LPS Exchange

        // 7) Everything seems OK, we have checked the C register for the LPS
        // symbols and ensured that it is followed by bits synthetized by the
        // termination marker.
        return false;
    }

    /**
     * This function gets one byte of compressed bits from the in-stream.
     * the byte is added to c. If the byte is 0xFF and the next byte is greater
     * than 0x8F, the byte after 0xFF is a marker.
     *
     *
     *
     */
    private void byteIn(){
        if(!markerFound){
            if(b==0xFF){
                b=in.read()&0xFF; // Convert EOFs (-1) to 0xFF

                if(b>0x8F){
                    markerFound=true;
                    // software-convention decoder: c unchanged
                    cT=8;
                }else{
                    c += 0xFE00 - (b<<9);
                    cT=7;
                }
            }else{
                b=in.read()&0xFF; // Convert EOFs (-1) to 0xFF
                c += 0xFF00 - (b<<8);
                cT=8;
            }
        }
        else{
            // software-convention decoder: c unchanged
            cT=8;
        }
    }

    /**
      * Returns the number of contexts in the arithmetic coder.
      *
      * @return The number of contexts
      *
      *
      **/
    public final int getNumCtxts(){
        return I.length;
    }

    /**
     * Resets a context to the original probability distribution.
     *
     * @param c The number of the context (it starts at 0).
     *
     *
     *
     */
    public final void resetCtxt(int c){
        I[c] = initStates[c];
        mPS[c] = 0;
    }

    /**
     * Resets a context to the original probability distribution. The
     * original probability distribution depends on the actual
     * implementation of the arithmetic coder or decoder.
     *
     * @param c The index of the context (it starts at 0).
     *
     *
     *
     */
    public final void resetCtxts(){
        System.arraycopy(initStates,0,I,0,I.length);
        ArrayUtil.intArraySet(mPS,0);
    }

    /**
     * Resets the MQ decoder to start a new segment. This is like recreating a
     * new MQDecoder object with new input data.
     *
     * @param buf The byte array containing the MQ encoded data. If null the
     * current byte array is assumed.
     *
     * @param off The index of the first element in 'buf' to be decoded. If
     * negative the byte just after the previous segment is assumed, only
     * valid if 'buf' is null.
     *
     * @param len The number of bytes in 'buf' to be decoded. Any subsequent
     * bytes are taken to be 0xFF.
     *
     *
     * */
    public final void nextSegment(byte buf[], int off, int len) {
        // Set the new input
        in.setByteArray(buf,off,len);
        // Reinitialize MQ
        init();
    }

    /**
     * Returns the underlying 'ByteInputBuffer' from where the MQ
     * coded input bytes are read.
     *
     * @return The underlying ByteInputBuffer.
     *
     * */
    public ByteInputBuffer getByteInputBuffer() {
        return in;
    }

    /**
     * Initializes the state of the MQ coder, without modifying the current
     * context states. It sets the registers (A,C,B) and the "marker found"
     * state to the initial state, to start the decoding of a new segment.
     *
     * <P>To have a complete reset of the MQ (as if a new MQDecoder object was
     * created) 'resetCtxts()' should be called after this method.
     *
     *
     * */
    private void init() {
        // --- INITDEC
        markerFound = false;

        // Read first byte
        b=in.read()&0xFF;

        // Software conventions decoder
        c=(b^0xFF)<<16;
        byteIn();
        c=c<<7;
        cT=cT-7;
        a=0x8000;

        // End of INITDEC ---
    }
}
