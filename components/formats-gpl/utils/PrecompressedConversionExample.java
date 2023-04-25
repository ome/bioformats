/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2023 Open Microscopy Environment:
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


import loci.common.services.ServiceFactory;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.in.SVSReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.out.DicomWriter;

/**
 * Example of working with precompressed tiles.
 * Input is a SVS file, output is a DICOM dataset.
 */
public class PrecompressedConversionExample {

  public static void main(String[] args) throws Exception {
    // initialize the provided SVS file, preserving the pyramid structure
    try (SVSReader reader = new SVSReader()) {
      reader.setFlattenedResolutions(false);

      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata omexmlMeta = service.createOMEXMLMetadata();
      reader.setMetadataStore(omexmlMeta);

      reader.setId(args[0]);

      try (DicomWriter writer = new DicomWriter()) {
        writer.setMetadataRetrieve((IMetadata) reader.getMetadataStore());
        writer.setWriteSequentially(true);
        writer.setId(args[1]);
        writer.setCompression("JPEG");

        // iterate over every tile in every series, resolution, and plane
        for (int series=0; series<reader.getSeriesCount(); series++) {
          reader.setSeries(series);
          writer.setSeries(series);

          for (int resolution=0; resolution<reader.getResolutionCount(); resolution++) {
            reader.setResolution(resolution);
            writer.setResolution(resolution);

            int width = reader.getOptimalTileWidth();
            int height = reader.getOptimalTileHeight();

            for (int plane=0; plane<reader.getImageCount(); plane++) {
              Codec tileCodec = reader.getTileCodec(plane);

              // convert pyramids tile-wise
              if (reader.getResolutionCount() > 1) {
                for (int y=0; y<reader.getTileRows(plane); y++) {
                  for (int x=0; x<reader.getTileColumns(plane); x++) {
                    // read the pre-compressed tile from the SVS and save directly to DICOM
                    byte[] tile = reader.openCompressedBytes(plane, x, y);

                    int tileWidth = (int) Math.min(width, reader.getSizeX() - x * width);
                    int tileHeight = (int) Math.min(height, reader.getSizeY() - y * height);
                    writer.saveCompressedBytes(plane, tile, x * width, y * height, tileWidth, tileHeight);
                  }
                }
              }
              else {
                writer.saveBytes(plane, reader.openBytes(plane));
              }
            }
          }
        }
      }
    }
  }

}
