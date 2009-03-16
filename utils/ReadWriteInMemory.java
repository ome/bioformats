//
// ReadWriteInMemory.java
//

import java.io.*;
import loci.common.Location;
import loci.common.RABytes;
import loci.formats.*;
import loci.formats.meta.IMetadata;

/** Tests the Bio-Formats I/O logic to and from byte arrays in memory. */
public class ReadWriteInMemory {

  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 1) {
      System.out.println("Please specify a (small) image file.");
      System.exit(1);
    }
    String path = args[0];

    // read in entire file
    System.out.println("Reading file into memory from disk...");
    File inputFile = new File(path);
    int fileSize = (int) inputFile.length();
    DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
    byte[] inBytes = new byte[fileSize];
    in.readFully(inBytes);
    System.out.println(fileSize + " bytes read.");

    // determine input file suffix
    String fileName = inputFile.getName();
    int dot = fileName.lastIndexOf(".");
    String suffix = dot < 0 ? "" : fileName.substring(dot);

    // map input id string to input byte array
    String inId = "inBytes" + suffix;
    Location.mapFile(inId, new RABytes(inBytes));

    // read data from byte array using ImageReader
    System.out.println();
    System.out.println("Reading image data from memory...");
    IMetadata omeMeta = MetadataTools.createOMEXMLMetadata();
    ImageReader reader = new ImageReader();
    reader.setMetadataStore(omeMeta);
    reader.setId(inId);
    int seriesCount = reader.getSeriesCount();
    int imageCount = reader.getImageCount();
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int sizeZ = reader.getSizeZ();
    int sizeC = reader.getSizeC();
    int sizeT = reader.getSizeT();

    // output some details
    System.out.println("Series count: " + seriesCount);
    System.out.println("First series:");
    System.out.println("\tImage count = " + imageCount);
    System.out.println("\tSizeX = " + sizeX);
    System.out.println("\tSizeY = " + sizeY);
    System.out.println("\tSizeZ = " + sizeZ);
    System.out.println("\tSizeC = " + sizeC);
    System.out.println("\tSizeT = " + sizeT);

    /*
    // map output id string to output byte array
    String outId = fileName + ".ome.tif";
    RABytes outputFile = new RABytes();
    Location.mapFile(outId, outputFile);

    // write data to byte array using ImageWriter
    System.out.println();
    System.out.print("Writing planes to destination in memory: ");
    ImageWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(omeMeta);
    writer.setId(outId);

    byte[] plane = null;
    for (int i=0; i<imageCount; i++) {
      if (plane == null) {
        // allow reader to allocate a new byte array
        plane = reader.openBytes(i);
      }
      else {
        // reuse previously allocated byte array
        reader.openBytes(i, plane);
      }
      writer.saveBytes(plane, i == imageCount - 1);
      System.out.print(".");
    }
    reader.close();
    writer.close();
    System.out.println();

    byte[] outBytes = outputFile.getBytes();
    outputFile.close();

    // flush output byte array to disk
    System.out.println();
    System.out.println("Flushing image data to disk...");
    File outFile = new File(fileName + ".ome.tif");
    DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile));
    out.write(outBytes);
    out.close();
    */
  }

}
