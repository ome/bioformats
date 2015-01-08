/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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

import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.hdf5.HDF5CompoundDataMap;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.zip.Inflater;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.codec.Base64Codec;
import loci.formats.codec.BitWriter;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JHDFService;
import loci.formats.services.JHDFServiceImpl;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;

/**
 * Reader for CellH5 (HDF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a
 * href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/CellH5Reader.java">Trac</a>,
 * <a
 * href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/CellH5Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CellH5Reader extends FormatReader {

  // -- Constants --
    public static final String HDF_MAGIC_STRING = "HDF";

    private static final String[] DELIMITERS = {" ", "-", "."};

  // -- Fields --
    private double pixelSizeX, pixelSizeY, pixelSizeZ;
    private double minX, minY, minZ, maxX, maxY, maxZ;
    private int seriesCount;
    private JHDFService jhdf;

    private MetadataStore store;

    // channel parameters
    // private Vector<String> emWave, exWave, channelMin, channelMax;
    // private Vector<String> gain, pinhole, channelName, microscopyMode;
    private int lastChannel = 0;

    private List<String> pathToImageData = new LinkedList<String>();
    private List<String> pathToPosition = new LinkedList<String>();
    private List<String> seriesNames = new LinkedList<String>();
    
    private String pathToDefinition = "/definition";
    private String pathToObjectDefinition = pathToDefinition + "/object";
    private Vector<String> cellObjectNames = new Vector<String>();

    // Default colors 
    private int[][] COLORS = {{255, 0, 0}, {0, 255, 0}, {0, 0, 255},
                              {255, 255, 0}, {0, 255, 255}, {255, 0, 255},
                              {255, 255, 255}, {255, 0, 128}, {0, 255, 128},
                              {0, 128, 256}, {128, 0, 128}, {255, 128, 0},
                              {64, 128, 0}, {0, 64, 128}, {128, 0, 64}};

    private String[] rois;
    private HDF5CompoundDataMap[] times = null;
    private HDF5CompoundDataMap[] classes = null;
    private HDF5CompoundDataMap[] bbox = null;

  // -- Constructor --
    /**
     * Constructs a new CellH5 HDF reader.
     */
    public CellH5Reader() {
        super("CellH5 (HDF)", "ch5");
        suffixSufficient = true;
        domains = new String[]{FormatTools.HCS_DOMAIN};
    }

  // -- IFormatReader API methods --

    /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
    public int getOptimalTileHeight() {
        FormatTools.assertId(currentId, true, 1);
        return getSizeY();
    }

    public boolean isThisType(String name, boolean open) {
        String[] tokens = name.split("\\.(?=[^\\.]+$)");
        if (tokens[1].equals("ch5")) {
            return true;
        }
        return false;

    }

    /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
    public boolean isThisType(RandomAccessInputStream stream) throws IOException {
        final int blockLen = 8;
        if (!FormatTools.validStream(stream, blockLen, false)) {
            return false;
        }
        return stream.readString(blockLen).contains(HDF_MAGIC_STRING);
    }

    /* @see loci.formats.IFormatReader#get8BitLookupTable() */
    public byte[][] get8BitLookupTable() {
        FormatTools.assertId(currentId, true, 1);
        if (getPixelType() != FormatTools.UINT8 || !isIndexed()) {
            return null;
        }

        if (lastChannel < 0) {
            return null;
        }

        byte[][] lut = new byte[3][256];
        for (int i = 0; i < 256; i++) {
            switch (lastChannel) {
                case 0:
                    // red
                    lut[0][i] = (byte) (i & 0xff);
                    break;
                case 1:
                    // green
                    lut[1][i] = (byte) (i & 0xff);
                    break;
                case 2:
                    // blue
                    lut[2][i] = (byte) (i & 0xff);
                    break;
                case 3:
                    // cyan
                    lut[1][i] = (byte) (i & 0xff);
                    lut[2][i] = (byte) (i & 0xff);
                    break;
                case 4:
                    // magenta
                    lut[0][i] = (byte) (i & 0xff);
                    lut[2][i] = (byte) (i & 0xff);
                    break;
                case 5:
                    // yellow
                    lut[0][i] = (byte) (i & 0xff);
                    lut[1][i] = (byte) (i & 0xff);
                    break;
                default:
                    // gray
                    lut[0][i] = (byte) (i & 0xff);
                    lut[1][i] = (byte) (i & 0xff);
                    lut[2][i] = (byte) (i & 0xff);
            }
        }
        return lut;
    }

    /* @see loci.formats.IFormatReaderget16BitLookupTable() */
    public short[][] get16BitLookupTable() {
        FormatTools.assertId(currentId, true, 1);
        if (getPixelType() != FormatTools.UINT16 || !isIndexed()) {
            return null;
        }

        if (lastChannel < 0 || lastChannel >= 9) {
            return null;
        }

        short[][] lut = new short[3][65536];
        for (int i = 0; i < 65536; i++) {
            switch (lastChannel) {
                case 0:
                    // red
                    lut[0][i] = (short) (i & 0xff);
                    break;
                case 1:
                    // green
                    lut[1][i] = (short) (i & 0xff);
                    break;
                case 2:
                    // blue
                    lut[2][i] = (short) (i & 0xff);
                    break;
                case 3:
                    // cyan
                    lut[1][i] = (short) (i & 0xff);
                    lut[2][i] = (short) (i & 0xff);
                    break;
                case 4:
                    // magenta
                    lut[0][i] = (short) (i & 0xff);
                    lut[2][i] = (short) (i & 0xff);
                    break;
                case 5:
                    // yellow
                    lut[0][i] = (short) (i & 0xff);
                    lut[1][i] = (short) (i & 0xff);
                    break;
                default:
                    // gray
                    lut[0][i] = (short) (i & 0xff);
                    lut[1][i] = (short) (i & 0xff);
                    lut[2][i] = (short) (i & 0xff);
            }
        }
        return lut;
    }

    /**
     * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int,
     * int)
     */
    public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
            throws FormatException, IOException {
        FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
        lastChannel = getZCTCoords(no)[1];

        // pixel data is stored in XYZ blocks
        Object image = getImageData(no, y, h);

        boolean big = !isLittleEndian();
        
        // images is of type byte[][]. Left these checks and unpacking 
        // in the code for feature data types
        int bpp = FormatTools.getBytesPerPixel(getPixelType());
        for (int row = 0; row < h; row++) {
            int base = row * w * bpp;
            if (image instanceof byte[][]) {
                byte[][] data = (byte[][]) image;
                byte[] rowData = data[row];
                System.arraycopy(rowData, x, buf, row * w, w);
            } else if (image instanceof short[][]) {
                short[][] data = (short[][]) image;
                short[] rowData = data[row];
                for (int i = 0; i < w; i++) {
                    DataTools.unpackBytes(rowData[i + x], buf, base + 2 * i, 2, big);
                }
            } else if (image instanceof int[][]) {
                int[][] data = (int[][]) image;
                int[] rowData = data[row];
                for (int i = 0; i < w; i++) {
                    DataTools.unpackBytes(rowData[i + x], buf, base + i * 4, 4, big);
                }
            } else if (image instanceof float[][]) {
                float[][] data = (float[][]) image;
                float[] rowData = data[row];
                for (int i = 0; i < w; i++) {
                    int v = Float.floatToIntBits(rowData[i + x]);
                    DataTools.unpackBytes(v, buf, base + i * 4, 4, big);
                }
            } else if (image instanceof double[][]) {
                double[][] data = (double[][]) image;
                double[] rowData = data[row];
                for (int i = 0; i < w; i++) {
                    long v = Double.doubleToLongBits(rowData[i + x]);
                    DataTools.unpackBytes(v, buf, base + i * 8, 8, big);
                }
            }
        }
        return buf;
    }

    /* @see loci.formats.IFormatReader#close(boolean) */
    public void close(boolean fileOnly) throws IOException {
        super.close(fileOnly);
        if (!fileOnly) {
            seriesCount = 0;
            pixelSizeX = pixelSizeY = pixelSizeZ = 0;
            
            if (jhdf != null) {
                jhdf.close();
            }
            jhdf = null;
            lastChannel = 0;
        }
    }

    private static JProgressBar initProgressBar(String title, String text) {
        JFrame parentFrame = new JFrame();
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setVisible(false);

        final JDialog dlg = new JDialog(parentFrame, title, true);
        JProgressBar dpb = new JProgressBar(0, 100);
        dlg.add(BorderLayout.CENTER, dpb);
        dlg.add(BorderLayout.NORTH, new JLabel(text));
        dpb.setStringPainted(true);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setSize(300, 75);
        dlg.setLocationRelativeTo(parentFrame);

        Thread t = new Thread(new Runnable() {
            public void run() {
                dlg.setVisible(true);
            }
        });
        t.start();
        return dpb;
    }

  // -- Internal FormatReader API methods --
    protected void initFile(String id) throws FormatException, IOException {
        super.initFile(id);

        try {
            ServiceFactory factory = new ServiceFactory();
            jhdf = factory.getInstance(JHDFService.class);
            jhdf.setFile(id);
        } catch (DependencyException e) {
            throw new MissingLibraryException(JHDFServiceImpl.NO_JHDF_MSG, e);
        }

        parseStructure();
        // The ImageJ RoiManager can not distinguish ROIs from different
        // Series. This is why they only will be loaded if the CellH5 contains
        // a single image / series
        if (seriesCount == 1) {
            parseROIs();
        }
    }

  // -- Helper methods --
    private byte[][] getImageData(int no, int y, int height)
            throws FormatException {
        int[] zct = getZCTCoords(no);
        int zslice = zct[0];
        int channel = zct[1];
        int time = zct[2];
        int width = getSizeX();
                
        MDIntArray test = jhdf.readIntBlockArray(pathToImageData.get(series), new int[]{channel, time, zslice, 0, 0},
                new int[]{1, 1, 1, height, width});
        byte[][] image = new byte[height][width];
        
        // Slice x, y dimension
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                image[yy][xx] = (byte) test.get(0, 0, 0, yy, xx);
            }
        }
        return image;
    }

    private void parseStructure() throws FormatException {
        seriesCount = 0;
        pixelSizeX = pixelSizeY = pixelSizeZ = 1;
        // read experiment structure

        String path = "/sample/";
        String sample = jhdf.getMember(path).get(0);
        path += sample + "/plate/";
        String plate = jhdf.getMember(path).get(0);
        path += plate + "/experiment/";
        
        
        String currentPathToPosition;
        for (String well : jhdf.getMember(path)) {
            for (String pos : jhdf.getMember(path + well + "/position/")) {
                currentPathToPosition = path + well + "/position/" + pos;
                pathToPosition.add(currentPathToPosition);
                pathToImageData.add(currentPathToPosition + "/image/channel");
                seriesNames.add(String.format("%s, %s_%s", plate, well, pos));
                seriesCount++;
                LOGGER.debug(String.format("Found sample '%s', plate: '%s', well: '%s' and position: '%s' in path: '%s'", sample, plate, well, pos, currentPathToPosition));
            }
        }
        
        if (seriesCount > 1) {
            for (int i=1; i<seriesCount; i++) {
                core.add(new CoreMetadata());
            }
        }

        for (int k=0; k < pathToImageData.size(); k++) {
            int[] ctzyx = jhdf.getShape(pathToImageData.get(k));
            core.get(k).sizeC = ctzyx[0];
            core.get(k).sizeT = ctzyx[1];
            core.get(k).sizeZ = ctzyx[2];
            core.get(k).sizeY = ctzyx[3];
            core.get(k).sizeX = ctzyx[4];
            core.get(k).resolutionCount = 1;
            core.get(k).thumbnail = false;
            core.get(k).imageCount = getSizeC() * getSizeT() * getSizeZ();
            core.get(k).dimensionOrder = "XYZCT";
            core.get(k).rgb = false;
            core.get(k).thumbSizeX = 128;
            core.get(k).thumbSizeY = 128;
            core.get(k).orderCertain = false;
            core.get(k).littleEndian = true;
            core.get(k).interleaved = false;
            core.get(k).indexed = true;
            core.get(k).pixelType = FormatTools.UINT8;
        }

        parseCellObjects();
        
        store = makeFilterMetadata();
        MetadataTools.populatePixels(store, this);
        
        for (int s=0; s<seriesCount; s++ ) {
            store.setImageName(seriesNames.get(s), s);
        }
        
        setSeries(0);    
    }

    private void parseCellObjects() {
        List<String> allObjects = jhdf.getMember(pathToObjectDefinition);
        for (String objectName : allObjects) {
            String objectType = (String) jhdf.readCompoundArrayDataMap(pathToObjectDefinition + "/" + objectName)[0].get("type");
            if (objectType.equals("region")) {
                cellObjectNames.add(objectName.toString());
                LOGGER.debug(String.format("CellH5Reader: Found cell object %s", objectName));
            }
        }
    }

    private int getChannelIndexOfCellObjectName(String cellObjectName) {
        HDF5CompoundDataMap[] allImageRegions = jhdf.readCompoundArrayDataMap(pathToDefinition + "/image/region");
        for (int regionIdx = 0; regionIdx < allImageRegions.length; regionIdx++) {
            String regionName = (String) allImageRegions[regionIdx].get("region_name");
            Integer channelIdx = (Integer) allImageRegions[regionIdx].get("channel_idx");
            if (regionName.endsWith(cellObjectName)) {
                return channelIdx.intValue();
            }
        }
        return -1;
    }

    private static int[] hex2Rgb(String colorStr) {
        return new int[]{
            Integer.valueOf(colorStr.substring(1, 3), 16),
            Integer.valueOf(colorStr.substring(3, 5), 16),
            Integer.valueOf(colorStr.substring(5, 7), 16)};
    }
    
    private String parseCrackContour(String input) {
        byte[] zipaa = null;
        String polygonStr = "";
                
        try {
            zipaa = new Base64Codec().decompress(input.getBytes());
        } catch (FormatException e) {
            LOGGER.info(String.format("Error in BASE64 %s", e.toString()));
        }

        try {
            Inflater decompresser = new Inflater();
            decompresser.setInput(zipaa);
            byte[] result = new byte[1048576];
            int resultLength = decompresser.inflate(result);
            decompresser.end();
            String outputString = new String(result, 0, resultLength, "UTF-8");

            String[] parts = outputString.split(",");
            polygonStr = "";
            for (int j = 0; j < parts.length - 2; j += 2) {
                polygonStr += parts[j] + "," + parts[j + 1] + " ";
            }
        } catch (Exception e) {
            LOGGER.info(String.format("Error decompressing %s", e.toString()));
        }
        return polygonStr;
    }

    private void parseROIs() {
        int shapeIndex = 0;
        int seriesOffset = 0;
        int objectIdx = 0;
        
        Vector<int[]> classColors = new Vector<int[]>();
        for (int s=0;s<seriesCount;s++) {
            int roiIndexOffset = 0;
            for (String cellObjectName : cellObjectNames) {
                LOGGER.info(String.format("Parse segmentation ROIs for cell object %s : %d", cellObjectName, objectIdx));
                String pathToBoundingBox = pathToPosition.get(s) + "/feature/" + cellObjectName + "/bounding_box";
                String pathToClassDefinition = pathToDefinition + "/feature/" + cellObjectName + "/object_classification/class_labels";
                boolean hasClassification = false;
                if (jhdf.exists(pathToClassDefinition)) {
                    String classColorHexString;
                    HDF5CompoundDataMap[] classDef = jhdf.readCompoundArrayDataMap(pathToClassDefinition);
                    for (int cls = 0; cls < classDef.length; cls++) {
                        classColorHexString = (String) classDef[cls].get("color");
                        classColors.add(hex2Rgb(classColorHexString));
                    }
                    if (classDef.length > 0) {
                        hasClassification = true;
                        classes = jhdf.readCompoundArrayDataMap(pathToPosition.get(s) + "/feature/" + cellObjectName + "/object_classification/prediction");
                    }
                }

                if (jhdf.exists(pathToBoundingBox)){
                    bbox = jhdf.readCompoundArrayDataMap(pathToBoundingBox);
                    times = jhdf.readCompoundArrayDataMap(pathToPosition.get(s) + "/object/" + cellObjectName);
                    int roiChannel = getChannelIndexOfCellObjectName(cellObjectName);
                    int roiZSlice = (Integer) 0;

                    for (int roiIndex = 0; roiIndex < bbox.length; roiIndex++) {
                        int roiManagerRoiIndex = roiIndex + roiIndexOffset + seriesOffset;
                        int roiTime = (Integer) times[roiIndex].get("time_idx");
                        int objectLabelId = (Integer) times[roiIndex].get("obj_label_id");

                        int left = (Integer) bbox[roiIndex].get("left");
                        int right = (Integer) bbox[roiIndex].get("right");
                        int top = (Integer) bbox[roiIndex].get("top");
                        int bottom = (Integer) bbox[roiIndex].get("bottom");
                        int width = right - left;
                        int height = bottom - top;

                        String roiID = MetadataTools.createLSID("ROI", roiManagerRoiIndex);
                        store.setROIID(roiID, roiManagerRoiIndex);
                        store.setImageROIRef(roiID, s, roiManagerRoiIndex);

                        store.setRectangleX((double) left , roiManagerRoiIndex, 0);
                        store.setRectangleY((double) top , roiManagerRoiIndex, 0);
                        store.setRectangleWidth((double) width , roiManagerRoiIndex, 0);
                        store.setRectangleHeight((double) height , roiManagerRoiIndex, 0);

                        store.setRectangleTheT(new NonNegativeInteger(roiTime + 1), roiManagerRoiIndex, 0);
                        store.setRectangleTheC(new NonNegativeInteger(roiChannel + 1), roiManagerRoiIndex, 0);
                        store.setRectangleTheZ(new NonNegativeInteger(roiZSlice + 1), roiManagerRoiIndex, 0);
                        store.setROIName(cellObjectName + " " + String.valueOf(objectLabelId), roiManagerRoiIndex);
                        store.setRectangleID(cellObjectName + "::" + String.valueOf(objectLabelId), roiManagerRoiIndex, 0);
                        Color strokeColor;
                        if (hasClassification) {
                            int classLabelIDx = (Integer) classes[roiIndex].get("label_idx");
                            int[] rgb = classColors.get(classLabelIDx);
                            strokeColor = new Color(rgb[0], rgb[1], rgb[2], 0xff);
                        } else {
                            strokeColor = new Color(COLORS[objectIdx][0], COLORS[objectIdx][1], COLORS[objectIdx][2], 0xff);
                        }
                        store.setRectangleStrokeColor(strokeColor, roiManagerRoiIndex, 0);
                        store.setRectangleStrokeWidth(1.0, roiIndex, 0);
                        store.setRectangleLocked(true, roiManagerRoiIndex, 0);
                        //store.setLabelText(String.valueOf(objectLabelId), roiManagerRoiIndex, shapeIndex);
                        //store.setRectangleText(cellObjectName, roiManagerRoiIndex, 0);
                    }

                    objectIdx++;
                    roiIndexOffset += bbox.length;
                }
                else {
                    LOGGER.info("No Segmentation data found...");
                    break;
                }
            }
            seriesOffset += roiIndexOffset;
        }
        
    }

}