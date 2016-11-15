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

import java.util.Properties;
import java.io.File;


/**
 * Default implementation of {@link loci.formats.in.MetadataOptions}.
 */
public class DefaultMetadataOptions implements MetadataOptions {

  public static final String METADATA_LEVEL_KEY = "metadata.level";
  public static final MetadataLevel METADATA_LEVEL_DEFAULT = MetadataLevel.ALL;

  public static final String READER_VALIDATE_KEY = "reader.validate.input";
  public static final boolean READER_VALIDATE_DEFAULT = false;

  private Properties props;

  /**
   * Construct a new {@code DefaultMetadataOptions}. Set the metadata
   * level to {@link loci.formats.in.MetadataLevel#ALL} and disable
   * file validation.
   */
  public DefaultMetadataOptions() {
    this(METADATA_LEVEL_DEFAULT);
  }

  /**
   * Construct a new {@code DefaultMetadataOptions}.
   *
   * @param level the {@link loci.formats.in.MetadataLevel} to use.
   */
  public DefaultMetadataOptions(MetadataLevel level) {
    props = new Properties();
    setEnum(METADATA_LEVEL_KEY, level);
    setBoolean(READER_VALIDATE_KEY, READER_VALIDATE_DEFAULT);
  }

  public void set(String name, String value) {
    if (null == name) {
      throw new IllegalArgumentException("name cannot be null");
    }
    try {
      props.setProperty(name, value);
    } catch (NullPointerException e) {
      props.remove(name);
    }
  }

  public String get(String name, String defaultValue) {
    return props.getProperty(name, defaultValue);
  }

  public <T extends Enum<T>> void setEnum(String name, T value) {
    set(name, value.toString());
  }

  public <T extends Enum<T>> T getEnum(String name, T defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Enum.valueOf(defaultValue.getDeclaringClass(), val);
  }

  public void setBoolean(String name, boolean value) {
    set(name, Boolean.toString(value));
  }

  public boolean getBoolean(String name, boolean defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    if (val.equalsIgnoreCase("true")) {
      return true;
    }
    if (val.equalsIgnoreCase("false")) {
      return false;
    }
    throw new IllegalArgumentException(val + "does not represent a boolean");
  }

  public void setChar(String name, char value) {
    set(name, Character.toString(value));
  }

  public char getChar(String name, char defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    if (val.length() != 1) {
      throw new IllegalArgumentException("string length is not 1");
    }
    return val.charAt(0);
  }

  public void setByte(String name, byte value) {
    set(name, Byte.toString(value));
  }

  public byte getByte(String name, byte defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Byte.parseByte(val);
  }

  public void setShort(String name, short value) {
    set(name, Short.toString(value));
  }

  public short getShort(String name, short defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Short.parseShort(val);
  }

  public void setInt(String name, int value) {
    set(name, Integer.toString(value));
  }

  public int getInt(String name, int defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Integer.parseInt(val);
  }

  public void setLong(String name, long value) {
    set(name, Long.toString(value));
  }

  public long getLong(String name, long defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Long.parseLong(val);
  }

  public void setFloat(String name, float value) {
    set(name, Float.toString(value));
  }

  public float getFloat(String name, float defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Float.parseFloat(val);
  }

  public void setDouble(String name, double value) {
    set(name, Double.toString(value));
  }

  public double getDouble(String name, double defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Double.parseDouble(val);
  }

  public void setClass(String name, Class<?> value) {
    set(name, value.getName());
  }

  public Class<?> getClass(String name, Class<?> defaultValue)
      throws ClassNotFoundException {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return Class.forName(val);
  }

  public void setFile(String name, File value) {
    set(name, value.toString());
  }

  public File getFile(String name, File defaultValue) {
    final String val = get(name, null);
    if (null == val) {
      return defaultValue;
    }
    return new File(val);
  }

  @Override
  public MetadataLevel getMetadataLevel() {
    return getEnum(METADATA_LEVEL_KEY, METADATA_LEVEL_DEFAULT);
  }

  @Override
  public void setMetadataLevel(MetadataLevel level) {
    setEnum(METADATA_LEVEL_KEY, level);
  }

  @Override
  public boolean isValidate() {
    return getBoolean(READER_VALIDATE_KEY, READER_VALIDATE_DEFAULT);
  }

  @Override
  public void setValidate(boolean validateMetadata) {
    setBoolean(READER_VALIDATE_KEY, validateMetadata);
  }

}
