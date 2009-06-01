//
// ImageConverter.java
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

import java.awt.image.IndexColorModel;
import java.io.IOException;

import loci.common.Location;
import loci.common.LogTools;
import loci.formats.ChannelFiller;
import loci.formats.ChannelMerger;
import loci.formats.ChannelSeparator;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatHandler;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.TiffWriter;

/**
 * ImageConverter is a utility class for converting a file between formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tools/ImageConverter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tools/ImageConverter.java">SVN</a></dd></dl>
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
    String map = null;
    String compression = null;
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
          else if (args[i].equals("-expand")) fill = true;
          else if (args[i].equals("-bigtiff")) bigtiff = true;
          else if (args[i].equals("-map")) map = args[++i];
          else if (args[i].equals("-compression")) compression = args[++i];
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
    if (in == null || out == null) {
      String[] s = {
        "To convert a file between formats, run:",
        "  bfconvert [-debug] [-stitch] [-separate] [-merge] [-expand]",
        "    [-bigtiff] [-compression codec] [-series series] [-map id]",
        "    in_file out_file",
        "",
        "      -debug: turn on debugging output",
        "     -stitch: stitch input files with similar names",
        "   -separate: split RGB images into separate channels",
        "      -merge: combine separate channels into RGB image",
        "     -expand: expand indexed color to RGB",
        "    -bigtiff: force BigTIFF files to be written",
        "-compression: specify the codec to use when saving images",
        "     -series: specify which image series to convert",
        "        -map: specify file on disk to which name should be mapped",
        "",
        "If any of the following patterns are present in out_file, they will",
        "be replaced with the indicated metadata value from the input file.",
        "",
        "   Pattern:    Metadata value:",
        "   ---------------------------",
        "   " + FormatTools.SERIES_NUM + "          series index",
        "   " + FormatTools.SERIES_NAME + "          series name",
        "   " + FormatTools.CHANNEL_NUM + "          channel index",
        "   " + FormatTools.CHANNEL_NAME +"          channel name",
        "   " + FormatTools.Z_NUM + "          Z index",
        "   " + FormatTools.T_NUM + "          T index",
        "",
        "If any of these patterns are present, then the images to be saved",
        "will be split into multiple files.  For example, if the input file",
        "contains 5 Z sections and 3 timepoints, and out_file is",
        "'converted_Z%z_T%t.tiff'",
        "then 15 files will be created, with the names",
        "",
        "  converted_Z0_T0.tiff",
        "  converted_Z0_T1.tiff",
        "  converted_Z0_T2.tiff",
        "  converted_Z1_T0.tiff",
        "  ...",
        "  converted_Z4_T2.tiff",
        "",
        "Each file would have a single image plane."
      };
      for (int i=0; i<s.length; i++) LogTools.println(s[i]);
      return false;
    }

    if (map != null) Location.mapId(in, map);

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
    if (store == null) LogTools.println("OME-XML Java library not found.");
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

    LogTools.print("[" + writer.getFormat() + "] ");
    long mid = System.currentTimeMillis();

    int num = writer.canDoStacks() ? reader.getSeriesCount() : 1;
    long read = 0, write = 0;
    int first = series == -1 ? 0 : series;
    int last = series == -1 ? num : series + 1;
    for (int q=first; q<last; q++) {
      reader.setSeries(q);
      writer.setInterleaved(reader.isInterleaved());
      int numImages = writer.canDoStacks() ? reader.getImageCount() : 1;
      for (int i=0; i<numImages; i++) {
        writer.setId(FormatTools.getFilename(q, i, reader, out));
        if (compression != null) writer.setCompression(compression);

        long s = System.currentTimeMillis();
        byte[] buf = reader.openBytes(i);
        byte[][] lut = reader.get8BitLookupTable();
        if (lut != null) {
          IndexColorModel model = new IndexColorModel(8, lut[0].length,
            lut[0], lut[1], lut[2]);
          writer.setColorModel(model);
        }
        long m = System.currentTimeMillis();
        boolean lastInSeries = i == numImages - 1;
        writer.saveBytes(buf, q, lastInSeries, q == last - 1 && lastInSeries);
        long e = System.currentTimeMillis();
        LogTools.print(".");
        read += m - s;
        write += e - m;
      }
    }
    writer.close();
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
