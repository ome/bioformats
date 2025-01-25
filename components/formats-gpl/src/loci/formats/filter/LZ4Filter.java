/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.filter;

import ucar.nc2.filter.FilterProvider;
import ucar.nc2.filter.Filter;

import io.airlift.compress.lz4.Lz4Decompressor;

import java.util.Map;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LZ4Filter extends Filter {

  static final String name = "LZ4 filter";
  static final int id = 32004;
  private Lz4Decompressor decompressor;

  public LZ4Filter(Map<String, Object> properties) {
    decompressor = new Lz4Decompressor();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public byte[] encode(byte[] dataIn) throws IOException {
    throw new UnsupportedOperationException("LZ4Filter does not support data compression");
  }

  @Override
  public byte[] decode(byte[] dataIn) throws IOException {
    ByteBuffer byteBuffer = ByteBuffer.wrap(dataIn);
    
    long totalDecompressedSize = byteBuffer.getLong();
    int decompressedBlockSize = byteBuffer.getInt();
    int compressedBlockSize = byteBuffer.getInt();

    byte[] decompressedBlock = new byte[Math.toIntExact(totalDecompressedSize)];
    byte[] compressedBlock = new byte[compressedBlockSize];

    byteBuffer.get(compressedBlock, 0, compressedBlockSize);
    decompressor.decompress(compressedBlock, 0, compressedBlockSize, decompressedBlock, 0, decompressedBlockSize);

    return decompressedBlock;
  }

  public static class LZ4FilterProvider implements FilterProvider {

    @Override
    public String getName() {
      return name;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public Filter create(Map<String, Object> properties) {
      return new LZ4Filter(properties);
    }

  }

}
