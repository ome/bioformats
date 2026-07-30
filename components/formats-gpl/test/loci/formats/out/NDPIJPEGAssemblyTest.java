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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests assembly of an NDPI-style restart-marked JPEG, and keeps an
 * independent reference implementation for decode-equivalence checks.
 */
public class NDPIJPEGAssemblyTest {

  private static final int CHANNELS = 3;
  private static final int MCU_SIZE = 8;
  private static final int RESTART_MCUS = 4;
  private static final int DRI = 0xffdd;
  private static final int EOI = 0xffd9;
  private static final int SOS = 0xffda;
  private static final int PREFIX_LENGTH = 37;

  /** One restart region wide and two MCU rows high. */
  private static final int SMALL_WIDTH = RESTART_MCUS * MCU_SIZE;
  private static final int SMALL_HEIGHT = 2 * MCU_SIZE;

  @DataProvider(name = "dimensions")
  public Object[][] dimensions() {
    return new Object[][] {
      {128, 80}, // four restart intervals per row and RST wraparound
      {125, 19}  // four intervals per row plus right and bottom padding
    };
  }

  @Test(dataProvider = "dimensions")
  public void testRestartMarkedAssembly(int width, int height)
    throws Exception
  {
    List<ParsedJPEG> pieces = new ArrayList<ParsedJPEG>();
    byte[] expected = new byte[width * height * CHANNELS];
    int regionWidth = RESTART_MCUS * MCU_SIZE;
    int regionIndex = 0;

    for (int y = 0; y < height; y += MCU_SIZE) {
      int pieceHeight = Math.min(MCU_SIZE, height - y);
      for (int x = 0; x < width; x += regionWidth) {
        int pieceWidth = Math.min(regionWidth, width - x);
        byte[] piecePixels = makeRegion(x, y, pieceWidth, pieceHeight,
          regionIndex++);
        CodecOptions options = options(pieceWidth, pieceHeight);
        byte[] compressed = new JPEGCodec().compress(piecePixels, options);
        ParsedJPEG piece = ParsedJPEG.parse(compressed);
        pieces.add(piece);
        copyRegion(new JPEGCodec().decompress(compressed, options), expected,
          x, y, pieceWidth, pieceHeight, width);
      }
    }

    assertTrue(pieces.size() > 1, "The test needs multiple regions");
    for (int i = 1; i < pieces.size(); i++) {
      pieces.get(0).assertCompatible(pieces.get(i));
    }
    pieces.get(0).assert444Sampling();

    byte[] assembled = assemble(pieces, width, height);
    ParsedJPEG parsed = ParsedJPEG.parse(assembled);
    assertEquals(parsed.restartInterval, RESTART_MCUS);
    assertEquals(parsed.restartMarkers.size(), pieces.size() - 1);
    for (int i = 0; i < parsed.restartMarkers.size(); i++) {
      assertEquals((int) parsed.restartMarkers.get(i), 0xffd0 + (i & 7),
        "Restart marker sequence differs at boundary " + i);
    }

    byte[] actual = new JPEGCodec().decompress(assembled,
      options(width, height));
    assertEquals(actual, expected,
      "Restart assembly must decode like the independent JPEG regions");
  }

