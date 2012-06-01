/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import loci.common.Constants;
import loci.ome.io.OmeroReader;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * A class for testing the OMERO implementation of
 * {@link loci.formats.IFormatReader}, for obtaining
 * pixels and metadata from an OMERO database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/OmeroReaderTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/OmeroReaderTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class OmeroReaderTest {

  private static String readLine(BufferedReader in,
    String prompt, String defaultValue) throws IOException
  {
    System.out.print(prompt + " [" + defaultValue + "]: ");
    String value = in.readLine();
    return value.equals("") ? defaultValue : value;
  }

  public static void main(String[] args) throws Exception {
    // configure logging
    Logger root = Logger.getRootLogger();
    root.setLevel(Level.INFO);
    root.addAppender(new ConsoleAppender(new PatternLayout("%m%n")));

    // get credentials from stdin
    BufferedReader in = new BufferedReader(
      new InputStreamReader(System.in, Constants.ENCODING));
    String server = readLine(in, "Server", "localhost");
    String portValue = readLine(in, "Port", "1099");
    String user = readLine(in, "Username", "root");
    String pass = readLine(in, "Password", "ome");
    String pidValue = readLine(in, "Pixels ID", "1");
    int port = Integer.parseInt(portValue);
    int pid = Integer.parseInt(pidValue);
    System.out.println();

    // connect to OMERO server
    System.out.println("Initializing OMERO reader");
    OmeroReader omero = new OmeroReader();
    String id = "omero:\n" +
      "server=" + server + "\n" +
      "port=" + port + "\n" +
      "user=" + user + "\n" +
      "pass=" + pass + "\n" +
      "pid=" + pid;
    omero.setId(id);

    // print some metadata as a simple test
    int sizeX = omero.getSizeX();
    int sizeY = omero.getSizeY();
    System.out.println("Image dimensions are " + sizeX + " x " + sizeY);

    omero.close();
  }

}
