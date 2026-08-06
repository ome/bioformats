/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2026 Open Microscopy Environment:
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

package loci.formats.out;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.swing.filechooser.FileFilter;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.gui.GUITools;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.NDPIReader;
import loci.formats.meta.IMetadata;
import loci.formats.meta.IPyramidStore;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Structural tests for the minimal NDPI container writer.
 */
public class NDPIWriterTest {

  /** TIFF type code of an ASCII value. */
  private static final int ASCII_TYPE = 2;

  private static final int WIDTH = 65536;
  private static final int HEIGHT = 24;

  private static final int PYRAMID_WIDTH = 512;
  private static final int PYRAMID_HEIGHT = 352;
  private static final int MACRO_WIDTH = 64;
  private static final int MACRO_HEIGHT = 352;
  private static final int MAP_WIDTH = 32;
  private static final int MAP_HEIGHT = 16;

  private static final String CALIBRATION_PROPERTIES =
    "System.Version=1..0\r\nCalibration.Version=500\r\n";

  /** Message of the simulated underlying output-stream close failure. */
  private static final String CLOSE_FAILURE =
    "Simulated NDPI output stream close failure";

  @Test
  public void testWriterRegistration() throws Exception {
    ImageWriter imageWriter = new ImageWriter();
    try {
      IFormatWriter writer = imageWriter.getWriter("registered.ndpi");
      assertEquals(writer.getClass(), NDPIWriter.class);
      assertEquals(writer.getFormat(), "Hamamatsu NDPI");
      assertEquals(writer.getSuffixes(), new String[] {"ndpi"});
      assertEquals(writer.getCompressionTypes(), new String[] {"JPEG"});

      int ndpiIndex = -1;
      int ndpiCount = 0;
      IFormatWriter[] writers = imageWriter.getWriters();
      for (int i = 0; i < writers.length; i++) {
        if (writers[i].getClass() == NDPIWriter.class) {
          ndpiIndex = i;
          ndpiCount++;
        }
      }
      assertEquals(ndpiCount, 1,
        "Writer discovery must expose NDPI exactly once");
      assertTrue(ndpiIndex >= 0);

      int ndpiFilterCount = 0;
      for (FileFilter filter : GUITools.buildFileFilters(imageWriter)) {
        if (filter.accept(new File("registered.ndpi"))) {
          ndpiFilterCount++;
          assertTrue(filter.getDescription().contains("Hamamatsu NDPI"));
        }
      }
      assertEquals(ndpiFilterCount, 1,
        "GUI writer discovery must expose NDPI exactly once");
    }
    finally {
      imageWriter.close();
    }
  }

