package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ImageDetails;

public class ImageSettingsExtractor {
  
  /**
   * Extracts image details from LMS XML
   */
  public static void extractImageDetails(LMSMainXmlNodes xmlNodes, ImageDetails imageDetails){
    NodeList attachmentNodes = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "User-Comment");
    if (attachmentNodes != null){
      for (int i = 0; i < attachmentNodes.getLength(); i++) {
        Node attachment = attachmentNodes.item(i);
        imageDetails.userComments.add(attachment.getTextContent());
        if (i == 0)
        imageDetails.description = attachment.getTextContent();
      }
    }
  }
}
