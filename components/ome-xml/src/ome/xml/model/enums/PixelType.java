/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:36.486351
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model.enums;

public enum PixelType implements Enumeration
{
  INT8("int8"), INT16("int16"), INT32("int32"), UINT8("uint8"), UINT16("uint16"), UINT32("uint32"), FLOAT("float"), BIT("bit"), DOUBLE("double"), COMPLEX("complex"), DOUBLECOMPLEX("double-complex");
  
  private PixelType(String value)
  {
    this.value = value;
  }

  public static PixelType fromString(String value)
    throws EnumerationException
  {
    if ("int8".equals(value))
    {
      return INT8;
    }
    if ("int16".equals(value))
    {
      return INT16;
    }
    if ("int32".equals(value))
    {
      return INT32;
    }
    if ("uint8".equals(value))
    {
      return UINT8;
    }
    if ("uint16".equals(value))
    {
      return UINT16;
    }
    if ("uint32".equals(value))
    {
      return UINT32;
    }
    if ("float".equals(value))
    {
      return FLOAT;
    }
    if ("bit".equals(value))
    {
      return BIT;
    }
    if ("double".equals(value))
    {
      return DOUBLE;
    }
    if ("complex".equals(value))
    {
      return COMPLEX;
    }
    if ("double-complex".equals(value))
    {
      return DOUBLECOMPLEX;
    }
    String s = String.format("%s not a supported value of %s",
                             value, PixelType.class);
    throw new EnumerationException(s);
  }

  public String getValue()
  {
    return value;
  }

  @Override
  public String toString()
  {
    return value;
  }

  private final String value;
}
