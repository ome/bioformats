/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2022 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 *   - Dotphoton AG
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;
import loci.formats.UnsupportedCompressionException;

/**
 */
public class JetrawCodec extends BaseCodec {

  private boolean isInitialized = false;

  private byte[] convertShortArrayToByteArray(short[] shortBuffer, CodecOptions options) {
    int pixels = options.width*options.height;
    // convert short buffer into a byte buffer
    int short_index = 0;
    int byte_index = 0;
    byte[] byteBuffer = new byte[2*pixels];
    for(/*NOP*/; short_index != pixels; /*NOP*/) {
      if (options.littleEndian) {
        byteBuffer[byte_index]     = (byte) (shortBuffer[short_index] & 0x00FF);
        byteBuffer[byte_index + 1] = (byte) ((shortBuffer[short_index] & 0xFF00) >> 8);
      } else {
        byteBuffer[byte_index]     = (byte) ((shortBuffer[short_index] & 0xFF00) >> 8);
        byteBuffer[byte_index + 1] = (byte) (shortBuffer[short_index] & 0x00FF);
      }
      ++short_index; byte_index += 2;
    }
    return byteBuffer;
  }

  private String createExceptionMessage() {
    String operatingSystem = System.getProperty("os.name").toLowerCase();
    String message = "\n\n[JETRAW] Exception thrown"
                   + "\n  Error loading jetraw_bioformats_plugin dynamic library."
                   + "\n  Please verify that Jetraw is installed in default folder:";
    if (operatingSystem.contains("win")) {
        message = message
                + "\n\n    C:/Program Files/Jetraw/bin64/ \n"
                + "\n  If not, please add your Jetraw installation folder into your PATH.\n"
                + "\n  If you need to install Jetraw, download link: "
                + "\n    https://www.jetraw.com/download-jetraw-demo \n";
    } else if (operatingSystem.contains("mac")) {
        message = message
                + "\n\n    /Applications/Jetraw UI.app/Contents/jetraw/lib/ \n"
                + "\n  If not, please add your Jetraw installation folder"
                + "\n  into your DYLD_FALLBACK_LIBRARY_PATH.\n"
                + "\n  If you need to install Jetraw, download link: "
                + "\n    https://www.jetraw.com/download-jetraw-demo \n";
    } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
        message = message
                + "\n\n    ~/jetraw/lib/ \n"
                + "\n  If not, please add your Jetraw installation folder"
                + "\n  into your LD_LIBRARY_PATH.\n"
                + "\n  If you need to install Jetraw, download link: "
                + "\n    https://www.jetraw.com/download-jetraw-demo \n";
    }
    return message;
  }

  private String getJetrawLibraryPath() throws MissingLibraryException {
    String operatingSystem = System.getProperty("os.name").toLowerCase();
    String libraryPath = "";
    if (operatingSystem.contains("win")) {
        libraryPath = "C:\\Program Files\\Jetraw\\bin64\\jetraw_bioformats_plugin.dll";
    } else if (operatingSystem.contains("mac")) {
        libraryPath = File.separator + "Applications" + File.separator + "Jetraw UI.app"
                    + File.separator + "Contents" + File.separator + "jetraw" + File.separator + "lib"
                    + File.separator + "libjetraw_bioformats_plugin.dylib";
    } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
        String homeDirectory = System.getProperty("user.home");
        libraryPath = homeDirectory + File.separator + "jetraw" + File.separator
                    + "lib" + File.separator + "libjetraw_bioformats_plugin.so";
    } else {
        throw new MissingLibraryException("Could not determine OS to load jetraw dynamic library.");
    }
    return libraryPath;
  }

  // load jetraw jni dynamic library from default path or PATH
  private void loadLib(String libraryName) throws UnsatisfiedLinkError {
    LOGGER.info("[JETRAW] trying to load JNI dynamic library : " + libraryName);
    boolean libraryExists = Files.exists(Paths.get(libraryName));
    try {
      if (libraryExists) {
        LOGGER.info("[JETRAW] JNI library file found. Trying to load.");
        System.load(libraryName);
      } else {
        LOGGER.info("[JETRAW] JNI library file not found in default location.\n"
                  + "Trying to load from PATH.");
        System.loadLibrary("jetraw_bioformats_plugin");
      }
    } catch (UnsatisfiedLinkError exception) {
      throw new UnsatisfiedLinkError(createExceptionMessage());
    }
    LOGGER.info("[JETRAW] JNI library successfully loaded.");
    isInitialized = true;
  }

  // native JNI calls to jetraw
  public native void performDecoding(byte[] buf, int bufSize, short[] out, int outSize);

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options) throws FormatException {
    throw new UnsupportedCompressionException("[JETRAW] compression not supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options) throws FormatException, IOException {
    byte[] buf = new byte[(int) in.length()];
    in.read(buf);
    return decompress(buf, options);
  }

  @Override
  public byte[] decompress(byte[] buf, CodecOptions options) throws FormatException, UnsatisfiedLinkError {
    if (!isInitialized) {
      loadLib(getJetrawLibraryPath());
    }
    
    LOGGER.info("[JETRAW] performing decoding.");
    short[] decodedBuffer = new short[options.width*options.height];
    performDecoding(buf, buf.length, decodedBuffer, options.width*options.height);

    return convertShortArrayToByteArray(decodedBuffer, options);
  }

}
