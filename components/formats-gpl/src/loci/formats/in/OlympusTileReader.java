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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * OlympusTileReader is the file format reader for Olympus .omp2info files.
 */
public class OlympusTileReader extends FormatReader {

  // -- Fields --

  private IFormatReader helperReader;
  private List<Tile> tiles = new ArrayList<Tile>();
  private String[] allPixelsFiles;
  private List<String> extraFiles = new ArrayList<String>();

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
    int pixel = getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType());

    Region imageRegion = new Region(x, y, w, h);
    for (Tile t : tiles) {
      if (t.region.intersects(imageRegion)) {
        helperReader.setId(t.file);

        Region intersection = t.region.intersection(imageRegion);
        byte[] src = helperReader.openBytes(no,
          intersection.x - t.region.x, intersection.y - t.region.y,
          intersection.width, intersection.height);
        for (int row=0; row<intersection.height; row++) {
          int srcIndex = row * intersection.width * pixel;
          int destIndex = pixel * ((intersection.y - y + row) * w + (intersection.x - x));
          System.arraycopy(src, srcIndex, buf, destIndex, intersection.width * pixel);
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      List<String> allFiles = new ArrayList<String>();
      allFiles.add(currentId);
      allFiles.addAll(extraFiles);
      return allFiles.toArray(new String[allFiles.size()]);
    }
    if (allPixelsFiles == null) {
      List<String> allFiles = new ArrayList<String>();
      allFiles.add(currentId);
      allFiles.addAll(extraFiles);
      for (Tile t : tiles) {
        for (String f : t.files) {
          allFiles.add(f);
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
      tiles.clear();
      allPixelsFiles = null;
      extraFiles.clear();
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

    tiles.sort(null);

    MetadataStore store = makeFilterMetadata();
    helperReader.setMetadataStore(store);
    helperReader.setId(tiles.get(0).file);

    core.clear();
    CoreMetadata ms = new CoreMetadata(helperReader.getCoreMetadataList().get(0));

    for (Tile t : tiles) {
      Region r = t.region;
      ms.sizeX = (int) Math.max(ms.sizeX, r.width + r.x);
      ms.sizeY = (int) Math.max(ms.sizeY, r.height + r.y);
    }
    core.add(ms);

    MetadataTools.populatePixels(store, this);
  }

  private Element getMetadataRoot(String xml) throws FormatException, IOException {
    try {
      return XMLTools.parseDOM(xml).getDocumentElement();
    }
    catch (ParserConfigurationException|SAXException e) {
      throw new FormatException(e);
    }
  }

  private Element getChildNode(Element root, String name) {
    return (Element) root.getElementsByTagName(name).item(0);
  }

  private String getChildValue(Element root, String name) {
    Element node = getChildNode(root, name);
    if (node == null) {
      return null;
    }
    return node.getTextContent();
  }

  private String getName(Node root) {
    String name = root.getNodeName();
    return name.substring(name.indexOf(":") + 1);
  }

  private void readMetadata(String xml) throws FormatException, IOException {
    Location parentDir = new Location(getCurrentFile()).getParentFile();

    // matl:properties
    Element root = (Element) getMetadataRoot(xml);

    Element tileGroup = getChildNode(root, "matl:group");
    Element regionInfo = getChildNode(tileGroup, "marker:regionInfo");
    Element coordinates = getChildNode(regionInfo, "marker:coordinates");

    // nanometers
    double stitchedWidth = DataTools.parseDouble(coordinates.getAttribute("width"));
    double stitchedHeight = DataTools.parseDouble(coordinates.getAttribute("height"));

    Element areaInfo = getChildNode(tileGroup, "matl:areaInfo");
    int rows = Integer.parseInt(getChildValue(areaInfo, "matl:numOfYAreas"));
    int cols = Integer.parseInt(getChildValue(areaInfo, "matl:numOfXAreas"));

    double physicalTileWidth = stitchedWidth / cols;
    double physicalTileHeight = stitchedHeight / rows;

    NodeList allTiles = tileGroup.getElementsByTagName("matl:area");
    int adjustWidth = 0;
    int adjustHeight = 0;

    for (int i=0; i<allTiles.getLength(); i++) {
      Tile currentTile = new Tile();
      Element tile = (Element) allTiles.item(i);
      String tileFile = getChildValue(tile, "matl:image");
      tileFile = new Location(parentDir, tileFile).getAbsolutePath();
      currentTile.file = tileFile;

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

        int widthWithOverlaps = helperReader.getSizeX() * cols;
        int heightWithOverlaps = helperReader.getSizeY() * rows;

        Length physicalSizeX = metadata.getPixelsPhysicalSizeX(0);
        Length physicalSizeY = metadata.getPixelsPhysicalSizeY(0);
        int actualWidth = (int) (stitchedWidth / physicalSizeX.value(UNITS.NM).doubleValue());
        int actualHeight = (int) (stitchedHeight / physicalSizeY.value(UNITS.NM).doubleValue());

        int diffX = widthWithOverlaps - actualWidth;
        int diffY = heightWithOverlaps - actualHeight;

        adjustWidth = helperReader.getSizeX();
        if (cols > 1) {
          adjustWidth -= (diffX / (cols -1));
        }
        adjustHeight = helperReader.getSizeY();
        if (rows > 1) {
          adjustHeight -= (diffY / (rows - 1));
        }
      }
      else {
        helperReader.setId(tileFile);
      }
      currentTile.files = helperReader.getUsedFiles();

      int xIndex = Integer.parseInt(getChildValue(tile, "matl:xIndex"));
      int yIndex = Integer.parseInt(getChildValue(tile, "matl:yIndex"));

      currentTile.region = new Region(xIndex * adjustWidth, yIndex * adjustHeight,
        helperReader.getSizeX(), helperReader.getSizeY());
      tiles.add(currentTile);
    }
    helperReader.close();

    Element stage = getChildNode(root, "matl:stage");
    if (stage != null) {
      parseOriginalMetadata(stage);
    }

    Element cycle = getChildNode(root, "matl:cycle");
    if (cycle != null) {
      parseOriginalMetadata(cycle);
    }

    Element map = getChildNode(root, "matl:map");
    if (map != null) {
      String mapFile = getChildValue(map, "matl:image");
      if (mapFile != null) {
        mapFile = new Location(parentDir, mapFile).getAbsolutePath();
        extraFiles.add(mapFile);
      }
    }
  }

  private void parseOriginalMetadata(Node root) {
    String value = root.getNodeValue();
    if (value != null && value.trim().length() > 0) {
      value = value.trim();
      String key = "";

      Node parent = root.getParentNode();
      if (parent != null) {
        key = getName(parent);
      }
      Node grandparent = parent.getParentNode();
      if (grandparent != null) {
        String name = getName(grandparent);
        key = name + " " + key;
      }
      addGlobalMeta(key, value);
    }
    else {
      NamedNodeMap attrs = root.getAttributes();
      if (attrs != null) {
        for (int i=0; i<attrs.getLength(); i++) {
          Attr attr = (Attr) attrs.item(i);
          addGlobalMeta(getName(root) + " " + attr.getName(), attr.getValue());
        }
      }

      NodeList children = root.getChildNodes();
      for (int i=0; i<children.getLength(); i++) {
        parseOriginalMetadata(children.item(i));
      }
    }
  }

  class Tile implements Comparable<Tile> {
    public String file;
    public String[] files;
    public Region region;

    @Override
    public int compareTo(Tile o) {
      if (region.equals(o.region)) {
        return 0;
      }
      int yDiff = region.y - o.region.y;
      if (yDiff != 0) {
        return yDiff;
      }
      return region.x - o.region.x;
    }

    @Override
    public String toString() {
      return file + ", region = " + region;
    }

  }

}
