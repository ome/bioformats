package loci.formats.in;

public class MetadataGroupFilesOptions {
  public enum GroupFilesGranularity {
    TOP_LEVEL,
    LIMITED_DEPTH,
    RECURSIVE;
  }
  
  private int maxDepth;
  private GroupFilesGranularity granularityLevel;
  
  public MetadataGroupFilesOptions() {
    granularityLevel = GroupFilesGranularity.TOP_LEVEL;
    maxDepth = 0;
  }
  
  public MetadataGroupFilesOptions(GroupFilesGranularity granularity) {
    granularityLevel = granularity;
    maxDepth = 0;
    if (granularityLevel == GroupFilesGranularity.RECURSIVE) {
      maxDepth = Integer.MAX_VALUE;
    }
  }
  
  public MetadataGroupFilesOptions(GroupFilesGranularity granularity, int depth) {
    granularityLevel = granularity;
    maxDepth = depth;     
  }

  public GroupFilesGranularity getGranularityLevel() {
    return granularityLevel;
  }
  
  public void setGranularityLevel(GroupFilesGranularity level) {
    granularityLevel = level;
  }

  public int getMaxDepth() {
    if (granularityLevel == GroupFilesGranularity.TOP_LEVEL) {
      return 0;
    }
    else if (granularityLevel == GroupFilesGranularity.RECURSIVE) {
      return Integer.MAX_VALUE;
    }
    return maxDepth;
  }
  
  public void setMaxDepth(int depth) {
    maxDepth = depth;
  }
}
