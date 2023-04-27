package loci.formats.in.LeicaMicrosystemsMetadata.helpers;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LMSMainXmlNodes {
  public Element imageNode;
  public Element imageDescription;
  public Element hardwareSetting;
  
  public Element mainConfocalSetting;
  public Element masterConfocalSetting;
  public List<Element> sequentialConfocalSettings = new ArrayList<Element>();
  
  public Element mainCameraSetting;
  public Element masterCameraSetting;
  public List<Element> sequentialCameraSettings = new ArrayList<Element>();
  public Element widefieldChannelConfig;
  public List<Element> widefieldChannelInfos = new ArrayList<Element>();

  public NodeList scannerSettingRecords;
  public NodeList filterSettingRecords;

  public enum HardwareSettingLayout {
    OLD, // e.g. SP5
    NEW // e.g. SP8, STELLARIS, MICA, ...
  }
  public HardwareSettingLayout hardwareSettingLayout;

  public enum DataSourceType {
    CAMERA, CONFOCAL
  }
  public DataSourceType dataSourceType;

  public Element getAtlSetting(){
    if (hardwareSettingLayout == HardwareSettingLayout.OLD){
      return dataSourceType == DataSourceType.CONFOCAL ? masterConfocalSetting : masterCameraSetting;
    } else {
      return dataSourceType == DataSourceType.CONFOCAL ? mainConfocalSetting : mainCameraSetting;
    }
  }
}
