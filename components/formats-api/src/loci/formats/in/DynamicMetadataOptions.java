/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2016 - 2017 Open Microscopy Environment:
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

import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.formats.FormatException;

import java.io.File;
import java.io.IOException;

/**
 * Configuration object for readers and writers.
 */
public class DynamicMetadataOptions implements MetadataOptions {
  protected static final Logger LOGGER = LoggerFactory.getLogger(DynamicMetadataOptions.class);

  public static final String METADATA_LEVEL_KEY = "metadata.level";
  public static final MetadataLevel METADATA_LEVEL_DEFAULT = MetadataLevel.ALL;

  public static final String READER_VALIDATE_KEY = "reader.validate.input";
  public static final boolean READER_VALIDATE_DEFAULT = false;

  private Properties props;

  /**
   * Creates an options object with metadata level set to {@link
   * #METADATA_LEVEL_DEFAULT} and file validation set to {@link
   * #READER_VALIDATE_DEFAULT}.
   */
  public DynamicMetadataOptions() {
    this(METADATA_LEVEL_DEFAULT);
  }

  /**
   * Creates an options object with metadata level set to the
   * specified value and file validation set to {@link
   * #READER_VALIDATE_DEFAULT}
   *
   * @param level the {@link loci.formats.in.MetadataLevel} to use.
   */
  public DynamicMetadataOptions(MetadataLevel level) {
    props = new Properties();
    setEnum(METADATA_LEVEL_KEY, level);
    setBoolean(READER_VALIDATE_KEY, READER_VALIDATE_DEFAULT);
  }

  // -- MetadataOptions API Methods --

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

  // -- key/value options --

  /**
   * Set property {@code name} to {@code value}.
   * If {@code value} is {@code null} the property will be removed.
   *
   * @param name property name.
   * @param value property value.
   * @throws IllegalArgumentException if {@code name} is {@code null}.
   */
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

  /**
   * Get the value of property {@code name}.
   *
   * @param name property name.
   * @return property value, or {@code null} if the property does not exist.
   */
  public String get(String name) {
    return props.getProperty(name);
  }

  /**
   * Get the value of property {@code name}.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value, or {@code defaultValue} if the property
   * does not exist.
   */
  public String get(String name, String defaultValue) {
    return props.getProperty(name, defaultValue);
  }

  /**
   * Set property {@code name} to enumerated type {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public <T extends Enum<T>> void setEnum(String name, T value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, value.toString());
    }
  }

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
  public <T extends Enum<T>> T getEnum(String name, T defaultValue) {
    if (null == defaultValue) {
      throw new IllegalArgumentException("default value can't be null");
    }
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return Enum.valueOf(defaultValue.getDeclaringClass(), val);
  }

  /**
   * Set property {@code name} to Boolean {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setBoolean(String name, Boolean value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, Boolean.toString(value));
    }
  }

  /**
   * Get the value of property {@code name} as a Boolean.
   *
   * @param name property name.
   * @return property value as a Boolean, or {@code null} if the
   * property does not exist.
   * @throws IllegalArgumentException if value does not represent a Boolean.
   */
  public Boolean getBoolean(String name) {
    return getBoolean(name, null);
  }

