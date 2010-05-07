package loci.plugins.in;

import static org.junit.Assert.*;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.io.IOException;

import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.BF;

import org.junit.Test;

// TODO
//  - flesh out existing tests
//  - add some tests for combination of options

public class ImporterTest {

  private static final boolean debug = true;

  // TODO - in place to switch off tests that interface with gui
  private static final boolean runAllTests = true;
  
  private enum Axis {Z,C,T};
  
  // ** Helper methods *******************************************************************

  private String constructFakeFilename(String title,
      int pixelType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries)
  {
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
    assertEquals(z,ip.getNSlices());
    assertEquals(c,ip.getNChannels());
    assertEquals(t,ip.getNFrames());
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
    
    // these tests rely on each image being large enough to get the siztc index pixels of a
    // FakeFile. This requires the x value of tested images to be somewhat large. Assert
    // the input image fits the bill
    
    assertTrue(ip.getWidth() >= 41);
    assertTrue(ip.getHeight() >= 1);
    
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
  
  private void datasetSwapDimsTest(int pixType, String order, int x, int y, int z, int c, int t)
  {
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

    System.out.println("Numslices == " + numSlices);
    for (int i = 0; i < numSlices; i++)
      printVals(st.getProcessor(i+1));

    // TODO - do some tests on order of dims of created images
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
  
  private void memorySpecifyZRangeTest()
  { 
    int pixType = FormatTools.UINT8, x=30, y=30, z=5, c=2, t=4, s=-1;
    String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setZBegin(0, 0);
      options.setZEnd(0, 4);
      options.setZStep(0, 2);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // should have the data: one series, all t's, all c's, z's from 1 to 3 by 2
    assertNotNull(imps);
    assertTrue(imps.length == 1);
    ImagePlus ip = imps[0];
    assertNotNull(ip);
    assertTrue(ip.getWidth() == x);
    assertTrue(ip.getHeight() == y);
    ImageStack st = ip.getStack();
    int numSlices = st.getSize();
    assertEquals(16,numSlices);
    System.out.println("SpecifyZRangeTest: slices below");
    for (int i = 0; i < numSlices; i++)
      printVals(st.getProcessor(i));
    
    //TODO - some assertions
    
  }
  
  private void memorySpecifyCRangeTest()
  { 
    int pixType = FormatTools.UINT8, x=30, y=30, z=4, c=11, t=4, s=-1;
    String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setCBegin(0, 3);
      options.setCEnd(0, 9);
      options.setCStep(0, 3);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // should have the data: one series, all t's, all c's, z's from 1 to 3 by 2
    assertNotNull(imps);
    assertTrue(imps.length == 1);
    ImagePlus ip = imps[0];
    assertNotNull(ip);
    assertTrue(ip.getWidth() == x);
    assertTrue(ip.getHeight() == y);
    ImageStack st = ip.getStack();
    int numSlices = st.getSize();
    assertEquals(48,numSlices);
    System.out.println("SpecifyCRangeTest: slices below");
    for (int i = 0; i < numSlices; i++)
      printVals(st.getProcessor(i));
    
    //TODO - some assertions
    
  }
  
  private void memorySpecifyTRangeTest()
  { 
    int pixType = FormatTools.UINT8, x=30, y=30, z=3, c=2, t=12, s=-1;
    String path = constructFakeFilename("range", pixType, x, y, z, c, t, s);
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setTBegin(0, 0);
      options.setTEnd(0, 3);
      options.setTStep(0, 4);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }
    
    // should have the data: one series, all t's, all c's, z's from 1 to 3 by 2
    assertNotNull(imps);
    assertTrue(imps.length == 1);
    ImagePlus ip = imps[0];
    assertNotNull(ip);
    assertTrue(ip.getWidth() == x);
    assertTrue(ip.getHeight() == y);
    ImageStack st = ip.getStack();
    int numSlices = st.getSize();
    assertEquals(18,numSlices);
    System.out.println("SpecifyTRangeTest: slices below");
    for (int i = 0; i < numSlices; i++)
      printVals(st.getProcessor(i));
    
    //TODO - some assertions
    
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
  public void testDefaultBehavior() {

    if (runAllTests)
    {
      defaultBehaviorTest(FormatTools.UINT16, 400, 300, 1, 1, 1);
      defaultBehaviorTest(FormatTools.INT16, 107, 414, 1, 1, 1);
      defaultBehaviorTest(FormatTools.UINT32, 323, 206, 3, 2, 1);
      defaultBehaviorTest(FormatTools.UINT8, 57, 78, 5, 4, 3);
      defaultBehaviorTest(FormatTools.INT32, 158, 99, 2, 3, 4);
      defaultBehaviorTest(FormatTools.INT8, 232, 153, 3, 7, 5);
    }
  }

  @Test
  public void testOutputStackOrder()
  {
    if (runAllTests)
    {
      outputStackOrderTest(FormatTools.UINT8, "XYZCT", 82, 47, 2, 3, 4);
      outputStackOrderTest(FormatTools.UINT8, "XYZTC", 82, 47, 2, 3, 4);
      outputStackOrderTest(FormatTools.UINT8, "XYCZT", 82, 47, 2, 3, 4);
      outputStackOrderTest(FormatTools.UINT8, "XYCTZ", 82, 47, 2, 3, 4);
      outputStackOrderTest(FormatTools.UINT8, "XYTCZ", 82, 47, 2, 3, 4);
      outputStackOrderTest(FormatTools.UINT8, "XYTZC", 82, 47, 2, 3, 4);
    }
  }
    
  @Test
  public void testDatasetGroupFiles() {
    // TODO - need to enhance FakeFiles first I think
    //   This option kicks in when you have similarly named files. all the files get loaded
    //   as one dataset. This relies on the filename differing only by an index. Not sure
    //   what an index in a fake filename would do. Tried adding -1 before .fake to see what
    //   would happen and BF crashes with negArraySizeExcep  
  }

  @Test
  public void testDatasetSwapDims() {
    // TODO - can't really test this with fake files. It needs a series of files from grouping
    //   to reorder.
    if (runAllTests)
    {
      datasetSwapDimsTest(FormatTools.UINT8, "XYZCT", 82, 47, 2, 3, 4);
    }
  }
  
  @Test
  public void testDatasetConcatenate() {
    
    // TODO - Curtis says impl broken right now - will test later
    if (runAllTests)
    {
      // open a dataset that has multiple series
      //   and should get back a single series
      datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 1, 1, 1, 1);
      datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 1, 1, 1, 17);
      datasetConcatenateTest(FormatTools.UINT8, "XYZCT", 82, 47, 4, 5, 2, 9);
    }
  }
  
