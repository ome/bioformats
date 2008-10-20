package mdbtools.libmdb;

public class Util
{
  // convert from access string format to unicode
  public static String extractText(byte[] bytes)
  {
    boolean clean = true;
    // busco null
    for (int i = 1; i < bytes.length; i += 2) {
      if (bytes[i] != 0) {
        clean = false;
        break;
      }
    }
    if (clean) {
      byte[] ab = new byte[bytes.length / 2];
      for (int i = 0, j = 0; i < ab.length; i++, j += 2) {
        ab[i] = bytes[j];
      }
      return new String(ab); //,0,ab.length);
    }
    else {
      int start = 0;
      // for some kind of reason, my access is returning varchars with this
      // leading chars:
      if (bytes[0] == -1 && bytes[1] == -2)
        start = 2;
      return new String(bytes,start,bytes.length-start);
    }
  }
}
