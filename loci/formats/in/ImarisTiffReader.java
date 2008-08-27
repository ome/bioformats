//
// ImarisTiffReader.java
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

package loci.formats.in;

import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ImarisTiffReader is the file format reader for
 * Bitplane Imaris 3 files (TIFF variant).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ImarisTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ImarisTiffReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ImarisTiffReader extends BaseTiffReader {

  // -- Constructor --

  /** Constructs a new Imaris TIFF reader. */
  public ImarisTiffReader() {
    super("Bitplane Imaris 3 (TIFF)", "ims");
    blockCheckLen = 1024;
    suffixSufficient = false;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ImarisTiffReader.initFile(" + id + ")");
    super.initFile(id);

    in = new RandomAccessStream(id);
    if (in.readShort() == 0x4949) in.order(true);

    ifds = TiffTools.getIFDs(in);
    if (ifds == null) throw new FormatException("No IFDs found");

    // hack up the IFDs
    //
    // Imaris TIFFs store a thumbnail in the first IFD; each of the remaining
    // IFDs defines a stack of tiled planes.

    status("Verifying IFD sanity");

    Vector tmp = new Vector();

    for (int i=1; i<ifds.length; i++) {
      long[] byteCounts = TiffTools.getIFDLongArray(ifds[i],
        TiffTools.TILE_BYTE_COUNTS, false);
      long[] offsets = TiffTools.getIFDLongArray(ifds[i],
        TiffTools.TILE_OFFSETS, false);

      for (int j=0; j<byteCounts.length; j++) {
        Hashtable t = (Hashtable) ifds[i].clone();
        TiffTools.putIFDValue(t, TiffTools.TILE_BYTE_COUNTS, byteCounts[j]);
        TiffTools.putIFDValue(t, TiffTools.TILE_OFFSETS, offsets[j]);
        tmp.add(t);
      }
    }

    String comment = TiffTools.getComment(ifds[0]);

    status("Populating metadata");

    core[0].sizeC = ifds.length - 1;
    core[0].sizeZ = tmp.size() / getSizeC();
    core[0].sizeT = 1;
    core[0].sizeX = (int) TiffTools.getImageWidth(ifds[1]);
    core[0].sizeY = (int) TiffTools.getImageLength(ifds[1]);

    ifds = (Hashtable[]) tmp.toArray(new Hashtable[0]);
    core[0].imageCount = getSizeC() * getSizeZ();
    core[0].inputOrder = "XYZCT";
    core[0].interleaved = false;
    core[0].rgb = getImageCount() != getSizeZ() * getSizeC() * getSizeT();
    core[0].pixelType = getPixelType(ifds[0]);

    status("Parsing comment");

    // likely an INI-style comment, although we can't be sure

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    int[] channelIndexes = new int[3];

    if (comment != null && comment.startsWith("[")) {
      // parse key/value pairs
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int equals = line.indexOf("=");
        if (equals < 0) continue;
        String key = line.substring(0, equals).trim();
        String value = line.substring(equals + 1).trim();
        addMeta(key, value);

        if (key.equals("Description")) {
          store.setImageDescription(value, 0);
        }
        else if (key.equals("LSMEmissionWavelength") && !value.equals("0")) {
          if (channelIndexes[1] < getSizeC()) {
            store.setLogicalChannelEmWave(new Integer(value), 0,
              channelIndexes[1]++);
          }
        }
        else if (key.equals("LSMExcitationWavelength") && !value.equals("0")) {
          if (channelIndexes[2] < getSizeC()) {
            store.setLogicalChannelExWave(new Integer(value), 0,
              channelIndexes[2]++);
          }
        }
        else if (key.equals("Name") && !currentId.endsWith(value)) {
          if (channelIndexes[0] < getSizeC()) {
            store.setLogicalChannelName(value, 0, channelIndexes[0]++);
          }
        }
        else if (key.equals("RecordingDate")) {
          value = value.replaceAll(" ", "T");
          store.setImageCreationDate(value.substring(0, value.indexOf(".")), 0);
        }
      }
      metadata.remove("Comment");
    }

    store.setImageName("", 0);
    MetadataTools.populatePixels(store, this);
  }

}
