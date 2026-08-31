/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import loci.common.Location;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Reader for PreciPoint .vmic WSI files
 *
 * @author Kai Wiechen kai.wiechen at pathologie-worms.de
 *
 * contains a lot of code from:
 * https://github.com/usnistgov/pyramidio
 * DZI pyramid reader. Thread safe.
 * @author Antoine Vandecreme
 *
 * .vmic format information from:
 * https://github.com/openslide/openslide/issues/168
 * https://lists.andrew.cmu.edu/pipermail/openslide-users/2015-December/001164.html
 */

public class VmicReader extends SubResolutionFormatReader {
    private static final String INNER_CONTAINER = "Image.vmici";
    public static final String DZI_FILE = "dzc_output.xml";
    private static final String EXTENDED_METADATA = "VMCF/config.osc";
    public static final String DZI_FILES = "dzc_output_files";

    private File filesFolder;
    private int tileSize;
    private int overlap;
    private String format;
    private int width;
    private int height;
    private int maxLevel;

    private FileSystem inner_zipfs;
    private File innerZipFile;
    boolean unzippedOuterZip = false;

    // -- Constructor --

    public VmicReader() {
        super("vmic", new String[]{"vmic"});
        domains = new String[]{FormatTools.GRAPHICS_DOMAIN};
        suffixNecessary = true;
        suffixSufficient = true;
    }

    /* (non-Javadoc)
     * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
     */
    @Override
    public boolean isThisType(String name, boolean open) {
        boolean isThisType = super.isThisType(name, open);
        if (isThisType && open) {
            try (ZipFile outerZipFile = new ZipFile(name)) {
                ZipEntry innerZipEntry = outerZipFile.getEntry(INNER_CONTAINER);
                try (InputStream innerZip = outerZipFile.getInputStream(innerZipEntry)) {
                    // check inner zip file magic numbers 0x50 0x4B
                    byte[] bytes = new byte[2];
                    innerZip.read(bytes, 0, 2);
                    if (bytes[0] == 0x50 && bytes[1] == 0x4B) return true;
                    else return false;
                }
            } catch (IOException e) {
                LOGGER.debug("I/O exception during isThisType() evaluation.", e);
                return false;
            }
        }
        return isThisType;
    }

    /**
     * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
     */
    @Override
    public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
            throws FormatException, IOException {
        FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

        Rectangle rect = new Rectangle(x, y, w, h);
        BufferedImage image = readRegionOfLevel(rect, maxLevel - resolution);

        byte[] t = AWTImageTools.getBytes(image, false);
        System.arraycopy(t, 0, buf, 0, Math.min(t.length, buf.length));

        return buf;
    }

