package loci.formats.in;

import loci.common.Region;

public class DicomTile {
  public Region region;
  public String file;
  public int fileIndex;
  public long fileOffset;
  public long endOffset;
  public Double zOffset;
  public int channel;
  public int coreIndex = 0;
  public boolean last = false;

  public boolean isJP2K = false;
  public boolean isJPEG = false;
  public boolean isRLE = false;
  public boolean isDeflate = false;
}
