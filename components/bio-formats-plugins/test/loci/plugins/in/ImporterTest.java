/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

package loci.plugins.in;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.IOException;

import loci.common.Location;
import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.plugins.BF;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO

// left off
//   should be able to make colorTests() handle both ImagePluses and CompositeImages. Change lutTest() too. Then can get
//     rid of ImagePlusLutTest().
//   the color test methods seem to fail for indexed data (my issue - need to fix indexValuesTest for special cases)
//     the special case is 1/1/indexed w/lutLen=3. 2/3's of values are all 0. This is correct behavior.
//   special note: the colorDefault code has a COLOR case (indexed and wantLutDefined both true) that is not correct.
//     In fact in the whole method need better case logic to create the three possible modes and then test for them.
//     I cannot get BF to set hasChannelLut true so far and this kills the COLOR mode. Also with indexed I've gotten
//     back COMPOSITE when expecting GRAYSCALE (or vice versa, I don't remember at the moment) because I have no test
//     that matches the channelFiller.isFilled() case in BF.
//   the color test methods seem to fail for virtual=true (no longer seems to be case - something Curtis fixed?)
//   datasetSwapDims has ugly workaround to handle bad signed data / virtual flag interaction. Need BF fix.
//     (Curtis may have fixed. Existing code hacked to work until I know more.)
//   many test methods are only UINT8
//   expand compositeTestSubcases() to handle more pixTypes and indexed data
//   implement more combo tests
//   maybe phase out *Index(ImageProcessor) to support indexed data
//   add pixelsTest() where necessary
//   perhaps refactor the various imageSeriesIn... and ImagesIn... order tests into a general tester and an orderBy param

// broken
//  comboCropAndAutoscale for INT32. I think its a limitation of Fake. The values of the cropped image are less
//    than the minimum representable value of an int as a float. So when we make a FloatProcessor on the int[] data
//    the huge negative values get clamped to the lowest representable point and thus max and min are not set correctly
//    by IJ. I have verified that the pixel data that is sent to FloatProcessor() is correct. Limitation we'll live
//    with I guess.

// (OLD) seem broken but don't know status from Curtis
//   colorized: 1/1/indexed (all indices 0 for all images), 3/1/indexed (iIndex,cIndex) (although w/ falseColor its okay),
//     6/3/nonindexed (iIndex,cIndex), 12/3/nonindexed (iIndex,cIndex), 3/3/indexed (iIndex,cIndex)
//   record does not work

// note
//   I changed MemoryRecord from "Flip Horizontally" (which could not be found at runtime) to "Invert". Verified
//     "Invert" is recordable and record("invert") getting called but doRecording is false for some reason. Also Curtis
//     thought flip would be easier to use for predicting actual values rather than special case code with invert. As
//     I'm only doing UINT8 for now this is not a problem.

// (OLD) waiting on BF implementations for
//   - indexed color support

// must address before release

//  - macros should still work like before
//  - add some tests for combination of options
//      comboConcatSplit() - done and passing
//      comboManyOptions - done and passing
//      other combo tests - rely on color code working. Waiting for BF. (OLD)

// would be nice to address before release

//    - BF/imageJ returning wrong values of max num pixels (UINT32 off by one - IJ bug, float weird too, etc.)
//    - memoryRecord failure (needs BF code fix)
//    - open individual files: try to come up with a way to test without a disk file as source
//    - output stack order - testing of iIndex? should match imagestack number? i.e. 5th plane == 4 - doesn't look so
//    - improve, comment, and generalize code for increased coverage

/**
 * A class for testing the Bio-Formats Importer behavior.
 *
 * @author Barry DeZonia bdezonia at wisc.edu
 */
public class ImporterTest {

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(ImporterTest.class);

  /**
   * Whether to run tests with special requirements.
   * This flag is mainly to disable the testDatasetOpenFilesIndividually test
   * on systems without the required Bio-Rad PIC sample data available.
   */
  static final boolean RUN_SPECIAL_TESTS = false;

  private enum Axis {Z,C,T};

  private enum ChannelOrder {ZCT, ZTC, CZT, CTZ, TZC, TCZ};

  private static final boolean[] BOOLEAN_STATES = new boolean[] {false, true};
  private static final int[] MINIMAL_PIXEL_TYPES = new int[] {
    FormatTools.UINT8, FormatTools.UINT16};

