import javax.swing.tree.DefaultMutableTreeNode;
import ij.*;
import ij.io.FileInfo;
import java.io.StringReader;
import java.io.StringWriter;
import loci.ome.xml.*;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
/**
 * XMLUtils is the class that handles
 * the xml in tiff headers.  It needs java 1.4.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class XMLUtils{
  /** retrieves the metadata from an OME tiff file which contains
    XML in the image description*/
  public static void readTiff(Integer imageID){
    int ijimageID=imageID.intValue();
    System.out.println("getting imagemetadata from table in sidepanel");
    Object[] meta=null;
    meta=OMESidePanel.getImageMeta(ijimageID);
    FileInfo fi=null;
    System.out.println("get original fileinfo");
    try {
       fi = WindowManager.getImage(ijimageID).getOriginalFileInfo();
    }
    catch (Exception x) {
      OMEDownPanel.error(IJ.getInstance(),
      "An error occurred while retrieving the original file information.",
      "Error");
      IJ.showStatus("Error retrieving file information.");
      x.printStackTrace();
    }
    if ( fi==null || fi.description==null) {
      IJ.showStatus("Not an OME tiff file.");
      if ( meta==null) {
        meta=new Object[2];
        meta[0]=new Integer(0);
      }
      meta[1]=null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }
    StringReader sbis=new StringReader(fi.description);
    InputSource inputSource=null;
    System.out.println("starting to transform");
    //this portion is to convert the OME XML to OME CA XML before parsing
    try {
    javax.xml.transform.TransformerFactory tFactory=
      javax.xml.transform.TransformerFactory.newInstance();
    javax.xml.transform.Transformer transformer = tFactory.newTransformer
      (new javax.xml.transform.stream.StreamSource("plugins/Input-Output/OME2OME-CA.xslt"));
    org.xml.sax.InputSource inputsource=new org.xml.sax.InputSource(sbis);
    javax.xml.transform.sax.SAXSource saxsource=new javax.xml.transform.sax.SAXSource(inputsource);
    StringWriter stringWriter = new StringWriter();
    javax.xml.transform.stream.StreamResult streamresult=new javax.xml.transform.stream.StreamResult(stringWriter);
    transformer.transform(saxsource,streamresult);
    String finalResult = stringWriter.toString();
    sbis=new StringReader(finalResult);
    inputSource=new InputSource(sbis);
    System.out.println(finalResult);
    }
    catch (Exception e){
      IJ.showStatus("Error while converting xml file.");
      e.printStackTrace();
      if ( meta==null) {
        meta=new Object[2];
         meta[0]=new Integer(0);
      }
      meta[1]=null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }
    OMEElement omeElement=new OMEElement();
    try {
      //parse the xml code
      omeElement.readXML(inputSource);
    }
    catch (SAXException e) {
      //not an OME tiff file so there is no metadata in the header
      IJ.showStatus("Not an OME tiff file.");
      if ( meta==null) {
        meta=new Object[2];
         meta[0]=new Integer(0);
      }
      meta[1]=null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
//      e.printStackTrace();
    }
    catch (IOException f){
      OMEDownPanel.error(IJ.getInstance(),
      "An error occurred while parsing metadata in the tiff file.", "Error");
      IJ.showStatus("Error Retrieving metadata.");
      f.printStackTrace();
    }
    
    
    if ( meta==null) {
      meta=new Object[2];
      meta[0]=new Integer(0);
    }
    meta[1]=new DefaultMutableTreeNode(
      new XMLObject("Meta Data"));
    System.out.println("meta data is being put in the table");
    createNodes((DefaultMutableTreeNode)meta[1], omeElement);
    OMESidePanel.hashInImage(ijimageID, meta);
  }//end of readTiff method
  
  /**creates nodes for the tree to display from metadata*/
  private static void createNodes(DefaultMutableTreeNode root, OMEElement omeElement){
    DefaultMutableTreeNode 
      image=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.IMAGEHEADING)),
      custom=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.CUSTOMHEADING)),
      project=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.PROJECTHEADING)),
      dataset=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.DATASETHEADING));
    if (createNodes(image, omeElement.getImage())) root.add(image);
    if (createNodes(dataset, omeElement.getDataset())) root.add(dataset);
    if (createNodes(project, omeElement.getProject())) root.add(project);
    if (createNodes(custom, omeElement.getCustomAttr())) root.add(custom);
  }//end of createNodes(TreeNode, OMEElement)
  
  /**creates nodes from the different elements*/
  private static boolean createNodes(DefaultMutableTreeNode root, CAElement caElement){
    int[] ids=caElement.getElementIDs();
    //always want the node to show up to allow adding of custom attributes
    if (ids.length==0)return true;
    DefaultMutableTreeNode element;
    for (int i=0; i<ids.length; i++) {
      element=new DefaultMutableTreeNode(
        new XMLObject(caElement.getElementName(ids[i]), XMLObject.ELEMENT));
      root.add(element);
      String[] attrNames = caElement.getAttributeNames(ids[i]);
      for (int k=0; k<attrNames.length; k++) { 
        String attrValue = caElement.getAttribute(ids[i], attrNames[k]);
        element.add(new DefaultMutableTreeNode
        (new XMLObject(attrNames[k], attrValue,XMLObject.ATTRIBUTE), false));
      }
    }
    return true;
  }//end of createNodes method
  
  private static boolean createNodes(DefaultMutableTreeNode root, ImageElement imageElement){
    root.add(new DefaultMutableTreeNode(
      new XMLObject("Name", imageElement.getName(), XMLObject.IMAGE)));
    root.add(new DefaultMutableTreeNode(
      new XMLObject("ID", imageElement.getID(), XMLObject.IMAGE)));
    root.add(new DefaultMutableTreeNode(
      new XMLObject("Creation Date", imageElement.getCreationDate(), XMLObject.IMAGE)));
    root.add(new DefaultMutableTreeNode(
      new XMLObject("Description", imageElement.getDescription(), XMLObject.IMAGE)));
    DefaultMutableTreeNode datasets=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.DATASETREF));
    DatasetRefElement[] dre=imageElement.getDatasetRefs();
    if (dre.length!=0) {
      String s="";
      for (int i=0; i<dre.length;i++){
        s.concat(dre[i].getID()+", ");
      }    
      if (s.length()!=0 && s.length()!=2){
        root.add(datasets);
        datasets.add(new DefaultMutableTreeNode(
          new XMLObject("Dataset IDs", s.substring(0, s.length()-3),XMLObject.READONLY)));
      }
    }
    //get the image element custom attributes
    DefaultMutableTreeNode cas=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.CUSTOMHEADING));
    if (createNodes(cas, imageElement.getCustomAttr())) root.add(cas);
    //get the image element features
    FeatureElement[] featureList=imageElement.getFeatures();
    for(int i=0; i<featureList.length; i++){
      createNodes(root, featureList[i]);
    }
    return true;
  }//end of createNodes method
  
  private static void createNodes(DefaultMutableTreeNode root, FeatureElement featureElement){
    DefaultMutableTreeNode feature=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.FEATUREHEADING));
    root.add(feature);
    feature.add(new DefaultMutableTreeNode(
      new XMLObject("Name", featureElement.getName(), XMLObject.FEATURE)));
    feature.add(new DefaultMutableTreeNode(
      new XMLObject("ID", featureElement.getID(), XMLObject.FEATURE)));
    feature.add(new DefaultMutableTreeNode(
      new XMLObject("Tag", featureElement.getTag(), XMLObject.FEATURE)));
    FeatureElement[] featureList=featureElement.getFeatures();
    for(int i=0; i<featureList.length; i++){
      createNodes(feature, featureList[i]);
    }
    //get the feature element custom attributes
    DefaultMutableTreeNode cas=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.CUSTOMHEADING));
    if (createNodes(cas, featureElement.getCustomAttr())) feature.add(cas);
  }//end of createNodes featureElement version method
  
  private static boolean createNodes(DefaultMutableTreeNode root, ProjectElement projectElement){
    String [] attrs={projectElement.getName(), projectElement.getID(), 
      projectElement.getExperimenter(), projectElement.getGroup(),
      projectElement.getDescription()};
    boolean present=false;
    for (int i=0; i<attrs.length; i++){
      if (attrs[i]!=null) present=true;
    }
    DefaultMutableTreeNode image=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.PROJECTHEADING));
    root.add(image);
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Name", attrs[0], XMLObject.PROJECT)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("ID", attrs[1], XMLObject.PROJECT)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Experimenter", attrs[2], XMLObject.PROJECT)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Group", attrs[3], XMLObject.PROJECT)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Description", attrs[4], XMLObject.PROJECT)));
    return present;
  }//end of createNodes method
  
  private static boolean createNodes(DefaultMutableTreeNode root, DatasetElement datasetElement){
    String [] attrs={datasetElement.getName(), datasetElement.getID(), 
      datasetElement.getExperimenter(), datasetElement.getGroup(),
      datasetElement.getDescription(), datasetElement.getLocked()};
    boolean present=false;
    for (int i=0; i<attrs.length; i++){
      if (attrs[i]!=null) present=true;
    }
    DefaultMutableTreeNode image=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.DATASETHEADING));
    root.add(image);
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Name", attrs[0], XMLObject.DATASET)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("ID", attrs[1], XMLObject.DATASET)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Experimenter", attrs[2], XMLObject.DATASET)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Group", attrs[3], XMLObject.DATASET)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Description", attrs[4], XMLObject.DATASET)));
    image.add(new DefaultMutableTreeNode(
      new XMLObject("Locked", attrs[5], XMLObject.DATASET)));
      
    DefaultMutableTreeNode projects=new DefaultMutableTreeNode("Project References");
    ProjectRefElement[] pre=datasetElement.getProjectRefs();
    if (pre.length!=0) {
      root.add(projects);
      String s="";
      for (int i=0; i<pre.length;i++){
        s.concat(pre[i].getID()+", ");
      }    
      projects.add(new DefaultMutableTreeNode(
        new XMLObject("Project IDs", s.substring(0, s.length()-3),XMLObject.READONLY)));
    }
    DefaultMutableTreeNode cas=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.CUSTOMHEADING));
    if (createNodes(cas, datasetElement.getCustomAttr())) root.add(cas);
    return present;
  }//end of createNodes method

}//end of XMLUtils class