  @Test
  public void testColorMerge() {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testColorRgbColorize() {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testColorCustomColorize() {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testColorAutoscale() {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testMemoryVirtualStack() {
    if (runAllTests)
    {
      memoryVirtualStackTest(false);
      memoryVirtualStackTest(true);
    }
  }
  
  @Test
  public void testMemoryRecordModifications() {
    // TODO - how to test this?
  }
  
  @Test
  public void testMemorySpecifyRange() {
    memorySpecifyZRangeTest();
    memorySpecifyCRangeTest();
    memorySpecifyTRangeTest();
    //memorySpecifySRangeTest();
  }
  
  @Test
  public void testMemoryCrop() {
    
    // TODO - Curtis says impl broken right now - will test later
    
    if (runAllTests)
    {
      memoryCropTest(FormatTools.UINT8, 203, 409, 185, 104);
      // TODO - crop window too big or too small
    }
  }
  
  @Test
  public void testSplitChannels() {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testSplitFocalPlanes() {
    // TODO - Curtis says impl broken right now - will test later
  }
  
  @Test
  public void testSplitTimepoints() {
    // TODO - Curtis says impl broken right now - will test later
  }

  // ** Main method *****************************************************************

  public static void main(String[] args) {
    ImporterTest tester = new ImporterTest();
 
    //TODO - we could use reflection to discover all test methods, loop, and run them
    
    tester.testDefaultBehavior();
    tester.testOutputStackOrder();
    tester.testDatasetGroupFiles();
    tester.testDatasetSwapDims();
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
    
    // TODO - add combination tests
    
    System.exit(0);
  }
}

  // ****************************************************************************************
  // * old code vault : working copies to be deleted once refactored code proven
  // ****************************************************************************************
  
  /*
  @Test
  public void testOutputStackOrderXYZCT() {
    final int x = 82, y = 47, z = 2, c = 3, t = 4;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("xyzct", pixelType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder("XYZCT");
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
    int count = 0;
    // order differs here from other cases
    System.out.println("XYZCT");
    for (int k = 0; k < t; k++)
      for (int j = 0; j < c; j++)
        for (int i = 0; i < z; i++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          assertEquals(count,iIndex(proc));
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
          count++;
        }
  }
  
  @Test
  public void testOutputStackOrderXYZTC() {
    final int x = 82, y = 47, z = 2, c = 3, t = 4;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("xyztc", pixelType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder("XYZTC");
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
    int count = 0;
    // order differs here from other cases
    System.out.println("XYZTC");
    for (int j = 0; j < c; j++)
      for (int k = 0; k < t; k++)
        for (int i = 0; i < z; i++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          //assertEquals(count,iIndex(proc));
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
          count++;
        }
  }
  
  @Test
  public void testOutputStackOrderXYCZT() {
    final int x = 82, y = 47, z = 2, c = 3, t = 4;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("xyczt", pixelType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder("XYCZT");
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
    int count = 0;
    // order differs here from other cases
    System.out.println("XYCZT");
    for (int k = 0; k < t; k++)
      for (int i = 0; i < z; i++)
        for (int j = 0; j < c; j++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          //assertEquals(count,iIndex(proc));
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
          count++;
        }
  }
  
  @Test
  public void testOutputStackOrderXYCTZ() {
    final int x = 82, y = 47, z = 2, c = 3, t = 4;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("xyctz", pixelType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder("XYCTZ");
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
    int count = 0;
    // order differs here from other cases
    System.out.println("XYCTZ");
    for (int i = 0; i < z; i++)
      for (int k = 0; k < t; k++)
        for (int j = 0; j < c; j++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          //assertEquals(count,iIndex(proc));
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
          count++;
        }
  }
  
  @Test
  public void testOutputStackOrderXYTZC() {
    final int x = 82, y = 47, z = 2, c = 3, t = 4;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("xytzc", pixelType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder("XYTZC");
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
    int count = 0;
    // order differs here from other cases
    System.out.println("XYTZC");
    for (int j = 0; j < c; j++)
      for (int i = 0; i < z; i++)
        for (int k = 0; k < t; k++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          //assertEquals(count,iIndex(proc));
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
          count++;
        }
  }
  
  @Test
  public void testOutputStackOrderXYTCZ() {
    final int x = 82, y = 47, z = 2, c = 3, t = 4;
    final int pixelType = FormatTools.UINT8;
    final String path = constructFakeFilename("xytcz", pixelType, x, y, z, c, t, -1);
    ImagePlus[] imps = null;
    try {
      final ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setStackOrder("XYTCZ");
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
    int count = 0;
    // order differs here from other cases
    System.out.println("XYTCZ");
    for (int i = 0; i < z; i++)
      for (int j = 0; j < c; j++)
        for (int k = 0; k < t; k++)
        {
          ImageProcessor proc = st.getProcessor(count+1);
          printVals(proc);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          //assertEquals(count,iIndex(proc));
          assertEquals(i,zIndex(proc));
          assertEquals(j,cIndex(proc));
          assertEquals(k,tIndex(proc));
          count++;
        }
  }
*/
