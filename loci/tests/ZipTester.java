//
// ZipTester.java
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

import java.io.*;
import loci.formats.FormatException;
import loci.formats.ImageReader;

/**
 * A class for testing {@link loci.formats.RandomAccessStream}'s
 * ability to handle files compressed with gz, zip or bz2.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/tests/ZipTester.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/tests/ZipTester.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ZipTester {

  private static ImageReader reader = new ImageReader();

  public static void main(String[] args) throws IOException, FormatException, InterruptedException {
    if (args.length < 2) {
      System.out.println("Usage: java loci.tests.ZipTester " +
        "/path/to/input-file /path/to/output-folder");
      System.exit(1);
    }
    File in = new File(args[0]);
    if (!in.exists()) {
      System.out.println("Input file '" + in + "' does not exist.");
      System.exit(2);
    }
    File out = new File(args[1]);
    if (!out.isDirectory()) {
      System.out.println("Output folder '" + out + "' is not a directory.");
      System.exit(3);
    }

    // create temporary working directory
    File tmp = new File(out, "ZipTester.tmp");
    if (!tmp.exists()) tmp.mkdir();

    // copy original file into working directory
    String name = in.getName();
    String id = new File(tmp, name).getPath();
    System.out.println("cp '" + in.getPath() + "' '" + id + "'");
    FileInputStream fin = new FileInputStream(in);
    FileOutputStream fout = new FileOutputStream(id);
    byte[] buf = new byte[8192];
    while (true) {
      int r = fin.read(buf);
      if (r <= 0) break;
      fout.write(buf, 0, r);
    }
    fout.close();
    fin.close();

    time(id);

    Runtime r = Runtime.getRuntime();
    String[] cmd;
    Process p;

    // test zip archive
    String zip = id + ".zip";
    cmd = new String[] {"zip", zip, id};
    System.out.println("zip '" + zip + "' '" + id + "'");
    r.exec(cmd).waitFor();
    time(zip);

    // test bz2 archive
    String bz2 = id + ".bz2";
    cmd = new String[] {"bzip2", "-k", id};
    System.out.println("bzip2 -k '" + id + "'");
    r.exec(cmd).waitFor();
    time(bz2);

    // create gz archive
    String gz = id + ".gz";
    cmd = new String[] {"gzip", id};
    System.out.println("gzip '" + id + "'");
    r.exec(cmd).waitFor();
    time(gz);

    // clean up
    new File(zip).delete();
    new File(bz2).delete();
    new File(gz).delete();
    tmp.delete();
  }

  public static void time(String id) throws IOException, FormatException {
    long t1 = System.currentTimeMillis();
    System.out.print("Timing " + id + ": ");
    reader.setId(id);
    long t2 = System.currentTimeMillis();
    System.out.println((t2 - t1) + " ms");
  }

}
