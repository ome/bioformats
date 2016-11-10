/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

/**
 * Configuration object for readers and writers.
 */
public interface MetadataOptions {

  /**
   * Set property {@code name} to {@code value}.
   * If {@code value} is {@code null} the property will be removed.
   *
   * @param name property name.
   * @param value property value.
   * @throws IllegalArgumentException if {@code name} is {@code null}.
   */
  public void set(String name, String value);

  /**
   * Get the value of property {@code name}.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value, or {@code defaultValue} if the property
   * does not exist.
   */
  public String get(String name, String defaultValue);

  /**
   * Set property {@code name} to enumerated type {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public <T extends Enum<T>> void setEnum(String name, T value);

  /**
   * Get the value of property {@code name} as an enumerated type.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as an enumerated type, or {@code
   * defaultValue} if the property does not exist.
   * @throws IllegalArgumentException if mapping is illegal for the type
   * provided
   */
  public <T extends Enum<T>> T getEnum(String name, T defaultValue);

  /**
   * Set property {@code name} to boolean {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setBoolean(String name, boolean value);

  /**
   * Get the value of property {@code name} as a boolean.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a boolean, or {@code defaultValue} if
   * the property does not exist.
   * @throws IllegalArgumentException if value does not represent a boolean.
   */
  public boolean getBoolean(String name, boolean defaultValue);

  /**
   * Set property {@code name} to char {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setChar(String name, char value);

  /**
   * Get the value of property {@code name} as a char.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a char, or {@code defaultValue} if
   * the property does not exist.
   * @throws IllegalArgumentException if value's length is not 1.
   */
  public char getChar(String name, char defaultValue);

  /**
   * Set property {@code name} to byte {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setByte(String name, byte value);

  /**
   * Get the value of property {@code name} as a byte.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a byte, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a byte.
   */
  public byte getByte(String name, byte defaultValue);

  /**
   * Set property {@code name} to short {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setShort(String name, short value);

  /**
   * Get the value of property {@code name} as a short.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a short, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a short.
   */
  public short getShort(String name, short defaultValue);

  /**
   * Set property {@code name} to int {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setInt(String name, int value);

  /**
   * Get the value of property {@code name} as an int.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as an int, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent an int.
   */
  public int getInt(String name, int defaultValue);

  /**
   * Set property {@code name} to long {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setLong(String name, long value);

  /**
   * Get the value of property {@code name} as a long.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a long, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a long.
   */
  public long getLong(String name, long defaultValue);

  /**
   * Set property {@code name} to float {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setFloat(String name, float value);

  /**
   * Get the value of property {@code name} as a float.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a float, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a float.
   */
  public float getFloat(String name, float defaultValue);

  /**
   * Set property {@code name} to double {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setDouble(String name, double value);

  /**
   * Get the value of property {@code name} as a double.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a double, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a double.
   */
  public double getDouble(String name, double defaultValue);

  /**
   * Set property {@code name} to Class {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setClass(String name, Class<?> value);

  /**
   * Get the value of property {@code name} as a class.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a class, or {@code defaultValue} if
   * the property does not exist.
   * @throws ClassNotFoundException if value does not map to an existing class.
   */
  public Class<?> getClass(String name, Class<?> defaultValue)
      throws ClassNotFoundException;

  /**
   * Set the metadata level.
   *
   * @param level a {@link loci.formats.in.MetadataLevel}.
   */
  void setMetadataLevel(MetadataLevel level);

  /**
   * Get the configured metadata level.
   *
   * @return the configured {@link loci.formats.in.MetadataLevel}.
   */
  MetadataLevel getMetadataLevel();

  /**
   * Specifies whether or not to validate files when reading.
   *
   * @param validate {@code true} if files should be validated, {@code
   * false} otherwise.
   */
  void setValidate(boolean validate);

  /**
   * Checks whether file validation has been set.
   *
   * @return {@code true} if files are validated when read, {@code
   * false} otherwise.
   */
  boolean isValidate();

}
