/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataConverter;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.TiffData;

/**
 * PyramidTiffReader is the file format reader for pyramid TIFFs.
 */
public class PyramidTiffReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(PyramidTiffReader.class);

  // -- Fields --

  private IMetadata omexml = null;;

  // -- Constructor --

  /** Constructs a new pyramid TIFF reader. */
  public PyramidTiffReader() {
    super("Pyramid TIFF", new String[] {"tif", "tiff"});
    domains = new String[] {FormatTools.EM_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;
    equalStrips = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setAssumeEqualStrips(equalStrips);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    if (software == null) return false;
    return software.indexOf("Faas") >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#close(boolean)
   */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      omexml = null;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    int index = getCoreIndex() * getImageCount() + no;
    tiffParser.setAssumeEqualStrips(equalStrips);
    tiffParser.getSamples(ifds.get(index), buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(getCoreIndex() * getImageCount()).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(getCoreIndex() * getImageCount()).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.in.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    // count number of consecutive planes with the same XY dimensions

    int nPlanes = 1;
    long baseWidth = ifds.get(0).getImageWidth();
    long baseHeight = ifds.get(0).getImageLength();
    for (int i=1; i<ifds.size(); i++) {
      long width = ifds.get(i).getImageWidth();
      long height = ifds.get(i).getImageLength();
      if (width == baseWidth && height == baseHeight) {
        nPlanes++;
      }
      else {
        break;
      }
    }

    int seriesCount = ifds.size() / nPlanes;

    String comment = ifds.get(0).getComment();
    if (comment != null && comment.length() > 0 && comment.trim().startsWith("<")) {
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        omexml = service.createOMEXMLMetadata(comment);
      }
      catch (DependencyException de) {
       throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
      }
      catch (ServiceException e) {
        LOGGER.debug("Could not parse comment as OME-XML", e);
      }
    }

    // repopulate core metadata
    core.clear();
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      if (s == 0) {
        ms.resolutionCount = seriesCount;
      }

      IFD ifd = ifds.get(s * nPlanes);

      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      ms.rgb = samples > 1 || p == PhotoInterp.RGB;

      long numTileRows = ifd.getTilesPerColumn() - 1;
      long numTileCols = ifd.getTilesPerRow() - 1;

      ms.sizeX = (int) ifd.getImageWidth();
      ms.sizeY = (int) ifd.getImageLength();
      if (omexml != null) {
        ms.sizeZ = omexml.getPixelsSizeZ(0).getValue();
        ms.sizeT = omexml.getPixelsSizeT(0).getValue();
        ms.sizeC = omexml.getPixelsSizeC(0).getValue();
        ms.dimensionOrder = omexml.getPixelsDimensionOrder(0).getValue();
      }
      else {
        ms.sizeZ = 1;
        ms.sizeT = 1;
        ms.sizeC = ms.rgb ? samples : 1;
        // assuming all planes are channels
        ms.sizeC *= nPlanes;
        ms.dimensionOrder = "XYCZT";
      }
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      ms.imageCount = nPlanes;
      ms.pixelType = ifd.getPixelType();
      ms.metadataComplete = true;
      ms.interleaved = false;
      ms.falseColor = false;
      ms.thumbnail = s > 0;
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    boolean setImageNames = true;
    if (omexml == null) {
      super.initMetadataStore();
    }
    else {
      OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omexml.getRoot();
      List<Image> images = root.copyImageList();
      for (int i=0; i<images.size(); i++) {
        Image img = images.get(i);
        if (i > 0 && !hasFlattenedResolutions()) {
          root.removeImage(img);
          continue;
        }
        Pixels pix = img.getPixels();
        List<TiffData> tiffData = pix.copyTiffDataList();
        for (TiffData t : tiffData) {
          pix.removeTiffData(t);
        }
        if (img.getName() != null) {
          setImageNames = false;
        }
      }
      omexml.setRoot(root);

      MetadataConverter.convertMetadata(omexml, makeFilterMetadata());
    }

    if (setImageNames) {
     MetadataStore store = makeFilterMetadata();

      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageName("Series " + (i + 1), i);
      }
    }
  }

}
