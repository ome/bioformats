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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import ome.xml.model.primitives.Color;

/**
 * ChannelExtractor is a helper class for extracting channel information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ChannelExtractor extends Extractor {
  
  /***
   * Creates and returns Channels with information extracted from channel description nodes
   */
  public static List<Channel> extractChannels(NodeList channelDescNodes) {
    List<Channel> channels = new ArrayList<Channel>();

    for (int ch = 0; ch < channelDescNodes.getLength(); ch++) {
      Element channelElement = (Element) channelDescNodes.item(ch);

      Channel channel = new Channel();
      channel.channelTag = Integer.parseInt(channelElement.getAttribute("ChannelTag"));
      channel.resolution = Integer.parseInt(channelElement.getAttribute("Resolution"));
      channel.min = parseDouble(channelElement.getAttribute("Min"));
      channel.max = parseDouble(channelElement.getAttribute("Max"));
      channel.unit = channelElement.getAttribute("Unit");
      channel.lutName = channelElement.getAttribute("LUTName");
      channel.bytesInc = parseLong(channelElement.getAttribute("BytesInc"));

      channels.add(channel);
    }

    translateLuts(channels);

    return channels;
  }

  /***
   * Translates raw channel luts of an image to Colors and lut color indices
   */
  public static void translateLuts(List<Channel> channels) {
    for (Channel channel : channels){
      channel.lutColor = translateLut(channel.lutName);
      channel.lutColorIndex = getLutColorIndex(channel.lutName);
    }
  }

  /**
   * Translates LeicaXML lut name to lut color index
   */
  private static int getLutColorIndex(String lutName) {
    switch (lutName) {
      case "red":
        return Channel.RED;
      case "green":
        return Channel.GREEN;
      case "blue":
        return Channel.BLUE;
      case "cyan":
        return Channel.CYAN;
      case "magenta":
        return Channel.MAGENTA;
      case "yellow":
        return Channel.YELLOW;
      case "black":
      case "gray":
      case "":
      default:
        return Channel.GREY;
    }
  }

  /***
   * Translates Leica XML lut name / value to Color
   */
  private static Color translateLut(String lutName) {
    lutName = lutName.replaceAll("\\s+", "");
    // some LUTs are stored as gradients
    Pattern pattern = Pattern.compile("Gradient\\(\\d+,\\d+,\\d+\\)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(lutName);
    if (matcher.find()) {
      String[] rgb = lutName.substring(9, lutName.length() - 1).split(",");
      return new Color(Integer.parseInt(rgb[2]),
          Integer.parseInt(rgb[1]),
          Integer.parseInt(rgb[0]),
          255);
    } else {
      switch (lutName.toLowerCase()) {
        case "red":
          return new Color(255, 0, 0, 255);
        case "green":
          return new Color(0, 255, 0, 255);
        case "blue":
          return new Color(0, 0, 255, 255);
        case "cyan":
          return new Color(0, 255, 255, 255);
        case "magenta":
          return new Color(255, 0, 255, 255);
        case "yellow":
          return new Color(255, 255, 0, 255);
        default:
          return new Color(255, 255, 255, 255);
      }
    }
    // TODO: numeric lut handling
  }
}
