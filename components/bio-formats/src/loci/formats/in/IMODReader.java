//
// IMODReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;

/**
 * Reader for IMOD binary files.
 *
 * See http://bio3d.colorado.edu/imod/doc/binspec.html
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/IMODReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/IMODReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class IMODReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "IMODV1.2";

  // -- Fields --

  private float[][][][] points;
  private byte[][] colors;

  // -- Constructor --

  /** Constructs a new IMOD reader. */
  public IMODReader() {
    super("IMOD", "mod");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    // draw points given for each contour

    /*
    Region image = new Region(x, y, w, h);
    int pixel =
      getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType());

    for (int obj=0; obj<points.length; obj++) {
      for (int contour=0; contour<points[obj].length; contour++) {
        for (int point=0; point<points[obj][contour].length; point++) {
          if (points[obj][contour][point][2] == no) {
            int xc = (int) points[obj][contour][point][0];
            int yc = getSizeY() - (int) points[obj][contour][point][1] - 1;

            if (image.containsPoint(xc, yc)) {
              xc -= x;
              yc -= y;

              int index = pixel * (yc * w + xc);
              System.arraycopy(colors[obj], 0, buf, index, colors[obj].length);
            }
          }
        }
      }
    }
    */

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      points = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String check = in.readString(8);

    if (!check.equals(MAGIC_STRING)) {
      throw new FormatException("Invalid file ID: " + check);
    }

    String filename = in.readString(128);
    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    core[0].sizeZ = in.readInt();

    int nObjects = in.readInt();
    points = new float[nObjects][][][];
    colors = new byte[nObjects][3];

    int flags = in.readInt();

    int drawMode = in.readInt();
    int mouseMode = in.readInt();

    int blackLevel = in.readInt();
    int whiteLevel = in.readInt();

    float xOffset = in.readFloat();
    float yOffset = in.readFloat();
    float zOffset = in.readFloat();

    float xScale = in.readFloat();
    float yScale = in.readFloat();
    float zScale = in.readFloat();

    int currentObject = in.readInt();
    int currentContour = in.readInt();
    int currentPoint = in.readInt();

    int res = in.readInt();
    int thresh = in.readInt();

    float pixSize = in.readFloat();
    int pixSizeUnits = in.readInt();

    int checksum = in.readInt();

    float alpha = in.readFloat();
    float beta = in.readFloat();
    float gamma = in.readFloat();

    addGlobalMeta("Model name", filename);
    addGlobalMeta("Model flags", flags);
    addGlobalMeta("Model drawing mode", drawMode);
    addGlobalMeta("Mouse mode", mouseMode);
    addGlobalMeta("Black level", blackLevel);
    addGlobalMeta("White level", whiteLevel);
    addGlobalMeta("X offset", xOffset);
    addGlobalMeta("Y offset", yOffset);
    addGlobalMeta("Z offset", zOffset);
    addGlobalMeta("X scale", xScale);
    addGlobalMeta("Y scale", yScale);
    addGlobalMeta("Z scale", zScale);
    addGlobalMeta("Alpha", alpha);
    addGlobalMeta("Beta", beta);
    addGlobalMeta("Gamma", gamma);

    MetadataStore store = makeFilterMetadata();
    ArrayList<String> roiIDs = new ArrayList<String>();

    for (int obj=0; obj<nObjects; obj++) {
      String objt = in.readString(4);
      while (!objt.equals("OBJT")) {
        String prefix = "Object #" + obj + " ";
        if (objt.equals("IMAT")) {
          addGlobalMeta(prefix + "ambient", in.read());
          addGlobalMeta(prefix + "diffuse", in.read());
          addGlobalMeta(prefix + "specular", in.read());
          addGlobalMeta(prefix + "shininess", in.read());
          addGlobalMeta(prefix + "fill red", in.read());
          addGlobalMeta(prefix + "fill green", in.read());
          addGlobalMeta(prefix + "fill blue", in.read());
          addGlobalMeta(prefix + "sphere quality", in.read());
          in.skipBytes(4);
          addGlobalMeta(prefix + "black level", in.read());
          addGlobalMeta(prefix + "white level", in.read());
          in.skipBytes(2);
        }
        objt = in.readString(4);
      }
      String objName = in.readString(64);
      in.skipBytes(64); // unused

      int nContours = in.readInt();
      points[obj] = new float[nContours][][];

      int objFlags = in.readInt();
      int axis = in.readInt();
      int objDrawMode = in.readInt();

      float red = in.readFloat();
      float green = in.readFloat();
      float blue = in.readFloat();

      colors[obj][0] = (byte) (red * 255);
      colors[obj][1] = (byte) (green * 255);
      colors[obj][2] = (byte) (blue * 255);

      int pixelRadius = in.readInt();
      int pixelSymbol = in.read();
      int symbolSize = in.read();
      int lineWidth2D = in.read();
      int lineWidth3D = in.read();
      int lineStyle = in.read();
      int symbolFlags = in.read();
      int symbolPadding = in.read();
      int transparency = in.read();

      int nMeshes = in.readInt();
      int nSurfaces = in.readInt();

      if (getMetadataOptions().getMetadataLevel() == MetadataLevel.ALL) {
        String roiID = MetadataTools.createLSID("ROI", obj);
        store.setROIID(roiID, obj);
        store.setROIName(objName, obj);

        roiIDs.add(roiID);
      }


      int nextShape = 0;

      for (int contour=0; contour<nContours; contour++) {
        in.skipBytes(4); // CONT

        int nPoints = in.readInt();
        int contourFlags = in.readInt();
        int timeIndex = in.readInt();
        int surface = in.readInt();

        points[obj][contour] = new float[nPoints][3];

        for (int p=0; p<nPoints; p++) {
          for (int i=0; i<points[obj][contour][p].length; i++) {
            points[obj][contour][p][i] = in.readFloat();
          }
        }

        if (getMetadataOptions().getMetadataLevel() == MetadataLevel.ALL) {
          boolean wild = (contourFlags & 0x10) == 0x10;

          if (wild) {
            int r = colors[obj][0] & 0xff;
            int g = colors[obj][1] & 0xff;
            int b = colors[obj][2] & 0xff;

            for (int i=0; i<nPoints; i++) {
              String shapeID =
                MetadataTools.createLSID("Shape", obj, nextShape);
              store.setPointID(shapeID, obj, nextShape);
              store.setPointStrokeColor(
                new Color(r, g, b, 0xff), obj, nextShape);
              store.setPointStrokeWidth(
                new Double(lineWidth2D), obj, nextShape);
              if (lineStyle == 1) {
                store.setPointStrokeDashArray("5", obj, nextShape);
              }

              store.setPointX(
                new Double(points[obj][contour][i][0]), obj, nextShape);
              store.setPointY(
                new Double(points[obj][contour][i][1]), obj, nextShape);
              store.setPointTheZ(new NonNegativeInteger(
                (int) points[obj][contour][i][2]), obj, nextShape);

              nextShape++;
            }
          }
          else {
            String shapeID = MetadataTools.createLSID("Shape", obj, nextShape);
            boolean closed = (contourFlags & 0x8) == 0;

            int r = colors[obj][0] & 0xff;
            int g = colors[obj][1] & 0xff;
            int b = colors[obj][2] & 0xff;

            StringBuffer sb = new StringBuffer();
            for (int i=0; i<nPoints; i++) {
              sb.append(points[obj][contour][i][0]);
              sb.append(",");
              sb.append(points[obj][contour][i][1]);
              if (i < nPoints - 1) {
                sb.append(" ");
              }
            }

            if (closed) {
              store.setPolygonID(shapeID, obj, nextShape);
              store.setPolygonStrokeColor(
                new Color(r, g, b, 0xff), obj, nextShape);
              store.setPolygonStrokeWidth(
                new Double(lineWidth2D), obj, nextShape);
              if (lineStyle == 1) {
                store.setPolygonStrokeDashArray("5", obj, nextShape);
              }

              if (nPoints > 0) {
                store.setPolygonTheZ(new NonNegativeInteger(
                  (int) points[obj][contour][0][2]), obj, nextShape);
              }
              store.setPolygonPoints(sb.toString(), obj, nextShape);
            }
            else {
              store.setPolylineID(shapeID, obj, nextShape);
              store.setPolylineStrokeColor(
                new Color(r, g, b, 0xff), obj, nextShape);
              store.setPolylineStrokeWidth(
                new Double(lineWidth2D), obj, nextShape);
              if (lineStyle == 1) {
                store.setPolylineStrokeDashArray("5", obj, nextShape);
              }

              if (nPoints > 0) {
                store.setPolylineTheZ(new NonNegativeInteger(
                  (int) points[obj][contour][0][2]), obj, nextShape);
              }
              store.setPolylinePoints(sb.toString(), obj, nextShape);
            }
            nextShape++;
          }
        }
      }

      for (int mesh=0; mesh<nMeshes; mesh++) {
        in.skipBytes(4); // MESH

        int vsize = in.readInt();
        int lsize = in.readInt();
        int meshFlags = in.readInt();
        int timeIndex = in.readShort();
        int surface = in.readShort();

        // TODO
        in.skipBytes(12 * vsize + 4 * lsize);
      }
    }

    double physicalX = 0d, physicalY = 0d, physicalZ = 0d;

    while (in.getFilePointer() + 4 < in.length()) {
      // check if there is any extra metadata at the end of the file

      String chunkType = in.readString(4);
      if (chunkType.equals("IMAT")) {
        in.skipBytes(20);
      }
      else if (chunkType.equals("VIEW")) {
        in.skipBytes(4);
        if (in.readInt() != 1) {
          in.skipBytes(176);

          int bytesPerView = in.readInt();

          in.skipBytes(bytesPerView);
        }
      }
      else if (chunkType.equals("MINX")) {
        in.skipBytes(40); // skip old transformation values

        physicalX = in.readFloat();
        physicalY = in.readFloat();
        physicalZ = in.readFloat();
      }
    }

    core[0].sizeT = 1;
    core[0].sizeC = 3;
    core[0].rgb = true;
    core[0].interleaved = true;
    core[0].imageCount = getSizeT() * getSizeZ();
    core[0].littleEndian = false;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = FormatTools.UINT8;

    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.ALL) {
      for (int i=0; i<roiIDs.size(); i++) {
        store.setROIID(roiIDs.get(i), i);
        store.setImageROIRef(roiIDs.get(i), 0, i);
      }

      if (physicalX > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat(adjustForUnits(pixSizeUnits, physicalX)), 0);
      }
      if (physicalY > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat(adjustForUnits(pixSizeUnits, physicalY)), 0);
      }
      if (physicalZ > 0) {
        store.setPixelsPhysicalSizeZ(
          new PositiveFloat(adjustForUnits(pixSizeUnits, physicalZ)), 0);
      }
    }
  }

  // -- Helper methods --

  private double adjustForUnits(int units, double value) {
    switch (units) {
      case 0:   // pixels
        return value;
      case 1:   // m
        return value * 100000000.0;
      case 3:   // km
        return value * 100000000000.0;
      case -2:  // cm
        return value * 1000000.0;
      case -3:  // mm
        return value * 1000.0;
      case -6:  // µm
        return value;
      case -9:  // nm
        return value / 1000.0;
      case -10: // Å
        return value / 10000.0;
      case -12: // pm
        return value / 1000000.0;
    }
    return value;
  }

}
