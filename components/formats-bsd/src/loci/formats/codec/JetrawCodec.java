/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2022 Dotphoton AG:
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MissingLibraryException;
import loci.formats.UnsupportedCompressionException;

/**
 */
public class JetrawCodec extends BaseCodec {

  private String getJetrawLibraryName() throws MissingLibraryException {
    String operatingSystem = System.getProperty("os.name").toLowerCase();
    String libraryName = "";
    if (operatingSystem.contains("win")) {
        libraryName = "jetraw_bioformats_plugin.dll";
    } else if (operatingSystem.contains("mac")) {
        libraryName = "libjetraw_bioformats_plugin.dylib";
    } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
        libraryName = "libjetraw_bioformats_plugin.so";
    } else {
        throw new MissingLibraryException("Could not determine OS to load jetraw dynamic library.");
    }
    return libraryName;
  }

  // load jetraw jni dll from jar file
  private void loadLib(String name) throws MissingLibraryException {
    System.out.println("[INFO] loading JNI Jetraw library.");
    InputStream in = JetrawCodec.class.getResourceAsStream(name);
    byte[] buffer = new byte[1024];
    int read = -1;
    try {
        File temp = File.createTempFile(name, "");
        FileOutputStream fos = new FileOutputStream(temp);

        while((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();
        System.load(temp.getAbsolutePath());
        System.out.println("[INFO] JNI Jetraw library loaded successfully.");
    } catch (Exception e) {
        throw new MissingLibraryException("JNI Jetraw library not available", e);
    }
  }

  public native int performDecoding(byte[] buf, int bufSize, short[] out, int outSize);
  // -- Fields --

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options) throws FormatException {
    throw new UnsupportedCompressionException("Jetraw compression not supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options) throws FormatException, IOException {
    byte[] buf = new byte[(int) in.length()];
    in.read(buf);
    return decompress(buf, options);
  }

  @Override
  public byte[] decompress(byte[] buf, CodecOptions options) throws FormatException {
    // load jetraw native library
    loadLib(getJetrawLibraryName());

    int pixels = options.width*options.height;
    short[] decomp = new short[pixels];
    int ok = performDecoding(buf, buf.length, decomp, pixels);
    if (ok == 0) {
      throw new FormatException("[ERROR] Jetraw performDecoding exited with error.");
    }

    int short_index = 0;
    int byte_index = 0;
    byte[] byteBuf = new byte[2*pixels];
    // convert short buffer into a byte buffer
    for(/*NOP*/; short_index != pixels; /*NOP*/) {
        if (options.littleEndian == true) {
            byteBuf[byte_index]     = (byte) (decomp[short_index] & 0x00FF);
            byteBuf[byte_index + 1] = (byte) ((decomp[short_index] & 0xFF00) >> 8);
        } else {
            byteBuf[byte_index]     = (byte) ((decomp[short_index] & 0xFF00) >> 8);
            byteBuf[byte_index + 1] = (byte) (decomp[short_index] & 0x00FF);
        }
        ++short_index; byte_index += 2;
    }
    return byteBuf;
  }

}
