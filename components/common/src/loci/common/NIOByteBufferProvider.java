//
// NIOByteBufferProvider.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a facade to byte buffer allocation that enables
 * <code>FileChannel.map()</code> usage on platforms where it's unlikely to
 * give us problems and heap allocation where it is. References:
 * <ul>
 *   <li>http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5092131</li>
 *   <li>http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6417205</li>
 * </ul>
 *
 * @author Chris Allan <callan at blackcat dot ca>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/NIOByteBufferProvider.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/NIOByteBufferProvider.java">SVN</a></dd></dl>
 */
public class NIOByteBufferProvider {

  // -- Constants --

  /** The minimum Java version we know is safe for memory mapped I/O. */
  public static final int MINIMUM_JAVA_VERSION = 6;

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(NIOByteBufferProvider.class);

  // -- Fields --

  /** Whether or not we are to use memory mapped I/O. */
  private static boolean useMappedByteBuffer = false;

  /** File channel to allocate or map data from. */
  private FileChannel channel;

  /** If we are to use memory mapped I/O, the map mode. */
  private MapMode mapMode;

  static {
    String osArch = System.getProperty("os.arch");
    String javaVersion = System.getProperty("java.version");
    if ("amd64".equals(osArch) || "x86_64".equals(osArch)) {
      // We're on a 64-bit platform; we're safe to use memory mapped I/O
      // without consequence.
      useMappedByteBuffer = true;
    }
    else if (osArch == null) {
      LOGGER.warn("Java Virtual Machine os.arch system property missing!");
    }
    else if (javaVersion == null) {
      LOGGER.warn("Java Virtual Machine os.arch system property missing!");
    }
    else {
      // Check if we're on a Java 1.6 VM where we're safe to use memory
      // mapped I/O without consequence regardless of architecture.
      StringTokenizer tokenizer = new StringTokenizer(javaVersion, ".");
      try {
        // Ensure we have an integer here at least
        Integer.parseInt(tokenizer.nextToken());
        // This is the actual "major" version number we want to check
        int y = Integer.parseInt(tokenizer.nextToken());
        if (y >= MINIMUM_JAVA_VERSION) {
          useMappedByteBuffer = true;
        }
        else {
          useMappedByteBuffer = false;
        }
      }
      catch (Exception e) {
        LOGGER.warn("Error parsing x.y.z Java version string.", e);
      }
    }
    LOGGER.info("Using mapped byte buffer? " + useMappedByteBuffer);
  }

  // -- Constructors --

  /**
   * Default constructor.
   * @param channel File channel to allocate or map byte buffers from.
   * @param mapMode The map mode. Required but only used if memory mapped I/O
   * is to occur.
   */
  public NIOByteBufferProvider(FileChannel channel, MapMode mapMode) {
    this.channel = channel;
    this.mapMode = mapMode;
  }

  /**
   * Allocates or maps the desired file data into memory.
   * @param bufferStartPosition The absolute position of the start of the
   * buffer.
   * @param newSize The buffer size.
   * @return A newly allocated or mapped NIO byte buffer.
   * @throws IOException If there is an issue mapping, aligning or allocating
   * the buffer.
   */
  public ByteBuffer allocate(long bufferStartPosition, int newSize)
    throws IOException {
    if (useMappedByteBuffer) {
      return allocateMappedByteBuffer(bufferStartPosition, newSize);
    }
    return allocateDirect(bufferStartPosition, newSize);
  }

  /**
   * Allocates memory and copies the desired file data into it.
   * @param bufferStartPosition The absolute position of the start of the
   * buffer.
   * @param newSize The buffer size.
   * @return A newly allocated NIO byte buffer.
   * @throws IOException If there is an issue aligning or allocating
   * the buffer.
   */
  protected ByteBuffer allocateDirect(long bufferStartPosition, int newSize)
    throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(newSize);
    channel.read(buffer, bufferStartPosition);
    return buffer;
  }

  /**
   * Memory maps the desired file data into memory.
   * @param bufferStartPosition The absolute position of the start of the
   * buffer.
   * @param newSize The buffer size.
   * @return A newly mapped NIO byte buffer.
   * @throws IOException If there is an issue mapping, aligning or allocating
   * the buffer.
   */
  protected ByteBuffer allocateMappedByteBuffer(
      long bufferStartPosition, int newSize) throws IOException {
    return channel.map(mapMode, bufferStartPosition, newSize);
  }
}
