/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.common.DateTools;

/**
 * TimestampExtractor is a helper class for extracting timestamp information from LMS XML files.
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class TimestampExtractor extends Extractor {
  /**
   * Extracts and returns timestamps from image node
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
