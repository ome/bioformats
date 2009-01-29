//
// showinf.cpp
//

#include "jace/JNIHelper.h"

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/proxy/types/JBoolean.h"
using jace::proxy::types::JBoolean;

#include "jace/proxy/types/JByte.h"
using jace::proxy::types::JByte;

#include "jace/proxy/java/lang/String.h"
#include "jace/proxy/java/io/IOException.h"

using namespace jace::proxy::java::lang;
using namespace jace::proxy::java::io;

// TEMP - eventually want to automatically generate this list
#include "jace/proxy/loci/formats/AWTImageTools.h"
#include "jace/proxy/loci/formats/AxisGuesser.h"
#include "jace/proxy/loci/formats/ChannelFiller.h"
#include "jace/proxy/loci/formats/ChannelMerger.h"
#include "jace/proxy/loci/formats/ChannelSeparator.h"
#include "jace/proxy/loci/formats/ClassList.h"
#include "jace/proxy/loci/formats/CoreMetadata.h"
#include "jace/proxy/loci/formats/DimensionSwapper.h"
#include "jace/proxy/loci/formats/FilePattern.h"
#include "jace/proxy/loci/formats/FileStitcher.h"
#include "jace/proxy/loci/formats/FormatException.h"
#include "jace/proxy/loci/formats/FormatHandler.h"
#include "jace/proxy/loci/formats/FormatReader.h"
#include "jace/proxy/loci/formats/FormatTools.h"
#include "jace/proxy/loci/formats/FormatWriter.h"
#include "jace/proxy/loci/formats/IFormatHandler.h"
#include "jace/proxy/loci/formats/IFormatReader.h"
#include "jace/proxy/loci/formats/IFormatWriter.h"
#include "jace/proxy/loci/formats/ImageReader.h"
#include "jace/proxy/loci/formats/ImageTools.h"
#include "jace/proxy/loci/formats/ImageWriter.h"
#include "jace/proxy/loci/formats/Index16ColorModel.h"
#include "jace/proxy/loci/formats/LegacyQTTools.h"
#include "jace/proxy/loci/formats/MetadataTools.h"
#include "jace/proxy/loci/formats/MinMaxCalculator.h"
#include "jace/proxy/loci/formats/NetcdfTools.h"
#include "jace/proxy/loci/formats/NumberFilter.h"
#include "jace/proxy/loci/formats/POITools.h"
#include "jace/proxy/loci/formats/ReaderWrapper.h"
#include "jace/proxy/loci/formats/SignedColorModel.h"
#include "jace/proxy/loci/formats/StatusEvent.h"
#include "jace/proxy/loci/formats/StatusListener.h"
#include "jace/proxy/loci/formats/StatusReporter.h"
#include "jace/proxy/loci/formats/TiffIFDEntry.h"
#include "jace/proxy/loci/formats/TiffRational.h"
#include "jace/proxy/loci/formats/TiffTools.h"
#include "jace/proxy/loci/formats/TwoChannelColorSpace.h"
#include "jace/proxy/loci/formats/UnknownTagException.h"
#include "jace/proxy/loci/formats/XMLTools.h"
#include "jace/proxy/loci/formats/cache/BufferedImageSource.h"
#include "jace/proxy/loci/formats/cache/ByteArraySource.h"
#include "jace/proxy/loci/formats/cache/CacheConsole.h"
#include "jace/proxy/loci/formats/cache/CacheEvent.h"
#include "jace/proxy/loci/formats/cache/CacheException.h"
#include "jace/proxy/loci/formats/cache/Cache.h"
#include "jace/proxy/loci/formats/cache/CacheListener.h"
#include "jace/proxy/loci/formats/cache/CacheReporter.h"
#include "jace/proxy/loci/formats/cache/CacheSource.h"
#include "jace/proxy/loci/formats/cache/CacheStrategy.h"
#include "jace/proxy/loci/formats/cache/CacheUpdater.h"
#include "jace/proxy/loci/formats/cache/CrosshairStrategy.h"
#include "jace/proxy/loci/formats/cache/ICacheSource.h"
#include "jace/proxy/loci/formats/cache/ICacheStrategy.h"
#include "jace/proxy/loci/formats/cache/RectangleStrategy.h"
#include "jace/proxy/loci/formats/codec/Base64Codec.h"
#include "jace/proxy/loci/formats/codec/BaseCodec.h"
#include "jace/proxy/loci/formats/codec/BitBuffer.h"
#include "jace/proxy/loci/formats/codec/BitWriter.h"
#include "jace/proxy/loci/formats/codec/ByteVector.h"
#include "jace/proxy/loci/formats/codec/Codec.h"
#include "jace/proxy/loci/formats/codec/CodecOptions.h"
#include "jace/proxy/loci/formats/codec/JPEG2000Codec.h"
#include "jace/proxy/loci/formats/codec/JPEGCodec.h"
#include "jace/proxy/loci/formats/codec/LuraWaveCodec.h"
#include "jace/proxy/loci/formats/codec/LZOCodec.h"
#include "jace/proxy/loci/formats/codec/LZWCodec.h"
#include "jace/proxy/loci/formats/codec/MJPBCodec.h"
#include "jace/proxy/loci/formats/codec/MJPBCodecOptions.h"
#include "jace/proxy/loci/formats/codec/MSRLECodec.h"
#include "jace/proxy/loci/formats/codec/MSVideoCodec.h"
#include "jace/proxy/loci/formats/codec/NikonCodec.h"
#include "jace/proxy/loci/formats/codec/NikonCodecOptions.h"
#include "jace/proxy/loci/formats/codec/PackbitsCodec.h"
#include "jace/proxy/loci/formats/codec/QTRLECodec.h"
#include "jace/proxy/loci/formats/codec/RPZACodec.h"
#include "jace/proxy/loci/formats/codec/ZlibCodec.h"
#include "jace/proxy/loci/formats/gui/CacheComponent.h"
#include "jace/proxy/loci/formats/gui/CacheIndicator.h"
#include "jace/proxy/loci/formats/gui/ComboFileFilter.h"
#include "jace/proxy/loci/formats/gui/DataConverter.h"
#include "jace/proxy/loci/formats/gui/ExtensionFileFilter.h"
#include "jace/proxy/loci/formats/gui/FormatFileFilter.h"
#include "jace/proxy/loci/formats/gui/GUITools.h"
#include "jace/proxy/loci/formats/gui/ImageViewer.h"
#include "jace/proxy/loci/formats/gui/NoExtensionFileFilter.h"
#include "jace/proxy/loci/formats/gui/PreviewPane.h"
#include "jace/proxy/loci/formats/gui/XMLCellRenderer.h"
#include "jace/proxy/loci/formats/gui/XMLWindow.h"
#include "jace/proxy/loci/formats/in/AliconaReader.h"
#include "jace/proxy/loci/formats/in/APLReader.h"
#include "jace/proxy/loci/formats/in/APNGReader.h"
#include "jace/proxy/loci/formats/in/AVIReader.h"
#include "jace/proxy/loci/formats/in/BaseTiffReader.h"
#include "jace/proxy/loci/formats/in/BioRadReader.h"
#include "jace/proxy/loci/formats/in/BMPReader.h"
#include "jace/proxy/loci/formats/in/DeltavisionReader.h"
#include "jace/proxy/loci/formats/in/DicomReader.h"
#include "jace/proxy/loci/formats/in/EPSReader.h"
#include "jace/proxy/loci/formats/in/FEIReader.h"
#include "jace/proxy/loci/formats/in/FitsReader.h"
#include "jace/proxy/loci/formats/in/FlexReader.h"
#include "jace/proxy/loci/formats/in/FluoviewReader.h"
#include "jace/proxy/loci/formats/in/FV1000Reader.h"
#include "jace/proxy/loci/formats/in/GatanReader.h"
#include "jace/proxy/loci/formats/in/GelReader.h"
#include "jace/proxy/loci/formats/in/GIFReader.h"
#include "jace/proxy/loci/formats/in/ICSReader.h"
#include "jace/proxy/loci/formats/in/ImageIOReader.h"
#include "jace/proxy/loci/formats/in/ImarisHDFReader.h"
#include "jace/proxy/loci/formats/in/ImarisReader.h"
#include "jace/proxy/loci/formats/in/ImarisTiffReader.h"
#include "jace/proxy/loci/formats/in/ImprovisionTiffReader.h"
#include "jace/proxy/loci/formats/in/InCellReader.h"
#include "jace/proxy/loci/formats/in/IPLabReader.h"
#include "jace/proxy/loci/formats/in/IPWReader.h"
#include "jace/proxy/loci/formats/in/JPEGReader.h"
#include "jace/proxy/loci/formats/in/KhorosReader.h"
#include "jace/proxy/loci/formats/in/L2DReader.h"
#include "jace/proxy/loci/formats/in/LegacyND2Reader.h"
#include "jace/proxy/loci/formats/in/LegacyQTReader.h"
#include "jace/proxy/loci/formats/in/LeicaHandler.h"
#include "jace/proxy/loci/formats/in/LeicaReader.h"
#include "jace/proxy/loci/formats/in/LIFReader.h"
#include "jace/proxy/loci/formats/in/LIMReader.h"
#include "jace/proxy/loci/formats/in/MDBParser.h"
#include "jace/proxy/loci/formats/in/MetamorphHandler.h"
#include "jace/proxy/loci/formats/in/MetamorphReader.h"
#include "jace/proxy/loci/formats/in/MetamorphTiffReader.h"
#include "jace/proxy/loci/formats/in/MicromanagerReader.h"
#include "jace/proxy/loci/formats/in/MINCReader.h"
#include "jace/proxy/loci/formats/in/MinimalTiffReader.h"
#include "jace/proxy/loci/formats/in/MNGReader.h"
#include "jace/proxy/loci/formats/in/MRCReader.h"
#include "jace/proxy/loci/formats/in/MRWReader.h"
#include "jace/proxy/loci/formats/in/NAFReader.h"
#include "jace/proxy/loci/formats/in/ND2Reader.h"
#include "jace/proxy/loci/formats/in/NikonReader.h"
#include "jace/proxy/loci/formats/in/NRRDReader.h"
#include "jace/proxy/loci/formats/in/OMETiffReader.h"
#include "jace/proxy/loci/formats/in/OMEXMLReader.h"
#include "jace/proxy/loci/formats/in/OpenlabRawReader.h"
#include "jace/proxy/loci/formats/in/OpenlabReader.h"
#include "jace/proxy/loci/formats/in/PCIReader.h"
#include "jace/proxy/loci/formats/in/PCXReader.h"
#include "jace/proxy/loci/formats/in/PerkinElmerReader.h"
#include "jace/proxy/loci/formats/in/PGMReader.h"
#include "jace/proxy/loci/formats/in/PictReader.h"
#include "jace/proxy/loci/formats/in/PrairieReader.h"
#include "jace/proxy/loci/formats/in/PSDReader.h"
#include "jace/proxy/loci/formats/in/QTReader.h"
#include "jace/proxy/loci/formats/in/SDTInfo.h"
#include "jace/proxy/loci/formats/in/SDTReader.h"
#include "jace/proxy/loci/formats/in/SEQReader.h"
#include "jace/proxy/loci/formats/in/SlidebookReader.h"
#include "jace/proxy/loci/formats/in/SVSReader.h"
#include "jace/proxy/loci/formats/in/TCSReader.h"
#include "jace/proxy/loci/formats/in/TiffReader.h"
#include "jace/proxy/loci/formats/in/TillVisionReader.h"
#include "jace/proxy/loci/formats/in/VisitechReader.h"
#include "jace/proxy/loci/formats/in/ZeissLSMReader.h"
#include "jace/proxy/loci/formats/in/ZeissZVIReader.h"
#include "jace/proxy/loci/formats/meta/AggregateMetadata.h"
#include "jace/proxy/loci/formats/meta/DummyMetadata.h"
#include "jace/proxy/loci/formats/meta/FilterMetadata.h"
#include "jace/proxy/loci/formats/meta/IMetadata.h"
#include "jace/proxy/loci/formats/meta/IMinMaxStore.h"
#include "jace/proxy/loci/formats/meta/MetadataConverter.h"
#include "jace/proxy/loci/formats/meta/MetadataRetrieve.h"
#include "jace/proxy/loci/formats/meta/MetadataStore.h"
#include "jace/proxy/loci/formats/ome/OmeisException.h"
#include "jace/proxy/loci/formats/ome/OmeisImporter.h"
#include "jace/proxy/loci/formats/ome/OMEXML2003FCMetadata.h"
#include "jace/proxy/loci/formats/ome/OMEXML200706Metadata.h"
#include "jace/proxy/loci/formats/ome/OMEXML200802Metadata.h"
#include "jace/proxy/loci/formats/ome/OMEXML200809Metadata.h"
#include "jace/proxy/loci/formats/ome/OMEXMLMetadata.h"
#include "jace/proxy/loci/formats/out/APNGWriter.h"
#include "jace/proxy/loci/formats/out/AVIWriter.h"
#include "jace/proxy/loci/formats/out/EPSWriter.h"
#include "jace/proxy/loci/formats/out/ICSWriter.h"
#include "jace/proxy/loci/formats/out/ImageIOWriter.h"
#include "jace/proxy/loci/formats/out/JPEG2000Writer.h"
#include "jace/proxy/loci/formats/out/JPEGWriter.h"
#include "jace/proxy/loci/formats/out/LegacyQTWriter.h"
#include "jace/proxy/loci/formats/out/OMETiffWriter.h"
#include "jace/proxy/loci/formats/out/OMEXMLWriter.h"
#include "jace/proxy/loci/formats/out/QTWriter.h"
#include "jace/proxy/loci/formats/out/TiffWriter.h"
#include "jace/proxy/loci/formats/tools/EditTiffG.h"
#include "jace/proxy/loci/formats/tools/ImageConverter.h"
#include "jace/proxy/loci/formats/tools/ImageInfo.h"
#include "jace/proxy/loci/formats/tools/TiffComment.h"
#include "jace/proxy/loci/formats/tools/XMLIndent.h"
#include "jace/proxy/loci/formats/tools/XMLValidate.h"
using namespace jace::proxy::loci::formats;
using namespace jace::proxy::loci::formats::cache;
using namespace jace::proxy::loci::formats::codec;
using namespace jace::proxy::loci::formats::gui;
using namespace jace::proxy::loci::formats::in;
using namespace jace::proxy::loci::formats::meta;
using namespace jace::proxy::loci::formats::ome;
using namespace jace::proxy::loci::formats::out;
using namespace jace::proxy::loci::formats::tools;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;

