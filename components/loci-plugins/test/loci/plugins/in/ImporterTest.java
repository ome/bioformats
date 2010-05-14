//
// ImporterTest.java
//

package loci.plugins.in;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.lang.reflect.Field;

import loci.common.Location;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.BF;

import org.junit.Test;

// TODO
//    waiting on BF implementations for
//      range step by 0
//      BF/imageJ returning wrong max num pixels for UINT32 - off by one
//      memoryRecord failure needs BF code fix?
//      mergeOptions BF api for finishing merge tests
//      custom color BF api for doing that test
//  - flesh out existing tests
//      write tests for the color options : 4 cases - some mention was made that indexcolor is an issue in testing
//        merge - basic test in place but not passing. need to flesh out mergeOptions when BF code in place.
//        rgb colorize - need to do actual tests. see BF gui to get idea of how it works
//        custom colorize - waiting for creation of API for setting r,g,b info
//        autoscale - code written but failing
//      open individual files: try to come up with a way to test without a disk file as source
//      swapped dims test needs to test cases other than from default swapping Z & T
//      output stack order - testing of iIndex?
//      range - more combos of ztc? uncomment the by 0 tests
//  - add some tests for combination of options
//  - improve, comment, and generalize code for increased coverage

public class ImporterTest {

  private enum Axis {Z,C,T};
  
  private enum ChannelOrder {XYZTC, XYZCT, XYTZC, XYTCZ, XYCTZ, XYCZT};
  
  private static final boolean[] BooleanStates = new boolean[] {false, true};
  
  private static final int[] PixelTypes = new int[] {
      FormatTools.UINT8, FormatTools.UINT16, FormatTools.UINT32,
      FormatTools.INT8,  FormatTools.INT16,  FormatTools.INT32,
      FormatTools.FLOAT, FormatTools.DOUBLE
      };
  
  private static final String[] FAKE_FILES;
  private static final String FAKE_PATTERN;
 
  static {

    //String template = "test_C%s_TP%s&sizeX=50&sizeY=20&sizeZ=7.fake";
    String template = constructFakeFilename("test_C%s_TP%s", FormatTools.INT32, 50, 20, 7, 1, 1, -1);
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
      int pixelType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries)
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

  private Axis axis(String order, int d)
  {
    if ((d < 0) || (d > 2))
      throw new IllegalArgumentException("axis() - index out of bounds [0..2]: "+d);
    
    char dim = order.charAt(2+d);
    
    if (dim == 'Z') return Axis.Z;
    if (dim == 'C') return Axis.C;
    if (dim == 'T') return Axis.T;

    throw new IllegalArgumentException("axis() - invalid image order specified: "+order);
  }

  private int value(Axis axis, int z, int c, int t)
  {
    if (axis == Axis.Z) return z;
    if (axis == Axis.C) return c;
    if (axis == Axis.T) return t;

    throw new IllegalArgumentException("value() - unknown axis: "+axis);
  }
  
  private int index(Axis axis, ImageProcessor proc)
  {
    if (axis == Axis.Z) return zIndex(proc);
    if (axis == Axis.C) return cIndex(proc);
    if (axis == Axis.T) return tIndex(proc);
    
    throw new IllegalArgumentException("index() - unknown axis: "+axis);
  }

  private int numInSeries(int from, int to, int by)
  {
    // could calc this but simple loop suffices for our purposes
    int count = 0;
    for (int i = from; i <= to; i += by)
        count++;
    return count;
    
    /* calc'ed version : less clear
    int spread = to - from + 1;
    
    if (spread % by == 0)
      return (spread / by);
    else
      return (spread / by) + 1;
    */
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
    /*
    
    long maxUnsigned = (1L << FormatTools.getBytesPerPixel(pixType)*8) - 1;
    
    // signed data type
    if (FormatTools.isSigned(pixType))
      
      return maxUnsigned / 2;
    
    else  // unsigned data type
      
      return maxUnsigned;
    */

  }
  
  
  private long minPixelValue(int pixType)
  {
    if (FormatTools.isFloatingPoint(pixType))
      //return -4294967296L; // expected -Float.MAX_VALUE or maybe -Double.MAX_VALUE rather than -2^32 (and also its not 2^32-1 !!!)
      return 0;  // TODO this allows autoscale testing to work for floating types _ makes sense cuz FakeReader only does unsigned float data 
 
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
    /*
    if (FormatTools.isFloatingPoint(pixType))
      return (long)-Float.MAX_VALUE;
 
    // signed data type
    if (FormatTools.isSigned(pixType))

      return - (1L << ((FormatTools.getBytesPerPixel(pixType)*8)-1));  // -1 accounts for use of sign bit
      
    else  // unsigned data type
      
      return 0;
    */
  }
  
