//
// JVMLinkFlowCytometry.java
//

/*
Server application for flow cytometry with WiscScan using JVMLink.
Copyright (c) 2008 Hidayath Ansari. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.apps.flow;

import ij.*;
import ij.gui.ImageWindow;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.*;
import visad.*;
import visad.java2d.DisplayImplJ2D;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/apps/flow/JVMLinkFlowCytometry.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/apps/flow/JVMLinkFlowCytometry.java">SVN</a></dd></dl>
 */
public class JVMLinkFlowCytometry {

  private static final int MIN_INTENSITY = 0;
  private static final int MAX_INTENSITY = 255;

  private static ImageJ ij;
  private static ImagePlus imp;
  private static ImageStack stack;
  private static ByteProcessor bp;
  private static ColorModel theCM;
  private static Detector d;

  public static int nSlices;
  private static int nParticles;
  private static JFrame frame;
  private static JCheckBox CBcumulative;
  private static boolean cumulative;
  private static Vector<Double> areaValues;
  private static Vector<Double> intensityValues;
  private static Vector<Double> diameterValues;
  private static Vector<Integer> cellFrameV = new Vector<Integer>();

  private static Vector<Particle> particles;
  private static Vector<Slice> slices;

  //These are pointers to vectors containing actual values
  //to axes as specified in WiscScan.
  private static Vector<Double> XAxisValues;
  private static Vector<Double> YAxisValues;
  private static int xAxis, yAxis;

  private static RealType area, intensity, diameter, xType, yType;
  private static FunctionType fn;
  private static DataReferenceImpl data_ref;
  private static Scrollbar scroll;
  private static DisplayImpl display;
  private static ResultsTable rt;
  private static double maxArea, minArea, maxIntensity, minIntensity;
  private static ScalarMap xMap, yMap;

  private static double pixelMicronSquared;

  private static int resolutionWidth = 0;
  private static int resolutionHeight = 0;
  private static int minX=0, maxX=0, minY=0, maxY=0;
  private static int intensityThreshold=30, areaThreshold=100;
  private static boolean showParticles=false;

  private static Vector<Integer> sliceBegin, sliceEnd;
  // sliceBegin[i] is the minimum identifier of all particles on slice i
  // sliceEnd[i] is the maximum identifier of all particles on slice i

  // For XML metadata
  private static String s_Name, s_Experiment, s_Params, s_Date;

  public static void startImageJ() {
    ij = new ImageJ();
    ij.getBounds();
  }

  public static void incrementSlices() {
    nSlices++;
  }

  public static void showImage(int width, int height, byte[] imageData) {
    //bp = new ByteProcessor(width, height, imageData,
    //  ImageTools.makeColorModel(1, DataBuffer.TYPE_BYTE));
    bp = new ByteProcessor(width,height,imageData, theCM);
    bp.createImage();
    stack.addSlice("Slice "+nSlices, bp);
    imp.setStack("Islet images", stack);
    imp.setSlice(nSlices);
    imp.show();
  }

