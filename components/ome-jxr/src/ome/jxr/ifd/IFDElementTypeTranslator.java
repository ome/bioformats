
package ome.jxr.ifd;

import java.lang.reflect.Method;

import loci.common.RandomAccessInputStream;

public class IFDElementTypeTranslator {

  public Method toStreamMethod(IFDElementType type,
      Class<RandomAccessInputStream> klass) {
    try {
      Method method = klass.getDeclaredMethod("read");
      switch (type) {
        case BYTE:
          method = klass.getDeclaredMethod("readUnsignedByte");
          break;
        case DOUBLE:
          method = klass.getDeclaredMethod("readDouble");
          break;
        case FLOAT:
          method = klass.getDeclaredMethod("readFloat");
        case SBYTE:
          method = klass.getDeclaredMethod("readByte");
          break;
        case SLONG:
        case SRATIONAL:
        case ULONG:
        case URATIONAL:
          method = klass.getDeclaredMethod("readLong");
          break;
        case SSHORT:
          method = klass.getDeclaredMethod("readShort");
          break;
        case UNDEFINED:
          break;
        case USHORT:
          method = klass.getDeclaredMethod("readUnsignedShort");
          break;
        case UTF8:
          method = klass.getDeclaredMethod("readUTF");
          break;
        default:
          break;
      }
      return method;
    } catch (NoSuchMethodException nsme) {
      throw new RuntimeException("Invocation of non-existing method.");
    }
  }

}
