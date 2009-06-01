//
// XMLValidate.java
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

import loci.formats.XMLTools;

/**
 * Attempts to validate the given XML files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tools/XMLValidate.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tools/XMLValidate.java">SVN</a></dd></dl>
 */
public class XMLValidate {

  public static void process(String label, BufferedReader in)
    throws IOException
  {
    StringBuffer sb = new StringBuffer();
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      sb.append(line);
    }
    in.close();
    XMLTools.validateXML(sb.toString(), label);
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      // read from stdin
      process("<stdin>", new BufferedReader(new InputStreamReader(System.in)));
    }
    else {
      // read from file(s)
      for (int i=0; i<args.length; i++) {
        process(args[i], new BufferedReader(new FileReader(args[i])));
      }
    }
  }

}
