//
// OmeroReaderTest.java
//

/*
LOCI software manual test suite. Copyright (C) 2007-@year@
Curtis Rueden and Melissa Linkert. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
