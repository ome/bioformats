
//only needed to show status, otherwise independent
import ij.IJ;

import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.st.*;
import loci.ome.xml.*;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 * OMEMetaDataHandler is the class that handles
 * the export of metadata
 * associated with an image and OME.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEMetaDataHandler{
  
  /**Method that downloads metadata from the OME database and creates a tree*/
  public static DefaultMutableTreeNode exportMeta(Image image, DataFactory df){
    //create root node of whole tree
    DefaultMutableTreeNode root=new DefaultMutableTreeNode(
      new XMLObject("Meta Data"));
    //image node
    DefaultMutableTreeNode imageNode=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.IMAGEHEADING));
    root.add(imageNode);
    //nodes under image
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", image.getName(), XMLObject.IMAGE)));
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("ID", ""+image.getID(), XMLObject.IMAGE)));
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("Creation Date", image.getCreated(), XMLObject.IMAGE)));
    imageNode.add(new DefaultMutableTreeNode(
      new XMLObject("Description", image.getDescription(), XMLObject.IMAGE)));
    //get pixel information
    DefaultMutableTreeNode pixelNode=new DefaultMutableTreeNode(
        new XMLObject(XMLObject.PIXELHEADING));
    imageNode.add(pixelNode);
    Pixels pixels=image.getDefaultPixels();
    exportPixelMeta(pixels, pixelNode, df);
    //get dataset references
    DefaultMutableTreeNode datasets=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.DATASETREF));
    List data=image.getDatasets();
    Iterator iterator=data.iterator();
    String s="";
    while (iterator.hasNext()) {
      s.concat(((Dataset)iterator.next()).getID()+", ");
    }
    if (s.length()!=0 && s.length()!=2){
      imageNode.add(datasets);
      datasets.add(new DefaultMutableTreeNode(
        new XMLObject("Dataset IDs", s.substring(0, s.length()-3),XMLObject.READONLY)));
    }
    //get the image element features
    List featureList=image.getFeatures();
    Iterator fIter=featureList.iterator();
    while(fIter.hasNext()){
      exportMeta((Feature)fIter.next(), imageNode, df);
    }

    //load custom attributes
    DefaultMutableTreeNode customNode=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.CUSTOMHEADING));
    imageNode.add(customNode);
    for (int i=0; i<OMEMetaPanel.IMAGE_TYPES.length; i++){
      Criteria criteria= OMEDownload.makeAttributeFields(OMEMetaPanel.IMAGE_ATTRS[i]);
      criteria.addWantedField("image_id");
      criteria.addFilter("image_id", (new Integer(image.getID())).toString());
      IJ.showStatus("Retrieving "+OMEMetaPanel.IMAGE_TYPES[i]+"s.");
      List customs=null;
      try{
        customs=df.retrieveList(OMEMetaPanel.IMAGE_TYPES[i], criteria);
      }catch(Exception e){
        e.printStackTrace();
        System.out.println("Error while retrieving "+OMEMetaPanel.IMAGE_TYPES[i]+"s.");
      }
      if(customs!=null){
        Iterator itCustoms=customs.iterator();
        while (itCustoms.hasNext()){
          AttributeDTO attr=(AttributeDTO)itCustoms.next();
          DefaultMutableTreeNode elementNode=new DefaultMutableTreeNode(
            new XMLObject(OMEMetaPanel.IMAGE_TYPES[i], XMLObject.ELEMENT));
          customNode.add(elementNode);
          Map map = attr.getMap();
          Set set = map.keySet();
          Iterator iter2 = set.iterator();
          while (iter2.hasNext()) {
            Object key = iter2.next();
            Object value = map.get(key);
            if((value instanceof String) || (value instanceof Number) || 
              (value instanceof Boolean)){
              elementNode.add(new DefaultMutableTreeNode(new XMLObject((String)key,
                ""+value, XMLObject.ATTRIBUTE)));
            }
          }
          if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageExperiment")){
            addExperiment(((ImageExperiment)attr).getExperiment(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("Classification")){
            addCategory(((Classification)attr).getCategory(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageInstrument")){
            addInstrument(((ImageInstrument)attr).getInstrument(), elementNode, df);
            addObjective(((ImageInstrument)attr).getObjective(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageAnnotation")){
            addExperimenter(((ImageAnnotation)attr).getExperimenter(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImageGroup")){
            addGroup(((ImageGroup)attr).getGroup(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("ImagePlate")){
            addPlate(((ImagePlate)attr).getPlate(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("DisplayROI")){
            addDisplayOptions(((DisplayROI)attr).getDisplayOptions(), elementNode, df);
          }else if(OMEMetaPanel.IMAGE_TYPES[i].equals("PixelChannelComponent")){
            addLogicalChannel(((PixelChannelComponent)attr).getLogicalChannel(), elementNode, df);
          }
        }
      }
    } 
    return root;
  }//end of exportMeta method
  
  /**exports DisplayOptions from the database and adds to the tree*/
  private static void addDisplayOptions(DisplayOptions element, DefaultMutableTreeNode root, 
    DataFactory df){
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"BlueChannel", "ColorMap", "GreenChannel", "GreyChannel",
      "Pixels", "RedChannel", "TStart", "TStop", "Zoom", "ZStart", "ZStop",
      "BlueChannelOn", "DisplayRGB", "GreenChannelOn", "RedChannelOn"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(DisplayOptions)df.retrieve("DisplayOptions", criteria);
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
    node.add(new DefaultMutableTreeNode(new XMLObject("Pixels", ""+element.getPixels().getID(),
      XMLObject.ATTRIBUTE)));
    //get children of this element
    DefaultMutableTreeNode red=new DefaultMutableTreeNode(new XMLObject("RedChannel"));
    node.add(red);
    DefaultMutableTreeNode green=new DefaultMutableTreeNode(new XMLObject("GreenChannel"));
    node.add(green);
    DefaultMutableTreeNode blue=new DefaultMutableTreeNode(new XMLObject("BlueChannel"));
    node.add(blue);
    DefaultMutableTreeNode grey=new DefaultMutableTreeNode(new XMLObject("GreyChannel"));
    node.add(grey);
    addDisplayChannel(element.getRedChannel(), red, df);
    addDisplayChannel(element.getGreenChannel(), green, df);
    addDisplayChannel(element.getBlueChannel(), blue, df);
    addDisplayChannel(element.getBlueChannel(), grey, df);
  }//end of addDisplayOptions method
  
  /**exports DisplayChannel from the database and adds to the tree*/
  private static void addDisplayChannel(DisplayChannel element, DefaultMutableTreeNode root, 
    DataFactory df){
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"BlackLevel", "ChannelNumber", "Gamma", "WhiteLevel"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(DisplayChannel)df.retrieve("DisplayChannel", criteria);
    //add attributes to this element's node
    DefaultMutableTreeNode node=new DefaultMutableTreeNode(new XMLObject("DisplayChannel",
      XMLObject.ELEMENT));
    root.add(node);
    node.add(new DefaultMutableTreeNode(new XMLObject("ChannelNumber",
      ""+element.getChannelNumber(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("Gamma",
      ""+element.getGamma(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("WhiteLevel",
      ""+element.getWhiteLevel(), XMLObject.ATTRIBUTE)));
    node.add(new DefaultMutableTreeNode(new XMLObject("BlackLevel",
      ""+element.getBlackLevel(), XMLObject.ATTRIBUTE)));
  }//end of addDisplayChannel method
  
  /**exports LogicalChannels from the database and adds to the tree*/
  private static void addLogicalChannel(LogicalChannel element, DefaultMutableTreeNode root, 
    DataFactory df){
    if(element==null)return;
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
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"ImageServerURL", "Path", "IsLocal"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Repository)df.retrieve("Repository", criteria);
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
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"Filter","Instrument", "Objective", "Path", "PixelType",
      "Repository", "SizeX",  "SizeY","OpticalAxisAverage" };
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(OTF)df.retrieve("OTF", criteria);
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
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"Instrument"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Filter)df.retrieve("Filter", criteria);
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
    if(element==null)return;
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
    if(element==null)return;
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
    if(element==null)return;
    //setup and load the columns that this element has
    Criteria criteria=new Criteria();
    criteria.addWantedField("Name");
    criteria.addWantedField("ExternalReference");
    criteria.addWantedField("Screen");
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Plate)df.retrieve("Plate", criteria);
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
    if(element==null)return;
    //setup and load the columns that this element has
    Criteria criteria=new Criteria();
    criteria.addWantedField("Name");
    criteria.addWantedField("ExternalReference");
    criteria.addWantedField("Description");
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Screen)df.retrieve("Screen", criteria);
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
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"Instrument", "LensNA", "Magnification", "Manufacturer",
      "Model", "SerialNumber"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Objective)df.retrieve("Objective", criteria);
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
    if(element==null)return;
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
    if(experiment==null)return;
    Criteria criteria=new Criteria();
    criteria.addWantedField("Type");
    criteria.addWantedField("Description");
    criteria.addWantedField("Experimenter");
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(experiment.getID())).toString());
    experiment=(Experiment)df.retrieve("Experiment", criteria);
    
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
    if(element==null)return;
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
    if(element==null)return;
    //setup and load the columns that this element has
    Criteria criteria=new Criteria();
    criteria.addWantedField("Name");
    criteria.addWantedField("Contact");
    criteria.addWantedField("Leader");
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Group)df.retrieve("Group", criteria);
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
    if(element==null)return;
    //setup and load the columns that this element has
    Criteria criteria=new Criteria();
    criteria.addWantedField("Name");
    criteria.addWantedField("Description");
    criteria.addWantedField("CategoryGroup");
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Category)df.retrieve("Category", criteria);
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
    if(cg==null)return;
    //setup and load the columns that this element has
    Criteria criteria=new Criteria();
    criteria.addWantedField("Name");
    criteria.addWantedField("Description");
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(cg.getID())).toString());
    //retrieve element to add with all columns loaded
    cg=(CategoryGroup)df.retrieve("CategoryGroup", criteria);
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
    if(pixels==null)return;
    //setup and load the columns that this element has
    String [] pixelAttrs={"FileSHA1", "ImageServerID",
      "PixelType", "Repository", "SizeC", "SizeT", "SizeX", "SizeY", "SizeZ"};
    Criteria pixelCriteria=OMEDownload.makeAttributeFields(pixelAttrs);
    pixelCriteria.addWantedField("id");
    pixelCriteria.addFilter("id", (new Integer(pixels.getID())).toString());
    //retrieve element to add with all columns loaded
    pixels=(Pixels)df.retrieve("Pixels", pixelCriteria);
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
    
    //setup and load the columns that this element has
    String [] attrs={"ColorDomain", "Index", "LogicalChannel", "Pixels"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("Pixels", (new Integer(pixels.getID())).toString());
    List channeli=df.retrieveList("PixelChannelComponent", criteria);
    Iterator iter=channeli.iterator();
    while (iter.hasNext()){
      addPixelChannelComponent((PixelChannelComponent)iter.next(), pixelNode, df);
    }
    //setup and load the displayOptions related to the default pixels
    Criteria displayCriteria=new Criteria();
    displayCriteria.addWantedField("Pixels");
    displayCriteria.addFilter("Pixels", (new Integer(pixels.getID())).toString());
    List channeld=df.retrieveList("DisplayOptions", displayCriteria);
    Iterator iterDis=channeld.iterator();
    while (iterDis.hasNext()){
      addDisplayOptions((DisplayOptions)iterDis.next(), pixelNode, df);
    }
  }//end of exportPixelMeta method
  
  /**exports channelIndexes from the database and adds to the tree*/
  private static void addPixelChannelComponent(PixelChannelComponent element, DefaultMutableTreeNode root, 
    DataFactory df){
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
  }//end of addChannelIndex method  
  
  /**exports TrajectoryEntries from the database and adds to the tree*/
  private static void addTrajectoryEntry(TrajectoryEntry element, DefaultMutableTreeNode root, 
    DataFactory df){
    if(element==null)return;
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
    //add children attributes
    addTrajectory(element.getTrajectory(), node, df);
  }//end of addTrajectoryEntry method  
  
  /**exports Trajectories from the database and adds to the tree*/
  private static void addTrajectory(Trajectory element, DefaultMutableTreeNode root, 
    DataFactory df){
    if(element==null)return;
    //setup and load the columns that this element has
    String [] attrs={"AverageVelocity","Name", "TotalDistance"};
    Criteria criteria=OMEDownload.makeAttributeFields(attrs);
    criteria.addWantedField("id");
    criteria.addFilter("id", (new Integer(element.getID())).toString());
    //retrieve element to add with all columns loaded
    element=(Trajectory)df.retrieve("Trajectory", criteria);
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
    }//end of addTrajectory method  
  
  private static void exportMeta(Feature feature, DefaultMutableTreeNode root,
    DataFactory df){
    DefaultMutableTreeNode featureNode=new DefaultMutableTreeNode(
      new XMLObject(XMLObject.FEATUREHEADING));
    root.add(featureNode);
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", feature.getName(), XMLObject.FEATURE)));
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Tag", feature.getTag(), XMLObject.FEATURE)));
    //add trajectory entries
    String [] attrs={"DeltaX","DeltaY", "DeltaZ", "Distance",
      "Order", "Trajectory", "Velocity"};
    Criteria trajCriteria=OMEDownload.makeAttributeFields(attrs);
    trajCriteria.addWantedField("feature_id");
    trajCriteria.addFilter("feature_id", (new Integer(feature.getID())).toString());
    //retrieve element to add with all columns loaded
    List trajects=df.retrieveList("TrajectoryEntry", trajCriteria);
    Iterator iterTra=trajects.iterator();
    while (iterTra.hasNext()){
      addTrajectoryEntry((TrajectoryEntry)iterTra.next(), featureNode, df);
    }
    //add attributes to this element's node
    for (int i=0; i<OMEMetaPanel.FEATURE_TYPES.length; i++){
      Criteria criteria= OMEDownload.makeAttributeFields(OMEMetaPanel.FEATURE_ATTRS[i]);
      criteria.addWantedField("feature_id");
      criteria.addFilter("feature_id", (new Integer(feature.getID())).toString());
      IJ.showStatus("Retrieving  "+OMEMetaPanel.FEATURE_TYPES[i]+"s.");
      List customs=null;
      try{
        customs=df.retrieveList(OMEMetaPanel.FEATURE_TYPES[i], criteria);
      }catch(Exception e){
        //don't tell anyone about it, just keep going
        e.printStackTrace();
        System.out.println("Error while retrieving "+OMEMetaPanel.FEATURE_TYPES[i]+"s.");
      }
      if(customs!=null){
        Iterator itCustoms=customs.iterator();
        while (itCustoms.hasNext()){
          AttributeDTO attr=(AttributeDTO)itCustoms.next();
          DefaultMutableTreeNode elementNode=new DefaultMutableTreeNode(
            new XMLObject(OMEMetaPanel.FEATURE_TYPES[i], XMLObject.ELEMENT));
          featureNode.add(elementNode);
          Map map = attr.getMap();
          Set set = map.keySet();
          Iterator iter2 = set.iterator();
          while (iter2.hasNext()) {
            Object key = iter2.next();
            Object value = map.get(key);
            if((value instanceof String) || (value instanceof Number) || 
              (value instanceof Boolean)){
              elementNode.add(new DefaultMutableTreeNode(new XMLObject((String)key,
                ""+value, XMLObject.ATTRIBUTE)));
              //System.out.println("key=" + key + "; value=" + value);
            }
          }
        }
      }
    }
    //add children features to tree
    List features=feature.getChildren();
    Iterator iter=features.iterator();
    while (iter.hasNext()){
      exportMeta((Feature)iter.next(), featureNode, df);
    }
  }//end of exportMeta method
  
}//end of OMEMetaDataHandler class