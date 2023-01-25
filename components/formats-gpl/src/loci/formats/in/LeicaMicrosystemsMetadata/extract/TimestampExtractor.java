package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.common.DateTools;

public class TimestampExtractor extends Extractor {
  /**
   * Extracts timestamps and writes them to reader's {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param image
   *          image / core index
   * @throws FormatException
   */
  public static List<Double> translateTimestamps(Element imageNode, int imageCount) {
    List<Double> timestamps = new ArrayList<Double>();

    NodeList timeStampLists = getDescendantNodesWithName(imageNode, "TimeStampList");
    if (timeStampLists == null)
      return timestamps;

    Element timeStampList = (Element) timeStampLists.item(0);

    // probe if timestamps are saved in the format of LAS AF 3.1 or newer
    String numberOfTimeStamps = timeStampList.getAttribute("NumberOfTimeStamps");
    if (numberOfTimeStamps != null && !numberOfTimeStamps.isEmpty()) {
      // LAS AF 3.1 (or newer) timestamps are available
      String timeStampsRaw = timeStampList.getTextContent();
      List<String> timeStamps = Arrays.asList(timeStampsRaw.split(" "));
      for (int stamp = 0; stamp < timeStamps.size(); stamp++) {
        if (stamp < imageCount) {
          String timestamp = timeStamps.get(stamp);
          timestamps.add(translateSingleTimestamp(timestamp));
        }
      }
    } else {
      // probe if timestamps are saved in the format of LAS AF 3.0 or older
      NodeList timestampNodes = getDescendantNodesWithName(imageNode, "TimeStamp");
      if (timestampNodes != null) {
        // LAS AF 3.0 (or older) timestamps are available
        for (int stamp = 0; stamp < timestampNodes.getLength(); stamp++) {
          if (stamp < imageCount) {
            Element timestamp = (Element) timestampNodes.item(stamp);
            timestamps.add(translateSingleTimestamp(timestamp));
          }
        }
      } else {
        return timestamps;
      }
    }

    return timestamps;
  }

  /**
   * Translates hex timestamp string to double value
   * 
   * @param timestamp
   *          timestamp in format
   * @return number of seconds
   */
  private static double translateSingleTimestamp(String timestamp) {
    timestamp = timestamp.trim();
    int stampLowStart = Math.max(0, timestamp.length() - 8);
    int stampHighEnd = Math.max(0, stampLowStart);
    String stampHigh = timestamp.substring(0, stampHighEnd);
    String stampLow = timestamp.substring(stampLowStart, timestamp.length());
    long high = stampHigh == null || stampHigh.trim().isEmpty() ? 0
        : Long.parseLong(stampHigh.trim(), 16);
    long low = stampLow == null || stampLow.trim().isEmpty() ? 0
        : Long.parseLong(stampLow.trim(), 16);
    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double) milliseconds / 1000;
    return seconds;
  }

  /**
   * Translates the content of a timestamp node to double value
   * 
   * @param imageNode
   *          timestamp node
   * @param image
   *          image / core index
   * @throws FormatException
   */
  private static double translateSingleTimestamp(Element timestamp) {
    String stampHigh = timestamp.getAttribute("HighInteger");
    String stampLow = timestamp.getAttribute("LowInteger");
    long high = stampHigh == null || stampHigh.trim().isEmpty() ? 0
        : Long.parseLong(stampHigh.trim());
    long low = stampLow == null || stampLow.trim().isEmpty() ? 0
        : Long.parseLong(stampLow.trim());

    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double) milliseconds / 1000;
    return seconds;
  }
}
