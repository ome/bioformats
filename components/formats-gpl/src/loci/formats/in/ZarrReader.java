package loci.formats.in;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.ZarrServiceImpl;
import ome.xml.meta.MetadataConverter;
import loci.formats.services.OMEXMLService;
import loci.formats.services.ZarrService;


public class ZarrReader extends FormatReader  {

  private transient ZarrService zarrService;
  
  public ZarrReader() {
    super("Zarr", "zarr");
    suffixSufficient = true;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }
  
  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    Location zarrFolder = new Location(name).getParentFile();
    if (zarrFolder != null && zarrFolder.exists() && zarrFolder.getAbsolutePath().indexOf(".zarr") > 0) {
      return true;
    }
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return zarrService.getChunkSize()[1];
  }
  
  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return zarrService.getChunkSize()[0];
  }
  
  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    Location zarrFolder = new Location( id ).getParentFile();
    String zarrPath = zarrFolder.getAbsolutePath();
    String name = zarrPath.substring(zarrPath.lastIndexOf("/")+1, zarrPath.indexOf(".zarr"));
    Location omeMetaFile = new Location( zarrFolder, name+".ome.xml" );
    if ( !omeMetaFile.exists() ) { throw new IOException( "Could not find " + omeMetaFile + " in folder." ); }
    
    /*
     * Open OME metadata file
     */

    RandomAccessInputStream measurement =
      new RandomAccessInputStream(omeMetaFile.getAbsolutePath());
    Document omeDocument = null;
    try {
      omeDocument = XMLTools.parseDOM(measurement);
    }
    catch (ParserConfigurationException e) {
      throw new IOException(e);
    }
    catch (SAXException e) {
      throw new IOException(e);
    }
    finally {
      measurement.close();
    }
    omeDocument.getDocumentElement().normalize();
    
    OMEXMLService service = null;
    String xml = null;
    try
    {
      xml = XMLTools.getXML( omeDocument );
    }
    catch (TransformerException e2 )
    {
      LOGGER.debug( "", e2 );
    }
    OMEXMLMetadata omexmlMeta = null;
    try
    {
      service = new ServiceFactory().getInstance( OMEXMLService.class );
      omexmlMeta = service.createOMEXMLMetadata( xml );
    }
    catch (DependencyException | ServiceException | NullPointerException e1 )
    {
      LOGGER.debug( "", e1 );
    }

    int numDatasets = omexmlMeta.getImageCount();

    int oldSeries = getSeries();
    core.clear();
    for (int i=0; i<numDatasets; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      setSeries(i);

      Integer w = omexmlMeta.getPixelsSizeX(i).getValue();
      Integer h = omexmlMeta.getPixelsSizeY(i).getValue();
      Integer t = omexmlMeta.getPixelsSizeT(i).getValue();
      Integer z = omexmlMeta.getPixelsSizeZ(i).getValue();
      Integer c = omexmlMeta.getPixelsSizeC(i).getValue();
      if (w == null || h == null || t == null || z == null | c == null) {
        throw new FormatException("Image dimensions not found");
      }

      Boolean endian = null;
      String pixType = omexmlMeta.getPixelsType(i).toString();
      ms.dimensionOrder = omexmlMeta.getPixelsDimensionOrder(i).toString();
      ms.sizeX = w.intValue();
      ms.sizeY = h.intValue();
      ms.sizeT = t.intValue();
      ms.sizeZ = z.intValue();
      ms.sizeC = c.intValue();
      ms.imageCount = getSizeZ() * getSizeC() * getSizeT();
      ms.littleEndian = endian == null ? false : !endian.booleanValue();
      ms.rgb = false;
      ms.interleaved = false;
      ms.indexed = false;
      ms.falseColor = true;
      ms.pixelType = FormatTools.pixelTypeFromString(pixType);
      ms.orderCertain = true;
      if (omexmlMeta.getPixelsSignificantBits(i) != null) {
        ms.bitsPerPixel = omexmlMeta.getPixelsSignificantBits(i).getValue();
      }
    }
    setSeries(oldSeries);
    final MetadataStore store = makeFilterMetadata();
    MetadataConverter.convertMetadata( omexmlMeta, store );
    MetadataTools.populatePixels( store, this, true );

    initializeZarrService(zarrPath);
  }

  /* @see loci.formats.FormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    try {
      initializeZarrService(currentId);
    }
    catch (FormatException e) {
      throw new IOException(e);
    }
  }

  // -- Helper methods --

  private void initializeZarrService(String id) throws IOException, FormatException {
    try {
      ServiceFactory factory = new ServiceFactory();
      zarrService = factory.getInstance(ZarrService.class);
      zarrService.open(id);
    } catch (DependencyException e) {
      throw new MissingLibraryException(ZarrServiceImpl.NO_ZARR_MSG, e);
    }
  }
  
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    int[] coordinates = getZCTCoords(no);
    int [] shape = {w, h, 1, 1, 1};
    int [] offsets = {x, y, coordinates[0], coordinates[1], coordinates[2]};
    Object image = zarrService.readBytes(shape, offsets);

    boolean little = zarrService.isLittleEndian();
    int bpp = FormatTools.getBytesPerPixel(zarrService.getPixelType());
    if (image instanceof byte[]) {
      buf = (byte []) image;
    }
    else if (image instanceof short[]) {
      short[] data = (short[]) image;
      for (int row = 0; row < h; row++) {
        int base = row * w * bpp;
        for (int i = 0; i < w; i++) {
          DataTools.unpackBytes(data[(row * w) + i + x], buf, base + 2 * i, 2, little);
        }
      }
    }
    else if (image instanceof int[]) {
      int[] data = (int[]) image;
      for (int row = 0; row < h; row++) {
        int base = row * w * bpp;
        for (int i = 0; i < w; i++) {
          DataTools.unpackBytes(data[(row * w) + i + x], buf, base + 4 * i, 4, little);
        }
      }
    }
    else if (image instanceof float[]) {
      float[] data = (float[]) image;
      for (int row = 0; row < h; row++) {
        int base = row * w * bpp;
        for (int i = 0; i < w; i++) {
          int value = Float.floatToIntBits(data[(row * w) + i + x]);
          DataTools.unpackBytes(value, buf, base + 4 * i, 4, little);
        }
      }
    }
    else if (image instanceof double[]) {
      double[] data = (double[]) image;
      for (int row = 0; row < h; row++) {
        int base = row * w * bpp;
        for (int i = 0; i < w; i++) {
          long value = Double.doubleToLongBits(data[(row * w) + i + x]);
          DataTools.unpackBytes(value, buf, base + 8 * i, 8, little);
        }
      }
    }
    return buf;
  }

}