  public static void init(int width, int height, double pixelsPerMicron) {
    setResolution(width, height);
    s_Date = new SimpleDateFormat("MM.dd.yyyy hh:mm:ss").format(new Date());

    byte[] r = new byte[256];
    byte[] g = new byte[256];
    byte[] b = new byte[256];

    for(int ii=0 ; ii<256 ; ii++)
    r[ii] = g[ii] = b[ii] = (byte)ii;

    theCM = new IndexColorModel(8, 256, r,g,b);
    imp = new ImagePlus("Islet images",
      new ByteProcessor(resolutionWidth, resolutionHeight));
    //stack = new ImageStack(512, 512,
    //  ImageTools.makeColorModel(1, DataBuffer.TYPE_BYTE));
    stack = new ImageStack(resolutionWidth, resolutionHeight, theCM);
    imp.show();

    Detector.createImageHolder(resolutionWidth, resolutionHeight);

    imp.unlock();
    maxArea=Double.MIN_VALUE;
    minArea=Double.MAX_VALUE;
    maxIntensity = Double.MIN_VALUE;
    minIntensity = Double.MAX_VALUE;
    nSlices = 0;
    nParticles = 0;

    pixelMicronSquared = pixelsPerMicron*pixelsPerMicron;
    pixelMicronSquared = 0.149*0.149;

    particles = new Vector<Particle>();
    slices = new Vector<Slice>();

    sliceBegin = new Vector<Integer>();
    sliceEnd = new Vector<Integer>();
    areaValues = new Vector<Double>();
    intensityValues = new Vector<Double>();
    diameterValues = new Vector<Double>();

    XAxisValues = intensityValues;
    YAxisValues = areaValues;

    byte[] dummyData = new byte[resolutionWidth*resolutionHeight];
    bp = new ByteProcessor(resolutionWidth,resolutionHeight,dummyData, theCM);
    bp.createImage();
    nSlices = 2;
    stack.addSlice("Slice "+nSlices, bp);

    // because newestProcessFrame indexes from non-blank slides
    newestProcessFrame(-1);

    stack.addSlice("Slice "+nSlices, bp);
    newestProcessFrame(0);
    imp.setStack("Islet images", stack);

    ImageWindow stackwin = ((ImageWindow) imp.getWindow());
    scroll = (Scrollbar) stackwin.getComponent(1);

    imp.setSlice(nSlices);
    //intensityValues.add(0.0);
    //areaValues.add(0.0);
    //intensityValues.add(0.0);
    //areaValues.add(0.0);

    area = RealType.getRealType("Area");
    intensity = RealType.getRealType("Intensity");
    diameter = RealType.getRealType("Diameter");

    try {
      // Display initialization
      //RealTupleType point = new RealTupleType(area, intensity);

      display = new DisplayImplJ2D("Graph Display");
      data_ref = new DataReferenceImpl("data_ref");
      data_ref.setData(null);
      display.addReference(data_ref);
      display.getGraphicsModeControl().setScaleEnable(true);
      display.getGraphicsModeControl().setPointSize(3);
      setAxes(0,1);

      frame = new JFrame("Graph Window");
      frame.setLayout(new BorderLayout());

      CBcumulative = new JCheckBox("Cumulative");
      CBcumulative.setMnemonic(KeyEvent.VK_G);
      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
      bottomPanel.add(CBcumulative);

      frame.getContentPane().add(display.getComponent(), BorderLayout.CENTER);
      frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

      ItemListener CBitemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent itemEvent) {
          cumulative = itemEvent.getStateChange() == ItemEvent.SELECTED;
          try {
            JVMLinkFlowCytometry.data_ref.setData(
              newestGetData(imp.getCurrentSlice(), cumulative, intensity, fn));
            JVMLinkFlowCytometry.display.reDisplayAll();
          }
          catch (RemoteException e) { }
          catch (VisADException e) { }
        }
      };
      CBcumulative.addItemListener(CBitemListener);

      AdjustmentListener l = new AdjustmentListener() {
        public void adjustmentValueChanged(AdjustmentEvent arg0) {
          try {
            int slideNum = ((Scrollbar)
              imp.getWindow().getComponent(1)).getValue();
            //for the detected particles window
            if (showParticles) {
              d = new Detector(resolutionWidth,
                intensityThreshold, areaThreshold);
              d.findParticles(stack.getProcessor(slideNum));
              d.crunchArray();
              Detector.displayImage(d.getFloodArray());
            }

            //for the graph
            //IJ.log("This is slide "+slideNum+" and particle numbers " +
            //  "on this slide go from "+sliceBegin[slideNum]+" to "+
            //  sliceEnd[slideNum]);
            //IJ.log(Integer.toString(((Scrollbar)
            //  Intensity_.this.imp.getWindow().getComponent(1)).getValue()));
            JVMLinkFlowCytometry.data_ref.setData(newestGetData(slideNum,
              cumulative, intensity, fn));
            JVMLinkFlowCytometry.display.reDisplayAll();
            //Intensity_.this.data_ref.setData(getData(imp.getCurrentSlice(),
            //  cumulative, intensity, fn));
          }
          catch (RemoteException e) { }
          catch (VisADException e) { }
        }
      };
      scroll.addAdjustmentListener(l);

