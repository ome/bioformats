package loci.formats.services;

import java.io.IOException;

import loci.common.services.Service;
import loci.formats.FormatException;

public interface ZarrService extends Service {
  
  enum Compression {
    NONE,
    ZLIB
  }
  
  /**
   * Initializes the service for the given file path.
   * @param     file            File path with which to initialize the service.
   * @throws    IOException
   * @throws     FormatException
   */
  public void create(String file, int[] shape, int[] chunks, int pixelType, boolean isLittleEndian) throws IOException, FormatException;
  
  /**
   * Initializes the service for the given file path.
   * @param     file            File path with which to initialize the service.
   * @throws    IOException
   * @throws     FormatException
   */
  public void create(String file, int[] shape, int[] chunks, int pixelType, boolean isLittleEndian, Compression compression) throws IOException, FormatException;

  /**
   * Gets the text string for when Zarr implementation has not been found.
   */
  public String getNoZarrMsg();

  /**
   * Gets shape of Zarr as an array of dimensions.
   * @return  shape.
   */
  public int [] getShape();

  /**
   * Gets the chunk size as an array of dimensions.
   * @return  chunk size.
   */
  public int [] getChunkSize();

  /**
   * Gets the image pixel type.
   * @return                    pixel type.
   */
  public int getPixelType();

  /**
   * Closes the file.
   */
  public void close() throws IOException;

  /**
  * Reads values from the Zarr Array
  * @return     Buffer of bytes read.
  * @param      shape           int array representing the shape of each dimension
  * @param      offset          buffer for bytes.
  */
  public Object readBytes(int [] shape, int [] offset) throws FormatException, IOException;

  /**
  * Writes values to the Zarr Array
  * @param      buf            values to be written in a one dimensional array
  * @param      shape           int array representing the shape of each dimension
  * @param      x               the offset for each dimension
  */
  void saveBytes(Object data, int[] shape, int[] offset) throws FormatException, IOException;

  void open(String file) throws IOException, FormatException;

  boolean isLittleEndian();

  boolean isOpen() throws IOException;

  String getID() throws IOException;

}
