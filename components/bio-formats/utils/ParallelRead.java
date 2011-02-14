//
// ParallelRead.java
//

import java.io.File;
import loci.common.services.ServiceFactory;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Reads all files in given directory in parallel,
 * using a separate thread for each.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/ParallelRead.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/ParallelRead.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ParallelRead implements Runnable {
  private String id;

  public ParallelRead(String id) {
    this.id = id;
  }

  public void run() {
    try {
      ImageReader r = new ImageReader();
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata meta = service.createOMEXMLMetadata();
      r.setMetadataStore(meta);
      r.setId(id);
      System.out.println(Thread.currentThread().getName() +
        ": id=" + id +
        ", sizeX=" + r.getSizeX() +
        ", sizeY=" + r.getSizeY() +
        ", sizeZ=" + r.getSizeZ() +
        ", sizeT=" + r.getSizeT() +
        ", sizeC=" + r.getSizeC() +
        ", imageName=" + meta.getImageName(0));
      r.close();
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public static void main(String[] args) {
    String dir = args[0];
    File[] list = new File(dir).listFiles();
    for (int i=0; i<list.length; i++) {
      ParallelRead pr = new ParallelRead(list[i].getAbsolutePath());
      new Thread(pr).start();
    }
  }
}