  @Test
  public void testRoundTripPyramid() throws Exception {
    int[] widths = {7680, 3840};
    int[] heights = {352, 176};
    int[] restartMCUs = {480, 240};
    int[] mcuStartCounts = {88, 44};
    Path file = Files.createTempFile("bioformats-pyramid-", ".ndpi");
    try {
      IMetadata metadata = metadata(widths[0], heights[0]);
      IPyramidStore pyramid = (IPyramidStore) metadata;
      pyramid.setResolutionSizeX(new PositiveInteger(widths[1]), 0, 1);
      pyramid.setResolutionSizeY(new PositiveInteger(heights[1]), 0, 1);

      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      for (int resolution = 0; resolution < widths.length; resolution++) {
        writer.setResolution(resolution);
        byte[] pixels = pixels(widths[resolution], heights[resolution],
          resolution);
        writer.saveBytes(0, pixels, 0, 0, widths[resolution],
          heights[resolution]);
      }
      writer.close();

      assertAuthCodes(file, widths.length);
      assertMCUStarts(file);
      assertRestartGeometry(file, restartMCUs, mcuStartCounts);

      NDPIReader reader = new NDPIReader();
      try {
        reader.setMetadataStore(MetadataTools.createOMEXMLMetadata());
        reader.setFlattenedResolutions(false);
        reader.setId(file.toString());
        assertEquals(reader.getSeriesCount(), 1);
        assertEquals(reader.getResolutionCount(), widths.length);
        assertEquals(reader.getRGBChannelCount(), 3);
        assertEquals(reader.getBitsPerPixel(), 8);

        for (int resolution = 0; resolution < widths.length; resolution++) {
          reader.setResolution(resolution);
          assertEquals(reader.getSizeX(), widths[resolution]);
          assertEquals(reader.getSizeY(), heights[resolution]);
          byte[] expected =
            pixels(widths[resolution], heights[resolution], resolution);
          if (!reader.isInterleaved()) {
            expected = planar(expected);
          }
          assertJPEGClose(reader.openBytes(0), expected);
        }

        IMetadata readMetadata = (IMetadata) reader.getMetadataStore();
        assertEquals(
          readMetadata.getObjectiveNominalMagnification(0, 0), 40d);
        assertEquals(readMetadata.getPixelsPhysicalSizeX(0)
          .value(UNITS.MICROMETER).doubleValue(), 0.25d, 0.0001d);
        assertEquals(readMetadata.getPixelsPhysicalSizeY(0)
          .value(UNITS.MICROMETER).doubleValue(), 0.25d, 0.0001d);
      }
      finally {
        reader.close();
      }

    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testPlanarRGBRoundTrip() throws Exception {
    int width = 768;
    int height = 352;
    byte[] expected = pixels(width, height, 0);
    Path file = Files.createTempFile("bioformats-planar-", ".ndpi");
    try {
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(false);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, planar(expected));
      writer.close();

      NDPIReader reader = new NDPIReader();
      try {
        reader.setId(file.toString());
        byte[] actual = reader.openBytes(0);
        if (!reader.isInterleaved()) {
          expected = planar(expected);
        }
        assertJPEGClose(actual, expected);
      }
      finally {
        reader.close();
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testCompleteCloseKeepsOutput() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-complete-close-", ".ndpi");
    try {
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.close();

      assertTrue(Files.exists(file),
        "Closing a complete NDPI output must preserve the file");
      assertTrue(Files.size(file) > 0,
        "A preserved complete NDPI output must not be empty");
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testIncompleteCloseRemovesOutputWithoutFailing()
    throws Exception
  {
    Path file = Files.createTempFile("bioformats-incomplete-", ".ndpi");
    try {
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(WIDTH, HEIGHT));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, new byte[WIDTH * 3], 0, 0, WIDTH, 1);
      writer.close();

      assertFalse(Files.exists(file),
        "Incomplete NDPI output must be removed by close");
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testCloseFailureIsThePrimaryException() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-close-failure-", ".ndpi");
    try {
      FailingCloseNDPIWriter writer = new FailingCloseNDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.failNextClose();
      try {
        writer.close();
        fail("A failing underlying close must be reported");
      }
      catch (IOException e) {
        assertEquals(e.getMessage(), CLOSE_FAILURE,
          "The underlying close failure must be the primary exception");
        assertEquals(e.getSuppressed().length, 0,
          "A complete output must not add a cleanup failure");
      }
      assertTrue(Files.exists(file),
        "A complete NDPI output must survive a close failure");
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testUnremovableIncompleteOutputIsReported() throws Exception {
    Path directory =
      Files.createTempDirectory("bioformats-unremovable-");
    Path file = directory.resolve("incomplete.ndpi");
    try {
      NDPIWriter writer = new NDPIWriter();
      writeIncompletePixels(writer, file);
      lockDirectory(directory);
      try {
        writer.close();
        skipIfDeletionWasPermitted(file);
        fail("An unremovable incomplete NDPI output must be reported");
      }
      catch (IOException e) {
        assertTrue(e.getMessage().startsWith(
          "Could not remove incomplete NDPI output"),
          "Cleanup failures must describe the unremovable output, not \"" +
          e.getMessage() + "\"");
      }
    }
    finally {
      unlockDirectory(directory);
      Files.deleteIfExists(file);
      Files.deleteIfExists(directory);
    }
  }

  @Test
  public void testCleanupFailureIsSuppressedUnderCloseFailure()
    throws Exception
  {
    Path directory = Files.createTempDirectory("bioformats-both-failed-");
    Path file = directory.resolve("incomplete.ndpi");
    try {
      FailingCloseNDPIWriter writer = new FailingCloseNDPIWriter();
      writeIncompletePixels(writer, file);
      writer.failNextClose();
      lockDirectory(directory);
      try {
        writer.close();
        fail("A failing underlying close must be reported");
      }
      catch (IOException e) {
        assertEquals(e.getMessage(), CLOSE_FAILURE,
          "The close failure must remain the primary exception");
        skipIfDeletionWasPermitted(file);
        assertEquals(e.getSuppressed().length, 1,
          "The cleanup failure must be suppressed under the close failure");
        assertTrue(e.getSuppressed()[0].getMessage().startsWith(
          "Could not remove incomplete NDPI output"),
          "The suppressed exception must describe the cleanup failure");
      }
    }
    finally {
      unlockDirectory(directory);
      Files.deleteIfExists(file);
      Files.deleteIfExists(directory);
    }
  }

  @Test
  public void testExistingOutputIsTruncated() throws Exception {
    int width = 512;
    int height = 352;
    long staleLength = 8L * 1024 * 1024;
    Path file = Files.createTempFile("bioformats-existing-", ".ndpi");
    try {
      try (RandomAccessFile stale = new RandomAccessFile(file.toFile(), "rw")) {
        stale.setLength(staleLength);
        stale.seek(8);
        stale.writeInt(0x7fffffff);
      }

      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.close();

      assertTrue(Files.size(file) < staleLength,
        "Rewritten NDPI must not retain the old file length");
      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        in.order(true);
        in.seek(4);
        long header = in.readLong();
        assertEquals(header >>> 32, 0L,
          "NDPI header must contain an explicit zero high word");
        assertEquals(header, firstIFD(in),
          "The stale first-IFD pointer must have been overwritten");
        assertTrue(header > NDPIClassicTiffWriter.HEADER_LENGTH,
          "The first NDPI directory must follow the image it describes");
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testLongTagsAndLittleEndianContainer() throws Exception {
    Path file = Files.createTempFile("bioformats-", ".ndpi");
    try {
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(WIDTH, HEIGHT));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.setId(file.toString());

      byte[] row = new byte[WIDTH * 3];
      for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
          int offset = x * 3;
          row[offset] = (byte) (x + y);
          row[offset + 1] = (byte) (x >>> 3);
          row[offset + 2] = (byte) (y * 11);
        }
        writer.saveBytes(0, row, 0, y, WIDTH, 1);
      }
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        assertEquals(in.readUnsignedByte(), 'I');
        assertEquals(in.readUnsignedByte(), 'I');
        in.order(true);
        in.seek(4);
        long ifdOffset = in.readLong();
        assertTrue(ifdOffset > NDPIClassicTiffWriter.HEADER_LENGTH,
          "The first NDPI directory must follow its own image");

        assertTag(in, ifdOffset, IFD.IMAGE_WIDTH, 4, WIDTH);
        assertTag(in, ifdOffset, IFD.IMAGE_LENGTH, 4, HEIGHT);
        assertTag(in, ifdOffset, IFD.ROWS_PER_STRIP, 4, HEIGHT);
        assertTag(in, ifdOffset, NDPITags.TISSUE_INDEX, 4, 0);
        assertTag(in, ifdOffset, NDPITags.AUTH_CODE, 4, null);
        assertTag(in, ifdOffset, NDPITags.JPEG_QUALITY, 4, 80);
        assertTag(in, ifdOffset, NDPITags.VERSION, 3, 1);
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  /**
   * Native NDPI output interleaves images and directories: for each image
   * its own external values, then its payload, then its restart index where
   * it has one, then its completed directory. Every IFD of every genuine
   * file in the reference corpus is laid out this way.
   */
  @Test
  public void testNativePhysicalLayout() throws Exception {
    Path file = Files.createTempFile("bioformats-native-layout-", ".ndpi");
    try {
      writeNativeLayoutOutput(file);
      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        long[] ifds = chain(in);
        assertEquals(ifds.length, 4, "Two pyramid levels, the macro and the " +
          "tissue map must each be described");

        long previousIFD = NDPIClassicTiffWriter.HEADER_LENGTH;
        for (int i = 0; i < ifds.length; i++) {
          long payload = extendedScalar(in, ifds[i], IFD.STRIP_OFFSETS);
          long payloadEnd =
            payload + extendedScalar(in, ifds[i], IFD.STRIP_BYTE_COUNTS);
          assertEquals(ifds[i] & 1, 0,
            "NDPI directories must begin on TIFF word boundaries");
          assertTrue(payload > previousIFD,
            "Image " + i + " must be written after the previous directory");
          assertTrue(payloadEnd <= ifds[i],
            "Image " + i + " must be written before its own directory");
          assertRestartIndexPlacement(in, ifds[i], payloadEnd);
          previousIFD = ifds[i];
        }
        assertEquals(nextIFDOffset(in, ifds[ifds.length - 1]), 0L,
          "The final NDPI directory must end the chain");
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  /**
   * Values an IFD names are written before it: the ones every directory
   * repeats once, near the beginning of the file, and the rest immediately
   * before the image they belong to.
   */
  @Test
  public void testExternalValuePlacement() throws Exception {
    Path file = Files.createTempFile("bioformats-external-values-", ".ndpi");
    try {
      writeNativeLayoutOutput(file);
      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        long[] ifds = chain(in);
        long firstPayload = extendedScalar(in, ifds[0], IFD.STRIP_OFFSETS);

        int[] shared = {
          IFD.SOFTWARE, IFD.MAKE, IFD.MODEL, IFD.DATE_TIME,
          NDPITags.REFERENCE, NDPITags.SERIAL_NUMBER, NDPITags.CALIBRATION
        };
        for (int tag : shared) {
          long offset = externalOffset(in, ifds[0], tag);
          assertTrue(offset < firstPayload,
            "Shared value " + tag + " must precede every payload");
          for (long ifd : ifds) {
            assertEquals(externalOffset(in, ifd, tag), offset,
              "Every NDPI directory must reuse shared value " + tag);
          }
        }

        assertEquals(entry(in, ifds[0], NDPITags.REFERENCE)[1], 3L,
          "The two-character reference must be stored as three ASCII bytes");
        assertTrue(externalOffset(in, ifds[0], NDPITags.REFERENCE) >
          NDPIClassicTiffWriter.HEADER_LENGTH,
          "Short ASCII values must still be stored outside their entry");

        long previousIFD = NDPIClassicTiffWriter.HEADER_LENGTH;
        for (int i = 0; i < ifds.length; i++) {
          long payload = extendedScalar(in, ifds[i], IFD.STRIP_OFFSETS);
          for (int tag : new int[] {IFD.X_RESOLUTION, IFD.Y_RESOLUTION}) {
            long offset = externalOffset(in, ifds[i], tag);
            assertTrue(offset > previousIFD && offset < payload,
              "Resolution value " + tag + " of image " + i +
              " must be written just before that image");
          }
          previousIFD = ifds[i];
        }

        for (long ifd : ifds) {
          for (long[] region : externalRegions(in, ifd)) {
            assertEquals(region[0] & 1, 0,
              "External NDPI values must begin on TIFF word boundaries");
            assertTrue(region[0] + region[1] <= ifd,
              "External NDPI values must be complete before their IFD");
          }
        }
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  /**
   * Exercises the 64-bit header and chain pointers by placing the first
   * payload just below 4 GiB, so that its directory, the whole of the second
   * image and every value that image names lie above the classic TIFF
   * address space.
   */
  @Test
  public void testSparseChainBeyondFourGiB() throws Exception {
    int width = 512;
    int height = 352;
    long boundary = 0x100000000L;
    long sparseOffset = boundary - 2048;
    Path file = Files.createTempFile("bioformats-sparse-", ".ndpi");
    try {
      IMetadata metadata = metadata(width, height);
      IPyramidStore pyramid = (IPyramidStore) metadata;
      pyramid.setResolutionSizeX(new PositiveInteger(width / 2), 0, 1);
      pyramid.setResolutionSizeY(new PositiveInteger(height / 2), 0, 1);

      SparseNDPIWriter writer = new SparseNDPIWriter();
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.seekPayload(sparseOffset);
      byte[] expected = pixels(width, height, 0);
      writer.saveBytes(0, expected, 0, 0, width, height);
      writer.setResolution(1);
      writer.saveBytes(0, pixels(width / 2, height / 2, 1), 0, 0,
        width / 2, height / 2);
      writer.close();

      assertTrue(Files.size(file) > boundary,
        "Sparse NDPI must cross the classic TIFF 4 GiB boundary");
      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        long[] ifds = chain(in);
        assertEquals(ifds.length, 2, "Both resolutions must be described");
        assertEquals(ifds[0], firstIFD(in),
          "The header must name the first directory");
        assertTrue(firstIFD(in) >>> 32 != 0,
          "The header first-IFD pointer must use its high word");
        assertTrue(nextIFDOffset(in, ifds[0]) >>> 32 != 0,
          "The chain's next-IFD pointer must use its high word");
        assertEquals(nextIFDOffset(in, ifds[1]), 0L,
          "The final NDPI directory must end the chain");

        long payload = extendedScalar(in, ifds[0], IFD.STRIP_OFFSETS);
        long length = extendedScalar(in, ifds[0], IFD.STRIP_BYTE_COUNTS);
        assertTrue(externalOffset(in, ifds[0], IFD.X_RESOLUTION) >=
          sparseOffset,
          "The first image's own values must be written where it begins");
        assertTrue(payload < boundary && payload + length > boundary,
          "The first payload must cross the 4 GiB boundary");
        assertTrue(payload + length <= ifds[0] && ifds[0] > boundary,
          "The first directory must follow its payload, above 4 GiB");
        assertTrue(extendedScalar(in, ifds[1], IFD.STRIP_OFFSETS) > boundary,
          "The second payload must lie above 4 GiB");

        assertTrue(externalOffset(in, ifds[0], NDPITags.MCU_STARTS) > boundary,
          "The restart index must be addressed above 4 GiB");
        assertTrue(externalOffset(in, ifds[1], IFD.X_RESOLUTION) > boundary,
          "Per-image values must be addressed above 4 GiB");
        assertTrue(externalOffset(in, ifds[1], IFD.SOFTWARE) < boundary,
          "Shared values stay below 4 GiB, where the file begins");

        in.order(false);
        in.seek(payload);
        assertEquals(in.readUnsignedShort(), 0xffd8,
          "Reconstructed 64-bit strip offset must point to JPEG SOI");
      }

      NDPIReader reader = new NDPIReader();
      try {
        reader.setFlattenedResolutions(false);
        reader.setId(file.toString());
        assertEquals(reader.getResolutionCount(), 2,
          "The reader must recover both resolutions of a 4 GiB NDPI");
        byte[] readExpected = reader.isInterleaved() ?
          expected : planar(expected);
        assertJPEGClose(reader.openBytes(0), readExpected);
      }
      finally {
        reader.close();
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testSerializesAllHighWordTags() throws Exception {
    long payloadOffset = 0x123456789aL;
    long payloadLength = 0x23456789abL;
    long[] starts = {0x3456789abcL, 0x456789abcdL};
    ByteArrayHandle handle = new ByteArrayHandle();
    long ifdOffset;
    try (RandomAccessOutputStream out = new RandomAccessOutputStream(handle)) {
      NDPIClassicTiffWriter saver = new NDPIClassicTiffWriter(out);
      saver.writeHeader();
      java.util.List<NDPIClassicTiffWriter.Entry> entries =
        new java.util.ArrayList<NDPIClassicTiffWriter.Entry>();
      entries.add(saver.entry(NDPIClassicTiffWriter.value(
        NDPITags.MCU_STARTS, words(starts, 0))));
      entries.add(saver.entry(NDPIClassicTiffWriter.value(
        NDPITags.MCU_STARTS_HIGH_BYTES, words(starts, 32))));
      IFD ifd = new IFD();
      NDPIWriter.putStripTags(ifd, payloadOffset, payloadLength);
      for (NDPIClassicTiffWriter.Value value :
        NDPIClassicTiffWriter.values(ifd))
      {
        entries.add(saver.entry(value));
      }
      NDPIClassicTiffWriter.Directory directory = saver.writeIFD(entries);
      ifdOffset = directory.offset();
      saver.patchOffset(NDPIClassicTiffWriter.FIRST_IFD_POINTER, ifdOffset);
    }

    try (RandomAccessInputStream in = new RandomAccessInputStream(handle)) {
      assertEquals(firstIFD(in), ifdOffset,
        "The header must be backpatched with the completed IFD offset");
      assertEquals(extendedScalar(in, ifdOffset, IFD.STRIP_OFFSETS),
        payloadOffset);
      assertEquals(extendedScalar(in, ifdOffset, IFD.STRIP_BYTE_COUNTS),
        payloadLength);
      assertTrue(externalOffset(in, ifdOffset, NDPITags.MCU_STARTS) < ifdOffset,
        "External values must precede the directory that names them");
      TiffParser parser = new TiffParser(in);
      IFD parsed = parser.getIFD(ifdOffset);
      parser.fillInIFD(parsed);
      assertEquals(reconstruct(parsed.getIFDLongArray(NDPITags.MCU_STARTS),
        parsed.getIFDLongArray(NDPITags.MCU_STARTS_HIGH_BYTES)), starts);
    }
  }

  /**
   * A negative signed value is an ordinary inline scalar, so its extension
   * word must stay zero rather than sign-extending into a huge offset.
   */
  @Test
  public void testNegativeScalarExtendsToZero() throws Exception {
    ByteArrayHandle handle = new ByteArrayHandle();
    long ifdOffset;
    try (RandomAccessOutputStream out = new RandomAccessOutputStream(handle)) {
      NDPIClassicTiffWriter saver = new NDPIClassicTiffWriter(out);
      saver.writeHeader();
      IFD ifd = new IFD();
      ifd.putIFDValue(NDPITags.X_POSITION, -101L);
      java.util.List<NDPIClassicTiffWriter.Entry> entries =
        new java.util.ArrayList<NDPIClassicTiffWriter.Entry>();
      for (NDPIClassicTiffWriter.Value value :
        NDPIClassicTiffWriter.values(ifd))
      {
        entries.add(saver.entry(value));
      }
      ifdOffset = saver.writeIFD(entries).offset();
    }

    try (RandomAccessInputStream in = new RandomAccessInputStream(handle)) {
      assertEquals(extensionWord(in, ifdOffset, NDPITags.X_POSITION), 0L,
        "A negative SLONG must receive a zero extension word");
    }
  }

  @Test
  public void testRejectsMissingObjectiveMagnification() throws Exception {
    Path file = Files.createTempFile("bioformats-", ".ndpi");
    try {
      IMetadata metadata = metadata(WIDTH, HEIGHT);
      metadata.setObjectiveNominalMagnification(null, 0, 0);
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      try {
        writer.setId(file.toString());
        fail("Missing objective magnification must be rejected");
      }
      catch (loci.formats.FormatException e) {
        assertEquals(e.getMessage(),
          "NDPI requires a positive objective nominal magnification");
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsMissingObjectiveSettings() throws Exception {
    Path file = Files.createTempFile("bioformats-", ".ndpi");
    try {
      IMetadata metadata = metadata(WIDTH, HEIGHT);
      metadata.setObjectiveSettingsID(null, 0);
      metadata.setObjectiveNominalMagnification(null, 0, 0);
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      try {
        writer.setId(file.toString());
        fail("Missing objective settings must be rejected");
      }
      catch (loci.formats.FormatException e) {
        assertEquals(e.getMessage(),
          "NDPI requires a positive objective nominal magnification");
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsUnsupportedWriterContracts() throws Exception {
    assertSetIdRejected(metadata(64, 64), false, true,
      "NDPI requires sequential writing");

    IMetadata uint16 = metadata(64, 64);
    uint16.setPixelsType(PixelType.UINT16, 0);
    assertSetIdRejected(uint16, true, true,
      "NDPI supports only unsigned 8-bit pixels");

    IMetadata nonRGB = metadata(64, 64);
    nonRGB.setPixelsSizeC(new PositiveInteger(1), 0);
    nonRGB.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);
    assertSetIdRejected(nonRGB, true, true,
      "NDPI requires SizeC == 3 as one RGB plane");

    IMetadata unordered = metadata(64, 64);
    IPyramidStore pyramid = (IPyramidStore) unordered;
    pyramid.setResolutionSizeX(new PositiveInteger(128), 0, 1);
    pyramid.setResolutionSizeY(new PositiveInteger(32), 0, 1);
    assertSetIdRejected(unordered, true, true,
      "NDPI resolutions must be ordered largest to smallest");
  }

  @Test
  public void testRejectsNonSequentialPixelWrites() throws Exception {
    int width = 64;
    int height = 64;
    Path file = Files.createTempFile("bioformats-contract-", ".ndpi");
    Files.delete(file);
    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      try {
        writer.saveBytes(0, new byte[(width - 1) * 3], 0, 0,
          width - 1, 1);
        fail("Partial-width NDPI writes must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(),
          "NDPI row blocks must span the full width");
      }
      try {
        writer.saveBytes(0, new byte[width * 3], 0, 1, width, 1);
        fail("Out-of-order NDPI rows must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(),
          "NDPI rows must be written once, top to bottom");
      }
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsOutOfOrderResolutionWrites() throws Exception {
    Path file = Files.createTempFile("bioformats-resolution-order-", ".ndpi");
    Files.delete(file);
    NDPIWriter writer = new NDPIWriter();
    try {
      IMetadata metadata = metadata(128, 64);
      IPyramidStore pyramid = (IPyramidStore) metadata;
      pyramid.setResolutionSizeX(new PositiveInteger(64), 0, 1);
      pyramid.setResolutionSizeY(new PositiveInteger(32), 0, 1);
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      try {
        writer.setResolution(1);
        fail("Skipping the largest NDPI resolution must be rejected");
      }
      catch (IllegalArgumentException e) {
        assertEquals(e.getMessage(),
          "NDPI resolutions must be written from largest to smallest");
      }
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsInvalidAssociatedSeriesContracts() throws Exception {
    IMetadata unidentified = metadata(64, 32);
    addImage(unidentified, 1, 32, 16, 3, 3);
    assertMetadataOptionsRejected(unidentified, new DynamicMetadataOptions(),
      "NDPI requires every non-pyramid series to be identified as macro or " +
      "tissue map");

    IMetadata duplicate = metadata(64, 32);
    addImage(duplicate, 1, 32, 16, 3, 3);
    addImage(duplicate, 2, 16, 8, 1, 1);
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.MACRO_SERIES_KEY, 1);
    options.setInteger(NDPIWriter.TISSUE_MAP_SERIES_KEY, 1);
    assertMetadataOptionsRejected(duplicate, options,
      "NDPI macro and tissue-map series must be different");

    options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.MACRO_SERIES_KEY, 2);
    options.setInteger(NDPIWriter.TISSUE_MAP_SERIES_KEY, 1);
    assertMetadataOptionsRejected(duplicate, options,
      "NDPI macro series must follow the pyramid");

    IMetadata oneAssociated = metadata(64, 32);
    addImage(oneAssociated, 1, 16, 8, 1, 1);
    options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.TISSUE_MAP_SERIES_KEY, 2);
    assertMetadataOptionsRejected(oneAssociated, options,
      "NDPI associated-image series index is out of range");

    options = new DynamicMetadataOptions();
    options.setBoolean(NDPIWriter.LABEL_OBSCURED_KEY, true);
    assertMetadataOptionsRejected(metadata(64, 32), options,
      "NDPI label-obscured option requires a macro series");

    IMetadata multiPlaneMacro = metadata(64, 32);
    addImage(multiPlaneMacro, 1, 32, 16, 3, 3);
    multiPlaneMacro.setPixelsSizeT(new PositiveInteger(2), 1);
    options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.MACRO_SERIES_KEY, 1);
    assertMetadataOptionsRejected(multiPlaneMacro, options,
      "NDPI macro requires exactly one image plane and resolution");
  }

  @Test
  public void testSmallLevelOmitsIndexAndAuthCode() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-small-", ".ndpi");
    try {
      DynamicMetadataOptions options = new DynamicMetadataOptions();
      options.set(NDPIWriter.REFERENCE_KEY, "");
      options.setLong(NDPIWriter.EXPOSURE_RATIO_KEY, 123L);
      options.setLong(NDPIWriter.RED_MULTIPLIER_KEY, 456L);
      options.setLong(NDPIWriter.GREEN_MULTIPLIER_KEY, 789L);
      options.setLong(NDPIWriter.BLUE_MULTIPLIER_KEY, 1011L);
      options.setFloat(NDPIWriter.WAVELENGTH_KEY, 550.5f);

      NDPIWriter writer = new NDPIWriter();
      IMetadata metadata = metadata(width, height);
      metadata.setImageAcquisitionDate(
        new Timestamp("2026-07-29T19:56:16-04:00"), 0);
      writer.setMetadataRetrieve(metadata);
      writer.setMetadataOptions(options);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      byte[] expected = pixels(width, height, 0);
      writer.saveBytes(0, expected, 0, 0, width, height);
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        TiffParser parser = new TiffParser(in);
        IFD ifd = parser.getFirstIFD();
        parser.fillInIFD(ifd);
        assertFalse(ifd.containsKey(NDPITags.MCU_STARTS));
        assertFalse(ifd.containsKey(NDPITags.MCU_STARTS_HIGH_BYTES));
        assertFalse(ifd.containsKey(NDPITags.AUTH_CODE));
        assertOrdinaryJPEG(in, ifd);
        assertFalse(ifd.containsKey(NDPITags.REFERENCE),
          "Empty ASCII options must be omitted");
        assertFalse(ifd.containsKey(IFD.MAKE),
          "Unknown microscope manufacturer must be omitted");
        assertFalse(ifd.containsKey(IFD.MODEL),
          "Unknown microscope model must be omitted");
        assertTag(in, firstIFD(in), NDPITags.EXPOSURE_RATIO, 4, 123);
        assertTag(in, firstIFD(in), NDPITags.RED_MULTIPLIER, 4, 456);
        assertTag(in, firstIFD(in), NDPITags.GREEN_MULTIPLIER, 4, 789);
        assertTag(in, firstIFD(in), NDPITags.BLUE_MULTIPLIER, 4, 1011);
        assertTag(in, firstIFD(in), NDPITags.X_POSITION, 9, 0);
        assertTag(in, firstIFD(in), NDPITags.Y_POSITION, 9, 0);
        assertTag(in, firstIFD(in), NDPITags.Z_POSITION, 9, 0);
        assertFloatTag(in, firstIFD(in), NDPITags.WAVELENGTH, 550.5f);
        assertAsciiTag(in, firstIFD(in), IFD.DATE_TIME,
          "2026:07:29 23:56:16");
      }

      NDPIReader reader = new NDPIReader();
      try {
        reader.setId(file.toString());
        byte[] readExpected = reader.isInterleaved() ?
          expected : planar(expected);
        assertJPEGClose(reader.openBytes(0), readExpected);
      }
      finally {
        reader.close();
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testSignedPositionAndOffsetTags() throws Exception {
    Path file = writePrivateMetadataOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      long ifd = firstIFD(in);
      assertTag(in, ifd, NDPITags.X_POSITION, 9, -101);
      assertTag(in, ifd, NDPITags.Y_POSITION, 9, 202);
      assertTag(in, ifd, NDPITags.Z_POSITION, 9, -303);
      assertTag(in, ifd, NDPITags.REFOCUS_INTERVAL, 9, -15);
      assertTag(in, ifd, NDPITags.FOCUS_OFFSET, 9, 16);
      assertEquals(extensionWord(in, ifd, NDPITags.X_POSITION), 0L,
        "Signed X position must reserve a zero 64-bit extension word");
      assertEquals(extensionWord(in, ifd, NDPITags.Y_POSITION), 0L,
        "Signed Y position must reserve a zero 64-bit extension word");
      assertEquals(extensionWord(in, ifd, NDPITags.Z_POSITION), 0L,
        "Signed Z position must reserve a zero 64-bit extension word");
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testExposureGainTimingAndAutofocusTags() throws Exception {
    Path file = writePrivateMetadataOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      long ifd = firstIFD(in);
      assertTag(in, ifd, NDPITags.EXPOSURE_RATIO, 4, 11);
      assertTag(in, ifd, NDPITags.RED_MULTIPLIER, 4, 12);
      assertTag(in, ifd, NDPITags.GREEN_MULTIPLIER, 4, 13);
      assertTag(in, ifd, NDPITags.BLUE_MULTIPLIER, 4, 14);
      assertFloatTag(in, ifd, NDPITags.WAVELENGTH, 550.5f);
      assertTag(in, ifd, NDPITags.LAMP_AGE, 4, 17);
      assertTag(in, ifd, NDPITags.EXPOSURE_TIME, 4, 18);
      assertTag(in, ifd, NDPITags.FOCUS_TIME, 4, 19);
      assertTag(in, ifd, NDPITags.SCAN_TIME, 4, 20);
      assertTag(in, ifd, NDPITags.WRITE_TIME, 4, 21);
      assertTag(in, ifd, NDPITags.FULLY_AUTO_FOCUS, 4, 1);
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testFocusPointAndRegionArrays() throws Exception {
    Path file = writePrivateMetadataOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      assertTagCount(in, firstIFD(in), NDPITags.FOCUS_POINTS, 9, 6);
      assertTagCount(in, firstIFD(in), NDPITags.FOCUS_POINT_REGIONS, 9, 2);
      for (IFD parsed : mainIFDs(in)) {
        assertEquals(parsed.getIFDIntArray(NDPITags.FOCUS_POINTS),
          new int[] {-1, 2, -3, 4, 5, 6},
          "Every NDPI IFD must repeat the signed focus-point triplets");
        assertEquals(parsed.getIFDIntArray(NDPITags.FOCUS_POINT_REGIONS),
          new int[] {7, -8},
          "Every NDPI IFD must repeat the signed focus-point regions");
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testScannerIdentityAndPropertyMapTags() throws Exception {
    Path file = writePrivateMetadataOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      long ifd = firstIFD(in);
      assertTag(in, ifd, NDPITags.VERSION, 3, 1);
      assertFloatTag(in, ifd, NDPITags.SOURCE_LENS, 40f);
      assertTag(in, ifd, NDPITags.CAPTURE_MODE, 4, 0);
      assertTag(in, ifd, NDPITags.JPEG_QUALITY, 4, 80);
      assertAsciiTag(in, ifd, NDPITags.REFERENCE, "A1");
      assertAsciiTag(in, ifd, NDPITags.SERIAL_NUMBER, "RGB");
      assertAsciiTag(in, ifd, IFD.MAKE, "Acme");
      assertAsciiTag(in, ifd, IFD.MODEL, "S1");
      assertAsciiTag(in, ifd, NDPITags.FIRMWARE_VERSION, "IVR");
      assertAsciiTag(in, ifd, NDPITags.CALIBRATION, CALIBRATION_PROPERTIES);
      for (IFD parsed : mainIFDs(in)) {
        assertEquals(parsed.getIFDStringValue(NDPITags.CALIBRATION),
          CALIBRATION_PROPERTIES,
          "Every NDPI IFD must repeat the calibration property map");
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testBarcodeAndMedicalRegulationTags() throws Exception {
    Path file = writePrivateMetadataOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      for (int i = 0; i < 8; i++) {
        assertAsciiTag(in, firstIFD(in), NDPITags.FIRST_BARCODE + i, "B" + i);
      }
      assertAsciiTag(in, firstIFD(in), NDPITags.MEDICAL_REGULATION, "MR");
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testFileWideASCIITagsRepeatInEveryIFD() throws Exception {
    Path file = writePrivateMetadataOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      long secondIFD = nextIFDOffset(in, firstIFD(in));
      assertTrue(secondIFD > firstIFD(in),
        "The second NDPI directory must follow the first");
      assertEquals(secondIFD & 1, 0,
        "NDPI IFDs must begin on TIFF word boundaries");
      assertAsciiTag(in, secondIFD, NDPITags.REFERENCE, "A1");
      assertAsciiTag(in, secondIFD, NDPITags.SERIAL_NUMBER, "RGB");
      assertAsciiTag(in, secondIFD, IFD.MAKE, "Acme");
      assertAsciiTag(in, secondIFD, IFD.MODEL, "S1");
      assertAsciiTag(in, secondIFD, NDPITags.FIRMWARE_VERSION, "IVR");
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testEmptyASCIIOptionsAreOmitted() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-empty-options-", ".ndpi");
    try {
      DynamicMetadataOptions options = new DynamicMetadataOptions();
      options.set(NDPIWriter.REFERENCE_KEY, "");
      options.set(NDPIWriter.SERIAL_NUMBER_KEY, "");
      options.set(NDPIWriter.SCANNER_MANUFACTURER_KEY, "");
      options.set(NDPIWriter.SCANNER_MODEL_KEY, "");
      options.set(NDPIWriter.FIRMWARE_VERSION_KEY, "");
      options.set(NDPIWriter.CALIBRATION_KEY, "");
      for (int i = 0; i < 8; i++) {
        options.set(NDPIWriter.BARCODE_KEY_PREFIX + i, "");
      }
      options.set(NDPIWriter.MEDICAL_REGULATION_KEY, "");

      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setMetadataOptions(options);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0));
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        TiffParser parser = new TiffParser(in);
        IFD ifd = parser.getFirstIFD();
        parser.fillInIFD(ifd);
        int[] omitted = {
          NDPITags.REFERENCE, NDPITags.SERIAL_NUMBER,
          NDPITags.FIRMWARE_VERSION, NDPITags.CALIBRATION,
          NDPITags.MEDICAL_REGULATION, IFD.MAKE, IFD.MODEL
        };
        for (int tag : omitted) {
          assertFalse(ifd.containsKey(tag),
            "Empty ASCII option must omit TIFF tag " + tag);
        }
        for (int i = 0; i < 8; i++) {
          assertFalse(ifd.containsKey(NDPITags.FIRST_BARCODE + i),
            "Empty barcode option must omit TIFF tag " +
            (NDPITags.FIRST_BARCODE + i));
        }
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testJPEGQualityPropagationAndValidation() throws Exception {
    byte[] unspecified = writeAtQuality(0, 80);
    byte[] quality80 = writeAtQuality(0.8, 80);
    assertTrue(Arrays.equals(unspecified, quality80),
      "Unspecified quality must use the NDPI default");
    byte[] quality90 = writeAtQuality(0.9, 90);
    assertFalse(Arrays.equals(quality80, quality90),
      "Caller quality must affect JPEG encoding");

    double[] invalid = {
      Double.NaN, Double.POSITIVE_INFINITY, 0.24, 1.01
    };
    for (double quality : invalid) {
      Path file = Files.createTempFile("bioformats-quality-rejected-", ".ndpi");
      Files.delete(file);
      NDPIWriter writer = new NDPIWriter();
      try {
        CodecOptions options = CodecOptions.getDefaultOptions();
        options.quality = quality;
        writer.setCodecOptions(options);
        writer.setMetadataRetrieve(metadata(64, 32));
        writer.setInterleaved(true);
        writer.setWriteSequentially(true);
        try {
          writer.setId(file.toString());
          fail("Invalid JPEG quality must be rejected");
        }
        catch (FormatException e) {
          assertEquals(e.getMessage(),
            "NDPI JPEG quality must be between 0.25 and 1");
        }
        assertFalse(Files.exists(file),
          "JPEG quality must be rejected before opening the output");
      }
      finally {
        Files.deleteIfExists(file);
      }
    }
  }

  @Test
  public void testRejectsInvalidPrivateMetadataOptions() throws Exception {
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.setLong(NDPIWriter.EXPOSURE_RATIO_KEY, -1L);
    assertOptionsRejected(options,
      "NDPI exposure ratio must fit an unsigned 32-bit integer");

    options = new DynamicMetadataOptions();
    options.setLong(NDPIWriter.X_POSITION_KEY, 0x80000000L);
    assertOptionsRejected(options,
      "NDPI X position must fit a signed 32-bit integer");

    options = new DynamicMetadataOptions();
    options.setLong(NDPIWriter.CAPTURE_MODE_KEY, 1L);
    assertOptionsRejected(options,
      "NDPI capture mode must be 0 for brightfield RGB");

    options = new DynamicMetadataOptions();
    options.setFloat(NDPIWriter.WAVELENGTH_KEY, Float.NaN);
    assertOptionsRejected(options,
      "NDPI wavelength must be finite and non-negative");

    options = new DynamicMetadataOptions();
    options.set(NDPIWriter.REFERENCE_KEY, "non\u00a0ASCII");
    assertOptionsRejected(options,
      "NDPI slide reference must contain printable ASCII");

    options = new DynamicMetadataOptions();
    options.set(NDPIWriter.FOCUS_POINTS_KEY, "1,2");
    assertOptionsRejected(options,
      "NDPI focus points must contain X/Y/Z triplets");

    options = new DynamicMetadataOptions();
    options.set(NDPIWriter.FOCUS_POINT_REGIONS_KEY, "1,,2");
    assertOptionsRejected(options,
      "NDPI focus-point regions contains an empty value");

    options = new DynamicMetadataOptions();
    options.set(NDPIWriter.CALIBRATION_KEY, "CAL");
    assertOptionsRejected(options,
      "NDPI calibration metadata must contain key=value records");

    options = new DynamicMetadataOptions();
    options.set(NDPIWriter.CALIBRATION_KEY, "System.Version=1\nProduct=X");
    assertOptionsRejected(options,
      "NDPI calibration metadata must use printable ASCII and CRLF");

    options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.RESTART_MCUS_KEY, 0);
    assertOptionsRejected(options,
      "NDPI restart interval must be between 1 and 65535 MCUs");

    options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.RESTART_MCUS_KEY, 257);
    assertMetadataOptionsRejected(metadata(64, 32), options,
      "NDPI restart interval must exactly divide the base image " +
      "MCU-column count");
  }

  @Test
  public void testExactMCUIndexBoundary() throws Exception {
    assertIndexPresence(64, 43 * 8, false);
    assertIndexPresence(64, 44 * 8, true);
  }

  @Test
  public void testAuthCodeSamplingAndClamping() throws Exception {
    long[] starts = uniformMCUStarts(44, 60);
    byte[][] entropy = authEntropy(60);
    int baseline = invokeAuthCode(entropy, starts);

    byte[][] changed = clone(entropy);
    changed[2][48] ^= 1;
    assertTrue(invokeAuthCode(changed, starts) != baseline,
      "Changing an authenticated byte must change AuthCode");

    changed = clone(entropy);
    changed[2][0] ^= 1;
    assertEquals(invokeAuthCode(changed, starts), baseline,
      "Changing an unauthenticated byte must not change AuthCode");

    starts = uniformMCUStarts(44, 1);
    entropy = new byte[][] {{1}, {2}, {3}, {4}};
    int expected =
      0x1d6edb2a ^ 1 ^ (3 << 8) ^ (4 << 16) ^ (2 << 24);
    assertEquals(invokeAuthCode(entropy, starts), expected,
      "Short AuthCode segments must clamp sampling to their final byte");
  }

  @Test
  public void testDeterministicRestartGridPolicy() throws Exception {
    assertEquals(NDPIWriter.automaticRestartInterval(496), 496);
    assertEquals(NDPIWriter.automaticRestartInterval(1024), 512);
    assertEquals(NDPIWriter.automaticRestartInterval(1440), 480);
    assertEquals(NDPIWriter.automaticRestartInterval(1280), 256);
    assertEquals(NDPIWriter.automaticRestartInterval(720), 240);
    assertEquals(NDPIWriter.automaticRestartInterval(3840), 480);
    assertEquals(NDPIWriter.baseRestartInterval(3840, null), 480);
    assertEquals(NDPIWriter.baseRestartInterval(3840, 240), 240);
    assertEquals(NDPIWriter.automaticRestartInterval(1030), 206);
    assertEquals(NDPIWriter.restartIntervalForGrid(3840, 8), 480);
    assertEquals(NDPIWriter.restartIntervalForGrid(1920, 8), 240);
    assertEquals(NDPIWriter.restartIntervalForGrid(960, 8), 120);
    assertEquals(NDPIWriter.restartIntervalForGrid(125, 8), 0);

    assertIndexPresence(512, 44 * 8 + 1, false);
    assertIndexPresence(800, 596, false);

    // The base level's 1024 MCU columns divide into two restart-interval
    // columns, which the 125 MCU columns of the second level cannot preserve.
    IMetadata incompatible = metadata(8192, 352);
    IPyramidStore pyramid = (IPyramidStore) incompatible;
    pyramid.setResolutionSizeX(new PositiveInteger(1000), 0, 1);
    pyramid.setResolutionSizeY(new PositiveInteger(344), 0, 1);
    assertLevelIndexPresence(incompatible, new int[] {8192, 1000},
      new int[] {352, 344}, new boolean[] {true, false});
  }

  @Test
  public void testRestartMCUOverrideIsWritten() throws Exception {
    int width = 30720;
    int height = 24;
    int restartMCUs = 240;
    Path file = Files.createTempFile("bioformats-restart-override-", ".ndpi");
    try {
      DynamicMetadataOptions options = new DynamicMetadataOptions();
      options.setInteger(NDPIWriter.RESTART_MCUS_KEY, restartMCUs);
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setMetadataOptions(options);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0));
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        TiffParser parser = new TiffParser(in);
        IFD ifd = parser.getFirstIFD();
        parser.fillInIFD(ifd);
        assertEquals(ifd.getIFDLongArray(NDPITags.MCU_STARTS).length,
          width / (restartMCUs * 8) * (height / 8));
        assertEquals(readRestartInterval(in, ifd.getStripOffsets()[0]),
          restartMCUs);
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testMacroPixelRoundTrip() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    NDPIReader reader = new NDPIReader();
    try {
      reader.setFlattenedResolutions(false);
      reader.setId(output.file.toString());
      reader.setSeries(1);
      assertEquals(reader.getSizeX(), MACRO_WIDTH,
        "The macro series must keep its written width");
      assertEquals(reader.getSizeY(), MACRO_HEIGHT,
        "The macro series must keep its written height");
      byte[] expected = reader.isInterleaved() ?
        output.macro : planar(output.macro);
      assertJPEGClose(reader.openBytes(0), expected);
    }
    finally {
      reader.close();
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testTissueMapPixelRoundTrip() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    NDPIReader reader = new NDPIReader();
    try {
      reader.setFlattenedResolutions(false);
      reader.setId(output.file.toString());
      reader.setSeries(2);
      assertEquals(reader.getSizeX(), MAP_WIDTH,
        "The tissue-map series must keep its written width");
      assertEquals(reader.getSizeY(), MAP_HEIGHT,
        "The tissue-map series must keep its written height");
      assertEquals(reader.openBytes(0), output.map,
        "Uncompressed tissue-map pixels must round trip exactly, using " +
        "only the valid prefix of an oversized caller buffer");
    }
    finally {
      reader.close();
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testAssociatedSeriesOrdering() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    try {
      try (RandomAccessInputStream in =
        new RandomAccessInputStream(output.file.toString()))
      {
        java.util.List<IFD> ifds = mainIFDs(in);
        assertEquals(ifds.size(), 3,
          "NDPI must write one IFD each for the pyramid, macro and map");
        assertEquals(ifds.get(0).getImageWidth(), PYRAMID_WIDTH,
          "The pyramid must be written first");
        assertEquals(ifds.get(1).getImageWidth(), MACRO_WIDTH,
          "The macro must be written after the pyramid");
        assertEquals(ifds.get(2).getImageWidth(), MAP_WIDTH,
          "The tissue map must be written after the macro");
        assertEquals(
          ((Float) ifds.get(1).get(NDPITags.SOURCE_LENS)).floatValue(), -1f,
          "The macro IFD must be identified by SourceLens -1");
        assertEquals(
          ((Float) ifds.get(2).get(NDPITags.SOURCE_LENS)).floatValue(), -2f,
          "The tissue-map IFD must be identified by SourceLens -2");
      }

      NDPIReader reader = new NDPIReader();
      try {
        reader.setFlattenedResolutions(false);
        reader.setId(output.file.toString());
        assertEquals(reader.getSeriesCount(), 3,
          "The reader must recover the pyramid, macro and map series");
      }
      finally {
        reader.close();
      }
    }
    finally {
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testMacroLabelObscured() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(output.file.toString()))
    {
      java.util.List<IFD> ifds = mainIFDs(in);
      assertEquals(ifds.get(1).getIFDLongValue(NDPITags.LABEL_OBSCURED, -1),
        1, "The macro IFD must record the label-obscured option");
      assertFalse(ifds.get(2).containsKey(NDPITags.LABEL_OBSCURED),
        "The tissue-map IFD must omit the macro-only LabelObscured tag");
    }
    finally {
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testAssociatedImageTagRoles() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(output.file.toString()))
    {
      java.util.List<IFD> ifds = mainIFDs(in);
      IFD pyramidIFD = ifds.get(0);
      IFD macroIFD = ifds.get(1);
      IFD mapIFD = ifds.get(2);
      for (int tag : pyramidOnlyTags()) {
        assertTrue(pyramidIFD.containsKey(tag),
          "Pyramid IFD must contain private tag " + tag);
        assertFalse(macroIFD.containsKey(tag),
          "Macro IFD must omit pyramid-only tag " + tag);
        assertFalse(mapIFD.containsKey(tag),
          "Tissue-map IFD must omit pyramid-only tag " + tag);
      }
      for (int tag : fileWideTags()) {
        for (IFD ifd : ifds) {
          assertTrue(ifd.containsKey(tag),
            "Every IFD must contain file-wide tag " + tag);
        }
      }
      for (IFD ifd : ifds) {
        assertEquals(ifd.getIFDTextValue(IFD.MAKE), "Acme",
          "Every IFD must repeat the microscope manufacturer");
        assertEquals(ifd.getIFDTextValue(IFD.MODEL), "Scope",
          "Every IFD must repeat the microscope model");
        assertEquals(ifd.getIFDTextValue(IFD.SOFTWARE),
          "Bio-Formats NDPIWriter",
          "Every IFD must repeat the writing software");
        assertEquals(ifd.getIFDTextValue(IFD.DATE_TIME),
          "2026:07:29 23:56:16",
          "Every IFD must repeat the acquisition date");
      }
      assertEquals(macroIFD.getIFDLongValue(NDPITags.X_POSITION, -1), 0,
        "The macro must record a zero X position");
      assertEquals(macroIFD.getIFDLongValue(NDPITags.Y_POSITION, -1), 0,
        "The macro must record a zero Y position");
      assertEquals(mapIFD.getIFDLongValue(NDPITags.X_POSITION, -1), 0,
        "The tissue map must record a zero X position");
      assertEquals(mapIFD.getIFDLongValue(NDPITags.Y_POSITION, -1), 0,
        "The tissue map must record a zero Y position");
      assertTrue(macroIFD.containsKey(NDPITags.JPEG_QUALITY),
        "The JPEG-compressed macro must record its JPEG quality");
      assertTrue(macroIFD.containsKey(NDPITags.CAPTURE_MODE),
        "The macro must record its capture mode");
      assertFalse(mapIFD.containsKey(NDPITags.JPEG_QUALITY),
        "The uncompressed tissue map must omit JPEG quality");
      assertFalse(mapIFD.containsKey(NDPITags.CAPTURE_MODE),
        "The tissue map must omit the capture mode");
    }
    finally {
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testAssociatedImageEncoding() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(output.file.toString()))
    {
      java.util.List<IFD> ifds = mainIFDs(in);
      IFD pyramidIFD = ifds.get(0);
      IFD macroIFD = ifds.get(1);
      IFD mapIFD = ifds.get(2);
      long[] referenceBlackWhite = {0, 255, 128, 255, 128, 255};
      for (IFD ifd : new IFD[] {pyramidIFD, macroIFD}) {
        assertEquals(ifd.getPhotometricInterpretation(),
          loci.formats.tiff.PhotoInterp.Y_CB_CR,
          "JPEG NDPI images must be YCbCr");
        assertEquals(ifd.getIFDIntArray(IFD.Y_CB_CR_SUB_SAMPLING),
          new int[] {1, 1}, "JPEG NDPI images must not subsample chroma");
        assertEquals(ifd.getIFDLongArray(IFD.REFERENCE_BLACK_WHITE),
          referenceBlackWhite,
          "JPEG NDPI images must declare full-range YCbCr");
      }
      assertFalse(macroIFD.containsKey(NDPITags.MCU_STARTS),
        "The macro must not be indexed with restart offsets");
      assertFalse(macroIFD.containsKey(NDPITags.AUTH_CODE),
        "The unindexed macro must omit AuthCode");
      assertFalse(mapIFD.containsKey(IFD.REFERENCE_BLACK_WHITE),
        "The uncompressed tissue map must omit ReferenceBlackWhite");
      assertEquals(mapIFD.getCompression(), TiffCompression.UNCOMPRESSED,
        "The tissue map must be stored uncompressed");
      assertEquals(mapIFD.getSamplesPerPixel(), 1,
        "The tissue map must be single-channel");
      long macroIFDOffset = nextIFDOffset(in, firstIFD(in));
      assertTagCount(in, firstIFD(in), IFD.REFERENCE_BLACK_WHITE, 4, 6);
      assertTagCount(in, macroIFDOffset, IFD.REFERENCE_BLACK_WHITE, 4, 6);
      assertOrdinaryJPEG(in, macroIFD);
    }
    finally {
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testAssociatedImagePhysicalResolution() throws Exception {
    AssociatedOutput output = writeAssociatedOutput();
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(output.file.toString()))
    {
      java.util.List<IFD> ifds = mainIFDs(in);
      IFD macroIFD = ifds.get(1);
      IFD mapIFD = ifds.get(2);
      assertEquals(macroIFD.getIFDRationalValue(IFD.X_RESOLUTION)
        .doubleValue(), 2000d, 0.0001d,
        "The macro X resolution must invert its 5 um physical size");
      assertEquals(macroIFD.getIFDRationalValue(IFD.Y_RESOLUTION)
        .doubleValue(), 10000d / 6, 0.0001d,
        "The macro Y resolution must invert its 6 um physical size");
      assertEquals(macroIFD.getIFDLongValue(IFD.RESOLUTION_UNIT, -1), 3,
        "NDPI physical resolutions must be per centimeter");
      assertEquals(mapIFD.getIFDRationalValue(IFD.X_RESOLUTION)
        .doubleValue(), 500d, 0.0001d,
        "The tissue-map X resolution must invert its 20 um physical size");
      assertEquals(mapIFD.getIFDRationalValue(IFD.Y_RESOLUTION)
        .doubleValue(), 10000d / 24, 0.0001d,
        "The tissue-map Y resolution must invert its 24 um physical size");
      assertEquals(mapIFD.getIFDLongValue(IFD.RESOLUTION_UNIT, -1), 3,
        "NDPI physical resolutions must be per centimeter");
    }
    finally {
      Files.deleteIfExists(output.file);
    }
  }

  @Test
  public void testRejectsRGBTissueMap() throws Exception {
    IMetadata metadata = metadata(512, 352);
    addImage(metadata, 1, 64, 64, 3, 3);
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.TISSUE_MAP_SERIES_KEY, 1);
    assertMetadataOptionsRejected(metadata, options,
      "NDPI tissue map requires SizeC == 1 in one plane");
  }

  @Test
  public void testWriterCapabilityContract() throws Exception {
    NDPIWriter writer = new NDPIWriter();
    try {
      assertFalse(writer.canDoStacks(),
        "NDPI stores one plane per image and must not claim stacks");
      assertEquals(writer.getPixelTypes("JPEG"),
        new int[] {FormatTools.UINT8},
        "NDPI must offer only unsigned 8-bit pixels");
    }
    finally {
      writer.close();
    }
  }

  @Test
  public void testRejectsNonJPEGCompression() throws Exception {
    NDPIWriter writer = new NDPIWriter();
    try {
      try {
        writer.setCompression("LZW");
        fail("A non-JPEG NDPI codec must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(), "NDPI supports only JPEG compression");
      }
      assertEquals(writer.getCompression(), "JPEG",
        "A rejected codec must leave JPEG compression selected");
      writer.setCompression("JPEG");
      assertEquals(writer.getCompression(), "JPEG",
        "JPEG must remain selected when it is requested explicitly");
      writer.setCompression(null);
      assertEquals(writer.getCompression(), "JPEG",
        "An unspecified codec must leave JPEG compression selected");
    }
    finally {
      writer.close();
    }
  }

  @Test
  public void testSetIdDiscardsThePreviousOutput() throws Exception {
    int width = 64;
    int height = 32;
    Path first = Files.createTempFile("bioformats-reopen-first-", ".ndpi");
    Path second = Files.createTempFile("bioformats-reopen-second-", ".ndpi");
    NDPIWriter writer = openWriter(metadata(width, height), null, first);
    try {
      assertTrue(Files.exists(first),
        "The first NDPI output must have been opened");
      writer.setId(second.toString());
      assertFalse(Files.exists(first),
        "Reopening must close the previous incomplete NDPI output, which " +
        "close() then removes");
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.close();
      assertTrue(Files.exists(second),
        "The reopened NDPI output must be completed normally");
    }
    finally {
      writer.close();
      Files.deleteIfExists(first);
      Files.deleteIfExists(second);
    }
  }

  @Test
  public void testUntruncatableExistingOutputIsReported() throws Exception {
    Path directory = Files.createTempDirectory("bioformats-untruncatable-");
    Path file = directory.resolve("existing.ndpi");
    Files.write(file, new byte[] {1, 2, 3, 4});
    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata(64, 32));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      lockDirectory(directory);
      try {
        writer.setId(file.toString());
        // A process that may delete from a read-only directory, such as
        // root, cannot exercise this failure.
        throw new SkipException(
          "This platform allows deletion from a read-only directory");
      }
      catch (IOException e) {
        assertEquals(e.getMessage(),
          "Could not truncate existing NDPI output: " + file.toString(),
          "Truncation failures must name the unremovable output");
      }
    }
    finally {
      unlockDirectory(directory);
      writer.close();
      Files.deleteIfExists(file);
      Files.deleteIfExists(directory);
    }
  }

  @Test
  public void testRejectsNonZeroAssociatedResolution() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-macro-resolution-", ".ndpi");
    NDPIWriter writer =
      openWriter(macroMetadata(width, height), macroOptions(), file);
    try {
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.setSeries(1);
      try {
        writer.setResolution(1);
        fail("A non-zero NDPI associated-image resolution must be rejected");
      }
      catch (IllegalArgumentException e) {
        assertEquals(e.getMessage(),
          "NDPI associated images support only resolution zero");
      }
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsUnexpectedSeriesOrder() throws Exception {
    Path file = Files.createTempFile("bioformats-series-order-", ".ndpi");
    NDPIWriter writer =
      openWriter(macroMetadata(64, 32), macroOptions(), file);
    try {
      writer.setSeries(1);
      fail("Writing the NDPI macro before the pyramid must be rejected");
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(),
        "NDPI series must be written as pyramid, macro, then tissue map");
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsWritesAfterCompletion() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-after-complete-", ".ndpi");
    NDPIWriter writer = openWriter(metadata(width, height), null, file);
    try {
      byte[] pixels = pixels(width, height, 0);
      writer.saveBytes(0, pixels, 0, 0, width, height);
      try {
        writer.saveBytes(0, pixels, 0, 0, width, height);
        fail("Writing after the NDPI directories must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(), "NDPI writing is already complete");
      }
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  /**
   * A second OME Channel makes the inherited plane-index check accept plane
   * one, so that the NDPI single-plane contract is the rule under test.
   */
  @Test
  public void testRejectsASecondPlane() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-second-plane-", ".ndpi");
    IMetadata metadata = metadata(width, height);
    metadata.setChannelID("Channel:0:1", 0, 1);
    metadata.setChannelSamplesPerPixel(new PositiveInteger(3), 0, 1);
    NDPIWriter writer = openWriter(metadata, null, file);
    try {
      writer.saveBytes(1, pixels(width, height, 0), 0, 0, width, height);
      fail("A second NDPI plane must be rejected");
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(), "NDPI supports one plane per image");
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsRewritingACompletedResolution() throws Exception {
    int width = 128;
    int height = 64;
    Path file = Files.createTempFile("bioformats-resolution-rewrite-",
      ".ndpi");
    IMetadata metadata = metadata(width, height);
    IPyramidStore pyramid = (IPyramidStore) metadata;
    pyramid.setResolutionSizeX(new PositiveInteger(width / 2), 0, 1);
    pyramid.setResolutionSizeY(new PositiveInteger(height / 2), 0, 1);
    NDPIWriter writer = openWriter(metadata, null, file);
    try {
      byte[] pixels = pixels(width, height, 0);
      writer.saveBytes(0, pixels, 0, 0, width, height);
      writer.saveBytes(0, pixels, 0, 0, width, height);
      fail("Rewriting a completed NDPI resolution must be rejected");
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(),
        "NDPI resolutions must be written from largest to smallest");
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsMultiplePlanesInMetadata() throws Exception {
    IMetadata multiZ = metadata(64, 32);
    multiZ.setPixelsSizeZ(new PositiveInteger(2), 0);
    assertSetIdRejected(multiZ, true, true, "NDPI supports one RGB plane");

    IMetadata multiT = metadata(64, 32);
    multiT.setPixelsSizeT(new PositiveInteger(2), 0);
    assertSetIdRejected(multiT, true, true, "NDPI supports one RGB plane");
  }

  @Test
  public void testRejectsInvalidAssociatedSeriesLayout() throws Exception {
    IMetadata wideMacro = macroMetadata(64, 32);
    wideMacro.setPixelsType(PixelType.UINT16, 1);
    assertMetadataOptionsRejected(wideMacro, macroOptions(),
      "NDPI macro supports only unsigned 8-bit pixels");

    IMetadata multiResolutionMap = metadata(64, 32);
    addImage(multiResolutionMap, 1, 32, 16, 1, 1);
    IPyramidStore pyramid = (IPyramidStore) multiResolutionMap;
    // The store only counts resolutions for an image once every earlier
    // image has its own resolution list, so the pyramid is given one too.
    pyramid.setResolutionSizeX(new PositiveInteger(32), 0, 1);
    pyramid.setResolutionSizeY(new PositiveInteger(16), 0, 1);
    pyramid.setResolutionSizeX(new PositiveInteger(16), 1, 1);
    pyramid.setResolutionSizeY(new PositiveInteger(8), 1, 1);
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.TISSUE_MAP_SERIES_KEY, 1);
    assertMetadataOptionsRejected(multiResolutionMap, options,
      "NDPI tissue map requires exactly one image plane and resolution");
  }

  @Test
  public void testPlanePositionZIsWrittenInNanometers() throws Exception {
    Path file = writeZPositionOutput(new Length(1.5, UNITS.MICROMETER), null);
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      assertTag(in, firstIFD(in), NDPITags.Z_POSITION, 9, 1500);
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testExplicitZPositionOverridesPlanePosition() throws Exception {
    Path file =
      writeZPositionOutput(new Length(1.5, UNITS.MICROMETER), -303L);
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      assertTag(in, firstIFD(in), NDPITags.Z_POSITION, 9, -303);
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testRejectsOversizedFocusPointValues() throws Exception {
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.set(NDPIWriter.FOCUS_POINTS_KEY, "1,2,2147483648");
    assertOptionsRejected(options,
      "NDPI focus points values must fit signed 32-bit integers");

    options = new DynamicMetadataOptions();
    options.set(NDPIWriter.FOCUS_POINT_REGIONS_KEY, "-2147483649");
    assertOptionsRejected(options,
      "NDPI focus-point regions values must fit signed 32-bit integers");
  }

  @Test
  public void testPrimeMCUColumnCountUsesSingleMCUIntervals()
    throws Exception
  {
    assertEquals(NDPIWriter.automaticRestartInterval(521), 1,
      "A prime MCU-column count above 512 must fall back to one MCU");
  }

  @Test
  public void testRejectsInvalidMCUSegment() throws Exception {
    long[] starts = uniformMCUStarts(44, 60);
    byte[][] truncated = authEntropy(60);
    truncated[1] = new byte[59];
    try {
      invokeAuthCode(truncated, starts);
      fail("A mismatched NDPI MCU segment length must be rejected");
    }
    catch (java.lang.reflect.InvocationTargetException e) {
      assertEquals(e.getCause().getMessage(),
        "NDPI JPEG contains an invalid MCU segment");
    }
  }

  @Test
  public void testRejectsUnsupportedTIFFValueType() throws Exception {
    IFD ifd = new IFD();
    ifd.putIFDValue(IFD.IMAGE_WIDTH, Boolean.TRUE);
    try {
      NDPIClassicTiffWriter.values(ifd);
      fail("An unsupported NDPI TIFF value type must be rejected");
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(),
        "Unsupported NDPI TIFF value for tag " + IFD.IMAGE_WIDTH);
    }
  }

  private static IMetadata metadata(int width, int height) throws Exception {
    IMetadata metadata = MetadataTools.createOMEXMLMetadata();
    metadata.setImageID("Image:0", 0);
    metadata.setPixelsID("Pixels:0", 0);
    metadata.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
    metadata.setPixelsSizeX(new PositiveInteger(width), 0);
    metadata.setPixelsSizeY(new PositiveInteger(height), 0);
    metadata.setPixelsSizeZ(new PositiveInteger(1), 0);
    metadata.setPixelsSizeC(new PositiveInteger(3), 0);
    metadata.setPixelsSizeT(new PositiveInteger(1), 0);
    metadata.setPixelsType(PixelType.UINT8, 0);
    metadata.setPixelsBigEndian(false, 0);
    metadata.setChannelID("Channel:0", 0, 0);
    metadata.setChannelSamplesPerPixel(new PositiveInteger(3), 0, 0);
    metadata.setPixelsPhysicalSizeX(new Length(0.25, UNITS.MICROMETER), 0);
    metadata.setPixelsPhysicalSizeY(new Length(0.25, UNITS.MICROMETER), 0);
    metadata.setInstrumentID("Instrument:0", 0);
    metadata.setImageInstrumentRef("Instrument:0", 0);
    metadata.setObjectiveID("Objective:0", 0, 0);
    metadata.setObjectiveNominalMagnification(40d, 0, 0);
    metadata.setObjectiveSettingsID("Objective:0", 0);
    return metadata;
  }

  /**
   * Opens an interleaved, sequential NDPI writer, which the caller must
   * close. Options may be null.
   */
  private static NDPIWriter openWriter(IMetadata metadata,
    DynamicMetadataOptions options, Path file) throws Exception
  {
    NDPIWriter writer = new NDPIWriter();
    writer.setMetadataRetrieve(metadata);
    if (options != null) writer.setMetadataOptions(options);
    writer.setInterleaved(true);
    writer.setWriteSequentially(true);
    writer.setId(file.toString());
    return writer;
  }

  /** Describes one pyramid of the given size followed by an RGB macro. */
  private static IMetadata macroMetadata(int width, int height)
    throws Exception
  {
    IMetadata metadata = metadata(width, height);
    addImage(metadata, 1, width / 2, height / 2, 3, 3);
    return metadata;
  }

  /** Identifies the second series of macroMetadata as the macro. */
  private static DynamicMetadataOptions macroOptions() {
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.MACRO_SERIES_KEY, 1);
    return options;
  }

  /**
   * Writes one completed NDPI pyramid whose metadata carries a plane Z
   * position, optionally overridden by an explicit Z-position option.
   * Returns the new output, which the caller must delete.
   */
  private static Path writeZPositionOutput(Length planeZ, Long option)
    throws Exception
  {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-plane-z-", ".ndpi");
    try {
      IMetadata metadata = metadata(width, height);
      metadata.setPlanePositionZ(planeZ, 0, 0);
      DynamicMetadataOptions options = new DynamicMetadataOptions();
      if (option != null) {
        options.setLong(NDPIWriter.Z_POSITION_KEY, option);
      }
      NDPIWriter writer = openWriter(metadata, options, file);
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.close();
    }
    catch (Exception e) {
      Files.deleteIfExists(file);
      throw e;
    }
    return file;
  }

  /**
   * One NDPI output holding a pyramid, a macro and a tissue map, together
   * with the pixels each associated series was written from.
   */
  private static final class AssociatedOutput {

    private final Path file;
    private final byte[] macro;
    private final byte[] map;

    private AssociatedOutput(Path file, byte[] macro, byte[] map) {
      this.file = file;
      this.macro = macro;
      this.map = map;
    }
  }

  /**
   * Writes a fresh pyramid, macro and tissue-map NDPI output, returning it
   * with its expected associated-series pixels. Every call produces an
   * independent temporary file, which the caller must delete.
   */
  private static AssociatedOutput writeAssociatedOutput() throws Exception {
    Path file = Files.createTempFile("bioformats-associated-", ".ndpi");
    byte[] macro = pixels(MACRO_WIDTH, MACRO_HEIGHT, 1);
    byte[] map = new byte[MAP_WIDTH * MAP_HEIGHT];
    for (int i = 0; i < map.length; i++) map[i] = (byte) (i * 7);

    // The tissue map is written from an oversized, partly stale buffer so
    // that only its valid prefix may reach the output.
    byte[] reusableMapBuffer = new byte[map.length + 97];
    System.arraycopy(map, 0, reusableMapBuffer, 0, map.length);
    for (int i = map.length; i < reusableMapBuffer.length; i++) {
      reusableMapBuffer[i] = (byte) 0xa5;
    }

    try {
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(associatedMetadata());
      writer.setMetadataOptions(associatedOptions());
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(PYRAMID_WIDTH, PYRAMID_HEIGHT, 0), 0, 0,
        PYRAMID_WIDTH, PYRAMID_HEIGHT);
      writer.setSeries(1);
      writer.setResolution(0);
      writer.saveBytes(0, macro, 0, 0, MACRO_WIDTH, MACRO_HEIGHT);
      writer.setSeries(2);
      writer.setResolution(0);
      writer.saveBytes(0, reusableMapBuffer, 0, 0, MAP_WIDTH, MAP_HEIGHT);
      writer.close();
    }
    catch (Exception e) {
      Files.deleteIfExists(file);
      throw e;
    }
    return new AssociatedOutput(file, macro, map);
  }

  /** Describes the pyramid, macro and tissue-map series of one NDPI file. */
  private static IMetadata associatedMetadata() throws Exception {
    IMetadata metadata = metadata(PYRAMID_WIDTH, PYRAMID_HEIGHT);
    addImage(metadata, 1, MACRO_WIDTH, MACRO_HEIGHT, 3, 3);
    addImage(metadata, 2, MAP_WIDTH, MAP_HEIGHT, 1, 1);
    metadata.setMicroscopeManufacturer("Acme", 0);
    metadata.setMicroscopeModel("Scope", 0);
    metadata.setImageAcquisitionDate(
      new Timestamp("2026-07-29T19:56:16-04:00"), 0);
    metadata.setPixelsPhysicalSizeX(new Length(5, UNITS.MICROMETER), 1);
    metadata.setPixelsPhysicalSizeY(new Length(6, UNITS.MICROMETER), 1);
    metadata.setPixelsPhysicalSizeX(new Length(20, UNITS.MICROMETER), 2);
    metadata.setPixelsPhysicalSizeY(new Length(24, UNITS.MICROMETER), 2);
    return metadata;
  }

  /** Identifies the associated series and sets one value per private tag. */
  private static DynamicMetadataOptions associatedOptions() {
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.setInteger(NDPIWriter.MACRO_SERIES_KEY, 1);
    options.setInteger(NDPIWriter.TISSUE_MAP_SERIES_KEY, 2);
    options.setBoolean(NDPIWriter.LABEL_OBSCURED_KEY, true);
    options.set(NDPIWriter.REFERENCE_KEY, "role-test");
    options.setLong(NDPIWriter.X_POSITION_KEY, 101L);
    options.setLong(NDPIWriter.Y_POSITION_KEY, 202L);
    options.setLong(NDPIWriter.Z_POSITION_KEY, 303L);
    options.setLong(NDPIWriter.EXPOSURE_RATIO_KEY, 11L);
    options.setLong(NDPIWriter.RED_MULTIPLIER_KEY, 12L);
    options.setLong(NDPIWriter.GREEN_MULTIPLIER_KEY, 13L);
    options.setLong(NDPIWriter.BLUE_MULTIPLIER_KEY, 14L);
    options.set(NDPIWriter.SERIAL_NUMBER_KEY, "serial");
    options.setLong(NDPIWriter.REFOCUS_INTERVAL_KEY, 15L);
    options.setLong(NDPIWriter.FOCUS_OFFSET_KEY, 16L);
    options.set(NDPIWriter.FIRMWARE_VERSION_KEY, "firmware");
    options.set(NDPIWriter.CALIBRATION_KEY, CALIBRATION_PROPERTIES);
    options.set(NDPIWriter.FOCUS_POINTS_KEY, "1,2,3");
    options.set(NDPIWriter.FOCUS_POINT_REGIONS_KEY, "4");
    options.setFloat(NDPIWriter.WAVELENGTH_KEY, 550f);
    options.setLong(NDPIWriter.LAMP_AGE_KEY, 17L);
    options.setLong(NDPIWriter.EXPOSURE_TIME_KEY, 18L);
    options.setLong(NDPIWriter.FOCUS_TIME_KEY, 19L);
    options.setLong(NDPIWriter.SCAN_TIME_KEY, 20L);
    options.setLong(NDPIWriter.WRITE_TIME_KEY, 21L);
    options.setBoolean(NDPIWriter.FULLY_AUTO_FOCUS_KEY, true);
    options.set(NDPIWriter.BARCODE_KEY_PREFIX + 0, "barcode");
    options.set(NDPIWriter.MEDICAL_REGULATION_KEY, "regulation");
    return options;
  }

  /** Private tags that only a pyramid level may carry. */
  private static int[] pyramidOnlyTags() {
    return new int[] {
      NDPITags.TISSUE_INDEX, NDPITags.Z_POSITION, NDPITags.EXPOSURE_RATIO,
      NDPITags.RED_MULTIPLIER, NDPITags.GREEN_MULTIPLIER,
      NDPITags.BLUE_MULTIPLIER, NDPITags.WAVELENGTH, NDPITags.LAMP_AGE,
      NDPITags.EXPOSURE_TIME, NDPITags.FOCUS_TIME, NDPITags.SCAN_TIME,
      NDPITags.WRITE_TIME, NDPITags.FULLY_AUTO_FOCUS
    };
  }

  /** Private tags that every IFD of one NDPI file must repeat. */
  private static int[] fileWideTags() {
    return new int[] {
      NDPITags.REFERENCE, NDPITags.FOCUS_POINTS,
      NDPITags.FOCUS_POINT_REGIONS, NDPITags.SERIAL_NUMBER,
      NDPITags.REFOCUS_INTERVAL, NDPITags.FOCUS_OFFSET,
      NDPITags.FIRMWARE_VERSION, NDPITags.CALIBRATION,
      NDPITags.FIRST_BARCODE, NDPITags.MEDICAL_REGULATION
    };
  }

  /** Parses and fully populates every main IFD of an NDPI output. */
  private static java.util.List<IFD> mainIFDs(RandomAccessInputStream in)
    throws Exception
  {
    TiffParser parser = new TiffParser(in);
    java.util.List<IFD> ifds = parser.getMainIFDs();
    for (IFD ifd : ifds) parser.fillInIFD(ifd);
    return ifds;
  }

  /**
   * Writes a two-resolution NDPI output with every private metadata option
   * set to a distinct value. Every call produces an independent temporary
   * file, which the caller must delete.
   */
  private static Path writePrivateMetadataOutput() throws Exception {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-options-", ".ndpi");
    try {
      IMetadata metadata = metadata(width, height);
      IPyramidStore pyramid = (IPyramidStore) metadata;
      pyramid.setResolutionSizeX(new PositiveInteger(width / 2), 0, 1);
      pyramid.setResolutionSizeY(new PositiveInteger(height / 2), 0, 1);

      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata);
      writer.setMetadataOptions(privateMetadataOptions());
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.setResolution(1);
      writer.saveBytes(0, pixels(width / 2, height / 2, 1), 0, 0,
        width / 2, height / 2);
      writer.close();
    }
    catch (Exception e) {
      Files.deleteIfExists(file);
      throw e;
    }
    return file;
  }

  /** Sets one distinct value for every supported private metadata option. */
  private static DynamicMetadataOptions privateMetadataOptions() {
    DynamicMetadataOptions options = new DynamicMetadataOptions();
    options.set(NDPIWriter.REFERENCE_KEY, "A1");
    options.setLong(NDPIWriter.X_POSITION_KEY, -101L);
    options.setLong(NDPIWriter.Y_POSITION_KEY, 202L);
    options.setLong(NDPIWriter.Z_POSITION_KEY, -303L);
    options.setLong(NDPIWriter.EXPOSURE_RATIO_KEY, 11L);
    options.setLong(NDPIWriter.RED_MULTIPLIER_KEY, 12L);
    options.setLong(NDPIWriter.GREEN_MULTIPLIER_KEY, 13L);
    options.setLong(NDPIWriter.BLUE_MULTIPLIER_KEY, 14L);
    options.set(NDPIWriter.FOCUS_POINTS_KEY, "-1, 2, -3, 4, 5, 6");
    options.set(NDPIWriter.FOCUS_POINT_REGIONS_KEY, "7, -8");
    options.setLong(NDPIWriter.CAPTURE_MODE_KEY, 0L);
    options.set(NDPIWriter.SERIAL_NUMBER_KEY, "RGB");
    options.set(NDPIWriter.SCANNER_MANUFACTURER_KEY, "Acme");
    options.set(NDPIWriter.SCANNER_MODEL_KEY, "S1");
    options.setLong(NDPIWriter.REFOCUS_INTERVAL_KEY, -15L);
    options.setLong(NDPIWriter.FOCUS_OFFSET_KEY, 16L);
    options.set(NDPIWriter.FIRMWARE_VERSION_KEY, "IVR");
    options.set(NDPIWriter.CALIBRATION_KEY, CALIBRATION_PROPERTIES);
    options.setFloat(NDPIWriter.WAVELENGTH_KEY, 550.5f);
    options.setLong(NDPIWriter.LAMP_AGE_KEY, 17L);
    options.setLong(NDPIWriter.EXPOSURE_TIME_KEY, 18L);
    options.setLong(NDPIWriter.FOCUS_TIME_KEY, 19L);
    options.setLong(NDPIWriter.SCAN_TIME_KEY, 20L);
    options.setLong(NDPIWriter.WRITE_TIME_KEY, 21L);
    options.setBoolean(NDPIWriter.FULLY_AUTO_FOCUS_KEY, true);
    for (int i = 0; i < 8; i++) {
      options.set(NDPIWriter.BARCODE_KEY_PREFIX + i, "B" + i);
    }
    options.set(NDPIWriter.MEDICAL_REGULATION_KEY, "MR");
    return options;
  }

  /**
   * Opens the given output and writes one row of a much taller image, so
   * that the writer never completes its directory chain.
   */
  private static void writeIncompletePixels(NDPIWriter writer, Path file)
    throws Exception
  {
    writer.setMetadataRetrieve(metadata(WIDTH, HEIGHT));
    writer.setInterleaved(true);
    writer.setWriteSequentially(true);
    writer.setId(file.toString());
    writer.saveBytes(0, new byte[WIDTH * 3], 0, 0, WIDTH, 1);
  }

  /** Makes the given directory reject the removal of its entries. */
  private static void lockDirectory(Path directory) {
    directory.toFile().setWritable(false);
  }

  /** Restores write access so the fixture can always be cleaned up. */
  private static void unlockDirectory(Path directory) {
    directory.toFile().setWritable(true);
  }

  /**
   * Skips the calling test when the platform removed the incomplete output
   * from a read-only directory anyway, which a process running as root is
   * allowed to do.
   */
  private static void skipIfDeletionWasPermitted(Path file) {
    if (!Files.exists(file)) {
      throw new SkipException(
        "This platform allows deletion from a read-only directory");
    }
  }

  private static void addImage(IMetadata metadata, int image, int width,
    int height, int sizeC, int samplesPerPixel)
  {
    metadata.setImageID("Image:" + image, image);
    metadata.setPixelsID("Pixels:" + image, image);
    metadata.setPixelsDimensionOrder(DimensionOrder.XYZCT, image);
    metadata.setPixelsSizeX(new PositiveInteger(width), image);
    metadata.setPixelsSizeY(new PositiveInteger(height), image);
    metadata.setPixelsSizeZ(new PositiveInteger(1), image);
    metadata.setPixelsSizeC(new PositiveInteger(sizeC), image);
    metadata.setPixelsSizeT(new PositiveInteger(1), image);
    metadata.setPixelsType(PixelType.UINT8, image);
    metadata.setPixelsBigEndian(false, image);
    metadata.setChannelID("Channel:" + image + ":0", image, 0);
    metadata.setChannelSamplesPerPixel(
      new PositiveInteger(samplesPerPixel), image, 0);
  }

  private static byte[] pixels(int width, int height, int resolution) {
    byte[] pixels = new byte[width * height * 3];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int offset = (y * width + x) * 3;
        pixels[offset] = (byte) (32 + (x / 16 + resolution * 7) % 160);
        pixels[offset + 1] = (byte) (48 + (y / 16) % 144);
        pixels[offset + 2] =
          (byte) (64 + ((x / 32 + y / 32) * 9) % 128);
      }
    }
    return pixels;
  }

  private static void assertJPEGClose(byte[] actual, byte[] expected) {
    assertEquals(actual.length, expected.length);
    long totalDifference = 0;
    int maximumDifference = 0;
    for (int i = 0; i < actual.length; i++) {
      int difference = Math.abs((actual[i] & 0xff) - (expected[i] & 0xff));
      totalDifference += difference;
      maximumDifference = Math.max(maximumDifference, difference);
    }
    double meanDifference = (double) totalDifference / actual.length;
    assertTrue(maximumDifference <= 20,
      "Maximum JPEG difference was " + maximumDifference);
    assertTrue(meanDifference <= 2.0,
      "Mean JPEG difference was " + meanDifference);
  }

  private static byte[] planar(byte[] interleaved) {
    byte[] planar = new byte[interleaved.length];
    int pixels = interleaved.length / 3;
    for (int pixel = 0; pixel < pixels; pixel++) {
      for (int channel = 0; channel < 3; channel++) {
        planar[channel * pixels + pixel] =
          interleaved[pixel * 3 + channel];
      }
    }
    return planar;
  }

  private static byte[] writeAtQuality(double quality, long expectedTag)
    throws Exception
  {
    int width = 64;
    int height = 32;
    Path file = Files.createTempFile("bioformats-quality-", ".ndpi");
    try {
      CodecOptions options = CodecOptions.getDefaultOptions();
      options.quality = quality;
      NDPIWriter writer = new NDPIWriter();
      writer.setCodecOptions(options);
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0));
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        TiffParser parser = new TiffParser(in);
        IFD ifd = parser.getFirstIFD();
        parser.fillInIFD(ifd);
        assertEquals(ifd.getIFDLongValue(NDPITags.JPEG_QUALITY, -1),
          expectedTag);
        long offset = ifd.getStripOffsets()[0];
        long byteCount = ifd.getStripByteCounts()[0];
        assertTrue(byteCount <= Integer.MAX_VALUE);
        byte[] jpeg = new byte[(int) byteCount];
        in.seek(offset);
        in.readFully(jpeg);
        return jpeg;
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  private static void assertAuthCodes(Path file, int levels)
    throws Exception
  {
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      TiffParser parser = new TiffParser(in);
      for (IFD ifd : parser.getMainIFDs()) {
        long[] starts = ifd.getIFDLongArray(NDPITags.MCU_STARTS);
        long jpegOffset = ifd.getStripOffsets()[0];
        long expected = expectedAuthCode(in, jpegOffset, starts);
        assertEquals(ifd.getIFDLongValue(NDPITags.AUTH_CODE, -1) & 0xffffffffL,
          expected, "Incorrect independently calculated NDPI AuthCode");
      }
      assertEquals(parser.getMainIFDs().size(), levels);
    }
  }

  private static void assertMCUStarts(Path file) throws Exception {
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      TiffParser parser = new TiffParser(in);
      for (IFD ifd : parser.getMainIFDs()) {
        long jpegOffset = ifd.getStripOffsets()[0];
        long[] starts = ifd.getIFDLongArray(NDPITags.MCU_STARTS);
        assertTrue(starts.length > 0);
        in.order(false);
        in.seek(jpegOffset);
        assertEquals(in.readUnsignedShort(), 0xffd8);
        while (in.getFilePointer() - jpegOffset < starts[0]) {
          assertEquals(in.readUnsignedByte(), 0xff);
          int marker = in.readUnsignedByte();
          int length = in.readUnsignedShort();
          if (marker == 0xda) {
            assertEquals(in.getFilePointer() + length - 2 - jpegOffset,
              starts[0], "First MCU start must immediately follow SOS");
          }
          in.skipBytes(length - 2);
        }
        assertEquals(in.getFilePointer() - jpegOffset, starts[0],
          "First MCU start must immediately follow SOS");
        for (int i = 1; i < starts.length; i++) {
          in.seek(jpegOffset + starts[i] - 2);
          assertEquals(in.readUnsignedByte(), 0xff);
          int marker = in.readUnsignedByte();
          assertTrue(marker >= 0xd0 && marker <= 0xd7,
            "MCU start must immediately follow a restart marker");
        }
      }
    }
  }

  private static void assertRestartGeometry(Path file, int[] restartMCUs,
    int[] mcuStartCounts) throws Exception
  {
    try (RandomAccessInputStream in =
      new RandomAccessInputStream(file.toString()))
    {
      TiffParser parser = new TiffParser(in);
      java.util.List<IFD> ifds = parser.getMainIFDs();
      assertEquals(ifds.size(), restartMCUs.length);
      for (int i = 0; i < ifds.size(); i++) {
        IFD ifd = ifds.get(i);
        parser.fillInIFD(ifd);
        assertEquals(ifd.getIFDLongArray(NDPITags.MCU_STARTS).length,
          mcuStartCounts[i]);
        assertEquals(readRestartInterval(in, ifd.getStripOffsets()[0]),
          restartMCUs[i]);
      }
    }
  }

  private static int readRestartInterval(RandomAccessInputStream in,
    long jpegOffset) throws IOException
  {
    in.order(false);
    in.seek(jpegOffset);
    assertEquals(in.readUnsignedShort(), 0xffd8);
    while (true) {
      assertEquals(in.readUnsignedByte(), 0xff);
      int marker = in.readUnsignedByte();
      int length = in.readUnsignedShort();
      if (marker == 0xdd) {
        assertEquals(length, 4);
        return in.readUnsignedShort();
      }
      if (marker == 0xda) {
        fail("Indexed NDPI JPEG has no DRI marker");
      }
      in.skipBytes(length - 2);
    }
  }

  private static void assertOrdinaryJPEG(RandomAccessInputStream in, IFD ifd)
    throws Exception
  {
    long offset = ifd.getStripOffsets()[0];
    long length = ifd.getStripByteCounts()[0];
    assertTrue(length <= Integer.MAX_VALUE);
    byte[] jpeg = new byte[(int) length];
    in.seek(offset);
    in.readFully(jpeg);
    assertEquals(((jpeg[0] & 0xff) << 8) | (jpeg[1] & 0xff), 0xffd8);

    int position = 2;
    boolean scan = false;
    while (position + 1 < jpeg.length) {
      if ((jpeg[position] & 0xff) != 0xff) {
        assertTrue(scan, "JPEG data outside entropy scan");
        position++;
        continue;
      }
      int next = jpeg[position + 1] & 0xff;
      if (scan && next == 0) {
        position += 2;
        continue;
      }
      if (next == 0xd9) return;
      assertFalse(next == 0xdd, "Non-indexed JPEG must omit DRI");
      assertFalse(next >= 0xd0 && next <= 0xd7,
        "Non-indexed JPEG must omit restart markers");
      assertFalse(scan, "Unexpected marker in ordinary JPEG entropy");
      int markerLength =
        (jpeg[position + 2] & 0xff) << 8 | jpeg[position + 3] & 0xff;
      if (next == 0xda) scan = true;
      position += 2 + markerLength;
    }
    fail("Ordinary JPEG has no EOI marker");
  }

  private static void assertSetIdRejected(IMetadata metadata,
    boolean sequential, boolean interleaved, String message) throws Exception
  {
    Path file = Files.createTempFile("bioformats-rejected-", ".ndpi");
    Files.delete(file);
    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(interleaved);
      writer.setWriteSequentially(sequential);
      try {
        writer.setId(file.toString());
        fail("Unsupported NDPI writer contract must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(), message);
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  private static void assertOptionsRejected(DynamicMetadataOptions options,
    String detail) throws Exception
  {
    Path file = Files.createTempFile("bioformats-invalid-option-", ".ndpi");
    Files.delete(file);
    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata(64, 32));
      writer.setMetadataOptions(options);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      try {
        writer.setId(file.toString());
        fail("Invalid NDPI writer option must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(),
          "Invalid NDPI writer option: " + detail);
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  private static void assertMetadataOptionsRejected(IMetadata metadata,
    DynamicMetadataOptions options, String message) throws Exception
  {
    Path file = Files.createTempFile("bioformats-invalid-series-", ".ndpi");
    Files.delete(file);
    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata);
      writer.setMetadataOptions(options);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      try {
        writer.setId(file.toString());
        fail("Invalid NDPI associated-series contract must be rejected");
      }
      catch (FormatException e) {
        assertEquals(e.getMessage(), message);
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  private static long expectedAuthCode(RandomAccessInputStream in,
    long jpegOffset, long[] starts) throws Exception
  {
    int count = starts.length;
    int[] segments = {
      (int) (72L * count / 100), (int) (80L * count / 100),
      (int) (75L * count / 100), 42
    };
    int[] percentages = {0, 75, 80, 72};
    int[] masks = {0x1d, 0x6e, 0xdb, 0x2a};
    long authCode = 0;
    for (int i = 0; i < segments.length; i++) {
      int segment = segments[i];
      long entropyLength = starts[segment + 1] - starts[segment] - 2;
      assertTrue(entropyLength > 0, "Invalid restart segment length");
      long sample = i == 0 ? 42 : percentages[i] * entropyLength / 100;
      long sampleOffset = Math.min(entropyLength - 1, sample);
      in.seek(jpegOffset + starts[segment] + sampleOffset);
      authCode = (authCode << 8) |
        ((in.readUnsignedByte() ^ masks[i]) & 0xff);
    }
    return authCode;
  }

  private static int invokeAuthCode(byte[][] entropy, long[] starts)
    throws Exception
  {
    java.lang.reflect.Constructor<NDPIJPEGAssembler.Result> constructor =
      NDPIJPEGAssembler.Result.class.getDeclaredConstructor(
        long.class, long.class, long[].class, byte[][].class);
    constructor.setAccessible(true);
    NDPIJPEGAssembler.Result result =
      constructor.newInstance(0L, 0L, starts, entropy);
    java.lang.reflect.Method calculate = NDPIWriter.class.getDeclaredMethod(
      "calculateAuthCode", NDPIJPEGAssembler.Result.class, long[].class);
    calculate.setAccessible(true);
    return ((Integer) calculate.invoke(new NDPIWriter(), result, starts))
      .intValue();
  }

  private static long[] uniformMCUStarts(int count, int entropyLength) {
    long[] starts = new long[count];
    for (int i = 0; i < starts.length; i++) {
      starts[i] = (long) i * (entropyLength + 2);
    }
    return starts;
  }

  private static byte[][] authEntropy(int length) {
    byte[][] entropy = new byte[4][length];
    for (int segment = 0; segment < entropy.length; segment++) {
      for (int i = 0; i < length; i++) {
        entropy[segment][i] = (byte) (segment * 67 + i);
      }
    }
    return entropy;
  }

  private static byte[][] clone(byte[][] values) {
    byte[][] copy = new byte[values.length][];
    for (int i = 0; i < values.length; i++) copy[i] = values[i].clone();
    return copy;
  }

  private static void assertTag(RandomAccessInputStream in, long ifdOffset,
    int expectedTag, int expectedType, Integer expectedValue) throws Exception
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      int tag = in.readUnsignedShort();
      int type = in.readUnsignedShort();
      long valueCount = in.readInt() & 0xffffffffL;
      long value = in.readInt() & 0xffffffffL;
      if (tag == expectedTag) {
        assertEquals(type, expectedType, "Wrong TIFF type for tag " + tag);
        assertEquals(valueCount, 1L, "Wrong count for tag " + tag);
        if (expectedValue != null) {
          assertEquals(value, expectedValue.intValue() & 0xffffffffL,
            "Wrong value for tag " + tag);
        }
        return;
      }
    }
    assertTrue(false, "Missing TIFF tag " + expectedTag);
  }

  private static void assertFloatTag(RandomAccessInputStream in,
    long ifdOffset, int expectedTag, float expectedValue) throws Exception
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      int tag = in.readUnsignedShort();
      int type = in.readUnsignedShort();
      long valueCount = in.readInt() & 0xffffffffL;
      int bits = in.readInt();
      if (tag == expectedTag) {
        assertEquals(type, 11, "Wrong TIFF type for tag " + tag);
        assertEquals(valueCount, 1L, "Wrong count for tag " + tag);
        assertEquals(Float.intBitsToFloat(bits), expectedValue);
        return;
      }
    }
    assertTrue(false, "Missing TIFF tag " + expectedTag);
  }

  private static void assertTagCount(RandomAccessInputStream in,
    long ifdOffset, int expectedTag, int expectedType, long expectedCount)
    throws Exception
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      int tag = in.readUnsignedShort();
      int type = in.readUnsignedShort();
      long valueCount = in.readInt() & 0xffffffffL;
      in.skipBytes(4);
      if (tag == expectedTag) {
        assertEquals(type, expectedType, "Wrong TIFF type for tag " + tag);
        assertEquals(valueCount, expectedCount,
          "Wrong count for tag " + tag);
        return;
      }
    }
    assertTrue(false, "Missing TIFF tag " + expectedTag);
  }

  private static void assertAsciiTag(RandomAccessInputStream in,
    long ifdOffset, int expectedTag, String expectedValue) throws Exception
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      int tag = in.readUnsignedShort();
      int type = in.readUnsignedShort();
      long valueCount = in.readInt() & 0xffffffffL;
      long valueOffset = in.readInt() & 0xffffffffL;
      if (tag == expectedTag) {
        assertEquals(type, 2, "Wrong TIFF type for tag " + tag);
        assertEquals(valueCount, expectedValue.length() + 1L,
          "Wrong count for tag " + tag);
        assertEquals(valueOffset & 1, 0,
          "TIFF ASCII values must begin on word boundaries");
        assertTrue(valueOffset < ifdOffset,
          "NDPI ASCII values must be stored out-of-line, before their IFD");
        byte[] value = new byte[(int) valueCount - 1];
        in.seek(valueOffset);
        in.readFully(value);
        assertEquals(new String(value, StandardCharsets.US_ASCII),
          expectedValue);
        assertEquals(in.readUnsignedByte(), 0,
          "NDPI ASCII values must be NUL-terminated");
        return;
      }
    }
    assertTrue(false, "Missing TIFF tag " + expectedTag);
  }

  /**
   * Reads the offset of the first directory from the NDPI header. Native
   * NDPI output places each directory after the image it describes, so the
   * header offset is the only way to find the first one.
   */
  private static long firstIFD(RandomAccessInputStream in) throws IOException {
    in.order(true);
    in.seek(4);
    return in.readLong();
  }

  private static long nextIFDOffset(RandomAccessInputStream in,
    long ifdOffset) throws IOException
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    in.seek(ifdOffset + 2 + 12L * count);
    return in.readLong();
  }

  private static void assertIndexPresence(int width, int height,
    boolean expected) throws Exception
  {
    Path file = Files.createTempFile("bioformats-index-boundary-", ".ndpi");
    try {
      NDPIWriter writer = new NDPIWriter();
      writer.setMetadataRetrieve(metadata(width, height));
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(width, height, 0), 0, 0, width, height);
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        TiffParser parser = new TiffParser(in);
        IFD ifd = parser.getFirstIFD();
        parser.fillInIFD(ifd);
        assertEquals(ifd.containsKey(NDPITags.MCU_STARTS), expected);
        assertEquals(ifd.containsKey(NDPITags.AUTH_CODE), expected);
        if (expected) {
          assertEquals(ifd.getIFDLongArray(NDPITags.MCU_STARTS).length, 44);
        }
        else {
          assertOrdinaryJPEG(in, ifd);
        }
      }
    }
    finally {
      Files.deleteIfExists(file);
    }
  }

  /**
   * Writes a whole pyramid and checks which of its levels were indexed.
   *
   * @param metadata the pyramid to write
   * @param widths the width of each resolution
   * @param heights the height of each resolution
   * @param indexed whether each resolution is expected to carry a restart
   *   index and the AuthCode that authenticates it
   */
  private static void assertLevelIndexPresence(IMetadata metadata,
    int[] widths, int[] heights, boolean[] indexed) throws Exception
  {
    Path file = Files.createTempFile("bioformats-index-levels-", ".ndpi");
    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      for (int r = 0; r < widths.length; r++) {
        writer.setResolution(r);
        writer.saveBytes(0, pixels(widths[r], heights[r], r), 0, 0,
          widths[r], heights[r]);
      }
      writer.close();

      try (RandomAccessInputStream in =
        new RandomAccessInputStream(file.toString()))
      {
        long[] ifds = chain(in);
        assertEquals(ifds.length, widths.length,
          "Every resolution must be described");
        for (int r = 0; r < ifds.length; r++) {
          assertEquals(entry(in, ifds[r], NDPITags.MCU_STARTS) != null,
            indexed[r], "Wrong restart-index presence at resolution " + r);
          assertEquals(entry(in, ifds[r], NDPITags.AUTH_CODE) != null,
            indexed[r], "Wrong AuthCode presence at resolution " + r);
        }
      }
    }
    finally {
      writer.close();
      Files.deleteIfExists(file);
    }
  }

  /**
   * Checks that an indexed image's restart arrays lie between the end of its
   * payload and the beginning of its directory.
   */
  private static void assertRestartIndexPlacement(RandomAccessInputStream in,
    long ifdOffset, long payloadEnd) throws IOException
  {
    long[] low = entry(in, ifdOffset, NDPITags.MCU_STARTS);
    if (low == null) {
      assertTrue(entry(in, ifdOffset, NDPITags.MCU_STARTS_HIGH_BYTES) == null,
        "A non-indexed NDPI image must omit both restart arrays");
      return;
    }
    long lowEnd = low[2] + low[1] * 4;
    assertTrue(low[2] >= payloadEnd,
      "The restart index must follow the payload it indexes");
    long[] high = entry(in, ifdOffset, NDPITags.MCU_STARTS_HIGH_BYTES);
    if (high != null) {
      assertTrue(lowEnd <= high[2], "The high restart words must follow the " +
        "low words");
      lowEnd = high[2] + high[1] * 4;
    }
    assertTrue(lowEnd <= ifdOffset,
      "The restart index must precede the directory that names it");
  }

  /**
   * Writes a two-level pyramid, a macro and a tissue map into one NDPI file,
   * with every private option set, for the layout tests to inspect.
   */
  private static void writeNativeLayoutOutput(Path file) throws Exception {
    IMetadata metadata = associatedMetadata();
    IPyramidStore pyramid = (IPyramidStore) metadata;
    pyramid.setResolutionSizeX(new PositiveInteger(PYRAMID_WIDTH / 2), 0, 1);
    pyramid.setResolutionSizeY(new PositiveInteger(PYRAMID_HEIGHT / 2), 0, 1);
    DynamicMetadataOptions options = associatedOptions();
    options.set(NDPIWriter.REFERENCE_KEY, "A1");

    NDPIWriter writer = new NDPIWriter();
    try {
      writer.setMetadataRetrieve(metadata);
      writer.setMetadataOptions(options);
      writer.setInterleaved(true);
      writer.setWriteSequentially(true);
      writer.setId(file.toString());
      writer.saveBytes(0, pixels(PYRAMID_WIDTH, PYRAMID_HEIGHT, 0), 0, 0,
        PYRAMID_WIDTH, PYRAMID_HEIGHT);
      writer.setResolution(1);
      writer.saveBytes(0, pixels(PYRAMID_WIDTH / 2, PYRAMID_HEIGHT / 2, 1),
        0, 0, PYRAMID_WIDTH / 2, PYRAMID_HEIGHT / 2);
      writer.setSeries(1);
      writer.setResolution(0);
      writer.saveBytes(0, pixels(MACRO_WIDTH, MACRO_HEIGHT, 1), 0, 0,
        MACRO_WIDTH, MACRO_HEIGHT);
      writer.setSeries(2);
      writer.setResolution(0);
      writer.saveBytes(0, new byte[MAP_WIDTH * MAP_HEIGHT], 0, 0, MAP_WIDTH,
        MAP_HEIGHT);
    }
    finally {
      writer.close();
    }
  }

  private static long[] reconstruct(long[] low, long[] high) {
    long[] values = new long[low.length];
    for (int i = 0; i < values.length; i++) {
      values[i] = (low[i] & 0xffffffffL) | (high[i] << 32);
    }
    return values;
  }

  /**
   * Reads one directory entry and joins its value field to the NDPI
   * extension word that follows the directory.
   *
   * @return {type, count, value}, where the value is either the entry's own
   *   64-bit scalar or the offset of its external value, or null when the
   *   directory does not contain the tag
   */
  private static long[] entry(RandomAccessInputStream in, long ifdOffset,
    int expectedTag) throws IOException
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      in.seek(ifdOffset + 2 + 12L * i);
      int tag = in.readUnsignedShort();
      if (tag != expectedTag) continue;
      long type = in.readUnsignedShort();
      long valueCount = in.readInt() & 0xffffffffL;
      long low = in.readInt() & 0xffffffffL;
      in.seek(ifdOffset + 2 + 12L * count + 8 + 4L * i);
      long high = in.readInt() & 0xffffffffL;
      return new long[] {type, valueCount, low | (high << 32)};
    }
    return null;
  }

  /** Follows the whole directory chain from the NDPI header. */
  private static long[] chain(RandomAccessInputStream in) throws IOException {
    java.util.List<Long> offsets = new java.util.ArrayList<Long>();
    for (long offset = firstIFD(in); offset != 0;
      offset = nextIFDOffset(in, offset))
    {
      assertTrue(offsets.size() < 64, "The NDPI directory chain must end");
      offsets.add(Long.valueOf(offset));
    }
    long[] chain = new long[offsets.size()];
    for (int i = 0; i < chain.length; i++) chain[i] = offsets.get(i);
    return chain;
  }

  /** Locates the external value of one tag, or fails when it has none. */
  private static long externalOffset(RandomAccessInputStream in,
    long ifdOffset, int tag) throws IOException
  {
    long[] entry = entry(in, ifdOffset, tag);
    assertTrue(entry != null, "Missing TIFF tag " + tag);
    assertTrue(isExternal(entry), "TIFF tag " + tag + " is not stored " +
      "externally");
    return entry[2];
  }

  /** Locates every external value one directory names. */
  private static java.util.List<long[]> externalRegions(
    RandomAccessInputStream in, long ifdOffset) throws IOException
  {
    java.util.List<long[]> regions = new java.util.ArrayList<long[]>();
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      in.seek(ifdOffset + 2 + 12L * i);
      int tag = in.readUnsignedShort();
      long[] entry = entry(in, ifdOffset, tag);
      if (isExternal(entry)) {
        regions.add(new long[] {entry[2], entry[1] * typeBytes(entry[0])});
      }
    }
    return regions;
  }

  /**
   * Whether one entry's value is stored outside it. Every ASCII value is,
   * as genuine NDPI files store even short strings externally.
   */
  private static boolean isExternal(long[] entry) {
    return entry[0] == ASCII_TYPE || entry[1] * typeBytes(entry[0]) > 4;
  }

  private static long typeBytes(long type) {
    switch ((int) type) {
      case ASCII_TYPE: return 1;
      case 3: return 2;
      case 5: return 8;
      default: return 4;
    }
  }

  /** Extracts the low or high 32-bit words of 64-bit offsets. */
  private static long[] words(long[] values, int shift) {
    long[] words = new long[values.length];
    for (int i = 0; i < values.length; i++) {
      words[i] = (values[i] >>> shift) & 0xffffffffL;
    }
    return words;
  }

  private static long extendedScalar(RandomAccessInputStream in,
    long ifdOffset, int expectedTag) throws IOException
  {
    long[] entry = entry(in, ifdOffset, expectedTag);
    if (entry == null) {
      throw new IOException("Missing TIFF tag " + expectedTag);
    }
    return entry[2];
  }

  private static long extensionWord(RandomAccessInputStream in,
    long ifdOffset, int expectedTag) throws IOException
  {
    in.order(true);
    in.seek(ifdOffset);
    int count = in.readUnsignedShort();
    for (int i = 0; i < count; i++) {
      int tag = in.readUnsignedShort();
      in.skipBytes(10);
      if (tag == expectedTag) {
        long extension = ifdOffset + 2 + 12L * count + 8 + 4L * i;
        in.seek(extension);
        return in.readInt() & 0xffffffffL;
      }
    }
    throw new IOException("Missing TIFF tag " + expectedTag);
  }

  private static final class SparseNDPIWriter extends NDPIWriter {

    private void seekPayload(long offset) throws IOException {
      out.seek(offset);
    }
  }

  /**
   * An NDPI writer whose underlying output stream can be made to fail on
   * close, by swapping in a different FormatWriter.out stream.
   */
  private static final class FailingCloseNDPIWriter extends NDPIWriter {

    /**
     * Flushes and closes the real output, then substitutes a stream that
     * refuses to close.
     */
    private void failNextClose() throws IOException {
      out.close();
      out = new FailingCloseStream();
    }
  }

  /** An output stream that always fails to close. */
  private static final class FailingCloseStream
    extends RandomAccessOutputStream
  {

    private FailingCloseStream() {
      super(new ByteArrayHandle());
    }

    @Override
    public void close() throws IOException {
      throw new IOException(CLOSE_FAILURE);
    }
  }
}