      imp.setSlice(1);
      frame.setSize(600, 600);
      frame.setVisible(true);
    }
    catch (VisADException e) {
      IJ.log("VisAD Exception in init: "+e.getMessage());
    }
    catch (RemoteException re) {
      IJ.log("Remote Exception: "+re.getMessage());
    }
  }

  public static void setAxes(int x, int y) {
    // 0 - Intensity
    // 1 - Area
    // 2 - Diameter
    xAxis = x; yAxis = y;

    switch(xAxis) {
    case 0:
      xType = intensity;
      XAxisValues = intensityValues;
      break;
    case 1:
      xType = area;
      XAxisValues = areaValues;
      break;
    case 2:
      xType = diameter;
      XAxisValues = diameterValues;
      break;
    }

    switch(yAxis) {
    case 0:
      yType = intensity;
      YAxisValues = intensityValues;
      break;
    case 1:
      yType = area;
      YAxisValues = areaValues;
      break;
    case 2:
      yType = diameter;
      YAxisValues = diameterValues;
      break;
    }
    try {
      xMap = new ScalarMap(xType, Display.XAxis);
      yMap = new ScalarMap(yType, Display.YAxis);
      xMap.setRange(getMinX(), getMaxX());
      yMap.setRange(getMinY(), getMaxY());

      fn = new FunctionType(xType, yType);
      display.clearMaps();
      display.addMap(xMap);
      display.addMap(yMap);

    }
    catch (RemoteException e) { e.printStackTrace(); }
    catch (VisADException  e) { e.printStackTrace(); }
  }

  // Setters

  public static void setResolution(int width, int height) {
    resolutionWidth = width;
    resolutionHeight = height;
  }

  public static void setXAxis(int option) { xAxis = option; }
  public static void setYAxis(int option) { yAxis = option; }
  public static void setName(String name) { s_Name = name; }
  public static void setExperiment(String exp) { s_Experiment = exp; }
  public static void setParams(String params) { s_Params = params; }
  public static void setMinX(int val) { minX = val; }
  public static void setMaxX(int val) { maxX = val; }
  public static void setMinY(int val) { minY = val; }
  public static void setMaxY(int val) { maxY = val; }
  public static void setIntensityThreshold(int val) {
    intensityThreshold = val;
  }
  public static void setAreaThreshold(int val) { areaThreshold = val; }
  public static void setPixelMicronSquared(double val) {
    pixelMicronSquared = val;
  }
  public static void reprocessAll() {
    particles = new Vector<Particle>();
    slices = new Vector<Slice>();
    nParticles = 0;
    nSlices = 0;
    for (int i=0; i<stack.getSize(); i++) {
      incrementSlices();
      newestProcessFrame(i+1);
    }
    updateGraph();
  }

  public static void saveDataWithXML() throws FormatException, IOException {
    ImageWriter iw = new ImageWriter();
    MetadataStore metadataStore = MetadataTools.createOMEXMLMetadata();
    metadataStore.createRoot();

    metadataStore.setPixelsSizeX(new Integer(resolutionWidth), 0, 0);
    metadataStore.setPixelsSizeY(new Integer(resolutionHeight), 0, 0);
    metadataStore.setPixelsSizeZ(new Integer(1), 0, 0);
    metadataStore.setPixelsSizeC(new Integer(1), 0, 0);
    metadataStore.setPixelsSizeT(new Integer(stack.getSize()), 0, 0);
    metadataStore.setPixelsPixelType("Uint8", 0, 0);
    metadataStore.setPixelsBigEndian(new Boolean(false), 0, 0);
    metadataStore.setPixelsDimensionOrder("XYTZC", 0, 0);
    metadataStore.setExperimenterFirstName(s_Name, 0);

    MetadataRetrieve metadataObject = (MetadataRetrieve) metadataStore;
    iw.setMetadataRetrieve(metadataObject);
    MetadataTools.populateOriginalMetadata(metadataObject,
      "Experiment", s_Experiment);
    MetadataTools.populateOriginalMetadata(metadataObject,
      "Parameters", s_Params);
    MetadataTools.populateOriginalMetadata(metadataObject, "Date", s_Date);
    MetadataTools.populateOriginalMetadata(metadataObject,
      "AreaValues", flattenVector(areaValues));
    MetadataTools.populateOriginalMetadata(metadataObject,
      "IntensityValues", flattenVector(intensityValues));

    // setImageCreationDate, setImageName on MetadataStore
    iw.setId("testImage.ome.tiff");

    System.out.println(stack.getSize());
    for (int i=1; i<=stack.getSize(); i++) {
      byte[] byteArray = (byte[]) stack.getProcessor(i).getPixels();
      iw.saveBytes(byteArray, i==stack.getSize());
    }
    iw.close();
  }

  public static void newestProcessFrame() {
    newestProcessFrame(nSlices-2);
  }

  public static void newestProcessFrame(int sliceNum) {
    sliceNum = sliceNum+2;
    imp.unlock();
    d = new Detector(resolutionWidth, intensityThreshold, areaThreshold);
    d.findParticles(stack.getProcessor(sliceNum));
    Vector<Particle> thisParticles = d.crunchArray();
    if (showParticles) {
      System.out.println("processFrame for slice "+sliceNum);
      Detector.displayImage(d.getFloodArray());
    }
    Slice thisSlice = new Slice(nSlices);
    if (thisParticles.size() > 0) {
      thisSlice.hasParticles = true;
      thisSlice.begin = nParticles;
      thisSlice.end = nParticles+thisParticles.size()-1;
    }

    for (int i=0; i<thisParticles.size(); i++) {
      Particle thisParticle = thisParticles.get(i);
      thisParticle.setNum(nParticles++);
      thisParticle.setSliceNum(nSlices);
      particles.add(thisParticle);
      //thisParticle.print();

      int thisArea = thisParticle.getArea();
      int thisIntensity = thisParticle.getIntensity();
      if (thisArea > maxArea) maxArea = thisArea;
      if (thisArea < minArea) minArea = thisArea;
      if (thisIntensity > maxIntensity) maxIntensity = thisIntensity;
      if (thisIntensity < minIntensity) minIntensity = thisIntensity;
    }

    slices.add(thisSlice);
  }

  public static void newProcessFrame() {
    imp.unlock();
    ImageProcessor ip = stack.getProcessor(imp.getCurrentSlice());
    double totalArea=0, totalIntensity=0;
    for (int i=0; i<resolutionWidth; i++) {
      for (int j=0; j<resolutionHeight; j++) {
        int value = ip.getPixel(i, j);
        if (value >= intensityThreshold) {
          totalArea++;
          totalIntensity += value;
        }
      }
    }
    IJ.log("Area: "+totalArea);
    IJ.log("Intensity: "+totalIntensity);
    if (totalArea > maxArea) maxArea = totalArea;
    if (totalArea < minArea) minArea = totalArea;
    if (totalIntensity > maxIntensity) maxIntensity = totalIntensity;
    if (totalIntensity < minIntensity) minIntensity = totalIntensity;
    areaValues.add(totalArea);
    if (totalArea != 0) intensityValues.add(totalIntensity/totalArea);
    else intensityValues.add(0.0);
    System.out.println("just added to vectors: "+
      totalArea+" "+totalIntensity/totalArea);
  }

  public static void processFrame() {
    incrementSlices();
    imp.unlock();
    bp.setThreshold(25, 255, 1);
    IJ.run("Analyze Particles...",
      "size=0-Infinity circularity=0.00-1.00 show=Nothing display clear");
    rt = Analyzer.getResultsTable();
    double totalArea=0, totalIntensity=0;
    for (int j=0; j<rt.getCounter(); j++) {
      double area = rt.getValue("Area", j);
      totalArea += area;
      totalIntensity += (rt.getValue("Mean", j))*area;
    }
    if (totalArea > maxArea) maxArea = totalArea;
    if (totalArea < minArea) minArea = totalArea;
    if (totalIntensity > maxIntensity) maxIntensity = totalIntensity;
    if (totalIntensity < minIntensity) minIntensity = totalIntensity;
    areaValues.add(totalArea);
    intensityValues.add(totalIntensity);
    cellFrameV.add(nSlices);

    sliceBegin.ensureCapacity(2*nSlices);
    sliceEnd.ensureCapacity(2*nSlices);
    sliceBegin.add(nSlices);
    sliceEnd.add(nSlices);
    System.out.println("############: sliceBegin looks like ");
    for (int i=0; i<sliceBegin.size(); i++) {
      System.out.println(sliceBegin.get(i));
    }
  }

  private static double getMinX() {
    double areaRange = (maxArea - minArea)/pixelMicronSquared;

    if (minX !=0) return minX;
    else {
      switch(xAxis) {
        case 1: return (minArea/pixelMicronSquared - 0.05*areaRange);
        case 0: return MIN_INTENSITY;
        default: return (minArea/pixelMicronSquared - 0.05*areaRange);
      }
    }
  }

  private static double getMaxX() {
    double areaRange = (maxArea - minArea)/pixelMicronSquared;

    if (maxX !=0) return maxX;
    else {
      switch(xAxis) {
        case 1: return (maxArea/pixelMicronSquared+0.05*areaRange);
        case 0: return MAX_INTENSITY;
        default: return (maxArea/pixelMicronSquared+0.05*areaRange);
      }
    }
  }

  private static double getMinY() {
    double areaRange = (maxArea - minArea)/pixelMicronSquared;

    if (minY !=0) return minY;
    else {
      switch(yAxis) {
        case 1: return (minArea/pixelMicronSquared - 0.05*areaRange);
        case 0: return MIN_INTENSITY;
        default: return (minArea/pixelMicronSquared - 0.05*areaRange);
      }
    }
  }

  private static double getMaxY() {
    double areaRange = (maxArea - minArea)/pixelMicronSquared;

    if (maxY !=0) return maxY;
    else {
      switch(yAxis) {
        case 1: return (maxArea/pixelMicronSquared+0.05*areaRange);
        case 0: return MAX_INTENSITY;
        default: return (maxArea/pixelMicronSquared+0.05*areaRange);
      }
    }
  }

