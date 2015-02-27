/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools.libmdb;

public class Util
{
  // convert from access string format to unicode
  public static String extractText(byte[] bytes)
  {
    boolean clean = true;
    // busco null
    for (int i = 1; i < bytes.length; i += 2) {
      if (bytes[i] != 0) {
        clean = false;
        break;
      }
    }
    if (clean) {
      byte[] ab = new byte[bytes.length / 2];
      for (int i = 0, j = 0; i < ab.length; i++, j += 2) {
        ab[i] = bytes[j];
      }
      return new String(ab); //,0,ab.length);
    }
    else {
      int start = 0;
      // for some kind of reason, my access is returning varchars with this
      // leading chars:
      if (bytes[0] == -1 && bytes[1] == -2)
        start = 2;
      return new String(bytes,start,bytes.length-start);
    }
  }
}
