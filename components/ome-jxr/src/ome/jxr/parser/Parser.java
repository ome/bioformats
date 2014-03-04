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

  long parsingOffset;

  RandomAccessInputStream stream;

  private Parser parentParser;

  public Parser(Parser parentParser, RandomAccessInputStream stream) {
    if (stream == null) {
      throw new NullPointerException("Input stream cannot be null.");
    }

    this.parentParser = parentParser;
    this.stream = stream;
  }

  public void parse() throws JXRException {
    parse(0);
  }

  public void parse(long parsingOffset) throws JXRException {
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

    // Set the byte order to little-endian by default
    this.stream.order(true);
    this.parsingOffset = parsingOffset;
  }

  Parser getParentParser() {
    return parentParser;
  }

  public void close() throws IOException {
    stream.close();
  }

}
