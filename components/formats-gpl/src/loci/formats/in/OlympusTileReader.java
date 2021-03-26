/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2021 Open Microscopy Environment:
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * OlympusTileReader is the file format reader for Olympus .omp2info files.
 */
public class OlympusTileReader extends FormatReader {

  // -- Fields --

  private IFormatReader helperReader;
  private List<String> tileFiles = new ArrayList<String>();
  private List<Region> tileRegions = new ArrayList<Region>();
  private String[] allPixelsFiles;

  // -- Constructor --

  /** Constructs a new Olympus .omp2info reader. */
  public OlympusTileReader() {
    super("Olympus .omp2info", "omp2info");
    domains = new String[] {FormatTools.LM_DOMAIN};
    datasetDescription = "One .omp2info file and at least one .oir or .vsi file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return helperReader.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return helperReader.getOptimalTileHeight();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    Region imageRegion = new Region(x, y, w, h);
    for (int r=0; r<tileRegions.size(); r++) {
      if (tileRegions.get(r).intersects(imageRegion)) {
        helperReader.setId(tileFiles.get(r));
        // TODO - tile math
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      return new String[] {currentId};
    }
    if (allPixelsFiles == null) {
      List<String> allFiles = new ArrayList<String>();
      allFiles.add(currentId);
      for (String file : tileFiles) {
        try {
        helperReader.setId(file);
          for (String f : helperReader.getSeriesUsedFiles()) {
            allFiles.add(f);
          }
        }
        catch (FormatException|IOException e) {
          LOGGER.error("Could not read tile file " + file, e);
        }
      }
      allPixelsFiles = allFiles.toArray(new String[allFiles.size()]);
    }
    return allPixelsFiles;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (helperReader != null) {
      helperReader.close(fileOnly);
    }
    if (!fileOnly) {
      helperReader = null;
      tileFiles.clear();
      tileRegions.clear();
      allPixelsFiles = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    String xml = DataTools.readFile(currentId);
    xml = XMLTools.sanitizeXML(xml);
    readMetadata(xml);

    MetadataStore store = makeFilterMetadata();
    helperReader.setMetadataStore(store);
    helperReader.setId(tileFiles.get(0));

    core = new ArrayList<CoreMetadata>(helperReader.getCoreMetadataList());
    for (int i=0; i<core.size(); i++) {
      CoreMetadata ms = core.get(i);
      //ms.sizeX = handler.getImageWidth();
      //ms.sizeY = handler.getImageHeight();
    }

    MetadataTools.populatePixels(store, this);
  }

  private Element getMetadataRoot(String xml) throws FormatException, IOException {
    ByteArrayInputStream s = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      s = new ByteArrayInputStream(xml.getBytes(Constants.ENCODING));
      return parser.parse(s).getDocumentElement();
    }
    catch (ParserConfigurationException e) {
      throw new FormatException(e);
    }
    catch (SAXException e) {
      throw new FormatException(e);
    } finally {
        if (s != null) s.close();
    }
  }

  private Element getChildNode(Element root, String name) {
    return (Element) root.getElementsByTagName(name).item(0);
  }

  private void readMetadata(String xml) throws FormatException, IOException {
    // matl:properties
    Element root = (Element) getMetadataRoot(xml);

    Element tileGroup = getChildNode(root, "matl:group");
    Element regionInfo = getChildNode(tileGroup, "marker:regionInfo");
    Element coordinates = getChildNode(regionInfo, "marker:coordinates");

    // nanometers
    double stitchedWidth = DataTools.parseDouble(coordinates.getAttribute("width"));
    double stitchedHeight = DataTools.parseDouble(coordinates.getAttribute("height"));

    Element areaInfo = getChildNode(tileGroup, "matl:areaInfo");
    int rows = Integer.parseInt(getChildNode(areaInfo, "matl:numOfYAreas").getTextContent());
    int cols = Integer.parseInt(getChildNode(areaInfo, "matl:numOfXAreas").getTextContent());

    double physicalTileWidth = stitchedWidth / cols;
    double physicalTileHeight = stitchedHeight / rows;

    NodeList tiles = tileGroup.getElementsByTagName("matl:area");
    int adjustWidth = 0;
    int adjustHeight = 0;
    for (int i=0; i<tiles.getLength(); i++) {
      Element tile = (Element) tiles.item(i);
      String tileFile = getChildNode(tile, "matl:image").getTextContent();
      tileFiles.add(tileFile);

      if (helperReader == null) {
        if (checkSuffix(tileFile, "oir")) {
          helperReader = new OIRReader();
        }
        else if (checkSuffix(tileFile, "vsi")) {
          helperReader = new CellSensReader();
        }
        else {
          throw new FormatException("Unsupported tile file " + tileFile);
        }

        IMetadata metadata = MetadataTools.createOMEXMLMetadata();
        helperReader.setMetadataStore(metadata);
        helperReader.setId(tileFile);

        Length physicalSizeX = metadata.getPixelsPhysicalSizeX(0);
        Length physicalSizeY = metadata.getPixelsPhysicalSizeY(0);
        adjustWidth = (int) (physicalTileWidth / physicalSizeX.value(UNITS.NM).doubleValue());
        adjustHeight = (int) ( physicalTileHeight / physicalSizeY.value(UNITS.NM).doubleValue());
      }
      /* debug */ System.out.println("adjustWidth = " + adjustWidth);
      /* debug */ System.out.println("adjustHeight = " + adjustHeight);

      int xIndex = Integer.parseInt(getChildNode(tile, "matl:xIndex").getTextContent());
      int yIndex = Integer.parseInt(getChildNode(tile, "matl:yIndex").getTextContent());

      Region region = new Region(xIndex * adjustWidth, yIndex * adjustHeight,
        helperReader.getSizeX(), helperReader.getSizeY());
      tileRegions.add(region);
    }
    helperReader.close();

    Element stage = getChildNode(root, "matl:stage");
    Element cycle = getChildNode(root, "matl:cycle");
    Element map = getChildNode(root, "matl:map");
  }

}
