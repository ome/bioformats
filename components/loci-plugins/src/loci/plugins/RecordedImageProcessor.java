//
// RecordedImageProcessor.java
//

package loci.plugins;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.process.*;

public class RecordedImageProcessor extends ImageProcessor {

  // -- Fields --

  private ImageProcessor proc;
  private boolean doRecording;
  private Vector methodStack;
  private int sliceNumber;

  private int channelNumber;
  private ImageProcessor[] otherChannels;

  // -- Constructor --

  public RecordedImageProcessor(ImageProcessor proc, int num) {
    this.proc = proc;
    methodStack = new Vector();
    doRecording = true;
    sliceNumber = num;
  }

  public RecordedImageProcessor(ImageProcessor proc, int num, int channelNum,
    ImageProcessor[] otherChannels)
  {
    this.proc = proc;
    methodStack = new Vector();
    doRecording = true;
    sliceNumber = num;
    this.channelNumber = channelNum;
    this.otherChannels = otherChannels;
  }

  // -- RecordedImageProcessor API methods --

  public void setDoRecording(boolean doRecording) {
    this.doRecording = doRecording;
  }

  public Vector getMethodStack() {
    return methodStack;
  }

  public void applyMethodStack(Vector stack) {
    for (int i=0; i<stack.size(); i++) {
      MethodEntry m = (MethodEntry) stack.get(i);
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
    record("abs", null, (Class) null);
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
    record("autoThreshold", null, (Class) null);
    proc.autoThreshold();
  }

  public ImageProcessor convertToByte(boolean doScaling) {
    record("convertToByte", new Boolean(doScaling), boolean.class);
    return proc.convertToByte(doScaling);
  }

  public ImageProcessor convertToFloat() {
    record("convertToFloat", null, (Class) null);
    return proc.convertToFloat();
  }

  public ImageProcessor convertToRGB() {
    record("convertToRGB", null, (Class) null);
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
    record("crop", null, (Class) null);
    return proc.crop();
  }

  public void dilate() {
    record("dilate", null, (Class) null);
    proc.dilate();
  }

  public void drawDot(int xcenter, int ycenter) {
    record("drawDot", new Object[] {new Integer(xcenter),
      new Integer(ycenter)}, new Class[] {int.class, int.class});
    proc.drawDot(xcenter, ycenter);
  }

  public void drawDot2(int x, int y) {
    record("drawDot2", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    proc.drawDot2(x, y);
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
    record("duplicate", null, (Class) null);
    return proc.duplicate();
  }

  public void erode() {
    record("erode", null, (Class) null);
    proc.erode();
  }

  public void exp() {
    record("exp", null, (Class) null);
    proc.exp();
  }

  public void fill() {
    record("fill", null, (Class) null);
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
    record("findEdges", null, (Class) null);
    proc.findEdges();
  }

  public void flipHorizontal() {
    record("flipHorizontal", null, (Class) null);
    proc.flipHorizontal();
  }

  public void flipVertical() {
    record("flipVertical", null, (Class) null);
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
    record("getAutoThreshold", null, (Class) null);
    return proc.getAutoThreshold();
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
    record("getBufferedImage", null, (Class) null);
    return proc.getBufferedImage();
  }

  public float[] getCalibrationTable() {
    record("getCalibrationTable", null, (Class) null);
    return proc.getCalibrationTable();
  }

  public ColorModel getColorModel() {
    record("getColorModel", null, (Class) null);
    return proc.getColorModel();
  }

  public void getColumn(int x, int y, int[] data, int length) {
    record("getColumn", new Object[] {new Integer(x), new Integer(y), data,
      new Integer(length)}, new Class[] {int.class, int.class, int[].class,
      int.class});
    proc.getColumn(x, y, data, length);
  }

  public ColorModel getCurrentColorModel() {
    record("getCurrentColorModel", null, (Class) null);
    return proc.getCurrentColorModel();
  }

  public IndexColorModel getDefaultColorModel() {
    record("getDefaultColorModel", null, (Class) null);
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
    record("getFloatArray", null, (Class) null);
    return proc.getFloatArray();
  }

  public FontMetrics getFontMetrics() {
    record("getFontMetrics", null, (Class) null);
    return proc.getFontMetrics();
  }

  public int getHeight() {
    record("getHeight", null, (Class) null);
    return proc.getHeight();
  }

  public int[] getHistogram() {
    record("getHistogram", null, (Class) null);
    return proc.getHistogram();
  }

  public double getHistogramMax() {
    record("getHistogramMax", null, (Class) null);
    return proc.getHistogramMax();
  }

  public double getHistogramMin() {
    record("getHistogramMin", null, (Class) null);
    return proc.getHistogramMin();
  }

  public int getHistogramSize() {
    record("getHistogramSize", null, (Class) null);
    return proc.getHistogramSize();
  }

  public int[][] getIntArray() {
    record("getIntArray", null, (Class) null);
    return proc.getIntArray();
  }

  public boolean getInterpolate() {
    record("getInterpolate", null, (Class) null);
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
    record("getLutUpdateMode", null, (Class) null);
    return proc.getLutUpdateMode();
  }

  public ImageProcessor getMask() {
    record("getMask", null, (Class) null);
    return proc.getMask();
  }

  public byte[] getMaskArray() {
    record("getMaskArray", null, (Class) null);
    return proc.getMaskArray();
  }

  public double getMax() {
    record("getMax", null, (Class) null);
    return proc.getMax();
  }

  public double getMaxThreshold() {
    record("getMaxThreshold", null, (Class) null);
    return proc.getMaxThreshold();
  }

  public double getMin() {
    record("getMin", null, (Class) null);
    return proc.getMin();
  }

  public double getMinThreshold() {
    record("getMinThreshold", null, (Class) null);
    return proc.getMinThreshold();
  }

  public int getNChannels() {
    record("getNChannels", null, (Class) null);
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

  public int getPixelCount() {
    record("getPixelCount", null, (Class) null);
    return proc.getPixelCount();
  }

  public Object getPixels() {
    record("getPixels", null, (Class) null);
    return proc.getPixels();
  }

  public Object getPixelsCopy() {
    record("getPixelsCopy", null, (Class) null);
    return proc.getPixelsCopy();
  }

  public float getPixelValue(int x, int y) {
    record("getPixelValue", new Object[] {new Integer(x), new Integer(y)},
      new Class[] {int.class, int.class});
    return proc.getPixelValue(x, y);
  }

  public Rectangle getRoi() {
    record("getRoi", null, (Class) null);
    return proc.getRoi();
  }

  public void getRow(int x, int y, int[] data, int length) {
    record("getRow", new Object[] {new Integer(x), new Integer(y), data,
      new Integer(length)}, new Class[] {int.class, int.class, int[].class,
      int.class});
    proc.getRow(x, y, data, length);
  }

  public Object getSnapshotPixels() {
    record("getSnapshotPixels", null, (Class) null);
    return proc.getSnapshotPixels();
  }

  public int getStringWidth(String s) {
    record("getStringWidth", s, String.class);
    return proc.getStringWidth(s);
  }

  public int getWidth() {
    record("getWidth", null, (Class) null);
    return proc.getWidth();
  }

  public void insert(ImageProcessor ip, int xloc, int yloc) {
    record("insert", new Object[] {ip, new Integer(xloc), new Integer(yloc)},
      new Class[] {ImageProcessor.class, int.class, int.class});
    proc.insert(ip, xloc, yloc);
  }

  public void invert() {
    record("invert", null, (Class) null);
    proc.invert();
  }

  public void invertLut() {
    record("invertLut", null, (Class) null);
    proc.invertLut();
  }

  public boolean isColorLut() {
    record("isColorLut", null, (Class) null);
    return proc.isColorLut();
  }

  public boolean isInvertedLut() {
    record("isInvertedLut", null, (Class) null);
    return proc.isInvertedLut();
  }

  public boolean isKillable() {
    record("isKillable", null, (Class) null);
    return proc.isKillable();
  }

  public boolean isPseudoColorLut() {
    record("isPseudoColorLut", null, (Class) null);
    return proc.isPseudoColorLut();
  }

  public void lineTo(int x2, int y2) {
    record("lineTo", new Object[] {new Integer(x2), new Integer(y2)},
      new Class[] {int.class, int.class});
    proc.lineTo(x2, y2);
  }

  public void log() {
    record("log", null, (Class) null);
    proc.log();
  }

  public void max(double value) {
    record("max", new Double(value), double.class);
    proc.max(value);
  }

  public double maxValue() {
    record("maxValue", null, (Class) null);
    return proc.maxValue();
  }

  public void medianFilter() {
    record("medianFilter", null, (Class) null);
    proc.medianFilter();
  }

  public void min(double value) {
    record("min", new Double(value), double.class);
    proc.min(value);
  }

  public double minValue() {
    record("minValue", null, (Class) null);
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
    record("reset", null, (Class) null);
    proc.reset();
  }

  public void reset(ImageProcessor mask) {
    record("reset", mask, ImageProcessor.class);
    proc.reset(mask);
  }

  public void resetBinaryThreshold() {
    record("resetBinaryThreshold", null, (Class) null);
    proc.resetBinaryThreshold();
  }

  public void resetMinAndMax() {
    record("resetMinAndMax", null, (Class) null);
    proc.resetMinAndMax();
  }

  public void resetRoi() {
    record("resetRoi", null, (Class) null);
    proc.resetRoi();
  }

  public void resetThreshold() {
    record("resetThreshold", null, (Class) null);
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
    record("rotateLeft", null, (Class) null);
    return proc.rotateLeft();
  }

  public ImageProcessor rotateRight() {
    record("rotateRight", null, (Class) null);
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
    record("sharpen", null, (Class) null);
    proc.sharpen();
  }

  public void smooth() {
    record("smooth", null, (Class) null);
    proc.smooth();
  }

  public void snapshot() {
    record("snapshot", null, (Class) null);
    proc.snapshot();
  }

  public void sqr() {
    record("sqr", null, (Class) null);
    proc.sqr();
  }

  public void sqrt() {
    record("sqrt", null, (Class) null);
    proc.sqrt();
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
    record("toString", null, (Class) null);
    return proc.toString();
  }

  public void translate(int xOffset, int yOffset, boolean eraseBackground) {
    record("translate", new Object[] {new Integer(xOffset),
      new Integer(yOffset), new Boolean(eraseBackground)}, new Class[] {
      int.class, int.class, boolean.class});
    proc.translate(xOffset, yOffset, eraseBackground);
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

  // -- Helper methods --

  private void record(String method, Object v, Class c) {
    record(method, v == null ? null : new Object[] {v},
      c == null ? null : new Class[] {c});
  }

  private void record(String method, Object[] v, Class[] c) {
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
