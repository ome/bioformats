//
// ITKBridgePipes.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.itk;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ITKBridgePipes is a Java console application that listens for "commands"
 * on stdin and issues results on stdout. It is used by the pipes version of
 * the ITK Bio-Formats plugin to read image files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/itk/ITKBridgePipes.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/itk/ITKBridgePipes.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Mark Hiner
 * @author Curtis Rueden
 */
public class ITKBridgePipes {

  private static final String HASH_PREFIX = "hash:";

  private Hashtable<Integer, IFormatReader> readers =
    new Hashtable<Integer, IFormatReader>();

  /** Enters an input loop, waiting for commands, until EOF is reached. */
  public void waitForInput() throws FormatException, IOException {
    final BufferedReader in =
      new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      final String line = in.readLine(); // blocks until a line is read
      if (line == null) break; // eof
      executeCommand(line);
    }
    in.close();
  }

  /**
   * Executes the given command line. The following commands are supported:
   * <ul>
   * <li>info</li> - Dumps image metadata
   * <li>read</li> - Dumps image pixels
   * <li>canRead</li> - Tests whether the given file path can be parsed
   * </ul>
   */
  public boolean executeCommand(String commandLine)
    throws FormatException, IOException
  {
    final int space = commandLine.indexOf(" ");
    if (space < 0) {
      System.err.println("Invalid command line: " + commandLine);
      return false;
    }

    final String command = commandLine.substring(0, space).trim();
    final String filePath = commandLine.substring(space + 1).trim();

    if (command.equals("info")) {
       return readImageInfo(filePath);
    }
    else if (command.equals("read")) {
      return read(filePath);
    }
    else if (command.equals("canRead")) {
      return canRead(filePath);
    }
    else {
      System.err.println("Unknown command: " + command);
    }
    return false;
  }

  /**
   * Reads image metadata from the given file path, dumping the resultant
   * values to stdout in a specific order (which we have not documented here
   * because we are lazy).
   *
   * @param filePath a path to a file on disk, or a hash token for an
   *   initialized reader (beginning with "hash:") as given by a call to "info"
   *   earlier.
   */
  public boolean readImageInfo(String filePath)
    throws FormatException, IOException
  {
    final IFormatReader reader = createReader(filePath);

    final MetadataStore store = reader.getMetadataStore();
    IMetadata meta = (IMetadata) store;

   // now print the informations

   // little endian?
    System.out.println(reader.isLittleEndian()? 1:0);

    // component type
    // set ITK component type
    int pixelType = reader.getPixelType();
    int itkComponentType;
    switch (pixelType) {
      case FormatTools.UINT8:
        itkComponentType = 1;
        break;
      case FormatTools.INT8:
        itkComponentType = 2;
        break;
      case FormatTools.UINT16:
        itkComponentType = 3;
        break;
      case FormatTools.INT16:
        itkComponentType = 4;
        break;
      case FormatTools.UINT32:
        itkComponentType = 5;
        break;
      case FormatTools.INT32:
        itkComponentType = 6;
        break;
      case FormatTools.FLOAT:
        itkComponentType = 9;
        break;
      case FormatTools.DOUBLE:
        itkComponentType = 10;
        break;
      default:
        itkComponentType = 0;
    }
    System.out.println(itkComponentType);

    // x, y, z, t, c
    System.out.println(reader.getSizeX());
    System.out.println(reader.getSizeY());
    System.out.println(reader.getSizeZ());
    System.out.println(reader.getSizeT());
    System.out.println(reader.getEffectiveSizeC()); // reader.getSizeC()

    // number of components
    System.out.println(reader.getRGBChannelCount());

    // spacing
    System.out.println(meta.getPixelsPhysicalSizeX(0));
    System.out.println(meta.getPixelsPhysicalSizeY(0));
    System.out.println(meta.getPixelsPhysicalSizeZ(0));
    System.out.println(meta.getPixelsTimeIncrement(0));
    // should we give something more useful for channel spacing?
    System.out.println(1.0);

    // reader hash code
    final int hashCode = reader.hashCode();
    System.out.println(HASH_PREFIX + hashCode);
    readers.put(hashCode, reader);

    System.out.flush();
    return true;
  }

  /**
   * Reads image pixels from the given file path, dumping the resultant binary
   * stream to stdout.
   *
   * @param filePath a path to a file on disk, or a hash token for an
   *   initialized reader (beginning with "hash:") as given by a call to "info"
   *   earlier. Using a hash token eliminates the need to initialize the file a
   *   second time with a fresh reader object. Regardless, after reading the
   *   file, the reader closes the file handle, and invalidates its hash token.
   */
  public boolean read(String filePath)
    throws FormatException, IOException
  {
    final IFormatReader reader = createReader(filePath);

    BufferedOutputStream out = new BufferedOutputStream(System.out);
    for (int c = 0; c < reader.getSizeC(); c++) {
      for (int t = 0; t < reader.getSizeT(); t++) {
        for (int z=0; z < reader.getSizeZ(); z++) {
          byte[] image = reader.openBytes(reader.getIndex(z, c, t));
          out.write(image);
        }
      }
    }

    out.close();
    System.out.flush();

    // close file handle, and invalidate hash token, if any
    reader.close();
    readers.remove(reader.hashCode());

    return true;
  }

  /** Tests whether the given file path can be parsed by Bio-Formats. */
  public boolean canRead(String filePath)
    throws FormatException, IOException
  {
    final IFormatReader reader = createReader(null);
    final boolean canRead = reader.isThisType(filePath);
    System.out.println(canRead);
    System.out.flush();
    return true;
  }

  private IFormatReader createReader(final String filePath)
    throws FormatException, IOException
  {
    IFormatReader reader = null;
    if (filePath.startsWith(HASH_PREFIX)) {
      // file path is actually a reader hash code; reuse it
      try {
        final int hashCode = Integer.parseInt(filePath.substring(5));
        reader = readers.get(hashCode);
      }
      catch (NumberFormatException exc) {
        // invalid hash code; ignore and continue
      }
    }
    if (reader == null) {
      // no hash code; initialize a fresh reader
      reader = new ChannelSeparator();

      reader.setMetadataFiltered(true);
      reader.setOriginalMetadataPopulated(true);
      final MetadataStore store = MetadataTools.createOMEXMLMetadata();
      if (store == null) System.err.println("OME-Java library not found.");
      else reader.setMetadataStore(store);
      reader.setMetadataOptions(
        new DefaultMetadataOptions(MetadataLevel.MINIMUM));

      // avoid grouping all the .lsm when a .mdb is there
      reader.setGroupFiles(false);

      if (filePath != null) {
        reader.setId(filePath);
        reader.setSeries(0);
      }
    }

    return reader;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ITKBridgePipes().waitForInput();
  }

}
