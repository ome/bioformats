//
// ImagicReader.java
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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * ImagicReader is the file format reader for IMAGIC files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ImagicReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ImagicReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ImagicReader extends FormatReader {

  // -- Fields --

  private RandomAccessInputStream pixelsFile;

  // -- Constructor --

  /** Constructs a new IMAGIC reader. */
  public ImagicReader() {
    super("IMAGIC", "hed");
    domains = new String[] {FormatTools.EM_DOMAIN};
    datasetDescription = "One .hed file plus one similarly-named .img file";
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    pixelsFile.seek(no * FormatTools.getPlaneSize(this));
    readPlane(pixelsFile, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (pixelsFile != null) {
        pixelsFile.close();
      }
      pixelsFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String pixels = id.substring(0, id.lastIndexOf(".")) + ".img";
    pixelsFile = new RandomAccessInputStream(pixels);

    core[0].littleEndian = true;
    in.order(isLittleEndian());
    pixelsFile.order(isLittleEndian());

    int nImages = (int) (in.length() / 1024);

    String imageName = null;
    double physicalXSize = 0d;
    double physicalYSize = 0d;
    double physicalZSize = 0d;

    for (int i=0; i<nImages; i++) {
      in.seek(i * 1024);

      in.skipBytes(16);
      int month = in.readInt();
      int day = in.readInt();
      int year = in.readInt();
      int hour = in.readInt();
      int minute = in.readInt();
      int seconds = in.readInt();

      in.skipBytes(8);

      core[0].sizeY = in.readInt();
      core[0].sizeX = in.readInt();

      String type = in.readString(4);

      if (type.equals("REAL")) {
        core[0].pixelType = FormatTools.FLOAT;
      }
      else if (type.equals("INTG")) {
        core[0].pixelType = FormatTools.UINT16;
      }
      else if (type.equals("PACK")) {
        core[0].pixelType = FormatTools.UINT8;
      }
      else if (type.equals("COMP")) {
        throw new FormatException("Unsupported pixel type 'COMP'");
      }
      else if (type.equals("RECO")) {
        throw new FormatException("Unsupported pixel type 'RECO'");
      }

      int ixold = in.readInt();
      int iyold = in.readInt();
      float averageDensity = in.readFloat();
      float sigma = in.readFloat();
      in.skipBytes(8);
      float maxDensity = in.readFloat();
      float minDensity = in.readFloat();
      in.skipBytes(4);
      float defocus1 = in.readFloat();
      float defocus2 = in.readFloat();
      float defocusAngle = in.readFloat();
      float startAngle = in.readFloat();
      float endAngle = in.readFloat();
      imageName = in.readString(80);
      float ccc3d = in.readFloat();
      int ref3d = in.readInt();
      int micrographID = in.readInt();
      int zShift = in.readInt();
      float alpha = in.readFloat();
      float beta = in.readFloat();
      float gamma = in.readFloat();
      in.skipBytes(8);
      int nAliSum = in.readInt();
      int pointGroup = in.readInt();
      in.skipBytes(28);
      int version = in.readInt();
      int stamp = in.readInt();
      in.skipBytes(120);
      float angle = in.readFloat();
      float voltage = in.readFloat();
      float sphericalAberration = in.readFloat();
      float partialCoherence = in.readFloat();
      float ccc = in.readFloat();
      float errar = in.readFloat();
      float err3d = in.readFloat();
      int ref = in.readInt();
      float classNumber = in.readFloat();
      in.skipBytes(4);
      float representationQuality = in.readFloat();
      float eqZShift = in.readFloat();
      float xShift = in.readFloat();
      float yShift = in.readFloat();
      float numcls = in.readFloat();
      float overallQuality = in.readFloat();
      float equivalentAngle = in.readFloat();
      float eqXShift = in.readFloat();
      float eqYShift = in.readFloat();
      float cmToVar = in.readFloat();
      float informat = in.readFloat();
      int nEigenvalues = in.readInt();
      int nActiveImages = in.readInt();
      physicalXSize = in.readFloat();
      physicalYSize = in.readFloat();
      physicalZSize = in.readFloat();

      addGlobalMeta("IXOLD", ixold);
      addGlobalMeta("IYOLD", iyold);
      addGlobalMeta("Average density (AVDENS)", averageDensity);
      addGlobalMeta("SIGMA", sigma);
      addGlobalMeta("Maximum density (DENSMAX)", maxDensity);
      addGlobalMeta("Minimum density (DENSMIN)", minDensity);
      addGlobalMeta("DEFOCUS1", defocus1);
      addGlobalMeta("DEFOCUS2", defocus2);
      addGlobalMeta("Defocus angle (DEFANGLE)", defocusAngle);
      addGlobalMeta("SINOSTRT", startAngle);
      addGlobalMeta("SINOEND", endAngle);
      addGlobalMeta("Image name", imageName);
      addGlobalMeta("CCC3D", ccc3d);
      addGlobalMeta("REF3D", ref3d);
      addGlobalMeta("MIDENT", micrographID);
      addGlobalMeta("EZSHIFT", zShift);
      addGlobalMeta("EALPHA", alpha);
      addGlobalMeta("EBETA", beta);
      addGlobalMeta("EGAMMA", gamma);
      addGlobalMeta("NALISUM", nAliSum);
      addGlobalMeta("PGROUP", pointGroup);
      addGlobalMeta("IMAGIC Version (IMAVERS)", version);
      addGlobalMeta("REALTYPE", stamp);
      addGlobalMeta("ANGLE", angle);
      addGlobalMeta("VOLTAGE (in kV)", voltage);
      addGlobalMeta("SPABERR (in mm)", sphericalAberration);
      addGlobalMeta("PCOHER", partialCoherence);
      addGlobalMeta("CCC", ccc);
      addGlobalMeta("ERRAR", errar);
      addGlobalMeta("ERR3D", err3d);
      addGlobalMeta("REF", ref);
      addGlobalMeta("CLASSNO", classNumber);
      addGlobalMeta("REPQUAL", representationQuality);
      addGlobalMeta("ZSHIFT", eqZShift);
      addGlobalMeta("XSHIFT", xShift);
      addGlobalMeta("YSHIFT", yShift);
      addGlobalMeta("NUMCLS", numcls);
      addGlobalMeta("OVQUAL", overallQuality);
      addGlobalMeta("EANGLE", equivalentAngle);
      addGlobalMeta("EXSHIFT", eqXShift);
      addGlobalMeta("EYSHIFT", eqYShift);
      addGlobalMeta("CMTOTVAR", cmToVar);
      addGlobalMeta("INFORMAT", informat);
      addGlobalMeta("NUMEIGEN", nEigenvalues);
      addGlobalMeta("NIACTIVE", nActiveImages);
      addGlobalMeta("RESOLX", physicalXSize);
      addGlobalMeta("RESOLY", physicalYSize);
      addGlobalMeta("RESOLZ", physicalZSize);
    }

    core[0].sizeZ = nImages;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = nImages;

    core[0].dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageName(imageName.trim(), 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (physicalXSize > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat(physicalXSize * 0.0001), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          physicalXSize * 0.0001);
      }
      if (physicalYSize > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat(physicalYSize * 0.0001), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          physicalYSize * 0.0001);
      }
      if (physicalZSize > 0) {
        store.setPixelsPhysicalSizeZ(
          new PositiveFloat(physicalZSize * 0.0001), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
          physicalZSize * 0.0001);
      }
    }
  }

}
