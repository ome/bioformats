/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.tools;

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
  * Debug class. This is the preferred class to use when you want to output or log debug info
  *
  * @author Karl Avedal
  * @version 1.0.$Revision: 1.1.1.1 $
  * @since Jimi1.1
  **/

public class Debug
{

    /**
      * A static final variable for doing selective compilation of debug info
      **/

    public static final boolean DEBUG = false;

    /**
      * The stream all logging will go to
      **/

    private static DataOutputStream logOutput_ = new DataOutputStream(System.err);


    /**
      * Prints a message without linebreak if debugging is turned on
      *
      * @param message the message to print
      **/

    public static void print(String message)
    {
        if (DEBUG) System.err.print(message);
    }

    /**
      * Prints a message with linebreak if debugging is turned on
      *
      * @param message the message to print
      **/

    public static void println(String message)
    {
        if (DEBUG) System.err.println(message);
    }


    /**
      * A basic assert method that can be used to check a condition and create debug info
      * if the condition is false
      *
      * @param flag the condition to check
      * @param message the message to print if the error
      **/

    public static void assert(boolean flag, String message)
    {
        if (!flag)
        {
            System.err.println(message);
        }
    }

    /**
      * A basic assert method that can be used to check a condition and create debug info
      * if the condition is true
      *
      * @param flag the condition to check
      * @param message the message to print if the error
      **/

    public static void assertNot(boolean flag, String message)
    {
        assert(!flag, message);
    }

    /**
      * Set the filename of the file to be used for logging with log()
      *
      * @param filename the filename of the file to log to using log()
      * @throws IOException if the file cannot be opened for writing
      **/

    public static void setLogLocation(String filename) throws IOException
    {
        setLogLocation(new FileOutputStream(filename));
    }

    /**
      * Set the outputstream to be used for logging with log()
      *
      * @param output the <code>java.io.OutputStream</code> to log to using log()
      * @throws IOException if the file cannot be opened for writing
      **/

    public static void setLogLocation(OutputStream output) throws IOException
    {
        logOutput_ = new DataOutputStream(output);
    }

    /**
      * Log a message to the current log file selected with setLogLocation
      *
      * @param filename the filename of the file to log to using log()
      * @throws IOException if the file cannot be opened for writing
      **/

    public static void log(String message)
    {
        try
        {
            logOutput_.writeBytes(message);
            logOutput_.flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't log: "+message);
        }
    }

    /**
      * Stop logging (close all open stream used for logging)
      *
      **/

    public static void stopLogging()
    {
        try
        {
            logOutput_.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't close log stream");
        }
    }
}

/* 
$Log: Debug.java,v $
Revision 1.1.1.1  1998/12/01 12:21:59  luke
imported

Revision 1.1.1.1  1998/09/01 02:48:51  luke
Imported from jimi_1_0_Release codebase

Revision 1.1  1998/06/23 16:19:55  chris
Cosmetics, and added Debug class.

Revision 1.4  1998/06/04 22:12:16  chris
Added $Log: Debug.java,v $
Added Revision 1.1.1.1  1998/12/01 12:21:59  luke
Added imported
Added
Added Revision 1.1.1.1  1998/09/01 02:48:51  luke
Added Imported from jimi_1_0_Release codebase
Added
Added Revision 1.1  1998/06/23 16:19:55  chris
Added Cosmetics, and added Debug class.
Added to a few files.

*/
