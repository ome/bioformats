//
// DumpOMEXML.java
//

import java.io.IOException;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Extracts and prints out the OME-XML for a given file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/DumpOMEXML.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/DumpOMEXML.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class DumpOMEXML {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java DumpOMEXML file1 file2 ...");
      return;
    }
    for (int i=0; i<args.length; i++) dumpOMEXML(args[i]);
  }

  public static void dumpOMEXML(String path) throws FormatException,
    IOException, DependencyException, ServiceException
  {
    ServiceFactory serviceFactory = new ServiceFactory();
    OMEXMLService omexmlService =
      serviceFactory.getInstance(OMEXMLService.class);
    IMetadata meta = omexmlService.createOMEXMLMetadata();

    ImageReader r = new ImageReader();
    r.setMetadataStore(meta);
    r.setId(path);
    r.close();
    String xml = omexmlService.getOMEXML(meta);
    System.out.println(xml);
  }

}
