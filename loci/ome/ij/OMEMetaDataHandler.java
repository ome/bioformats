import ij.*;
import ij.process.ImageProcessor;
import ij.io.FileInfo;

import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.st.*;
import loci.ome.xml.*;
import org.w3c.dom.*;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Constructor;

/**
 * OMEMetaDataHandler is the class that handles the download of metadata 
 * from the database associated with an image and OME.  It also handles the 
 * sifting of XML meta-data in the header of an OME-TIFF file.
 *
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */

public class OMEMetaDataHandler {
  
  private static ImageProcessor imageP;
  private static boolean isXML;
  private static OMENode omeNode;

   
  /**Method that begins the process of getting metadata from an OME_TIFF file*/
  /*
  public static DefaultMutableTreeNode exportMeta(Image image, 
      ImagePlus imagePlus, DataFactory df) {

    imageP = imagePlus.getProcessor();
    isXML = false;
    omeNode = null;

    DefaultMutableTreeNode root = 
      new DefaultMutableTreeNode(new XMLObject("OME-XML"));
    DefaultMutableTreeNode imageNode = addImage(image, df);
    root.add(imageNode);

    DefaultMutableTreeNode customNode = 
      new DefaultMutableTreeNode(new XMLObject(XMLObject.CUSTOMHEADING));
    imageNode.add(customNode);

    for(int i=0; i<OMEMetaPanel.IMAGE_TYPES.length; i++) {
      Criteria criteria = new Criteria();
      criteria.addWantedField("image_id");
      criteria.addFilter("image_id", (new Integer(image.getID())).toString());
      List customs = null;
      try {
        customs = df.retrieveList(OMEMetaPanel.IMAGE_TYPES[i], criteria);
      }
      catch(Exception e) { 
        e.printStackTrace();
	IJ.showStatus("Error while retrieving " + 
	  OMEMetaPanel.IMAGE_TYPES[i] + "s.");
      }

      if(customs != null) {
        Iterator itCustoms = customs.iterator();
	while(itCustoms.hasNext()) {
	  addSomething(itCustoms.next(), customNode, df, 
	    OMEMetaPanel.IMAGE_TYPES[i]);
	}
      }
    }
    return root;
  } 
  */
   
  /**Method that begins the process of getting metadata from an OME_TIFF file*/
  public static void exportMeta(int ijimageID) {
    // TODO: rewrite this method to parse metadata from the OME database

    isXML = true;
    imageP = WindowManager.getImage(ijimageID).getProcessor();
    IJ.showStatus("Retrieving OME-TIFF header.");
    Object[] meta = null;
    meta = OMESidePanel.getImageMeta(ijimageID);
    FileInfo fi = null;
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
    if (fi == null || fi.description == null) {
      IJ.showStatus("Not an OME-TIFF file.");
      if (meta == null) {
        meta = new Object[2];
        meta[0] = new Integer(0);
      }
      meta[1] = null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }
    // check if white is zero and if true invert the bits
    if(fi.whiteIsZero) {
      ImageProcessor iProc = WindowManager.getImage(ijimageID).getProcessor();
      iProc.invert();
      iProc.invertLut();
      fi.whiteIsZero = false;
    }
    IJ.showStatus("Parsing OME-TIFF header.");
    try {
      omeNode = new OMENode(fi.description);
    }
    catch (Exception e) {
      IJ.showStatus("Error parsing OME-XML metadata, possibly not present.");
      /* 
       * Don't print stack trace, this is a legal thing, 
       * you don't have to have an OME tiff file,  its just cooler if you do.
       */
      if (meta == null) {
        meta = new Object[2];
         meta[0] = new Integer(0);
      }
      meta[1] = null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }
    if (meta == null) {
      meta = new Object[2];
      meta[0] = new Integer(0);
    }
    meta[1] = new DefaultMutableTreeNode(
      new XMLObject("OME-XML"));
    OMESidePanel.hashInImage(ijimageID, meta);
    IJ.showStatus("Metadata is being put into tree structure.");
    addSomething(omeNode, (DefaultMutableTreeNode) meta[1], null, "OME");
  }
  
  /** method that creates the image nodes in the metadata tree */
  private static DefaultMutableTreeNode addImage(Image image, DataFactory df) {
    DefaultMutableTreeNode imageNode = new DefaultMutableTreeNode(
      new XMLObject(XMLObject.IMAGEHEADING));
    addSomething(image, imageNode, df, "Image");
    return imageNode;
  }
  
