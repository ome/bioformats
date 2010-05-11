//
// BF.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins;

import ij.IJ;
import ij.ImagePlus;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;
import loci.plugins.in.ImporterPrompter;

/**
 * Miscellaneous LOCI plugins utility methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/BF.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/BF.java">SVN</a></dd></dl>
 */
public final class BF {

  // -- Constructor --

  private BF() { }

  // -- Utility methods --

  public static void debug(String msg) {
    if (IJ.debugMode) IJ.log("LOCI: " + msg);
  }

  public static ImagePlus[] openImagePlus(String path)
    throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    return openImagePlus(options);
  }

  public static ImagePlus[] openImagePlus(ImporterOptions options)
    throws FormatException, IOException
  {
    options.setQuiet(true);//TEMP
    options.setWindowless(true);//TEMP
    ImportProcess process = new ImportProcess(options);
    new ImporterPrompter(process);//TEMP
    if (!process.execute()) return null;
    ImagePlusReader reader = new ImagePlusReader(process);
    return reader.openImagePlus();
  }

}
