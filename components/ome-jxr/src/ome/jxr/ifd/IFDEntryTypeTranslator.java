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

import loci.common.DataTools;

/**
 * Translates raw data bytes into calls to methods returning their native
 * type representation. Used to convert JPEG XR IFD Entry Type values into
 * a language-specific format.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/ifd/IFDEntryTypeTranslator.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/ifd/IFDEntryTypeTranslator.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class IFDEntryTypeTranslator {

//  public Object toPrimitiveType(IFDEntryType type, byte[] value) {
//
//    switch (type) {
//      case BYTE:
//        return value;
//      case DOUBLE:
//        return DataTools.bytesToDouble(value, true);
//      case FLOAT:
//        return DataTools.bytesToFloat(value, true);
//      case SBYTE:
//        return DataTools.makeSigned(value);
//      case SLONG:
//      case ULONG:
//        return DataTools.bytesToLong(value, true);
//      case SRATIONAL:
//      case URATIONAL:
//        if (value.length == 8) {
//          return new JXRRational(DataTools.bytesToInt(value, 0, true),
//              DataTools.bytesToInt(value, 4, true));
//        } else {
//          throw new IllegalArgumentException("Not enough value bytes for: "
//              + type.name());
//        }
//      case SSHORT:
//      case USHORT:
//        return DataTools.bytesToShort(value, true);
//      case UNDEFINED:
//        return value;
//      case UTF8:
//        return new String(value);
//      default:
//        return value;
//    }
//  }

  

}
