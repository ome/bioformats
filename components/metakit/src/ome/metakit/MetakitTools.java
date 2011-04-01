package ome.metakit;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.RandomAccessInputStream;

public class MetakitTools {

  public static String readPString(RandomAccessInputStream stream)
    throws IOException
  {
    int stringLength = readBpInt(stream);
    return stream.readString(stringLength);
  }

  public static int readBpInt(RandomAccessInputStream stream) throws IOException
  {
    int signByte = stream.read();
    boolean negative = signByte == 0;
    int dataByte = !negative ? signByte : stream.read();
    int stopByte = dataByte;
    ArrayList<Integer> dataBytes = new ArrayList<Integer>();
    while ((stopByte & 0x80) == 0) {
      dataBytes.add(stopByte);
      stopByte = stream.read();
    }

    int value = 0;
    for (int i=0; i<dataBytes.size(); i++) {
      int shift = (dataBytes.size() - i) * 8 - 1;
      value |= (dataBytes.get(i) << shift);
    }

    value |= (stopByte & 0x7f);

    if (negative) {
      value = ~value;
    }
    return value & 0xffffffff;
  }

}
