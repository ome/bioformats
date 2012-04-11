
package loci.formats.utests.tiff;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDType;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/RGBTiffMock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/RGBTiffMock.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class RGBTiffMock extends BaseTiffMock {

  public RGBTiffMock() throws FormatException, IOException {
    super();
  }

  @Override
  public int[] getBitsPerSample() {
    return new int[] { 8, 8, 8 };
  }

  @Override
  public int getSamplesPerPixel() {
    return 3;
  }

}
