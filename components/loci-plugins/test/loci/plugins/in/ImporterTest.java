//
// ImporterTest.java
//

package loci.plugins.in;

import static org.junit.Assert.assertEquals; 
import static org.junit.Assert.assertArrayEquals; 
import static org.junit.Assert.assertNotNull; 
import static org.junit.Assert.assertTrue; 
import static org.junit.Assert.fail; 

import java.io.IOException;
import java.lang.reflect.Field;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import loci.common.Location;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.BF;

import org.junit.Test;

// TODO
//  - flesh out existing tests
//      splits - see if for loops are in correct order by comparing to actual data
//      write tests for the color options : 4 cases - some mention was made that indexcolor is an issue in testing
//        merge - basic test in place but not passing. need to flesh out mergeOptions when BF code in place.
//        rgb colorize - 
//        custom colorize - 
//        autoscale - code stubbed out but tests not in place for histogram testing. Its possible the histogram won't
//          change when base image is a fake file because the whole data range may already be in use.
//      open individual files: try to come up with a way to test without a disk file as source
//      concatenate - test order of images in stack?
//      swapped dims test needs to test cases other than from default swapping Z & T
//      lowest priority - record modifications
//      output stack order - testing of iIndex?
//      range - more combos of ztc
//  - add some tests for combination of options

public class ImporterTest {

  private enum Axis {Z,C,T};
  
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
    if (sizeX < 41)
      throw new IllegalArgumentException("constructFakeFilename() - width < 41 : can break some of our tests");
    
    String fileName = "";
    
    fileName += title;
    
    fileName += "&pixelType=" + FormatTools.getPixelTypeString(pixelType);
    
    fileName += "&sizeX=" + sizeX;
    
    fileName += "&sizeY=" + sizeY;
    
    fileName += "&sizeZ=" + sizeZ;

    fileName += "&sizeC=" + sizeC;
 
    fileName += "&sizeT=" + sizeT;