  /** exports DisplayOptions from the database and adds to the tree */
  private static void addDisplayOptions(DisplayOptions element, 
      DefaultMutableTreeNode root, DataFactory df) {
  
    IJ.showStatus("Retrieving Display Options attributes.");
    if(element == null) return;
    if(df != null) {
      //setup and load the columns that this element has
      String [] attrs = {"BlueChannel", "ColorMap","DisplayROIList",
        "GreenChannel", "GreyChannel", "Pixels", "RedChannel", "TStart",
        "TStop", "Zoom", "ZStart", "ZStop", "BlueChannelOn", "DisplayRGB",
        "GreenChannelOn", "RedChannelOn"};
      Criteria criteria = OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element = (DisplayOptions)df.retrieve("DisplayOptions", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(
      new XMLObject("DisplayOptions", XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("ColorMap",
      element.getColorMap(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TStart",
      "" + element.getTStart(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TStop",
      "" + element.getTStop(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Zoom",
      "" + element.getZoom(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ZStart",
      "" + element.getZStart(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ZStop",
      "" + element.getZStop(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DisplayRGB",
      "" + element.isDisplayRGB(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("RedChannelOn",
      "" + element.isRedChannelOn(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("GreenChannelOn",
      "" + element.isGreenChannelOn(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("BlueChannelOn",
      "" + element.isBlueChannelOn(), XMLObject.ATTRIBUTE)));
    if (element.getPixels() == null);
    else if(element.getPixels() instanceof Pixels) {
      node.add(new DefaultMutableTreeNode(new XMLObject(
        "Pixels", "" + ((Pixels)element.getPixels()).getID(),
        XMLObject.ATTRIBUTE)));
    }
    else {
      node.add(new DefaultMutableTreeNode(new XMLObject(
        "Pixels", "" + element.getPixels().getID(), XMLObject.ATTRIBUTE)));
    }
    //get children of this element
    DefaultMutableTreeNode red = new DefaultMutableTreeNode(
      new XMLObject("RedChannel"));
    DefaultMutableTreeNode green = new DefaultMutableTreeNode(
      new XMLObject("GreenChannel"));
    DefaultMutableTreeNode blue = new DefaultMutableTreeNode(
      new XMLObject("BlueChannel"));
    DefaultMutableTreeNode grey = new DefaultMutableTreeNode(
      new XMLObject("GreyChannel"));
    
    double[] redd = addDisplayChannel(element.getRedChannel(), red, df);
    double[] greend = addDisplayChannel(element.getGreenChannel(), green, df);
    double[] blued = addDisplayChannel(element.getBlueChannel(), blue, df);
    double[] greyd = addDisplayChannel(element.getGreyChannel(), grey, df);
    if (!red.isLeaf()) node.add(red);
    if (!green.isLeaf()) node.add(green);
    if (!blue.isLeaf()) node.add(blue);
    if (!grey.isLeaf()) node.add(grey);
    //set display characteristics in ImageJ from display channel values
    if (greyd != null) {
      imageP.setMinAndMax(greyd[0], greyd[1]);
      imageP.gamma(greyd[2]);
    }
    else if(redd!=null) {
      imageP.setMinAndMax(redd[0], redd[1]);
      imageP.gamma(redd[2]);
    }
    else if(blued != null) {
      imageP.setMinAndMax(blued[0], blued[1]);
      imageP.gamma(blued[2]);
    }
    else if(greend != null) {
      imageP.setMinAndMax(greend[0], greend[1]);
      imageP.gamma(greend[2]);
    }
  }
  
  /**
   * exports DisplayChannel from the database, adds it to the tree and 
   * returns the black level, white level, and gamma of the channel
   */
  private static double[] addDisplayChannel(DisplayChannel element, 
      DefaultMutableTreeNode root, DataFactory df) {
    IJ.showStatus("Retrieving Display Channel attributes.");
    if(element == null) return null;
    if(df != null) {
      //setup and load the columns that this element has
      String [] attrs = {"BlackLevel", "ChannelNumber", "Gamma", "WhiteLevel"};
      Criteria criteria = OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element = (DisplayChannel)df.retrieve("DisplayChannel", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(
      new XMLObject("DisplayChannel", XMLObject.ELEMENT));
    root.add(node);
    //get levels for display in ImageJ
    Double black = element.getBlackLevel(), white = element.getWhiteLevel();
    Float gamma = element.getGamma();
    double [] levels = new double[3];
    if(black == null || white == null || gamma == null) {
      levels = null;
    }
    else {
      levels[0] = black.doubleValue();
      levels[1] = white.doubleValue();
      levels[2] = gamma.doubleValue();
    }
    
    node.add(new DefaultMutableTreeNode(new XMLObject("ChannelNumber",
      "" + element.getChannelNumber(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Gamma",
      "" + gamma, XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("WhiteLevel",
      "" + white, XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("BlackLevel",
      "" + black, XMLObject.ATTRIBUTE)));
    return levels;
  }
  
  private static void exportMeta(Feature feature, DefaultMutableTreeNode root,
    DataFactory df) {
    
    IJ.showStatus("Retrieving Feature attributes.");
    DefaultMutableTreeNode featureNode = new DefaultMutableTreeNode(
      new XMLObject(XMLObject.FEATUREHEADING));
    root.add(featureNode);
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", feature.getName(), XMLObject.FEATURE)));
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Tag", feature.getTag(), XMLObject.FEATURE)));
    //add trajectory entries
    List trajects = null;
    if(df != null) {
      Criteria trajCriteria = new Criteria();
      trajCriteria.addWantedField("feature_id");
      trajCriteria.addFilter("feature_id", (new Integer(
        feature.getID())).toString());
      //retrieve element to add with all columns loaded
      trajects = df.retrieveList("Trajectory", trajCriteria);
    }
    else trajects = ((Trajectory)feature).getTrajectoryEntryList();
    
    Iterator iterTra = trajects.iterator();
    while (iterTra.hasNext()) {
      addSomething((Trajectory)iterTra.next(), featureNode, df, "Trajectory");
    }
    //add attributes to this element's node
    if(df != null) {
      for (int i=0; i<OMEMetaPanel.FEATURE_TYPES.length; i++) {
        Criteria criteria = new Criteria();
        criteria.addWantedField("feature_id");
        criteria.addFilter("feature_id", (new Integer(
	  feature.getID())).toString());
        IJ.showStatus("Retrieving " + OMEMetaPanel.FEATURE_TYPES[i] + "s.");
        List customs = null;
        try {
          customs = df.retrieveList(OMEMetaPanel.FEATURE_TYPES[i], criteria);
        }
	catch(Exception e) {
          e.printStackTrace();
          IJ.showStatus("Error while retrieving " + 
	    OMEMetaPanel.FEATURE_TYPES[i] + "s.");
        }
        if(customs != null) {
          Iterator itCustoms = customs.iterator();
          while (itCustoms.hasNext()) {
	    addSomething(itCustoms.next(), featureNode, df, 
	      OMEMetaPanel.FEATURE_TYPES[i]);
          }
        }
      }
    }
    else addSomething(feature, featureNode, df, "Feature");
    //add child features to tree
    List features = feature.getChildren();
    Iterator iter = features.iterator();
    while (iter.hasNext()) {
      exportMeta((Feature)iter.next(), featureNode, df);
    }
  }
 
  private static void addSomething(Object element, DefaultMutableTreeNode root,
    DataFactory df, String identifier) {

    IJ.showStatus("Retrieving " + identifier + " attributes.");
    if(element == null) return;

    OMEXMLNode xml = null;
    if(!element.toString().startsWith("[")) {
      xml = (OMEXMLNode) element;
    }
      
    String[] tempAttrs = null;
    String[] tempValues = null;

    try {
      tempAttrs = xml.getAttributeNames();
      tempValues = xml.getAttributeValues();
    }
    catch(NullPointerException n) { }
    
    // NodeList is a list of child DOM elements
    NodeList tempChilds = null;
    Object[] childs = null;
    String[] names = null;
    try {
      tempChilds = xml.getDOMElement().getChildNodes(); 
      childs = new Object[tempChilds.getLength()];
    }
    catch(NullPointerException n) { }
   
    try {
      for(int i=0; i<tempChilds.getLength(); i++) {
        String tmp = tempChilds.item(i).toString();
	String id = tmp.substring(1, tmp.indexOf(" "));
	String tmpId = tmp.substring(1, tmp.indexOf(">"));
	if(tmpId.length() < id.length()) id = tmpId;
	if(!id.endsWith("Node")) {
 	  id = id + "Node";
	}
	if(!id.equals("AttributeNode") && !id.equals("CustomAttributesNode") &&
	    !id.equals("DatasetNode") && !id.equals("FeatureNode") && 
	    !id.equals("ImageNode") && !id.equals("OMENode") && 
	    !id.equals("ProjectNode")) {
          id = "loci.ome.xml.st." + id;
	} 
	else {
	  id = "loci.ome.xml." + id;
	}

	// construct a new node using this class
        Class toConstruct = Class.forName(id.trim());
	Constructor construct = 
	  toConstruct.getDeclaredConstructor(new Class[] {Element.class});
	childs[i] = 
	  construct.newInstance(new Object[] {(Element) tempChilds.item(i)});
      }
    
      names = new String[childs.length];
      for(int i=0; i<childs.length; i++) {
        String tmp = childs[i].toString();
        names[i] = tmp.substring(tmp.lastIndexOf(".")+1, tmp.lastIndexOf("@"));
      }
    }
    catch(Throwable t) { }

    if(df != null && tempAttrs != null) {
      Criteria criteria = OMEDownload.makeAttributeFields(tempAttrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(
        ((ImageGroup) element).getID())).toString());
      element = df.retrieve(identifier, criteria);
    }

    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new XMLObject(
      identifier, XMLObject.ELEMENT));
    root.add(node);

    if(tempAttrs != null && tempValues != null) {
      for(int i=0; i<tempValues.length; i++) {
        node.add(new DefaultMutableTreeNode(new XMLObject(tempAttrs[i], 
	  tempValues[i], XMLObject.ATTRIBUTE)));
      }
    }   
    
    if(tempChilds != null) {
      for(int i=0; i<childs.length; i++) {
        addSomething(childs[i], node, df, names[i]);
      }
    }  
  }
}