  @Test(dataProvider = "dimensions")
  public void testAssemblerOutputStructure(int width, int height)
    throws Exception
  {
    byte[] pixels = new byte[width * height * CHANNELS];
    byte[] expected = new byte[pixels.length];
    int regionWidth = RESTART_MCUS * MCU_SIZE;
    int regionIndex = 0;

    for (int y = 0; y < height; y += MCU_SIZE) {
      int pieceHeight = Math.min(MCU_SIZE, height - y);
      for (int x = 0; x < width; x += regionWidth) {
        int pieceWidth = Math.min(regionWidth, width - x);
        byte[] piece = makeRegion(x, y, pieceWidth, pieceHeight,
          regionIndex++);
        copyRegion(piece, pixels, x, y, pieceWidth, pieceHeight, width);
        byte[] compressed = new JPEGCodec().compress(piece,
          options(pieceWidth, pieceHeight));
        copyRegion(new JPEGCodec().decompress(compressed,
          options(pieceWidth, pieceHeight)), expected, x, y, pieceWidth,
          pieceHeight, width);
      }
    }

    ByteArrayHandle handle = new ByteArrayHandle();
    NDPIJPEGAssembler.Result result;
    try (RandomAccessOutputStream out = new RandomAccessOutputStream(handle)) {
      out.write(new byte[PREFIX_LENGTH]);
      NDPIJPEGAssembler assembler =
        new NDPIJPEGAssembler(out, width, height, RESTART_MCUS, true, 0.8,
          null);
      int row = 0;
      while (row < height) {
        int rows = Math.min(row % 2 == 0 ? 3 : 7, height - row);
        assembler.writeRows(pixels, row * width * CHANNELS, rows);
        row += rows;
      }
      result = assembler.finish();
    }

    assertEquals(result.getJPEGOffset(), PREFIX_LENGTH);
    assertEquals(result.getMCUStarts().length,
      ((width + regionWidth - 1) / regionWidth) *
      ((height + MCU_SIZE - 1) / MCU_SIZE));
    assertEquals(handle.length() - PREFIX_LENGTH, result.getJPEGLength());
    byte[] assembled = Arrays.copyOfRange(handle.getBytes(), PREFIX_LENGTH,
      PREFIX_LENGTH + (int) result.getJPEGLength());
    ParsedJPEG parsed = ParsedJPEG.parse(assembled);
    assertEquals(parsed.restartInterval, RESTART_MCUS);
    assertEquals(parsed.restartMarkers.size(),
      result.getMCUStarts().length - 1);
    for (int i = 0; i < result.getMCUStarts().length; i++) {
      long start = result.getMCUStarts()[i];
      if (i == 0) {
        assertEquals(start, (long) parsed.header.length);
      }
      else {
        assertEquals(ParsedJPEG.marker(assembled, (int) start - 2),
          0xffd0 + ((i - 1) & 7));
      }
    }
    assertEquals(new JPEGCodec().decompress(assembled, options(width, height)),
      expected);
  }

  @Test
  public void testOversizedSOFDimensionsBecomeZero() {
    byte[] header = new byte[16];

    patchDimensions(header, 0, 0x10000, 123);
    assertEquals(u16(header, 5), 123);
    assertEquals(u16(header, 7), 0);

    patchDimensions(header, 0, 456, 0x10000);
    assertEquals(u16(header, 5), 0);
    assertEquals(u16(header, 7), 456);
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp =
      "Padded JPEG width must contain a whole number of restart intervals")
  public void testRejectsPartialRestartIntervalAtRowBoundary()
    throws Exception
  {
    ByteArrayHandle handle = new ByteArrayHandle();
    try (RandomAccessOutputStream out = new RandomAccessOutputStream(handle)) {
      new NDPIJPEGAssembler(out, 65, 8, RESTART_MCUS, true, 0.8, null);
    }
  }

  // -- Constructor validation tests --

  @Test(expectedExceptions = IllegalArgumentException.class,
    expectedExceptionsMessageRegExp = "Destination stream cannot be null")
  public void testRejectsNullDestinationStream() throws Exception {
    new NDPIJPEGAssembler(null, SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS, true,
      0.8, null);
  }

  @Test
  public void testRejectsNonPositiveWidth() throws Exception {
    String message = "JPEG dimensions must be positive";
    assertConstructorRejects(0, SMALL_HEIGHT, RESTART_MCUS, true, 0.8, null,
      message);
    assertConstructorRejects(-SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS, true,
      0.8, null, message);
  }

  @Test
  public void testRejectsNonPositiveHeight() throws Exception {
    String message = "JPEG dimensions must be positive";
    assertConstructorRejects(SMALL_WIDTH, 0, RESTART_MCUS, true, 0.8, null,
      message);
    assertConstructorRejects(SMALL_WIDTH, -SMALL_HEIGHT, RESTART_MCUS, true,
      0.8, null, message);
  }

