package loci.formats.services;

import java.io.IOException;
import java.nio.ByteOrder;

import com.bc.zarr.ArrayParams;
import com.bc.zarr.Compressor;
import com.bc.zarr.CompressorFactory;
import com.bc.zarr.DataType;
import com.bc.zarr.ZarrArray;

import loci.common.services.AbstractService;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import ucar.ma2.InvalidRangeException;

public class ZarrServiceImpl extends AbstractService
implements ZarrService  {
  // -- Constants --
  public static final String NO_ZARR_MSG = "JZARR is required to read Zarr files.";

  // -- Fields --
  ZarrArray zarrArray;
  String currentId;
  Compressor zlibComp = CompressorFactory.create("zlib", 8);  // 8 = compression level .. valid values 0 .. 9
  Compressor nullComp = CompressorFactory.create("null", 0);
  
  /**
   * Default constructor.
   */
  public ZarrServiceImpl() {
      checkClassDependency(com.bc.zarr.ZarrArray.class);
  }
  
  @Override
  public void open(String file) throws IOException, FormatException {
    currentId = file;
    zarrArray = ZarrArray.open(file);
  }
  
  @Override
  public void create(String file, int[] shape, int[] chunks, int pixelType, boolean isLittleEndian) throws IOException, FormatException {
    create(file, shape, chunks, pixelType, isLittleEndian, Compression.NONE);
  }
  
  @Override
  public void create(String file, int[] shape, int[] chunks, int pixelType, boolean isLittleEndian, Compression compression) throws IOException, FormatException {
    ArrayParams params = new ArrayParams();
    DataType zarrPixelType = getZarrPixelType(pixelType);
    params.shape(shape);
    params.chunks(chunks);
    params.dataType(zarrPixelType);
    params.compressor(nullComp);
    if (isLittleEndian) {
      params.byteOrder(ByteOrder.LITTLE_ENDIAN);
    }
    zarrArray = ZarrArray.create(file, params);
    currentId = file;
  }
  
  private DataType getZarrPixelType(int pixType) {
    DataType pixelType = null;
      switch(pixType) {
        case FormatTools.INT8:
          pixelType = DataType.i1;
          break;
        case FormatTools.INT16:
          pixelType = DataType.i2;
          break;
        case FormatTools.INT32:
          pixelType = DataType.i4;
          break;
        case FormatTools.UINT8:
          pixelType = DataType.u1;
          break;
        case FormatTools.UINT16:
          pixelType = DataType.u2;
          break;
        case FormatTools.UINT32:
          pixelType = DataType.u4;
          break;
        case FormatTools.FLOAT:
          pixelType = DataType.f4;
          break;
        case FormatTools.DOUBLE:
          pixelType = DataType.f8;
          break;
      }
      return(pixelType);
  }
  
  private int getOMEPixelType(DataType pixType) {
    int pixelType = -1;
      switch(pixType) {
        case i1:
          pixelType = FormatTools.INT8;
          break;
        case i2:
          pixelType = FormatTools.INT16;
          break;
        case i4:
          pixelType = FormatTools.INT32;
          break;
        case u1:
          pixelType = FormatTools.UINT8;
          break;
        case u2:
          pixelType = FormatTools.UINT16;
          break;
        case u4:
          pixelType = FormatTools.UINT32;
          break;
        case f4:
          pixelType = FormatTools.FLOAT;
          break;
        case f8:
          pixelType = FormatTools.DOUBLE;
          break;
      default:
        break;
      }
      return(pixelType);
  }

  @Override
  public String getNoZarrMsg() {
    return NO_ZARR_MSG;
  }

  @Override
  public int[] getShape() {
    if (zarrArray != null) return zarrArray.getShape();
    return null;
  }

  @Override
  public int[] getChunkSize() {
    if (zarrArray != null) return zarrArray.getChunks();
    return null;
  }

  @Override
  public int getPixelType() {
    if (zarrArray != null) return getOMEPixelType(zarrArray.getDataType());
    return 0;
  }
  
  @Override
  public boolean isLittleEndian() {
    if (zarrArray != null) return (zarrArray.getByteOrder() == ByteOrder.LITTLE_ENDIAN);
    return false;
  }

  @Override
  public void close() throws IOException {
    zarrArray = null;
    currentId = null;
  }
  
  @Override
  public boolean isOpen() throws IOException {
    return (zarrArray != null && currentId != null);
  }
  
  @Override
  public String getID() throws IOException {
    return currentId;
  }

  @Override
  public Object readBytes(int[] shape, int[] offset) throws FormatException, IOException {
    if (zarrArray != null) {
      try {
        return zarrArray.read(shape, offset);
      } catch (InvalidRangeException e) {
        throw new FormatException(e);
      }
    }
    else throw new IOException("No Zarr file opened");
  }

  @Override
  public void saveBytes(Object data, int[] shape, int[] offset) throws FormatException, IOException {
    if (zarrArray != null) {
      try {
        zarrArray.write(data, shape, offset);
      } catch (InvalidRangeException e) {
        throw new FormatException(e);
      }
    }
    else throw new IOException("No Zarr file opened");
  }
}
