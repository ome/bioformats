package loci.formats.in.LeicaMicrosystemsMetadata.writeCore;

import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;

public class DimensionWriter {
  
  public static void setupCoreDimensionParameters(ImageFormat imageFormat, CoreMetadata core, DimensionStore dimensionStore, int extras){
    setCoreDimensionSizes(core, dimensionStore, extras);
    setPixelType(core, dimensionStore);

    // TIFF and JPEG files not interleaved
    if (imageFormat == ImageFormat.TIF || imageFormat == ImageFormat.JPEG) {
      core.interleaved = false;
    } else {
      core.interleaved = core.rgb;
    }
    core.indexed = !core.rgb;
    core.imageCount = core.sizeZ * core.sizeT;
    if (!core.rgb)
      core.imageCount *= core.sizeC;
    else {
      core.imageCount *= (core.sizeC / 3);
    }

    core.dimensionOrder = dimensionStore.getDimensionOrder();
  }

  /**
   * Writes extracted dimension sizes to CoreMetadata
   * 
   * @param coreIndex
   */
  private static void setCoreDimensionSizes(CoreMetadata core, DimensionStore dimensionStore, int extras) {
    core.sizeX = dimensionStore.getDimension(DimensionKey.X).size;
    core.sizeY = dimensionStore.getDimension(DimensionKey.Y).size;
    core.sizeZ = dimensionStore.getDimension(DimensionKey.Z).size;
    core.sizeT = dimensionStore.getDimension(DimensionKey.T).size;
    core.rgb = (dimensionStore.getDimension(DimensionKey.X).bytesInc % 3) == 0;
    if (core.rgb)
    dimensionStore.getDimension(DimensionKey.X).bytesInc /= 3;

    if (extras > 1) {
      if (core.sizeZ == 1)
        core.sizeZ = extras;
      else {
        if (core.sizeT == 0)
          core.sizeT = extras;
        else
          core.sizeT *= extras;
      }
    }

    if (core.sizeX == 0)
      core.sizeX = 1;
    if (core.sizeY == 0)
      core.sizeY = 1;
    if (core.sizeC == 0)
      core.sizeC = 1;
    if (core.sizeZ == 0)
      core.sizeZ = 1;
    if (core.sizeT == 0)
      core.sizeT = 1;
  }

  /**
   * Sets CoreMetadata.pixelType depending on extracted x bytesInc
   * 
   * @param coreIndex
   * @throws FormatException
   */
  private static void setPixelType(CoreMetadata core, DimensionStore dimensionStore) {
    long xBytesInc = dimensionStore.getDimension(DimensionKey.X).bytesInc;
    try {
      core.pixelType = FormatTools.pixelTypeFromBytes((int) xBytesInc, false, true);
    } catch (Exception e){
      System.out.println("Could not render pixel type from x bytes inc: " + xBytesInc);
      e.printStackTrace();
    }
  }
}
