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
 * $RCSfile: FacilityManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:25 $
 * $State: Exp $
 *
 * Class:                   MsgLoggerManager
 *
 * Description:             Manages common facilities across threads
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

import java.util.*;
import java.io.*;

/**
 * This class manages common facilities for mutithreading environments, It can
 * register different facilities for each thread, and also a default one, so
 * that they can be referred by static methods, while possibly having
 * different ones for different threads. Also a default facility exists that
 * is used for threads for which no particular facility has been registerd
 * registered.
 *
 * <P>Currently the only kind of facilities managed is MsgLogger.
 *
 * <P>An example use of this class is if 2 instances of a decoder are running
 * in different threads and the messages of the 2 instances should be
 * separated.
 *
 * <P>The default MsgLogger is a StreamMsgLogger that uses System.out as
 * the 'out' stream and System.err as the 'err' stream, and a line width of
 * 78. This can be changed using the registerMsgLogger() method.
 *
 * @see MsgLogger
 *
 * @see StreamMsgLogger
 * */
public class FacilityManager {

    /** The loggers associated to different threads */
    private final static Hashtable loggerList = new Hashtable();

    /** The default logger, for threads that have none associated with them */
    private static MsgLogger defMsgLogger =
        new StreamMsgLogger(System.out,System.err,78);

    /** The ProgressWatch instance associated to different threads */
    private final static Hashtable watchProgList = new Hashtable();

    /** The default ProgressWatch for threads that have none
     * associated with them. */
    private static ProgressWatch defWatchProg = null;

    /** */
    public static void registerProgressWatch(Thread t,ProgressWatch pw) {
       if(pw==null) {
            throw new NullPointerException();
        }
        if(t==null) {
            defWatchProg = pw;
        }
        else {
            watchProgList.put(t,pw);
        }
    }

    /** 
     * Returns the ProgressWatch instance registered with the current
     * thread (the thread that calls this method). If the current
     * thread has no registered ProgressWatch, then the default one is used. 
     * */
    public static ProgressWatch getProgressWatch() {
        ProgressWatch pw = (ProgressWatch)
            watchProgList.get(Thread.currentThread());
        return (pw==null) ? defWatchProg : pw;
    }

    /**
     * Registers the MsgLogger 'ml' as the logging facility of the
     * thread 't'. If any other logging facility was registered with the
     * thread 't' it is overriden by 'ml'. If 't' is null then 'ml' is taken
     * as the default message logger that is used for threads that have no
     * MsgLogger registered.
     *
     * @param t The thread to associate with 'ml'
     *
     * @param ml The MsgLogger to associate with therad ml
     * */
    public static void registerMsgLogger(Thread t, MsgLogger ml) {
        if (ml == null) {
            throw new NullPointerException();
        }
        if (t == null) {
            defMsgLogger = ml;
        }
        else {
            loggerList.put(t,ml);
        }
    }

    /**
     * Returns the MsgLogger registered with the current thread (the
     * thread that calls this method). If the current thread has no registered
     * MsgLogger then the default message logger is returned.
     *
     * @return The MsgLogger registerd for the current thread, or the
     * default one if there is none registered for it.
     *
     *
     * */
    public static MsgLogger getMsgLogger() {
        MsgLogger ml =
            (MsgLogger) loggerList.get(Thread.currentThread());
        return (ml == null) ? defMsgLogger : ml;
    }

    /**
     * Returns the MsgLogger registered with the thread 't' (the thread
     * that calls this method). If the thread 't' has no registered
     * MsgLogger then the default message logger is returned.
     *
     * @param t The thread for which to return the MsgLogger
     *
     * @return The MsgLogger registerd for the current thread, or the
     * default one if there is none registered for it.
     *
     *
     * */
    public static MsgLogger getMsgLogger(Thread t) {
        MsgLogger ml =
            (MsgLogger) loggerList.get(t);
        return (ml == null) ? defMsgLogger : ml;
    }
}