/*
  public static void oldupdateGraph() {
    //1.55^2 from the pixels/micron value.
    double areaRange = (maxArea - minArea)/pixelMicronSquared;
    try {
      areaMap.setRange(minArea/pixelMicronSquared - 0.05*areaRange,
        maxArea/pixelMicronSquared+0.05*areaRange);
      data_ref.setData(newGetData(nSlices, true, intensity, fn));
      display.addReference(data_ref);
    }
    catch (VisADException e) {
      IJ.log("VisAD Exception in updateGraph: "+e.getMessage());
    }
    catch (RemoteException re) {
      IJ.log("Remote Exception: "+re.getMessage());
    }
  }
*/

  public static void updateGraph() {
    try {
      xMap.setRange(getMinX(), getMaxX());
      yMap.setRange(getMinY(), getMaxY());
      data_ref.setData(newestGetData(nSlices, true, xType, fn));
      display.addReference(data_ref);
    }
    catch (VisADException e) {
      IJ.log("VisAD Exception in updateGraph: "+e.getMessage());
    }
    catch (RemoteException re) {
      IJ.log("Remote Exception: "+re.getMessage());
    }
  }

  public static FlatField newestGetData(int slice,
    boolean cumulative, RealType x, FunctionType fn)
  {
    //slice is NOT zero-indexed.
    double[] xArray, yArray;
    int beginSlice=0, endSlice=slice-1;
    if (!cumulative) beginSlice = slice-1;
    int beginIndex = Integer.MAX_VALUE,
      endIndex = Integer.MIN_VALUE, numParticles=0;
    for (int i=beginSlice; i<=endSlice; i++) {
      if (slices.get(i).hasParticles) {
        if (slices.get(i).begin < beginIndex) beginIndex = slices.get(i).begin;
        if (slices.get(i).end > endIndex) endIndex = slices.get(i).end;
        numParticles += (slices.get(i).end - slices.get(i).begin + 1);
      }
    }

    //System.out.println("beginIndex:"+beginIndex+
    //  " endIndex:"+endIndex+" numParticles:"+numParticles);

    if (numParticles == 0) return null;

    xArray = new double[numParticles];
    yArray = new double[numParticles];

    for (int i=beginIndex; i<=endIndex; i++) {
      switch(xAxis) {
        case 0:
          xArray[i-beginIndex] =
            particles.get(i).totalIntensity / particles.get(i).area;
          break;
        case 1:
          xArray[i-beginIndex] = particles.get(i).area/pixelMicronSquared;
          break;
      }
      switch(yAxis) {
        case 0:
          yArray[i-beginIndex] =
            particles.get(i).totalIntensity / particles.get(i).area;
          break;
        case 1:
          yArray[i-beginIndex] = particles.get(i).area/pixelMicronSquared;
          break;
      }
    }
    //for (int i=beginIndex; i<=endIndex; i++) {
    //  System.out.println("Now plotting "+
    //    Double.toString(xArray[i-beginIndex]) + " " +
    //    Double.toString(yArray[i-beginIndex]));
    //}

    List1DDoubleSet xSet;
    FlatField ff=null;
    if (endIndex >= beginIndex) try {
      xSet = new List1DDoubleSet(xArray, x, null, null);
      ff = new FlatField(fn, xSet);
      double[][] ff_vals = new double[1][xArray.length];
      ff_vals[0] = yArray;
      ff.setSamples(ff_vals);
    }
    catch (VisADException e) {
      IJ.log("VisAD Exception in newGetData: "+e.getMessage());
    }
    catch (RemoteException re) {
      IJ.log("Remote Exception: "+re.getMessage());
    }
    return ff;
  }

  public static FlatField newGetData(int slice,
    boolean cumulative, RealType x, FunctionType fn)
  {
    //slice is NOT zero-indexed.
    double[] xArray, yArray;
    int beginIndex=0, endIndex;
    if (!cumulative) beginIndex = slice-1;
    endIndex = slice-1;
    xArray = new double[endIndex - beginIndex + 1];
    yArray = new double[endIndex - beginIndex + 1];
    for (int i=beginIndex; i<=endIndex; i++) {
      //intensityValues.get(i);
      xArray[i-beginIndex] = XAxisValues.get(i);

      //areaValues.get(i)/pixelMicronSquared;
      yArray[i-beginIndex] = YAxisValues.get(i);
    }
    System.out.println("Plotting for slices between indices "+
      beginIndex+ " and "+endIndex);
    //printVector(XAxisValues);
    //System.out.println("--");
    //printVector(YAxisValues);
    for (int i=beginIndex; i<=endIndex; i++) {
      IJ.log("Now plotting "+Double.toString(xArray[i-beginIndex]) +
        " " + Double.toString(yArray[i-beginIndex]));
    }
    List1DDoubleSet xSet;
    FlatField ff=null;
    if (endIndex >= beginIndex) try {
      xSet = new List1DDoubleSet(xArray, x, null, null);
      ff = new FlatField(fn, xSet);
      double[][] ff_vals = new double[1][xArray.length];
      ff_vals[0] = yArray;
      ff.setSamples(ff_vals);
    }
    catch (VisADException e) {
      IJ.log("VisAD Exception in newGetData: "+e.getMessage());
    }
    catch (RemoteException re) {
      IJ.log("Remote Exception: "+re.getMessage());
    }
    return ff;
  }

  public static FlatField getData(int slice,
    boolean cumulative, RealType x, FunctionType fn)
  {
    double[] xArray, yArray;
    int beginIndex=0, endIndex;
    if (!cumulative) beginIndex = sliceBegin.get(slice);
    endIndex = sliceEnd.get(slice);
    xArray = new double[endIndex - beginIndex + 1];
    yArray = new double[endIndex - beginIndex + 1];
    IJ.log("In getData, begin and end indices are "+
      beginIndex+ " and "+endIndex);
    for (int i=beginIndex; i<=endIndex; i++) {
      //IJ.log("Assigning values in the x and y arrays");
      xArray[i-beginIndex] = intensityValues.elementAt(i);
      yArray[i-beginIndex] = areaValues.elementAt(i);
    }
    IJ.log("Done assigning");
    for (int i=beginIndex; i<=endIndex; i++) {
      IJ.log(Double.toString(xArray[i-beginIndex]) + " " +
        Double.toString(yArray[i-beginIndex]));
    }
    List1DDoubleSet intensitySet;
    FlatField ff=null;
    if (endIndex >= beginIndex) try {
      intensitySet = new List1DDoubleSet(xArray, x, null, null);
      ff = new FlatField(fn, intensitySet);
      double[][] ff_vals = new double[1][xArray.length];
      ff_vals[0] = yArray;
      ff.setSamples(ff_vals);
    }
    catch (VisADException e) {
      IJ.log("VisAD Exception: "+e.getMessage());
    }
    catch (RemoteException re) {
      IJ.log("Remote Exception: "+re.getMessage());
    }
    return ff;
  }

  public static void saveValues() throws IOException {
    BufferedWriter bw = new BufferedWriter(
      new FileWriter("values"+s_Date.substring(0,9)));
    bw.write("Area values: "); bw.newLine();
    for (int i=0; i<areaValues.size(); i++) {
      bw.write(Double.toString(areaValues.get(i))); bw.newLine();
    }
    bw.write("Intensity values: "); bw.newLine();
    for (int i=0; i<intensityValues.size(); i++) {
      bw.write(Double.toString(intensityValues.get(i))); bw.newLine();
    }
  }

  public static void showParticles(boolean val) {
    showParticles = val;
    if (val) Detector.impParticles.show();
    else Detector.impParticles.hide();
  }

  public static void printVector(Vector<Double> v) {
    for (int i=0; i<v.size(); i++) {
      double dtemp = v.get(i);
      System.out.println(dtemp);
    }
  }

  private static String flattenVector(Vector<Double> v) {
    String retval = "";
    for (int i=0; i<v.size(); i++) {
      retval += v.get(i)+"\n";
    }
    return retval;
  }

  // -- Helper classes --

  private static class Slice {
    int num;
    boolean hasParticles;
    int begin;
    int end;

    public Slice(int n) {
      num = n;
      hasParticles = false;
    }

    public void print() {
      if (hasParticles) {
        System.out.println("Slice "+num+" begins with particle "+
          begin+" and ends at "+end);
      }
      else System.out.println("Slice "+num+" has no particles");
    }
  }

}

