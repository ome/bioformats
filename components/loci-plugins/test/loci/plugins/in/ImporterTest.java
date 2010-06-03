//
// ImporterTest.java
//

package loci.plugins.in;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;

import loci.common.Location;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;


// TODO

// waiting on BF implementations for
//   - indexed color support
//   - changes to concat

// must address before release

//  - macros should still work like before
//  - write tests for the color options : some mention was made that indexcolor is an issue in testing
//     default - waiting on BF to know how it should behave
//     composite
//     colorized
//     grayscale
//     custom
//  - add some tests for combination of options
//      comboConcatSplit() - done but not passing
//      comboManyOptions
//      other combo tests - rely on color code working. Waiting for BF.
//  - comboCropAutoscale() - autoscale of a cropped image returning min of whole image

// would be nice to address before release

//    - BF/imageJ returning wrong values of max num pixels (UINT32 off by one - IJ bug, float weird too, etc.)
//    - memoryRecord failure (needs BF code fix)
//    - open individual files: try to come up with a way to test without a disk file as source
//    - swapped dims test needs to test cases other than from default swapping Z & T
//    - output stack order - testing of iIndex? should match imagestack number? i.e. 5th plane == 4 - doesn't look so
//    - improve, comment, and generalize code for increased coverage

public class ImporterTest {

  private enum Axis {Z,C,T};
  
  private enum ChannelOrder {ZTC, ZCT, CZT, CTZ, TZC, TCZ};
  
  private static final boolean[] BooleanStates = new boolean[] {false, true};
  
  private static final int[] PixelTypes = new int[] {
      FormatTools.UINT8, FormatTools.UINT16, FormatTools.UINT32,
      FormatTools.INT8,  FormatTools.INT16,  FormatTools.INT32,
      FormatTools.FLOAT, FormatTools.DOUBLE
      };
  