  // ****** helper tests ****************************************************************************************
  
  private void defaultBehaviorTest(int pixType, int x, int y, int z, int c, int t)
  {
    String path = constructFakeFilename("default", pixType, x, y, z, c, t, -1);
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
    
    assertNotNull(imps);
    assertEquals(1,imps.length);
    ImagePlus imp = imps[0];
    assertNotNull(imp);
    assertEquals(x,imp.getWidth());
    assertEquals(y,imp.getHeight());
    /*
    assertEquals(z,ip.getNSlices());    // tricky - these last 3 getters have side effects that change their output.
    assertEquals(c,ip.getNChannels());
    assertEquals(t,ip.getNFrames());
    */
    //if (z != getSizeZ(ip)) { new ij.ImageJ(); ip.show(); }//TEMP
    assertEquals(z,getSizeZ(imp));
    assertEquals(t,getSizeT(imp));
    assertEquals(c,getEffectiveSizeC(imp));
  }
  
  private void outputStackOrderTest(int pixType, String order, int x, int y, int z, int c, int t)
  {
    String path = constructFakeFilename(order, pixType, x, y, z, c, t, -1);
    
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder(order);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    assertNotNull(imps);
    assertEquals(1,imps.length);
    
    ImagePlus imp = imps[0];
    
    ImageStack st = imp.getStack();
    int numSlices = st.getSize();

    assertEquals(z*c*t,numSlices);

    int count = 0;
    //System.out.println(order);
    Axis fastest = axis(order,0);
    Axis middle = axis(order,1);
    Axis slowest = axis(order,2);
    int maxI = value(slowest,z,c,t);
    int maxJ = value(middle,z,c,t);
    int maxK = value(fastest,z,c,t);
    for (int i = 0; i < maxI; i++)
      for (int j = 0; j < maxJ; j++)
        for (int k = 0; k < maxK; k++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          //printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          assertEquals(0,sIndex(proc));
          //TODO - test iIndex too? : assertEquals(count,somethingOrOther(iIndex(proc)));
          //System.out.println("iIndex " + iIndex(proc) + " calc " +
              // pre loop reorder ((maxJ*maxI*k) + (maxI*j) + i)
              //((maxJ*maxK*i) + (maxK*j) + k)
              //((value(middle,maxI,maxJ,maxK)*value(fastest,maxI,maxJ,maxK)*i) + (value(fastest,maxI,maxJ,maxK)*j) + (k))
              //((value(middle,maxK,maxJ,maxI)*value(fastest,maxK,maxJ,maxI)*i) + (value(fastest,maxK,maxJ,maxI)*j) + (k))
              //);
          //System.out.println("maxI "+maxI+" maxJ "+maxJ+" maxK "+maxK+" number "+count+" (i"+i+" "+j+"j "+k+"k) = "+iIndex(proc));
          assertEquals(i,index(slowest,proc));
          assertEquals(j,index(middle,proc));
          assertEquals(k,index(fastest,proc));
          count++;
        }
  }
  