  /**
   * Get the value of property {@code name} as a Boolean.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a Boolean, or {@code defaultValue} if
   * the property does not exist.
   * @throws IllegalArgumentException if value does not represent a Boolean.
   */
  public Boolean getBoolean(String name, Boolean defaultValue) {
    final String val = get(name);
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

  /**
   * Set property {@code name} to Integer {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setInteger(String name, Integer value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, Integer.toString(value));
    }
  }

  /**
   * Get the value of property {@code name} as an Integer.
   *
   * @param name property name.
   * @return property value as an Integer, or {@code null} if the
   * property does not exist.
   * @throws NumberFormatException if value does not represent an Integer.
   */
  public Integer getInteger(String name) {
    return getInteger(name, null);
  }

  /**
   * Get the value of property {@code name} as an Integer.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as an Integer, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent an Integer.
   */
  public Integer getInteger(String name, Integer defaultValue) {
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return Integer.parseInt(val);
  }

  /**
   * Set property {@code name} to Long {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setLong(String name, Long value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, Long.toString(value));
    }
  }

  /**
   * Get the value of property {@code name} as a Long.
   *
   * @param name property name.
   * @return property value as a Long, or {@code null} if the property
   * does not exist.
   * @throws NumberFormatException if value does not represent a Long.
   */
  public Long getLong(String name) {
    return getLong(name, null);
  }

  /**
   * Get the value of property {@code name} as a Long.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a Long, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a Long.
   */
  public Long getLong(String name, Long defaultValue) {
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return Long.parseLong(val);
  }

  /**
   * Set property {@code name} to Float {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setFloat(String name, Float value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, Float.toString(value));
    }
  }

  /**
   * Get the value of property {@code name} as a Float.
   *
   * @param name property name.
   * @return property value as a Float, or {@code null} if the
   * property does not exist.
   * @throws NumberFormatException if value does not represent a Float.
   */
  public Float getFloat(String name) {
    return getFloat(name, null);
  }

  /**
   * Get the value of property {@code name} as a Float.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a Float, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a Float.
   */
  public Float getFloat(String name, Float defaultValue) {
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return Float.parseFloat(val);
  }

  /**
   * Set property {@code name} to Double {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setDouble(String name, Double value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, Double.toString(value));
    }
  }

  /**
   * Get the value of property {@code name} as a Double.
   *
   * @param name property name.
   * @return property value as a Double, or {@code null} if the
   * property does not exist.
   * @throws NumberFormatException if value does not represent a Double.
   */
  public Double getDouble(String name) {
    return getDouble(name, null);
  }

  /**
   * Get the value of property {@code name} as a Double.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a Double, or {@code defaultValue} if
   * the property does not exist.
   * @throws NumberFormatException if value does not represent a Double.
   */
  public Double getDouble(String name, Double defaultValue) {
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return Double.parseDouble(val);
  }

  /**
   * Set property {@code name} to Class {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setClass(String name, Class<?> value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, value.getName());
    }
  }

  /**
   * Get the value of property {@code name} as a class.
   *
   * @param name property name.
   * @return property value as a class, or {@code null} if the
   * property does not exist.
   * @throws ClassNotFoundException if value does not map to an existing class.
   */
  public Class<?> getClass(String name) throws ClassNotFoundException {
    return getClass(name, null);
  }

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
      throws ClassNotFoundException {
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return Class.forName(val);
  }

  /**
   * Set property {@code name} to file object {@code value}.
   *
   * @param name property name.
   * @param value property value.
   */
  public void setFile(String name, File value) {
    if (null == value) {
      set(name, null);
    } else {
      set(name, value.toString());
    }
  }

  /**
   * Get the value of property {@code name} as a file object.
   *
   * @param name property name.
   * @return property value as a {@code File}, or {@code null} if the
   * property does not exist.
   */
  public File getFile(String name) {
    return getFile(name, null);
  }

  /**
   * Get the value of property {@code name} as a file object.
   *
   * @param name property name.
   * @param defaultValue default value.
   * @return property value as a {@code File}, or {@code defaultValue} if
   * the property does not exist.
   */
  public File getFile(String name, File defaultValue) {
    final String val = get(name);
    if (null == val) {
      return defaultValue;
    }
    return new File(val);
  }
  
  public void loadOptions(String optionsFile, ArrayList<String> availableOptionKeys) throws IOException, FormatException {
    if (!new Location(optionsFile).exists()) {
      LOGGER.trace("Options file doesn't exist: {}", optionsFile);
      // TODO: potentially create option
      return;
    }
    if(!new Location(optionsFile).canRead()) {
      LOGGER.trace("Can't read options file: {}", optionsFile);
      return;
    }

    IniParser parser = new IniParser();
    IniList list = parser.parseINI(new File(optionsFile));
    for (IniTable attrs: list) {
      for (String key: attrs.keySet()) {
        if (!availableOptionKeys.contains(key)) {
          LOGGER.warn("Metadata Option Key is not supported in this reader " + key);
        }
        set(key, attrs.get(key));
      }
    }
  }

  public static String getMetadataOptionsFile(String id) {
    Location f = new Location(id);
    if (f != null && f.getParent() != null) {
      String p = f.getParent();
      String n = f.getName();
      if (n.indexOf(".") >= 0) {
        n = n.substring(0, n.indexOf("."));
      }
      return new Location(p, n + ".bfoptions").getAbsolutePath();
    }
    return null;
  }

}
