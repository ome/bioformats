//
// EditImageName.java
//

import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Edits the given file's image name (but does not save back to disk).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/utils/EditImageName.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/utils/EditImageName.java">SVN</a></dd></dl>
 */
public class EditImageName {

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: java EditImageName file");
      return;
    }
    ImageReader reader = new ImageReader();
    // record metadata to OME-XML format
    IMetadata omexmlMeta = MetadataTools.createOMEXMLMetadata();
    reader.setMetadataStore(omexmlMeta);
    String id = args[0];
    System.out.print("Reading metadata ");
    reader.setId(id);
    System.out.println(" [done]");

    // get image name
    String name = omexmlMeta.getImageName(0);
    System.out.println("Initial Image name = " + name);
    // change image name (reverse it)
    char[] arr = name.toCharArray();
    for (int i=0; i<arr.length/2; i++) {
      int i2 = arr.length - i - 1;
      char c = arr[i];
      char c2 = arr[i2];
      arr[i] = c2;
      arr[i2] = c;
    }
    name = new String(arr);
    // save altered name back to OME-XML structure
    omexmlMeta.setImageName(name, 0);
    System.out.println("Updated Image name = " + name);
    // output full OME-XML block
    System.out.println("Full OME-XML dump:");
    String xml = MetadataTools.getOMEXML(omexmlMeta);
    System.out.println(xml);
  }

}