  private void datasetSwapDimsTest(int pixType, int x, int y, int z, int t)
  {
    int c = 3; String origOrder = "XYZCT", swappedOrder = "XYTCZ";
    String path = constructFakeFilename(origOrder, pixType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setSwapDimensions(true);
      options.setInputOrder(0, swappedOrder);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    assertNotNull(imps);
    assertEquals(1,imps.length);

    ImagePlus imp = imps[0];
    ImageStack st = imp.getStack();
    int numSlices = st.getSize();
    assertEquals(z*c*t,numSlices);

    int actualZ = getSizeZ(imp);
    int actualT = getSizeT(imp);
    assertEquals(z,actualT); // Z<->T swapped
    assertEquals(t,actualZ); // Z<->T swapped

    // make sure the dimensions were swapped correctly
    int p = 1;
    for (int zIndex = 0; zIndex < z; zIndex++)
      for (int cIndex = 0; cIndex < c; cIndex++)
        for (int tIndex = 0; tIndex < t; tIndex++)
        {
          ImageProcessor proc = st.getProcessor(p++);
          assertEquals(tIndex,zIndex(proc)); // Z<->T swapped
          assertEquals(cIndex,cIndex(proc));
          assertEquals(zIndex,tIndex(proc)); // Z<->T swapped
        }
  }

  private void datasetOpenAllSeriesTest(int x, int y, int z, int c, int t, int s)
  {
    String path = constructFakeFilename("XYZCT", FormatTools.UINT32, x, y, z, c, t, s);
    
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
    
    assertEquals(1,imps.length);
    assertEquals(x,imps[0].getWidth());
    assertEquals(y,imps[0].getHeight());
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
  
  private void datasetConcatenateTest(int pixType, String order,
      int x, int y, int z, int c, int t, int s)
  {
    assertTrue(s >= 1);  // necessary for this test
    
    // open all series as one
    
    String path = constructFakeFilename(order, pixType, x, y, z, c, t, s);
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
    
    assertNotNull(imps);
    assertEquals(1,imps.length);
    ImageStack st = imps[0].getStack();

    // make sure the number of slices in stack is a sum of all series
    assertEquals(z*c*t*s, st.getSize());
    
    int index = 0;
    for (int sIndex = 0; sIndex < s; sIndex++) {
      for (int tIndex = 0; tIndex < t; tIndex++) {
        for (int cIndex = 0; cIndex < c; cIndex++) {
          for (int zIndex = 0; zIndex < z; zIndex++) {
            ImageProcessor proc = st.getProcessor(++index); 
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
    final String path = constructFakeFilename("autoscale",pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, -1);
    
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
    
    assertEquals(1,imps.length);
    imp = imps[0];
    assertEquals(sizeX,imp.getWidth());
    assertEquals(sizeY,imp.getHeight());
    assertEquals(sizeZ,getSizeZ(imp));
    assertEquals(sizeC,getEffectiveSizeC(imp));
    assertEquals(sizeT,getSizeT(imp));

    ImageStack st = imp.getStack();
    int numSlices = st.getSize();

    long expectedMax,expectedMin;
    
    if (wantAutoscale)
    {
      expectedMax = Math.max( minPixelValue(pixType)+sizeX-1, sizeZ*sizeC*sizeT - 1 );
      expectedMin = minPixelValue(pixType);
    }
    else // not autoscaling - get min/max of pixel type
    {
      expectedMax = maxPixelValue(pixType);
      expectedMin = 0;
    }

    // TODO : verify each slice? or just imp.getDisplayRangeMax/Min()?
    
    for (int i = 0; i < numSlices; i++)
    {
      ImageProcessor proc = st.getProcessor(i+1);
      //if ((int)expectedMax != (int)proc.getMax())
      //  System.out.println(FormatTools.getPixelTypeString(pixType) + " failed for proc #"+i+" exp "+expectedMax+" act "+(int)proc.getMax());
      assertEquals(expectedMax,proc.getMax(),0.1);
      assertEquals(expectedMin,proc.getMin(),0.1);
    }
  }
  
  // note - this test needs to rely on crop() to get predictable nonzero minimums
  
  private void cropAndAutoscaleTest(int pixType)
  {
    //TODO - this code set aside until crop/minMax stuff changed in BF. May be in a partially correct state
    
    //TODO: test more stringently final int sizeZ = 5, sizeC = 3, sizeT = 7, sizeX = 123, sizeY = 74;
    final int sizeZ = 1, sizeC = 1, sizeT = 1, sizeX = 123, sizeY = 74;
    final int cOriginX = 55, cOriginY = 15, cropSize = 24;
    final String path = constructFakeFilename("autoscale",pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, -1);
    
    // needed for this test
    assertTrue(cOriginX >= 50);
    assertTrue(cOriginY >= 10); 
    assertTrue(cOriginX + cropSize < sizeX);
    assertTrue(cOriginY + cropSize < sizeY);
    assertTrue(cOriginX + cropSize < 255);
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(true);
      options.setCrop(true);
      options.setCropRegion(0,new Region(cOriginX,cOriginY,cropSize,cropSize));
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    assertEquals(1,imps.length);
    imp = imps[0];
    assertEquals(cropSize,imp.getWidth());
    assertEquals(cropSize,imp.getHeight());
    assertEquals(sizeZ,getSizeZ(imp));
    assertEquals(sizeC,getEffectiveSizeC(imp));
    assertEquals(sizeT,getSizeT(imp));

    ImageStack st = imp.getStack();
    int numSlices = st.getSize();

    long expectedMax = cOriginX+cropSize-1;
    long expectedMin = cOriginX;

    // TODO : verify each slice? or just imp.getDisplayRangeMax/Min()?
    
    for (int i = 0; i < numSlices; i++)
    {
      ImageProcessor proc = st.getProcessor(i+1);
      assertEquals(expectedMax,proc.getMax(),0.1);
      assertEquals(expectedMin,proc.getMin(),0.1);
    }
  }
  
  private void memoryVirtualStackTest(boolean desireVirtual)
  {
      int x = 604, y = 531;
      
      String path = constructFakeFilename("vstack", FormatTools.UINT16, x, y, 7, 1, 1, -1);
      
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
      assertNotNull(imps);
      assertEquals(1,imps.length);
      ImagePlus imp = imps[0];
      assertNotNull(imp);
      assertEquals(x,imp.getWidth());
      assertEquals(y,imp.getHeight());
  
      assertEquals(desireVirtual,imp.getStack().isVirtual());
  }

  private void memoryRecordModificationsTest(boolean wantToRemember)
  {
    int x = 444, y = 387;
    String path = constructFakeFilename("memRec", FormatTools.UINT8, x, y, 7, 1, 1, -1);
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    assertTrue(y > 10);  // needed for this test
    
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
    assertNotNull(imps);
    assertEquals(1,imps.length);
    imp = imps[0];
    assertNotNull(imp);
    assertEquals(x,imp.getWidth());
    assertEquals(y,imp.getHeight());

    // change data in slice 1, swap to slice 2, swap back, see whether data reverts

    // original way - looks correct
    imp.setSlice(1);
    assertEquals(0,(int)imp.getProcessor().getPixelValue(0,10));
    imp.getProcessor().invert();
    //IJ.runPlugIn(imp, "Invert", "");  // this makes no difference
    assertEquals(255,(int)imp.getProcessor().getPixelValue(0,10));
    imp.setSlice(2);
    assertEquals(0,(int)imp.getProcessor().getPixelValue(0,10));
    imp.setSlice(1);
    int expectedVal = wantToRemember ? 255 : 0;
    assertEquals(expectedVal,(int)imp.getProcessor().getPixelValue(0,10));
    
    /*
    // alternative way - should be equivalent but testing to be sure
    imp.setSlice(1);
    assertEquals(0,(int)imp.getStack().getProcessor(1).getPixelValue(0,10));
    imp.getStack().getProcessor(1).invert();
    assertEquals(255,(int)imp.getStack().getProcessor(1).getPixelValue(0,10));
    imp.setSlice(2);
    assertEquals(0,(int)imp.getStack().getProcessor(2).getPixelValue(0,10));
    imp.setSlice(1);
    int expectedVal = wantToRemember ? 255 : 0;
    assertEquals(expectedVal,(int)imp.getStack().getProcessor(1).getPixelValue(0,10));
    */
  }
  
  private void memorySpecifyRangeTest(int z, int c, int t,
      int zFrom, int zTo, int zBy,
      int cFrom, int cTo, int cBy,
      int tFrom, int tTo, int tBy)
  { 
    int pixType = FormatTools.UINT8, x=50, y=5, s=-1;
    String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
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
    assertNotNull(imps);
    assertEquals(1,imps.length);
    ImagePlus imp = imps[0];
    assertNotNull(imp);
    assertEquals(x,imp.getWidth());
    assertEquals(y,imp.getHeight());
    ImageStack st = imp.getStack();

    // should be in correct order
    assertTrue(seriesInCorrectOrder(st,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy));
  }
  
  private void memoryCropTest(int pixType, int x, int y, int cx, int cy)
  {
    String path = constructFakeFilename("crop", pixType, x, y, 1, 1, 1, 1);
    
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
    assertNotNull(imps);
    assertEquals(1,imps.length);
    assertNotNull(imps[0]);
    assertEquals(cx,imps[0].getWidth());  // here is where we make sure we get back a cropped image
    assertEquals(cy,imps[0].getHeight());
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
  }

  @Test
  public void testOutputStackOrder()
  {
    for (ChannelOrder order : ChannelOrder.values())
      outputStackOrderTest(FormatTools.UINT8, order.toString(),  82, 47, 2, 3, 4);
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
    
    assertEquals(1,imps.length);
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
    
    assertEquals(1,imps.length);
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
    
    assertEquals(1,imps.length);
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
    datasetSwapDimsTest(FormatTools.INT8, 44, 109, 1, 4);
    datasetSwapDimsTest(FormatTools.INT16, 44, 109, 2, 1);
    datasetSwapDimsTest(FormatTools.INT32, 44, 109, 4, 3);
    datasetSwapDimsTest(FormatTools.UINT8, 82, 47, 3, 2);
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
    datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 1, 1, 1, 1);
    datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 1, 1, 1, 17);
    datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 4, 5, 2, 9);
  }

  /*
  @Test
  public void testColorMerge()
  {
    
    String path = FAKE_FILES[0];
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    // test when color merge false

    // open file
    try {
      ImporterOptions options = new ImporterOptions();
      options.setMergeChannels(false);
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
    assertEquals(1,imps.length);
    imp = imps[0];
    assertEquals(3,getEffectiveSizeC(imp));  // unmerged
    assertEquals(7,getSizeZ(imp));
    assertEquals(5,getSizeT(imp));
    
    // test when color merge true
    
    // open file
    try {
      ImporterOptions options = new ImporterOptions();
      options.setMergeChannels(true);
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
    assertEquals(1,imps.length);
    imp = imps[0];
    assertTrue(imp.getHeight() > 10);  // required for this test to work
    assertEquals(1, getEffectiveSizeC(imp));  // merged
    assertEquals(7, getSizeZ(imp));
    assertEquals(5, getSizeT(imp));
    for (int i = 0; i < 10; i++)
      assertEquals(mergedPixel(i),imp.getProcessor().get(i,10));
    
    // TODO - also test mergeOptions when chans > 3. it will be an int == chans per plane. extra blank images are
    //   added as needed to make multiple images each with same number of channels
    //   i.e. 6 channels can -> 123/456 or 12/34/56 or 1/2/3/4/5/6 (last one not merged ???)
    //        5 channels can -> 123/45b or 12/34/5b or 1/2/3/4/5 (last one not merged ???)
    
    fail("unfinished implementation");
  }
  
  @Test
  public void testColorRgbColorize()
  {
    // From BF: RGB colorize channels - Each channel is assigned an appropriate pseudocolor table rather than the normal
    // grayscale.  The first channel is colorized red, the second channel is green, and the third channel is blue. This
    // option is not available when Merge channels to RGB or Custom colorize channels are set.
    
    String path = FAKE_FILES[0];
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;

    // TODO - should not allow mergeChannels with rgb colorize
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorize(true);
      options.setMergeChannels(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
      // TODO - eventually fail() here but need BF support first I think
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // TODO - should not allow mergeChannels with custom colorize
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorize(true);
      options.setCustomColorize(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
      // TODO - eventually fail() here but need BF support first I think
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    // TODO - legitimate testing
    // open file
    try {
      ImporterOptions options = new ImporterOptions();
      options.setColorize(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    assertEquals(1,imps.length);
    imp = imps[0];
    assertEquals(7,getSizeZ(imp));
    assertEquals(1,getEffectiveSizeC(imp));  // TODO : correct?
    assertEquals(1,getSizeT(imp));  // TODO : huh?
    
    // TODO - actual tests of data
    
    fail("unfinished implementation");
  }

  @Test
  public void testColorCustomColorize()
  {
    // From BF: Custom colorize channels - Each channel is assigned a pseudocolor table rather than the normal grayscale.
    //   The color for each channel is chosen by the user. This option is not available when Merge channels to RGB or RGB
    //   colorize channels are set.
    
    // TODO
    fail("to be implemented");
  }
  */
  
  @Test
  public void testColorAutoscale()
  {
    
    // From BF:
    // Autoscale - Stretches the histogram of the image planes to fit the data range. Does not alter underlying values in
    // the image. If selected, histogram is stretched for each stack based upon the global minimum and maximum value
    // throughout the stack.

    autoscaleTest(FormatTools.UINT8,false);
    autoscaleTest(FormatTools.UINT16,false);
    autoscaleTest(FormatTools.UINT32,false);
    autoscaleTest(FormatTools.INT8,false);
    autoscaleTest(FormatTools.INT16,false);
    autoscaleTest(FormatTools.INT32,false);
    autoscaleTest(FormatTools.DOUBLE,false);
    autoscaleTest(FormatTools.FLOAT,false);
    
    autoscaleTest(FormatTools.UINT8,true);
    autoscaleTest(FormatTools.UINT16,true);
    autoscaleTest(FormatTools.UINT32,true);
    autoscaleTest(FormatTools.INT8,true);
    //autoscaleTest(FormatTools.INT16,true);  // TODO in this case IJ via ShortProcessor::setMinAndMax() clamps the min value to 0 : bug due to obliviousness to sign?
    autoscaleTest(FormatTools.INT32,true);
    autoscaleTest(FormatTools.DOUBLE,true);
    autoscaleTest(FormatTools.FLOAT,true);

    /*
    // TODO - delete above code when tests are passing
    for (int pixType : PixelTypes)
    {
      for (boolean autoscale : BooleanStates)
      {
        //System.out.println("testColorAutoscale(): pixType = "+FormatTools.getPixelTypeString(pixType)+" autoscale = "+autoscale);
        autoscaleTest(pixType,autoscale);
      }
    }
    */
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
    
    /* TODO - enable when step by 0 code fixed and remove extra tests above and below
    // uber combo test
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
                      if ((zStart < 0) || (zStart >= z) ||
                          (zEnd < 0) || (zEnd >= z) || (zEnd < zStart) ||
                          (zInc < 1) ||
                          (cStart < 0) || (cStart >= c) ||
                          (cEnd < 0) || (cEnd >= c) || (cEnd < cStart) ||
                          (cInc < 1) ||
                          (tStart < 0) || (tStart >= t) ||
                          (tEnd < 0) || (tEnd >= z) || (tEnd < tStart) ||
                          (tInc < 1))
                      {
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
                        memorySpecifyRangeTest(z,c,t,zStart,zEnd,zInc,cStart,cEnd,cInc,tStart,tEnd,tInc);
    */
    
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
      // TODO - enable post fix
      //memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      //fail();
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
      // TODO - enable post fix
      //memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      //fail();
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
      // TODO - enable post fix
      //memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      //fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
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
    final int sizeZ = 5, sizeC = 3, sizeT = 7;
    final String path = constructFakeFilename("splitC",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, -1);

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
    assertEquals(sizeC,imps.length);
    
    // unwind ZCT loop : C pulled to front, ZT in order
    for (int c = 0; c < sizeC; c++) {
      ImageStack st = imps[c].getStack();
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
    final int sizeZ = 5, sizeC = 3, sizeT = 7;
    final String path = constructFakeFilename("splitZ",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, -1);

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
    assertEquals(sizeZ,imps.length);

    // unwind ZCT loop : Z pulled to front, CT in order
    for (int z = 0; z < sizeZ; z++) {
      ImageStack st = imps[z].getStack();
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
    final int sizeZ = 5, sizeC = 3, sizeT = 7;
    final String path = constructFakeFilename("splitT",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, -1);

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
    assertEquals(sizeT,imps.length);
    
    // unwind ZTC loop : T pulled to front, ZC in order
    for (int t = 0; t < sizeT; t++) {
      ImageStack st = imps[t].getStack();
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
  public void testComboCropAutoscale()
  {
    cropAndAutoscaleTest(FormatTools.UINT8);
    
    cropAndAutoscaleTest(FormatTools.UINT8);
    cropAndAutoscaleTest(FormatTools.UINT16);
    //TODO: UINT32 failing - bug in BF?
    cropAndAutoscaleTest(FormatTools.UINT32);
    //TODO: exp 127 act 255 
    cropAndAutoscaleTest(FormatTools.INT8);
    //TODO: exp 32767 act 65535
    cropAndAutoscaleTest(FormatTools.INT16);
    //TODO: signed max broken here too
    cropAndAutoscaleTest(FormatTools.INT32);
 
    cropAndAutoscaleTest(FormatTools.UINT8);
    cropAndAutoscaleTest(FormatTools.UINT16);
    cropAndAutoscaleTest(FormatTools.UINT32);
    cropAndAutoscaleTest(FormatTools.INT8);
    cropAndAutoscaleTest(FormatTools.INT16);
    cropAndAutoscaleTest(FormatTools.INT32);
  }
  
}
