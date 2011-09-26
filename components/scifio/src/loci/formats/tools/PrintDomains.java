//
// PrintDomains.java
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

/**
 * Utility class for printing a list of scientific domains supported by
 * Bio-Formats, and all of the supported formats in each domain.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/PrintDomains.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/PrintDomains.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PrintDomains {

  public static void main(String[] args) {
    // get a list of all available readers
    IFormatReader[] readers = new ImageReader().getReaders();

    Hashtable<String, Vector<IFormatReader>> domains =
      new Hashtable<String, Vector<IFormatReader>>();

    for (String domain : FormatTools.ALL_DOMAINS) {
      domains.put(domain, new Vector<IFormatReader>());
    }

    for (IFormatReader reader : readers) {
      try {
        String[] readerDomains = reader.getPossibleDomains("");
        for (String domain : readerDomains) {
          domains.get(domain).add(reader);
        }
      }
      catch (FormatException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    String[] domainKeys = domains.keySet().toArray(new String[domains.size()]);
    Arrays.sort(domainKeys);

    for (String domain : domainKeys) {
      System.out.println(domain + ":");
      Vector<IFormatReader> r = domains.get(domain);
      for (IFormatReader reader : r) {
        System.out.println("  " + reader.getFormat());
      }
    }
  }

}
