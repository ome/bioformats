package loci.formats.in.LeicaMicrosystemsMetadata.model.confocal;

import java.util.ArrayList;
import java.util.List;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Objective;

public class ConfocalSettingRecords {
  // scanner setting records
  public double pinholeSize;
  public double zoom;
  public double readOutRate;
  public boolean reverseX;
  public boolean reverseY;
  // filter setting records
  public List<Laser> laserRecords = new ArrayList<>();
  public List<Aotf> aotfRecords = new ArrayList<>();
  public List<Detector> detectorRecords = new ArrayList<>();
  public List<Multiband> multibandRecords = new ArrayList<>();
  public Objective objectiveRecord = new Objective();
}
