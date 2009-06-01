//
// ImarisTiffReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.TiffTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ImarisTiffReader is the file format reader for
 * Bitplane Imaris 3 files (TIFF variant).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ImarisTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ImarisTiffReader.java">SVN</a></dd></dl>
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
    debug("ImarisTiffReader.initFile(" + id + ")");
    super.initFile(id);

    in = new RandomAccessInputStream(id);
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
    core[0].dimensionOrder = "XYZCT";
    core[0].interleaved = false;
    core[0].rgb = getImageCount() != getSizeZ() * getSizeC() * getSizeT();
    core[0].pixelType = TiffTools.getPixelType(ifds[0]);

    status("Parsing comment");

    // likely an INI-style comment, although we can't be sure

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    String description = null, creationDate = null;
    Vector emWave = new Vector();
    Vector exWave = new Vector();
    Vector channelNames = new Vector();

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
          description = value;
        }
        else if (key.equals("LSMEmissionWavelength") && !value.equals("0")) {
          emWave.add(new Integer(value));
        }
        else if (key.equals("LSMExcitationWavelength") && !value.equals("0")) {
          exWave.add(new Integer(value));
        }
        else if (key.equals("Name") && !currentId.endsWith(value)) {
          channelNames.add(value);
        }
        else if (key.equals("RecordingDate")) {
          value = value.replaceAll(" ", "T");
          creationDate = value.substring(0, value.indexOf("."));
        }
      }
      metadata.remove("Comment");
    }

    // populate Image data
    store.setImageDescription(description, 0);
    store.setImageCreationDate(creationDate, 0);

    // populate LogicalChannel data
    for (int i=0; i<emWave.size(); i++) {
      store.setLogicalChannelEmWave((Integer) emWave.get(i), 0, i);
      store.setLogicalChannelExWave((Integer) exWave.get(i), 0, i);
      store.setLogicalChannelName((String) channelNames.get(i), 0, i);
    }
  }

}
