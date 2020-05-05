package loci.formats.out;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.UUID;

import loci.common.Constants;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.ZarrServiceImpl;
import ome.xml.meta.OMEXMLMetadataRoot;
import loci.formats.services.OMEXMLService;
import loci.formats.services.ZarrService;


public class ZarrWriter extends FormatWriter {

  // -- Fields --
  private ZarrService zarrService = null;
  private final int CHUNK_DEFAULT = 32;
  private OMEXMLService service;
  private OMEXMLMetadata omeMeta;
  
  
  public ZarrWriter() {
    this("Zarr", new String[] {"zarr"});
  }
 
  public ZarrWriter(String format, String[] exts) {
    super(format, exts);
  }
  
  
  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if(zarrService != null) {
      checkParams(no, buf, x, y, w, h);
      MetadataRetrieve meta = getMetadataRetrieve();
      MetadataTools.verifyMinimumPopulated(meta, series);
      
      int zSize = meta.getPixelsSizeZ(series).getValue().intValue();
      int cSize = meta.getPixelsSizeC(series).getValue().intValue();
      int tSize = meta.getPixelsSizeT(series).getValue().intValue();
      String order = meta.getPixelsDimensionOrder(series).toString();
      int imageCount = getPlaneCount();
      int[] coordinates = FormatTools.getZCTCoords(order, zSize, cSize, tSize, imageCount, no);
      int [] shape = {w, h, 1, 1, 1};

      int type = FormatTools.pixelTypeFromString(meta.getPixelsType(0).toString());
      int bpp = FormatTools.getBytesPerPixel(type);
      
      int [] offsets = {x, y, coordinates[0], coordinates[1], coordinates[2]};
      if (bpp==1) {
        zarrService.saveBytes(buf, shape, offsets);
      }
      else if (bpp==2) {
        ByteBuffer bb = ByteBuffer.wrap(buf);
        ShortBuffer sb = bb.asShortBuffer();
        short[] image = new short[w * h];
        for (int x_i=0; x_i < w; x_i++) {
            for (int y_i=0; y_i < h; y_i++) {
                short value = sb.get(y_i*w + x_i);
                image[y_i*w + x_i] = value;
            }
        }
        zarrService.saveBytes(image, shape, offsets);
      }
      else if (bpp==4) {
        ByteBuffer bb = ByteBuffer.wrap(buf);
        IntBuffer sb = bb.asIntBuffer();
        int[] image = new int[w * h];
        for (int x_i=0; x_i < w; x_i++) {
            for (int y_i=0; y_i < h; y_i++) {
                int value = sb.get(y_i*w + x_i);
                image[y_i*w + x_i] = value;
            }
        }
        zarrService.saveBytes(image, shape, offsets);
      }
      else {
        throw new FormatException("CellH5Writer: Pixel type not supported");
      }
    }
  }
  
  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() {
    return true;
  }
  
  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    if(zarrService == null) {
      try {
        ServiceFactory factory = new ServiceFactory();
        zarrService = factory.getInstance(ZarrService.class);
      }
      catch (DependencyException e) {
        throw new FormatException(ZarrServiceImpl.NO_ZARR_MSG, e);
      }
    }
    if(zarrService != null) {
      if (!zarrService.isOpen() && zarrService.getID() != id) {
        MetadataRetrieve meta = getMetadataRetrieve();
        MetadataTools.verifyMinimumPopulated(meta, series);
        int x = meta.getPixelsSizeX(series).getValue().intValue();
        int y = meta.getPixelsSizeY(series).getValue().intValue();
        int z = meta.getPixelsSizeZ(series).getValue().intValue();
        int c = meta.getPixelsSizeC(series).getValue().intValue();
        int t = meta.getPixelsSizeT(series).getValue().intValue();
        int [] shape = {x, y, z, c, t};
        int chunkX = getTileSizeX();
        int chunkY = getTileSizeY();
        boolean usingTiling = chunkX > 0 && chunkY > 0;
        if (!usingTiling) {
          chunkX = CHUNK_DEFAULT;
          chunkY = CHUNK_DEFAULT;
        }
        int [] chunks = {chunkX, chunkY, 1, 1, 1};
        int pixelType = FormatTools.pixelTypeFromString(
            meta.getPixelsType(series).toString());
   
        zarrService.create(id, shape, chunks, pixelType, !meta.getPixelsBigEndian(series));
      }
    }
  }
  
  /* @see loci.formats.IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    try {
      if (currentId != null) {
        setupServiceAndMetadata();
        Location zarrFolder = new Location( currentId ).getAbsoluteFile();
        String name = currentId.substring(currentId.lastIndexOf("/")+1, currentId.indexOf(".zarr"));
        Location omeMetaFile = new Location( zarrFolder, name+".ome.xml" );
        String companionXML = getOMEXML(omeMetaFile.getAbsolutePath());
        PrintWriter out = new PrintWriter(omeMetaFile.getAbsolutePath(), Constants.ENCODING);
        out.println(XMLTools.indentXML(companionXML, true));
        out.close();
      }
    }
    catch (DependencyException | ServiceException | FormatException | IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
    finally {
      super.close();
    }
  }
    
  private String getOMEXML(String file) throws FormatException, IOException {
    // generate UUID and add to OME element
    String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
    omeMeta.setUUID(uuid);

    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omeMeta.getRoot();
    root.setCreator(FormatTools.CREATOR);

    String xml;
    try {
      xml = service.getOMEXML(omeMeta);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }
    return xml;
  }
    
  private String getUUID(String filename) {
    String uuid = UUID.randomUUID().toString();
    return uuid;
  }
  
  private void setupServiceAndMetadata() throws DependencyException, ServiceException {
    // extract OME-XML string from metadata object
    MetadataRetrieve retrieve = getMetadataRetrieve();

    ServiceFactory factory = new ServiceFactory();
    service = factory.getInstance(OMEXMLService.class);
    OMEXMLMetadata originalOMEMeta = service.getOMEMetadata(retrieve);
    originalOMEMeta.resolveReferences();

    String omexml = service.getOMEXML(originalOMEMeta);
    omeMeta = service.createOMEXMLMetadata(omexml);
  }
}
