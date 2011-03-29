package ome.metakit;

import java.io.IOException;

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
    if ((dataByte & 0x80) == 0) {
      stopByte = stream.read();
    }
    else dataByte = 0;

    int value = ((dataByte << 7) & 0xffff) | (stopByte & 0x7f);
    if (negative) {
      value = ~value;
    }
    return value & 0xffff;
  }

}
