//
// JimiJPEGReader.java
//

package loci.formats.in;

import java.io.IOException;
import java.util.Hashtable;

import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JimiService;

/**
 * Reader for decoding JPEG images using JIMI.
 * This reader is useful for reading very large JPEG images, as it supports
 * tile-based access.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JimiJPEGReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JimiJPEGReader.java;hb=HEAD">Gitweb</a></dd></dl>
 * @author Melissa Linkert melissa at glencoesoftware.com
 */

public class JimiJPEGReader extends FormatReader {

  // -- Fields --

  private JimiService service;

  // -- Constructor --

  public JimiJPEGReader() {
    super("JIMI JPEG", new String[] {"jpg", "jpeg"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int c = getRGBChannelCount();

    for (int ty=y; ty<y+h; ty++) {
      byte[] scanline = service.getScanline(ty);
      System.arraycopy(scanline, c * x, buf, (ty - y) * c * w, c * w);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (service != null) {
        service.close();
      }
      service = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(JimiService.class);
      service.initialize(id);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException("Could not find Jimi library", de);
    }

    core[0].interleaved = true;
    core[0].littleEndian = false;

    core[0].sizeX = service.getWidth();
    core[0].sizeY = service.getHeight();
    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].sizeC = service.getScanline(0).length / getSizeX();
    core[0].rgb = getSizeC() > 1;
    core[0].imageCount = 1;
    core[0].pixelType = FormatTools.UINT8;
    core[0].dimensionOrder = "XYCZT";
    core[0].metadataComplete = true;
    core[0].indexed = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

}
