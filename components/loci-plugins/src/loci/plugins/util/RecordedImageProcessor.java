/*
 * #%L
 * LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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

package loci.plugins.util;

import ij.gui.ProgressBar;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Image processor extension that records commands executed upon it.
 * Allows "recordable virtual stacks" that repeat operations performed on
 * one image plane on other image planes when they are loaded from disk.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/src/loci/plugins/util/RecordedImageProcessor.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/src/loci/plugins/util/RecordedImageProcessor.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class RecordedImageProcessor extends ImageProcessor {

  // -- Fields --

  private ImageProcessor proc;
  private boolean doRecording;
  private List<MethodEntry> methodStack;

  private int channelNumber;
  private ImageProcessor[] otherChannels;

  // -- Constructor --

  public RecordedImageProcessor(ImageProcessor proc) {
    this.proc = proc;
    methodStack = new ArrayList<MethodEntry>();
    doRecording = true;
  }

  public RecordedImageProcessor(ImageProcessor proc, int channelNum,
    ImageProcessor[] otherChannels)
  {
    this.proc = proc;
    methodStack = new ArrayList<MethodEntry>();
    doRecording = true;
    this.channelNumber = channelNum;
    this.otherChannels = otherChannels;
  }

  // -- RecordedImageProcessor API methods --

  public void setDoRecording(boolean doRecording) {
    this.doRecording = doRecording;
  }

  public List<MethodEntry> getMethodStack() {
    return methodStack;
  }

  public void applyMethodStack(List<MethodEntry> stack) {
    for (int i=0; i<stack.size(); i++) {
      MethodEntry m = stack.get(i);
      try {
        m.method.invoke(proc, m.args);
      }
      catch (IllegalAccessException e) {
      }
      catch (InvocationTargetException e) {
      }
    }
  }

  public ImageProcessor getChild() {
    return proc;
  }

  // -- ImageProcessor API methods --

  public void abs() {
    record("abs");
    proc.abs();
  }

  public void add(double value) {
    record("add", new Double(value), double.class);
    proc.add(value);
  }

  public void add(int value) {
    record("add", new Integer(value), int.class);
    proc.add(value);
  }

  public void and(int value) {
    record("and", new Integer(value), int.class);
    proc.and(value);
  }

  public void applyTable(int[] lut) {
    record("applyTable", lut, int[].class);
    proc.applyTable(lut);
  }

  public void autoThreshold() {
    record("autoThreshold");
    proc.autoThreshold();
  }

  public ImageProcessor convertToByte(boolean doScaling) {
    record("convertToByte", new Boolean(doScaling), boolean.class);
    return proc.convertToByte(doScaling);
  }

  public ImageProcessor convertToFloat() {
    record("convertToFloat");
    return proc.convertToFloat();
  }

  public ImageProcessor convertToRGB() {
    record("convertToRGB");
    return proc.convertToRGB();
  }

  public ImageProcessor convertToShort(boolean doScaling) {
    record("convertToShort", new Boolean(doScaling), boolean.class);
    return proc.convertToShort(doScaling);
  }

  public void convolve(float[] kernel, int kernelWidth, int kernelHeight) {
    record("convolve", new Object[] {kernel, new Integer(kernelWidth),
      new Integer(kernelHeight)}, new Class[] {float[].class,
      int.class, int.class});
    proc.convolve(kernel, kernelWidth, kernelHeight);
  }

  public void convolve3x3(int[] kernel) {
    record("convolve3x3", kernel, int[].class);
    proc.convolve3x3(kernel);
  }

  public void copyBits(ImageProcessor ip, int xloc, int yloc, int mode) {
    record("copyBits", new Object[] {ip, new Integer(xloc), new Integer(yloc),
      new Integer(mode)}, new Class[] {ImageProcessor.class, int.class,
      int.class, int.class});
    proc.copyBits(ip, xloc, yloc, mode);
  }

  public Image createImage() {
    if (otherChannels == null) return proc.createImage();
    // merge channels - adapted from CompositeImage

    int size = proc.getWidth() * proc.getHeight();
    int[] rgbPixels = new int[size];

    for (int i=0; i<otherChannels.length+1; i++) {
      ImageProcessor activeProcessor = null;
      if (i == channelNumber) {
        activeProcessor = proc;
      }
      else {
        if (i < channelNumber) activeProcessor = otherChannels[i];
        else activeProcessor = otherChannels[i - 1];
      }

      IndexColorModel cm = (IndexColorModel) activeProcessor.getColorModel();
      int mapSize = cm.getMapSize();
      int[] reds = new int[mapSize];
      int[] greens = new int[mapSize];
      int[] blues = new int[mapSize];

      byte[] tmp = new byte[mapSize];
      cm.getReds(tmp);
      for (int q=0; q<mapSize; q++) {
        reds[q] = (tmp[q] & 0xff) << 16;
      }
      cm.getGreens(tmp);
      for (int q=0; q<mapSize; q++) {
        greens[q] = (tmp[q] & 0xff) << 8;
      }
      cm.getBlues(tmp);
      for (int q=0; q<mapSize; q++) {
        blues[q] = tmp[q] & 0xff;
      }

      byte[] pixels = new byte[size];
      double min = activeProcessor.getMin();
      double max = activeProcessor.getMax();
      double scale = 256.0 / (max - min + 1);

      if (activeProcessor instanceof ByteProcessor) {
        pixels = (byte[]) activeProcessor.getPixels();
      }
      else if (activeProcessor instanceof ShortProcessor) {
        short[] s = (short[]) activeProcessor.getPixels();
        for (int q=0; q<size; q++) {
          int value = (int) ((s[q] & 0xffff) - min);
          if (value < 0) value = 0;
          value = (int) (value * scale + 0.5);
          if (value > 255) value = 255;
          pixels[q] = (byte) value;
        }
      }
      else if (activeProcessor instanceof FloatProcessor) {
        float[] f = (float[]) activeProcessor.getPixels();
        for (int q=0; q<size; q++) {
          float value = (float) (f[q] - min);
          if (value < 0) value = 0f;
          int ivalue = (int) (value * scale);
          if (ivalue > 255) ivalue = 255;
          pixels[q] = (byte) ivalue;
        }
      }

      switch (i) {
        case 0:
          for (int q=0; q<size; q++) {
            rgbPixels[q] = (rgbPixels[q] & 0xff00ffff) | reds[pixels[q] & 0xff];
          }
          break;
        case 1:
          for (int q=0; q<size; q++) {
            rgbPixels[q] =
              (rgbPixels[q] & 0xffff00ff) | greens[pixels[q] & 0xff];
          }
          break;
        case 2:
          for (int q=0; q<size; q++) {
            rgbPixels[q] =
              (rgbPixels[q] & 0xffffff00) | blues[pixels[q] & 0xff];
          }
          break;
        case 3:
          for (int q=0; q<size; q++) {
            int red = reds[pixels[q] & 0xff];
            int green = greens[pixels[q] & 0xff];
            int blue = blues[pixels[q] & 0xff];
            rgbPixels[q] = red | green | blue;
          }
          break;
        default:
          for (int q=0; q<size; q++) {
            int pixel = rgbPixels[q];
            int red = (pixel & 0xff0000) + reds[pixels[q] & 0xff];
            int green = (pixel & 0x00ff00) + greens[pixels[q] & 0xff];
            int blue = (pixel & 0xff) + blues[pixels[q] & 0xff];
            if (red > 16711680) red = 16711680;
            if (green > 65280) green = 65280;
            if (blue > 255) blue = 255;
            rgbPixels[q] = red | green | blue;
          }
      }

    }

    DirectColorModel rgb = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
    MemoryImageSource src = new MemoryImageSource(proc.getWidth(),
      proc.getHeight(), rgb, rgbPixels, 0, proc.getWidth());
    src.setAnimated(true);
    src.setFullBufferUpdates(true);
    return Toolkit.getDefaultToolkit().createImage(src);
  }

  public ImageProcessor createProcessor(int width, int height) {
    record("createProcessor", new Object[] {new Integer(width),
      new Integer(height)}, new Class[] {int.class, int.class});
    return proc.createProcessor(width, height);
  }

  public ImageProcessor crop() {
    record("crop");
    return proc.crop();
  }

  public void dilate() {
    record("dilate");
    proc.dilate();
  }

  public void drawDot(int xcenter, int ycenter) {
    record("drawDot", new Object[] {new Integer(xcenter),
      new Integer(ycenter)}, new Class[] {int.class, int.class});
    proc.drawDot(xcenter, ycenter);
  }

  public void drawLine(int x1, int y1, int x2, int y2) {
    record("drawLine", new Object[] {new Integer(x1), new Integer(y1),
      new Integer(x2), new Integer(y2)}, new Class[] {int.class, int.class,
      int.class, int.class});
    proc.drawLine(x1, y1, x2, y2);
  }

  public void drawOval(int x, int y, int width, int height) {
    record("drawOval", new Object[] {new Integer(x), new Integer(y),
      new Integer(width), new Integer(height)}, new Class[] {int.class,
      int.class, int.class, int.class});
    proc.drawOval(x, y, width, height);
  }

  public void drawPixel(int x, int y) {
    record("drawPixel", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    proc.drawPixel(x, y);
  }

  public void drawPolygon(Polygon p) {
    record("drawPolygon", p, Polygon.class);
    proc.drawPolygon(p);
  }

  public void drawRect(int x, int y, int width, int height) {
    record("drawRect", new Object[] {new Integer(x), new Integer(y),
      new Integer(width), new Integer(height)}, new Class[] {int.class,
      int.class, int.class, int.class});
    proc.drawRect(x, y, width, height);
  }

  public void drawString(String s) {
    record("drawString", s, String.class);
    proc.drawString(s);
  }

  public void drawString(String s, int x, int y) {
    record("drawString", new Object[] {s, new Integer(x), new Integer(y)},
      new Class[] {String.class, int.class, int.class});
    proc.drawString(s, x, y);
  }

  public ImageProcessor duplicate() {
    record("duplicate");
    return proc.duplicate();
  }

  public void erode() {
    record("erode");
    proc.erode();
  }

  public void exp() {
    record("exp");
    proc.exp();
  }

  public void fill() {
    record("fill");
    proc.fill();
  }

  public void fill(ImageProcessor mask) {
    record("fill", mask, ImageProcessor.class);
    proc.fill(mask);
  }

  public void fillOval(int x, int y, int width, int height) {
    record("fillOval", new Object[] {new Integer(x), new Integer(y),
      new Integer(width), new Integer(height)}, new Class[] {int.class,
      int.class, int.class, int.class});
    proc.fillOval(x, y, width, height);
  }

  public void fillPolygon(Polygon p) {
    record("fillPolygon", p, Polygon.class);
    proc.fillPolygon(p);
  }

  public void filter(int type) {
    record("filter", new Integer(type), int.class);
    proc.filter(type);
  }

  public void findEdges() {
    record("findEdges");
    proc.findEdges();
  }

  public void flipHorizontal() {
    record("flipHorizontal");
    proc.flipHorizontal();
  }

  public void flipVertical() {
    record("flipVertical");
    proc.flipVertical();
  }

  public void gamma(double value) {
    record("gamma", new Double(value), double.class);
    proc.gamma(value);
  }

  public int get(int index) {
    record("get", new Integer(index), int.class);
    return proc.get(index);
  }

  public int get(int x, int y) {
    record("get", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    return proc.get(x, y);
  }

  public int getAutoThreshold() {
    record("getAutoThreshold");
    return proc.getAutoThreshold();
  }

  public double getBackgroundValue() {
    record("getBackgroundValue");
    return proc.getBackgroundValue();
  }

  public int getAutoThreshold(int[] histogram) {
    record("getAutoThreshold", histogram, int[].class);
    return proc.getAutoThreshold(histogram);
  }

  public int getBestIndex(Color c) {
    record("getBestIndex", c, Color.class);
    return proc.getBestIndex(c);
  }

  public BufferedImage getBufferedImage() {
    record("getBufferedImage");
    return proc.getBufferedImage();
  }

  public float[] getCalibrationTable() {
    record("getCalibrationTable");
    return proc.getCalibrationTable();
  }

  public ColorModel getColorModel() {
    record("getColorModel");
    return proc.getColorModel();
  }

  public void getColumn(int x, int y, int[] data, int length) {
    record("getColumn", new Object[] {new Integer(x), new Integer(y), data,
      new Integer(length)}, new Class[] {int.class, int.class, int[].class,
      int.class});
    proc.getColumn(x, y, data, length);
  }

  public ColorModel getCurrentColorModel() {
    record("getCurrentColorModel");
    return proc.getCurrentColorModel();
  }

  public IndexColorModel getDefaultColorModel() {
    record("getDefaultColorModel");
    return proc.getDefaultColorModel();
  }

  public float getf(int index) {
    record("getf", new Integer(index), int.class);
    return proc.getf(index);
  }

  public float getf(int x, int y) {
    record("getf", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    return proc.getf(x, y);
  }

  public float[][] getFloatArray() {
    record("getFloatArray");
    return proc.getFloatArray();
  }

  public FontMetrics getFontMetrics() {
    record("getFontMetrics");
    return proc.getFontMetrics();
  }

  public int getHeight() {
    record("getHeight");
    return proc.getHeight();
  }

  public int[] getHistogram() {
    record("getHistogram");
    return proc.getHistogram();
  }

  public double getHistogramMax() {
    record("getHistogramMax");
    return proc.getHistogramMax();
  }

  public double getHistogramMin() {
    record("getHistogramMin");
    return proc.getHistogramMin();
  }

  public int getHistogramSize() {
    record("getHistogramSize");
    return proc.getHistogramSize();
  }

  public int[][] getIntArray() {
    record("getIntArray");
    return proc.getIntArray();
  }

  public boolean getInterpolate() {
    record("getInterpolate");
    return proc.getInterpolate();
  }

  public double getInterpolatedPixel(double x, double y) {
    record("getInterpolatedPixel", new Object[] {new Double(x), new Double(y)},
      new Class[] {double.class, double.class});
    return proc.getInterpolatedPixel(x, y);
  }

  public double[] getLine(double x1, double y1, double x2, double y2) {
    record("getLine", new Object[] {new Double(x1), new Double(y1),
      new Double(x2), new Double(y2)}, new Class[] {double.class, double.class,
      double.class, double.class});
    return proc.getLine(x1, y1, x2, y2);
  }

  public int getLutUpdateMode() {
    record("getLutUpdateMode");
    return proc.getLutUpdateMode();
  }

  public ImageProcessor getMask() {
    record("getMask");
    return proc.getMask();
  }

  public byte[] getMaskArray() {
    record("getMaskArray");
    return proc.getMaskArray();
  }

  public double getMax() {
    record("getMax");
    return proc.getMax();
  }

  public double getMaxThreshold() {
    record("getMaxThreshold");
    return proc.getMaxThreshold();
  }

  public double getMin() {
    record("getMin");
    return proc.getMin();
  }

  public double getMinThreshold() {
    record("getMinThreshold");
    return proc.getMinThreshold();
  }

  public int getNChannels() {
    record("getNChannels");
    return proc.getNChannels();
  }

  public int getPixel(int x, int y) {
    record("getPixel", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    return proc.getPixel(x, y);
  }

  public int[] getPixel(int x, int y, int[] iArray) {
    record("getPixel", new Object[] {new Integer(x), new Integer(y), iArray},
      new Class[] {int.class, int.class, int[].class});
    return proc.getPixel(x, y, iArray);
  }

  public int getPixelInterpolated(double x, double y) {
    record("getPixelInterpolated", new Object[] {new Double(x), new Double(y)},
      new Class[] {double.class, double.class});
    return proc.getPixelInterpolated(x, y);
  }

  public int getPixelCount() {
    record("getPixelCount");
    return proc.getPixelCount();
  }

  public Object getPixels() {
    record("getPixels");
    return proc.getPixels();
  }

  public Object getPixelsCopy() {
    record("getPixelsCopy");
    return proc.getPixelsCopy();
  }

  public float getPixelValue(int x, int y) {
    record("getPixelValue", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    return proc.getPixelValue(x, y);
  }

  public Rectangle getRoi() {
    record("getRoi");
    return proc.getRoi();
  }

  public void getRow(int x, int y, int[] data, int length) {
    record("getRow", new Object[] {new Integer(x), new Integer(y), data,
      new Integer(length)}, new Class[] {int.class, int.class, int[].class,
      int.class});
    proc.getRow(x, y, data, length);
  }

  public Object getSnapshotPixels() {
    record("getSnapshotPixels");
    return proc.getSnapshotPixels();
  }

  public int getStringWidth(String s) {
    record("getStringWidth", s, String.class);
    return proc.getStringWidth(s);
  }

  public int getWidth() {
    record("getWidth");
    return proc.getWidth();
  }

  public void insert(ImageProcessor ip, int xloc, int yloc) {
    record("insert", new Object[] {ip, new Integer(xloc), new Integer(yloc)},
      new Class[] {ImageProcessor.class, int.class, int.class});
    proc.insert(ip, xloc, yloc);
  }

  public void invert() {
    record("invert");
    proc.invert();
  }

  public void invertLut() {
    record("invertLut");
    proc.invertLut();
  }

  public boolean isColorLut() {
    record("isColorLut");
    return proc.isColorLut();
  }

  public boolean isInvertedLut() {
    record("isInvertedLut");
    return proc.isInvertedLut();
  }

  public boolean isPseudoColorLut() {
    record("isPseudoColorLut");
    return proc.isPseudoColorLut();
  }

  public void lineTo(int x2, int y2) {
    record("lineTo", new Object[] {new Integer(x2), new Integer(y2)},
      new Class[] {int.class, int.class});
    proc.lineTo(x2, y2);
  }

  public void log() {
    record("log");
    proc.log();
  }

  public void max(double value) {
    record("max", new Double(value), double.class);
    proc.max(value);
  }

  public double maxValue() {
    record("maxValue");
    return proc.maxValue();
  }

  public void medianFilter() {
    record("medianFilter");
    proc.medianFilter();
  }

  public void min(double value) {
    record("min", new Double(value), double.class);
    proc.min(value);
  }

  public double minValue() {
    record("minValue");
    return proc.minValue();
  }

  public void moveTo(int x, int y) {
    record("moveTo", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    proc.moveTo(x, y);
  }

  public void multiply(double value) {
    record("multiply", new Double(value), double.class);
    proc.multiply(value);
  }

  public void noise(double range) {
    record("noise", new Double(range), double.class);
    proc.noise(range);
  }

  public void or(int value) {
    record("or", new Integer(value), int.class);
    proc.or(value);
  }

  public void putColumn(int x, int y, int[] data, int length) {
    record("putColumn", new Object[] {new Integer(x), new Integer(y), data,
      new Integer(length)}, new Class[] {int.class, int.class, int[].class,
      int.class});
    proc.putColumn(x, y, data, length);
  }

  public void putPixel(int x, int y, int value) {
    record("putPixel", new Object[] {new Integer(x), new Integer(y),
      new Integer(value)}, new Class[] {int.class, int.class, int.class});
    proc.putPixel(x, y, value);
  }

  public void putPixel(int x, int y, int[] iArray) {
    record("putPixel", new Object[] {new Integer(x), new Integer(y), iArray},
      new Class[] {int.class, int.class, int[].class});
    proc.putPixel(x, y, iArray);
  }

  public void putPixelValue(int x, int y, double value) {
    record("putPixelValue", new Object[] {new Integer(x), new Integer(y),
      new Double(value)}, new Class[] {int.class, int.class, double.class});
    proc.putPixelValue(x, y, value);
  }

  public void putRow(int x, int y, int[] data, int length) {
    record("putRow", new Object[] {new Integer(x), new Integer(y), data,
      new Integer(length)}, new Class[] {int.class, int.class, int[].class,
      int.class});
    proc.putRow(x, y, data, length);
  }

  public void reset() {
    record("reset");
    proc.reset();
  }

  public void reset(ImageProcessor mask) {
    record("reset", mask, ImageProcessor.class);
    proc.reset(mask);
  }

  public void resetBinaryThreshold() {
    record("resetBinaryThreshold");
    proc.resetBinaryThreshold();
  }

  public void resetMinAndMax() {
    record("resetMinAndMax");
    proc.resetMinAndMax();
  }

  public void resetRoi() {
    record("resetRoi");
    proc.resetRoi();
  }

  public void resetThreshold() {
    record("resetThreshold");
    proc.resetThreshold();
  }

  public ImageProcessor resize(int dstWidth) {
    record("resize", new Integer(dstWidth), int.class);
    return proc.resize(dstWidth);
  }

  public ImageProcessor resize(int dstWidth, int dstHeight) {
    record("resize", new Object[] {new Integer(dstWidth),
      new Integer(dstHeight)}, new Class[] {int.class, int.class});
    return proc.resize(dstWidth, dstHeight);
  }

  public void rotate(double angle) {
    record("rotate", new Double(angle), double.class);
    proc.rotate(angle);
  }

  public ImageProcessor rotateLeft() {
    record("rotateLeft");
    return proc.rotateLeft();
  }

  public ImageProcessor rotateRight() {
    record("rotateRight");
    return proc.rotateRight();
  }

  public void scale(double xScale, double yScale) {
    record("scale", new Object[] {new Double(xScale), new Double(yScale)},
      new Class[] {double.class, double.class});
    proc.scale(xScale, yScale);
  }

  public void set(int index, int value) {
    record("set", new Object[] {new Integer(index), new Integer(value)},
      new Class[] {int.class, int.class});
    proc.set(index, value);
  }

  public void set(int x, int y, int value) {
    record("set", new Object[] {new Integer(x), new Integer(y),
      new Integer(value)}, new Class[] {int.class, int.class, int.class});
    proc.set(x, y, value);
  }

  public void setAntialiasedText(boolean antialiased) {
    record("setAntialiasedText", new Boolean(antialiased), boolean.class);
    proc.setAntialiasedText(antialiased);
  }

  public void setAutoThreshold(int method, int lutUpdate) {
    record("setAutoThreshold", new Object[] {new Integer(method),
      new Integer(lutUpdate)}, new Class[] {int.class, int.class});
    proc.setAutoThreshold(method, lutUpdate);
  }

  public void setBackgroundValue(double value) {
    record("setBackgroundValue", new Double(value), double.class);
    proc.setBackgroundValue(value);
  }

  public void setCalibrationTable(float[] ctable) {
    record("setCalibrationTable", ctable, float[].class);
    proc.setCalibrationTable(ctable);
  }

  public void setClipRect(Rectangle clipRect) {
    record("setClipRect", clipRect, Rectangle.class);
    proc.setClipRect(clipRect);
  }

  public void setColor(Color color) {
    record("setColor", color, Color.class);
    proc.setColor(color);
  }

  public void setColor(int value) {
    record("setColor", new Integer(value), int.class);
    proc.setColor(value);
  }

  public void setColorModel(ColorModel cm) {
    record("setColorModel", cm, ColorModel.class);
    proc.setColorModel(cm);
  }

  public void setf(int index, float value) {
    record("setf", new Object[] {new Integer(index), new Float(value)},
      new Class[] {int.class, float.class});
    proc.setf(index, value);
  }

  public void setf(int x, int y, float value) {
    record("setf", new Object[] {new Integer(x), new Integer(y),
      new Float(value)}, new Class[] {int.class, int.class, float.class});
    proc.setf(x, y, value);
  }

  public void setFloatArray(float[][] a) {
    record("setFloatArray", a, float[][].class);
    proc.setFloatArray(a);
  }

  public void setFont(Font font) {
    record("setFont", font, Font.class);
    proc.setFont(font);
  }

  public void setHistogramRange(double histMin, double histMax) {
    record("setHistogramRange", new Object[] {new Double(histMin),
      new Double(histMax)}, new Class[] {double.class, double.class});
    proc.setHistogramRange(histMin, histMax);
  }

  public void setHistogramSize(int size) {
    record("setHistogramSize", new Integer(size), int.class);
    proc.setHistogramSize(size);
  }

  public void setIntArray(int[][] a) {
    record("setIntArray", a, int[][].class);
    proc.setIntArray(a);
  }

  public void setInterpolate(boolean interpolate) {
    record("setInterpolate", new Boolean(interpolate), boolean.class);
    proc.setInterpolate(interpolate);
  }

  public void setJustification(int justification) {
    record("setJustification", new Integer(justification), int.class);
    proc.setJustification(justification);
  }

  public void setLineWidth(int width) {
    record("setLineWidth", new Integer(width), int.class);
    proc.setLineWidth(width);
  }

  public void setLutAnimation(boolean lutAnimation) {
    record("setLutAnimation", new Boolean(lutAnimation), boolean.class);
    proc.setLutAnimation(lutAnimation);
  }

  public void setMask(ImageProcessor mask) {
    record("setMask", mask, ImageProcessor.class);
    proc.setMask(mask);
  }

  public void setMinAndMax(double min, double max) {
    record("setMinAndMax", new Object[] {new Double(min), new Double(max)},
      new Class[] {double.class, double.class});
    proc.setMinAndMax(min, max);
  }

  public void setPixels(int channelNumber, FloatProcessor fp) {
    record("setPixels", new Object[] {new Integer(channelNumber), fp},
      new Class[] {int.class, FloatProcessor.class});
    proc.setPixels(channelNumber, fp);
  }

  public void setPixels(Object pixels) {
    record("setPixels", pixels, Object.class);
    proc.setPixels(pixels);
  }

  public void setProgressBar(ProgressBar pb) {
    record("setProgressBar", pb, ProgressBar.class);
    proc.setProgressBar(pb);
  }

  public void setRoi(int x, int y, int rwidth, int rheight) {
    record("setRoi", new Object[] {new Integer(x), new Integer(y),
      new Integer(rwidth), new Integer(rheight)}, new Class[] {int.class,
      int.class, int.class, int.class});
    proc.setRoi(x, y, rwidth, rheight);
  }

  public void setRoi(Polygon roi) {
    record("setRoi", roi, Polygon.class);
    proc.setRoi(roi);
  }

  public void setRoi(Rectangle roi) {
    record("setRoi", roi, Rectangle.class);
    proc.setRoi(roi);
  }

  public void setRoi(Roi roi) {
    record("setRoi", roi, Roi.class);
    proc.setRoi(roi);
  }

  public void setSnapshotCopyMode(boolean b) {
    record("setSnapshotCopyMode", new Boolean(b), boolean.class);
    proc.setSnapshotCopyMode(b);
  }

  public void setSnapshotPixels(Object pixels) {
    record("setSnapshotPixels", pixels, Object.class);
    proc.setSnapshotPixels(pixels);
  }

  public void setThreshold(double minThreshold, double maxThreshold,
    int lutUpdate)
  {
    record("setThreshold", new Object[] {new Double(minThreshold),
      new Double(maxThreshold), new Integer(lutUpdate)},
      new Class[] {double.class, double.class, int.class});
    proc.setThreshold(minThreshold, maxThreshold, lutUpdate);
  }

  public void setValue(double value) {
    record("setValue", new Double(value), double.class);
    proc.setValue(value);
  }

  public void sharpen() {
    record("sharpen");
    proc.sharpen();
  }

  public void smooth() {
    record("smooth");
    proc.smooth();
  }

  public void snapshot() {
    record("snapshot");
    proc.snapshot();
  }

  public void sqr() {
    record("sqr");
    proc.sqr();
  }

  public void sqrt() {
    record("sqrt");
    proc.sqrt();
  }

  public void swapPixelArrays() {
    record("swapPixelArrays");
    proc.swapPixelArrays();
  }

  public void threshold(int level) {
    record("threshold", new Integer(level), int.class);
    proc.threshold(level);
  }

  public FloatProcessor toFloat(int channelNumber, FloatProcessor fp) {
    record("toFloat", new Object[] {new Integer(channelNumber), fp},
      new Class[] {int.class, FloatProcessor.class});
    return proc.toFloat(channelNumber, fp);
  }

  public String toString() {
    record("toString");
    return proc.toString();
  }

  public void translate(int xOffset, int yOffset) {
    record("translate",
      new Object[] {new Integer(xOffset), new Integer(yOffset)},
      new Class[] {int.class, int.class});
    proc.translate(xOffset, yOffset);
  }

  public void updateComposite(int[] rgbPixels, int channel) {
    record("updateComposite", new Object[] {rgbPixels, new Integer(channel)},
      new Class[] {int[].class, int.class});
    proc.updateComposite(rgbPixels, channel);
  }

  public void xor(int value) {
    record("xor", new Integer(value), int.class);
    proc.xor(value);
  }

  // -- Deprecated methods --

  /** @deprecated */
  public void drawDot2(int x, int y) {
    record("drawDot2", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    proc.drawDot2(x, y);
  }

  /** @deprecated */
  public boolean isKillable() {
    record("isKillable");
    return proc.isKillable();
  }

  // -- Helper methods --

  private void record(String method) {
    record(method, (Object[]) null, (Class<?>) null);
  }

  private void record(String method, Object v, Class<?> c) {
    record(method, v == null ? null : new Object[] {v},
      c == null ? null : new Class<?>[] {c});
  }

  private void record(String method, Object[] v, Class<?>[] c) {
    if (!doRecording) return;

    MethodEntry m = new MethodEntry();
    m.name = method;
    m.args = v;
    if (v == null && c != null) {
      m.args = new Object[c.length];
      for (int i=0; i<m.args.length; i++) {
        m.args[i] = null;
      }
    }
    try {
      m.method = proc.getClass().getMethod(method, c);
    }
    catch (NoSuchMethodException e) {
    }
    methodStack.add(m);
  }

  // -- Helper class --

  class MethodEntry {
    public String name;
    public Object[] args;
    public Method method;
  }

}
