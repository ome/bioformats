//
// ReaderTest.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.test;

import java.io.*;
import junit.framework.*;
import loci.formats.FileStitcher;
import loci.formats.FormatException;

/** Tests the Bio-Formats file format readers. */
public class ReaderTest extends TestCase {

  private String id;
  private FileStitcher reader;

  // -- Constructor --

  public ReaderTest(String dataFile) {
    super("testReader");
    id = dataFile;
  }

  // -- ReaderTest API methods --

  public void testReader() {
    boolean success = false;
    System.out.print(id + ": ");
    try {
      int sizeZ = reader.getSizeZ(id);
      System.out.print("sizeZ=" + sizeZ + "; ");
      int sizeC = reader.getSizeC(id);
      System.out.print("sizeC=" + sizeC + "; ");
      int sizeT = reader.getSizeT(id);
      System.out.println("sizeT=" + sizeT);
      success = true;
    }
    catch (FormatException exc) { }
    catch (IOException exc) { }
    assertTrue(success);
  }

  // -- TestCase API methods --

  protected void setUp() {
    reader = new FileStitcher();
  }

  protected void tearDown() {
    reader = null;
  }

  // -- Main method --

  public static void main(String[] args) {
    String base = args[0];

    // construct test suite
    TestSuite suite = new TestSuite();
    try {
      BufferedReader in = new BufferedReader(new FileReader("data-files.txt"));
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        suite.addTest(new ReaderTest(base + File.separator + line));
      }
      in.close();
    }
    catch (IOException exc) {
      exc.printStackTrace();
      System.exit(1);
    }

    // run test suite
    TestResult result = new TestResult();
    suite.run(result);
    System.out.println();
    System.out.println("Tests complete.");
    System.out.println(result.failureCount() + " failures, " +
      result.errorCount() + " errors.");
  }

}
