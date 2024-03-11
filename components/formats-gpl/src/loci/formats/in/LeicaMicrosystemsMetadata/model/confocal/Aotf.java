package loci.formats.in.LeicaMicrosystemsMetadata.model.confocal;

import java.util.ArrayList;
import java.util.List;

public class Aotf {
  public String name;
  public List<LaserSetting> laserLineSettings = new ArrayList<>();
  public String type; //e.g. visible, UV chaser, UV, MP EOM (treated as AOTF in LMS XML)
  public boolean shutterOpen;
}