/** A C++ wrapped version of the showinf command line utility. */
int main(int argc, const char *argv[]) {
  try {
    cout << "Creating JVM..." << endl;
    StaticVmLoader loader(JNI_VERSION_1_4);
    OptionList list;
    list.push_back( jace::ClassPath( "jace-runtime.jar:bio-formats.jar" ) );
    list.push_back(jace::CustomOption("-Xcheck:jni"));
    list.push_back(jace::CustomOption("-Xmx256m"));
    list.push_back(jace::CustomOption("-verbose:jni"));
    jace::helper::createVm(loader, list, false);
    cout << "JVM created." << endl;

    cout << "Arguments:" << endl;
    for (int i=0; i<argc; i++) cout << "\t#" << i << ": " << argv[i] << endl;

    typedef JArray<String> StringArray;
    StringArray args(argc - 1);
    for (int i=1; i<argc; i++) args[i - 1] = argv[i];
    JBoolean result = ImageInfo::testRead(args);

    if (argc < 2) {
      cout << "Please specify a filename on the command line." << endl;
    }
    else {
      String id = argv[1];
      cout << "Initializing " << id << endl;
      //ImageReader r();
      //r.setId(id);
      //int w = r.getWidth();
      //int h = r.getHeight();
      //cout << "Image planes are " << w << " x " << h << endl;
      //r.openBytes(0);
      //JBoolean result = ImageInfo::testRead(r, args);

      //return result ? 0 : 1;
    }
  }
  catch (FormatException& fe) {
    cout << fe << endl;
    return -1;
  }
  catch (IOException& ioe) {
    cout << ioe << endl;
    return -2;
  }
  catch (JNIException& jniException) {
    cout << "An unexpected JNI error occurred. " << jniException.what() << endl;
    return -3;
  }
  catch (std::exception& e) {
    cout << "An unexpected C++ error occurred. " << e.what() << endl;
    return -4;
  }

}
