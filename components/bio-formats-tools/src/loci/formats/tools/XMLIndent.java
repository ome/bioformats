/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import loci.common.Constants;
import loci.common.xml.XMLTools;

/**
 * Indents XML to be more readable.
 */
public class XMLIndent {

  public static void process(BufferedReader in, boolean keepValid)
    throws IOException
  {
    StringBuffer sb = new StringBuffer();
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      sb.append(line);
    }
    in.close();
    System.out.println(XMLTools.indentXML(sb.toString(), 3, keepValid));
  }

  public static void main(String[] args) throws Exception {
    // parse command line arguments
    int numFiles = 0;
    boolean keepValid = false;
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
        if (args[i].equals("-valid")) keepValid = true;
        else {
          System.err.println("Warning: ignoring unknown command line flag \"" +
            args[i] + "\"");
        }
      }
      else numFiles++;
    }

    if (numFiles == 0) {
      // read from stdin
      process(new BufferedReader(
        new InputStreamReader(System.in, Constants.ENCODING)), keepValid);
    }
    else {
      // read from file(s)
      for (int i=0; i<args.length; i++) {
        if (!args[i].startsWith("-")) {
          process(new BufferedReader(new InputStreamReader(
            new FileInputStream(args[i]), Constants.ENCODING)), keepValid);
        }
      }
    }
  }

}
