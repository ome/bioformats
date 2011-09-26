//
// XMLIndent.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import loci.common.xml.XMLTools;

/**
 * Indents XML to be more readable.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/XMLIndent.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/XMLIndent.java;hb=HEAD">Gitweb</a></dd></dl>
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
      process(new BufferedReader(new InputStreamReader(System.in)), keepValid);
    }
    else {
      // read from file(s)
      for (int i=0; i<args.length; i++) {
        if (!args[i].startsWith("-")) {
          process(new BufferedReader(new FileReader(args[i])), keepValid);
        }
      }
    }
  }

}
