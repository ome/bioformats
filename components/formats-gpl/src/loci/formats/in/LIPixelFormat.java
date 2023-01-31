package loci.formats.in;

public class LIPixelFormat {
  public static final String PIXELFORMAT_MONO8 = "Mono8";
  public static final String PIXELFORMAT_MONO10 = "Mono10";
  public static final String PIXELFORMAT_MONO10P = "Mono10P";
  public static final String PIXELFORMAT_MONO10PMSB = "Mono10pmsb";
  public static final String PIXELFORMAT_MONO12 = "Mono12";
  public static final String PIXELFORMAT_MONO12P = "Mono12p";
  public static final String PIXELFORMAT_MONO12PMSB = "Mono12pmsb";
  public static final String PIXELFORMAT_MONO12PACKED = "Mono12Packed";
  public static final String PIXELFORMAT_MONO14 = "Mono14";
  public static final String PIXELFORMAT_MONO14P = "Mono14p";
  public static final String PIXELFORMAT_MONO16 = "Mono16";
  public static final String PIXELFORMAT_BGR8 = "BGR8";
  public static final String PIXELFORMAT_BGR8PACKED = "BGR8Packed"; //Is not packed
  public static final String PIXELFORMAT_RGB8 = "RGB8";
  public static final String PIXELFORMAT_RGB8PACKED = "RGB8Packed"; //Is not packed
  public static final String PIXELFORMAT_BAYERBG8 = "BayerBG8";
  public static final String PIXELFORMAT_BAYERBG12 = "BayerBG12";
  public static final String PIXELFORMAT_BAYERBG12P = "BayerBG12p";
  public static final String PIXELFORMAT_BAYERBG12PMSB = "BayerBG12pmsb";
  public static final String PIXELFORMAT_BAYERBG16 = "BayerBG16";
  public static final String PIXELFORMAT_BAYERGB8 = "BayerGB8";
  public static final String PIXELFORMAT_BAYERGB12 = "BayerGB12";
  public static final String PIXELFORMAT_BAYERGB12P = "BayerGB12p";
  public static final String PIXELFORMAT_BAYERGB12PMSB = "BayerGB12pmsb";
  public static final String PIXELFORMAT_BAYERGB16 = "BayerGB16";
  public static final String PIXELFORMAT_BAYERGR8 = "BayerGR8";
  public static final String PIXELFORMAT_BAYERGR10 = "BayerGR10";
  public static final String PIXELFORMAT_BAYERGR12 = "BayerGR12";
  public static final String PIXELFORMAT_BAYERGR12P = "BayerGR12p";
  public static final String PIXELFORMAT_BAYERGR12PMSB = "BayerGR12psmb";
  public static final String PIXELFORMAT_BAYERGR16 = "BayerGR16";
  public static final String PIXELFORMAT_BAYERRG8 = "BayerRG8";
  public static final String PIXELFORMAT_BAYERRG10 = "BayerRG10";
  public static final String PIXELFORMAT_BAYERRG12 = "BayerRG12";
  public static final String PIXELFORMAT_BAYERRG12P = "BayerRG12P";
  public static final String PIXELFORMAT_BAYERRG12PMSB = "BayerRG12";
  public static final String PIXELFORMAT_BAYERRG12PACKED = "BayerRG12Packed";
  public static final String PIXELFORMAT_BAYERRG16 = "BayerRG16";

  public static String getPacking(String format) {
    switch (format) {
      case PIXELFORMAT_BAYERBG12P:
      case PIXELFORMAT_BAYERGB12P:
      case PIXELFORMAT_BAYERGR12P:
      case PIXELFORMAT_BAYERRG12P:
      case PIXELFORMAT_MONO10P:
      case PIXELFORMAT_MONO12P:
      case PIXELFORMAT_MONO14P:
        return "lsb";
      case PIXELFORMAT_MONO12PACKED:
      case PIXELFORMAT_BAYERRG12PACKED:
      case PIXELFORMAT_MONO10PMSB:
      case PIXELFORMAT_BAYERGB12PMSB:
      case PIXELFORMAT_BAYERGR12PMSB:
      case PIXELFORMAT_BAYERRG12PMSB:
      case PIXELFORMAT_BAYERBG12PMSB:
        return "msb";
    }
    return "";
  }

  public static Boolean pixelFormatEqualsBitSize(Integer bitSize, String format) {
    switch (bitSize) {
      case 8:
        return PIXELFORMAT_MONO8.equals(format) || PIXELFORMAT_BGR8.equals(format) || 
          PIXELFORMAT_BGR8PACKED.equals(format) || PIXELFORMAT_RGB8.equals(format) ||
          PIXELFORMAT_RGB8PACKED.equals(format) || PIXELFORMAT_BAYERBG8.equals(format) ||
          PIXELFORMAT_BAYERGB8.equals(format) || PIXELFORMAT_BAYERGR8.equals(format) ||
          PIXELFORMAT_BAYERRG8.equals(format);
      case 10:
        return PIXELFORMAT_MONO10.equals(format) || PIXELFORMAT_MONO10P.equals(format) ||
          PIXELFORMAT_MONO10PMSB.equals(format) || PIXELFORMAT_BAYERGR10.equals(format) ||
          PIXELFORMAT_BAYERGR10.equals(format) || PIXELFORMAT_BAYERRG10.equals(format);
      case 12:
        return PIXELFORMAT_MONO12.equals(format) || PIXELFORMAT_MONO12P.equals(format) ||
          PIXELFORMAT_MONO12PMSB.equals(format) || PIXELFORMAT_MONO12PACKED.equals(format) || 
          PIXELFORMAT_BAYERBG12.equals(format) || PIXELFORMAT_BAYERBG12P.equals(format) || 
          PIXELFORMAT_BAYERBG12PMSB.equals(format) || PIXELFORMAT_BAYERGB12.equals(format) || 
          PIXELFORMAT_BAYERGB12P.equals(format) || PIXELFORMAT_BAYERGB12PMSB.equals(format) || 
          PIXELFORMAT_BAYERGR12.equals(format) || PIXELFORMAT_BAYERGR12P.equals(format) || 
          PIXELFORMAT_BAYERGR12PMSB.equals(format) || PIXELFORMAT_BAYERRG12.equals(format) ||
          PIXELFORMAT_BAYERRG12P.equals(format) || PIXELFORMAT_BAYERRG12PMSB.equals(format) ||
          PIXELFORMAT_BAYERRG12PACKED.equals(format);
      case 14:
        return PIXELFORMAT_MONO14.equals(format) || PIXELFORMAT_MONO14P.equals(format);
      case 16:
        return PIXELFORMAT_MONO16.equals(format) || PIXELFORMAT_BAYERBG16.equals(format) ||
          PIXELFORMAT_BAYERGB16.equals(format) || PIXELFORMAT_BAYERGR16.equals(format) ||
          PIXELFORMAT_BAYERRG16.equals(format);
    }
    return false;
  }
}
