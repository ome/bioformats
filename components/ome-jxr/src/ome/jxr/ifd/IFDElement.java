/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.jxr.ifd;

enum IFDElement {
  
  DOCUMENT_NAME(0x010D, IFDElementType.UTF8, Integer.MAX_VALUE, false),
  IMAGE_DESCRIPTION(0x010E, IFDElementType.UTF8, Integer.MAX_VALUE, false),
  EQUIPMENT_MAKE(0x010F, IFDElementType.UTF8, Integer.MAX_VALUE, false),
  EQUIPMENT_MODEL(0x0110, IFDElementType.UTF8, Integer.MAX_VALUE, false),
  PAGE_NAME(0x011D, IFDElementType.UTF8, Integer.MAX_VALUE, false),
  PAGE_NUMBER(0x0129, IFDElementType.USHORT, 2, false),
  SOFTWARE_NAME_VERSION(0x0131, IFDElementType.UTF8, Integer.MAX_VALUE, false);

  // I need a map of tag to entry here! This way in the decoder I will be
  // able to search for a value based on the tag bytes.

  private final int tag;
  private final IFDElementType elementType;
  private final int count;
  private final boolean required;

  private IFDElement(int tag, IFDElementType elementType, int count,
      boolean required) {
    this.tag = tag;
    this.elementType = elementType;
    this.count = count;
    this.required = required;
  }

  public int getTag() {
    return tag;
  }

  public IFDElementType getElementType() {
    return elementType;
  }

  public int getCount() {
    return count;
  }

  public boolean isRequired() {
    return required;
  }
}