package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;

import loci.formats.CoreMetadata;
import loci.formats.DelegateReader;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;

public class LIFDelegateReader extends DelegateReader {

  public LIFDelegateReader() {
    super("Leica Image File Format", "lif");
    nativeReader = new LMSLIFReader();
    legacyReader = new LIFReader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
    suffixNecessary = false;
    hasCompanionFiles = true;
    domains = new String[] { FormatTools.LM_DOMAIN };
    useLegacy = false;
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    IFormatReader reader = isLegacy() ? legacyReader : nativeReader;
    return reader.openBytes(no, buf, x, y, w, h);
  }

  @Override
  public void setId(String id) throws FormatException, IOException {
    IFormatReader reader = isLegacy() ? legacyReader : nativeReader;
    reader.setId(id);

    if (isLegacy())
      legacyReaderInitialized = true;
    else
      nativeReaderInitialized = true;
    
    currentId = reader.getCurrentFile();
    core = new ArrayList<CoreMetadata>(reader.getCoreMetadataList());
    metadata = reader.getGlobalMetadata();
    metadataStore = reader.getMetadataStore();
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    IFormatReader reader = isLegacy() ? legacyReader : nativeReader;
    return reader.isThisType(name, open);
  }
}
