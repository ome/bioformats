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
//  - figure out what openIndiv is supposed to do and also where it fits in priorities
//  - flesh out existing tests
//      groups files test needs to be written/debugged
//      write test for open indiv files : getting dataset from Melissa. Priority may be wrong.
//      write tests for split options : 3 cases
//      write tests for the color options : 4 cases
//      concat follows - see my test to see if sufficient
//      lowest priority - record modifications
//  - add some tests for combination of options

public class ImporterTest {

  private enum Axis {Z,C,T};
  
  // ** Helper methods *******************************************************************

  private String constructFakeFilename(String title,
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

  private int sIndex(ImageProcessor proc) { return proc.get(0,0);  }  // series
  private int iIndex(ImageProcessor proc) { return proc.get(10,0); }  // num in series
  private int zIndex(ImageProcessor proc) { return proc.get(20,0); }  // z
  private int cIndex(ImageProcessor proc) { return proc.get(30,0); }  // c
  private int tIndex(ImageProcessor proc) { return proc.get(40,0); }  // t
  
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
    ImagePlus ip = imps[0];
    assertNotNull(ip);
    assertEquals(x,ip.getWidth());
    assertEquals(y,ip.getHeight());
    /*
    assertEquals(z,ip.getNSlices());    // tricky - these last 3 getters have side effects that change their output.
    assertEquals(c,ip.getNChannels());
    assertEquals(t,ip.getNFrames());
    */
    //if (z != getSizeZ(ip)) { new ij.ImageJ(); ip.show(); }//TEMP
    assertEquals(z,getSizeZ(ip));
    assertEquals(t,getSizeT(ip));
    assertEquals(c,getEffectiveSizeC(ip));
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
    
    ImagePlus ip = imps[0];
    
    ImageStack st = ip.getStack();
    int numSlices = st.getSize();

    assertEquals(z*c*t,numSlices);

    int count = 0;
    //if (debug)
    //  System.out.println(order);
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
          //if (debug)
          //  printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          assertEquals(0,sIndex(proc));
          //test iIndex too? : assertEquals(count,somethingOrOther(iIndex(proc)));
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
    int c = 3; String order = "XYZCT";
    String path = constructFakeFilename(order, pixType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setSwapDimensions(true);
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
    assertEquals(z*c*t,numSlices);

    int maxZ = -1;
    int maxT = -1;
    int tmp;
    for (int i = 0; i < numSlices; i++)
    {
      ImageProcessor proc = st.getProcessor(i+1);
      printVals(proc);
      tmp = zIndex(proc)+1;
      if (maxZ < tmp) maxZ = tmp;
      tmp = tIndex(proc)+1;
      if (maxT < tmp) maxT = tmp;
    }
    assertEquals(z,maxT);
    assertEquals(t,maxZ);
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
      assertTrue(imps.length == 1);
      ImagePlus ip = imps[0];
      assertNotNull(ip);
      assertTrue(ip.getWidth() == x);
      assertTrue(ip.getHeight() == y);
  
      assertEquals(desireVirtual,ip.getStack().isVirtual());
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
      // only set z if nondefault behavior specified
      if ((zFrom != 0) || (zTo != z-1) || (zBy != 1))
      {
        options.setZBegin(0, zFrom);
        options.setZEnd(0, zTo);
        options.setZStep(0, zBy);
      }
      // only set c if nondefault behavior specified
      if ((cFrom != 0) || (cTo != c-1) || (cBy != 1))
      {
        options.setCBegin(0, cFrom);
        options.setCEnd(0, cTo);
        options.setCStep(0, cBy);
      }
      // only set t if nondefault behavior specified
      if ((tFrom != 0) || (tTo != t-1) || (tBy != 1))
      {
        options.setTBegin(0, tFrom);
        options.setTEnd(0, tTo);
        options.setTStep(0, tBy);
      }
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
    assertTrue(imps.length == 1);
    ImagePlus ip = imps[0];
    assertNotNull(ip);
    assertTrue(ip.getWidth() == x);
    assertTrue(ip.getHeight() == y);
    ImageStack st = ip.getStack();
    
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
    assertTrue(imps.length == 1);
    assertNotNull(imps[0]);
    assertTrue(imps[0].getWidth() == cx);
    assertTrue(imps[0].getHeight() == cy);
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
    String[] files = {
        "test_C1_TP1&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C2_TP1&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C3_TP1&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C1_TP2&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C2_TP2&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C3_TP2&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C1_TP3&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C2_TP3&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C3_TP3&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C1_TP4&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C2_TP4&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C3_TP4&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C1_TP5&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C2_TP5&sizeX=50&sizeY=20&sizeZ=7.fake",
        "test_C3_TP5&sizeX=50&sizeY=20&sizeZ=7.fake",
        "outlier.txt"
      };
    
    for (String file : files)
      Location.mapId(file, "iThinkI'mImportantButI'mNot");

    String path = "test_C1_TP1&sizeX=50&sizeY=20&sizeZ=7.fake";

    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setGroupFiles(true);
      options.setId(path);
      imps = BF.openImagePlus(options);
      // TODO - assertEquals("test_C<1-3>_TP<1-5>&sizeX=50&sizeY=20&sizeZ=7.fake",options.getId());
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
    // TODO - can't really test this with fake files. It needs a series of files from grouping
    //   to reorder.
    
    // TODO - Curtis says I should be able to test this without grouping
    
    datasetSwapDimsTest(FormatTools.UINT8, 82, 47, 1, 3);
    datasetSwapDimsTest(FormatTools.UINT16, 82, 47, 3, 1);
    datasetSwapDimsTest(FormatTools.UINT32, 82, 47, 5, 2);
    datasetSwapDimsTest(FormatTools.INT8, 44, 109, 1, 4);
    datasetSwapDimsTest(FormatTools.INT16, 44, 109, 2, 1);
    datasetSwapDimsTest(FormatTools.INT32, 44, 109, 4, 3);
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
    
    // TODO - Curtis says impl broken right now - will test later

    // open a dataset that has multiple series and should get back a single series
    datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 1, 1, 1, 1);
    datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 1, 1, 1, 17);
    datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 4, 5, 2, 9);
  }
  
  @Test
  public void testColorMerge()
  {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testColorRgbColorize()
  {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testColorCustomColorize()
  {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testColorAutoscale()
  {
    // TODO - Curtis says impl broken right now - will test later
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
  }
  
  @Test
  public void testMemorySpecifyRange()
  {
    int z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy;
    
    // test z
    z=8; c=3; t=2; zFrom=2; zTo=7; zBy=3; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test c
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=12; cBy=4; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test t
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=4; tTo=13; tBy=2;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // test a combination of zct's
    z=5; c=4; t=6; zFrom=1; zTo=4; zBy=2; cFrom=1; cTo=3; cBy=1; tFrom=2; tTo=6; tBy=2;
    memorySpecifyRangeTest(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
    
    // TODO
    //  1) more combos
    //  2) noticed I am always setting from/to/by at the same time. test setting only one at a time (pass -1 to ignore)
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
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testSplitFocalPlanes()
  {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testSplitTimepoints()
  {
    // TODO - Curtis says impl broken right now - will test later
  }

  // ** Main method *****************************************************************

  public static void main(String[] args)
  {
    ImporterTest tester = new ImporterTest();
 
    //TODO - we could use reflection to discover all test methods, loop, and run them
  
    // tests of single features
    tester.testDefaultBehavior();
    tester.testOutputStackOrder();
    tester.testDatasetGroupFiles();
    tester.testDatasetOpenFilesIndividually();
    tester.testDatasetSwapDims();
    tester.testDatasetOpenAllSeries();
    tester.testDatasetConcatenate();
    tester.testColorMerge();
    tester.testColorRgbColorize();
    tester.testColorCustomColorize();
    tester.testColorAutoscale();
    tester.testMemoryVirtualStack();
    tester.testMemoryRecordModifications();
    tester.testMemorySpecifyRange();
    tester.testMemoryCrop();
    tester.testSplitChannels();
    tester.testSplitFocalPlanes();
    tester.testSplitTimepoints();
    
    // TODO - add tests involving combinations of features
    
    System.exit(0);
  }
}


/*  old stuff - keep until replacement code tested

  private int numPresent(ImageStack st, Axis axis)
  {
    List<Integer> indices = new ArrayList<Integer>();
    
    int count = 0;
    for (int i = 0; i < st.getSize(); i++)
    {
      int currVal = this.index(axis, st.getProcessor(i+1));

      //fails
      // (!indices.contains(new Integer(currVal)))
      //
      //  indices.add(new Integer(currVal));
      //  count++;
      //}

      boolean found = false;
      for (int j = 0; j < indices.size(); j++)
        if (currVal == indices.get(j))
        {
          found = true;
          break;
        }
      if (!found)
      {
        count++;
        indices.add(currVal);
      }
    }
    return count;
  }
  
private void memorySpecifyZRangeTest()
{ 
  int pixType = FormatTools.UINT8, x=30, y=30, z=6, c=2, t=4, s=-1;
  int from = 1, to = 5, by = 2;
  String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
  ImagePlus[] imps = null;
  try {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    options.setZBegin(0, from);
    options.setZEnd(0, to);
    options.setZStep(0, by);
    imps = BF.openImagePlus(options);
  }
  catch (IOException e) {
    fail(e.getMessage());
  }
  catch (FormatException e) {
    fail(e.getMessage());
  }
  
  // should have the data: one series, all t's, all c's, z's from 1 to 5 by 2
  assertNotNull(imps);
  assertTrue(imps.length == 1);
  ImagePlus ip = imps[0];
  assertNotNull(ip);
  assertTrue(ip.getWidth() == x);
  assertTrue(ip.getHeight() == y);
  ImageStack st = ip.getStack();
  int numSlices = st.getSize();
  assertEquals(numInSeries(from,to,by)*c*t,numSlices);

  System.out.println("SpecifyZRangeTest: slices below");
  for (int i = 0; i < numSlices; i++)
  {
    ImageProcessor proc = st.getProcessor(i+1); 
    printVals(proc);
  }
  
  // all t's present
  //assertEquals(numInSeries(1,t,1), numPresent(st,Axis.T));
  
  // all c's present
  //assertEquals(numInSeries(1,c,1), numPresent(st,Axis.C));

  // only specific z's present
  //assertEquals(numInSeries(from,to,by), numPresent(st,Axis.Z));
  
  assertTrue(seriesInCorrectOrder(st,from,to,by,0,c-1,1,0,t-1,1));
}

private void memorySpecifyCRangeTest()
{ 
  int pixType = FormatTools.UINT8, x=30, y=30, z=4, c=11, t=4, s=-1;
  int from = 3, to = 9, by = 3;
  String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
  ImagePlus[] imps = null;
  try {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    options.setCBegin(0, from);
    options.setCEnd(0, to);
    options.setCStep(0, by);
    imps = BF.openImagePlus(options);
  }
  catch (IOException e) {
    fail(e.getMessage());
  }
  catch (FormatException e) {
    fail(e.getMessage());
  }
  
  // should have the data: one series, all t's, all z's, c's from 3 to 9 by 3
  assertNotNull(imps);
  assertTrue(imps.length == 1);
  ImagePlus ip = imps[0];
  assertNotNull(ip);
  assertTrue(ip.getWidth() == x);
  assertTrue(ip.getHeight() == y);
  ImageStack st = ip.getStack();
  int numSlices = st.getSize();
  assertEquals(z*numInSeries(from,to,by)*t,numSlices);
  //System.out.println("SpecifyCRangeTest: slices below");
  //for (int i = 0; i < numSlices; i++)
  //  printVals(st.getProcessor(i+1));
      
  // all t's present
  //assertEquals(numInSeries(1,t,1), numPresent(st,Axis.T));
  
  // all z's present
  //assertEquals(numInSeries(1,z,1), numPresent(st,Axis.Z));

  // only specific c's present
  //assertEquals(numInSeries(from,to,by), numPresent(st,Axis.C));

  assertTrue(seriesInCorrectOrder(st,0,z-1,1,from,to,by,0,t-1,1));
}

private void memorySpecifyTRangeTest()
{ 
  int pixType = FormatTools.UINT8, x=30, y=30, z=3, c=2, t=12, s=-1;
  int from = 1, to = 10, by = 4;
  String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
  ImagePlus[] imps = null;
  try {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    options.setTBegin(0, from);
    options.setTEnd(0, to);
    options.setTStep(0, by);
    imps = BF.openImagePlus(options);
  }
  catch (IOException e) {
    fail(e.getMessage());
  }
  catch (FormatException e) {
    fail(e.getMessage());
  }
  
  // should have the data: one series, all z's, all c's, t's from 1 to 10 by 4
  assertNotNull(imps);
  assertTrue(imps.length == 1);
  ImagePlus ip = imps[0];
  assertNotNull(ip);
  assertTrue(ip.getWidth() == x);
  assertTrue(ip.getHeight() == y);
  ImageStack st = ip.getStack();
  int numSlices = st.getSize();
  assertEquals(z*c*numInSeries(from,to,by),numSlices);
  //System.out.println("SpecifyTRangeTest: slices below");
  //for (int i = 0; i < numSlices; i++)
  //  printVals(st.getProcessor(i+1));
  
  // all z's present
  //assertEquals(numInSeries(1,z,1), numPresent(st,Axis.Z));
  
  // all c's present
  //assertEquals(numInSeries(1,c,1), numPresent(st,Axis.C));

  // only specific t's present
  //assertEquals(numInSeries(from,to,by), numPresent(st,Axis.T));
  
  assertTrue(seriesInCorrectOrder(st,0,z-1,1,0,c-1,1,from,to,by));
}

*/

/*  notes

 public static void main(String[] args) throws FormatException, IOException {
   String[] files = {
     "test_C1_TP1&sizeZ=7.fake",
     "test_C2_TP1&sizeZ=7.fake",
     "test_C3_TP1&sizeZ=7.fake",
     "test_C1_TP2&sizeZ=7.fake",
     "test_C2_TP2&sizeZ=7.fake",
     "test_C3_TP2&sizeZ=7.fake",
     "test_C1_TP3&sizeZ=7.fake",
     "test_C2_TP3&sizeZ=7.fake",
     "test_C3_TP3&sizeZ=7.fake",
     "test_C1_TP4&sizeZ=7.fake",
     "test_C2_TP4&sizeZ=7.fake",
     "test_C3_TP4&sizeZ=7.fake",
     "test_C1_TP5&sizeZ=7.fake",
     "test_C2_TP5&sizeZ=7.fake",
     "test_C3_TP5&sizeZ=7.fake",
     "outlier.txt"
   };
   for (String file : files) Location.mapId(file, "x"); // "x" is irrelevant

   String id = files[0];
   IFormatReader reader = new FileStitcher();
   IMetadata meta = MetadataTools.createOMEXMLMetadata();
   reader.setMetadataStore(meta);
   reader.setId(id);
   String[] usedFiles = reader.getUsedFiles();
   int sizeX = reader.getSizeX();
   int sizeY = reader.getSizeY();
   int sizeZ = reader.getSizeZ();
   int sizeC = reader.getSizeC();
   int sizeT = reader.getSizeT();
   reader.close();
   System.out.println("Path = " + id);
   System.out.println("X size = " + sizeX);
   System.out.println("Y size = " + sizeY);
   System.out.println("Z size = " + sizeZ);
   System.out.println("C size = " + sizeC);
   System.out.println("T size = " + sizeT);
   System.out.println("Used files =");
   for (String used : usedFiles) System.out.println("\t" + used);
 }
That's how you synthesize a virtual directory on disk, essentially, for the file grouping logic to use.
The key is to register each fake filename with "Location.mapId(filename, dummy)" where "dummy" doesn't matter.
As long as it's not null.

Then below, I call "getUsedFiles" to get a list of the constituent filenames, which you can compare against
your expectations.

Oh, forget about what I said about getUsedFilesÑyou don't have access to the API at that level.

 */
