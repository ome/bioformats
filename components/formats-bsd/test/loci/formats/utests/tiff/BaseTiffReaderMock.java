package loci.formats.utests.tiff;

import loci.formats.in.BaseTiffReader;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;

public class BaseTiffReaderMock extends BaseTiffReader{

  public static final String[] TIFF_SUFFIXES =
    {"tif", "tiff", "tf2", "tf8", "btf"};

  public BaseTiffReaderMock() {
    super("Tagged Image File Format", TIFF_SUFFIXES);
    ifds = new IFDList();
  }
  
  public void addIFD(IFD ifd) {
    ifds.add(ifd);
  }
  
  public void clearIFDs() {
    ifds.clear();
  }
  
  public String getCreationDate() {
    return getImageCreationDate();
  }

}