    if (numSeries > 0)
      fileName += "&series=" + numSeries;
    
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
  }
  
  // note : for now assumes default ZCT ordering
  
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
    for (int k = tFrom; k <= tTo; k += tBy)
      for (int j = cFrom; j <= cTo; j += cBy)
        for (int i = zFrom; i <= zTo; i += zBy)
        {
          ImageProcessor proc = st.getProcessor(procNum);
          if ((zIndex(proc) != i) || (cIndex(proc) != j) || (tIndex(proc) != k))
          {
            System.out.println("seriesInCorrectOrder() - slices out of order: exp i"+i+" j"+j+" k"+k+" != act z"+
                zIndex(proc)+" c"+cIndex(proc)+" t"+tIndex(proc)+" for proc number "+procNum);
            return false;
          }
          procNum++;
        }
    
    return true;
  }
  
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
  
  private int getSizeZ(ImagePlus imp) { return getField(imp, "nSlices"); }
  private int getSizeT(ImagePlus imp) { return getField(imp, "nFrames"); }
  private int getEffectiveSizeC(ImagePlus imp) { return getField(imp, "nChannels"); }

  // used by the color merge code. calcs a pixel value in our ramped data for a 3 channel merged image
  private int mergedPixel(int i)
  {
    if ((i < 0) || (i > 15))
      throw new IllegalArgumentException("mergedPixel() can only handle 1st 16 cases. Wants 0<=i<=15 but i = " + i);
    
    return i*65536 + i*256 + i;
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
    int maxI = value(fastest,z,c,t);
    int maxJ = value(middle,z,c,t);
    int maxK = value(slowest,z,c,t);
    for (int k = 0; k < maxK; k++)
      for (int j = 0; j < maxJ; j++)
        for (int i = 0; i < maxI; i++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          //printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          assertEquals(0,sIndex(proc));
          //TODO - test iIndex too? : assertEquals(count,somethingOrOther(iIndex(proc)));
          //System.out.println("iIndex " + iIndex(proc) + " calc " +
          //    ((maxJ*maxI*k) + (maxI*j) + i)
          //    );
          assertEquals(i,index(fastest,proc));
          assertEquals(j,index(middle,proc));
          assertEquals(k,index(slowest,proc));
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
    // notice I'm testing from inside out in ZCT order
    // but using input z for T and the input t for Z
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
    assertTrue(s >= 1);
    
    String path = constructFakeFilename(order, pixType, x, y, z, c, t, s);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setConcatenate(true);
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

    ImageStack st = imps[0].getStack();
 
    int numSlices = st.getSize();
    
    // make sure the number of slices in stack is a sum of all series
    assertEquals(z*c*t*s,numSlices);
    
    // System.out.println("Numslices == " + numSlices);
    for (int i = 0; i < numSlices; i++)
    {
      ImageProcessor proc = st.getProcessor(i+1); 
      // printVals(proc);
      assertEquals(0,sIndex(proc));  // make sure we have one series only
      // TODO - do we need to test something regarding order of images in series
    }
  }
  
  private void memoryVirtualStackTest(boolean desireVirtual)
  {
      int x = 604, y = 531;
      String path = constructFakeFilename("vstack", FormatTools.UINT16, x, y, 7, 1, 1, -1);
      ImagePlus[] imps = null;
      try {
        ImporterOptions options = new ImporterOptions();
        options.setId(path);
        options.setVirtual(desireVirtual);
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
      assertNotNull(imp);
      assertEquals(x,imp.getWidth());
      assertEquals(y,imp.getHeight());
  
      assertEquals(desireVirtual,imp.getStack().isVirtual());
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
    
    //System.out.println("SpecifyCRangeTest: slices below");
    //for (int i = 0; i < numSlices; i++)
    //  printVals(st.getProcessor(i+1));
        
    assertTrue(seriesInCorrectOrder(st,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy));
  }
  
  private void memoryCropTest(int pixType, int x, int y, int cx, int cy)
  {
    String path = constructFakeFilename("crop", pixType, x, y, 1, 1, 1, 1);
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

    assertNotNull(imps);
    assertEquals(1,imps.length);
    assertNotNull(imps[0]);
    assertEquals(cx,imps[0].getWidth());
    assertEquals(cy,imps[0].getHeight());
  }

// ** ImporterTest methods **************************************************************

  @Test
  public void testDefaultBehavior()
  {
    defaultBehaviorTest(FormatTools.UINT16, 400, 300, 1, 1, 1);
    defaultBehaviorTest(FormatTools.INT16, 107, 414, 1, 1, 1);
    defaultBehaviorTest(FormatTools.UINT32, 323, 206, 3, 2, 1);  // failure on last val = 1,5,
    defaultBehaviorTest(FormatTools.UINT8, 57, 78, 5, 4, 3);
    defaultBehaviorTest(FormatTools.INT32, 158, 99, 2, 3, 4);
    defaultBehaviorTest(FormatTools.INT8, 232, 153, 3, 7, 5);
  }

  @Test
  public void testOutputStackOrder()
  {
    outputStackOrderTest(FormatTools.UINT8, "XYZCT", 82, 47, 2, 3, 4);
    outputStackOrderTest(FormatTools.UINT8, "XYZTC", 82, 47, 2, 3, 4);
    outputStackOrderTest(FormatTools.UINT8, "XYCZT", 82, 47, 2, 3, 4);
    outputStackOrderTest(FormatTools.UINT8, "XYCTZ", 82, 47, 2, 3, 4);
    outputStackOrderTest(FormatTools.UINT8, "XYTCZ", 82, 47, 2, 3, 4);
    outputStackOrderTest(FormatTools.UINT8, "XYTZC", 82, 47, 2, 3, 4);
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
    assertEquals(16,imps[0].getStack().getSize());
    
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
    assertEquals(32,imps[0].getStack().getSize());
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

  @Test
  public void testColorMerge()
  {
    
    String path = FAKE_FILES[0];
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    
    // test when color merge false

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
    
    assertEquals(1,imps.length);
    imp = imps[0];
    assertEquals(3,getEffectiveSizeC(imp));
    assertEquals(7,getSizeZ(imp));
    assertEquals(5,getSizeT(imp));
    
    // test when color merge true
    
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
    
    assertEquals(1,imps.length);
    imp = imps[0];
    assertEquals(1, getEffectiveSizeC(imp));
    assertEquals(7, getSizeZ(imp));
    assertEquals(5, getSizeT(imp));
    assertTrue(imp.getHeight() > 10);  // required for this test to work
    for (int i = 0; i < 10; i++)
      assertEquals(mergedPixel(i),imp.getProcessor().get(i,10));
    
    // TODO - also test mergeOptions when chans > 3. it will be an int == chans per plane. extra blank images are
    //   added as needed to make multiple images each with same number of channels
    //   i.e. 6 channels can -> 123/456 or 12/34/56 or 1/2/3/4/5/6 (last one not merged ???)
    //        5 channels can -> 123/45b or 12/34/5b or 1/2/3/4/5 (last one not merged ???)
  }
  
  @Test
  public void testColorRgbColorize()
  {
    // From BF: RGB colorize channels - Each channel is assigned an appropriate pseudocolor table rather than the normal
    // grayscale.  The first channel is colorized red, the second channel is green, and the third channel is blue. This
    // option is not available when Merge channels to RGB or Custom colorize channels are set.
    fail("to be implemented");
  }

  @Test
  public void testColorCustomColorize()
  {
    // From BF: Custom colorize channels - Each channel is assigned a pseudocolor table rather than the normal grayscale.
    //   The color for each channel is chosen by the user. This option is not available when Merge channels to RGB or RGB
    //   colorize channels are set.
    fail("to be implemented");
  }

  @Test
  public void testColorAutoscale()
  {
    // From BF:
    // Autoscale - Stretches the histogram of the image planes to fit the data range. Does not alter underlying values in
    // the image. If selected, histogram is stretched for each stack based upon the global minimum and maximum value
    // throughout the stack.

    // histogram stretched to match data vals that are present
    // original image data unchanged

    String path = FAKE_FILES[0];
    
    ImagePlus[] imps = null;
    ImagePlus imp = null;
    int[] h;
    
    // test when autoscale false

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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
    assertEquals(3,getEffectiveSizeC(imp));
    assertEquals(7,getSizeZ(imp));
    assertEquals(5,getSizeT(imp));
    
    System.out.println("setAutoscale(false) results");
    h = imp.getStatistics().histogram;
    for (int i = 0; i < h.length/8; i++)
      System.out.println(h[i*8+0]+" "+h[i*8+1]+" "+h[i*8+2]+" "+h[i*8+3]+" "+h[i*8+4]+" "+h[i*8+5]+" "+h[i*8+6]+" "+h[i*8+7]);
    
    // TODO - test histogram values
    
    ImagePlus baseImage = imp;
    
    // test when autoscale true

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(true);
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
    assertEquals(3,getEffectiveSizeC(imp));
    assertEquals(7,getSizeZ(imp));
    assertEquals(5,getSizeT(imp));
    
    System.out.println("setAutoscale(true) results");
    h = imp.getStatistics().histogram;
    for (int i = 0; i < h.length/8; i++)
      System.out.println(h[i*8+0]+" "+h[i*8+1]+" "+h[i*8+2]+" "+h[i*8+3]+" "+h[i*8+4]+" "+h[i*8+5]+" "+h[i*8+6]+" "+h[i*8+7]);
    
    // TODO - test histogram values

    // test that image data unchanged by autoscale
    
    ImageStack st1 = baseImage.getStack();
    ImageStack st2 = imp.getStack();
    
    int st1Size = st1.getSize();
    assertEquals(st1Size,st2.getSize());
    for (int i = 0; i < st1Size; i++)
      assertEquals(st1.getProcessor(i+1).getPixels(), st2.getProcessor(i+1).getPixels());
      // TODO - might need assertArrayEquals() and cast the returned pixels to the appropriate array type.
      //    what is the file type for fakefiles when not specified? byte or int?
    
    fail("to be implemented");
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
    // TODO - how to test this? lowest priority
    
    // outline
    //   open as virt stack
    //   run plugin (invert) or macro that changes pixels in currently loaded frame (frame 0)
    //   change curr frame to 1 and test that pixels are different from what we set
    //   change curr frame back to 0 and see if pixel changes remembered
    
    fail("to be implemented");
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

    // test bad combination of zct's - choosing beyond end of z's
    try {
      z=7; c=7; t=7; zFrom=3; zTo=7; zBy=4; cFrom=0; cTo=6; cBy=1; tFrom=0; tTo=6; tBy=1;
      memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
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
    String path = FAKE_FILES[0];

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

    // 3 channels goes to 3 images
    assertEquals(3,imps.length);
    
    // TODO - order of for loops correct?
    for (int k = 0; k < 5; k++)
      for (int j = 0; j < 3; j++)
        for (int i = 0; i < 7; i++)
        {
          // these next three statements called more times than needed but simplifies for loop logic
          ImageStack st = imps[j].getStack();
          assertEquals(35,st.getSize());
          ImageProcessor proc = st.getProcessor(j+1);
          // test the values
          assertEquals(i,zIndex(proc));
          assertEquals(0,cIndex(proc));  // this one should always be 0
          assertEquals(k,tIndex(proc));
        }
  }
  
  @Test
  public void testSplitFocalPlanes()
  {
    String path = FAKE_FILES[0];

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
    
    // 7 planes goes to 7 images
    assertEquals(7,imps.length);
    
    // TODO - order of for loops correct?
    for (int k = 0; k < 5; k++)
      for (int j = 0; j < 3; j++)
        for (int i = 0; i < 7; i++)
        {
          // these next three statements called more times than needed but simplifies for loop logic
          ImageStack st = imps[i].getStack();
          assertEquals(15,st.getSize());
          ImageProcessor proc = st.getProcessor(i+1);
          // test the values
          assertEquals(0,zIndex(proc));  // this one should always be 0
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
        }
  }
  
  @Test
  public void testSplitTimepoints()
  {
    String path = FAKE_FILES[0];

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
    
    // 5 time points goes to 5 images
    assertEquals(5,imps.length);
    
    // TODO - order of for loops correct?
    for (int k = 0; k < 5; k++)
      for (int j = 0; j < 3; j++)
        for (int i = 0; i < 7; i++)
        {
          // these next three statements called more times than needed but simplifies for loop logic
          ImageStack st = imps[k].getStack();
          assertEquals(21,st.getSize());
          ImageProcessor proc = st.getProcessor(k+1);
          // test the values
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(0,tIndex(proc));  // this one should always be 0
        }
  }

}
