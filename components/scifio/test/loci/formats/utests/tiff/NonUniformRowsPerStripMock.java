
package loci.formats.utests.tiff;

import java.io.IOException;

import loci.formats.FormatException;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/NonUniformRowsPerStripMock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/NonUniformRowsPerStripMock.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class NonUniformRowsPerStripMock extends BaseTiffMock {

  public NonUniformRowsPerStripMock() throws FormatException, IOException {
    super();
  }

  @Override
  public int[] getRowsPerStrip() {
    return new int[] { 1, 2, 3 };
  }

}
