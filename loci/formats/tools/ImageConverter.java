//
// ImageConverter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

package loci.formats.tools;

import java.awt.Image;
import java.io.IOException;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.TiffWriter;

/**
 * ImageConverter is a utility class for converting a file between formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/tools/ImageConverter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/tools/ImageConverter.java">SVN</a></dd></dl>
 */
public final class ImageConverter {

  // -- Constructor --

  private ImageConverter() { }

  // -- Utility methods --

  /** A utility method for converting a file from the command line. */
  public static boolean testConvert(IFormatWriter writer, String[] args)
    throws FormatException, IOException
  {
    String in = null, out = null;
    boolean stitch = false, separate = false, merge = false, fill = false;
    boolean bigtiff = false;
    int series = -1;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].startsWith("-") && args.length > 1) {
          if (args[i].equals("-debug")) FormatHandler.setDebug(true);
          else if (args[i].equals("-stitch")) stitch = true;
          else if (args[i].equals("-separate")) separate = true;
          else if (args[i].equals("-merge")) merge = true;
          else if (args[i].equals("-fill")) fill = true;
          else if (args[i].equals("-bigtiff")) bigtiff = true;
          else if (args[i].equals("-series")) {
            try {
              series = Integer.parseInt(args[++i]);
            }
            catch (NumberFormatException exc) { }
          }
          else LogTools.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (in == null) in = args[i];
          else if (out == null) out = args[i];
          else LogTools.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (FormatHandler.debug) {
      LogTools.println("Debugging at level " + FormatHandler.debugLevel);
    }
    String className = writer.getClass().getName();
    if (in == null || out == null) {
      LogTools.println("To convert a file to " + writer.getFormat() +
        " format, run:");
      LogTools.println("  bfconvert [-debug] in_file out_file");
      return false;
    }

    long start = System.currentTimeMillis();
    LogTools.print(in + " ");
    IFormatReader reader = new ImageReader();
    if (stitch) reader = new FileStitcher(reader);
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);
    if (fill) reader = new ChannelFiller(reader);

    reader.setMetadataFiltered(true);
    reader.setOriginalMetadataPopulated(true);
    MetadataStore store = MetadataTools.createOMEXMLMetadata();
    if (store == null) LogTools.println("OME-Java library not found.");
    else reader.setMetadataStore(store);

    reader.setId(in);

    LogTools.print("[" + reader.getFormat() + "] -> " + out + " ");

    store = reader.getMetadataStore();
    if (store instanceof MetadataRetrieve) {
      writer.setMetadataRetrieve((MetadataRetrieve) store);
    }

    if (writer instanceof TiffWriter) {
      ((TiffWriter) writer).setBigTiff(bigtiff);
    }
    else if (writer instanceof ImageWriter) {
      IFormatWriter w = ((ImageWriter) writer).getWriter(out);
      if (w instanceof TiffWriter) {
        ((TiffWriter) w).setBigTiff(bigtiff);
      }
    }
    writer.setId(out);

    LogTools.print("[" + writer.getFormat() + "] ");
    long mid = System.currentTimeMillis();

    int num = writer.canDoStacks() ? reader.getSeriesCount() : 1;
    long read = 0, write = 0;
    int first = series == -1 ? 0 : series;
    int last = series == -1 ? num : series + 1;
    for (int q=first; q<last; q++) {
      reader.setSeries(q);
      int numImages = writer.canDoStacks() ? reader.getImageCount() : 1;
      for (int i=0; i<numImages; i++) {
        long s = System.currentTimeMillis();
        Image image = reader.openImage(i);
        long m = System.currentTimeMillis();
        writer.saveImage(image, q, i == numImages - 1,
          q == num - 1 && i == numImages - 1);
        long e = System.currentTimeMillis();
        LogTools.print(".");
        read += m - s;
        write += e - m;
      }
    }
    long end = System.currentTimeMillis();
    LogTools.println(" [done]");

    // output timing results
    float sec = (end - start) / 1000f;
    long initial = mid - start;
    float readAvg = (float) read / num;
    float writeAvg = (float) write / num;
    LogTools.println(sec + "s elapsed (" +
      readAvg + "+" + writeAvg + "ms per image, " + initial + "ms overhead)");

    return true;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!testConvert(new ImageWriter(), args)) System.exit(1);
  }

}
