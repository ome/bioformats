import ij.*;
import ij.process.ImageProcessor;
import ij.io.FileInfo;

import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.st.*;
import loci.ome.xml.*;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 * OMEMetaDataHandler is the class that handles
 * the download of metadata from the database
 * associated with an image and OME.  It also
 * handles the sifting of xml meta-data in the
 * header of an OME-Tiff file.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEMetaDataHandler{
  
  //Field
  //ImageJ image processor that the levels will be set on
  private static ImageProcessor imageP;
  private static boolean isXML;
  private static OMENode omeNode;
  
  /**Method that begins the process of getting metadata from an OME_TIFF file*/
  public static void exportMeta(int ijimageID){
    isXML=true;
    imageP=WindowManager.getImage(ijimageID).getProcessor();
    IJ.showStatus("Retrieving OME-Tiff header.");
    Object[] meta=null;
    meta=OMESidePanel.getImageMeta(ijimageID);
    FileInfo fi=null;
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
    //check if white is zero and if true invert the bits
    if(fi.whiteIsZero){
      ImageProcessor iProc=WindowManager.getImage(ijimageID).getProcessor();
      iProc.invert();
      iProc.invertLut();
      fi.whiteIsZero=false;
    }
    IJ.showStatus("Parsing OME-Tiff header.");
    try {
      omeNode=new OMENode(fi.description);
    }
    catch (Exception e){
      IJ.showStatus("Error parsing OME-XML metadata, possibly not present.");
      /* Don't print stack trace, this is a legal thing, you don't have to have an OME
       tiff file,  its just cooler if you do.*/
      if ( meta==null) {
        meta=new Object[2];
         meta[0]=new Integer(0);
      }
      meta[1]=null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }
    if ( meta==null) {
      meta=new Object[2];
      meta[0]=new Integer(0);
    }
    meta[1]=new DefaultMutableTreeNode(
      new XMLObject("Meta Data"));
    OMESidePanel.hashInImage(ijimageID, meta);
    IJ.showStatus("Meta data is being put into tree structure.");
    List list=omeNode.getImages();
    Iterator iter =list.iterator();
    while (iter.hasNext()){
      exportMeta((ImageNode)iter.next(), (DefaultMutableTreeNode)meta[1]);
    }
    //stop holding the omeNode
    omeNode=null;
  }//end of OME-TIFF xml retrieval exportMeta method
  
  /**method that imports an image node from xml in an OME-TIFF file*/
  private static void exportMeta(ImageNode image, DefaultMutableTreeNode rootNode){
    DefaultMutableTreeNode imageNode=addImage(image, null);
    rootNode.add(imageNode);
    DefaultMutableTreeNode customNode=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.CUSTOMHEADING));
    //implement when known if list or single
    List list=image.getLogicalChannels();
    Iterator iter=list.iterator();
    while(iter.hasNext()){
      addLogicalChannel((LogicalChannel)iter.next(), customNode, null);
    }
    addDimensions( image.getDimensions(), customNode, null);
    addImageAnnotation(image.getImageAnnotation(), customNode, null);
    addImageExperiment(image.getImageExperiment(), customNode, null);
    addImageGroup(image.getImageGroup(), customNode, null);
    addImageInstrument(image.getImageInstrument(), customNode, null);
    addImagePlate(image.getImagePlate(), customNode, null);
    addImageTestSignature(image.getImageTestSignature(), customNode, null);
    addImagingEnvironment(image.getImagingEnvironment(), customNode, null);  
    addDisplayOptions(image.getDisplayOptions(), customNode, null);
    addPlaneCentroid(image.getPlaneCentroid(), customNode, null);
    addPlaneGeometricMean(image.getPlaneGeometricMean(), customNode, null);
    addPlaneGeometricSigma(image.getPlaneGeometricSigma(), customNode, null);
    addPlaneMaximum(image.getPlaneMaximum(), customNode, null);
    addPlaneMean(image.getPlaneMean(), customNode, null);
    addPlaneMinimum(image.getPlaneMinimum(), customNode, null);
    addPlaneSigma(image.getPlaneSigma(), customNode, null);
    addPlaneSum_i(image.getPlaneSum_i(), customNode, null);
    addPlaneSum_i2(image.getPlaneSum_i2(), customNode, null);
    addPlaneSum_log_i(image.getPlaneSum_log_i(), customNode, null);
    addPlaneSum_Xi(image.getPlaneSum_Xi(), customNode, null);
    addPlaneSum_Yi(image.getPlaneSum_Yi(), customNode, null);
    addPlaneSum_Zi(image.getPlaneSum_Zi(), customNode, null);
    addStackCentroid(image.getStackCentroid(), customNode, null);
    addStackGeometricMean(image.getStackGeometricMean(), customNode, null);
    addStackGeometricSigma(image.getStackGeometricSigma(), customNode, null);
    addStackMaximum(image.getStackMaximum(), customNode, null);
    addStackMean(image.getStackMean(), customNode, null);
    addStackMinimum(image.getStackMinimum(), customNode, null);
    addStackSigma(image.getStackSigma(), customNode, null);
    addStageLabel(image.getStageLabel(), customNode, null);
    addThumbnail(image.getThumbnail(), customNode, null);
    addClassification(image.getClassification(), customNode, null);
    if(!customNode.isLeaf())imageNode.add(customNode);
    IJ.showStatus("Finished retrieving image metadata.");
  }//end of ImageNode exportMeta Method
  
  /**method that imports a feature node from xml in an OME-TIFF file*/
  private static void exportMeta(FeatureNode feature, DefaultMutableTreeNode root){
    addBounds(feature.getBounds(), root, null);
    addExtent(feature.getExtent(), root, null);
    addLocation(feature.getLocation(), root, null);
    addRatio(feature.getRatio(), root, null);
    addSignal(feature.getSignal(), root, null);
    addThreshold(feature.getThreshold(), root, null);
    addTimepoint(feature.getTimepoint(), root, null);
  }//end of FeatureNode exportMeta method
  
  /**method that creates the image nodes in the metadata tree*/
  private static DefaultMutableTreeNode addImage(Image image, DataFactory df){
    IJ.showStatus("Retrieving Image attributes.");
    //image node
    DefaultMutableTreeNode imageNode=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.IMAGEHEADING));
    //nodes under image
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", image.getName(), XMLObject.IMAGE)));
    if(image instanceof ImageNode) imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("ID", ""+((ImageNode)image).getLSID(), XMLObject.IMAGE)));
    else imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("ID", ""+image.getID(), XMLObject.IMAGE)));
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("Creation Date", image.getCreated(), XMLObject.IMAGE)));
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("Description", image.getDescription(), XMLObject.IMAGE)));
    //get pixel information
    DefaultMutableTreeNode pixelNode=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.PIXELHEADING));
    Pixels pixels=image.getDefaultPixels();
    exportPixelMeta(pixels, pixelNode, df);
    if(!pixelNode.isLeaf())imageNode.add(pixelNode);
    //get dataset references
    IJ.showStatus("Retrieving Dataset References.");
    DefaultMutableTreeNode datasets=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.DATASETREF));
    List data=image.getDatasets();
    Iterator iterator=data.iterator();
    String s="";
    while (iterator.hasNext()) {
      Dataset ds=(Dataset)iterator.next();
      if(ds instanceof DatasetNode) s.concat(((DatasetNode)ds).getLSID()+", ");
      else s.concat(ds.getID()+", ");
    }
    if (s.length()!=0 && s.length()!=2){
      imageNode.add(datasets);
      datasets.add(new DefaultMutableTreeNode(
        new XMLObject("Dataset IDs", s.substring(0, s.length()-3),XMLObject.READONLY)));
    }
    //get the image element features
    List featureList=image.getFeatures();
    if(featureList==null) System.out.println("Features not implemented yet");
    else{
      Iterator fIter=featureList.iterator();
      while(fIter.hasNext()){
        exportMeta((Feature)fIter.next(), imageNode, df);
      }
    }
    return imageNode;
  }//end of addImage method
  
  /**Method that downloads metadata from the OME database and creates a tree*/
  public static DefaultMutableTreeNode exportMeta(Image image, ImagePlus imagePlus, DataFactory df){
    imageP=imagePlus.getProcessor();
    isXML=false;
    omeNode=null;
    //create root node of whole tree
    DefaultMutableTreeNode root=new DefaultMutableTreeNode(
      new XMLObject("Meta Data"));
    DefaultMutableTreeNode imageNode=addImage(image, df);
    root.add(imageNode);
    
    //load custom attributes
    DefaultMutableTreeNode customNode=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.CUSTOMHEADING));
    imageNode.add(customNode);
    for (int i=0; i<OMEMetaPanel.IMAGE_TYPES.length; i++){
      Criteria criteria=new Criteria();
      criteria.addWantedField("image_id");
      criteria.addFilter("image_id", (new Integer(image.getID())).toString());
      List customs=null;
      try{
        customs=df.retrieveList(OMEMetaPanel.IMAGE_TYPES[i], criteria);
      }catch(Exception e){
        e.printStackTrace();
        IJ.showStatus("Error while retrieving "+OMEMetaPanel.IMAGE_TYPES[i]+"s.");
      }
      if(customs!=null){
        Iterator itCustoms=customs.iterator();
        while (itCustoms.hasNext()){
          if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageExperiment")){
            addImageExperiment((ImageExperiment)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Classification")){
            addClassification((Classification)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageInstrument")){
            addImageInstrument((ImageInstrument)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageAnnotation")){
            addImageAnnotation((ImageAnnotation)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageGroup")){
            addImageGroup((ImageGroup)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImagePlate")){
            addImagePlate((ImagePlate)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("DisplayOptions")){
            addDisplayOptions((DisplayOptions)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Dimensions")){
            addDimensions((Dimensions)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageTestSignature")){
            addImageTestSignature((ImageTestSignature)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImagingEnvironment")){
            addImagingEnvironment((ImagingEnvironment)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneCentroid")){
            addPlaneCentroid((PlaneCentroid)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneGeometricMean")){
            addPlaneGeometricMean((PlaneGeometricMean)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneGeometricSigma")){
            addPlaneGeometricSigma((PlaneGeometricSigma)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneMaximum")){
            addPlaneMaximum((PlaneMaximum)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneMean")){
            addPlaneMean((PlaneMean)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneMinimum")){
            addPlaneMinimum((PlaneMinimum)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSigma")){
            addPlaneSigma((PlaneSigma)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSum_i")){
            addPlaneSum_i((PlaneSum_i)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSum_i2")){
            addPlaneSum_i2((PlaneSum_i2)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSum_log_i")){
            addPlaneSum_log_i((PlaneSum_log_i)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSum_Xi")){
            addPlaneSum_Xi((PlaneSum_Xi)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSum_Yi")){
            addPlaneSum_Yi((PlaneSum_Yi)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PlaneSum_Zi")){
            addPlaneSum_Zi((PlaneSum_Zi)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackCentroid")){
            addStackCentroid((StackCentroid)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackGeometricMean")){
            addStackGeometricMean((StackGeometricMean)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackGeometricSigma")){
            addStackGeometricSigma((StackGeometricSigma)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackMaximum")){
            addStackMaximum((StackMaximum)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackMean")){
            addStackMean((StackMean)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackMinimum")){
            addStackMinimum((StackMinimum)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StackSigma")){
            addStackSigma((StackSigma)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("StageLabel")){
            addStageLabel((StageLabel)itCustoms.next(), customNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Thumbnail")){
            addThumbnail((Thumbnail)itCustoms.next(), customNode, df);
          }
        }
      }
    } 
    return root;
  }//end of exportMeta method
  
  /**exports DisplayOptions from the database and adds to the tree*/
  private static void addDisplayOptions(DisplayOptions element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Display Options attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"BlueChannel", "ColorMap","DisplayROIList", "GreenChannel", "GreyChannel",
        "Pixels", "RedChannel", "TStart", "TStop", "Zoom", "ZStart", "ZStop",
        "BlueChannelOn", "DisplayRGB", "GreenChannelOn", "RedChannelOn"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(DisplayOptions)df.retrieve("DisplayOptions", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("DisplayOptions",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("ColorMap", element.getColorMap(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TStart", ""+element.getTStart(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TStop", ""+element.getTStop(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Zoom", ""+element.getZoom(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ZStart", ""+element.getZStart(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ZStop", ""+element.getZStop(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DisplayRGB", ""+element.isDisplayRGB(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("RedChannelOn", ""+element.isRedChannelOn(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("GreenChannelOn", ""+element.isGreenChannelOn(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("BlueChannelOn", ""+element.isBlueChannelOn(),
      XMLObject.ATTRIBUTE)));
    if (element.getPixels()==null);
    else if(element.getPixels() instanceof PixelsNode) node.add(new DefaultMutableTreeNode(new XMLObject(
      "Pixels", ""+((PixelsNode)element.getPixels()).getLSID(), XMLObject.ATTRIBUTE)));
    else node.add(new DefaultMutableTreeNode(new XMLObject(
      "Pixels", ""+element.getPixels().getID(), XMLObject.ATTRIBUTE)));
    //get children of this element
    DefaultMutableTreeNode red=new DefaultMutableTreeNode(new XMLObject("RedChannel"));
    DefaultMutableTreeNode green=new DefaultMutableTreeNode(new XMLObject("GreenChannel"));
    DefaultMutableTreeNode blue=new DefaultMutableTreeNode(new XMLObject("BlueChannel"));
    DefaultMutableTreeNode grey=new DefaultMutableTreeNode(new XMLObject("GreyChannel"));
    double[] redd=addDisplayChannel(element.getRedChannel(), red, df);
    double[] greend=addDisplayChannel(element.getGreenChannel(), green, df);
    double[] blued=addDisplayChannel(element.getBlueChannel(), blue, df);
    double[] greyd=addDisplayChannel(element.getGreyChannel(), grey, df);
    if (!red.isLeaf())node.add(red);
    if (!green.isLeaf())node.add(green);
    if (!blue.isLeaf())node.add(blue);
    if (!grey.isLeaf())node.add(grey);
    //set display characteristics in ImageJ from display channel values
    if (greyd!=null){
      imageP.setMinAndMax(greyd[0],greyd[1]);
      imageP.gamma(greyd[2]);
    }else if(redd!=null){
      imageP.setMinAndMax(redd[0], redd[1]);
      imageP.gamma(redd[2]);
    }else if(blued!=null){
      imageP.setMinAndMax(blued[0], blued[1]);
      imageP.gamma(blued[2]);
    }else if(greend!=null){
      imageP.setMinAndMax(greend[0], greend[1]);
      imageP.gamma(greend[2]);
    }
    List list=element.getDisplayROIList();
    Iterator iter=list.iterator();
    while(iter.hasNext()){
      addDisplayROI((DisplayROI)iter.next(), node, df);
    }
  }//end of addDisplayOptions method
  
  /**exports DisplayChannel from the database, adds it to the tree and returns the
  black level, white level, and gamma of the channel*/
  private static double[] addDisplayChannel(DisplayChannel element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Display Channel attributes.");
    if(element==null)return null;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"BlackLevel", "ChannelNumber", "Gamma", "WhiteLevel"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(DisplayChannel)df.retrieve("DisplayChannel", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("DisplayChannel",
      XMLObject.ELEMENT));
    root.add(node);
    //get levels for display in ImageJ
    Double black=element.getBlackLevel(), white=element.getWhiteLevel();
    Float gamma=element.getGamma();
    double [] levels=new double[3];
    if(black==null||white==null||gamma==null){
      levels=null;
    }
    else{
      levels[0]=black.doubleValue();
      levels[1]=white.doubleValue();
      levels[2]=gamma.doubleValue();
    }
    
    node.add(new DefaultMutableTreeNode(new XMLObject("ChannelNumber",
      ""+element.getChannelNumber(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Gamma",
      ""+gamma, XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("WhiteLevel",
      ""+white, XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("BlackLevel",
      ""+black, XMLObject.ATTRIBUTE)));
    return levels;
  }//end of addDisplayChannel method
  
  /**exports LogicalChannels from the database and adds to the tree*/
  private static void addLogicalChannel(LogicalChannel element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving LogicalChannel attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"PixelChannelComponentList", "AuxLightAttenuation", "AuxLightSource", "AuxLightWavelength",
        "AuxTechnique", "ContrastMethod", "Detector", "DetectorGain",
        "DetectorOffset", "EmissionWavelength", "ExcitationWavelength",
        "Filter", "Fluor", "IlluminationType", "LightAttenuation", 
        "LightSource", "LightWavelength", "Mode", "Name", "NDFilter", "OTF",
        "PhotometricInterpretation", "PinholeSize", "SamplesPerPixel"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(LogicalChannel)df.retrieve("LogicalChannel", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("LogicalChannel",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", element.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Mode", element.getMode(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("PhotometricInterpretation",
      element.getPhotometricInterpretation(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Fluor", element.getFluor(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("IlluminationType", element.getIlluminationType(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ContrastMethod", element.getContrastMethod(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("AuxTechnique", element.getAuxTechnique(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("AuxLightAttenuation",
      ""+element.getAuxLightAttenuation(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("AuxLightWavelength",
      ""+element.getAuxLightWavelength(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DetectorGain",
      ""+element.getDetectorOffset(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("EmissionWavelength",
      ""+element.getEmissionWavelength(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ExcitationWavelength",
      ""+element.getExcitationWavelength(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("LightAttenuation",
      ""+element.getLightAttenuation(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("LightWavelength",
      ""+element.getLightWavelength(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("NDFilter",
      ""+element.getNDFilter(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("PinholeSize",
      ""+element.getPinholeSize(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SamplesPerPixel",
      ""+element.getSamplesPerPixel(), XMLObject.ATTRIBUTE)));
    //get children of this element
    DefaultMutableTreeNode aux=new DefaultMutableTreeNode(new XMLObject(
                "AuxLightSource"));
    node.add(aux);
    addLightSource(element.getAuxLightSource(), aux, df);
    addLightSource(element.getLightSource(), node, df);
    addDetector(element.getDetector(), node, df);
    addFilter(element.getFilter(), node, df);
    addOTF(element.getOTF(), node, df);
  }//end of addLogicalChannel method
  
  /**exports Repositories from the database and adds to the tree*/
  private static void addRepository(Repository element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Repository attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"ImageServerURL", "Path", "IsLocal"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Repository)df.retrieve("Repository", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Repository",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("ImageServerURL", element.getImageServerURL(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Path", element.getPath(),
      XMLObject.ATTRIBUTE)));
   node.add(new DefaultMutableTreeNode(new XMLObject("IsLocal", ""+element.isIsLocal(),
      XMLObject.ATTRIBUTE)));
  }//end of addRepository method
  
  /**exports OTFs from the database and adds to the tree*/
  private static void addOTF(OTF element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving OTF attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Filter","Instrument", "Objective", "Path", "PixelType",
        "Repository", "SizeX",  "SizeY","OpticalAxisAverage" };
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(OTF)df.retrieve("OTF", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("OTF",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("PixelType", element.getPixelType(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Path", element.getPath(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SizeX", ""+element.getSizeX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SizeY", ""+element.getSizeY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("OpticalAxisAverage", ""+element.isOpticalAxisAverage(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addObjective(element.getObjective(), node, df);
    addFilter(element.getFilter(), node, df);
    addInstrument(element.getInstrument(), node, df);
    addRepository(element.getRepository(), node, df);
  }//end of addOTF method
  
  /**exports Filters from the database and adds to the tree*/
  private static void addFilter(Filter element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Filter attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Instrument"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Filter)df.retrieve("Filter", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Filter",
      XMLObject.ELEMENT));
    root.add(node);
    //get children of this element
    addInstrument(element.getInstrument(), node, df);
  }//end of addFilter method
  
  /**exports Detectors from the database and adds to the tree*/
  private static void addDetector(Detector element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Detector attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Instrument");
      criteria.addWantedField("Manufacturer");
      criteria.addWantedField("Model");
      criteria.addWantedField("SerialNumber");
      criteria.addWantedField("Gain");
      criteria.addWantedField("Offset");
      criteria.addWantedField("Type");
      criteria.addWantedField("Voltage");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Detector)df.retrieve("Detector", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Detector",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Manufacturer", element.getManufacturer(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Model", element.getModel(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SerialNumber", element.getSerialNumber(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Type", element.getType(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Gain", ""+element.getGain(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Offset", ""+element.getOffset(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Voltage", ""+element.getVoltage(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addInstrument(element.getInstrument(), node, df);
  }//end of addDetector method
  
  /**exports Lightsources from the database and adds to the tree*/
  private static void addLightSource(LightSource element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Light Source attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Instrument");
      criteria.addWantedField("Manufacturer");
      criteria.addWantedField("Model");
      criteria.addWantedField("SerialNumber");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(LightSource)df.retrieve("LightSource", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("LightSource",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Manufacturer", element.getManufacturer(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Model", element.getModel(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SerialNumber", element.getSerialNumber(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addInstrument(element.getInstrument(), node, df);
  }//end of addLightSource method
  
  /**exports Plates from the database and adds to the tree*/
  private static void addPlate(Plate element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Plate attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Name");
      criteria.addWantedField("ExternalReference");
      criteria.addWantedField("Screen");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Plate)df.retrieve("Plate", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Plate",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", element.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ExternalReference", element.getExternalReference(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addScreen(element.getScreen(), node, df);
  }//end of addPlate method
  
  /**exports Screens from the database and adds to the tree*/
  private static void addScreen(Screen element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Screen attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Name");
      criteria.addWantedField("ExternalReference");
      criteria.addWantedField("Description");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Screen)df.retrieve("Screen", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Screen",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", element.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("ExternalReference", element.getExternalReference(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Description", element.getDescription(),
      XMLObject.ATTRIBUTE)));
  }//end of addScreen method
  
  /**exports Objectives from the database and adds to the tree*/
  private static void addObjective(Objective element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Objective attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Instrument", "LensNA", "Magnification", "Manufacturer",
        "Model", "SerialNumber"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Objective)df.retrieve("Objective", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Objective",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Manufacturer", element.getManufacturer(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Model", element.getModel(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SerialNumber", element.getSerialNumber(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("LensNA", ""+element.getLensNA(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Magnification", ""+element.getMagnification(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addInstrument(element.getInstrument(), node, df);
  }//end of addObjective method
  
  /**exports Instruments from the database and adds to the tree*/
  private static void addInstrument(Instrument element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Instrument attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Manufacturer");
      criteria.addWantedField("Model");
      criteria.addWantedField("SerialNumber");
      criteria.addWantedField("Type");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Instrument)df.retrieve("Instrument", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Instrument",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Manufacturer", element.getManufacturer(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Model", element.getModel(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Type", element.getType(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SerialNumber", element.getSerialNumber(),
      XMLObject.ATTRIBUTE)));
  }//end of addInstrument method
  
  /**exports experiments from the database and adds to the tree*/
  private static void addExperiment(Experiment experiment, DefaultMutableTreeNode root,
    DataFactory df){
    IJ.showStatus("Retrieving Experiment attributes.");
    if(experiment==null)return;
    if(df!=null){
      Criteria criteria=new Criteria();
      criteria.addWantedField("Type");
      criteria.addWantedField("Description");
      criteria.addWantedField("Experimenter");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(experiment.getID())).toString());
      experiment=(Experiment)df.retrieve("Experiment", criteria);
    }  
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Experiment",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Type", experiment.getType(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Description", experiment.getDescription(),
      XMLObject.ATTRIBUTE)));
    addExperimenter(experiment.getExperimenter(), node, df);
  }//end of addExperiment method
  
  /**exports Experimenter from the database and adds to the tree*/
  private static void addExperimenter(Experimenter element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Experimenter attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("LastName");
      criteria.addWantedField("FirstName");
      criteria.addWantedField("Email");
      criteria.addWantedField("DataDirectory");
      criteria.addWantedField("Institution");
      criteria.addWantedField("Group");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Experimenter)df.retrieve("Experimenter", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Experimenter",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("LastName", element.getLastName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("FirstName", element.getFirstName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Email", element.getEmail(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DataDirectory", element.getDataDirectory(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Institution", element.getInstitution(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addGroup(element.getGroup(), node, df);
  }//end of addExperimenter method
  
  /**exports Groups from the database and adds to the tree*/
  private static void addGroup(Group element,DefaultMutableTreeNode root,
    DataFactory df){
    IJ.showStatus("Retrieving Group attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Name");
      criteria.addWantedField("Contact");
      criteria.addWantedField("Leader");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Group)df.retrieve("Group", criteria);
    }
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Group",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", element.getName(),
      XMLObject.ATTRIBUTE)));
    Experimenter contact=element.getContact();
    node.add(new DefaultMutableTreeNode(new XMLObject("Contact", contact.getLastName()+
      ", "+contact.getFirstName()+"<"+contact.getEmail()+">" ,XMLObject.ATTRIBUTE)));
    Experimenter leader=element.getLeader();
    node.add(new DefaultMutableTreeNode(new XMLObject("Leader", leader.getLastName()+
      ", "+leader.getFirstName()+"<"+leader.getEmail()+">" ,XMLObject.ATTRIBUTE)));
  }//end of addGroup method
  
  /**exports categories from the database and adds to the tree*/
  private static void addCategory(Category element,DefaultMutableTreeNode root,
    DataFactory df){
    IJ.showStatus("Retrieving Category attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Name");
      criteria.addWantedField("Description");
      criteria.addWantedField("CategoryGroup");
      criteria.addWantedField("id");
     criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Category)df.retrieve("Category", criteria);
    }
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Category",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", element.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Description", element.getDescription(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    addCategoryGroup(element.getCategoryGroup(), node, df);
  }//end of addCategory method
  
  /**exports categoryGroups from the database and adds to the tree*/
  private static void addCategoryGroup(CategoryGroup cg,DefaultMutableTreeNode root,
    DataFactory df){
    IJ.showStatus("Retrieving Category Group attributes.");
    if(cg==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      Criteria criteria=new Criteria();
      criteria.addWantedField("Name");
      criteria.addWantedField("Description");
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(cg.getID())).toString());
      //retrieve element to add with all columns loaded
      cg=(CategoryGroup)df.retrieve("CategoryGroup", criteria);
    }
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("CategoryGroup",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", cg.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Description", cg.getDescription(),
      XMLObject.ATTRIBUTE)));
  }//end of addCategoryGroup method
  
  /**exports all attributes stemming from the pixels from the ome database*/
  private static void exportPixelMeta(Pixels pixels,
    DefaultMutableTreeNode pixelNode, DataFactory df){
    IJ.showStatus("Retrieving Pixel attributes.");
    if(pixels==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] pixelAttrs={"FileSHA1", "ImageServerID",
        "PixelType", "Repository", "SizeC", "SizeT", "SizeX", "SizeY", "SizeZ"};
      Criteria pixelCriteria=OMEDownload.makeAttributeFields(pixelAttrs);
      pixelCriteria.addWantedField("id");
      pixelCriteria.addFilter("id", (new Integer(pixels.getID())).toString());
      //retrieve element to add with all columns loaded
      pixels=(Pixels)df.retrieve("Pixels", pixelCriteria);
    }
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("PixelType", pixels.getPixelType(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("SizeX", ""+pixels.getSizeX(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("SizeY", ""+pixels.getSizeY(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("SizeZ", ""+pixels.getSizeZ(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("SizeT", ""+pixels.getSizeT(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("SizeC", ""+pixels.getSizeC(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("FileSHA1", pixels.getFileSHA1(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("ImageServerID", ""+pixels.getImageServerID(), XMLObject.PIXELS)));
    pixelNode.add(new DefaultMutableTreeNode(
      new XMLObject("FileSHA1", pixels.getFileSHA1(), XMLObject.PIXELS)));
    addRepository(pixels.getRepository(), pixelNode, df);
    
    List channeli=null;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"ColorDomain", "Index", "LogicalChannel", "Pixels"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("Pixels", (new Integer(pixels.getID())).toString());
      channeli=df.retrieveList("PixelChannelComponent", criteria);
    }else channeli=pixels.getPixelChannelComponentList();
    Iterator iter=channeli.iterator();
    while (iter.hasNext()){
      addPixelChannelComponent((PixelChannelComponent)iter.next(), pixelNode, df);
    }
    //setup and load the displayOptions related to the default pixels
    List channeld=null;
    if(df!=null){
      Criteria displayCriteria=new Criteria();
      displayCriteria.addWantedField("Pixels");
      displayCriteria.addFilter("Pixels", (new Integer(pixels.getID())).toString());
      channeld=df.retrieveList("DisplayOptions", displayCriteria);
    }else channeld=pixels.getDisplayOptionsList();
    Iterator iterDis=channeld.iterator();
    while (iterDis.hasNext()){
      addDisplayOptions((DisplayOptions)iterDis.next(), pixelNode, df);
    }
  }//end of exportPixelMeta method
  
  /**exports channelIndexes from the database and adds to the tree*/
  private static void addPixelChannelComponent(PixelChannelComponent element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Pixel Channel Component attributes.");
    if(element==null)return;
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PixelChannelComponent",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("ColorDomain", ""+element.getColorDomain(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Index", ""+element.getIndex(),
      XMLObject.ATTRIBUTE)));
    addLogicalChannel(element.getLogicalChannel(), node, df);
  }//end of addPixelChannelComponent method  
  
  /**exports TrajectoryEntries from the database and adds to the tree*/
  private static void addTrajectoryEntry(TrajectoryEntry element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Trajectory Entry attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] elementAttrs={"DeltaX","DeltaY", "DeltaZ", "Distance", "Order",
        "Trajectory", "Velocity"};
      Criteria criteria=OMEDownload.makeAttributeFields(elementAttrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(TrajectoryEntry)df.retrieve("TrajectoryEntry", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("TrajectoryEntry",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Velocity", ""+element.getVelocity(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Distance", ""+element.getDistance(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Order", ""+element.getOrder(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DeltaX", ""+element.getDeltaX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DeltaY", ""+element.getDeltaY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("DeltaZ", ""+element.getDeltaZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addTrajectoryEntry method  
  
  /**exports Trajectories from the database and adds to the tree*/
  private static void addTrajectory(Trajectory element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Trajectory attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"AverageVelocity","Name", "TotalDistance"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Trajectory)df.retrieve("Trajectory", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Trajectory",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", element.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("AverageVelocity", ""+element.getAverageVelocity(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TotalDistance", ""+element.getTotalDistance(),
      XMLObject.ATTRIBUTE)));
    //add children attributes
    List list=element.getTrajectoryEntryList();
    Iterator iter=list.iterator();
    while(iter.hasNext()){
      addTrajectoryEntry((TrajectoryEntry)iter.next(), node, df);
    }
  }//end of addTrajectory method  
  
  private static void exportMeta(Feature feature, DefaultMutableTreeNode root,
    DataFactory df){
    IJ.showStatus("Retrieving Feature attributes.");
    System.out.println("feature output");
    DefaultMutableTreeNode featureNode=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.FEATUREHEADING));
    root.add(featureNode);
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", feature.getName(), XMLObject.FEATURE)));
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Tag", feature.getTag(), XMLObject.FEATURE)));
    //add trajectory entries
    List trajects=null;
    if(df!=null){
      //These fields don't have to be loaded because
      //they will be when addTrajectory is called
      //String [] attrs={"DeltaX","DeltaY", "DeltaZ", "Distance",
      //"Order", "Trajectory", "Velocity"};
      Criteria trajCriteria=new Criteria();//=OMEDownload.makeAttributeFields(attrs);
      trajCriteria.addWantedField("feature_id");
      trajCriteria.addFilter("feature_id", (new Integer(feature.getID())).toString());
      //retrieve element to add with all columns loaded
      trajects=df.retrieveList("Trajectory", trajCriteria);
    }else trajects=((FeatureNode)feature).getTrajectoryList();
    Iterator iterTra=trajects.iterator();
    while (iterTra.hasNext()){
      addTrajectory((Trajectory)iterTra.next(), featureNode, df);
    }
    //add attributes to this element's node
    if(df!=null){
      for (int i=0; i<OMEMetaPanel.FEATURE_TYPES.length; i++){
        Criteria criteria=new Criteria();
        criteria.addWantedField("feature_id");
        criteria.addFilter("feature_id", (new Integer(feature.getID())).toString());
        IJ.showStatus("Retrieving  "+OMEMetaPanel.FEATURE_TYPES[i]+"s.");
        List customs=null;
        try{
          customs=df.retrieveList(OMEMetaPanel.FEATURE_TYPES[i], criteria);
        }catch(Exception e){
          //don't tell anyone about it, just keep going
          e.printStackTrace();
          IJ.showStatus("Error while retrieving "+OMEMetaPanel.FEATURE_TYPES[i]+"s.");
        }
        if(customs!=null){
          Iterator itCustoms=customs.iterator();
          while (itCustoms.hasNext()){
            if(OMEMetaPanel.IMAGE_TYPES[i].equals("Bounds")){
              addBounds((Bounds)itCustoms.next(), featureNode, df);
            }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Extent")){
              addExtent((Extent)itCustoms.next(), featureNode, df);
            }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Location")){
              addLocation((Location)itCustoms.next(), featureNode, df);
            }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Ratio")){
              addRatio((Ratio)itCustoms.next(), featureNode, df);
            }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Signal")){
              addSignal((Signal)itCustoms.next(), featureNode, df);
            }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Threshold")){
              addThreshold((Threshold)itCustoms.next(), featureNode, df);
            }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Timepoint")){
              addTimepoint((Timepoint)itCustoms.next(), featureNode, df);
            }
          }
        }
      }
    }else exportMeta((FeatureNode)feature, featureNode);
    //add children features to tree
    List features=feature.getChildren();
    Iterator iter=features.iterator();
    while (iter.hasNext()){
      exportMeta((Feature)iter.next(), featureNode, df);
    }
  }//end of exportMeta method
  
  /**exports Dimensions from the database and adds to the tree*/
  private static void addDimensions(Dimensions element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Dimensions attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"PixelSizeC", "PixelSizeT", "PixelSizeX", "PixelSizeY", "PixelSizeZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Dimensions)df.retrieve("Dimensions", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Dimensions",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("PixelSizeC", ""+element.getPixelSizeC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("PixelSizeT", ""+element.getPixelSizeT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("PixelSizeX", ""+element.getPixelSizeX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("PixelSizeY", ""+element.getPixelSizeY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("PixelSizeZ", ""+element.getPixelSizeZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addDimensions method
  
  /**exports DisplayROI from the database and adds to the tree*/
  private static void addDisplayROI(DisplayROI element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving DisplayROI attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"DisplayOptions", "TO", "T1", "X0", "X1", "Y0", "Y1", "Z0", "Z1"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(DisplayROI)df.retrieve("DisplayROI", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("DisplayROI",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("T0", ""+element.getT0(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("T1", ""+element.getT1(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("X0", ""+element.getX0(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("X1", ""+element.getX1(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Y0", ""+element.getY0(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Y1", ""+element.getY1(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Z0", ""+element.getZ0(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Z1", ""+element.getZ1(),
      XMLObject.ATTRIBUTE)));
  }//end of addDisplayROI method
  
  /**exports ImageAnnotation from the database and adds to the tree*/
  private static void addImageAnnotation(ImageAnnotation element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImageAnnotation attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      //these two columns don't work "WEIRD"
      String [] attrs={"Content",/* "Experimenter",*/ "TheC", "TheT", "TheZ",/*"Timestamp",*/ "Valid"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImageAnnotation)df.retrieve("ImageAnnotation", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImageAnnotation",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Content", ""+element.getContent(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    //this won't work as long as timestamp above doesn't load "WEIRD"
//    node.add(new DefaultMutableTreeNode(new XMLObject("Timestamp", ""+element.getTimestamp(),
//      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Valid", ""+element.isValid(),
      XMLObject.ATTRIBUTE)));
    //This will not work as long as Experimenter above won't load "WEIRD"
//    addExperimenter(element.getExperimenter(), node, df);
  }//end of addImageAnnotation method
  
  /**exports ImageExperiment from the database and adds to the tree*/
  private static void addImageExperiment(ImageExperiment element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImageExperiment attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Experiment"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImageExperiment)df.retrieve("ImageExperiment", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImageExperiment",
      XMLObject.ELEMENT));
    root.add(node);
    addExperiment(element.getExperiment(), node, df);
  }//end of addImageExperiment method
  
  /**exports ImageGroup from the database and adds to the tree*/
  private static void addImageGroup(ImageGroup element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImageGroup attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Group"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImageGroup)df.retrieve("ImageGroup", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImageGroup",
      XMLObject.ELEMENT));
    root.add(node);
    addGroup(element.getGroup(), node, df);
  }//end of addImageGroup method
  
  /**exports ImageInstrument from the database and adds to the tree*/
  private static void addImageInstrument(ImageInstrument element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImageInstrument attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Instrument", "Objective"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImageInstrument)df.retrieve("ImageInstrument", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImageInstrument",
      XMLObject.ELEMENT));
    root.add(node);
    addInstrument(element.getInstrument(), node, df);
    addObjective(element.getObjective(), node, df);
  }//end of addImageInstrument method
  
  /**exports ImagePlate from the database and adds to the tree*/
  private static void addImagePlate(ImagePlate element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImagePlate attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Plate", "Sample", "Well"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImagePlate)df.retrieve("ImagePlate", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImagePlate",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Well", ""+element.getWell(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Sample", ""+element.getSample(),
      XMLObject.ATTRIBUTE)));
    addPlate(element.getPlate(), node, df);
  }//end of addImagePlate method
  
  /**exports ImageTestSignature from the database and adds to the tree*/
  private static void addImageTestSignature(ImageTestSignature element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImageTestSignature attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Value"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImageTestSignature)df.retrieve("ImageTestSignature", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImageTestSignature",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Value", ""+element.getValue(),
      XMLObject.ATTRIBUTE)));
  }//end of addImageTestSignature method
  
  /**exports ImagingEnvironment from the database and adds to the tree*/
  private static void addImagingEnvironment(ImagingEnvironment element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving ImagingEnvironment attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"AirPressure", "CO2Percent", "Humidity", "Temperature"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(ImagingEnvironment)df.retrieve("ImagingEnvironment", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("ImagingEnvironment",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("AirPressure", ""+element.getAirPressure(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("CO2Percent", ""+element.getCO2Percent(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Humidity", ""+element.getHumidity(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Temperature", ""+element.getTemperature(),
      XMLObject.ATTRIBUTE)));
  }//end of addImagingEnvironment method
  
  /**exports PlaneCentroid from the database and adds to the tree*/
  private static void addPlaneCentroid(PlaneCentroid element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneCentroid attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"TheC", "TheT", "TheZ", "X", "Y"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneCentroid)df.retrieve("PlaneCentroid", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneCentroid",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("X", ""+element.getX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Y", ""+element.getY(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneCentroid method
  
  /**exports PlaneGeometricMean from the database and adds to the tree*/
  private static void addPlaneGeometricMean(PlaneGeometricMean element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneGeometricMean attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"GeometricMean", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneGeometricMean)df.retrieve("PlaneGeometricMean", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneGeometricMean",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("GeometricMean", ""+element.getGeometricMean(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneGeometricMean method
  
  /**exports PlaneGeometricSigma from the database and adds to the tree*/
  private static void addPlaneGeometricSigma(PlaneGeometricSigma element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneGeometricSigma attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"GeometricSigma", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneGeometricSigma)df.retrieve("PlaneGeometricSigma", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneGeometricSigma",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("GeometricSigma", ""+element.getGeometricSigma(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneGeometricSigma method
  
  /**exports PlaneMaximum from the database and adds to the tree*/
  private static void addPlaneMaximum(PlaneMaximum element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneMaximum attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Maximum", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneMaximum)df.retrieve("PlaneMaximum", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneMaximum",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Maximum", ""+element.getMaximum(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneMaximum method
  
  /**exports PlaneMean from the database and adds to the tree*/
  private static void addPlaneMean(PlaneMean element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneMean attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Mean", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneMean)df.retrieve("PlaneMean", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneMean",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Mean", ""+element.getMean(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneMean method
  
  /**exports PlaneMinimum from the database and adds to the tree*/
  private static void addPlaneMinimum(PlaneMinimum element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneMinimum attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Minimum", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneMinimum)df.retrieve("PlaneMinimum", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneMinimum",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Minimum", ""+element.getMinimum(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneMinimum method
  
  /**exports PlaneSigma from the database and adds to the tree*/
  private static void addPlaneSigma(PlaneSigma element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSigma attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sigma", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSigma)df.retrieve("PlaneSigma", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSigma",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sigma", ""+element.getSigma(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSigma method
  
  /**exports PlaneSum_i from the database and adds to the tree*/
  private static void addPlaneSum_i(PlaneSum_i element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSum_i attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sum_i", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSum_i)df.retrieve("PlaneSum_i", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSum_i",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sum_i", ""+element.getSum_i(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSum_i method
  
  /**exports PlaneSum_i2 from the database and adds to the tree*/
  private static void addPlaneSum_i2(PlaneSum_i2 element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSum_i2 attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sum_i2", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSum_i2)df.retrieve("PlaneSum_i2", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSum_i2",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sum_i2", ""+element.getSum_i2(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSum_i2 method
  
  /**exports PlaneSum_log_i from the database and adds to the tree*/
  private static void addPlaneSum_log_i(PlaneSum_log_i element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSum_log_i attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sum_log_i", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSum_log_i)df.retrieve("PlaneSum_log_i", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSum_log_i",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sum_log_i", ""+element.getSum_log_i(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSum_log_i method
  
  /**exports PlaneSum_Xi from the database and adds to the tree*/
  private static void addPlaneSum_Xi(PlaneSum_Xi element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSum_Xi attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sum_Xi", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSum_Xi)df.retrieve("PlaneSum_Xi", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSum_Xi",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sum_Xi", ""+element.getSum_Xi(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSum_Xi method
  
  /**exports PlaneSum_Yi from the database and adds to the tree*/
  private static void addPlaneSum_Yi(PlaneSum_Yi element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSum_Yi attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sum_Yi", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSum_Yi)df.retrieve("PlaneSum_Yi", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSum_Yi",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sum_Yi", ""+element.getSum_Yi(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSum_Yi method
  
  /**exports PlaneSum_Zi from the database and adds to the tree*/
  private static void addPlaneSum_Zi(PlaneSum_Zi element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving PlaneSum_Zi attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sum_Zi", "TheC", "TheT", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(PlaneSum_Zi)df.retrieve("PlaneSum_Zi", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("PlaneSum_Zi",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sum_Zi", ""+element.getSum_Zi(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addPlaneSum_Zi method
  
  /**exports StackCentroid from the database and adds to the tree*/
  private static void addStackCentroid(StackCentroid element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackCentroid attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"TheC", "TheT", "X", "Y", "Z"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackCentroid)df.retrieve("StackCentroid", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackCentroid",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("X", ""+element.getX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Y", ""+element.getY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Z", ""+element.getZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackCentroid method
  
  /**exports StackGeometricMean from the database and adds to the tree*/
  private static void addStackGeometricMean(StackGeometricMean element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackGeometricMean attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"GeometricMean","TheC", "TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackGeometricMean)df.retrieve("StackGeometricMean", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackGeometricMean",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("GeometricMean", ""+element.getGeometricMean(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackGeometricMean method
  
  /**exports StackGeometricSigma from the database and adds to the tree*/
  private static void addStackGeometricSigma(StackGeometricSigma element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackGeometricSigma attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"GeometricSigma","TheC", "TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackGeometricSigma)df.retrieve("StackGeometricSigma", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackGeometricSigma",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("GeometricSigma", ""+element.getGeometricSigma(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackGeometricSigma method
  
  /**exports StackMaximum from the database and adds to the tree*/
  private static void addStackMaximum(StackMaximum element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackMaximum attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Maximum","TheC", "TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackMaximum)df.retrieve("StackMaximum", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackMaximum",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Maximum", ""+element.getMaximum(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackMaximum method
  
  /**exports StackMean from the database and adds to the tree*/
  private static void addStackMean(StackMean element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackMean attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Mean","TheC", "TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackMean)df.retrieve("StackMean", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackMean",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Mean", ""+element.getMean(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackMean method
  
  /**exports StackMinimum from the database and adds to the tree*/
  private static void addStackMinimum(StackMinimum element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackMinimum attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Minimum","TheC", "TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackMinimum)df.retrieve("StackMinimum", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackMinimum",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Minimum", ""+element.getMinimum(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackMinimum method
  
  /**exports StackSigma from the database and adds to the tree*/
  private static void addStackSigma(StackSigma element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StackSigma attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Sigma","TheC", "TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StackSigma)df.retrieve("StackSigma", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StackSigma",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Sigma", ""+element.getSigma(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addStackSigma method
  
  /**exports StageLabel from the database and adds to the tree*/
  private static void addStageLabel(StageLabel element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving StageLabel attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Name","X", "Y", "Z"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(StageLabel)df.retrieve("StageLabel", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("StageLabel",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Name", ""+element.getName(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("X", ""+element.getX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Y", ""+element.getY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Z", ""+element.getZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addStageLabel method
  
  /**exports Thumbnail from the database and adds to the tree*/
  private static void addThumbnail(Thumbnail element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Thumbnail attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"MimeType","Path", "Repository"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Thumbnail)df.retrieve("Thumbnail", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Thumbnail",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("MimeType", ""+element.getMimeType(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Path", ""+element.getPath(),
      XMLObject.ATTRIBUTE)));
    addRepository(element.getRepository(), node, df);
  }//end of addThumbnail method
  
  /**exports Classification from the database and adds to the tree*/
  private static void addClassification(Classification element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Classification attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Category", "Confidence", "Valid"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Classification)df.retrieve("Classification", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Classification",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Confidence", ""+element.getConfidence(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Valid", ""+element.isValid(),
      XMLObject.ATTRIBUTE)));
    addCategory(element.getCategory(), node, df);
  }//end of addClassification method
  
  /**exports Bounds from the database and adds to the tree*/
  private static void addBounds(Bounds element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Bounds attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Height", "Width", "X", "Y"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Bounds)df.retrieve("Bounds", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Bounds",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Height", ""+element.getHeight(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Width", ""+element.getWidth(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("X", ""+element.getX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Y", ""+element.getY(),
      XMLObject.ATTRIBUTE)));
  }//end of addBounds method
  
  /**exports Extent from the database and adds to the tree*/
  private static void addExtent(Extent element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Extent attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"FormFacter", "MaxX", "MaxY", "MaxZ", "MinX", "MinY", "MinZ", "Perimeter", "SigmaX",
      "SigmaY", "SigmaZ", "SurfaceArea", "Volume"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Extent)df.retrieve("Extent", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Extent",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("FormFactor", ""+element.getFormFactor(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("MaxX", ""+element.getMaxX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("MaxY", ""+element.getMaxY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("MaxZ", ""+element.getMaxZ(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("MinX", ""+element.getMinX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("MinY", ""+element.getMinY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("MinZ", ""+element.getMinZ(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Perimeter", ""+element.getPerimeter(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SigmaX", ""+element.getSigmaX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SigmaY", ""+element.getSigmaY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SigmaZ", ""+element.getSigmaZ(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("SurfaceArea", ""+element.getSurfaceArea(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Volume", ""+element.getVolume(),
      XMLObject.ATTRIBUTE)));
  }//end of addExtent method
  
  /**exports Location from the database and adds to the tree*/
  private static void addLocation(Location element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Location attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"TheX", "TheY", "TheZ"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Location)df.retrieve("Location", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Location",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("TheX", ""+element.getTheX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheY", ""+element.getTheY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheZ", ""+element.getTheZ(),
      XMLObject.ATTRIBUTE)));
  }//end of addLocation method
  
  /**exports Ratio from the database and adds to the tree*/
  private static void addRatio(Ratio element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Ratio attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Ratio"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Ratio)df.retrieve("Ratio", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Ratio",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Ratio", ""+element.getRatio(),
      XMLObject.ATTRIBUTE)));
  }//end of addRatio method
  
  /**exports Signal from the database and adds to the tree*/
  private static void addSignal(Signal element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Signal attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Background", "CentroidX", "CentroidY", "CentroidZ", "GeometricMean",
      "GeometricSigma", "Integral", "Mean", "Sigma", "TheC"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Signal)df.retrieve("Signal", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Signal",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Background", ""+element.getBackground(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("CentroidX", ""+element.getCentroidX(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("CentroidY", ""+element.getCentroidY(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("CentroidZ", ""+element.getCentroidZ(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("GeometricMean", ""+element.getGeometricMean(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("GeometricSigma", ""+element.getGeometricSigma(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Integral", ""+element.getIntegral(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Mean", ""+element.getMean(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Sigma", ""+element.getSigma(),
      XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("TheC", ""+element.getTheC(),
      XMLObject.ATTRIBUTE)));
  }//end of addSignal method
  
  /**exports Threshold from the database and adds to the tree*/
  private static void addThreshold(Threshold element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Threshold attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"Threshold"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Threshold)df.retrieve("Threshold", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Threshold",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("Threshold", ""+element.getThreshold(),
      XMLObject.ATTRIBUTE)));
  }//end of addThreshold method
  
  /**exports Timepoint from the database and adds to the tree*/
  private static void addTimepoint(Timepoint element, DefaultMutableTreeNode root, 
    DataFactory df){
    IJ.showStatus("Retrieving Timepoint attributes.");
    if(element==null)return;
    if(df!=null){
      //setup and load the columns that this element has
      String [] attrs={"TheT"};
      Criteria criteria=OMEDownload.makeAttributeFields(attrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(element.getID())).toString());
      //retrieve element to add with all columns loaded
      element=(Timepoint)df.retrieve("Timepoint", criteria);
    }
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("Timepoint",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("TheT", ""+element.getTheT(),
      XMLObject.ATTRIBUTE)));
  }//end of addTimepoint method
  
}//end of OMEMetaDataHandler class