  private static Color[] DEFAULT_COLOR_ORDER =
    new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.CYAN, Color.MAGENTA, Color.YELLOW};

  private static Color[] CUSTOM_COLOR_ORDER =
    new Color[] {Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.WHITE};

  private static final boolean NOT_INDEXED = false;
  private static final boolean INDEXED = true;

  private static final boolean FALSE_COLOR = true;
  private static final boolean REAL_COLOR = false;

  private static final int ONE_SERIES = 1;

  private static final String[] FAKE_FILES;
  private static final String FAKE_PATTERN;

  private static final int FAKE_PLANE_COUNT = 7;
  private static final int FAKE_CHANNEL_COUNT = 3;
  private static final int FAKE_TIMEPOINT_COUNT = 5;
  private static final int FAKE_SIZE_X = 50;
  private static final int FAKE_SIZE_Y = 50;

  static {
    //String template = "test_C%s_TP%s&sizeX=50&sizeY=20&sizeZ=7.fake";
    String template = constructFakeFilename("test_C%s_TP%s", FormatTools.UINT8, FAKE_SIZE_X, FAKE_SIZE_Y, FAKE_PLANE_COUNT, 1, 1,
                        -1, false, -1, false, -1);

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
      String.format(template, "3", "5")
    };
    FAKE_PATTERN = String.format(template, "<1-3>", "<1-5>");

    for (String file : FAKE_FILES) {
      Location.mapId(file, "iThinkI'mImportantButI'mNot");
    }
  }

  // ** Helper methods *******************************************************************

  private static String constructFakeFilename(String title,
      int pixelType, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT, int numSeries,
      boolean indexed, int rgb, boolean falseColor, int lutLength)
  {
    // some tests rely on each image being large enough to get the s,i,z,t,c index pixels of a
    // FakeFile. This requires the x value of tested images to be somewhat large. Assert
    // the input image fits the bill
    if (sizeX < 41) {
      throw new IllegalArgumentException("Width < 41 can break some tests");
    }

    StringBuffer filename = new StringBuffer(title);
    filename.append("&pixelType=");
    filename.append(FormatTools.getPixelTypeString(pixelType));
    filename.append("&sizeX=");
    filename.append(sizeX);
    filename.append("&sizeY=");
    filename.append(sizeY);
    filename.append("&sizeZ=");
    filename.append(sizeZ);
    filename.append("&sizeC=");
    filename.append(sizeC);
    filename.append("&sizeT=");
    filename.append(sizeT);
    if (numSeries > 0) {
      filename.append("&series=");
      filename.append(numSeries);
    }
    if (indexed) {
      filename.append("&indexed=true");
    }
    if (rgb != -1) {
      filename.append("&rgb=");
      filename.append(rgb);
    }
    if (falseColor) {
      filename.append("&falseColor=true");
    }
    if (lutLength > 0) {
      filename.append("&lutLength=");
      filename.append(lutLength);
    }
    filename.append(".fake");

    return filename.toString();
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

  /** Series number of the given ImagePlus at z,c,t index */
  private int sIndex(ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor)
  {
    return getPixelValue(0,0,imp,z,c,t,indexed,falseColor);
  }

  /** Image number of the given ImagePlus at z,c,t index */
  @SuppressWarnings("unused")
  private int iIndex(ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor)
  {
    return getPixelValue(10,0,imp,z,c,t,indexed,falseColor);
  }

  /** Slice number of the given ImagePlus at z,c,t index */
  private int zIndex(ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor)
  {
    return getPixelValue(20,0,imp,z,c,t,indexed,falseColor);
  }

  /** Channel number of the given ImagePlus at z,c,t index */
  private int cIndex(ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor)
  {
    return getPixelValue(30,0,imp,z,c,t,indexed,falseColor);
  }

  /** Frame number of the given ImagePlus at z,c,t index */
  private int tIndex(ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor)
  {
    return getPixelValue(40,0,imp,z,c,t,indexed,falseColor);
  }

  /** returns the character at the given index within a string that is a permutation of ZCT */
  private char axisChar(String order, int d) {
    if ((d < 0) || (d > 2))
      throw new IllegalArgumentException("index out of bounds [0..2]: "+d);

    return order.charAt(d);
  }

  /** returns Axis given an order string and an index */
  private Axis axis(String order, int d)
  {
    char dimChar = axisChar(order,d);

    if (dimChar == 'Z') return Axis.Z;
    if (dimChar == 'C') return Axis.C;
    if (dimChar == 'T') return Axis.T;

    throw new IllegalArgumentException("unknown dimension : ("+dimChar+")");
  }

  /** returns z, c, or t value given an Axis selector */
  private int value(Axis axis, int z, int c, int t)
  {
    if (axis == Axis.Z) return z;
    if (axis == Axis.C) return c;
    if (axis == Axis.T) return t;

    throw new IllegalArgumentException("unknown axis: "+axis);
  }

  /** returns z, c, or t index value given an ImageProcessor and an Axis selector */
  private int index(Axis axis, ImageProcessor proc)
  {
    if (axis == Axis.Z) return zIndex(proc);
    if (axis == Axis.C) return cIndex(proc);
    if (axis == Axis.T) return tIndex(proc);

    throw new IllegalArgumentException("unknown axis: "+axis);
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
      throw new IllegalArgumentException("stepBy < 1");

    // could calc this but simple loop suffices for our purposes
    int count = 0;
    for (int i = from; i <= to; i += by)
        count++;
    return count;
  }

  /** The number of Z slices in an ImagePlus */
  private int getSizeZ(ImagePlus imp) {
    return imp.getDimensions()[3];
  }

  /** The number of T slices in an ImagePlus */
  private int getSizeT(ImagePlus imp) {
    return imp.getDimensions()[4];
  }

  /** The number of effective C slices in an ImagePlus */
  private int getEffectiveSizeC(ImagePlus imp) {
    return imp.getDimensions()[2];
  }

  /** set an ImagePlus' position relative to CZT ordering (matches imp.setPosition()) */
  private void setCztPosition(ImagePlus imp, int z, int c, int t)
  {
    // czt order : should match the .setPosition(c,z,t) code
    int sliceNumber = c + (z*imp.getNChannels()) + (t*imp.getNSlices()*imp.getNChannels());
    imp.setSlice(sliceNumber+1);
  }

  /** set an ImagePlus' position relative to ZCT ordering (rather than default CZT) */
  private void setZctPosition(ImagePlus imp, int z, int c, int t)
  {
    // zct order
    int sliceNumber = z + (c*imp.getNSlices()) + (t*imp.getNSlices()*imp.getNChannels());
    imp.setSlice(sliceNumber+1);
  }

  // note - the following code relies on setZctPosition() being called previously. Otherwise regular ImagePlus case
  //   won't work. In general this method not designed for general use but actually just for use by getPixelValue().

  /** gets the color table from any kind of ImagePlus (composite or not) - not for general use */
  private LUT getColorTable(ImagePlus imp, int channel)
  {
    if (imp instanceof CompositeImage)
      return ((CompositeImage)imp).getChannelLut(channel+1);

    // else a regular ImagePlus

    IndexColorModel icm = (IndexColorModel)imp.getProcessor().getColorModel();

    // TODO - maybe I can cast from IndexColorModel to LUT here - depends what IJ did.
    // otherwise make a LUT

    byte[] reds = new byte[256], greens = new byte[256], blues = new byte[256];

    icm.getReds(reds);
    icm.getGreens(greens);
    icm.getBlues(blues);

    return new LUT(reds,greens,blues);
  }

  /** get the actual pixel value (lookup when data is indexed) of the index of a fake image at a given z,c,t */
  private int getPixelValue(int x,int y, ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor)
  {
    //TODO - restore - changed for compositeTest debugging setZctPosition(imp,z,c,t);
    setCztPosition(imp,z,c,t);

    int rawValue = (int) (imp.getProcessor().getPixelValue(x, y));

    if ((!indexed) || (falseColor)) // TODO - disabling falseColor test here improves 3/1/indexed/falseColor
      return rawValue;

    // otherwise indexed - lookup pixel value in LUT

    LUT lut = getColorTable(imp,c);
    int value = lut.getRed(rawValue);  // since r g and b vals should be the same choose one arbitrarily.
      // OR Use red in case lut len < 3 and zero fills other channels

    return value;
  }

  /** calculate the effective size C of an image given various params */
  private int effectiveC(int sizeC, int rgb, int lutLen, boolean indexed, boolean falseColor)
  {
    int effC = sizeC;

    if (indexed)  // this is from Melissa
    {
      if (falseColor)
        effC /= rgb;
      else // not falseColor
        effC *= lutLen;
    }

    return effC;
  }

  // ****** helper tests ****************************************************************************************

  /** tests that the correct number of ImagePluses exist */
  private void impsCountTest(ImagePlus[] imps, int numExpected)
  {
    assertNotNull(imps);
    assertEquals(numExpected,imps.length);
  }

  /** tests that the stack of an ImagePlus is of a given size */
  private void stackTest(ImagePlus imp, int expectedSize)
  {
    assertNotNull(imp);
    assertEquals(expectedSize,imp.getStack().getSize());
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

  /** tests that input to the crop tests is valid */
  private void verifyCropInput(int sizeX, int sizeY, int originCropX, int originCropY, int sizeCrop)
  {
    assertTrue((sizeX > 50) || (sizeY > 10));
    assertTrue(sizeX < 256);
    assertTrue(sizeY < 256);
    assertTrue(sizeCrop > 0);
    assertTrue(originCropX >= 0);
    assertTrue(originCropY >= 0);
    assertTrue((originCropX > 50) || (originCropY > 10));
    assertTrue(originCropX + sizeCrop <= sizeX);
    assertTrue(originCropY + sizeCrop <= sizeY);
  }

  /** tests that the indices of a FakeFile[z,c,t] match passed in values*/
  private boolean indexValuesTest(ImagePlus imp, int z, int c, int t, boolean indexed, boolean falseColor,
                                  int expS, /*int expI,*/ int expZ, int expC, int expT)
  {
    // TODO - returns a boolean so we can print out all values before asserting failure. Could be changed if desired.

    int tempS = sIndex(imp, z, c, t, indexed, falseColor);
    int tempZ = zIndex(imp, z, c, t, indexed, falseColor);
    int tempC = cIndex(imp, z, c, t, indexed, falseColor);
    int tempT = tIndex(imp, z, c, t, indexed, falseColor);

    if ((expS != tempS) || (expZ != tempZ) || (expC != tempC) || (expT != tempT))
    {
      return false;
    }
    assertEquals(expS,tempS);
    assertEquals(expZ,tempZ);
    assertEquals(expC,tempC);
    assertEquals(expT,tempT);
    return true;
  }

  /** tests that a FakeFile dataset has index values in ZCT order */
  private void stackInZctOrderTest(ImagePlus imp, int maxZ, int maxC, int maxT, boolean indexed, boolean falseColor)
  {
    LOGGER.debug("stackInZctOrderTest()");

    boolean success = true;

    stackTest(imp,(maxZ * maxC * maxT));

    int iIndex = 0;
    for (int t = 0; t < maxT; t++) {
      for (int c = 0; c < maxC; c++) {
        for (int z = 0; z < maxZ; z++) {

          int expectedS = 0;
          //int expectedI = iIndex;
          int expectedZ = z;
          int expectedC = c;
          int expectedT = t;

          iIndex++;

          success &= indexValuesTest(imp,z,c,t,indexed,falseColor,expectedS,expectedZ,expectedC,expectedT);
        }
      }
    }

    assertTrue(success);
  }

  /** tests that a FakeFile dataset has index values in CZT order */
  private void stackInCztOrderTest(ImagePlus imp, int maxZ, int maxC, int maxT, boolean indexed, boolean falseColor)
  {
    LOGGER.debug("stackInCztOrderTest()");

    boolean success = true;

    stackTest(imp,(maxZ * maxC * maxT));

    int iIndex = 0;
    for (int t = 0; t < maxT; t++) {
      for (int z = 0; z < maxZ; z++) {
        for (int c = 0; c < maxC; c++) {

          int expectedS = 0;
          int expectedZ = z;
          int expectedC = c;
          int expectedT = t;

          iIndex++;

          success &= indexValuesTest(imp,z,c,t,indexed,falseColor,expectedS,expectedZ,expectedC,expectedT);
        }
      }
    }

    assertTrue(success);
  }

  /** tests that a FakeFile dataset has index values in CZT order repeated once per series */
  private void multipleSeriesInCztOrderTest(ImagePlus imp, int numSeries, int maxZ, int maxC, int maxT)
  {
    // make sure the number of slices in stack is a sum of all series
    stackTest(imp,(numSeries*maxZ*maxC*maxT));

    ImageStack st = imp.getStack();

    int slice = 0;
    for (int sIndex = 0; sIndex < numSeries; sIndex++) {
      for (int tIndex = 0; tIndex < maxT; tIndex++) {
        for (int zIndex = 0; zIndex < maxZ; zIndex++) {
          for (int cIndex = 0; cIndex < maxC; cIndex++) {
            ImageProcessor proc = st.getProcessor(++slice);
            assertEquals(sIndex, sIndex(proc));
            assertEquals(zIndex, zIndex(proc));
            assertEquals(cIndex, cIndex(proc));
            assertEquals(tIndex, tIndex(proc));
          }
        }
      }
    }
  }

  /** tests that an ImagePlus stack is in a specified order */
  private void stackInSpecificOrderTest(ImagePlus imp, String chOrder)
  {
    ImageStack st = imp.getStack();

    int x = imp.getWidth();
    int y = imp.getHeight();
    int z = imp.getNSlices();
    int c = imp.getNChannels();
    int t = imp.getNFrames();

    Axis fastest = axis(chOrder,0);
    Axis middle = axis(chOrder,1);
    Axis slowest = axis(chOrder,2);

    int maxI = value(slowest,z,c,t);
    int maxJ = value(middle,z,c,t);
    int maxK = value(fastest,z,c,t);

    int slice = 0;
    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < maxJ; j++) {
        for (int k = 0; k < maxK; k++) {

          ImageProcessor proc = st.getProcessor(++slice);
          assertNotNull(proc);
          assertEquals(x,proc.getWidth());
          assertEquals(y,proc.getHeight());
          assertEquals(0,sIndex(proc));
          assertEquals(i,index(slowest,proc));
          assertEquals(j,index(middle,proc));
          assertEquals(k,index(fastest,proc));
        }
      }
    }
  }

  /** tests that the pixel values of a FakeFile are as expected */
  private void pixelsTest(ImagePlus imp, int pixType, boolean indexed, boolean falseColor)
  {
    assertTrue(pixType == FormatTools.UINT8);  // TODO - for now
    assertTrue(imp.getHeight() > 10);

    int max = imp.getWidth();
    if (max > 255) max = 255;

    for (int t = 0; t < imp.getNFrames(); t++)
      for (int c = 0; c < imp.getNChannels(); c++)
        for (int z = 0; z < imp.getNSlices(); z++)
          for (int i = 0; i < max; i++)
            assertEquals(i,getPixelValue(i,10,imp,z,c,t,indexed,falseColor));
  }

  /** tests that the pixels values of a cropped FakeFile are correct */
  private void croppedPixelsTest(ImagePlus imp, int ox, int cropSize)
  {
    ImageProcessor proc = imp.getProcessor();

    for (int ix = 0; ix < cropSize; ix++)
      for (int iy = 0; iy < cropSize; iy++)
        assertEquals(ox+ix,proc.getPixelValue(ix, iy),0);
  }

  /** tests that multiple file groups are pulled into one dataset */
  private void groupedFilesTest(ImagePlus imp, int expNumZ, int expNumC, int expNumT)
  {
    stackTest(imp,expNumZ*expNumC*expNumT);

    ImageStack st = imp.getStack();

    int slice = 0;
    for (int t = 0; t < expNumT; t++) {
      for (int z = 0; z < expNumZ; z++) {
        for (int c = 0; c < expNumC; c++) {
          ImageProcessor proc = st.getProcessor(++slice);
          assertEquals(0,sIndex(proc));
          assertEquals(z,iIndex(proc));
          assertEquals(z,zIndex(proc));
          assertEquals(0,cIndex(proc));
          assertEquals(0,tIndex(proc));
        }
      }
    }
  }

  /** tests that a dataset has had its Z & T dimensions swapped */
  private void swappedZtTest(ImagePlus imp, int pixType, boolean virtual, int originalZ, int originalC, int originalT)
  {
    LOGGER.debug("swappedZtTest() : virtual {} pixType {}", virtual, FormatTools.getPixelTypeString(pixType));

    stackTest(imp,(originalZ*originalC*originalT));

    ImageStack st = imp.getStack();

    // verify that the dimensional extents were swapped
    final int actualSizeZ = imp.getNSlices();
    final int actualSizeC = imp.getNChannels();
    final int actualSizeT = imp.getNFrames();
    assertEquals(originalT, actualSizeZ); // Z<->T swapped
    assertEquals(originalC, actualSizeC);
    assertEquals(originalZ, actualSizeT); // Z<->T swapped

    // verify that every plane appears in the swapped order
    int slice = 0;
    for (int tIndex = 0; tIndex < actualSizeT; tIndex++) {
      for (int zIndex = 0; zIndex < actualSizeZ; zIndex++) {
        for (int cIndex = 0; cIndex < actualSizeC; cIndex++) {

          int actualZ, actualC, actualT;
          ImageProcessor proc = st.getProcessor(++slice);

          actualZ = tIndex(proc); // Z<->T swapped
          actualC = cIndex(proc);
          actualT = zIndex(proc); // Z<->T swapped

          assertEquals(zIndex, actualZ);
          assertEquals(cIndex, actualC);
          assertEquals(tIndex, actualT);
        }
      }
    }
  }

  /** Tests that an ImageStack is ordered ZCT according to specified from/to/by points of z/c/t */
  private void seriesInZctOrderTest(ImagePlus imp, boolean indexed, boolean falseColor,
      int zFrom, int zTo, int zBy,
      int cFrom, int cTo, int cBy,
      int tFrom, int tTo, int tBy)
  {
    int zs = numInSeries(zFrom,zTo,zBy);
    int cs = numInSeries(cFrom,cTo,cBy);
    int ts = numInSeries(tFrom,tTo,tBy);

    stackTest(imp,(zs * cs * ts));

    for (int t = 0; t < ts; t++) {
      for (int c = 0; c < cs; c++) {
        for (int z = 0; z < zs; z++) {

          int zIndex = zIndex(imp,z,c,t,indexed,falseColor);
          int cIndex = cIndex(imp,z,c,t,indexed,falseColor);
          int tIndex = tIndex(imp,z,c,t,indexed,falseColor);

          int zVal = zFrom + z*zBy;
          int cVal = cFrom + c*cBy;
          int tVal = tFrom + t*tBy;

          assertEquals(zVal,zIndex);
          assertEquals(cVal,cIndex);
          assertEquals(tVal,tIndex);
        }
      }
    }
  }

  /** tests that the Calibration of an ImagePlus of signed integer data is correct */


  /** tests if images split on Z are ordered correctly */
  private void imagesZInCtOrderTest(ImagePlus[] imps, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT)
  {
    // unwind CZT loop : Z pulled outside, CT in order
    for (int z = 0; z < sizeZ; z++) {
      ImagePlus imp = imps[z];
      xyzctTest(imp,sizeX,sizeY,1,sizeC,sizeT);
      stackTest(imp,sizeC * sizeT);
      ImageStack st = imp.getStack();
      int slice = 0;
      for (int t = 0; t < sizeT; t++) {
        for (int c = 0; c < sizeC; c++) {
          ImageProcessor proc = st.getProcessor(++slice);
          // test the values
          assertEquals(z,zIndex(proc));
          assertEquals(c,cIndex(proc));
          assertEquals(t,tIndex(proc));
        }
      }
    }
  }

  /** tests if images split on C are ordered correctly */
  private void imagesCInZtOrderTest(ImagePlus[] imps, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT)
  {
    // unwind CZT loop : C pulled outside, ZT in order
    for (int c = 0; c < sizeC; c++) {
      ImagePlus imp = imps[c];
      xyzctTest(imp,sizeX,sizeY,sizeZ,1,sizeT);
      stackTest(imp,sizeZ * sizeT);
      ImageStack st = imp.getStack();
      int slice = 0;
      for (int t = 0; t < sizeT; t++) {
        for (int z = 0; z < sizeZ; z++) {
          ImageProcessor proc = st.getProcessor(++slice);
          // test the values
          assertEquals(z,zIndex(proc));
          assertEquals(c,cIndex(proc));
          assertEquals(t,tIndex(proc));
        }
      }
    }
  }

  /** tests if images split on T are ordered correctly */
  private void imagesTInCzOrderTest(ImagePlus[] imps, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT)
  {
    // unwind CZT loop : T pulled outside, CZ in order
    for (int t = 0; t < sizeT; t++) {
      ImagePlus imp = imps[t];
      xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,1);
      stackTest(imp,sizeZ * sizeC);
      ImageStack st = imp.getStack();
      int slice = 0;
      for (int z = 0; z < sizeZ; z++) {
        for (int c = 0; c < sizeC; c++) {
          ImageProcessor proc = st.getProcessor(++slice);
          // test the values
          assertEquals(z,zIndex(proc));
          assertEquals(c,cIndex(proc));
          assertEquals(t,tIndex(proc));
        }
      }
    }
  }

  /** tests that a set of images is ordered via Z first - used by concatSplit tests */
  private void imageSeriesZInCtOrderTest(ImagePlus[] imps, int numSeries, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT)
  {
    // from CZT order: Z pulled out, CT in order
    for (int z = 0; z < sizeZ; z++) {
      ImagePlus imp = imps[z];
      xyzctTest(imp,sizeX,sizeY,1,sizeC,sizeT*numSeries);
      stackTest(imp,numSeries*sizeC*sizeT);
      ImageStack st = imp.getStack();
      for (int s = 0; s < numSeries; s++) {
        int slice = s*sizeC*sizeT;
        for (int t = 0; t < sizeT; t++) {
          for (int c = 0; c < sizeC; c++) {
            ImageProcessor proc = st.getProcessor(++slice);
            assertEquals(z,zIndex(proc));
            assertEquals(c,cIndex(proc));
            assertEquals(t,tIndex(proc));
            assertEquals(s,sIndex(proc));
          }
        }
      }
    }
  }

  /** tests that a set of images is ordered via C first - used by concatSplit tests */
  private void imageSeriesCInZtOrderTest(ImagePlus[] imps, int numSeries, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT)
  {
    // from CZT order: C pulled out, ZT in order
    for (int c = 0; c < sizeC; c++) {
      ImagePlus imp = imps[c];
      xyzctTest(imp,sizeX,sizeY,sizeZ,1,sizeT*numSeries);
      stackTest(imp,numSeries*sizeZ*sizeT);
      ImageStack st = imp.getStack();
      for (int s = 0; s < numSeries; s++) {
        int slice = s*sizeZ*sizeT;
        for (int t = 0; t < sizeT; t++) {
          for (int z = 0; z < sizeZ; z++) {
            ImageProcessor proc = st.getProcessor(++slice);
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

  // this one will be different from the previous two as we concat along T by default for FakeFiles as all dims compatible.
  //   Then we're splitting on T. Logic will need to be different from others.
  /** tests that a set of images is ordered via T first - used by concatSplit tests */
  private void imageSeriesTInCzOrderTest(ImagePlus[] imps, int numSeries, int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT)
  {
    int imageNum = 0;
    for (int s = 0; s < numSeries; s++) {
      // from CZT order: T pulled out, CZ in order
      for (int t = 0; t < sizeT; t++)
      {
        ImagePlus imp = imps[imageNum++];
        xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,1);
        stackTest(imp,sizeZ*sizeC);
        ImageStack st = imp.getStack();
        int slice = 0;
        for (int z = 0; z < sizeZ; z++) {
          for (int c = 0; c < sizeC; c++) {
            ImageProcessor proc = st.getProcessor(++slice);
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

  /** tests that an image stack is correctly ordered after swapping and cropping */
  private void stackCtzSwappedAndCroppedTest(ImagePlus[] imps, int cropSizeX, int cropSizeY, int origSizeZ, int origSizeC, int origSizeT, int start, int stepBy)
  {
    // note orig data is ZCT. swapping order is CTZ (all dims swapped).

    int newMaxT = origSizeC;

    int numC = numInSeries(start,origSizeC-1,stepBy);

    int newZ = origSizeT;
    int newC = origSizeZ;
    int newT = numC;

    for (int zIndex = 0; zIndex < newZ; zIndex++) {

      ImagePlus imp = imps[zIndex];

      xyzctTest(imp,cropSizeX,cropSizeY,1,newC,newT); // all dims changed

      stackTest(imp,newC*newT);

      ImageStack st = imp.getStack();

      int slice = 0;
      for (int tIndex = start; tIndex < newMaxT; tIndex += stepBy) {
        for (int cIndex = 0; cIndex < newC; cIndex++)
        {
          ImageProcessor proc = st.getProcessor(++slice);

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

  // ******** specific testers  **********************************

  /** tests BioFormats when directly calling BF.openImagePlus(path) (no options set) */
  private void defaultBehaviorTester(int pixType, int x, int y, int z, int c, int t)
  {
    String path = constructFakeFilename("default", pixType, x, y, z, c, t, -1, false, -1, false, -1);
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

  /** tests BF's options.setStackOrder() */
  private void outputStackOrderTester(boolean virtual, int pixType, ChannelOrder order, int x, int y, int z, int c, int t)
  {
    String bfChOrder = bfChanOrd(order);
    String chOrder = order.toString();

    String path = constructFakeFilename("stack", pixType, x, y, z, c, t, -1, false, -1, false, -1);

    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    stackTest(imp,z*c*t);

    stackInSpecificOrderTest(imp, chOrder);
  }

  /** tests BF's options.setGroupFiles() */
  private void datasetGroupFilesTester(boolean virtual)
  {
    String path = FAKE_FILES[0];

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    xyzctTest(imps[0], FAKE_SIZE_X, FAKE_SIZE_Y, FAKE_PLANE_COUNT, FAKE_CHANNEL_COUNT, FAKE_TIMEPOINT_COUNT);

    groupedFilesTest(imps[0], FAKE_PLANE_COUNT, FAKE_CHANNEL_COUNT, FAKE_TIMEPOINT_COUNT);
  }

  /** tests BF's options.setUngroupFiles() */
  private void datasetOpenFilesIndividuallyTester(boolean virtual)
  {
    // TODO - try to remove file dependency

    String path = "2channel_stack_raw01.pic";

    // there is a second file called "2channel_stack_raw02.pic" present in the same directory
    // if open indiv true should only load one of them, otherwise both

    // try ungrouped

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    stackTest(imps[0],16); // one loaded as one set with 16 slices

    // try grouped

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    stackTest(imps[0],32); // both loaded as one set of 32 slices
  }

  /** tests BF's options.setSwapDimensions() */
  private void datasetSwapDimsTester(boolean virtual, int pixType, int x, int y, int z, int t)
  {
    LOGGER.debug("datsetSwapDimsTester() virtual = {} pixType = {}", virtual, FormatTools.getPixelTypeString(pixType));
    int c = 3;
    ChannelOrder swappedOrder = ChannelOrder.TCZ; // original order is ZCT

    String path = constructFakeFilename("swapDims", pixType, x, y, z, c, t, -1, false, -1, false, -1);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    swappedZtTest(imp,pixType,virtual,z,c,t);
  }

  /** open a fakefile series either as separate ImagePluses or as one ImagePlus depending on input flag allOfThem */
  private ImagePlus[] openSeriesTest(String fakeFileName, boolean virtual, boolean openAllValue)
  {
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
      options.setId(fakeFileName);
      options.setOpenAllSeries(openAllValue);
      imps = BF.openImagePlus(options);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    catch (FormatException e) {
      fail(e.getMessage());
    }

    return imps;
  }

  /** tests BF's options.setOpenAllSeries() */
  private void datasetOpenAllSeriesTester(boolean virtual, boolean openAll)
  {
    int x = 55, y = 20, z = 2, c = 3, t = 4, numSeries = 5;

    String path = constructFakeFilename("openAllSeries", FormatTools.UINT32, x, y, z, c, t, numSeries, false, -1, false, -1);

    int expectedNumImps = 1;
    if (openAll)
      expectedNumImps = numSeries;

    ImagePlus[] imps = openSeriesTest(path,virtual,openAll);
    impsCountTest(imps,expectedNumImps);
    for (int i = 0; i < expectedNumImps; i++)
      xyzctTest(imps[i],x,y,z,c,t);
  }

  /** tests BF's options.setOpenAllSeries() and options.setConcatenate() */
  private void datasetConcatenateTester(int pixType, int x, int y, int z, int c, int t, int s)
  {
    assertTrue(s >= 1);  // necessary for this test

    // open all series as one

    String path = constructFakeFilename("concat", pixType, x, y, z, c, t, s, false, -1, false, -1);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    ImagePlus imp = imps[0];

    // with FakeFiles all dims compatible for concat, BF will concat along T. Thus t*s in next test.
    xyzctTest(imp,x,y,z,c,t*s);

    multipleSeriesInCztOrderTest(imp,s,z,c,t);
  }


  private void ascendingValuesTest(byte[] data, int expectedLength)
  {
    assertEquals(expectedLength,data.length);
    for (int i = 0; i < expectedLength; i++)
      assertEquals(i,data[i]&0xff);
  }

  // TODO : can I replace all calls to this to colorTests() passing numChannels == 1. Another way: modify lutTest() to
  //   use a standard ImagePlus rather than a CompImg and have it call getColorTable(). Then pass in just RED ramped
  //   test values.
  private void imagePlusLutTest(ImagePlus imp, boolean indexed, boolean falseColor, Color color)
  {
    // When numCh < 2 or numCh > 7 the setColorMode() code for Composite and Colorize cannot create a CompositeImage.
    // Therefore it creates a one channel ImagePlus with a LUT that only ramps the red channel. Test this to be
    // the case.

    assertFalse(imp instanceof CompositeImage);

    if (indexed) {
      fail("not yet supporting indexed");
    }

    LUT lut = getColorTable(imp,0);

    byte[] data = new byte[256];

    if (color.getRed() > 0)
    {
      lut.getReds(data);
      ascendingValuesTest(data,256);
    }
    if (color.getGreen() > 0)
    {
      lut.getGreens(data);
      ascendingValuesTest(data,256);
    }
    if (color.getBlue() > 0)
    {
      lut.getBlues(data);
      ascendingValuesTest(data,256);
    }
  }

  /** tests BF's options.setColorMode(composite) */
  private void colorDefaultTester(boolean virtual, int pixType, boolean indexed, int channels, int chanPerPlane,
                                    boolean falseColor, int numSeries, boolean wantLutDefined)
  {
    int sizeX = 55, sizeY = 71, sizeZ = 3, sizeT = 4;

    // reportedly works in BF as long as numSeries*sizeC*3 <= 25

    String path = constructFakeFilename("colorDefault", pixType, sizeX, sizeY, sizeZ, channels, sizeT, numSeries,
        indexed, chanPerPlane, falseColor, -1);
    LOGGER.debug("colorDefaultTester: {}", path);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
      options.setColorMode(ImporterOptions.COLOR_MODE_DEFAULT);
      if (indexed && wantLutDefined)
      {
        for (int s = 0; s < numSeries; s++)
          options.setCustomColor(s, 0, Color.PINK);  // set the first channel lut to pink to force COLOR mode as return
      }
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

    ImagePlus imp = imps[0];

    int lutLen = 3;

    int expectedSizeC = effectiveC(channels, chanPerPlane, lutLen, indexed, falseColor);

    xyzctTest(imp,sizeX,sizeY,sizeZ,expectedSizeC,sizeT);

    if ((expectedSizeC >= 2) && (expectedSizeC <= 7))
    {
      assertTrue(imp.isComposite());

      CompositeImage ci = (CompositeImage)imp;

      assertEquals(chanPerPlane > 1, ci.hasCustomLuts());

      Color[] colorOrder;
      int expectedType;
      if (chanPerPlane > 1)  // TODO : apparently need another test here for channelFiller.isFilled() case so 1/1/indexed works
      {
        expectedType = CompositeImage.COMPOSITE;
        colorOrder = DEFAULT_COLOR_ORDER;
      }
      else if (indexed && wantLutDefined)
      {
        // TODO - left working here. this case doesn't work yet.
        //   have to figure how BF calcs hasChannelLut so I can exercise it here
        expectedType = CompositeImage.COLOR;
        colorOrder = new Color[]{Color.PINK,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE};
      }
      else
      {
        expectedType = CompositeImage.GRAYSCALE;
        colorOrder = DEFAULT_COLOR_ORDER;
      }

      assertEquals(expectedType, ci.getMode());
      colorTests(ci,expectedSizeC,colorOrder);
    }
    else  // expectedSizeC < 2 or > 7 - we should have gotten back a regular ImagePlus
    {
      assertFalse(imp.isComposite());

      imagePlusLutTest(imp,indexed,falseColor,DEFAULT_COLOR_ORDER[0]);
    }

    stackInCztOrderTest(imp,sizeZ,expectedSizeC,sizeT,indexed,falseColor);

    // TODO : i've done no pixel testing
  }

  /** tests BF's options.setColorMode(composite) */
  private void colorCompositeTester(boolean virtual, int pixType, boolean indexed, int channels, int chanPerPlane,
                                    boolean falseColor, int numSeries)
  {
    int sizeX = 55, sizeY = 71, sizeZ = 3, sizeT = 4;

    // reportedly works in BF as long as numSeries*sizeC*3 <= 25

    String path = constructFakeFilename("colorComposite", pixType, sizeX, sizeY, sizeZ, channels, sizeT, numSeries,
        indexed, chanPerPlane, falseColor, -1);

    LOGGER.debug("colorCompositeTester: {}", path);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    ImagePlus imp = imps[0];

    int lutLen = 3;

    int expectedSizeC = effectiveC(channels, chanPerPlane, lutLen, indexed, falseColor);

    xyzctTest(imp,sizeX,sizeY,sizeZ,expectedSizeC,sizeT);

    if ((expectedSizeC >= 2) && (expectedSizeC <= 7))
    {
      assertTrue(imp.isComposite());

      CompositeImage ci = (CompositeImage)imp;

      assertTrue(ci.hasCustomLuts());

      assertEquals(CompositeImage.COMPOSITE, ci.getMode());

      colorTests(ci,expectedSizeC,DEFAULT_COLOR_ORDER);
    }
    else  // expectedSizeC < 2 or > 7 - we should have gotten back a regular ImagePlus
    {
      assertFalse(imp.isComposite());

      imagePlusLutTest(imp,indexed,falseColor,DEFAULT_COLOR_ORDER[0]);
    }

    stackInCztOrderTest(imp,sizeZ,expectedSizeC,sizeT,indexed,falseColor);

    // TODO : i've done no pixel testing
  }

  /** tests BF's options.setColorMode(colorized) */
  private void colorColorizedTester(boolean virtual, int pixType, boolean indexed, int channels, int chanPerPlane,
                                    boolean falseColor, int numSeries)
  {
    int sizeX = 55, sizeY = 71, sizeZ = 3, sizeT = 4;

    String path = constructFakeFilename("colorColorized", pixType, sizeX, sizeY, sizeZ, channels, sizeT, numSeries,
        indexed, chanPerPlane, falseColor, -1);

    LOGGER.debug("colorColorizedTester: {}", path);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    ImagePlus imp = imps[0];

    int lutLen = 3;

    int expectedSizeC = effectiveC(channels, chanPerPlane, lutLen, indexed, falseColor);

    xyzctTest(imp,sizeX,sizeY,sizeZ,expectedSizeC,sizeT);

    if ((expectedSizeC >= 2) && (expectedSizeC <= 7))
    {
      assertTrue(imp.isComposite());

      CompositeImage ci = (CompositeImage)imp;

      assertTrue(ci.hasCustomLuts());

      assertEquals(CompositeImage.COLOR, ci.getMode());

      colorTests(ci,expectedSizeC,DEFAULT_COLOR_ORDER);
    }
    else  // expectedSizeC < 2 or > 7 - we should have gotten back a regular ImagePlus
    {
      assertFalse(imp.isComposite());

      imagePlusLutTest(imp,indexed,falseColor,DEFAULT_COLOR_ORDER[0]);
    }

    stackInCztOrderTest(imp,sizeZ,expectedSizeC,sizeT,indexed,falseColor);

    // TODO : i've done no pixel testing
  }

  /** tests BF's options.setColorMode(gray) */
  private void colorGrayscaleTester(boolean virtual, int pixType, boolean indexed, int channels, int chanPerPlane,
      boolean falseColor, int numSeries)
  {
    int sizeX = 55, sizeY = 71, sizeZ = 3, sizeT = 4;

    String path = constructFakeFilename("colorGrayscale", pixType, sizeX, sizeY, sizeZ, channels, sizeT, numSeries,
    indexed, chanPerPlane, falseColor, -1);

    LOGGER.debug("colorGrayscaleTester: {}", path);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
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

    ImagePlus imp = imps[0];

    int lutLen = 3;

    int expectedSizeC = effectiveC(channels, chanPerPlane, lutLen, indexed, falseColor);

    xyzctTest(imp,sizeX,sizeY,sizeZ,expectedSizeC,sizeT);

    if ((expectedSizeC >= 2) && (expectedSizeC <= 7))
    {
      assertTrue(imp.isComposite());

      CompositeImage ci = (CompositeImage)imp;

      assertFalse(ci.hasCustomLuts());

      assertEquals(CompositeImage.GRAYSCALE, ci.getMode());

      colorTests(ci,expectedSizeC,DEFAULT_COLOR_ORDER);
    }
    else  // expectedSizeC < 2 or > 7 - we should have gotten back a regular ImagePlus
    {
      assertFalse(imp.isComposite());

      imagePlusLutTest(imp,indexed,falseColor,DEFAULT_COLOR_ORDER[0]);
    }

    stackInCztOrderTest(imp,sizeZ,expectedSizeC,sizeT,indexed,falseColor);

    // TODO : i've done no pixel testing
  }

  /** tests BF's options.setColorMode(custom) */
  private void colorCustomTester(boolean virtual, int pixType, boolean indexed, int channels, int chanPerPlane,
      boolean falseColor, int numSeries)
  {
    int sizeX = 55, sizeY = 71, sizeZ = 3, sizeT = 4;

    String path = constructFakeFilename("colorCustom", pixType, sizeX, sizeY, sizeZ, channels, sizeT, numSeries,
    indexed, chanPerPlane, falseColor, -1);

    LOGGER.debug("colorCustomTester: {}", path);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setVirtual(virtual);
      options.setColorMode(ImporterOptions.COLOR_MODE_CUSTOM);
      int maxChannels = (channels <= 7) ? channels : 7;
      for (int s = 0; s < numSeries; s++)
        for (int c = 0; c < maxChannels; c++)
          options.setCustomColor(s, c, CUSTOM_COLOR_ORDER[c]);
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

    ImagePlus imp = imps[0];

    int lutLen = 3;

    int expectedSizeC = effectiveC(channels, chanPerPlane, lutLen, indexed, falseColor);

    xyzctTest(imp,sizeX,sizeY,sizeZ,expectedSizeC,sizeT);

    if ((expectedSizeC >= 2) && (expectedSizeC <= 7))
    {
      assertTrue(imp.isComposite());

      CompositeImage ci = (CompositeImage)imp;

      assertTrue(ci.hasCustomLuts());

      assertEquals(CompositeImage.COLOR, ci.getMode());

      colorTests(ci,expectedSizeC,CUSTOM_COLOR_ORDER);
    }
    else  // expectedSizeC < 2 or > 7 - we should have gotten back a regular ImagePlus
    {
      assertFalse(imp.isComposite());

      imagePlusLutTest(imp,indexed,falseColor,CUSTOM_COLOR_ORDER[0]);
    }

    stackInCztOrderTest(imp,sizeZ,expectedSizeC,sizeT,indexed,falseColor);

    // TODO : i've done no pixel testing
  }

  /** tests BF's options.setVirtual() */
  private void memoryVirtualStackTester(boolean desireVirtual)
  {
      int x = 604, y = 531, z = 7, c = 1, t = 1;

      String path = constructFakeFilename("vstack", FormatTools.UINT16, x, y, z, c, t, -1, false, -1, false, -1);

      // open stack
      ImagePlus[] imps = null;

      try {
        ImporterOptions options = new ImporterOptions();
        options.setAutoscale(false);
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

  /** tests BF's options.set?Begin(), options.set?End(), and options.set?Step() */
  private void memorySpecifyRangeTester(int z, int c, int t,
      int zFrom, int zTo, int zBy,
      int cFrom, int cTo, int cBy,
      int tFrom, int tTo, int tBy)
  {
    int pixType = FormatTools.UINT8, x=50, y=5;

    String path = constructFakeFilename("range", pixType, x, y, z, c, t, -1, false, -1, false, -1);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    // should be in correct order
    seriesInZctOrderTest(imp,false,false,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
  }

  /** tests BF's options.setCrop() and options.setCropRegion() */
  private void memoryCropTester(int x, int y, int ox, int oy, int cropSize)
  {
    verifyCropInput(x, y, ox, oy, cropSize);  // needed for this test

    String path = constructFakeFilename("crop", FormatTools.UINT8, x, y, 1, 1, 1, -1, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setId(path);
      options.setCrop(true);
      options.setCropRegion(0, new Region(ox, oy, cropSize, cropSize));
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

    xyzctTest(imp,cropSize,cropSize,1,1,1);

    // test we got the right pixels
    croppedPixelsTest(imp,ox,cropSize);
  }

  /** tests BF's options.setSplitChannels() */
  private void splitChannelsTester()
  {
    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7;

    final String path = constructFakeFilename("splitC",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, -1, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    // one image per channel
    impsCountTest(imps,sizeC);

    imagesCInZtOrderTest(imps,sizeX,sizeY,sizeZ,sizeC,sizeT);
  }

  /** tests BF's options.setFocalPlanes() */
  private void splitFocalPlanesTester()
  {
    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7;

    final String path = constructFakeFilename("splitZ",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, -1, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    // one image per focal plane
    impsCountTest(imps,sizeZ);

    imagesZInCtOrderTest(imps,sizeX,sizeY,sizeZ,sizeC,sizeT);
  }

  /** tests BF's options.setSplitTimepoints() */
  private void splitTimepointsTester()
  {
    final int sizeX = 50, sizeY = 20, sizeZ = 5, sizeC = 3, sizeT = 7;

    final String path = constructFakeFilename("splitT",
      FormatTools.UINT8, 50, 20, sizeZ, sizeC, sizeT, -1, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    // one image per time point
    impsCountTest(imps,sizeT);

    imagesTInCzOrderTest(imps,sizeX,sizeY,sizeZ,sizeC,sizeT);
  }

  // note - this test needs to rely on crop() to get predictable nonzero minimums

  /** tests BF's options.setConcatenate() with options.setSplitFocalPlanes() */
  private void comboConcatSplitFocalPlanesTester()
  {
    // take a nontrivial zct set of series
    // run split and concat at same time

    final int sizeX = 50, sizeY = 20, sizeZ = 3, sizeC = 5, sizeT = 7, series = 4;

    final String path = constructFakeFilename("concatSplitZ",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, series, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setOpenAllSeries(true);
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

    // one image per focal plane
    impsCountTest(imps,sizeZ);

    imageSeriesZInCtOrderTest(imps,series,sizeX,sizeY,sizeZ,sizeC,sizeT);
  }

  /** tests BF's options.setConcatenate() with options.setSplitChannels() */
  private void comboConcatSplitChannelsTester()
  {
    // take a nontrivial zct set of series
    // run split and concat at same time

    final int sizeX = 50, sizeY = 20, sizeZ = 3, sizeC = 5, sizeT = 7, series = 4;

    final String path = constructFakeFilename("concatSplitC",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, series, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setOpenAllSeries(true);
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

    // one image per channel
    impsCountTest(imps,sizeC);

    imageSeriesCInZtOrderTest(imps,series,sizeX,sizeY,sizeZ,sizeC,sizeT);
  }

  /** tests BF's options.setConcatenate() with options.setSplitTimepoints() */
  private void comboConcatSplitTimepointsTester()
  {
    // take a nontrivial zct set of series
    // run split and concat at same time

    final int sizeX = 50, sizeY = 20, sizeZ = 3, sizeC = 5, sizeT = 7, numSeries = 4;

    final String path = constructFakeFilename("concatSplitT",
      FormatTools.UINT8, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, false, -1, false, -1);

    // open image
    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
      options.setOpenAllSeries(true);
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

    // numSeries images per timepoint
    impsCountTest(imps,sizeT*numSeries);

    imageSeriesTInCzOrderTest(imps,numSeries,sizeX,sizeY,sizeZ,sizeC,sizeT);
  }

  /** tests BF's options.setColormode(composite) - alternate, later definition */
  private void compositeSubcaseTester(int sizeC, boolean indexed)
  {
    int pixType = FormatTools.UINT8, sizeX = 60, sizeY = 30, sizeZ = 2, sizeT = 3, numSeries = 1, rgb = -1, lutLen = -1;
    boolean falseColor = false;

    String path = constructFakeFilename("colorComposite", pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, indexed,
                                          rgb, falseColor, lutLen);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    ImagePlus imp = imps[0];

    xyzctTest(imp,sizeX,sizeY,sizeZ,sizeC,sizeT);

    assertTrue(imp.isComposite());

    CompositeImage ci = (CompositeImage)imp;

    assertTrue(ci.hasCustomLuts());

    assertEquals(CompositeImage.COMPOSITE, ci.getMode());

    colorTests(ci,sizeC,DEFAULT_COLOR_ORDER);

    stackInZctOrderTest(imp,sizeZ,sizeC,sizeT,indexed,falseColor);

    pixelsTest(imp,pixType,indexed,falseColor);
  }

// ** ImporterTest methods **************************************************************

  @Test
  public void testDefaultBehavior()
  {
    defaultBehaviorTester(FormatTools.UINT8, 57, 78, 5, 4, 3);
    defaultBehaviorTester(FormatTools.UINT32, 323, 206, 3, 2, 1);
    defaultBehaviorTester(FormatTools.UINT16, 400, 300, 1, 1, 1);
    defaultBehaviorTester(FormatTools.INT8, 232, 153, 3, 7, 5);
    defaultBehaviorTester(FormatTools.INT16, 107, 414, 1, 1, 1);
    defaultBehaviorTester(FormatTools.INT32, 158, 99, 2, 3, 4);
    defaultBehaviorTester(FormatTools.FLOAT, 73, 99, 3, 4, 5);
    defaultBehaviorTester(FormatTools.DOUBLE, 106, 44, 5, 5, 4);
  }

  @Test
  public void testOutputStackOrder()
  {
    for (ChannelOrder order : ChannelOrder.values())
      for (boolean virtual : BOOLEAN_STATES)
        outputStackOrderTester(virtual,FormatTools.UINT8, order,  82, 47, 2, 3, 4);
  }

  @Test
  public void testDatasetGroupFiles()
  {
    for (boolean virtual : BOOLEAN_STATES)
      datasetGroupFilesTester(virtual);
  }

  @Test
  public void testDatasetOpenFilesIndividually()
  {
    if (RUN_SPECIAL_TESTS) {
      for (boolean virtual : BOOLEAN_STATES)
        datasetOpenFilesIndividuallyTester(virtual);
    }
  }

  @Test
  public void testDatasetSwapDims()
  {
    // TODO: testing only swapping Z&T of XYZTC. Add more option testing.
    //   Note that testComboManyOptions() tests another swap order

    for (boolean virtual : BOOLEAN_STATES)
    {
      datasetSwapDimsTester(virtual,FormatTools.UINT8, 82, 47, 1, 3);
      datasetSwapDimsTester(virtual,FormatTools.UINT16, 82, 47, 3, 1);
      datasetSwapDimsTester(virtual,FormatTools.UINT16, 82, 47, 5, 2);
      datasetSwapDimsTester(virtual,FormatTools.UINT32, 82, 47, 5, 2);
      datasetSwapDimsTester(virtual,FormatTools.FLOAT, 67, 109, 4, 3);
      datasetSwapDimsTester(virtual,FormatTools.DOUBLE, 67, 100, 3, 2);
      datasetSwapDimsTester(virtual,FormatTools.INT8, 44, 108, 1, 4);
      datasetSwapDimsTester(virtual,FormatTools.INT16, 44, 108, 2, 1);
      datasetSwapDimsTester(virtual,FormatTools.INT32, 44, 108, 4, 3);
    }
  }

  @Test
  public void testDatasetOpenAllSeries()
  {
    for (boolean virtual : BOOLEAN_STATES) {
      for (boolean openAll : BOOLEAN_STATES) {
        datasetOpenAllSeriesTester(virtual,openAll);
        datasetOpenAllSeriesTester(virtual,openAll);
      }
    }
  }

  @Test
  public void testDatasetConcatenate()
  {
    // NOTE: for now we will not use a virtual boolean with datasetConcatenateTester() as that combo not a legal one in BF

    // open a dataset that has multiple series and should get back a single series
    datasetConcatenateTester(FormatTools.UINT8, 82, 47, 1, 1, 1, 1);
    datasetConcatenateTester(FormatTools.UINT8, 82, 47, 1, 1, 1, 17);
    datasetConcatenateTester(FormatTools.UINT8, 82, 47, 4, 5, 2, 9);
  }

  @Test
  public void testColorDefault() {
    for (int pixType : MINIMAL_PIXEL_TYPES) {
      for (boolean virtual : BOOLEAN_STATES) {
        for (boolean defineLutEntry : BOOLEAN_STATES) {
      // these here to simplify debugging

          // edge cases in number of channels nonindexed in one series
          colorDefaultTester(virtual,pixType,NOT_INDEXED,1,1,REAL_COLOR,ONE_SERIES,defineLutEntry);
          colorDefaultTester(virtual,pixType,NOT_INDEXED,2,2,REAL_COLOR,ONE_SERIES,defineLutEntry);
          colorDefaultTester(virtual,pixType,NOT_INDEXED,7,7,REAL_COLOR,ONE_SERIES,defineLutEntry);
          colorDefaultTester(virtual,pixType,NOT_INDEXED,8,8,REAL_COLOR,ONE_SERIES,defineLutEntry);

          // edge cases in number of channels nonindexed in one series
          colorDefaultTester(virtual,pixType,NOT_INDEXED,4,4,REAL_COLOR,ONE_SERIES,defineLutEntry);
          colorDefaultTester(virtual,pixType,NOT_INDEXED,6,3,REAL_COLOR,ONE_SERIES,defineLutEntry);
          colorDefaultTester(virtual,pixType,NOT_INDEXED,12,3,REAL_COLOR,ONE_SERIES,defineLutEntry);

          // edge case : standard 3 chan planar layout
          colorDefaultTester(virtual,pixType,NOT_INDEXED,3,1,REAL_COLOR,ONE_SERIES,defineLutEntry);

          // edge case 1 channel indexed
          // TODO - this one fails UINT8 before I used general pixTypes. With gen pix types indexed does not make sense.
          //colorDefaultTester(virtual,FormatTools.UINT8,INDEXED,1,1,REAL_COLOR,ONE_SERIES,defineLutEntry);
        }
      }
    }
  }

  @Test
  public void testColorComposite() {
    for (boolean virtual : BOOLEAN_STATES) {
      // these here to simplify debugging

      // edge cases in number of channels nonindexed in one series
      // TODO : next one fails when virtual true
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,1,1,REAL_COLOR,ONE_SERIES);
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,2,2,REAL_COLOR,ONE_SERIES);
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,7,7,REAL_COLOR,ONE_SERIES);
      // TODO : next one fails when virtual true
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,8,8,REAL_COLOR,ONE_SERIES);

      // edge cases in number of channels nonindexed in one series
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,4,4,REAL_COLOR,ONE_SERIES);
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,6,3,REAL_COLOR,ONE_SERIES);
      // TODO : next one fails when virtual true
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,12,3,REAL_COLOR,ONE_SERIES);

      // edge case : standard 3 chan planar layout
      colorCompositeTester(virtual,FormatTools.UINT8,NOT_INDEXED,3,1,REAL_COLOR,ONE_SERIES);

      // edge case 1 channel indexed
      // TODO - this one fails. Actual czt vals back from code are all zeroes 2/3 of the time (1 chan goes to 3)
      //colorCompositeTester(FormatTools.UINT8,INDEXED,1,1,REAL_COLOR,ONE_SERIES);

      // general test loop
      int[] channels = new int[] {1,2,3,4,5,6,7,8,9};
      int[] series = new int[] {1,2,3,4};
      int[] channelsPerPlaneVals = new int[]{1,2,3};

      for (int pixFormat : MINIMAL_PIXEL_TYPES) {
        for (int chanCount : channels) {
          for (int numSeries : series) {
            for (int channelsPerPlane : channelsPerPlaneVals) {
              if ((chanCount % channelsPerPlane) != 0) {  // invalid combo - skip
                continue;
              }

              colorCompositeTester(virtual,pixFormat,false,chanCount,channelsPerPlane,false,numSeries);
            }
          }
        }
      }
    }
  }

  @Test
  public void testColorColorized() {
    for (int pixType : MINIMAL_PIXEL_TYPES) {
      for (boolean virtual : BOOLEAN_STATES) {
        // these here to simplify debugging

        // edge cases in number of channels nonindexed in one series
        colorColorizedTester(virtual,pixType,NOT_INDEXED,1,1,REAL_COLOR,ONE_SERIES);
        colorColorizedTester(virtual,pixType,NOT_INDEXED,2,2,REAL_COLOR,ONE_SERIES);
        colorColorizedTester(virtual,pixType,NOT_INDEXED,7,7,REAL_COLOR,ONE_SERIES);
        colorColorizedTester(virtual,pixType,NOT_INDEXED,8,8,REAL_COLOR,ONE_SERIES);

        // edge cases in number of channels nonindexed in one series
        colorColorizedTester(virtual,pixType,NOT_INDEXED,4,4,REAL_COLOR,ONE_SERIES);
        colorColorizedTester(virtual,pixType,NOT_INDEXED,6,3,REAL_COLOR,ONE_SERIES);
        colorColorizedTester(virtual,pixType,NOT_INDEXED,12,3,REAL_COLOR,ONE_SERIES);

        // edge case : standard 3 chan planar layout
        colorColorizedTester(virtual,pixType,NOT_INDEXED,3,1,REAL_COLOR,ONE_SERIES);

        // edge case 1 channel indexed
        // TODO - this one fails UINT8 before I used general pixTypes. With gen pix types indexed does not make sense.
        //colorColorizedTester(virtual,FormatTools.UINT8,INDEXED,1,1,REAL_COLOR,ONE_SERIES);
      }
    }
  }

  @Test
  public void testColorGrayscale() {
    for (int pixType : MINIMAL_PIXEL_TYPES) {
      for (boolean virtual : BOOLEAN_STATES) {
        // these here to simplify debugging

        // edge cases in number of channels nonindexed in one series
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,1,1,REAL_COLOR,ONE_SERIES);
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,2,2,REAL_COLOR,ONE_SERIES);
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,7,7,REAL_COLOR,ONE_SERIES);
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,8,8,REAL_COLOR,ONE_SERIES);

        // edge cases in number of channels nonindexed in one series
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,4,4,REAL_COLOR,ONE_SERIES);
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,6,3,REAL_COLOR,ONE_SERIES);
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,12,3,REAL_COLOR,ONE_SERIES);

        // edge case : standard 3 chan planar layout
        colorGrayscaleTester(virtual,pixType,NOT_INDEXED,3,1,REAL_COLOR,ONE_SERIES);

        // edge case 1 channel indexed
        // TODO - this one fails UINT8 before I used general pixTypes. With gen pix types indexed does not make sense.
        //colorGrayscaleTester(FormatTools.UINT8,INDEXED,1,1,REAL_COLOR,ONE_SERIES);
      }
    }
  }

  @Test
  public void testColorCustom() {
    for (int pixType : MINIMAL_PIXEL_TYPES) {
      for (boolean virtual : BOOLEAN_STATES) {
        // these here to simplify debugging

        // edge cases in number of channels nonindexed in one series
        colorCustomTester(virtual,pixType,NOT_INDEXED,1,1,REAL_COLOR,ONE_SERIES);
        colorCustomTester(virtual,pixType,NOT_INDEXED,2,2,REAL_COLOR,ONE_SERIES);
        colorCustomTester(virtual,pixType,NOT_INDEXED,7,7,REAL_COLOR,ONE_SERIES);
        colorCustomTester(virtual,pixType,NOT_INDEXED,8,8,REAL_COLOR,ONE_SERIES);

        // edge cases in number of channels nonindexed in one series
        colorCustomTester(virtual,pixType,NOT_INDEXED,4,4,REAL_COLOR,ONE_SERIES);
        colorCustomTester(virtual,pixType,NOT_INDEXED,6,3,REAL_COLOR,ONE_SERIES);
        colorCustomTester(virtual,pixType,NOT_INDEXED,12,3,REAL_COLOR,ONE_SERIES);

        // edge case : standard 3 chan planar layout
        colorCustomTester(virtual,pixType,NOT_INDEXED,3,1,REAL_COLOR,ONE_SERIES);

        // edge case 1 channel indexed
        // TODO - this one fails UINT8 before I used general pixTypes. With gen pix types indexed does not make sense.
        //colorCustomTester(FormatTools.UINT8,INDEXED,1,1,REAL_COLOR,ONE_SERIES);
      }
    }
  }

  @Test
  public void testMemoryVirtualStack() {
    for (boolean virtual : BOOLEAN_STATES)
      memoryVirtualStackTester(virtual);
  }

  @Test
  public void testMemorySpecifyRange()
  {
    // note - can't specify range in a virtualStack - no need to test

    int z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy;

    // test partial z: from
    z=8; c=3; t=2; zFrom=2; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial z: to
    z=8; c=3; t=2; zFrom=0; zTo=4; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial z: by
    z=8; c=3; t=2; zFrom=0; zTo=z-1; zBy=3; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test full z
    z=8; c=3; t=2; zFrom=2; zTo=7; zBy=3; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial c: from
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=3; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial c: to
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=6; cBy=1; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial c: by
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=4; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test full c
    z=6; c=14; t=4; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=12; cBy=4; tFrom=0; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial t: from
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=4; tTo=t-1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial t: to
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=8; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test partial t: by
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=2;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test full t
    z=3; c=5; t=13; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=4; tTo=13; tBy=2;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test edge case combo with an invalid by
    z=2; c=2; t=2; zFrom=0; zTo=0; zBy=2; cFrom=1; cTo=1; cBy=1; tFrom=0; tTo=1; tBy=1;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test a combination of zct's
    z=5; c=4; t=6; zFrom=1; zTo=4; zBy=2; cFrom=1; cTo=3; cBy=1; tFrom=2; tTo=5; tBy=2;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test another combination of zct's
    z=7; c=7; t=7; zFrom=3; zTo=6; zBy=4; cFrom=1; cTo=6; cBy=3; tFrom=0; tTo=2; tBy=2;
    memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);

    // test bad combination of zct's - choosing beyond ends of ranges

    // z index before 0 begin
    try {
      z=7; c=7; t=7; zFrom=-1; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // z index after z-1 end
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // z by < 1
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=0; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // c index before 0 begin
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=-1; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // c index after c-1 end
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c; cBy=1; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // c by < 1
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=0; tFrom=0; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // t index before 0 begin
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=-1; tTo=t-1; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // t index after t-1 end
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t; tBy=1;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // t by < 1
    try {
      z=7; c=7; t=7; zFrom=0; zTo=z-1; zBy=1; cFrom=0; cTo=c-1; cBy=1; tFrom=0; tTo=t-1; tBy=0;
      memorySpecifyRangeTester(z,c,t,zFrom,zTo,zBy,cFrom,cTo,cBy,tFrom,tTo,tBy);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testMemoryCrop()
  {
    // note - can't crop a virtualStack. therefore no need to test it.

    memoryCropTester(203, 255, 55, 20, 3);
    memoryCropTester(203, 184, 55, 40, 2);
    memoryCropTester(101, 76, 0, 25, 4);
    memoryCropTester(100, 122, 0, 15, 3);
  }

  @Test
  public void testSplitChannels()
  {
    // note - can't split channels on a virtual stack. no need to test it.
    splitChannelsTester();
  }

  @Test
  public void testSplitFocalPlanes()
  {
    splitFocalPlanesTester();
  }

  @Test
  public void testSplitTimepoints()
  {
    splitTimepointsTester();
  }

  @Test
  public void testComboConcatSplitFocalPlanes()
  {
    // note - concat and split both don't work with virtualStacks. No need to test virtual here.

    comboConcatSplitFocalPlanesTester();
  }

  @Test
  public void testComboConcatSplitChannels()
  {
    // note - concat and split both don't work with virtualStacks. No need to test virtual here.

    comboConcatSplitChannelsTester();
  }

  @Test
  public void testComboConcatSplitTimepoints()
  {
    // note - concat and split both don't work with virtualStacks. No need to test virtual here.

    comboConcatSplitTimepointsTester();
  }

  @Test
  public void testComboManyOptions()
  {
    // note - crop and setTStep both don't work with virtualStacks. No need to test virtual here.

    int pixType = FormatTools.UINT16, sizeX = 106, sizeY = 33, sizeZ = 3, sizeC = 5, sizeT = 7;
    int cropOriginX = 0, cropOriginY = 0, cropSizeX = 55, cropSizeY = 16, start = 1, stepBy = 2;
    ChannelOrder swappedOrder = ChannelOrder.CTZ;  // orig is ZCT : this is a deadly swap of all dims

    // note - to reuse existing code it is necessary that the crop origin is (0,0)

    String path = constructFakeFilename("superCombo", pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, 1, false, -1, false, -1);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    stackCtzSwappedAndCroppedTest(imps,cropSizeX,cropSizeY,sizeZ,sizeC,sizeT,start,stepBy);
  }

  private void colorizeSubcaseTester(int pixType, int sizeC, int rgb, boolean indexed, boolean falseColor, int lutLen)
  {
    if ((pixType != FormatTools.UINT8) && (pixType != FormatTools.UINT16))
      throw new IllegalArgumentException("Invalid pixelType: not UINT8 or UINT16 ("+pixType+")");

    if (sizeC % rgb != 0)
      throw new IllegalArgumentException("Bad combo of sizeC and rgb: "+sizeC+" "+rgb);

    int channelsPerPlane = rgb;

    if (channelsPerPlane > 7)
      throw new IllegalArgumentException("Bad sizeC; channelsPerPlane > 7 : "+channelsPerPlane);

    int sizeX = 60, sizeY = 30, sizeZ = 1, sizeT = 1, numSeries = 1;

    String path = constructFakeFilename("colorColorized", pixType, sizeX, sizeY, sizeZ, sizeC, sizeT, numSeries, indexed, rgb, falseColor, lutLen);

    LOGGER.debug("colorizeSubcaseTester: {}", path);

    ImagePlus[] imps = null;

    try {
      ImporterOptions options = new ImporterOptions();
      options.setAutoscale(false);
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

    ImagePlus imp = imps[0];

    if (lutLen == -1)
      lutLen = 3;

    int expectedSizeC = effectiveC(sizeC, rgb, lutLen, indexed, falseColor);

    xyzctTest(imp,sizeX,sizeY,sizeZ,expectedSizeC,sizeT);

    // TODO: the following code conditional as BF sometimes does not return a CompositeImage. Handle better after BF
    //   changed and after I've handled all special cases.

    if (imp.isComposite())
    {
      CompositeImage ci = (CompositeImage)imp;

      assertTrue(ci.hasCustomLuts());

      assertEquals(CompositeImage.COLOR, ci.getMode());

      // TODO - falseColor stuff needs to be impl
      if (!falseColor)
        colorTests(ci,expectedSizeC,DEFAULT_COLOR_ORDER);

    }
    else {
      LOGGER.debug("  Not a composite image");
    }
  }

  // TODO - see if this can go away. How much does testColorColorize() handle this?
  // TODO - make a virtual case when working
  // TODO - enable tests rather thans prints. Its been a while since I worked on this and it may be working better now.
  @Test
  public void testColorizeSubcases() {
    LOGGER.debug("testColorizeSubcases()");

    // INDEXED and sizeC == 1,2,3,anything bigger than 3

    // sizeC == 1, rgb == 1, indexed, 8 bit, implicit lut length of 3 - KEY test to do, also note can vary lut len
    colorizeSubcaseTester(FormatTools.UINT8,1,1,INDEXED,REAL_COLOR,-1);
    colorizeSubcaseTester(FormatTools.UINT8,1,1,INDEXED,FALSE_COLOR,-1);
    colorizeSubcaseTester(FormatTools.UINT8,1,1,INDEXED,REAL_COLOR,2);

    // sizeC == 1, rgb == 1, indexed, 16 bit, implicit lut length of 3 - 2nd important test to do, also note can vary lut len
    colorizeSubcaseTester(FormatTools.UINT16,1,1,INDEXED,REAL_COLOR,-1);
    colorizeSubcaseTester(FormatTools.UINT16,1,1,INDEXED,FALSE_COLOR,-1);
    colorizeSubcaseTester(FormatTools.UINT16,1,1,INDEXED,REAL_COLOR,2);

    // sizeC = 3 and rgb = 1
    colorizeSubcaseTester(FormatTools.UINT8,3,1,INDEXED,REAL_COLOR,-1);
    colorizeSubcaseTester(FormatTools.UINT8,3,1,INDEXED,FALSE_COLOR,-1);                            // TODO - might be working

    // sizeC = 3 and rgb = 3 : interleaved
    colorizeSubcaseTester(FormatTools.UINT8,3,3,INDEXED,REAL_COLOR,-1);
    // TODO - enable this failing test
    //colorizeSubcaseTester(FormatTools.UINT8,3,3,INDEXED,FALSE_COLOR,-1);

    // NOT INDEXED

    // sizeC == 1 : don't test yet
    // TODO - is this limitation now fixed in BF? Do we need to test here?

    // sizeC = 4 and rgb = 4 : interleaved including alpha
    // if indexed == true this combo throws exception in CompositeImage constructor
    colorizeSubcaseTester(FormatTools.UINT8,4,4,NOT_INDEXED,REAL_COLOR,-1);

    // sizeC = 6, rgb = 3, indexed = false
    // if indexed == true this combo throws exception in CompositeImage constructor
    colorizeSubcaseTester(FormatTools.UINT8,6,3,NOT_INDEXED,REAL_COLOR,-1);

    // sizeC = 12, rgb = 3, indexed = false
    colorizeSubcaseTester(FormatTools.UINT8,12,3,NOT_INDEXED,REAL_COLOR,-1);
  }

  // TODO - see if this can go away. How much does testColorComposite() handle this?
  // TODO - make a virtual case when working
  @Test
  public void testCompositeSubcases()
  {
    // TODO - handle more cases with falseColor, rgb, etc.
    for (boolean indexed : BOOLEAN_STATES)
      for (int channels = 2; channels <= 7; channels++)
        if (!indexed)  // TODO - remove this limitation when BF updated
          compositeSubcaseTester(channels,indexed);
    LOGGER.debug("compositeSubcases() unfinished but 2<=sizeC<=7 nonindexed working");
  }

}