/* Old try catch block
try {
// Display initialization
area = RealType.getRealType("Area");
intensity = RealType.getRealType("Intensity");
diameter = RealType.getRealType("Diameter");
//RealTupleType point = new RealTupleType(area, intensity);
areaMap = new ScalarMap(area, Display.YAxis);
intensityMap = new ScalarMap(intensity, Display.XAxis);
intensityMap.setRange(0,255);

fn = new FunctionType(intensity, area);
display = new DisplayImplJ2D("Graph Display");
display.addMap(intensityMap);
display.addMap(areaMap);
data_ref = new DataReferenceImpl("data_ref");
data_ref.setData(null);
display.addReference(data_ref);
display.getGraphicsModeControl().setScaleEnable(true);
display.getGraphicsModeControl().setPointSize(3);
frame = new JFrame("Graph Window");
frame.setLayout(new BorderLayout());

CBcumulative = new JCheckBox("Cumulative");
CBcumulative.setMnemonic(KeyEvent.VK_G);
JPanel bottomPanel = new JPanel();
bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
bottomPanel.add(CBcumulative);

frame.getContentPane().add(display.getComponent(), BorderLayout.CENTER);
frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

ItemListener CBitemListener = new ItemListener() {
  public void itemStateChanged(ItemEvent itemEvent) {
    if (itemEvent.getStateChange() == ItemEvent.SELECTED) cumulative = true;
    else cumulative = false;
    try {
      JVMLinkFlowCytometry.data_ref.setData(newGetData(imp.getCurrentSlice(),
        cumulative, intensity, fn));
      JVMLinkFlowCytometry.display.reDisplayAll();
    }
    catch (RemoteException e) { }
    catch (VisADException e) { }
  }
};
CBcumulative.addItemListener(CBitemListener);

AdjustmentListener l = new AdjustmentListener() {
  public void adjustmentValueChanged(AdjustmentEvent arg0) {
    try {
      int slideNum = ((Scrollbar) imp.getWindow().getComponent(1)).getValue();
      //IJ.log("This is slide "+slideNum+" and particle numbers on this " +
      //  "slide go from "+sliceBegin[slideNum]+" to "+sliceEnd[slideNum]);
      //IJ.log(Integer.toString(((Scrollbar)
      //  Intensity_.this.imp.getWindow().getComponent(1)).getValue()));
      JVMLinkFlowCytometry.data_ref.setData(newGetData(slideNum,
        cumulative, intensity, fn));
      JVMLinkFlowCytometry.display.reDisplayAll();
      //Intensity_.this.data_ref.setData(getData(imp.getCurrentSlice(),
      //  cumulative, intensity, fn));
    }
    catch (RemoteException e) { }
    catch (VisADException e) { }
  }
};
scroll.addAdjustmentListener(l);

imp.setSlice(1);
frame.setSize(600, 600);
frame.setVisible(true);
}
catch (VisADException e) {IJ.log("VisAD Exception in init: "+e.getMessage());}
catch (RemoteException re) {IJ.log("Remote Exception: "+re.getMessage());}
*/
