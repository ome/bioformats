/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.codec;

import java.io.IOException;
import java.util.Vector;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.UnsupportedCompressionException;

/**
 * Decompresses lossless JPEG images.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LosslessJPEGCodec extends BaseCodec {

  // -- Constants --

  // Start of Frame markers - non-differential, Huffman coding
  private static final int SOF0 = 0xffc0; // baseline DCT
  private static final int SOF1 = 0xffc1; // extended sequential DCT
  private static final int SOF2 = 0xffc2; // progressive DCT
  private static final int SOF3 = 0xffc3; // lossless (sequential)

  // Start of Frame markers - differential, Huffman coding
  private static final int SOF5 = 0xffc5; // differential sequential DCT
  private static final int SOF6 = 0xffc6; // differential progressive DCT
  private static final int SOF7 = 0xffc7; // differential lossless (sequential)

  // Start of Frame markers - non-differential, arithmetic coding
  private static final int JPG = 0xffc8; // reserved for JPEG extensions
  private static final int SOF9 = 0xffc9; // extended sequential DCT
  private static final int SOF10 = 0xffca; // progressive DCT
  private static final int SOF11 = 0xffcb; // lossless (sequential)

  // Start of Frame markers - differential, arithmetic coding
  private static final int SOF13 = 0xffcd; // differential sequential DCT
  private static final int SOF14 = 0xffce; // differential progressive DCT
  private static final int SOF15 = 0xffcf; // differential lossless (sequential)

  private static final int DHT = 0xffc4; // define Huffman table(s)
  private static final int DAC = 0xffcc; // define arithmetic coding conditions

  // Restart interval termination
  private static final int RST_0 = 0xffd0;
  private static final int RST_1 = 0xffd1;
  private static final int RST_2 = 0xffd2;
  private static final int RST_3 = 0xffd3;
  private static final int RST_4 = 0xffd4;
  private static final int RST_5 = 0xffd5;
  private static final int RST_6 = 0xffd6;
  private static final int RST_7 = 0xffd7;

  private static final int SOI = 0xffd8; // start of image
  private static final int EOI = 0xffd9; // end of image
  private static final int SOS = 0xffda; // start of scan
  private static final int DQT = 0xffdb; // define quantization table(s)
  private static final int DNL = 0xffdc; // define number of lines
  private static final int DRI = 0xffdd; // define restart interval
  private static final int DHP = 0xffde; // define hierarchical progression
  private static final int EXP = 0xffdf; // expand reference components
  private static final int COM = 0xfffe; // comment

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "Lossless JPEG compression not supported");
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#interleaved interleaved}
   *  {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (in == null) 
      throw new IllegalArgumentException("No data to decompress.");
    if (options == null) options = CodecOptions.getDefaultOptions();
    byte[] buf = new byte[0];

    int width = 0, height = 0;
    int bitsPerSample = 0, nComponents = 0, bytesPerSample = 0;
    int[] horizontalSampling = null, verticalSampling = null;
    int[] quantizationTable = null;
    short[][] huffmanTables = null;

    int startPredictor = 0, endPredictor = 0;
    int pointTransform = 0;

    int[] dcTable = null, acTable = null;

    while (in.getFilePointer() < in.length() - 1) {
      int code = in.readShort() & 0xffff;
      int length = in.readShort() & 0xffff;
      long fp = in.getFilePointer();
      if (length > 0xff00) {
        length = 0;
        in.seek(fp - 2);
      }
      else if (code == SOS) {
        nComponents = in.read();
        dcTable = new int[nComponents];
        acTable = new int[nComponents];
        for (int i=0; i<nComponents; i++) {
          int componentSelector = in.read();
          int tableSelector = in.read();
          dcTable[i] = (tableSelector & 0xf0) >> 4;
          acTable[i] = tableSelector & 0xf;
        }
        startPredictor = in.read();
        endPredictor = in.read();
        pointTransform = in.read() & 0xf;

        // read image data

        byte[] toDecode = new byte[(int) (in.length() - in.getFilePointer())];
        in.read(toDecode);

        // scrub out byte stuffing

        ByteVector b = new ByteVector();
        for (int i=0; i<toDecode.length; i++) {
          byte val = toDecode[i];
          if (val == (byte) 0xff) {
        	if (toDecode[i + 1] == 0)
        		b.add(val);
    		i++;
          } else {
            b.add(val);
          }
        }
        toDecode = b.toByteArray();

        RandomAccessInputStream bb = new RandomAccessInputStream(
          new ByteArrayHandle(toDecode));

        HuffmanCodec huffman = new HuffmanCodec();
        HuffmanCodecOptions huffmanOptions = new HuffmanCodecOptions();
        huffmanOptions.bitsPerSample = bitsPerSample;
        huffmanOptions.maxBytes = buf.length / nComponents;

        int nextSample = 0;
        while (nextSample < buf.length / nComponents) {
          for (int i=0; i<nComponents; i++) {
            huffmanOptions.table = huffmanTables[dcTable[i]];
            int v = 0;

            if (huffmanTables != null) {
              v = huffman.getSample(bb, huffmanOptions);
              if (nextSample == 0) {
                v += (int) Math.pow(2, bitsPerSample - 1);
              }
            }
            else {
              throw new UnsupportedCompressionException(
                "Arithmetic coding not supported");
            }

            // apply predictor to the sample
            int predictor = startPredictor;
            if (nextSample < width * bytesPerSample) predictor = 1;
            else if ((nextSample % (width * bytesPerSample)) == 0) {
              predictor = 2;
            }

            int componentOffset = i * (buf.length / nComponents);

            int indexA = nextSample - bytesPerSample + componentOffset;
            int indexB = nextSample - width * bytesPerSample + componentOffset;
            int indexC = nextSample - (width + 1) * bytesPerSample +
              componentOffset;

            int sampleA = indexA < 0 ? 0 :
              DataTools.bytesToInt(buf, indexA, bytesPerSample, false);
            int sampleB = indexB < 0 ? 0 :
              DataTools.bytesToInt(buf, indexB, bytesPerSample, false);
            int sampleC = indexC < 0 ? 0 :
              DataTools.bytesToInt(buf, indexC, bytesPerSample, false);

            if (nextSample > 0) {
              int pred = 0;
              switch (predictor) {
                case 1:
                  pred = sampleA;
                  break;
                case 2:
                  pred = sampleB;
                  break;
                case 3:
                  pred = sampleC;
                  break;
                case 4:
                  pred = sampleA + sampleB + sampleC;
                  break;
                case 5:
                  pred = sampleA + ((sampleB - sampleC) / 2);
                  break;
                case 6:
                  pred = sampleB + ((sampleA - sampleC) / 2);
                  break;
                case 7:
                  pred = (sampleA + sampleB) / 2;
                  break;
              }
              v += pred;
            }

            int offset = componentOffset + nextSample;

            DataTools.unpackBytes(v, buf, offset, bytesPerSample, false);
          }
          nextSample += bytesPerSample;
        }
        bb.close();
      }
      else {
        length -= 2; // stored length includes length param
        if (length == 0) continue;

        if (code == EOI) { }
        else if (code == SOF3) {
          // lossless w/Huffman coding
          bitsPerSample = in.read();
          height = in.readShort();
          width = in.readShort();
          nComponents = in.read();
          horizontalSampling = new int[nComponents];
          verticalSampling = new int[nComponents];
          quantizationTable = new int[nComponents];
          for (int i=0; i<nComponents; i++) {
            in.skipBytes(1);
            int s = in.read();
            horizontalSampling[i] = (s & 0xf0) >> 4;
            verticalSampling[i] = s & 0x0f;
            quantizationTable[i] = in.read();
          }

          bytesPerSample = bitsPerSample / 8;
          if ((bitsPerSample % 8) != 0) bytesPerSample++;

          buf = new byte[width * height * nComponents * bytesPerSample];
        }
        else if (code == SOF11) {
          throw new UnsupportedCompressionException(
            "Arithmetic coding is not yet supported");
        }
        else if (code == DHT) {
          if (huffmanTables == null) {
            huffmanTables = new short[4][];
          }
          int bytesRead = 0;
          while (bytesRead < length) {
	          int s = in.read();
	          byte tableClass = (byte) ((s & 0xf0) >> 4);
	          byte destination = (byte) (s & 0xf);
	          int[] nCodes = new int[16];
	          Vector table = new Vector();
	          for (int i=0; i<nCodes.length; i++) {
	            nCodes[i] = in.read();
	            table.add(new Short((short) nCodes[i]));
	          }
	
	          for (int i=0; i<nCodes.length; i++) {
	            for (int j=0; j<nCodes[i]; j++) {
	              table.add(new Short((short) (in.read() & 0xff)));
	            }
	          }
	          huffmanTables[destination] = new short[table.size()];
	          for (int i=0; i<huffmanTables[destination].length; i++) {
	            huffmanTables[destination][i] = ((Short) table.get(i)).shortValue();
	          }
	          bytesRead += table.size() + 1;
          }
        }
        in.seek(fp + length);
      }
    }

    if (options.interleaved && nComponents > 1) {
      // data is stored in planar (RRR...GGG...BBB...) order
      byte[] newBuf = new byte[buf.length];
      for (int i=0; i<buf.length; i+=nComponents*bytesPerSample) {
        for (int c=0; c<nComponents; c++) {
          int src = c * (buf.length / nComponents) + (i / nComponents);
          int dst = i + c * bytesPerSample;
          System.arraycopy(buf, src, newBuf, dst, bytesPerSample);
        }
      }
      buf = newBuf;
    }

    if (options.littleEndian && bytesPerSample > 1) {
      // data is stored in big endian order
      // reverse the bytes in each sample
      byte[] newBuf = new byte[buf.length];
      for (int i=0; i<buf.length; i+=bytesPerSample) {
        for (int q=0; q<bytesPerSample; q++) {
          newBuf[i + bytesPerSample - q - 1] = buf[i + q];
        }
      }
      buf = newBuf;
    }

    return buf;
  }

}
