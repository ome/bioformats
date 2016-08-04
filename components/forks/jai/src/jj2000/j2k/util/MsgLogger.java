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
 * $RCSfile: MsgLogger.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:26 $
 * $State: Exp $
 *
 * Class:                   MsgLogger
 *
 * Description:             Facility to log messages (abstract)
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

/**
 * This class provides a simple common abstraction of a facility that logs
 * and/or displays messages or simple strings. The underlying facility can be
 * a terminal, text file, text area in a GUI display, dialog boxes in a GUI
 * display, etc., or a combination of those.
 *
 * <P>Messages are short strings (a couple of lines) that indicate some state
 * of the program, and that have a severity code associated with them (see
 * below). Simple strings is text (can be long) that has no severity code
 * associated with it. Typical use of simple strings is to display help texts.
 *
 * <P>Each message has a severity code, which can be one of the following:
 * LOG, INFO, WARNING, ERROR. Each implementation should treat each severity
 * code in a way which corresponds to the type of diplay used.
 *
 * <P>Messages are printed via the 'printmsg()' method. Simple strings are
 * printed via the 'print()', 'println()' and 'flush()' methods, each simple
 * string is considered to be terminated once the 'flush()' method has been
 * called. The 'printmsg()' method should never be called before a previous
 * simple string has been terminated.
 *
 * */
public interface MsgLogger {

    /** Severity of message. LOG messages are just for bookkeeping and do not
     * need to be displayed in the majority of cases */
    public static final int LOG = 0;

    /** Severity of message. INFO messages should be displayed just for user
     *  feedback. */
    public static final int INFO = 1;

    /** Severity of message. WARNING messages denote that an unexpected state
     * has been reached and should be given as feedback to the user. */
     public static final int WARNING = 2;

    /** Severity of message. ERROR messages denote that something has gone
     * wrong and probably that execution has ended. They should be definetely
     * displayed to the user. */
    public static final int ERROR = 3;

    /**
     * Prints the message 'msg' to the output device, appending a newline,
     * with severity 'sev'. Some implementations where the appended newline is
     * irrelevant may not append the newline. Depending on the implementation
     * the severity of the message may be added to it. The message is
     * reformatted as appropriate for the output devic, but any newline
     * characters are respected.
     *
     * @param sev The message severity (LOG, INFO, etc.)
     *
     * @param msg The message to display
     *
     *
     * */
    public void printmsg(int sev, String msg);

    /**
     * Prints the string 'str' to the output device, appending a line
     * return. The message is reformatted as appropriate to the particular
     * diplaying device, where 'flind' and 'ind' are used as hints for
     * performing that operation. However, any newlines appearing in 'str' are
     * respected. The output device may not display the string until flush()
     * is called. Some implementations may automatically flush when this
     * method is called. This method just prints the string, the string does
     * not make part of a "message" in the sense that no severity is
     * associated to it.
     *
     * @param str The string to print
     *
     * @param flind Indentation of the first line
     *
     * @param ind Indentation of any other lines.
     *
     *
     * */
    public void println(String str, int flind, int ind);

    /**
     * Writes any buffered data from the println() method to the device.
     *
     *
     * */
    public void flush();
}