  @Test
  public void testRejectsRestartIntervalOutOfRange() throws Exception {
    String message = "JPEG restart interval must be 1..65535 MCUs";
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, 0, true, 0.8, null,
      message);
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, -1, true, 0.8, null,
      message);
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, 0x10000, true, 0.8,
      null, message);
  }

  @Test
  public void testRejectsQualityOutOfRange() throws Exception {
    String message = "JPEG quality must be between 0.25 and 1";
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS, true,
      0.2499, null, message);
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS, true,
      1.0001, null, message);
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS, true,
      Double.NaN, null, message);
    assertConstructorRejects(SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS, true,
      Double.POSITIVE_INFINITY, null, message);
  }

  /** The guard runs before allocation, so the huge width costs no memory. */
  @Test
  public void testRejectsOversizedMCURowBuffer() throws Exception {
    assertConstructorRejects(100000000, MCU_SIZE, 1, true, 0.8, null,
      "One JPEG MCU row is too large to buffer");
  }

  /** Also guarded before allocation; this mode has no MCU row buffer. */
  @Test
  public void testRejectsOversizedNonIndexedImageBuffer() throws Exception {
    assertConstructorRejects(100000, 10000, 0, false, 0.8, null,
      "Non-indexed JPEG is too large to buffer as one image");
  }

  /** Nine restart columns need only a 1728-byte MCU row buffer. */
  @Test
  public void testRejectsTooManyRestartRegions() throws Exception {
    assertConstructorRejects(9 * MCU_SIZE, Integer.MAX_VALUE, 1, true, 0.8,
      null, "JPEG contains too many restart regions");
  }

  @Test
  public void testRejectsCaptureRegionsWhenNotIndexed() throws Exception {
    assertConstructorRejects(MCU_SIZE, MCU_SIZE, 0, false, 0.8, new int[] {0},
      "Non-indexed JPEG cannot capture restart regions");
  }

  @Test
  public void testRejectsCaptureRegionOutOfRange() throws Exception {
    String message = "Captured JPEG region is out of range";
    assertConstructorRejects(SMALL_WIDTH, MCU_SIZE, RESTART_MCUS, true, 0.8,
      new int[] {-1}, message);
    assertConstructorRejects(SMALL_WIDTH, MCU_SIZE, RESTART_MCUS, true, 0.8,
      new int[] {1}, message);
  }

  // -- writeRows and finish validation tests --

  @Test(expectedExceptions = IllegalArgumentException.class,
    expectedExceptionsMessageRegExp = "Pixel buffer cannot be null")
  public void testWriteRowsRejectsNullPixelBuffer() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      newAssembler(out).writeRows(null, 0, 1);
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp =
      "Rows must be written once, top to bottom")
  public void testWriteRowsRejectsNonPositiveRowCount() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      newAssembler(out).writeRows(new byte[smallPixelCount()], 0, 0);
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp =
      "Rows must be written once, top to bottom")
  public void testWriteRowsRejectsMoreRowsThanRemain() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      NDPIJPEGAssembler assembler = newAssembler(out);
      byte[] pixels = new byte[smallPixelCount()];
      assembler.writeRows(pixels, 0, SMALL_HEIGHT - 1);
      assembler.writeRows(pixels, 0, 2);
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp =
      "Pixel buffer does not contain 2 complete RGB rows")
  public void testWriteRowsRejectsShortPixelBuffer() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      newAssembler(out).writeRows(new byte[SMALL_WIDTH * CHANNELS], 0, 2);
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp =
      "Pixel buffer does not contain 1 complete RGB rows")
  public void testWriteRowsRejectsNegativeOffset() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      newAssembler(out).writeRows(new byte[smallPixelCount()], -1, 1);
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp = "Expected 16 rows but received 8")
  public void testFinishRejectsMissingRows() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      NDPIJPEGAssembler assembler = newAssembler(out);
      assembler.writeRows(new byte[smallPixelCount()], 0, MCU_SIZE);
      assembler.finish();
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp = "JPEG assembly is already complete")
  public void testWriteRowsAfterFinishIsRejected() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      byte[] pixels = new byte[smallPixelCount()];
      NDPIJPEGAssembler assembler = finishedAssembler(out, pixels);
      assembler.writeRows(pixels, 0, 1);
    }
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp = "JPEG assembly is already complete")
  public void testFinishAfterFinishIsRejected() throws Exception {
    try (RandomAccessOutputStream out = newStream()) {
      finishedAssembler(out, new byte[smallPixelCount()]).finish();
    }
  }

  // -- JPEG parser rejection tests --

  @Test
  public void testParseRejectsMissingSOI() {
    String message = "JPEG piece does not begin with SOI";
    assertParseRejects(bytes(0xff), message);
    assertParseRejects(bytes(0xff, 0xd8, 0x00), message);
    assertParseRejects(bytes(0x00, 0xd8, 0xff, 0xd9), message);
  }

  @Test
  public void testParseRejectsNonMarkerByte() {
    assertParseRejects(concat(soi(), bytes(0x00, 0x11, 0x22, 0x33)),
      "Expected JPEG marker at 2");
  }

  @Test
  public void testParseRejectsTruncatedMarker() {
    assertParseRejects(concat(soi(), bytes(0xff, 0xdb, 0x00)),
      "Truncated JPEG marker");
  }

  @Test
  public void testParseRejectsInvalidMarkerLength() {
    String message = "Invalid JPEG marker length";
    assertParseRejects(concat(soi(), bytes(0xff, 0xdb, 0x00, 0x01, 0x00,
      0x00)), message);
    assertParseRejects(concat(soi(), bytes(0xff, 0xdb, 0x00, 0x40, 0x00,
      0x00)), message);
  }

  @Test
  public void testParseRejectsNonBaselineSOF() {
    assertParseRejects(concat(soi(), bytes(0xff, 0xc2, 0x00, 0x04, 0x00,
      0x00), eoi()), "Only baseline sequential JPEG is supported, " +
      "found SOF marker 0xffc2");
  }

  @Test
  public void testParseRejectsMissingFrameAndScan() {
    assertParseRejects(concat(soi(), dqt(0x00, 0x10), eoi()),
      "Incomplete JPEG marker structure");
  }

  @Test
  public void testParseRejectsTruncatedFillBytes() {
    assertParseRejects(jpegWithEntropy(bytes(0x11, 0xff, 0xff)),
      "Truncated JPEG fill bytes");
  }

  @Test
  public void testParseRejectsMultipleScans() {
    assertParseRejects(jpegWithEntropy(bytes(0x11, 0xff, 0xda, 0x00, 0x00)),
      "Multiple JPEG scans are not supported");
  }

  @Test
  public void testParseRejectsFillBeforeStuffedByte() {
    assertParseRejects(jpegWithEntropy(bytes(0xff, 0xff, 0x00, 0x11)),
      "Invalid fill before stuffed entropy byte");
  }

  @Test
  public void testParseRejectsUnexpectedEntropyMarker() {
    assertParseRejects(jpegWithEntropy(bytes(0x11, 0xff, 0xc4, 0x00)),
      "Unexpected marker in JPEG entropy data");
  }

  @Test
  public void testParseRejectsEntropyWithoutEOI() {
    assertParseRejects(jpegWithEntropy(bytes(0x11, 0x22, 0x33)),
      "JPEG entropy data has no EOI");
  }

  /** Byte stuffing, restart markers, and 0xff fill bytes are skipped. */
  @Test
  public void testParseSkipsStuffingFillAndRestartMarkers() throws Exception {
    NDPIJPEGAssembler.ParsedJPEG parsed = parsed(jpegWithEntropy(bytes(0x11,
      0xff, 0xd0, 0x22, 0xff, 0x00, 0x33, 0xff, 0xff, 0xd9)));
    parsed.assert444Sampling();
    parsed.assertCompatible(parsed);
  }

  @Test
  public void testAssertCompatibleRejectsDifferingQuantizationTables()
    throws Exception
  {
    String message = "JPEG quantization tables differ between restart regions";
    assertIncompatible(minimalJPEG(), jpeg(dqt(0x00, 0x11), dht(0x00, 0x20),
      sof0(0x11, 0x11, 0x11), sos(0x00), entropy()), message);
    assertIncompatible(minimalJPEG(), jpeg(concat(dqt(0x00, 0x10),
      dqt(0x01, 0x10)), dht(0x00, 0x20), sof0(0x11, 0x11, 0x11), sos(0x00),
      entropy()), message);
  }

  @Test
  public void testAssertCompatibleRejectsDifferingHuffmanTables()
    throws Exception
  {
    assertIncompatible(minimalJPEG(), jpeg(dqt(0x00, 0x10), dht(0x00, 0x21),
      sof0(0x11, 0x11, 0x11), sos(0x00), entropy()),
      "JPEG Huffman tables differ between restart regions");
  }

  @Test
  public void testAssertCompatibleRejectsDifferingFrameComponents()
    throws Exception
  {
    assertIncompatible(minimalJPEG(), jpeg(dqt(0x00, 0x10), dht(0x00, 0x20),
      sof0(0x11, 0x11, 0x22), sos(0x00), entropy()),
      "JPEG frame components or sampling factors differ between restart " +
      "regions");
  }

  @Test
  public void testAssertCompatibleRejectsDifferingScanComponents()
    throws Exception
  {
    assertIncompatible(minimalJPEG(), jpeg(dqt(0x00, 0x10), dht(0x00, 0x20),
      sof0(0x11, 0x11, 0x11), sos(0x10), entropy()),
      "JPEG scan components differ between restart regions");
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp = "NDPI JPEG requires three components")
  public void testAssert444SamplingRejectsWrongComponentCount()
    throws Exception
  {
    parsed(jpeg(dqt(0x00, 0x10), dht(0x00, 0x20), sofOneComponent(),
      sos(0x00), entropy())).assert444Sampling();
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp = "NDPI JPEG requires 4:4:4 sampling")
  public void testAssert444SamplingRejectsSubsampledComponent()
    throws Exception
  {
    parsed(jpeg(dqt(0x00, 0x10), dht(0x00, 0x20), sof0(0x11, 0x21, 0x11),
      sos(0x00), entropy())).assert444Sampling();
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp = "Non-indexed JPEG must not contain DRI")
  public void testAssertNoRestartMarkersRejectsDRI() throws Exception {
    parsed(concat(soi(), dqt(0x00, 0x10), dht(0x00, 0x20), dri(),
      sof0(0x11, 0x11, 0x11), sos(0x00), entropy()))
      .assertNoRestartMarkers();
  }

  @Test(expectedExceptions = FormatException.class,
    expectedExceptionsMessageRegExp =
      "Non-indexed JPEG must not contain restart markers")
  public void testAssertNoRestartMarkersRejectsRestartMarker()
    throws Exception
  {
    parsed(jpegWithEntropy(bytes(0x11, 0xff, 0xd0, 0x22, 0xff, 0xd9)))
      .assertNoRestartMarkers();
  }

  // -- Validation test helpers --

  private static RandomAccessOutputStream newStream() {
    return new RandomAccessOutputStream(new ByteArrayHandle());
  }

  private static int smallPixelCount() {
    return SMALL_WIDTH * SMALL_HEIGHT * CHANNELS;
  }

  private static NDPIJPEGAssembler newAssembler(RandomAccessOutputStream out)
    throws FormatException
  {
    return new NDPIJPEGAssembler(out, SMALL_WIDTH, SMALL_HEIGHT, RESTART_MCUS,
      true, 0.8, null);
  }

  private static NDPIJPEGAssembler finishedAssembler(
    RandomAccessOutputStream out, byte[] pixels) throws Exception
  {
    NDPIJPEGAssembler assembler = newAssembler(out);
    assembler.writeRows(pixels, 0, SMALL_HEIGHT);
    assembler.finish();
    return assembler;
  }

  private static void assertConstructorRejects(int width, int height,
    int restartMCUs, boolean indexed, double quality, int[] captureRegions,
    String message) throws Exception
  {
    String description = "width=" + width + " height=" + height +
      " restartMCUs=" + restartMCUs + " indexed=" + indexed + " quality=" +
      quality + " captureRegions=" + Arrays.toString(captureRegions);
    try (RandomAccessOutputStream out = newStream()) {
      new NDPIJPEGAssembler(out, width, height, restartMCUs, indexed, quality,
        captureRegions);
      fail("Expected rejection of " + description);
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(), message,
        "Wrong rejection for " + description);
    }
  }

  private static NDPIJPEGAssembler.ParsedJPEG parsed(byte[] jpeg)
    throws FormatException
  {
    return NDPIJPEGAssembler.ParsedJPEG.parse(jpeg);
  }

  private static void assertParseRejects(byte[] jpeg, String message) {
    try {
      NDPIJPEGAssembler.ParsedJPEG.parse(jpeg);
      fail("Expected rejection: " + message);
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(), message);
    }
  }

  private static void assertIncompatible(byte[] first, byte[] second,
    String message) throws FormatException
  {
    try {
      parsed(first).assertCompatible(parsed(second));
      fail("Expected rejection: " + message);
    }
    catch (FormatException e) {
      assertEquals(e.getMessage(), message);
    }
  }

  // -- Handcrafted JPEG structure helpers --

  private static byte[] bytes(int... values) {
    byte[] result = new byte[values.length];
    for (int i = 0; i < values.length; i++) {
      result[i] = (byte) values[i];
    }
    return result;
  }

  private static byte[] concat(byte[]... parts) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (byte[] part : parts) {
      out.write(part, 0, part.length);
    }
    return out.toByteArray();
  }

  private static byte[] soi() {
    return bytes(0xff, 0xd8);
  }

  private static byte[] eoi() {
    return bytes(0xff, 0xd9);
  }

  /** A two-byte quantization table segment. */
  private static byte[] dqt(int first, int second) {
    return bytes(0xff, 0xdb, 0x00, 0x04, first, second);
  }

  /** A two-byte Huffman table segment. */
  private static byte[] dht(int first, int second) {
    return bytes(0xff, 0xc4, 0x00, 0x04, first, second);
  }

  private static byte[] dri() {
    return bytes(0xff, 0xdd, 0x00, 0x04, 0x00, 0x04);
  }

  /** A baseline SOF0 for three 8-by-8 components with the given sampling. */
  private static byte[] sof0(int first, int second, int third) {
    return bytes(0xff, 0xc0, 0x00, 0x11, 0x08, 0x00, 0x08, 0x00, 0x08, 0x03,
      0x01, first, 0x00, 0x02, second, 0x01, 0x03, third, 0x01);
  }

  private static byte[] sofOneComponent() {
    return bytes(0xff, 0xc0, 0x00, 0x0b, 0x08, 0x00, 0x08, 0x00, 0x08, 0x01,
      0x01, 0x11, 0x00);
  }

  /** A three-component scan header with a variable first table selector. */
  private static byte[] sos(int firstTables) {
    return bytes(0xff, 0xda, 0x00, 0x0c, 0x03, 0x01, firstTables, 0x02, 0x11,
      0x03, 0x11, 0x00, 0x3f, 0x00);
  }

  /** Entropy data containing one stuffed 0xff byte and a closing EOI. */
  private static byte[] entropy() {
    return bytes(0x11, 0x22, 0xff, 0x00, 0x33, 0xff, 0xd9);
  }

  private static byte[] jpeg(byte[] quantization, byte[] huffman, byte[] frame,
    byte[] scan, byte[] entropy)
  {
    return concat(soi(), quantization, huffman, frame, scan, entropy);
  }

  private static byte[] minimalJPEG() {
    return jpeg(dqt(0x00, 0x10), dht(0x00, 0x20), sof0(0x11, 0x11, 0x11),
      sos(0x00), entropy());
  }

  private static byte[] jpegWithEntropy(byte[] entropy) {
    return jpeg(dqt(0x00, 0x10), dht(0x00, 0x20), sof0(0x11, 0x11, 0x11),
      sos(0x00), entropy);
  }

  private static byte[] assemble(List<ParsedJPEG> pieces, int width,
    int height)
  {
    ParsedJPEG first = pieces.get(0);
    byte[] header = Arrays.copyOf(first.header, first.header.length);
    patchDimensions(header, first.sofOffset, width, height);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(header, 0, first.sosOffset);
    writeMarker(out, DRI);
    out.write(0);
    out.write(4);
    out.write(RESTART_MCUS >>> 8);
    out.write(RESTART_MCUS);
    out.write(header, first.sosOffset, header.length - first.sosOffset);

    for (int i = 0; i < pieces.size(); i++) {
      byte[] entropy = pieces.get(i).entropy;
      out.write(entropy, 0, entropy.length);
      if (i + 1 < pieces.size()) {
        writeMarker(out, 0xffd0 + (i & 7));
      }
    }
    writeMarker(out, EOI);
    return out.toByteArray();
  }

  private static void patchDimensions(byte[] header, int sofOffset, int width,
    int height)
  {
    int jpegWidth = width > 0xffff ? 0 : width;
    int jpegHeight = height > 0xffff ? 0 : height;
    header[sofOffset + 5] = (byte) (jpegHeight >>> 8);
    header[sofOffset + 6] = (byte) jpegHeight;
    header[sofOffset + 7] = (byte) (jpegWidth >>> 8);
    header[sofOffset + 8] = (byte) jpegWidth;
  }

  private static void writeMarker(ByteArrayOutputStream out, int marker) {
    out.write(marker >>> 8);
    out.write(marker);
  }

  private static CodecOptions options(int width, int height) {
    CodecOptions options = CodecOptions.getDefaultOptions();
    options.width = width;
    options.height = height;
    options.channels = CHANNELS;
    options.bitsPerSample = 8;
    options.interleaved = true;
    options.lossless = false;
    options.quality = 0.8;
    options.disableChromaSubsampling = true;
    return options;
  }

  private static byte[] makeRegion(int imageX, int imageY, int width,
    int height, int regionIndex)
  {
    byte[] pixels = new byte[width * height * CHANNELS];
    int content = regionIndex & 3;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int offset = (y * width + x) * CHANNELS;
        int globalX = imageX + x;
        int globalY = imageY + y;
        if (content == 0) {
          // Uniform region.
          pixels[offset] = 24;
          pixels[offset + 1] = (byte) 160;
          pixels[offset + 2] = 72;
        }
        else if (content == 1) {
          // Deterministic high-frequency noise.
          int noise = mix(globalX, globalY, regionIndex);
          pixels[offset] = (byte) noise;
          pixels[offset + 1] = (byte) (noise >>> 8);
          pixels[offset + 2] = (byte) (noise >>> 16);
        }
        else if (content == 2) {
          // High-contrast edges.
          int value = ((globalX / 4 + globalY / 4) & 1) == 0 ? 0 : 255;
          pixels[offset] = (byte) value;
          pixels[offset + 1] = (byte) (255 - value);
          pixels[offset + 2] = (byte) value;
        }
        else {
          // Smooth but colorful gradient.
          pixels[offset] = (byte) (globalX * 7 + globalY * 3);
          pixels[offset + 1] = (byte) (globalX * 2 + globalY * 11);
          pixels[offset + 2] = (byte) (globalX * 13 + globalY * 5);
        }
      }
    }
    return pixels;
  }

  private static int mix(int x, int y, int region) {
    int value = x * 0x1f123bb5 ^ y * 0x5f356495 ^ region * 0x6c8e9cf5;
    value ^= value >>> 16;
    value *= 0x7feb352d;
    value ^= value >>> 15;
    return value;
  }

  private static void copyRegion(byte[] region, byte[] image, int x, int y,
    int width, int height, int imageWidth)
  {
    int rowBytes = width * CHANNELS;
    for (int row = 0; row < height; row++) {
      System.arraycopy(region, row * rowBytes, image,
        ((y + row) * imageWidth + x) * CHANNELS, rowBytes);
    }
  }

  private static int u16(byte[] bytes, int offset) {
    return (bytes[offset] & 0xff) << 8 | bytes[offset + 1] & 0xff;
  }

  private static final class ParsedJPEG {

    private final byte[] header;
    private final byte[] entropy;
    private final List<byte[]> quantizationTables;
    private final List<byte[]> huffmanTables;
    private final byte[] frameComponents;
    private final byte[] scanComponents;
    private final int sofOffset;
    private final int sosOffset;
    private final List<Integer> restartMarkers;
    private final int restartInterval;

    private ParsedJPEG(byte[] header, byte[] entropy,
      List<byte[]> quantizationTables, List<byte[]> huffmanTables,
      byte[] frameComponents, byte[] scanComponents, int sofOffset,
      int sosOffset, List<Integer> restartMarkers, int restartInterval)
    {
      this.header = header;
      this.entropy = entropy;
      this.quantizationTables = quantizationTables;
      this.huffmanTables = huffmanTables;
      this.frameComponents = frameComponents;
      this.scanComponents = scanComponents;
      this.sofOffset = sofOffset;
      this.sosOffset = sosOffset;
      this.restartMarkers = restartMarkers;
      this.restartInterval = restartInterval;
    }

    private static ParsedJPEG parse(byte[] jpeg) throws FormatException {
      if (jpeg.length < 4 || marker(jpeg, 0) != 0xffd8) {
        throw new FormatException("JPEG does not begin with SOI");
      }

      List<byte[]> dqt = new ArrayList<byte[]>();
      List<byte[]> dht = new ArrayList<byte[]>();
      byte[] frame = null;
      byte[] scan = null;
      int sof = -1;
      int sos = -1;
      int entropyStart = -1;
      int entropyEnd = -1;
      int restartInterval = 0;
      int offset = 2;

      while (offset + 1 < jpeg.length) {
        if ((jpeg[offset] & 0xff) != 0xff) {
          throw new FormatException("Expected JPEG marker at " + offset);
        }
        int code = marker(jpeg, offset);
        if (code == EOI) {
          entropyEnd = offset;
          break;
        }
        if (code >= 0xffd0 && code <= 0xffd7) {
          offset += 2;
          continue;
        }
        if (offset + 3 >= jpeg.length) {
          throw new FormatException("Truncated JPEG marker");
        }
        int length = u16(jpeg, offset + 2);
        if (length < 2 || offset + 2 + length > jpeg.length) {
          throw new FormatException("Invalid JPEG marker length");
        }
        if (code == 0xffdb) {
          dqt.add(slice(jpeg, offset + 4, offset + 2 + length));
        }
        else if (code == 0xffc4) {
          dht.add(slice(jpeg, offset + 4, offset + 2 + length));
        }
        else if (code == DRI) {
          if (length != 4) {
            throw new FormatException("Invalid DRI marker length");
          }
          restartInterval = u16(jpeg, offset + 4);
        }
        else if (code == 0xffc0) {
          sof = offset;
          int components = jpeg[offset + 9] & 0xff;
          frame = slice(jpeg, offset + 9, offset + 10 + 3 * components);
        }
        else if (isSOF(code)) {
          throw new FormatException("Only baseline sequential JPEG is " +
            "supported, found SOF marker 0x" + Integer.toHexString(code));
        }
        else if (code == SOS) {
          sos = offset;
          int components = jpeg[offset + 4] & 0xff;
          scan = slice(jpeg, offset + 4, offset + 5 + 2 * components);
          entropyStart = offset + 2 + length;
          entropyEnd = findEntropyEnd(jpeg, entropyStart);
          break;
        }
        offset += 2 + length;
      }

      if (sof < 0 || sos < 0 || entropyStart < 0 || entropyEnd < 0) {
        throw new FormatException("Incomplete JPEG marker structure");
      }
      return new ParsedJPEG(slice(jpeg, 0, entropyStart),
        slice(jpeg, entropyStart, entropyEnd), dqt, dht, frame, scan, sof, sos,
        restartMarkers(jpeg, entropyStart, entropyEnd), restartInterval);
    }

    private void assertCompatible(ParsedJPEG other) {
      assertByteListsEqual(other.quantizationTables, quantizationTables,
        "Quantization tables differ between pieces");
      assertByteListsEqual(other.huffmanTables, huffmanTables,
        "Huffman tables differ between pieces");
      assertEquals(other.frameComponents, frameComponents,
        "SOF component order or sampling differs between pieces");
      assertEquals(other.scanComponents, scanComponents,
        "SOS component order differs between pieces");
    }

    private void assert444Sampling() {
      assertEquals(frameComponents[0] & 0xff, CHANNELS);
      for (int i = 0; i < CHANNELS; i++) {
        assertEquals(frameComponents[2 + 3 * i] & 0xff, 0x11,
          "Every component must use 1x1 (4:4:4) sampling");
      }
    }

    private static int findEntropyEnd(byte[] jpeg, int start)
      throws FormatException
    {
      int offset = start;
      while (offset + 1 < jpeg.length) {
        if ((jpeg[offset] & 0xff) != 0xff) {
          offset++;
          continue;
        }
        int next = jpeg[offset + 1] & 0xff;
        if (next == 0) {
          offset += 2;
          continue;
        }
        int markerOffset = offset;
        while (next == 0xff) {
          offset++;
          if (offset + 1 >= jpeg.length) {
            throw new FormatException("Truncated JPEG fill bytes");
          }
          next = jpeg[offset + 1] & 0xff;
        }
        if (next == 0xd9) {
          // Exclude all fill bytes preceding EOI from the entropy segment.
          return markerOffset;
        }
        if (next == 0xda) {
          throw new FormatException("Multiple JPEG scans are not supported");
        }
        if (next == 0) {
          throw new FormatException("Invalid fill before stuffed entropy byte");
        }
        if (next >= 0xd0 && next <= 0xd7) {
          offset += 2;
          continue;
        }
        throw new FormatException("Unexpected marker in JPEG entropy data");
      }
      throw new FormatException("JPEG entropy data has no EOI");
    }

    private static boolean isSOF(int code) {
      int low = code & 0xff;
      return code >= 0xffc0 && code <= 0xffcf &&
        low != 0xc4 && low != 0xc8 && low != 0xcc;
    }

    private static List<Integer> restartMarkers(byte[] jpeg, int start,
      int end)
    {
      List<Integer> markers = new ArrayList<Integer>();
      int offset = start;
      while (offset + 1 < end) {
        if ((jpeg[offset] & 0xff) != 0xff) {
          offset++;
          continue;
        }
        int next = jpeg[offset + 1] & 0xff;
        if (next == 0) {
          offset += 2;
          continue;
        }
        while (next == 0xff) {
          offset++;
          if (offset + 1 >= end) {
            break;
          }
          next = jpeg[offset + 1] & 0xff;
        }
        if (next >= 0xd0 && next <= 0xd7) {
          markers.add(0xff00 | next);
          offset += 2;
          continue;
        }
        offset++;
      }
      return markers;
    }

    private static void assertByteListsEqual(List<byte[]> actual,
      List<byte[]> expected, String message)
    {
      assertEquals(actual.size(), expected.size(), message);
      for (int i = 0; i < actual.size(); i++) {
        assertEquals(actual.get(i), expected.get(i), message);
      }
    }

    private static byte[] slice(byte[] bytes, int start, int end) {
      return Arrays.copyOfRange(bytes, start, end);
    }

    private static int marker(byte[] bytes, int offset) {
      return (bytes[offset] & 0xff) << 8 | bytes[offset + 1] & 0xff;
    }

  }
}
