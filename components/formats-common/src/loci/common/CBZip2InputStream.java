/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This package is based on the work done by Keiron Liddle, Aftex Software
 * <keiron@aftexsw.com> to whom the Ant project is very grateful for his
 * great code.
 */

package loci.common;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An input stream that decompresses from the BZip2 format (without the file
 * header chars) to be read as any other stream.
 *
 * <p>The decompression requires large amounts of memory.  Thus you
 * should call the {@link #close() close()} method as soon as
 * possible, to force <tt>CBZip2InputStream</tt> to release the
 * allocated memory.  See <tt>CBZip2OutputStream</tt>
 * for information about memory usage.</p>
 *
 * <p><tt>CBZip2InputStream</tt> reads bytes from the compressed
 * source stream via the single byte {@link java.io.InputStream#read() read()}
 * method exclusively.  Thus you should consider using a buffered source stream.
 * </p>
 *
 * <p>Instances of this class are not threadsafe.</p>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/CBZip2InputStream.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/CBZip2InputStream.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CBZip2InputStream extends InputStream {

  // -- Constants --

  private static final int BASE_BLOCK_SIZE = 100000;
  private static final int MAX_ALPHA_SIZE = 258;
  private static final int MAX_CODE_LEN = 23;
  private static final int RUNA = 0;
  private static final int RUNB = 1;
  private static final int N_GROUPS = 6;
  private static final int G_SIZE = 50;
  private static final int MAX_SELECTORS = (2 + (900000 / G_SIZE));

  private static final int[] R_NUMS = {
    619, 720, 127, 481, 931, 816, 813, 233, 566, 247,
    985, 724, 205, 454, 863, 491, 741, 242, 949, 214,
    733, 859, 335, 708, 621, 574, 73, 654, 730, 472,
    419, 436, 278, 496, 867, 210, 399, 680, 480, 51,
    878, 465, 811, 169, 869, 675, 611, 697, 867, 561,
    862, 687, 507, 283, 482, 129, 807, 591, 733, 623,
    150, 238, 59, 379, 684, 877, 625, 169, 643, 105,
    170, 607, 520, 932, 727, 476, 693, 425, 174, 647,
    73, 122, 335, 530, 442, 853, 695, 249, 445, 515,
    909, 545, 703, 919, 874, 474, 882, 500, 594, 612,
    641, 801, 220, 162, 819, 984, 589, 513, 495, 799,
    161, 604, 958, 533, 221, 400, 386, 867, 600, 782,
    382, 596, 414, 171, 516, 375, 682, 485, 911, 276,
    98, 553, 163, 354, 666, 933, 424, 341, 533, 870,
    227, 730, 475, 186, 263, 647, 537, 686, 600, 224,
    469, 68, 770, 919, 190, 373, 294, 822, 808, 206,
    184, 943, 795, 384, 383, 461, 404, 758, 839, 887,
    715, 67, 618, 276, 204, 918, 873, 777, 604, 560,
    951, 160, 578, 722, 79, 804, 96, 409, 713, 940,
    652, 934, 970, 447, 318, 353, 859, 672, 112, 785,
    645, 863, 803, 350, 139, 93, 354, 99, 820, 908,
    609, 772, 154, 274, 580, 184, 79, 626, 630, 742,
    653, 282, 762, 623, 680, 81, 927, 626, 789, 125,
    411, 521, 938, 300, 821, 78, 343, 175, 128, 250,
    170, 774, 972, 275, 999, 639, 495, 78, 352, 126,
    857, 956, 358, 619, 580, 124, 737, 594, 701, 612,
    669, 112, 134, 694, 363, 992, 809, 743, 168, 974,
    944, 375, 748, 52, 600, 747, 642, 182, 862, 81,
    344, 805, 988, 739, 511, 655, 814, 334, 249, 515,
    897, 955, 664, 981, 649, 113, 974, 459, 893, 228,
    433, 837, 553, 268, 926, 240, 102, 654, 459, 51,
    686, 754, 806, 760, 493, 403, 415, 394, 687, 700,
    946, 670, 656, 610, 738, 392, 760, 799, 887, 653,
    978, 321, 576, 617, 626, 502, 894, 679, 243, 440,
    680, 879, 194, 572, 640, 724, 926, 56, 204, 700,
    707, 151, 457, 449, 797, 195, 791, 558, 945, 679,
    297, 59, 87, 824, 713, 663, 412, 693, 342, 606,
    134, 108, 571, 364, 631, 212, 174, 643, 304, 329,
    343, 97, 430, 751, 497, 314, 983, 374, 822, 928,
    140, 206, 73, 263, 980, 736, 876, 478, 430, 305,
    170, 514, 364, 692, 829, 82, 855, 953, 676, 246,
    369, 970, 294, 750, 807, 827, 150, 790, 288, 923,
    804, 378, 215, 828, 592, 281, 565, 555, 710, 82,
    896, 831, 547, 261, 524, 462, 293, 465, 502, 56,
    661, 821, 976, 991, 658, 869, 905, 758, 745, 193,
    768, 550, 608, 933, 378, 286, 215, 979, 792, 961,
    61, 688, 793, 644, 986, 403, 106, 366, 905, 644,
    372, 567, 466, 434, 645, 210, 389, 550, 919, 135,
    780, 773, 635, 389, 707, 100, 626, 958, 165, 504,
    920, 176, 193, 713, 857, 265, 203, 50, 668, 108,
    645, 990, 626, 197, 510, 357, 358, 850, 858, 364,
    936, 638
  };

  private static final Logger LOGGER =
    LoggerFactory.getLogger(CBZip2InputStream.class);

  private static void reportCRCError() {
    LOGGER.error("BZip2 CRC error");
  }

  private void makeMaps() {
    final boolean[] inUse = this.data.inUse;
    final byte[] seqToUnseq = this.data.seqToUnseq;

    int nInUseShadow = 0;

    for (int i=0; i<256; i++) {
      if (inUse[i]) seqToUnseq[nInUseShadow++] = (byte) i;
    }
    this.nInUse = nInUseShadow;
  }

  /**
   * Index of the last char in the block, so the block size == last + 1.
   */
  private int last;

  /**
   * INdex in zptr[] of original string after sorting.
   */
  private int origPtr;

  /**
   * always: in the range 0 .. 9.
   * The current block size is 100000 * this number.
   */
  private int blockSize100k;

  private boolean blockRandomised;

  private int bsBuff;
  private int bsLive;
  private final CRC crc = new CRC();

  private int nInUse;

  private InputStream in;

  private int currentChar = -1;

  private static final int EOF = 0;
  private static final int START_BLOCK_STATE = 1;
  private static final int RAND_PART_A_STATE = 2;
  private static final int RAND_PART_B_STATE = 3;
  private static final int RAND_PART_C_STATE = 4;
  private static final int NO_RAND_PART_A_STATE = 5;
  private static final int NO_RAND_PART_B_STATE = 6;
  private static final int NO_RAND_PART_C_STATE = 7;

  private int currentState = START_BLOCK_STATE;

  private int storedBlockCRC, storedCombinedCRC;
  private int computedBlockCRC, computedCombinedCRC;

  // Variables used by setup* methods exclusively

  private int suCount;
  private int suCh2;
  private int suChPrev;
  private int suI2;
  private int suJ2;
  private int suRNToGo;
  private int suRTPos;
  private int suTPos;
  private char suZ;

  /**
   * All memory intensive stuff.
   * This field is initialized by initBlock();
   */
  private CBZip2InputStream.Data data;

  // -- Constructor --

  /**
   * Constructs a new CBZip2InputStream which decompresses bytes read from
   * the specified stream.
   *
   * <p>Although BZip2 headers are marked with the magic
   * <tt>"Bz"</tt> this constructor expects the next byte in the
   * stream to be the first one after the magic.  Thus callers have
   * to skip the first two bytes. Otherwise this constructor will
   * throw an exception. </p>
   *
   * @throws IOException
   *   if the stream content is malformed or an I/O error occurs.
   * @throws NullPointerException
   *   if <tt>in == null</tt>
   */
  public CBZip2InputStream(final InputStream in) throws IOException {
    super();

    this.in = in;
    init();
  }

  // -- InputStream API --

  public int read() throws IOException {
    if (this.in != null) return read0();
    throw new IOException("stream closed");
  }

  public int read(final byte[] dest, final int offs, final int len)
    throws IOException
  {
    if (offs < 0) {
      throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
    }
    if (len < 0) {
      throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
    }
    if (offs + len > dest.length) {
      throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" +
        len + ") > dest.length(" + dest.length + ").");
    }
    if (this.in == null) throw new IOException("stream closed");

    final int hi = offs + len;
    int destOffs = offs;
    for (int b; (destOffs < hi) && ((b = read0()) >= 0);) {
      dest[destOffs++] = (byte) b;
    }

    return (destOffs == offs) ? -1 : (destOffs - offs);
  }

  private int read0() throws IOException {
    final int retChar = this.currentChar;

    switch (this.currentState) {
      case EOF:
        return -1;

      case START_BLOCK_STATE:
        throw new IllegalStateException();

      case RAND_PART_A_STATE:
        throw new IllegalStateException();

      case RAND_PART_B_STATE:
        setupRandPartB();
        break;

      case RAND_PART_C_STATE:
        setupRandPartC();
        break;

      case NO_RAND_PART_A_STATE:
        throw new IllegalStateException();

      case NO_RAND_PART_B_STATE:
        setupNoRandPartB();
        break;

      case NO_RAND_PART_C_STATE:
        setupNoRandPartC();
        break;

      default:
        throw new IllegalStateException();
    }

    return retChar;
  }

  private void init() throws IOException {
    int magic2 = this.in.read();
    if (magic2 != 'h') {
      throw new IOException("Stream is not BZip2 formatted: expected 'h'" +
        " as first byte but got '" + (char) magic2 + "'");
    }

    int blockSize = this.in.read();
    if ((blockSize < '1') || (blockSize > '9')) {
      throw new IOException("Stream is not BZip2 formatted: illegal " +
        "blocksize " + (char) blockSize);
    }

    this.blockSize100k = blockSize - '0';

    initBlock();
    setupBlock();
  }

  private void initBlock() throws IOException {
    char magic0 = bsGetUByte();
    char magic1 = bsGetUByte();
    char magic2 = bsGetUByte();
    char magic3 = bsGetUByte();
    char magic4 = bsGetUByte();
    char magic5 = bsGetUByte();

    if (magic0 == 0x17 &&
      magic1 == 0x72 &&
      magic2 == 0x45 &&
      magic3 == 0x38 &&
      magic4 == 0x50 &&
      magic5 == 0x90)
    {
      complete(); // end of file
    }
    else if (magic0 != 0x31 || // '1'
           magic1 != 0x41 || // ')'
           magic2 != 0x59 || // 'Y'
           magic3 != 0x26 || // '&'
           magic4 != 0x53 || // 'S'
           magic5 != 0x59) // 'Y'
    {
      this.currentState = EOF;
      throw new IOException("bad block header");
    }
    else {
      this.storedBlockCRC = bsGetInt();
      this.blockRandomised = bsR(1) == 1;

      // Allocate data here instead in constructor, so we do not
      // allocate it if the input file is empty.
      if (this.data == null) {
        this.data = new Data(this.blockSize100k);
      }

      // currBlockNo++;
      getAndMoveToFrontDecode();

      this.crc.initialiseCRC();
      this.currentState = START_BLOCK_STATE;
    }
  }

  private void endBlock() {
    this.computedBlockCRC = this.crc.getFinalCRC();

    // A bad CRC is considered a fatal error.
    if (this.storedBlockCRC != this.computedBlockCRC) {
      // make next blocks readable without error
      // (repair feature, not yet documented, not tested)
      this.computedCombinedCRC =
        (this.storedCombinedCRC << 1) | (this.storedCombinedCRC >>> 31);
      this.computedCombinedCRC ^= this.storedBlockCRC;

      reportCRCError();
    }

    this.computedCombinedCRC =
      (this.computedCombinedCRC << 1) | (this.computedCombinedCRC >>> 31);
    this.computedCombinedCRC ^= this.computedBlockCRC;
  }

  private void complete() throws IOException {
    this.storedCombinedCRC = bsGetInt();
    this.currentState = EOF;
    this.data = null;

    if (this.storedCombinedCRC != this.computedCombinedCRC) {
      reportCRCError();
    }
  }

  public void close() throws IOException {
    InputStream inShadow = this.in;
    if (inShadow != null) {
      try {
        if (inShadow != System.in) inShadow.close();
      }
      finally {
        this.data = null;
        this.in = null;
      }
    }
  }

  private int bsR(final int n) throws IOException {
    int bsLiveShadow = this.bsLive;
    int bsBuffShadow = this.bsBuff;

    if (bsLiveShadow < n) {
      final InputStream inShadow = this.in;
      do {
        int thech = inShadow.read();

        if (thech < 0) throw new IOException("unexpected end of stream");

        bsBuffShadow = (bsBuffShadow << 8) | thech;
        bsLiveShadow += 8;
      }
      while (bsLiveShadow < n);

      this.bsBuff = bsBuffShadow;
    }

    this.bsLive = bsLiveShadow - n;
    return (bsBuffShadow >> (bsLiveShadow - n)) & ((1 << n) - 1);
  }

  private boolean bsGetBit() throws IOException {
    return bsR(1) != 0;
  }

  private char bsGetUByte() throws IOException {
    return (char) bsR(8);
  }

  private int bsGetInt() throws IOException {
    return (((((bsR(8) << 8) | bsR(8)) << 8) | bsR(8)) << 8) | bsR(8);
  }

  /** Called by createHuffmanDecodingTables() exclusively. */
  private static void hbCreateDecodeTables(final int[] limit,
    final int[] base, final int[] perm, final char[] length,
    final int minLen, final int maxLen, final int alphaSize)
  {
    for (int i = minLen, pp = 0; i <= maxLen; i++) {
      for (int j = 0; j < alphaSize; j++) {
        if (length[j] == i) perm[pp++] = j;
      }
    }

    for (int i = MAX_CODE_LEN; --i > 0;) {
      base[i] = 0;
      limit[i] = 0;
    }

    for (int i = 0; i < alphaSize; i++) {
      base[length[i] + 1]++;
    }

    for (int i = 1, b = base[0]; i < MAX_CODE_LEN; i++) {
      b += base[i];
      base[i] = b;
    }

    for (int i = minLen, vec = 0, b = base[i]; i <= maxLen; i++) {
      final int nb = base[i + 1];
      vec += nb - b;
      b = nb;
      limit[i] = vec - 1;
      vec <<= 1;
    }

    for (int i = minLen + 1; i <= maxLen; i++) {
      base[i] = ((limit[i - 1] + 1) << 1) - base[i];
    }
  }

  private void recvDecodingTables() throws IOException {
    final Data dataShadow    = this.data;
    final boolean[] inUse    = dataShadow.inUse;
    final byte[] pos       = dataShadow.recvDecodingTablesPos;
    final byte[] selector    = dataShadow.selector;
    final byte[] selectorMtf  = dataShadow.selectorMtf;

    int inUse16 = 0;

    // Receive the mapping table
    for (int i = 0; i < 16; i++) {
      if (bsGetBit()) {
        inUse16 |= 1 << i;
      }
    }

    for (int i = 256; --i >= 0;) inUse[i] = false;

    for (int i = 0; i < 16; i++) {
      if ((inUse16 & (1 << i)) != 0) {
        final int i16 = i << 4;
        for (int j = 0; j < 16; j++) {
          if (bsGetBit()) inUse[i16 + j] = true;
        }
      }
    }

    makeMaps();
    final int alphaSize = this.nInUse + 2;

    // Now the selectors
    final int nGroups = bsR(3);
    final int nSelectors = bsR(15);

    for (int i = 0; i < nSelectors; i++) {
      int j = 0;
      while (bsGetBit()) j++;
      selectorMtf[i] = (byte) j;
    }

    // Undo the MTF values for the selectors.
    for (int v = nGroups; --v >= 0;) pos[v] = (byte) v;

    for (int i = 0; i < nSelectors; i++) {
      int v = selectorMtf[i] & 0xff;
      final byte tmp = pos[v];
      while (v > 0) {
        // nearly all times v is zero, 4 in most other cases
        pos[v] = pos[v - 1];
        v--;
      }
      pos[0] = tmp;
      selector[i] = tmp;
    }

    final char[][] len  = dataShadow.tempCharArray2d;

    // Now the coding tables
    for (int t = 0; t < nGroups; t++) {
      int curr = bsR(5);
      final char[] tLen = len[t];
      for (int i = 0; i < alphaSize; i++) {
        while (bsGetBit()) curr += bsGetBit() ? -1 : 1;
        tLen[i] = (char) curr;
      }
    }

    // finally create the Huffman tables
    createHuffmanDecodingTables(alphaSize, nGroups);
  }

  /** Called by recvDecodingTables() exclusively. */
  private void createHuffmanDecodingTables(final int alphaSize,
    final int nGroups)
  {
    final Data dataShadow = this.data;
    final char[][] len  = dataShadow.tempCharArray2d;
    final int[] minLens = dataShadow.minLens;
    final int[][] limit = dataShadow.limit;
    final int[][] base  = dataShadow.base;
    final int[][] perm  = dataShadow.perm;

    for (int t = 0; t < nGroups; t++) {
      int minLen = 32;
      int maxLen = 0;
      final char[] tLen = len[t];
      for (int i = alphaSize; --i >= 0;) {
        final char lent = tLen[i];
        if (lent > maxLen) maxLen = lent;
        if (lent < minLen) minLen = lent;
      }
      hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen,
        maxLen, alphaSize);
      minLens[t] = minLen;
    }
  }

  private void getAndMoveToFrontDecode() throws IOException {
    this.origPtr = bsR(24);
    recvDecodingTables();

    final InputStream inShadow = this.in;
    final Data dataShadow  = this.data;
    final byte[] ll8      = dataShadow.ll8;
    final int[] unzftab    = dataShadow.unzftab;
    final byte[] selector  = dataShadow.selector;
    final byte[] seqToUnseq = dataShadow.seqToUnseq;
    final char[] yy      = dataShadow.getAndMoveToFrontDecodeYY;
    final int[] minLens    = dataShadow.minLens;
    final int[][] limit    = dataShadow.limit;
    final int[][] base    = dataShadow.base;
    final int[][] perm    = dataShadow.perm;
    final int limitLast    = this.blockSize100k * 100000;

    // Setting up the unzftab entries here is not strictly
    // necessary, but it does save having to do it later
    // in a separate pass, and so saves a block's worth of
    // cache misses.
    for (int i = 256; --i >= 0;) {
      yy[i] = (char) i;
      unzftab[i] = 0;
    }

    int groupNo    = 0;
    int groupPos   = G_SIZE - 1;
    final int eob  = this.nInUse + 1;
    int nextSym    = getAndMoveToFrontDecode0(0);
    int bsBuffShadow    = this.bsBuff;
    int bsLiveShadow    = this.bsLive;
    int lastShadow      = -1;
    int zt       = selector[groupNo] & 0xff;
    int[] baseZT  = base[zt];
    int[] limitZT  = limit[zt];
    int[] permZT  = perm[zt];
    int minLensZT  = minLens[zt];

    while (nextSym != eob) {
      if ((nextSym == RUNA) || (nextSym == RUNB)) {
        int s = -1;

        for (int n = 1; true; n <<= 1) {
          if (nextSym == RUNA) s += n;
          else if (nextSym == RUNB) s += n << 1;
          else break;

          if (groupPos == 0) {
            groupPos   = G_SIZE - 1;
            zt       = selector[++groupNo] & 0xff;
            baseZT    = base[zt];
            limitZT   = limit[zt];
            permZT    = perm[zt];
            minLensZT  = minLens[zt];
          }
          else groupPos--;

          int zn = minLensZT;

          // Inlined:
          // int zvec = bsR(zn);
          while (bsLiveShadow < zn) {
            final int thech = inShadow.read();
            if (thech >= 0) {
              bsBuffShadow = (bsBuffShadow << 8) | thech;
              bsLiveShadow += 8;
              continue;
            }
            throw new IOException("unexpected end of stream");
          }
          int zvec = (bsBuffShadow >> (bsLiveShadow - zn)) & ((1 << zn) - 1);
          bsLiveShadow -= zn;

          while (zvec > limitZT[zn]) {
            zn++;
            while (bsLiveShadow < 1) {
              final int thech = inShadow.read();
              if (thech >= 0) {
                bsBuffShadow = (bsBuffShadow << 8) | thech;
                bsLiveShadow += 8;
                continue;
              }
              throw new IOException("unexpected end of stream");
            }
            bsLiveShadow--;
            zvec = (zvec << 1) | ((bsBuffShadow >> bsLiveShadow) & 1);
          }
          nextSym = permZT[zvec - baseZT[zn]];
        }

        final byte ch = seqToUnseq[yy[0]];
        unzftab[ch & 0xff] += s + 1;

        while (s-- >= 0) ll8[++lastShadow] = ch;

        if (lastShadow >= limitLast) throw new IOException("block overrun");
      }
      else {
        if (++lastShadow >= limitLast) {
          throw new IOException("block overrun");
        }

        final char tmp = yy[nextSym - 1];
        unzftab[seqToUnseq[tmp] & 0xff]++;
        ll8[lastShadow] = seqToUnseq[tmp];

        /*
         This loop is hammered during decompression,
         hence avoid native method call overhead of
         System.arraycopy for very small ranges to copy.
        */
        if (nextSym <= 16) {
          for (int j = nextSym - 1; j > 0;) yy[j] = yy[--j];
        }
        else System.arraycopy(yy, 0, yy, 1, nextSym - 1);

        yy[0] = tmp;

        if (groupPos == 0) {
          groupPos   = G_SIZE - 1;
          zt       = selector[++groupNo] & 0xff;
          baseZT    = base[zt];
          limitZT   = limit[zt];
          permZT    = perm[zt];
          minLensZT  = minLens[zt];
        }
        else groupPos--;

        int zn = minLensZT;

        // Inlined:
        // int zvec = bsR(zn);
        while (bsLiveShadow < zn) {
          final int thech = inShadow.read();
          if (thech >= 0) {
            bsBuffShadow = (bsBuffShadow << 8) | thech;
            bsLiveShadow += 8;
            continue;
          }
          throw new IOException("unexpected end of stream");
        }
        int zvec = (bsBuffShadow >> (bsLiveShadow - zn)) & ((1 << zn) - 1);
        bsLiveShadow -= zn;

        while (zvec > limitZT[zn]) {
          zn++;
          while (bsLiveShadow < 1) {
            final int thech = inShadow.read();
            if (thech >= 0) {
              bsBuffShadow = (bsBuffShadow << 8) | thech;
              bsLiveShadow += 8;
              continue;
            }
            throw new IOException("unexpected end of stream");
          }
          bsLiveShadow--;
          zvec = (zvec << 1) | ((bsBuffShadow >> bsLiveShadow) & 1);
        }
        nextSym = permZT[zvec - baseZT[zn]];
      }
    }

    this.last = lastShadow;
    this.bsLive = bsLiveShadow;
    this.bsBuff = bsBuffShadow;
  }

  private int getAndMoveToFrontDecode0(final int groupNo)
    throws IOException
  {
    final InputStream inShadow  = this.in;
    final Data dataShadow  = this.data;
    final int zt       = dataShadow.selector[groupNo] & 0xff;
    final int[] limitZT  = dataShadow.limit[zt];
    int zn = dataShadow.minLens[zt];
    int zvec = bsR(zn);
    int bsLiveShadow = this.bsLive;
    int bsBuffShadow = this.bsBuff;

    while (zvec > limitZT[zn]) {
      zn++;
      while (bsLiveShadow < 1) {
        final int thech = inShadow.read();

        if (thech >= 0) {
          bsBuffShadow = (bsBuffShadow << 8) | thech;
          bsLiveShadow += 8;
          continue;
        }
        throw new IOException("unexpected end of stream");
      }
      bsLiveShadow--;
      zvec = (zvec << 1) | ((bsBuffShadow >> bsLiveShadow) & 1);
    }

    this.bsLive = bsLiveShadow;
    this.bsBuff = bsBuffShadow;

    return dataShadow.perm[zt][zvec - dataShadow.base[zt][zn]];
  }

  private void setupBlock() throws IOException {
    if (this.data == null) return;

    final int[] cftab = this.data.cftab;
    final int[] tt   = this.data.initTT(this.last + 1);
    final byte[] ll8  = this.data.ll8;
    cftab[0] = 0;
    System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);

    for (int i = 1, c = cftab[0]; i <= 256; i++) {
      c += cftab[i];
      cftab[i] = c;
    }

    for (int i = 0, lastShadow = this.last; i <= lastShadow; i++) {
      tt[cftab[ll8[i] & 0xff]++] = i;
    }

    if ((this.origPtr < 0) || (this.origPtr >= tt.length)) {
      throw new IOException("stream corrupted");
    }

    this.suTPos = tt[this.origPtr];
    this.suCount = 0;
    this.suI2 = 0;
    this.suCh2 = 256;  /* not a char and not EOF */

    if (this.blockRandomised) {
      this.suRNToGo = 0;
      this.suRTPos = 0;
      setupRandPartA();
    }
    else setupNoRandPartA();
  }

  private void setupRandPartA() throws IOException {
    if (this.suI2 <= this.last) {
      this.suChPrev = this.suCh2;
      int suCh2Shadow = this.data.ll8[this.suTPos] & 0xff;
      this.suTPos = this.data.tt[this.suTPos];
      if (this.suRNToGo == 0) {
        this.suRNToGo = R_NUMS[this.suRTPos] - 1;
        if (++this.suRTPos == 512) this.suRTPos = 0;
      }
      else this.suRNToGo--;
      this.suCh2 = suCh2Shadow ^= (this.suRNToGo == 1) ? 1 : 0;
      this.suI2++;
      this.currentChar = suCh2Shadow;
      this.currentState = RAND_PART_B_STATE;
      this.crc.updateCRC(suCh2Shadow);
    }
    else {
      endBlock();
      initBlock();
      setupBlock();
    }
  }

  private void setupNoRandPartA() throws IOException {
    if (this.suI2 <= this.last) {
      this.suChPrev = this.suCh2;
      int suCh2Shadow = this.data.ll8[this.suTPos] & 0xff;
      this.suCh2 = suCh2Shadow;
      this.suTPos = this.data.tt[this.suTPos];
      this.suI2++;
      this.currentChar = suCh2Shadow;
      this.currentState = NO_RAND_PART_B_STATE;
      this.crc.updateCRC(suCh2Shadow);
    }
    else {
      this.currentState = NO_RAND_PART_A_STATE;
      endBlock();
      initBlock();
      setupBlock();
    }
  }

  private void setupRandPartB() throws IOException {
    if (this.suCh2 != this.suChPrev) {
      this.currentState = RAND_PART_A_STATE;
      this.suCount = 1;
      setupRandPartA();
    }
    else if (++this.suCount >= 4) {
      this.suZ = (char) (this.data.ll8[this.suTPos] & 0xff);
      this.suTPos = this.data.tt[this.suTPos];
      if (this.suRNToGo == 0) {
        this.suRNToGo = R_NUMS[this.suRTPos] - 1;
        if (++this.suRTPos == 512) {
          this.suRTPos = 0;
        }
      }
      else this.suRNToGo--;
      this.suJ2 = 0;
      this.currentState = RAND_PART_C_STATE;
      if (this.suRNToGo == 1) this.suZ ^= 1;
      setupRandPartC();
    }
    else {
      this.currentState = RAND_PART_A_STATE;
      setupRandPartA();
    }
  }

  private void setupRandPartC() throws IOException {
    if (this.suJ2 < this.suZ) {
      this.currentChar = this.suCh2;
      this.crc.updateCRC(this.suCh2);
      this.suJ2++;
    }
    else {
      this.currentState = RAND_PART_A_STATE;
      this.suI2++;
      this.suCount = 0;
      setupRandPartA();
    }
  }

  private void setupNoRandPartB() throws IOException {
    if (this.suCh2 != this.suChPrev) {
      this.suCount = 1;
      setupNoRandPartA();
    }
    else if (++this.suCount >= 4) {
      this.suZ = (char) (this.data.ll8[this.suTPos] & 0xff);
      this.suTPos = this.data.tt[this.suTPos];
      this.suJ2 = 0;
      setupNoRandPartC();
    }
    else setupNoRandPartA();
  }

  private void setupNoRandPartC() throws IOException {
    if (this.suJ2 < this.suZ) {
      int suCh2Shadow = this.suCh2;
      this.currentChar = suCh2Shadow;
      this.crc.updateCRC(suCh2Shadow);
      this.suJ2++;
      this.currentState = NO_RAND_PART_C_STATE;
    }
    else {
      this.suI2++;
      this.suCount = 0;
      setupNoRandPartA();
    }
  }

  private static final class Data extends Object {
    // (with blockSize 900k)
    final boolean[] inUse  = new boolean[256];                  //     256 byte

    final byte[] seqToUnseq  = new byte[256];                   //     256 byte
    final byte[] selector    = new byte[MAX_SELECTORS];         //   18002 byte
    final byte[] selectorMtf  = new byte[MAX_SELECTORS];        //   18002 byte

    /**
     * Freq table collected to save a pass over the data during
     * decompression.
     */
    final int[] unzftab = new int[256];                         //    1024 byte

    final int[][] limit = new int[N_GROUPS][MAX_ALPHA_SIZE];    //    6192 byte
    final int[][] base  = new int[N_GROUPS][MAX_ALPHA_SIZE];    //    6192 byte
    final int[][] perm  = new int[N_GROUPS][MAX_ALPHA_SIZE];    //    6192 byte
    final int[] minLens = new int[N_GROUPS];                    //      24 byte

    final int[]    cftab    = new int[257];                     //    1028 byte
    final char[]   getAndMoveToFrontDecodeYY = new char[256];   //     512 byte

    //                                                                3096 byte
    final char[][]  tempCharArray2d  = new char[N_GROUPS][MAX_ALPHA_SIZE];

    final byte[] recvDecodingTablesPos = new byte[N_GROUPS];    //       6 byte
    //---------------
    //   60798 byte

    int[] tt;                                                   // 3600000 byte
    byte[] ll8;                                                 //  900000 byte
    //---------------
    //  4560782 byte
    //===============

    Data(int blockSize100k) {
      super();

      this.ll8 = new byte[blockSize100k * BASE_BLOCK_SIZE];
    }

    /**
     * Initializes the {@link #tt} array.
     *
     * This method is called when the required length of the array
     * is known.  I don't initialize it at construction time to
     * avoid unneccessary memory allocation when compressing small
     * files.
     */
    int[] initTT(int length) {
      int[] ttShadow = this.tt;

      // tt.length should always be >= length, but theoretically
      // it can happen, if the compressor mixed small and large
      // blocks.  Normally only the last block will be smaller
      // than others.
      if ((ttShadow == null) || (ttShadow.length < length)) {
        this.tt = ttShadow = new int[length];
      }

      return ttShadow;
    }
  }

}