    /* @see loci.formats.IFormatReader#close(boolean) */
    @Override
    public void close(boolean fileOnly) throws IOException {
        super.close(fileOnly);

        if (inner_zipfs != null) {
            if (unzippedOuterZip) {
                Path tempFileToDelete = Paths.get(innerZipFile.getAbsolutePath());
                if (Files.exists(tempFileToDelete)) {
                    try {
                        Files.delete(tempFileToDelete);
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            inner_zipfs = null;
            innerZipFile = null;
        }
    }

    /* @see IFormatReader#getResolutionCount() */
    @Override
    public int getResolutionCount() {
        FormatTools.assertId(currentId, true, 1);
        return core.get(0, 0).resolutionCount;
    }

    /* @see IFormatReader#setResolution(int) */
    @Override
    public void setResolution(int no) {
        if (no < 0 || no >= getResolutionCount()) {
            throw new IllegalArgumentException("Invalid resolution: " + no);
        }

        if (!hasFlattenedResolutions()) {
            resolution = no;
        }
    }

    /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
    @Override
    public int getOptimalTileWidth() { return tileSize; }

    /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
    @Override
    public int getOptimalTileHeight() {
        return tileSize;
    }

    /* @see loci.formats.FormatReader#getThumbSizeX() */
    @Override
    public int getThumbSizeX() {
        return core.get(0, 0).thumbSizeX;
    }

    /* @see loci.formats.FormatReader#getThumbSizeY() */
    @Override
    public int getThumbSizeY() {
        return core.get(0, 0).thumbSizeY;
    }

    /* @see loci.formats.FormatReader#initFile(String) */
    @Override
    public void initFile(String id) throws FormatException, IOException {
        setFlattenedResolutions(false);
        super.initFile(id);

        filesFolder = new File(DZI_FILES);

        try (ZipFile outerZipFile = new ZipFile(Location.getMappedId(id))) {
            ZipEntry innerZipEntry = outerZipFile.getEntry(INNER_CONTAINER);

            // unzip outer container only if inner container > 2 GB
            if (innerZipEntry.getSize() > Integer.MAX_VALUE - 1) {
                innerZipFile = File.createTempFile("Image", ".vmici");
                innerZipFile.deleteOnExit();
                try (InputStream innerZip = outerZipFile.getInputStream(innerZipEntry)) {
                    OutputStream out = new FileOutputStream(innerZipFile);
                    byte[] buffer = new byte[131872]; //8192];
                    int len;
                    while ((len = innerZip.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                }
                Path inner_zip_path = Paths.get(innerZipFile.getPath());
                inner_zipfs = FileSystems.newFileSystem(inner_zip_path, (ClassLoader) null);
                unzippedOuterZip = true;
            }
            else {
                Path outer_zip = Paths.get(Location.getMappedId(id));
                try (FileSystem fs = FileSystems.newFileSystem(outer_zip, (ClassLoader) null)) {
                    Path inner_zip_path = fs.getPath(INNER_CONTAINER);
                    inner_zipfs = FileSystems.newFileSystem(inner_zip_path, (ClassLoader) null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        initBasicMetadata();
        initExtendedMetadata();
    }

    private void initBasicMetadata() throws IOException {
        try {
            Path entry = inner_zipfs.getPath(DZI_FILE);
            InputStream is = Files.newInputStream(entry);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(is);
            Element imageNode = doc.getDocumentElement();
            if (!"Image".equals(imageNode.getNodeName())) {
                throw new IOException("Unsupported dzi file.");
            }

            tileSize = Integer.parseInt(imageNode.getAttribute("TileSize"));
            overlap = Integer.parseInt(imageNode.getAttribute("Overlap"));
            format = imageNode.getAttribute("Format");

            NodeList childNodes = imageNode.getChildNodes();
            int length = childNodes.getLength();
            String w = null;
            String h = null;
            for (int i = 0; i < length; i++) {
                Node node = childNodes.item(i);
                if ("Size".equals(node.getNodeName())) {
                    NamedNodeMap attributes = node.getAttributes();
                    w = attributes.getNamedItem("Width").getNodeValue();
                    h = attributes.getNamedItem("Height").getNodeValue();
                }
            }
            width = Integer.parseInt(w);
            height = Integer.parseInt(h);

            int maxDim = Math.max(width, height);
            maxLevel = (int) Math.ceil(Math.log(maxDim) / Math.log(2));

            is.close();

        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException(ex);
        }

        CoreMetadata m0 = core.get(0, 0);

        m0.interleaved = false;
        m0.littleEndian = false;
        m0.sizeX = width;
        m0.sizeY = height;
        m0.sizeZ = 1;
        m0.sizeT = 1;
        m0.sizeC = 3;
        m0.rgb = getSizeC() > 1;
        m0.imageCount = 1;
        m0.pixelType = FormatTools.UINT8;
        m0.dimensionOrder = "XYCZT";
        m0.metadataComplete = true;
        m0.indexed = false;

        int maxResolutionLevels = maxLevel - 1;

        m0.resolutionCount = 1;

        // deepzoom pyramid subresolutions
        for (int i = maxResolutionLevels; i >= 0; i--) {
            CoreMetadata ms = new CoreMetadata(this, 0);
            core.add(0, ms);
            ms.sizeX = (int) Math.round(width * getZoomOfLevel(i));
            ms.sizeY = (int) Math.round(height * getZoomOfLevel(i));
            ms.sizeT = m0.sizeT;
            ms.imageCount = m0.imageCount;
            ms.thumbnail = true;
            ms.resolutionCount = 1;

            m0.resolutionCount += 1;

            List<File> list_of_files = getFilesOfLevel(i);
            if (list_of_files.size() == 1) {
                m0.thumbSizeX = ms.sizeX;
                m0.thumbSizeY = ms.sizeY;
                break;
            }
        }
    }

    private void initExtendedMetadata() throws IOException {
        MetadataStore store = makeFilterMetadata();
        MetadataTools.populatePixels(store, this);

        HashMap<String, String> metaDataMap = new HashMap<>();
        metaDataMap.put("ShortName", null);
        metaDataMap.put("Magnification", null);
        metaDataMap.put("PixelPerMicron", null);

        try {
            Path entry = inner_zipfs.getPath(EXTENDED_METADATA);
            InputStream is = Files.newInputStream(entry);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(is);
            Element imageNode = doc.getDocumentElement();
            if (!imageNode.getNodeName().contains("ObjectScanConfig")) {
                throw new IOException("Unsupported config.osc file.");
            }

            NodeList outerChildNodes = imageNode.getChildNodes();
            int outerLength = outerChildNodes.getLength();

            for (int i = 0; i < outerLength; i++) {
                Node outerNode = outerChildNodes.item(i);
                if (outerNode.getNodeName().contains("Objective") || outerNode.getNodeName().contains("CombinedOpticalConfig")) {
                    NodeList innerChildNodes = outerNode.getChildNodes();
                    int innerLength = innerChildNodes.getLength();

                    for (int j = 0; j < innerLength; j++) {
                        Node innerNode = innerChildNodes.item(j);

                        String nn = innerNode.getNodeName().replace("ObjectScanConfig:", "");
                        if (metaDataMap.containsKey(nn)) {
                            metaDataMap.put(nn, innerNode.getTextContent());
                        }
                    }
                }
            }

            Length pixelsize = FormatTools.createLength(1 / Double.parseDouble(metaDataMap.get("PixelPerMicron")), UNITS.MICROMETER);
            store.setPixelsPhysicalSizeX(pixelsize, 0);
            store.setPixelsPhysicalSizeY(pixelsize, 0);
            // IDs must not contain white spaces ??
            store.setInstrumentID("PreciPoint", 0);
            store.setObjectiveSettingsID(metaDataMap.get("ShortName"), 0);
            store.setObjectiveID(metaDataMap.get("ShortName"), 0, 0);
            store.setObjectiveNominalMagnification(Double.parseDouble(metaDataMap.get("Magnification")), 0, 0);

            is.close();

        } catch (IOException | ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private double getZoomOfLevel(int level) {
        return Math.pow(2, level - maxLevel);
    }

    private List<File> getFilesOfLevel(int level) {
        int widthOfLevel = 1;
        int heightOfLevel = 1;

        if (level != 0) {
            widthOfLevel = (int) Math.round(width * getZoomOfLevel(level)); //Math.min(2 * level, width);
            heightOfLevel = (int) Math.round(height * getZoomOfLevel(level)); //Math.min(2 * level, height);
        }

        int numColumns = (int) Math.ceil(widthOfLevel / (float) tileSize);
        int numRows = (int) Math.ceil(heightOfLevel / (float) tileSize);

        File levelFolder = new File(filesFolder, Integer.toString(level));
        ArrayList<File> result = new ArrayList<>(numColumns * numRows);
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                File file = new File(levelFolder, i + "_" + j + "." + format);
                result.add(file);
            }
        }
        return result;
    }

    private BufferedImage readRegionOfLevel(Rectangle region, int level)
            throws IOException {
        int firstTileColumn = region.x / tileSize;
        if (tileSize * (firstTileColumn + 1) - overlap <= region.x) {
            firstTileColumn++;
        }
        int firstTileRow = region.y / tileSize;
        if (tileSize * (firstTileRow + 1) - overlap <= region.y) {
            firstTileRow++;
        }
        int lastTileColumn = (region.x + region.width) / tileSize;
        if (tileSize * lastTileColumn + overlap >= region.x + region.width
                && lastTileColumn != 0) {
            lastTileColumn--;
        }
        int lastTileRow = (region.y + region.height) / tileSize;
        if (tileSize * lastTileRow + overlap >= region.y + region.height
                && lastTileRow != 0) {
            lastTileRow--;
        }

        BufferedImage result = null;
        WritableRaster raster = null;
        int dx = 0;
        for (int i = firstTileColumn; i <= lastTileColumn; i++) {
            int x;
            int w;
            if (i == firstTileColumn) {
                x = region.x - firstTileColumn * tileSize;
                if (i == lastTileColumn) {
                    w = region.width;
                } else {
                    w = tileSize - x;
                }
                if (firstTileColumn != 0) {
                    x += overlap;
                }
            } else {
                x = overlap;
                if (i == lastTileColumn) {
                    w = region.width - dx;
                } else {
                    w = tileSize;
                }
            }

            int dy = 0;
            for (int j = firstTileRow; j <= lastTileRow; j++) {
                int y;
                int h;
                if (j == firstTileRow) {
                    y = region.y - firstTileRow * tileSize;
                    if (j == lastTileRow) {
                        h = region.height;
                    } else {
                        h = tileSize - y;
                    }
                    if (firstTileRow != 0) {
                        y += overlap;
                    }
                } else {
                    y = overlap;
                    if (j == lastTileRow) {
                        h = region.height - dy;
                    } else {
                        h = tileSize;
                    }
                }

                Rectangle area = new Rectangle(x, y, w, h);
                BufferedImage tile = readRegionOfTile(area, level, i, j);
                if (i == firstTileColumn && j == firstTileRow) {
                    result = createBufferedImage(region.width, region.height, tile);
                    raster = result.getRaster();
                }
                raster.setRect(dx, dy, tile.getRaster());
                tile.flush();
                dy += h;
            }
            dx += w;
        }
        return result;
    }

    private BufferedImage readRegionOfTile(Rectangle region, int level,
                                           int column, int row) throws IOException {
        File levelFolder = new File(filesFolder, Integer.toString(level));
        File tile = new File(levelFolder, column + "_" + row + "." + format);

        String s = tile.getPath();
        Path entry = inner_zipfs.getPath(s);

        if (Files.exists(entry)) {
            InputStream is = Files.newInputStream(entry);
            try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
                ImageReader reader = getImageReader(iis);
                reader.setInput(iis);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(region);
                return reader.read(0, param);
            }

        } else {
            BufferedImage image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, tileSize, tileSize);
            g2d.dispose();
            return image;
        }
    }

    private static ImageReader getImageReader(ImageInputStream iis)
            throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (!readers.hasNext()) {
            throw new IOException("No compatible image reader found.");
        }
        return readers.next();
    }

    /**
     * Create a new buffered image with the same characteristics (color model,
     * raster type, properties...) than the specified one.
     *
     * @param width the width
     * @param height the height
     * @param image an image with the same characteristics than the one which
     * will be created.
     */
    private static BufferedImage createBufferedImage(int width, int height,
                                                     BufferedImage image) {
        Hashtable<String, Object> properties = null;
        String[] propertyNames = image.getPropertyNames();
        if (propertyNames != null) {
            properties = new Hashtable<>(propertyNames.length);
            for (String propertyName : propertyNames) {
                properties.put(propertyName, image.getProperty(propertyName));
            }
        }
        return new BufferedImage(
                image.getColorModel(),
                image.getRaster().createCompatibleWritableRaster(width, height),
                image.isAlphaPremultiplied(),
                properties);
    }
}
