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
 * $RCSfile: NativeServices.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:26 $
 * $State: Exp $
 *
 * Class:                   NativeServices
 *
 * Description:             Static methods allowing to access to some
 *                          native services. It uses native methods.
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
 * This class presents a collection of static methods that allow access to
 * some native methods. It makes use of native methods to access those thread
 * properties.
 *
 * <P>Since the methods in this class require the presence of a shared library
 * with the name defined in SHLIB_NAME it is necessary to load it prior to
 * making use of any such methods. All methods that require the shared library
 * will automatically load the library if that has not been already done. The
 * library might also be manually loaded with the 'loadLibrary()' method of
 * this class.
 *
 * <P>This class provides only static methods. It should not be instantiated.
 *
 * <P>Currently the only native services available is settings relative to
 * POSIX threads, which are not accessible from the Java API.
 *
 * <P>Currently the methods in this class make sense with POSIX threads only,
 * since they access POSIX threads settings. POSIX threads are most used under
 * UNIX and UNIX-like operating systems and are mostly referred to as "native"
 * threads in Java Virtual Machine (JVM) implementations.
 *
 * <P>The shared library SHLIB_NAME uses functions of the POSIX thread library
 * (i.e. 'pthread'). Calling the methods that use the 'pthread' library will
 * most prbably cause the Java Virtual Machine (JVM) to crash if it is not
 * using the POSIX threads, due to unsatisfied references. For instance, JVMs
 * that use "green" threads will most certainly crash. POSIX threads are
 * referred to as "native" threads in JVMs under UNIX operating systems.
 *
 * <P>On Operating Systems where POSIX threads are not available (typically
 * Windows 95/98/NT/2000, MacIntosh, OS/2) there is no problem since the
 * SHLIB_NAME, if available, will not make use of POSIX threads library
 * functions, thus no problem should occur.
 * */
public final class NativeServices {

    /** The name of the shared library containing the implementation of the
     * native methods: 'jj2000'. The actual file name of the library is system
     * dependent. Under UNIX it will be 'libjj2000.so', while under Windows it
     * will be 'jj2000.dll'.
     * */
    public static final String SHLIB_NAME = "jj2000";

    /** The state of the library loading */
    private static int libState;

    /**
     * Library load state ID indicating that no attept to load the library has
     * been done yet.  */
    private final static int LIB_STATE_NOT_LOADED = 0;

    /**
     * Library load state ID indicating that the library was successfully
     * loaded. */
    private final static int LIB_STATE_LOADED = 1;

    /**
     * Library load state ID indicating that an attempt to load the library
     * was done and that it could not be found. */
    private final static int LIB_STATE_NOT_FOUND = 2;

    /**
     * Private and only constructor, so that no class instance might be
     * created. Since all methods are static creating a class instance is
     * useless. If called it throws an 'IllegalArgumentException'.
     * */
    private NativeServices() {
        throw new IllegalArgumentException("Class can not be instantiated");
    }

    /**
     * Sets the concurrency level of the threading system of the Java Virtual
     * Machine (JVM) to the specified level. The concurrency level specifies
     * how many threads can run in parallel on different CPUs at any given
     * time for JVM implementations that use POSIX threads with
     * PTHREAD_SCOPE_PROCESS scheduling scope. A concurrency level of 0 means
     * that the operating system will automatically adjust the concurrency
     * level depending on the number of threads blocking on system calls, but
     * this will probably not exploit more than one CPU in multiporocessor
     * machines. If the concurrency level if set to more than the number of
     * available processors in the machine the performance might degrade.
     *
     * <P>For JVM implementations that use POSIX threads with
     * PTHREAD_SCOPE_SYSTEM scheduling scope or JVM implementations that use
     * Windows(R) threads and maybe others, setting the concurrency level has
     * no effect. In this cases the number of CPUs that can be exploited by
     * the JVM is not limited in principle, all CPUs are available to the JVM.
     *
     * <P>For JVM implementations that use "green" threads setting the
     * concurrency level, and thus calling this method, makes no sense, since
     * "green" threads are all contained in one user process and can not use
     * multiple CPUs. In fact calling this method can result in a JVM crash is
     * the shared library SHLIB_NAME has been compiled to use POSIX threads.
     *
     * @param n The new concurrency level to set.
     *
     * @exception IllegalArgumentException Concurrency level is negative
     *
     * @exception UnsatisfiedLinkError If the shared native library
     * implementing the functionality could not be loaded.
     * */
    public static void setThreadConcurrency(int n) {
        // Check that the library is loaded
        checkLibrary();
        // Check argument
        if (n < 0) throw new IllegalArgumentException();
        // Set concurrency with native method
        setThreadConcurrencyN(n);
    }

    /**
     * Calls the POSIX threads 'pthread_setconcurrency', or equivalent,
     * function with 'level' as the argument.
     * */
    private static native void setThreadConcurrencyN(int level);

    /**
     * Returns the current concurrency level. See 'setThreadConcurrency' for
     * details on the meaning of concurrency
     *
     * @return The current concurrency level
     *
     * @see #setThreadConcurrency
     * */
    public static int getThreadConcurrency() {
        // Check that the library is loaded
        checkLibrary();
        // Return concurrency from native method
        return getThreadConcurrencyN();
    }

    /**
     * Calls the POSIX threads 'pthread_getconcurrency', or equivalent,
     * function and return the result.
     *
     * @return The current concurrency level.
     * */
    private static native int getThreadConcurrencyN();

    /**
     * Loads the shared library implementing the native methods of this
     * class and returns true on success. Multiple calls to this method after
     * a successful call have no effect and return true. Multiple calls to
     * this method after unsuccesful calls will make new attempts to load the
     * library.
     *
     * @return True if the libary could be loaded or is already loaded. False
     * if the library can not be found and loaded.
     * */
    public static boolean loadLibrary() {
        // If already loaded return true without doing anything
        if (libState == LIB_STATE_LOADED) return true;
        // Try to load the library
        try {
            System.loadLibrary(SHLIB_NAME);
        } catch (UnsatisfiedLinkError e) {
            // Library was not found
            libState = LIB_STATE_NOT_FOUND;
            return false;
        }
        // Library was found
        libState = LIB_STATE_LOADED;
        return true;
    }

    /**
     * Checks if the library SHLIB_NAME is already loaded and attempts to load
     * if not yet loaded. If the library can not be found (either in a
     * previous attempt to load it or in an attempt in this method) an
     * 'UnsatisfiedLinkError' exception is thrown.
     *
     * @exception UnsatisfiedLinkError If the library SHLIB_NAME can not be
     * found.
     * */
    private static void checkLibrary() {
        switch (libState) {
        case LIB_STATE_LOADED: // Already loaded, nothing to do
            return;
        case LIB_STATE_NOT_LOADED: // Not yet loaded => load now
            // If load successful break, otherwise continue to the
            // LIB_STATE_NOT_LOADED state
            if (loadLibrary()) break;
        case LIB_STATE_NOT_FOUND: // Could not be found, throw exception
            throw new UnsatisfiedLinkError("NativeServices: native shared "+
                                           "library could not be loaded");
        }
    }

}
