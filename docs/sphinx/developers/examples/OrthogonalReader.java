package com.glencoesoftware.orthogonal.reader;

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
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import ome.units.quantity.Length;

public class Main {

    private static final Logger log =
            LoggerFactory.getLogger(Main.class);

    @Arg
    private String input;

    @Arg
    private String output;

    @Arg
    private boolean debug;

    public static void main(String[] args) throws Throwable {
        log.info("Orthogonal reader started");
        ArgumentParser parser =
                ArgumentParsers.newArgumentParser("parser");
        parser.addArgument("--input")
              .required(true)
              .help("Input file");
        parser.addArgument("--output")
              .required(true)
              .help("Input file");
        parser.addArgument("--debug")
              .action(Arguments.storeTrue())
              .help("Set logging level to Debug");
        Main main = new Main();
        try {
            parser.parseArgs(args, main);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        main.readOrthogonalPlanes();
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
