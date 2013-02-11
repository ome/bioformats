/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.gui.BufferedImageWriter;
import loci.formats.meta.IMetadata;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import ome.xml.model.enums.EnumerationException;

/**
 * Creates sample OME-TIFF datasets according to the given parameters.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/MakeTestOmeTiff.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/MakeTestOmeTiff.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MakeTestOmeTiff {

  public void makeSamples() throws FormatException, IOException {
    makeOmeTiff("single-channel", "439", "167", "1", "1", "1", "XYZCT");
    makeOmeTiff("multi-channel", "439", "167", "1", "3", "1", "XYZCT");
    makeOmeTiff("z-series", "439", "167", "5", "1", "1", "XYZCT");
    makeOmeTiff("multi-channel-z-series", "439", "167", "5", "3", "1", "XYZCT");
    makeOmeTiff("time-series", "439", "167", "1", "1", "7", "XYZCT");
    makeOmeTiff("multi-channel-time-series", "439", "167", "1", "3", "7",
      "XYZCT");
    makeOmeTiff("4D-series", "439", "167", "5", "1", "7", "XYZCT");
    makeOmeTiff("multi-channel-4D-series", "439", "167", "5", "3", "7",
      "XYZCT");
  }

  public int makeOmeTiff(final String... args) throws FormatException,
    IOException
  {
    if (args == null || args.length == 0) {
      makeSamples();
      return 0;
    }

    // parse command line arguments
    if (args.length != 7) {
      displayUsage();
      return 1;
    }
    final String name = args[0];
    final CoreMetadata info = new CoreMetadata();
    info.sizeX = Integer.parseInt(args[1]);
    info.sizeY = Integer.parseInt(args[2]);
    info.sizeZ = Integer.parseInt(args[3]);
    info.sizeC = Integer.parseInt(args[4]);
    info.sizeT = Integer.parseInt(args[5]);
    info.imageCount = info.sizeZ * info.sizeC * info.sizeT;
    info.dimensionOrder = args[6].toUpperCase();

    makeOmeTiff(name, info);
    return 0;
  }

  public void makeOmeTiff(final String name, final CoreMetadata info)
    throws FormatException, IOException
  {
    final String id = getId(name);
    final OMETiffWriter out = createWriter(name, info, id);
    writeData(name, info, id, out);
  }

  public static void main(final String[] args) throws FormatException,
    IOException
  {
    final int returnCode = new MakeTestOmeTiff().makeOmeTiff(args);
    System.exit(returnCode);
  }

  // -- Helper methods --

  private void displayUsage() {
    System.out.println("Usage: java loci.formats.tools.MakeTestOmeTiff name");
    System.out.println("           SizeX SizeY SizeZ SizeC SizeT DimOrder");
    System.out.println();
    System.out.println("  name: output filename");
    System.out.println("  SizeX: width of image planes");
    System.out.println("  SizeY: height of image planes");
    System.out.println("  SizeZ: number of focal planes");
    System.out.println("  SizeC: number of channels");
    System.out.println("  SizeT: number of time points");
    System.out.println("  DimOrder: planar ordering:");
    System.out.println("    XYZCT, XYZTC, XYCZT, XYCTZ, XYTZC, or XYTCZ");
    System.out.println();
    System.out.println("Example:");
    System.out.println("  java loci.formats.tools.MakeTestOmeTiff test \\");
    System.out.println("    517 239 5 3 4 XYCZT");
  }

  private String getId(final String name) {
    final String id;
    if (name.toLowerCase().endsWith(".ome.tif")) id = name;
    else id = name + ".ome.tif";
    return id;
  }

  private OMETiffWriter createWriter(final String name,
    final CoreMetadata info, final String id) throws FormatException,
    IOException
  {
    final OMETiffWriter out = new OMETiffWriter();
    try {
      out.setMetadataRetrieve(createMetadata(name, info));
    }
    catch (final DependencyException e) {
      throw new FormatException(e);
    }
    catch (final ServiceException e) {
      throw new FormatException(e);
    }
    catch (final EnumerationException e) {
      throw new FormatException(e);
    }
    ensureNonExisting(id);
    out.setId(id);
    return out;
  }

  private void writeData(final String name, final CoreMetadata info,
    final String id, final OMETiffWriter out) throws FormatException,
    IOException
  {
    System.out.print(id);
    for (int i = 0; i < info.imageCount; i++) {
      final BufferedImage plane = createPlane(name, info, i);
      out.saveBytes(i, BufferedImageWriter.toBytes(plane, out));
      System.out.print(".");
    }
    System.out.println();
    out.close();
  }

  private void ensureNonExisting(final String id) {
    final File idFile = new File(id);
    if (idFile.exists()) idFile.delete();
  }

  private IMetadata createMetadata(final String name, final CoreMetadata info)
    throws DependencyException, ServiceException, EnumerationException
  {
    final ServiceFactory serviceFactory = new ServiceFactory();
    final OMEXMLService omexmlService =
      serviceFactory.getInstance(OMEXMLService.class);
    final IMetadata meta = omexmlService.createOMEXMLMetadata();
    MetadataTools.populateMetadata(meta, 0, name, info);
    return meta;
  }

  private BufferedImage createPlane(final String name, final CoreMetadata info,
    final int no)
  {
    final int[] zct =
      FormatTools.getZCTCoords(info.dimensionOrder, info.sizeZ, info.sizeC,
        info.sizeT, info.imageCount, no);

    final BufferedImage plane =
      new BufferedImage(info.sizeX, info.sizeY, BufferedImage.TYPE_BYTE_GRAY);
    final Graphics2D g = plane.createGraphics();

    // draw gradient
    final int type = 0;
    for (int y = 0; y < info.sizeY; y++) {
      final int v = gradient(type, y, info.sizeY);
      g.setColor(new Color(v, v, v));
      g.drawLine(0, y, info.sizeX, y);
    }

    // build list of text lines from planar information
    final ArrayList<TextLine> lines = new ArrayList<TextLine>();
    final Font font = g.getFont();
    lines.add(new TextLine(name, font.deriveFont(32f), 5, -5));
    lines.add(new TextLine(info.sizeX + " x " + info.sizeY, font.deriveFont(
      Font.ITALIC, 16f), 20, 10));
    lines.add(new TextLine(info.dimensionOrder, font.deriveFont(Font.ITALIC,
      14f), 30, 5));
    int space = 5;
    if (info.sizeZ > 1) {
      lines.add(new TextLine(
        "Focal plane = " + (zct[0] + 1) + "/" + info.sizeZ, font, 20, space));
      space = 2;
    }
    if (info.sizeC > 1) {
      lines.add(new TextLine("Channel = " + (zct[1] + 1) + "/" + info.sizeC,
        font, 20, space));
      space = 2;
    }
    if (info.sizeT > 1) {
      lines.add(new TextLine("Time point = " + (zct[2] + 1) + "/" + info.sizeT,
        font, 20, space));
      space = 2;
    }

    // draw text lines to image
    g.setColor(Color.white);
    int yoff = 0;
    for (int l = 0; l < lines.size(); l++) {
      final TextLine text = lines.get(l);
      g.setFont(text.font);
      final Rectangle2D r =
        g.getFont().getStringBounds(text.line, g.getFontRenderContext());
      yoff += r.getHeight() + text.ypad;
      g.drawString(text.line, text.xoff, yoff);
    }
    g.dispose();

    return plane;
  }

  private int gradient(final int type, final int num, final int total) {
    final int max = 96;
    final int split = type / 2 + 1;
    final boolean reverse = type % 2 == 0;
    int v = max;
    final int splitTotal = total / split;
    for (int i = 1; i <= split + 1; i++) {
      if (num < i * splitTotal) {
        if (i % 2 == 0) v = max * (num % splitTotal) / splitTotal;
        else v = max * (splitTotal - num % splitTotal) / splitTotal;
        break;
      }
    }
    if (reverse) v = max - v;
    return v;
  }

  // -- Helper classes --

  private static class TextLine {

    final String line;
    final Font font;
    final int xoff;
    final int ypad;

    TextLine(final String line, final Font font, final int xoff, final int ypad)
    {
      this.line = line;
      this.font = font;
      this.xoff = xoff;
      this.ypad = ypad;
    }

  }

}
