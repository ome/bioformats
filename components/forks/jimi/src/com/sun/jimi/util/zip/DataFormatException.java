package com.sun.jimi.util.zip;

/**
 * The DataFormatException class is thrown whenever there is an
 * error in a compressed data stream.
 */
public class DataFormatException extends java.io.IOException
{
  /**
   * Constructs a new DataFormatException object
   */
  public DataFormatException()
  {
    super();
  }

  /**
   * Constructs a new DataFormatException object
   *
   * @param s a string describing the exception
   */
  public DataFormatException(String s)
  {
    super(s);
  }
}
