/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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
 * $RCSfile: MsgPrinter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:26 $
 * $State: Exp $
 *
 * Class:                   MsgPrinter
 *
 * Description:             Prints messages formatted for a specific
 *                          line width.
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
 */


package jj2000.j2k.util;

import java.io.*;

/**
 * This utility class formats messages to the specified line width, by
 * inserting line-breaks between words, and printing the resulting
 * lines.
 * */
public class MsgPrinter {

    /** The line width to use */
    public int lw;

    /** Signals that a newline was found */
    private static final int IS_NEWLINE = -2;

    /** Signals that the end-of-string was reached */
    private static final int IS_EOS = -1;

    /**
     * Creates a new message printer with the specified line width and
     * with the default locale.
     *
     * @param linewidth The line width for which to format (in
     * characters)
     *
     *
     * */
    public MsgPrinter(int linewidth) {
        lw = linewidth;
    }

    /**
     * Returns the line width that is used for formatting.
     *
     * @return The line width used for formatting
     *
     *
     * */
    public int getLineWidth() {
        return lw;
    }

    /**
     * Sets the line width to the specified value. This new value will
     * be used in subsequent calls to the print() message.
     *
     * @param linewidth The new line width to use (in cahracters)
     *
     *
     * */
    public void setLineWidth(int linewidth) {
        if (linewidth <1) {
            throw new IllegalArgumentException();
        }
        lw = linewidth;
    }

    /**
     * Formats the message to print in the current line width, by
     * breaking the message into lines between words. The number of
     * spaces to indent the first line is specified by 'flind' and the
     * number of spaces to indent each of the following lines is
     * specified by 'ind'. Newlines in 'msg' are respected. A newline is
     * always printed at the end.
     *
     * @param out Where to print the message.
     *
     * @param flind The indentation for the first line.
     *
     * @param ind The indentation for the other lines.
     *
     * @param msg The message to format and print.
     *
     *
     * */
    public void print(PrintWriter out, int flind, int ind,
                      String msg) {
        int start,end,pend,efflw,lind,i;

        start = 0;
        end = 0;
        pend = 0;
        efflw = lw-flind;
        lind = flind;
        while ((end = nextLineEnd(msg,pend)) != IS_EOS) {
            if (end == IS_NEWLINE) { // Forced line break
                for (i=0; i<lind; i++) {
                    out.print(" ");
                }
                out.println(msg.substring(start,pend));
                if (nextWord(msg,pend) == msg.length()) {
                    // Traling newline => print it and done
                    out.println("");
                    start = pend;
                    break;
                }
            }
            else {
                if (efflw > end-pend) { // Room left on current line
                    efflw -= end-pend;
                    pend = end;
                    continue;
                }
                else { // Filled-up current line => print it
                    for (i=0; i<lind; i++) {
                        out.print(" ");
                    }
                    if (start == pend) { // Word larger than line width
                        // Print anyways
                        out.println(msg.substring(start,end));
                        pend = end;
                    }
                    else {
                        out.println(msg.substring(start,pend));
                    }
                }
            }
            // Initialize for next line
            lind = ind;
            efflw = lw-ind;
            start = nextWord(msg,pend);
            pend = start;
            if (start == IS_EOS) {
                break; // Did all the string
            }
        }
        if (pend != start) { // Part of a line left => print it
            for (i=0; i<lind; i++) {
                out.print(" ");
            }
            out.println(msg.substring(start,pend));
        }

    }

    /**
     * Returns the index of the last character of the next word, plus 1, or
     * IS_NEWLINE if a newline character is encountered before the next word,
     * or IS_EOS if the end of the string is ecnounterd before the next
     * word. The method first skips all whitespace characters at or after
     * 'from', except newlines. If a newline is found IS_NEWLINE is
     * returned. Then it skips all non-whitespace characters and returns the
     * position of the last non-whitespace character, plus 1. The returned
     * index may be greater than the last valid index in the tsring, but it is
     * always suitable to be used in the String.substring() method.
     *
     * <P>Non-whitespace characters are defined as in the
     * Character.isWhitespace method (that method is used).
     *
     * @param str The string to parse
     *
     * @param from The index of the first position to search from
     *
     * @return The index of the last character in the next word, plus 1,
     * IS_NEWLINE, or IS_EOS if there are no more words.
     *
     *
     * */
    private int nextLineEnd(String str, int from) {
        final int len = str.length();
        char c = '\0';
        // First skip all whitespace, except new line
        while (from < len && (c = str.charAt(from)) != '\n' &&
               Character.isWhitespace(c)) {
            from++;
        }
        if (c == '\n') {
            return IS_NEWLINE;
        }
        if (from >= len) {
            return IS_EOS;
        }
        // Now skip word characters
        while (from < len && !Character.isWhitespace(str.charAt(from))) {
            from++;
        }
        return from;
    }

    /**
     * Returns the position of the first character in the next word, starting
     * from 'from', if a newline is encountered first then the index of the
     * newline character plus 1 is returned. If the end of the string is
     * encountered then IS_EOS is returned. Words are defined as any
     * concatenation of 1 or more characters which are not
     * whitespace. Whitespace characters are those for which
     * Character.isWhitespace() returns true (that method is used).
     *
     * <P>Non-whitespace characters are defined as in the
     * Character.isWhitespace method (that method is used).
     *
     * @param str The string to parse
     *
     * @param from The index where to start parsing
     *
     * @return The index of the first character of the next word, or the index
     * of the newline plus 1, or IS_EOS.
     *
     *
     * */
    private int nextWord(String str, int from) {
        final int len = str.length();
        char c = '\0';
        // First skip all whitespace, but new lines
        while (from < len && (c = str.charAt(from)) != '\n' &&
               Character.isWhitespace(c)) {
            from++;
        }
        if (from >= len) {
            return IS_EOS;
        }
        else if (c == '\n') {
            return from+1;
        }
        else {
            return from;
        }
    }
}
