package loci.formats.in.LeicaMicrosystemsMetadata.model;

import java.util.ArrayList;
import java.util.List;

public class ImageDetails {
  public String originalImageName;
  public String targetImageName; // differs for tiles which are treated as separate series
  public String description;
  public List<String> userComments = new ArrayList<>();
}
