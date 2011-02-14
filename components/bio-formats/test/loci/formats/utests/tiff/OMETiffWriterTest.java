//
// OMETiffWriterTest.java
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
package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import loci.common.RandomAccessInputStream;
import loci.common.services.ServiceFactory;
import loci.formats.ImageWriter;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/tiff/OMETiffWriterTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/tiff/OMETiffWriterTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author callan
 */
public class OMETiffWriterTest {

  public static final int SIZE_Z = 100;

  public static final int SIZE_C = 1;

  public static final int SIZE_T = 20;

  private static final byte[] buf = new byte[] {
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
  };

  private File target;

  private FileHandleTrackingAppender app = new FileHandleTrackingAppender();

  private OMEXMLMetadata ms;

  @BeforeClass
  public void setUp() throws Exception {
    target = File.createTempFile("OMETiffWriterTest", ".ome.tiff");
    Logger l = Logger.getLogger(RandomAccessInputStream.class);
    l.setLevel(Level.TRACE);
    l.addAppender(app);

    ServiceFactory sf = new ServiceFactory();
    OMEXMLService service = sf.getInstance(OMEXMLService.class);
    ms = service.createOMEXMLMetadata();
    ms.setImageID("Image:1", 0);
    ms.setPixelsID("Pixels:1", 0);
    ms.setPixelsDimensionOrder(DimensionOrder.XYZCT, 0);
    ms.setPixelsSizeX(new PositiveInteger(4), 0);
    ms.setPixelsSizeY(new PositiveInteger(4), 0);
    ms.setPixelsSizeZ(new PositiveInteger(SIZE_Z), 0);
    ms.setPixelsSizeC(new PositiveInteger(SIZE_C), 0);
    ms.setPixelsSizeT(new PositiveInteger(SIZE_T), 0);
    ms.setPixelsType(PixelType.UINT8, 0);
    ms.setPixelsBinDataBigEndian(true, 0, 0);
    ms.setChannelID("Channel:1", 0, 0);
    ms.setChannelSamplesPerPixel(new PositiveInteger(1), 0, 0);
  }

  @AfterClass
  public void tearDown() throws Exception {
    target.delete();
  }

  @Test
  public void testFileHandleClosure() throws Exception {
    ImageWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(ms);
    writer.setId(target.getAbsolutePath());
    for (int i = 0; i < (SIZE_Z * SIZE_C * SIZE_T); i++) {
      writer.saveBytes(i, buf);
    }
    writer.close();
    for (Entry<Long, Boolean> entry : app.map.entrySet()) {
      if (!entry.getValue()) {
        fail("RandomAccessInputStream " + entry.getKey() + " not closed!");
      }
    }
    System.err.println("Tracker length: " + app.map.size());
  }

  class FileHandleTrackingAppender implements Appender {

    public Map<Long, Boolean> map = new HashMap<Long, Boolean>(); 

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#addFilter(org.apache.log4j.spi.Filter)
     */
    public void addFilter(Filter arg0) {
      // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#clearFilters()
     */
    public void clearFilters() {
      // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#close()
     */
    public void close() {
      // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#doAppend(org.apache.log4j.spi.LoggingEvent)
     */
    public void doAppend(LoggingEvent event) {
      String[] result = event.getMessage().toString().split("\\s");
      if (result.length == 3) {
        Long hashCode = Long.parseLong(result[1]);
        Boolean closed = "CLOSE".equals(result[2]);
        map.put(hashCode, closed);
      }
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#getErrorHandler()
     */
    public ErrorHandler getErrorHandler() {
      // TODO Auto-generated method stub
      return null;
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#getFilter()
     */
    public Filter getFilter() {
      // TODO Auto-generated method stub
      return null;
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#getLayout()
     */
    public Layout getLayout() {
      // TODO Auto-generated method stub
      return null;
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#getName()
     */
    public String getName() {
      // TODO Auto-generated method stub
      return null;
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout() {
      // TODO Auto-generated method stub
      return false;
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#setErrorHandler(org.apache.log4j.spi.ErrorHandler)
     */
    public void setErrorHandler(ErrorHandler handler) {
      // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#setLayout(org.apache.log4j.Layout)
     */
    public void setLayout(Layout layout) {
      // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#setName(java.lang.String)
     */
    public void setName(String name) {
      // TODO Auto-generated method stub
    }

  }
}
