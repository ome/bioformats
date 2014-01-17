/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2014 Open Microscopy Environment:
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

package ome.jxr.parser;

import java.io.IOException;

import ome.jxr.JXRException;
import ome.scifio.io.RandomAccessInputStream;

public class Parser {

  protected int parsingOffset;

  protected RandomAccessInputStream stream;

  public Parser(RandomAccessInputStream stream) throws JXRException {
    this(stream, 0);
  }

  public Parser(RandomAccessInputStream stream, int parsingOffset)
      throws JXRException {
    if (stream == null) {
      throw new IllegalArgumentException("Input stream cannot be null.");
    }

    if (parsingOffset != 0) {
      try {
        if (parsingOffset < 0 || parsingOffset > stream.length()) {
          throw new IllegalArgumentException(String.format(
              "Invalid offset supplied. Stream length: %d, offset: %d.",
              stream.length(), parsingOffset));
        }
      } catch (IOException ioe) {
        throw new JXRException(ioe);
      }
    }

    this.stream = stream;
    this.parsingOffset = parsingOffset;
  }

  public void close() throws IOException {
    stream.close();
  }

}