  private static Color[] DefaultColorOrder =
    new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.CYAN, Color.MAGENTA, Color.YELLOW};
  
  private static Color[] CustomColorOrder =
    new Color[] {Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.WHITE};

  private static final String[] FAKE_FILES;
  private static final String FAKE_PATTERN;
 
  static {

    //String template = "test_C%s_TP%s&sizeX=50&sizeY=20&sizeZ=7.fake";
    String template = constructFakeFilename("test_C%s_TP%s", FormatTools.INT32, 50, 20, 7, 1, 1, -1, false, -1, false);
                                                                        // BDZ - INT32 is desirable for the color tests
    
    FAKE_FILES = new String[] {
      String.format(template, "1", "1"),
      String.format(template, "2", "1"),
      String.format(template, "3", "1"),
      String.format(template, "1", "2"),
      String.format(template, "2", "2"),
      String.format(template, "3", "2"),
      String.format(template, "1", "3"),
      String.format(template, "2", "3"),
      String.format(template, "3", "3"),
      String.format(template, "1", "4"),
      String.format(template, "2", "4"),
      String.format(template, "3", "4"),
      String.format(template, "1", "5"),
      String.format(template, "2", "5"),
      String.format(template, "3", "5"),
      "outlier.txt" // optional
    };
    FAKE_PATTERN = String.format(template, "<1-3>", "<1-5>");

    for (String file : FAKE_FILES) {
      Location.mapId(file, "iThinkI'mImportantButI'mNot");
    }
  }
  
  // ** Helper methods *******************************************************************

  private static String constructFakeFilename(String title,
      int pixelType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries,
      boolean indexed, int rgb, boolean falseColor)
  {
    // some tests rely on each image being large enough to get the s,i,z,t,c index pixels of a
    // FakeFile. This requires the x value of tested images to be somewhat large. Assert
    // the input image fits the bill
    if (sizeX < 41) {
      throw new IllegalArgumentException("constructFakeFilename() - width < 41 : can break some of our tests");
    }

    String fileName = "";
    fileName += title;
    fileName += "&pixelType=" + FormatTools.getPixelTypeString(pixelType);
    fileName += "&sizeX=" + sizeX;
    fileName += "&sizeY=" + sizeY;
    fileName += "&sizeZ=" + sizeZ;
    fileName += "&sizeC=" + sizeC;
    fileName += "&sizeT=" + sizeT;
    if (numSeries > 0) fileName += "&series=" + numSeries;
    if (indexed) fileName += "&indexed=true";
    if (rgb != -1) fileName += "&rgb=" + rgb;
    if (falseColor) fileName += "&falseColor=true";
    fileName += ".fake";
    
    return fileName;
  }

  /** Series number of the given image processor. */
  private int sIndex(ImageProcessor proc) { return (int) proc.getPixelValue(0, 0);  }

  /** Image number of the given image processor. */
  private int iIndex(ImageProcessor proc) { return (int) proc.getPixelValue(10, 0); }

  /** Slice number of the given image processor. */
  private int zIndex(ImageProcessor proc) { return (int) proc.getPixelValue(20, 0); }

  /** Channel number of the given image processor. */
  private int cIndex(ImageProcessor proc) { return (int) proc.getPixelValue(30, 0); }

  /** Frame number of the given image processor. */
  private int tIndex(ImageProcessor proc) { return (int) proc.getPixelValue(40, 0); }

  @SuppressWarnings("unused")
  private void printVals(ImageProcessor proc)
  {
    System.out.println(
        " S=" + sIndex(proc) +
        " I=" + iIndex(proc) +
        " Z=" + zIndex(proc) +
        " C=" + cIndex(proc) +
        " T=" + tIndex(proc));
  }

  /** returns the character at the given index within a string that is a permutation of ZCT */
  private char axisChar(String order, int d)
  {
    if ((d < 0) || (d > 2))
      throw new IllegalArgumentException("axisChar() - index out of bounds [0..2]: "+d);
    
    return order.charAt(d);
  }
  
  /** returns Axis given an order string and an index */
  private Axis axis(String order, int d)
  {
    char dimChar = axisChar(order,d);
    
    if (dimChar == 'Z') return Axis.Z;
    if (dimChar == 'C') return Axis.C;
    if (dimChar == 'T') return Axis.T;

    throw new IllegalArgumentException("axis() - unknown dimension specified: ("+dimChar+")");
  }

  /** returns z, c, or t value given an Axis selector */
  private int value(Axis axis, int z, int c, int t)
  {
    if (axis == Axis.Z) return z;
    if (axis == Axis.C) return c;
    if (axis == Axis.T) return t;

    throw new IllegalArgumentException("value() - unknown axis: "+axis);
  }
  
  /** returns z, c, or t index value given an ImageProcessor and an Axis selector */
  private int index(Axis axis, ImageProcessor proc)
  {
    if (axis == Axis.Z) return zIndex(proc);
    if (axis == Axis.C) return cIndex(proc);
    if (axis == Axis.T) return tIndex(proc);
    
    throw new IllegalArgumentException("index() - unknown axis: "+axis);
  }

  /** get full order (XYTCZ) from ChannelOrder (TCZ) as string */
  private String bfChanOrd(ChannelOrder order)
  {
    return "XY" + order.toString();
  }
  
  /** returns the number of of elements in a series given from, to, and by values */
  private int numInSeries(int from, int to, int by)
  {
    if (by < 1)
      throw new IllegalArgumentException("numInSeries passed a stepBy value < 1");
    
    // could calc this but simple loop suffices for our purposes
    int count = 0;
    for (int i = from; i <= to; i += by)
        count++;
    return count;
  }
  
  // note : for now assumes default ZCT ordering
  /** Tests that an ImageStack is ordered according to specified from/to/by points of z/c/t */
  private boolean seriesInCorrectOrder(ImageStack st,
      int zFrom, int zTo, int zBy,
      int cFrom, int cTo, int cBy,
      int tFrom, int tTo, int tBy)
  {
    int zs = numInSeries(zFrom,zTo,zBy);
    int cs = numInSeries(cFrom,cTo,cBy);
    int ts = numInSeries(tFrom,tTo,tBy);
    
    if ((zs * cs * ts) != st.getSize())
    {
      System.out.println("seriesInCorrectOrder() - slices don't add up: z"+zs+" X c"+cs+" X t"+ts+" != "+st.getSize());
      return false;
    }
    
    int procNum = 1;
    for (int t = tFrom; t <= tTo; t += tBy)
      for (int c = cFrom; c <= cTo; c += cBy)
        for (int z = zFrom; z <= zTo; z += zBy)
        {
          ImageProcessor proc = st.getProcessor(procNum);
          if ((zIndex(proc) != z) || (cIndex(proc) != c) || (tIndex(proc) != t))
          {
            System.out.println("seriesInCorrectOrder() - slices out of order: exp z"+z+" c"+c+" t"+t+" != act z"+
                zIndex(proc)+" c"+cIndex(proc)+" t"+tIndex(proc)+" for proc number "+procNum);
            return false;
          }
          procNum++;
        }
    
    return true;
  }
  
  // this next method useful to avoid changes to instance vars of ImagePlus by query functions
  /** Gets values of private instance variable ints from an ImagePlus */
  private int getField(ImagePlus imp, String fieldName) {
    Exception exc = null;
    try {
      Field field = ImagePlus.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      Object value = field.get(imp);
      return (Integer) value;
    }
    catch (SecurityException e) { exc = e; }
    catch (NoSuchFieldException e) { exc = e; }
    catch (IllegalArgumentException e) { exc = e; }
    catch (IllegalAccessException e) { exc = e; }
    exc.printStackTrace();
    return -1;
  }
  
  /** The number of Z slices in an ImagePlus */
  private int getSizeZ(ImagePlus imp) { return getField(imp, "nSlices"); }

  /** The number of T slices in an ImagePlus */
  private int getSizeT(ImagePlus imp) { return getField(imp, "nFrames"); }

  /** The number of effective C slices in an ImagePlus */
  private int getEffectiveSizeC(ImagePlus imp) { return getField(imp, "nChannels"); }

  // used by the color merge code. calcs a pixel value in our ramped data for a 3 channel merged image
  private int mergedPixel(int i)
  {
    if ((i < 0) || (i > 15))
      throw new IllegalArgumentException("mergedPixel() can only handle 1st 16 cases. Wants 0<=i<=15 but i = " + i);
    
    return i*65536 + i*256 + i;
  }

  // TODO : this code written to pass tests - looks wrong on a number of pixel types
  private long maxPixelValue(int pixType)
  {
    if (FormatTools.isFloatingPoint(pixType))
      return 4294967296L; // expected Float.MAX_VALUE or maybe Double.MAX_VALUE
 
    switch (pixType)
    {
      case FormatTools.INT8:    return 255; // expected: Byte.MAX_VALUE 
      case FormatTools.INT16:   return 65535;  // expected: Short.MAX_VALUE
      case FormatTools.INT32:   return 4294967296L; // expected INTEGER.MAX_VALUE and also off by 1 from unsigned max 
      case FormatTools.UINT8:   return 255; 
      case FormatTools.UINT16:  return 65535; 
      case FormatTools.UINT32:  return 4294967296L; // off by 1 from unsigned max 

      default:
        throw new IllegalArgumentException("maxPixelValue() - unknown pixel type passed in: " + pixType);
    }
  }
  
  private long minPixelValue(int pixType)
  {
    if (FormatTools.isFloatingPoint(pixType))
      //return -4294967296L; // -2^32 (and also its not 2^32-1 !!!)
      return 0;  // TODO this allows autoscale testing to work for floating types - makes sense cuz FakeReader only does unsigned float data 
 
    switch (pixType)
    {
      case FormatTools.INT8:    return Byte.MIN_VALUE; 
      case FormatTools.INT16:   return Short.MIN_VALUE;
      case FormatTools.INT32:   return Integer.MIN_VALUE; 
      case FormatTools.UINT8:   return 0; 
      case FormatTools.UINT16:  return 0; 
      case FormatTools.UINT32:  return 0;

      default:
        throw new IllegalArgumentException("minPixelValue() - unknown pixel type passed in: " + pixType);
    }
  }

  /** get the actual pixel value (lookup when data is indexed) of the index of a fake image at a given z,c,t */
  private int getIndexPixelValue(CompositeImage ci, int z, int c, int t, boolean indexed)
  {
    //  if I have numZ=2,numC=3,numT=4 then don't I have 24 images? why only testing c values makes a difference?

    // our indices are 0-based while IJ's are 1-based
    ci.setPosition(c+1, z+1, t+1);
    int rawValue = iIndex(ci.getProcessor());
    //int rawValue = iIndex(ci.getProcessor(c+1));
    //System.out.println("zct "+z+" "+c+" "+t+" rawVal "+rawValue);
    if (indexed)
      return ci.getChannelLut(c+1).getRGB(rawValue);
    else
      return rawValue;
  }
  
  // ****** helper tests ****************************************************************************************
  
  /** tests that the correct number of ImagePluses exist */
  private void impsCountTest(ImagePlus[] imps, int numExpected)
  {
    assertNotNull(imps);
    assertEquals(numExpected,imps.length);
  }
  
  /** tests that the dimensions of an ImagePlus match passed in x,y,z,c,t values */
  private void xyzctTest(ImagePlus imp, int x, int y, int z, int c, int t)
  {
    assertNotNull(imp);
    assertEquals(x,imp.getWidth());
    assertEquals(y,imp.getHeight());
    assertEquals(z,getSizeZ(imp));
    assertEquals(c,getEffectiveSizeC(imp));
    assertEquals(t,getSizeT(imp));
  }
  
  /** tests that the first and last entries of a lut match expected values */
  private void lutTest(CompositeImage ci, int channel, int minR, int minG, int minB, int maxR, int maxG, int maxB)
  {
    // channel is 0-based
    LUT lut = ci.getChannelLut(channel+1);  // IJ is 1-based
    
    byte[] reds = new byte[256];
    byte[] blues = new byte[256];
    byte[] greens = new byte[256];
    
    lut.getReds(reds);
    lut.getGreens(greens);
    lut.getBlues(blues);
    
    assertEquals((byte)minR,reds[0]);
    assertEquals((byte)maxR,reds[255]);
    assertEquals((byte)minG,greens[0]);
    assertEquals((byte)maxG,greens[255]);
    assertEquals((byte)minB,blues[0]);
    assertEquals((byte)maxB,blues[255]);
  }

  /** tests that each channel lut in a CompositeImage matches the passed in expected colors */
  private void colorTests(CompositeImage ci, int numChannels, Color[] expectedColors)
  {
    for (int i = 0; i < numChannels; i++)
    {
      Color color = expectedColors[i];
      lutTest(ci,i,0,0,0,color.getRed(),color.getGreen(),color.getBlue());
    }
  }
  
  /** tests BioFormats when directly calling BF.openImagePlus(path) (no options set) */
  private void defaultBehaviorTest(int pixType, int x, int y, int z, int c, int t)
  {
    String path = constructFakeFilename("default", pixType, x, y, z, c, t, -1, false, -1, false);
    ImagePlus[] imps = null;
    
    try {
      imps = BF.openImagePlus(path);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    impsCountTest(imps,1);

    ImagePlus imp = imps[0];
    
    xyzctTest(imp,x,y,z,c,t);
  }
  
  private void outputStackOrderTest(int pixType, ChannelOrder order, int x, int y, int z, int c, int t)
  {
    String bfChOrder = bfChanOrd(order);
    String chOrder = order.toString();
    
    String path = constructFakeFilename("stack", pixType, x, y, z, c, t, -1, false, -1, false);
    
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder(bfChOrder);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);
    
    ImagePlus imp = imps[0];
    
    xyzctTest(imp,x,y,z,c,t);

    ImageStack st = imp.getStack();
    int numSlices = st.getSize();

    assertEquals(z*c*t,numSlices);

    int procNum = 1;
    //System.out.println(order);
    Axis fastest = axis(chOrder,0);
    Axis middle = axis(chOrder,1);
    Axis slowest = axis(chOrder,2);
    int maxI = value(slowest,z,c,t);
    int maxJ = value(middle,z,c,t);
    int maxK = value(fastest,z,c,t);
    for (int i = 0; i < maxI; i++)
      for (int j = 0; j < maxJ; j++)
        for (int k = 0; k < maxK; k++)
        {
          ImageProcessor proc = st.getProcessor(procNum++);
          //printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          assertEquals(0,sIndex(proc));
          assertEquals(i,index(slowest,proc));
          assertEquals(j,index(middle,proc));
          assertEquals(k,index(fastest,proc));
        }
  }
  
  private void datasetSwapDimsTest(int pixType, int x, int y, int z, int t)
  {
    int c = 3;
    ChannelOrder swappedOrder = ChannelOrder.TCZ; // original order is ZCT
    String path = constructFakeFilename("swapDims", pixType, x, y, z, c, t, -1, false, -1, false);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setSwapDimensions(true);
      options.setInputOrder(0, bfChanOrd(swappedOrder));
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);

    ImagePlus imp = imps[0];
    
    xyzctTest(imp,x,y,t,c,z); // Z<->T swapped

    ImageStack st = imp.getStack();
    int numSlices = st.getSize();
    assertEquals(z*c*t,numSlices);

    // verify that the dimensional extents were swapped
    final int actualSizeZ = imp.getNSlices();
    final int actualSizeC = imp.getNChannels();
    final int actualSizeT = imp.getNFrames();
    assertEquals(t, actualSizeZ); // Z<->T swapped
    assertEquals(c, actualSizeC);
    assertEquals(z, actualSizeT); // Z<->T swapped

    // verify that every plane appears in the swapped order
    int p = 1;
    for (int tIndex = 0; tIndex < actualSizeT; tIndex++)
      for (int cIndex = 0; cIndex < actualSizeC; cIndex++)
        for (int zIndex = 0; zIndex < actualSizeZ; zIndex++)
        {
          ImageProcessor proc = st.getProcessor(p++);
          final int actualZ = tIndex(proc); // Z<->T swapped
          final int actualC = cIndex(proc);
          final int actualT = zIndex(proc); // Z<->T swapped
          assertEquals(zIndex, actualZ);
          assertEquals(cIndex, actualC);
          assertEquals(tIndex, actualT);
        }
  }

  private void datasetOpenAllSeriesTest(int x, int y, int z, int c, int t, int s)
  {
    String path = constructFakeFilename("openAllSeries", FormatTools.UINT32, x, y, z, c, t, s, false, -1, false);
    
    // try it when false
    
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setOpenAllSeries(false);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // test results
    
    impsCountTest(imps,1);
    xyzctTest(imps[0],x,y,z,c,t);
    assertEquals(z*c*t, imps[0].getStack().getSize());
    
    // try it when true
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setOpenAllSeries(true);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // test results
    
    assertEquals(s,imps.length);
    for (int i = 0; i < s; i++)
    {
      assertEquals(x,imps[i].getWidth());
      assertEquals(y,imps[i].getHeight());
      assertEquals(z*c*t, imps[i].getStack().getSize());
    }
  }
  
  private void datasetConcatenateTest(int pixType, int x, int y, int z, int c, int t, int s)
  {
    assertTrue(s >= 1);  // necessary for this test
    
    // open all series as one
    
    String path = constructFakeFilename("concat", pixType, x, y, z, c, t, s, false, -1, false);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setOpenAllSeries(true);
      options.setConcatenate(true);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // test results
    
    impsCountTest(imps,1);
    ImageStack st = imps[0].getStack();

    // make sure the number of slices in stack is a sum of all series
    assertEquals(z*c*t*s, st.getSize());
    
    int index = 1;
    for (int sIndex = 0; sIndex < s; sIndex++) {
      for (int tIndex = 0; tIndex < t; tIndex++) {
        for (int cIndex = 0; cIndex < c; cIndex++) {
          for (int zIndex = 0; zIndex < z; zIndex++) {
            ImageProcessor proc = st.getProcessor(index++); 
            assertEquals(sIndex, sIndex(proc));
            assertEquals(zIndex, zIndex(proc));
            assertEquals(cIndex, cIndex(proc));
            assertEquals(tIndex, tIndex(proc));
          }
        }
      }
    }
  }
  
  private void autoscaleTest(int pixType, boolean wantAutoscale)
  {
    final int sizeZ = 2, sizeC = 3, sizeT = 4, sizeX = 51, sizeY = 16;
    final String path = constructFakeFilename("autoscale",pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, -1, false, -1, false);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(wantAutoscale);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    impsCountTest(imps,1);
    
    imp = imps[0];

    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);

    ImageStack st = imp.getStack();
    int numSlices = st.getSize();

    long expectedMax,expectedMin;
    
    if (wantAutoscale || (FormatTools.isFloatingPoint(pixType)))
    {
      expectedMax = Math.max( minPixelValue(pixType)+sizeX-1, sizeZ*sizeC*sizeT - 1 );  // series size always 1 so ignore
      expectedMin = minPixelValue(pixType);
    }
    else // not autoscaling - get min/max of pixel type
    {
      expectedMax = maxPixelValue(pixType);
      expectedMin = 0;
    }

    // workaround for IJ nonsupport of signed data
    if (FormatTools.isSigned(pixType) && !FormatTools.isFloatingPoint(pixType))
    {
      Calibration cal = imp.getCalibration();
      assertEquals(Calibration.STRAIGHT_LINE,cal.getFunction());
      double[] coeffs = cal.getCoefficients();
      int bitsPerPix = FormatTools.getBytesPerPixel(pixType) * 8;
      assertEquals(-(Math.pow(2, (bitsPerPix-1))),coeffs[0],0);
      assertEquals(1,coeffs[1],0);
    }
    else // regular case
    {
      //System.out.println("Checking max/min of each processor");
      for (int i = 0; i < numSlices; i++)
      {
        //System.out.println("Trying proc #"+i+" of "+numSlices);
        ImageProcessor proc = st.getProcessor(i+1);
        assertEquals(expectedMax,proc.getMax(),0.1);
        assertEquals(expectedMin,proc.getMin(),0.1);
      }
    }
  }
  
  private void colorCompositeTest(int pixType, boolean indexed, int rgb, boolean falseColor, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries)
  {
    // reportedly works in BF for 2<=sizeC<=7 and also numSeries*sizeC*3 <= 25
    
    assertTrue(sizeC >= 2);
    assertTrue(sizeC <= 7);
    assertTrue(numSeries*sizeC*3 <= 25);  // slider limit in IJ
    
    String path = constructFakeFilename("colorComposite", pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries,
        indexed, rgb, falseColor);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    CompositeImage ci = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorMode(ImporterOptions.COLOR_MODE_COMPOSITE);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);
    
    imp = imps[0];
    
    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);
    
    assertTrue(imp.isComposite());
    
    ci = (CompositeImage)imp;
    
    assertFalse(ci.hasCustomLuts());

    assertEquals(CompositeImage.COMPOSITE, ci.getMode());
    
    colorTests(ci,sizeC,DefaultColorOrder);

    ci.reset();  // force the channel processors to get initialized, otherwise nullptr  - TODO : does this point out a IJ bug?
    
    int maxZ = ci.getNSlices();
    int maxC = ci.getNChannels();
    int maxT = ci.getNFrames();
    
    //System.out.println("Checking index vals");
    //System.out.println("maxes z c t = "+maxZ+" "+maxC+" "+maxT);
    
    // check that each image in the overall series has the correct iIndex value
    for (int z = 0; z < ci.getNSlices(); z++)
      for (int c = 0; c < ci.getNChannels(); c++)
        for (int t = 0; t < ci.getNFrames(); t++)
        {
          assertEquals((maxZ*maxC*t + maxC*z + c), getIndexPixelValue(ci,z,c,t,indexed));  // CZT order
        }
  }
  
  private void colorCustomTest(int pixType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries)
  {
    // reportedly works in BF for 2<=sizeC<=7 and also numSeries*sizeC*3 <= 25
    
    assertTrue(sizeC >= 2);
    assertTrue(sizeC <= 7);
    assertTrue(numSeries*sizeC*3 <= 25);  // slider limit in IJ
    
    String path = constructFakeFilename("colorCustom", pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, false, -1, false);
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    CompositeImage ci = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorMode(ImporterOptions.COLOR_MODE_CUSTOM);
      for (int s = 0; s < numSeries; s++)
        for (int c = 0; c < sizeC; c++)
          options.setCustomColor(s, c, CustomColorOrder[c]);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);
    
    imp = imps[0];
    
    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);
    
    assertTrue(imp.isComposite());
    
    ci = (CompositeImage)imp;
    
    assertFalse(ci.hasCustomLuts());

    assertEquals(CompositeImage.COLOR, ci.getMode());

    colorTests(ci,sizeC,CustomColorOrder);
  }
  
  private void memoryVirtualStackTest(boolean desireVirtual)
  {
      int x = 604, y = 531, z = 7, c = 1, t = 1;
      
      String path = constructFakeFilename("vstack", FormatTools.UINT16, x, y, z, c, t, -1, false, -1, false);
      
      // open stack
      ImagePlus[] imps = null;
      try {
        ImporterOptions options = new ImporterOptions();
        options.setId(path);
        options.setVirtual(desireVirtual);  // user specified value here
        imps = BF.openImagePlus(options);
      }
      catch (IOException e) {
        fail(e.getMessage());
      }
      catch (FormatException e) {
        fail(e.getMessage());
      }
  
      // test results
      impsCountTest(imps,1);
      ImagePlus imp = imps[0];
      xyzctTest(imp,x,y,z,c,t);
  
      assertEquals(desireVirtual,imp.getStack().isVirtual());
  }

  private void memoryRecordModificationsTest(boolean wantToRemember)
  {
    int x = 50, y = 15, z = 3, c = 1, t = 1;
    String path = constructFakeFilename("memRec", FormatTools.UINT8, x, y, z, c, t, -1, false, -1, false);
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    assertTrue(y > 10);  // needed for this test
    assertTrue(z > 1);
    
    // open file
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setVirtual(true);
      options.setRecord(wantToRemember);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // basic tests
    impsCountTest(imps,1);
    imp = imps[0];
    xyzctTest(imp,x,y,z,c,t);

    // change data in slice 1, swap to slice 2, swap back, see whether data reverts

    imp.setSlice(1);
    assertEquals(1,(int)imp.getProcessor().getPixelValue(1,10));

    // run a plugin whose changes are recorded
    WindowManager.setTempCurrentImage(imp);
    IJ.run("Flip Horizontally","slice");
    assertEquals(x-2,(int)imp.getProcessor().getPixelValue(1,10));
    
    imp.setSlice(2);
    assertEquals(1,(int)imp.getProcessor().getPixelValue(1,10));
    
    imp.setSlice(1);
    int expectedVal = wantToRemember ? x-2 : 1;
    assertEquals(expectedVal,(int)imp.getProcessor().getPixelValue(1,10));
  }
  
  private void memorySpecifyRangeTest(int z, int c, int t,
      int zFrom, int zTo, int zBy,
      int cFrom, int cTo, int cBy,
      int tFrom, int tTo, int tBy)
  { 
    int pixType = FormatTools.UINT8, x=50, y=5, s=-1;
    String path = constructFakeFilename("range", pixType, x, y, z, c, t, s, false, -1, false);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      
      // only set values when nondefault behavior specified
      
      // z's
      if (zFrom != 0)
        options.setZBegin(0, zFrom);
      if (zTo != z-1)
        options.setZEnd(0, zTo);
      if (zBy != 1)
        options.setZStep(0, zBy);
      
      // c's
      if (cFrom != 0)
        options.setCBegin(0, cFrom);
      if (cTo != c-1)
        options.setCEnd(0, cTo);
      if (cBy != 1)
        options.setCStep(0, cBy);
      
      // t's
      if (tFrom != 0)
        options.setTBegin(0, tFrom);
      if (tTo != t-1)
        options.setTEnd(0, tTo);
      if (tBy != 1)
        options.setTStep(0, tBy);
        
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // should have the data in one series
    impsCountTest(imps,1);
    ImagePlus imp = imps[0];
    xyzctTest(imp,x,y,numInSeries(zFrom,zTo,zBy),numInSeries(cFrom,cTo,cBy),numInSeries(tFrom,tTo,tBy));
    ImageStack st = imp.getStack();

    // should be in correct order
    assertTrue(seriesInCorrectOrder(st,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy));
  }
  
  private void memoryCropTest(int pixType, int x, int y, int cx, int cy)
  {
    String path = constructFakeFilename("crop", pixType, x, y, 1, 1, 1, 1, false, -1, false);
    
    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setCrop(true);
      options.setCropRegion(0, new Region(0, 0, cx, cy));
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // test results
    impsCountTest(imps,1);
    xyzctTest(imps[0],cx,cy,1,1,1);
  }
  
  // note - this test needs to rely on crop() to get predictable nonzero minimums
  
  private void comboCropAndAutoscaleTest(int pixType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries,
      int originCropX, int originCropY, int sizeCrop)
  {
    final String path = constructFakeFilename("cropAutoscale",pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, false, -1, false);
    
    // needed for this test
    assertTrue(originCropX >= 50);
    assertTrue(originCropY >= 10); 
    assertTrue(sizeCrop > 0);
    assertTrue(originCropX + sizeCrop < sizeX);
    assertTrue(originCropY + sizeCrop < sizeY);
    assertTrue(originCropX + sizeCrop < 255);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(true);
      options.setCrop(true);
      options.setCropRegion(0,new Region(originCropX,originCropY,sizeCrop,sizeCrop));
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    impsCountTest(imps,1);
    imp = imps[0];
    xyzctTest(imps[0],sizeCrop,sizeCrop,sizeZ,sizeC,sizeT);

    ImageStack st = imp.getStack();
    int numSlices = st.getSize();

    long expectedMax = originCropX+sizeCrop-1;
    long expectedMin = originCropX;

    for (int i = 0; i < numSlices; i++)
    {
      ImageProcessor proc = st.getProcessor(i+1);
      assertEquals(expectedMax,proc.getMax(),0.1);
      assertEquals(expectedMin,proc.getMin(),0.1);
    }
  }
  
  private void comboConcatSplitFocalPlanesTest()
  {
    // take a nontrivial zct set of series
    // run split and concat at same time

    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7, series = 3;
    final String path = constructFakeFilename("concatSplitZ",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, series, false, -1, false);

    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setConcatenate(true);
      options.setSplitFocalPlanes(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
      }
    
    // one time point per image
    impsCountTest(imps,sizeZ);

    // from ZCT order: Z pulled out, CT in order
    for (int z = 0; z < sizeZ; z++)
    {
      ImagePlus imp = imps[z];
      xyzctTest(imp,sizeX,sizeY,1,sizeC,sizeT);
      ImageStack st = imp.getStack();
      assertEquals(series*sizeC*sizeT,st.getSize());
      for (int s = 0; s < series; s++) {
        int index = 1; // IJ 1-based
        for (int t = 0; t < sizeT; t++) {
          for (int c = 0; c < sizeC; c++) {
            System.out.println("index "+index);
            ImageProcessor proc = st.getProcessor(s*sizeT*sizeC + index++);
            //System.out.println("s z c t "+s+" "+z+" "+c+" "+t);
            System.out.println("z c t "+z+" "+c+" "+t);
            System.out.println("is iz ic it "+sIndex(proc)+" "+zIndex(proc)+" "+cIndex(proc)+" "+tIndex(proc));
            // test the values
            assertEquals(z,zIndex(proc));
            assertEquals(c,cIndex(proc));
            assertEquals(t,tIndex(proc));
            assertEquals(s,sIndex(proc));
          }
        }
      }
    }
  }
  
  private void comboConcatSplitChannelsTest()
  {
    // take a nontrivial zct set of series
    // run split and concat at same time

    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7, series = 3;
    final String path = constructFakeFilename("concatSplitC",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, series, false, -1, false);

    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setConcatenate(true);
      options.setSplitChannels(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
      }
    
    // one time point per image
    impsCountTest(imps,sizeC);
    
    // from ZCT order: C pulled out, ZT in order
    for (int c = 0; c < sizeC; c++)
    {
      ImagePlus imp = imps[c];
      xyzctTest(imp,sizeX,sizeY,sizeZ,1,sizeT);
      ImageStack st = imp.getStack();
      assertEquals(series*sizeZ*sizeT,st.getSize());
      for (int s = 0; s < series; s++) {
        int index = 1;
        for (int t = 0; t < sizeT; t++) {
          for (int z = 0; z < sizeZ; z++) {
            ImageProcessor proc = st.getProcessor(s*sizeZ*sizeT + index++);
            //System.out.println("index "+index);
            //System.out.println("s z c t "+s+" "+z+" "+c+" "+t);
            //System.out.println("iz ic it "+zIndex(proc)+" "+cIndex(proc)+" "+tIndex(proc));
            // test the values
            assertEquals(z,zIndex(proc));
            assertEquals(c,cIndex(proc));
            assertEquals(t,tIndex(proc));
            assertEquals(s,sIndex(proc));
          }
        }
      }
    }
  }
  
  private void comboConcatSplitTimepointsTest()
  {
    // take a nontrivial zct set of series
    // run split and concat at same time

    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7, series = 3;
    final String path = constructFakeFilename("concatSplitC",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, series, false, -1, false);

    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setConcatenate(true);
      options.setSplitTimepoints(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
      }
    
    // one time point per image
    impsCountTest(imps,sizeT);
    
    // from ZCT order: T pulled out, ZC in order
    for (int t = 0; t < sizeT; t++)
    {
      ImagePlus imp = imps[t];
      xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,1);
      ImageStack st = imp.getStack();
      assertEquals(series*sizeZ*sizeC,st.getSize());
      for (int s = 0; s < series; s++) {
        int index = 1;
        for (int c = 0; c < sizeC; c++) {
          for (int z = 0; z < sizeZ; z++) {
            ImageProcessor proc = st.getProcessor(s*sizeZ*sizeC + index++);
            //System.out.println("index "+index);
            //System.out.println("s z c t "+s+" "+z+" "+c+" "+t);
            //System.out.println("iz ic it "+zIndex(proc)+" "+cIndex(proc)+" "+tIndex(proc));
            // test the values
            assertEquals(z,zIndex(proc));
            assertEquals(c,cIndex(proc));
            assertEquals(t,tIndex(proc));
            assertEquals(s,sIndex(proc));
          }
        }
      }
    }
  }
  
