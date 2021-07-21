package loci.formats.in;

import java.io.IOException;
import java.util.List;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;

public class DicomFileInfo implements Comparable<DicomFileInfo> {
  public CoreMetadata coreMetadata;
  public String file;
  public List<DicomTile> tiles;
  public String imageType;
  public List<Double> zOffsets;

  public DicomFileInfo() {
  }

  public DicomFileInfo(String filePath) throws FormatException, IOException {
    file = filePath;

    try (DicomReader reader = new DicomReader()) {
      reader.setGroupFiles(false);
      reader.setId(file);
      coreMetadata = reader.getCoreMetadataList().get(0);
      tiles = reader.getTiles();
      imageType = reader.getImageType();
      zOffsets = reader.getZOffsets();
    }
  }

  @Override
  public int compareTo(DicomFileInfo info) {
    String[] infoTypeTokens = info.imageType.split("\\\\");
    String[] thisTypeTokens = this.imageType.split("\\\\");
    int endIndex = (int) Math.min(infoTypeTokens.length, thisTypeTokens.length);
    for (int i=2; i<endIndex; i++) {
      if (!infoTypeTokens[i].equals(thisTypeTokens[i])) {
        // this logic is intentional, the idea is to sort like this:
        //   ORIGINAL\PRIMARY\VOLUME\NONE
        //   DERIVED\PRIMARY\VOLUME\RESAMPLED
        //   DERIVED\PRIMARY\LABEL\NONE
        if (i < endIndex - 1) {
          return infoTypeTokens[i].compareTo(thisTypeTokens[i]);
        }
        else {
          return thisTypeTokens[i].compareTo(infoTypeTokens[i]);
        }
      }
    }
    if (!info.imageType.equals(this.imageType)) {
      return this.imageType.compareTo(info.imageType);
    }
    int infoX = info.coreMetadata.sizeX;
    int infoY = info.coreMetadata.sizeY;
    int thisX = this.coreMetadata.sizeX;
    int thisY = this.coreMetadata.sizeY;
    if (infoX != thisX) {
      return infoX - thisX;
    }
    if (infoY != thisY) {
      return infoY - thisY;
    }
    return 0;
  }
}
