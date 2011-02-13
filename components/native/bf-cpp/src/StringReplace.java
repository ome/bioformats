//
// StringReplace.java
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (c) 2008-@year@, UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * A program to filter and replace strings in a file.
 *
 * The main reason this program exists is because sed is
 * not available by default on Windows systems.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/native/bf-cpp/src/StringReplace.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/native/bf-cpp/src/StringReplace.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class StringReplace {

  // -- Fields --

  private String input, output;

  // -- Constructor --

  public StringReplace(String inputPattern, String outputPattern) {
    input = inputPattern;
    output = outputPattern;
  }

  // -- StringReplace methods --

  public void processFile(String path) {
    System.out.println("Processing file: " + path);

    // read data from file
    Vector<String> lines = null;
    try {
      lines = readFile(path);
    }
    catch (IOException exc) {
      System.err.println("Error: cannot read file: " + path);
      return;
    }

    // replace patterns
    int changed = 0;
    for (int i=0; i<lines.size(); i++) {
      String line = lines.get(i);
      String newLine = line.replaceAll(input, output);
      if (!line.equals(newLine)) {
        lines.set(i, newLine);
        changed++;
      }
    }

    // write data back to file
    try {
      writeFile(path, lines);
      System.out.println(changed + " lines updated.");
    }
    catch (IOException exc) {
      System.err.println("Error: cannot write file: " + path);
      return;
    }
  }

  public Vector<String> readFile(String path) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(path));
    Vector<String> lines = new Vector<String>();
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      lines.add(line);
    }
    in.close();
    return lines;
  }

  public void writeFile(String path, Vector<String> lines) throws IOException {
    File destFile = new File(path);
    File tempFile = new File(path + ".tmp");
    PrintWriter out = new PrintWriter(new FileWriter(tempFile));
    for (String line : lines) out.println(line);
    out.close();
    destFile.delete();
    tempFile.renameTo(destFile);
  }

  // -- Helper utility methods --

  public static String fixEscaped(String s) {
    s = s.replaceAll("\\\\n", "\n");
    s = s.replaceAll("\\\\r", "\r");
    s = s.replaceAll("\\\\t", "\t");
    return s;
  }

  // -- Main method --

  public static void main(String[] args) {
    if (args == null || args.length < 3) {
      System.out.println("Usage: java StringReplace " +
        "inputPattern outputPattern file [file2 file3 ...]");
      return;
    }
    String inputPattern = fixEscaped(args[0]);
    String outputPattern = fixEscaped(args[1]);
    StringReplace sr = new StringReplace(inputPattern, outputPattern);
    for (int i=2; i<args.length; i++) sr.processFile(args[i]);
  }

}
