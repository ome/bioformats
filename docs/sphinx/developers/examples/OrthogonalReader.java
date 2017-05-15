/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2017 Open Microscopy Environment:
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

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import loci.common.services.ServiceFactory;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import ome.units.quantity.Length;

public class OrthogonalReader {

    private static final Logger log =
            LoggerFactory.getLogger(OrthogonalReader.class);

    private String input;

    private String output;

    private boolean debug;

    public static void main(String[] args) throws Throwable {
        log.info("Orthogonal reader started");
        OrthogonalReader main = new OrthogonalReader();
        for (int i=0; i<args.length; i++) {
          if (args[i].equals("--input")) {
            main.setInputFile(args[i + 1]);
          }
          else if (args[i].equals("--output")) {
            main.setOutputFile(args[i + 1]);
          }
          else if (args[i].equals("--debug")) {
            main.setDebug(true);
          }
        }
        main.readOrthogonalPlanes();
    }

    public void setInputFile(String input) {
      this.input = input;
    }

    public void setOutputFile(String output) {
      this.output = output;
    }

    public void setDebug(boolean debug) {
      this.debug = debug;
    }

    private ImageReader initialiseReader(String fileName) throws Exception {
        ImageReader reader = new ImageReader();
        reader.setId(fileName);
        return reader;
    }

    private OMETiffWriter initialiseWriter(String fileName, ImageReader reader)
        throws Exception
    {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata metadata = service.createOMEXMLMetadata();
        MetadataRetrieve mr = (MetadataRetrieve) reader.getMetadataStore();
        Length originalSizeX = mr.getPixelsPhysicalSizeX(0);
        Length originalSizeY = mr.getPixelsPhysicalSizeY(0);
        Length originalSizeZ = mr.getPixelsPhysicalSizeZ(0);
        // Original XY planes
        // XZ planes
        MetadataTools.populateMetadata(
            metadata, 0, "XZ", reader.isLittleEndian(),
            reader.getDimensionOrder(),
            FormatTools.getPixelTypeString(reader.getPixelType()),
            reader.getSizeX(), reader.getSizeZ(),
            reader.getSizeY(), 1, 1, 1);
        metadata.setPixelsPhysicalSizeX(originalSizeX, 0);
        metadata.setPixelsPhysicalSizeY(originalSizeZ, 0);
        metadata.setPixelsPhysicalSizeZ(originalSizeY, 0);
        // YZ planes
        MetadataTools.populateMetadata(
            metadata, 1, "YZ", reader.isLittleEndian(),
            reader.getDimensionOrder(),
            FormatTools.getPixelTypeString(reader.getPixelType()),
            reader.getSizeY(), reader.getSizeZ(),
            reader.getSizeX(), 1, 1, 1);
        metadata.setPixelsPhysicalSizeX(originalSizeY, 1);
        metadata.setPixelsPhysicalSizeY(originalSizeZ, 1);
        metadata.setPixelsPhysicalSizeZ(originalSizeX, 1);
        OMETiffWriter writer = new OMETiffWriter();
        writer.setMetadataRetrieve(metadata);
        writer.setId(fileName);
        return writer;
    }

    private void readOrthogonalPlanes() throws Exception {
        // Setup logger
        ch.qos.logback.classic.Logger root =
            (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(
                Logger.ROOT_LOGGER_NAME);
        if (debug) {
            root.setLevel(Level.DEBUG);
        } else {
            root.setLevel(Level.INFO);
        }
        ImageReader reader = this.initialiseReader(this.input);
        reader.setSeries(0);
        OMETiffWriter writer = this.initialiseWriter(this.output, reader);
        int index;
        // Write XZ planes
        writer.setSeries(0);
        for (int y = 0; y < reader.getSizeY(); y++) {
            ByteBuffer bufferXZ = ByteBuffer.allocate(
                (int) (0.125 * reader.getBitsPerPixel()) * reader.getSizeX()
                * reader.getSizeZ());
            for (int z = 0; z < reader.getSizeZ(); z++) {
                index = reader.getIndex(z, 0, 0);
                byte[] line = reader.openBytes(
                    index, 0, y, reader.getSizeX(), 1);
                bufferXZ.put(line);
            }
            log.info("y: {}, {}", y, bufferXZ.array().length);
            writer.saveBytes(y, bufferXZ.array());
        }
        // Write YZ planes
        writer.setSeries(1);
        for (int x = 0; x < reader.getSizeX(); x++) {
            ByteBuffer bufferYZ = ByteBuffer.allocate(
                (int) (0.125 * reader.getBitsPerPixel()) * reader.getSizeY()
                * reader.getSizeZ());
            for (int z = 0; z < reader.getSizeZ(); z++) {
                index = reader.getIndex(z, 0, 0);
                byte[] line = reader.openBytes(
                    index, x, 0, 1, reader.getSizeY());
                bufferYZ.put(line);
            }
            log.info("x: {}, {}", x, bufferYZ.array().length);
            writer.saveBytes(x, bufferYZ.array());
        }
        reader.close();
        writer.close();
    }
}
