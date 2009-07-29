package loci.formats.codec;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;

public class JPEG2000CodecOptions extends CodecOptions

{
  public double quality;
  public int[] codeBlockSize;
  public String filter;

  /** Return JPEG2000CodecOptions with reasonable default values. */
  public static JPEG2000CodecOptions getDefaultOptions() {
    CodecOptions options = CodecOptions
        .getDefaultOptions();

    return getJ2KOptions(options);
  }
  /** Return JPEG2000CodecOptions using the CodecOptions as starting template. */
  public static JPEG2000CodecOptions getJ2KOptions(
      CodecOptions options) {
    if(options==null) {
      options=CodecOptions.getDefaultOptions();
    }
    
    JPEG2000CodecOptions j2kOptions = new JPEG2000CodecOptions();
    
    if(options.lossless) {
    j2kOptions.quality = Double.MAX_VALUE;
    j2kOptions.filter = J2KImageWriteParam.FILTER_53;
    j2kOptions.lossless=true;
    } else {
      
    j2kOptions.quality = 10;
    j2kOptions.filter = J2KImageWriteParam.FILTER_97;
    j2kOptions.lossless=false;
    }
    j2kOptions.codeBlockSize = new int[] { 64, 64 };
    
    
    //TODO: Better approach to extend to current info contained in CodecOptions
    j2kOptions.width=options.width;
    j2kOptions.height=options.height;
    j2kOptions.channels=options.channels;
    j2kOptions.bitsPerSample=options.bitsPerSample;
    j2kOptions.littleEndian=options.littleEndian;
    j2kOptions.interleaved=options.interleaved;
    j2kOptions.signed=options.signed;
    j2kOptions.maxBytes=options.maxBytes;
    j2kOptions.previousImage=options.previousImage;

    return j2kOptions;
  }
}
