package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import ome.xml.model.primitives.Color;

public class ChannelExtractor extends Extractor {
  /***
   * Extracts i.a. channel number and luts from channel descriptions and writes it
   * to reader's {@link CoreMetadata} and {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param coreIndex
   * @throws FormatException
   */
  public static List<Channel> extractChannels(NodeList channelNodes) {
    List<Channel> channels = new ArrayList<Channel>();

    for (int ch = 0; ch < channelNodes.getLength(); ch++) {
      Element channelElement = (Element) channelNodes.item(ch);

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
   * Translates raw channel luts of an image to Colors and writes it to reader's
   * {@link MetadataTempBuffer}
   * 
   * @param luts
   *          list of raw lut names / values from Leica XML
   */
  public static void translateLuts(List<Channel> channels) {
    for (Channel channel : channels){
      channel.lutColor = translateLut(channel.lutName);
      channel.channelPriority = getChannelPriority(channel.lutName);
    }
  }

  private static int getChannelPriority(String lutName) {
    switch (lutName) {
      case "red":
        return 0;
      case "green":
        return 1;
      case "blue":
        return 2;
      case "cyan":
        return 3;
      case "magenta":
        return 4;
      case "yellow":
        return 5;
      case "black":
        return 6;
      case "gray":
        return 7;
      case "":
      default:
        return 8;
    }
  }

  /***
   * Translates Leica XML lut name / value to Color
   * 
   * @param lutName
   * @return
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