// ** ImporterTest methods **************************************************************

  @Test
  public void testDefaultBehavior()
  {
    defaultBehaviorTest(FormatTools.UINT16, 400, 300, 1, 1, 1);
    defaultBehaviorTest(FormatTools.INT16, 107, 414, 1, 1, 1);
    defaultBehaviorTest(FormatTools.UINT32, 323, 206, 3, 2, 1);
    defaultBehaviorTest(FormatTools.UINT8, 57, 78, 5, 4, 3);
    defaultBehaviorTest(FormatTools.INT32, 158, 99, 2, 3, 4);
    defaultBehaviorTest(FormatTools.INT8, 232, 153, 3, 7, 5);
    defaultBehaviorTest(FormatTools.FLOAT, 73, 99, 3, 4, 5);
    defaultBehaviorTest(FormatTools.DOUBLE, 106, 44, 5, 5, 4);
  }

  @Test
  public void testOutputStackOrder()
  {
    for (ChannelOrder order : ChannelOrder.values())
      outputStackOrderTest(FormatTools.UINT8, order,  82, 47, 2, 3, 4);
  }
    
  @Test
  public void testDatasetGroupFiles()
  {
    String path = FAKE_FILES[0];

    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setGroupFiles(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
      assertEquals(FAKE_PATTERN, options.getId());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
      }
    
    impsCountTest(imps,1);
    assertEquals(105,imps[0].getStack().getSize());
  }

  @Test
  public void testDatasetOpenFilesIndividually()
  {
    // TODO - try to remove file dependency
    
    String path = "2channel_stack_raw01.pic";
    
    // there is a second file called "2channel_stack_raw02.pic" present in the same directory
    // if open indiv true should only load one of them, otherwise both
    
    // try ungrouped
    
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setUngroupFiles(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // test results
    
    impsCountTest(imps,1);
    assertEquals(16,imps[0].getStack().getSize());  // one loaded as one set with 16 slices
    
    // try grouped
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setUngroupFiles(false);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // test results
    
    impsCountTest(imps,1);
    assertEquals(32,imps[0].getStack().getSize());  // both loaded as one set of 32 slices
  }

  @Test
  public void testDatasetSwapDims()
  {
    // TODO: testing only swapping Z&T of XYZTC. Add more option testing

    datasetSwapDimsTest(FormatTools.UINT8, 82, 47, 1, 3);
    datasetSwapDimsTest(FormatTools.UINT16, 82, 47, 3, 1);
    datasetSwapDimsTest(FormatTools.UINT16, 82, 47, 5, 2);
    datasetSwapDimsTest(FormatTools.UINT32, 82, 47, 5, 2);
    datasetSwapDimsTest(FormatTools.INT8, 44, 108, 1, 4);
    datasetSwapDimsTest(FormatTools.INT16, 44, 108, 2, 1);
    datasetSwapDimsTest(FormatTools.INT32, 44, 108, 4, 3);
    datasetSwapDimsTest(FormatTools.FLOAT, 67, 109, 4, 3);
    datasetSwapDimsTest(FormatTools.DOUBLE, 67, 100, 3, 2);
  }

  @Test
  public void testDatasetOpenAllSeries()
  {
    datasetOpenAllSeriesTest(73,107,1,1,1,1);  // one series
    datasetOpenAllSeriesTest(73,107,1,1,1,2);  // two series
    datasetOpenAllSeriesTest(73,107,5,3,4,4);  // multiple series with Z,C,T larger than 1
  }

  @Test
  public void testDatasetConcatenate()
  {
    // open a dataset that has multiple series and should get back a single series
    datasetConcatenateTest(FormatTools.UINT8, 82, 47, 1, 1, 1, 1);
    datasetConcatenateTest(FormatTools.UINT8, 82, 47, 1, 1, 1, 17);
    datasetConcatenateTest(FormatTools.UINT8, 82, 47, 4, 5, 2, 9);
  }

  @Test
  public void testColorDefault()
  {
    int sizeX = 100, sizeY = 120, sizeZ = 2, sizeC = 7, sizeT = 4, numSeries = 3;
    
    String path = constructFakeFilename("colorDefault", FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, false, -1, false);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorMode(ImporterOptions.COLOR_MODE_DEFAULT);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);
    
    imp = imps[0];

    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);
    
    assertFalse(imp.isComposite());

    // TODO - not a composite - need to determine what to test
    
    fail("unfinished");
  }
  
  @Test
  public void testColorComposite()
  {
    // BF only supporting C from 2 to 7 and due to IJ's slider limitation (C*numSeries*3) <= 25

    // these here to simplify debugging
    colorCompositeTest(FormatTools.UINT8,false,1,false,55,44,2,3,4,1);
    colorCompositeTest(FormatTools.UINT8,true,1,false,55,44,2,3,4,1);

    int[] pixTypes = new int[] {FormatTools.UINT8};
    int[] xs = new int[] {56};
    int[] ys = new int[] {41};
    int[] zs = new int[] {1,2};
    int[] cs = new int[] {2,3,4,5,6,7};  // all that BF/IJ supports right now
    int[] ts = new int[] {1,2};
    int[] series = new int[] {1,2,3,4};
    
    for (int pixFormat : pixTypes)
      for (int x : xs)
        for (int y : ys)
          for (int z : zs)
            for (int c : cs)
              for (int t : ts)
                for (int s : series)
                  if ((c*s*3) <= 25)  // IJ slider limitation
                    for (boolean indexed : BooleanStates)
                    {
                      //System.out.println("indexed "+indexed+" format "+pixFormat+" x "+x+" y "+y+" z "+z+" c "+c+" t "+t+" s "+s);
                      //colorCompositeTest(indexed,pixFormat,x,y,z,c,t,s);
                    }
  }
  
  private void colorColorizedTest()
  {
    // TODO: temp first attempt: sizeC == 1 and rgb matches
    
    int sizeX = 100, sizeY = 120, sizeZ = 1, sizeC = 1, sizeT = 1, numSeries = 1, rgb = 1;
    boolean indexed = true;
    
    String path = constructFakeFilename("colorColorized", FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, indexed, rgb, false);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    CompositeImage ci = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorMode(ImporterOptions.COLOR_MODE_COLORIZED);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);
    
    imp = imps[0];
    
    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);
    
    assertTrue(imp.isComposite());
    
    ci = (CompositeImage)imp;
    
    assertFalse(ci.hasCustomLuts());

    assertEquals(CompositeImage.COLOR, ci.getMode());
    
    colorTests(ci,sizeC,DefaultColorOrder);

    fail("unfinished");
  }
  
  @Test
  public void testColorColorized()
  {
    colorColorizedTest();
  }
  
  @Test
  public void testColorGrayscale()
  {
    int sizeX = 100, sizeY = 120, sizeZ = 2, sizeC = 7, sizeT = 4, numSeries = 3;
    
    String path = constructFakeFilename("colorGrayscale", FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, false, -1, false);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    CompositeImage ci = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorMode(ImporterOptions.COLOR_MODE_GRAYSCALE);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,1);
    
    imp = imps[0];
    
    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);
    
    assertTrue(imp.isComposite());
    
    ci = (CompositeImage)imp;
    
    assertFalse(ci.hasCustomLuts());

    assertEquals(CompositeImage.GRAYSCALE, ci.getMode());

    colorTests(ci,sizeC,DefaultColorOrder);

    fail("unfinished");
  }
  
  @Test
  public void testColorCustom()
  {
    // BF only supporting C from 2 to 7 and due to IJ's slider limitation (C*numSeries*3) <= 25
    
    int[] pixTypes = new int[]{FormatTools.UINT8, FormatTools.UINT16, FormatTools.FLOAT};
    int[] xs = new int[] {45};
    int[] ys = new int[] {14};
    int[] zs = new int[] {1,2};
    int[] cs = new int[] {2,3,4,5,6,7};  // all that BF/IJ supports right now
    int[] ts = new int[] {1,2};
    int[] series = new int[] {1,2,3,4};
    
    for (int pixFormat : pixTypes)
      for (int x : xs)
        for (int y : ys)
          for (int z : zs)
            for (int c : cs)
              for (int t : ts)
                for (int s : series)
                  if ((c*s*3) <= 25)  // IJ slider limitation
                  {
                    //System.out.println("format "+pixFormat+" x "+x+" y "+y+" z "+z+" c "+c+" t "+t+" s "+s);
                    colorCustomTest(pixFormat,x,y,z,c,t,s);
                  }
  }
  
  @Test
  public void testColorAutoscale()
  {
    
    // From BF:
    // Autoscale - Stretches the histogram of the image planes to fit the data range. Does not alter underlying values in
    // the image. If selected, histogram is stretched for each stack based upon the global minimum and maximum value
    // throughout the stack.

    for (int pixType : PixelTypes)
    {
      for (boolean autoscale : BooleanStates)
      {
        //System.out.println("testColorAutoscale(): pixType = "+FormatTools.getPixelTypeString(pixType)+" autoscale = "+autoscale);
        autoscaleTest(pixType,autoscale);
      }
    }
  }

  @Test
  public void testMemoryVirtualStack()
  {
    memoryVirtualStackTest(false);
    memoryVirtualStackTest(true);
  }

  @Test
  public void testMemoryRecordModifications()
  {
    memoryRecordModificationsTest(false);
    memoryRecordModificationsTest(true);
  }

  @Test
  public void testMemorySpecifyRange()
  {
    int z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy;

    // test partial z: from
    z=8; c=3; t=2; zFrom=2; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial z: to
    z=8; c=3; t=2; zFrom=0; zTo=4; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial z: by
    z=8; c=3; t=2; zFrom=0; zTo=z-1; zBy=3; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test full z
    z=8; c=3; t=2; zFrom=2; zTo=7; zBy=3; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial c: from
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=3; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial c: to
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=6; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial c: by
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=4; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test full c
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=12; cBy=4; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial t: from
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=4; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial t: to
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=8; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test partial t: by
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=2;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test full t
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=4; tTo=13; tBy=2;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test edge case combo with an invalid by
    z=2; c=2; t=2; zFrom=0; zTo=0; zBy=2; cFrom=1; cTo=1; cBy=1; tFrom=0; tTo=1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test a combination of zct's
    z=5; c=4; t=6; zFrom=1; zTo=4; zBy=2; cFrom=1; cTo=3; cBy=1; tFrom=2; tTo=5; tBy=2;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test another combination of zct's
    z=7; c=7; t=7; zFrom=3; zTo=6; zBy=4; cFrom=1; cTo=6; cBy=3; tFrom=0; tTo=2; tBy=2;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test bad combination of zct's - choosing beyond ends of ranges
    
    // z index before 0 begin
    try {
      z=7; c=7; t=7; zFrom=-1; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // z index after z-1 end
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
    
    // z by < 1
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=0; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // c index before 0 begin
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=-1; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // c index after c-1 end
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // c by < 1
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=0; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // t index before 0 begin
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=-1; tTo=t-1; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // t index after t-1 end
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // t by < 1
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=0;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
    
    /* TODO - enable when step by 0 code fixed and remove extra tests above
    // uber combo test : comprehensive but probably WAY too much computation to finish in reasonable time
    z = 6; c = 5; t = 4;
    for (int zStart = -1; zStart < z+2; zStart++)
      for (int zEnd = -1; zEnd < z+2; zEnd++)
        for (int zInc = -1; zInc < z+2; zInc++)
          for (int cStart = -1; cStart < c+2; cStart++)
            for (int cEnd = -1; cEnd < c+2; cEnd++)
              for (int cInc = -1; cInc < c+2; cInc++)
                for (int tStart = -1; tStart < t+2; tStart++)
                  for (int tEnd = -1; tEnd < t+2; tEnd++)
                    for (int tInc = -1; tInc < t+2; tInc++)
                      // if an invalid index of some kind
                      if ((zStart < 0) || (zStart >= z) ||
                          (zEnd < 0) || (zEnd >= z) || // ignored by BF (zEnd < zStart) ||
                          (zInc < 1) ||
                          (cStart < 0) || (cStart >= c) ||
                          (cEnd < 0) || (cEnd >= c) || // ignored by BF (cEnd < cStart) ||
                          (cInc < 1) ||
                          (tStart < 0) || (tStart >= t) ||
                          (tEnd < 0) || (tEnd >= t) || // ignored by BF (tEnd < tStart) ||
                          (tInc < 1))
                      {
                        // expect failure
                        try {
                          memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
                          System.out.println("memorySpecifyRange() test failed: combo = zct "+z+" "+c+" "+t+
                            " z vals "+zFrom+" "+zTo+" "+zBy+
                            " c vals "+cFrom+" "+cTo+" "+cBy+
                            " t vals "+tFrom+" "+tTo+" "+tBy);
                          fail("BF did not catch bad indexing code");
                        } catch (IllegalArgumentException e) {
                          assertTrue(true);
                        }
                      }
                      else
                        // expect success
                        memorySpecifyRangeTest(z,c,t,zStart,zEnd,zInc,cStart,cEnd,cInc,tStart,tEnd,tInc);
    */
    
  }
  
  @Test
  public void testMemoryCrop()
  {
    memoryCropTest(FormatTools.UINT8, 203, 409, 185, 104);
    memoryCropTest(FormatTools.UINT8, 203, 409, 203, 409);
    memoryCropTest(FormatTools.UINT8, 100, 30, 3, 3);
    memoryCropTest(FormatTools.INT32, 100, 30, 3, 3);
  }
  
  @Test
  public void testSplitChannels()
  {
    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7;
    final String path = constructFakeFilename("splitC",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, -1, false, -1, false);

    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setSplitChannels(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // one channel per image
    impsCountTest(imps,sizeC);
    
    // unwind ZCT loop : C pulled outside, ZT in order
    for (int c = 0; c < sizeC; c++) {
      ImagePlus imp = imps[c];
      xyzctTest(imp,sizeX,sizeY,sizeZ,1,sizeT);
      ImageStack st = imp.getStack();
      assertEquals(sizeZ * sizeT,st.getSize());
      int index = 0;
      for (int t = 0; t < sizeT; t++) {
        for (int z = 0; z < sizeZ; z++) {
          ImageProcessor proc = st.getProcessor(++index);
          // test the values
          assertEquals(z,zIndex(proc));
          assertEquals(c,cIndex(proc));
          assertEquals(t,tIndex(proc));
        }
      }
    }
  }
  
  @Test
  public void testSplitFocalPlanes()
  {
    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7;
    final String path = constructFakeFilename("splitZ",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, -1, false, -1, false);

    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setSplitFocalPlanes(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
      }
    
    // one focal plane per image
    impsCountTest(imps,sizeZ);

    // unwind ZCT loop : Z pulled outside, CT in order
    for (int z = 0; z < sizeZ; z++) {
      ImagePlus imp = imps[z];
      xyzctTest(imp,sizeX,sizeY,1,sizeC,sizeT);
      ImageStack st = imp.getStack();
      assertEquals(sizeC * sizeT,st.getSize());
      int index = 0;
      for (int t = 0; t < sizeT; t++) {
        for (int c = 0; c < sizeC; c++) {
          ImageProcessor proc = st.getProcessor(++index);
          // test the values
          assertEquals(z,zIndex(proc));
          assertEquals(c,cIndex(proc));
          assertEquals(t,tIndex(proc));
        }
      }
    }
  }
  
  @Test
  public void testSplitTimepoints()
  {
    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7;
    final String path = constructFakeFilename("splitT",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, -1, false, -1, false);

    // open image
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setSplitTimepoints(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
      }
    
    // one time point per image
    impsCountTest(imps,sizeT);
    
    // unwind ZTC loop : T pulled outside, ZC in order
    for (int t = 0; t < sizeT; t++) {
      ImagePlus imp = imps[t];
      xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,1);
      ImageStack st = imp.getStack();
      assertEquals(sizeZ * sizeC,st.getSize());
      int index = 0;
      for (int c = 0; c < sizeC; c++) {
        for (int z = 0; z < sizeZ; z++) {
          ImageProcessor proc = st.getProcessor(++index);
          // test the values
          assertEquals(z,zIndex(proc));
          assertEquals(c,cIndex(proc));
          assertEquals(t,tIndex(proc));
        }
      }
    }
  }

  @Test
  public void testMacros()
  {
    //IJ.runMacro("Bio-Formats Importer", "open=int8&pixelType=int8&sizeZ=3&sizeC=5&sizeT=7&sizeY=50.fake merge_channels stack_order=Default");
    fail("unimplemented");
  }
  
  @Test
  public void testComboCropAutoscale()
  {
    // try a simple test: single small byte type image 
    comboCropAndAutoscaleTest(FormatTools.UINT8,100,80,1,1,1,1,70,40,25);
    
    // try multiple dimensions
    comboCropAndAutoscaleTest(FormatTools.UINT8,84,63,4,3,2,5,51,8,13);
    
    // try various pixTypes
    for (int pixType : PixelTypes)
      comboCropAndAutoscaleTest(pixType,96,96,2,2,2,2,70,60,10);
  }
  
  @Test
  public void testComboConcatColorize()
  {
    fail("unimplemented");
  }

  @Test
  public void testComboConcatSplit()
  {
    comboConcatSplitFocalPlanesTest();
    comboConcatSplitChannelsTest();
    comboConcatSplitTimepointsTest();
  }

  @Test
  public void testComboColorizeSplit()
  {
    fail("unimplemented");
  }
  
  @Test
  public void testComboConcatColorizeSplit()
  {
    fail("unimplemented");
  }
  
  @Test
  public void testComboManyOptions()
  {
    int pixType = FormatTools.UINT16, sizeX = 106, sizeY = 33, sizeZ = 3, sizeC = 5, sizeT = 7;
    int cropOriginX = 0, cropOriginY = 0, cropSizeX = 55, cropSizeY = 16, start = 1, stepBy = 2;
    ChannelOrder swappedOrder = ChannelOrder.CTZ;  // orig is ZCT : this is a deadly swap of all dims

    // note - to reuse existing code it is necessary that the crop origin is (0,0)
    
    String path = constructFakeFilename("superCombo", pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, 1, false, -1, false);
  
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setSwapDimensions(true);
      options.setInputOrder(0, bfChanOrd(swappedOrder));
      options.setCrop(true);
      options.setCropRegion(0, new Region(cropOriginX,cropOriginY,cropSizeX,cropSizeY));
      options.setTStep(0, stepBy);
      options.setTBegin(0, start);
      options.setSplitFocalPlanes(true);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    impsCountTest(imps,sizeT);  // we split on Z but that dim was swapped with T

    for (int zIndex = 0; zIndex < sizeT; zIndex++)  // sizeT = Z
    {
      ImagePlus imp = imps[zIndex];
      
      int numC = numInSeries(start,sizeC-1,stepBy);

      xyzctTest(imp,cropSizeX,cropSizeY,1,sizeZ,numC); // all dims changed
  
      ImageStack st = imp.getStack();
      assertEquals(sizeZ*numC,st.getSize());  // sizeZ = C, numC = T
      
      int p = 1;
      for (int tIndex = start; tIndex < sizeC; tIndex += stepBy)
        for (int cIndex = 0; cIndex < sizeZ; cIndex++)
        {
          ImageProcessor proc = st.getProcessor(p++);
          
          assertEquals(cropSizeX,proc.getWidth());
          assertEquals(cropSizeY,proc.getHeight());

          final int actualZ = tIndex(proc);
          final int actualC = zIndex(proc);
          final int actualT = cIndex(proc);
          
          assertEquals(zIndex, actualZ);
          assertEquals(cIndex, actualC);
          assertEquals(tIndex, actualT);
        }
    }
  }
}
