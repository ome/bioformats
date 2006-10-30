//
// EditImageName.java
//

import loci.formats.ImageReader;
import loci.formats.OMEXMLMetadataStore;
import org.openmicroscopy.xml.ImageNode;
import org.openmicroscopy.xml.OMENode;

/** Edits the given file's image name (but does not save back to disk). */
public class EditImageName {

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: java EditImageName file");
      return;
    }
    ImageReader reader = new ImageReader();
    // record metadata to OME-XML format
    reader.setMetadataStore(new OMEXMLMetadataStore());
    String id = args[0];
    System.out.print("Reading metadata ");
    OMEXMLMetadataStore store =
      (OMEXMLMetadataStore) reader.getMetadataStore(id);
    System.out.println(" [done]");
    // get OME root node
    OMENode ome = (OMENode) store.getRoot();
    // get first Image node
    ImageNode image = (ImageNode) ome.getImages().get(0);
    // get Image name
    String name = image.getName();
    System.out.println("Initial Image name = " + name);
    // change Image name (reverse it)
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
    image.setName(name);
    System.out.println("Updated Image name = " + name);
    // output full OME-XML block
    System.out.println("Full OME-XML dump:");
    System.out.println(ome.writeOME(false));
  }

}
