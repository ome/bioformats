//
// BF.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/src/loci/plugins/BF.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/src/loci/plugins/BF.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class BF {

  // -- Constructor --

  private BF() { }

  // -- Utility methods --

  public static void debug(String msg) {
    if (IJ.debugMode) IJ.log("LOCI: " + msg);
  }

  public static void status(boolean quiet, String msg) {
    if (quiet) return;
    IJ.showStatus(msg);
  }

  public static void warn(boolean quiet, String msg) {
    if (quiet) return;
    IJ.log("Warning: " + msg);
  }

  public static void progress(boolean quiet, int value, int max) {
    if (quiet) return;
    IJ.showProgress(value, max);
  }

  public static ImagePlus[] openImagePlus(String path)
    throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    return openImagePlus(options);
  }

  public static ImagePlus[] openThumbImagePlus(String path)
    throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    return openThumbImagePlus(options);
  }

  public static ImagePlus[] openImagePlus(ImporterOptions options)
    throws FormatException, IOException
  {
    // TODO: Eliminate use of the ImporterPrompter. While no dialogs should
    // appear due to the quiet and windowless flags, it would be cleaner to
    // avoid piping everything through invisible GenericDialogs internally.
    //
    // However, we need to be sure all the Dialog classes are not performing
    // any "side-effect" logic on the ImportProcess and/or ImporterOptions
    // before we can make this change.
    //
    // Another downside might be that we could miss out on any other magic that
    // ImageJ is performing (e.g., macro-related functionality), but further
    // testing is warranted.

    options.setQuiet(true); // NB: Only needed due to ImporterPrompter.
    options.setWindowless(true); // NB: Only needed due to ImporterPrompter.

    ImportProcess process = new ImportProcess(options);

    new ImporterPrompter(process); // NB: Could eliminate this (see above).

    if (!process.execute()) return null;
    ImagePlusReader reader = new ImagePlusReader(process);
    return reader.openImagePlus();
  }

  public static ImagePlus[] openThumbImagePlus(ImporterOptions options)
    throws FormatException, IOException
  {
    options.setQuiet(true); // NB: Only needed due to ImporterPrompter.
    options.setWindowless(true); // NB: Only needed due to ImporterPrompter.

    ImportProcess process = new ImportProcess(options);

    new ImporterPrompter(process); // NB: Could eliminate this (see above).

    if (!process.execute()) return null;
    ImagePlusReader reader = new ImagePlusReader(process);
    return reader.openThumbImagePlus();
  }

}
