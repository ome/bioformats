package com.sun.jimi.util.zip;

/**
 * <B>Checksum</B> is an interface that all data checksum objects
 * (such as Adler32 and CRC32) should implement.
 */
public interface Checksum
{
  /**
   * Returns the current value of the checksum
   */
  public abstract long getValue();
  /**
   * Updates the checksum with the given byte
   * @param b the byte of data to update the checksum with
   */
  public abstract void update(int b);
  /**
   * Updates the checksum with the data in the given array
   * @param b the array containing the data
   * @param offset the index of the first byte of data
   * @param length the number of data bytes in the array
   */
  public abstract void update(byte[] b, int offset, int length);
  /**
   * Resets the checksum to the initial value
   */
  public abstract void reset();
}
