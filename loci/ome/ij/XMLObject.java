/**
 * XMLObject is the class that handles
 * display and editing of a piece of metadata
 * in the tree.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
class XMLObject{
  public static final int IMAGE=1;
  public static final int PROJECT=2;
  public static final int DATASET=3;
  public static final int ELEMENT=4;
  public static final int CUSTOMHEADING=5;
  public static final int FEATUREHEADING=6;
  public static final int FEATURE=7;
  public static final int DATASETHEADING=8;
  public static final int IMAGEHEADING=9;
  public static final int DATASETREF=10;
  public static final int PROJECTHEADING=11;
  public static final int PIXELHEADING=12;
  public static final int PIXELS=13;
  public static final int ATTRIBUTE=0;
  public static final int READONLY=-1;
  private String attr, value;
  private int type;
  
  /**Create an custom attribute node*/
  public XMLObject(String attr, String value, int type){
    this.type=type;
    this.attr=attr;
    this.value=value;
  }
  /**Create a custom element node*/
  public XMLObject(String attr, int type){
    this.type=type;
    this.attr=attr;
  }
  /**Create a heading node*/
  public XMLObject(int type){
    this.type=type;
    if (type==IMAGEHEADING) this.attr="Image";
    else if (type==FEATUREHEADING) this.attr="Feature";
    else if (type==CUSTOMHEADING) this.attr="Custom Attributes";
    else if (type==DATASETREF) this.attr="Dataset References";
    else if (type==PROJECTHEADING) this.attr="Project";
    else if (type==DATASETHEADING) this.attr="Dataset";
    else if (type==PIXELHEADING) this.attr="Pixels";
  }
  /**Create an ome heading node*/
  public XMLObject(String attr){
    this.attr=attr;
    type=READONLY;
  }
  
  public String toString(){
    return attr;
  }
  
  /** sets new value of this attr*/
  public void setValue(String newValue){
    if (type==ATTRIBUTE || type==IMAGE ||type==PROJECT||type==DATASET||type==FEATURE)
      value=newValue;
  }//end of setValue method
  
  public String getValue(){
    return value;
  }
  /**adds an element to a CAElement object*/
/*  public void createElement(String name){
    if (type==CUSTOMHEADING || type==ELEMENT){
      if (name==null)return;
    }
  }//end of createElement method
  
  /**adds an attrbute to an element*/
/*  public void createAttribute(String newAttr, String newValue){
    if (type==ELEMENT){
      if (newAttr==null)return;
    }
  }//end of createAttribute method
  
  /**returns the type of XMLObject according to class constants*/
  public int getType(){
    return type;
  }//end of getType method
  
/*  public void addFeature(String name, String tag){
  }//end of addFeature method
*/  
}//end of class XMLObject