//
// bioformats.h
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (C) 2008-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

#ifndef BIO_FORMATS_H
#define BIO_FORMATS_H

// TODO - eventually want to automatically generate this list

#include "jace/JNIHelper.h"

#include "jace/JArray.h"
#include "jace/JNIException.h"
#include "jace/OptionList.h"
#include "jace/StaticVmLoader.h"
using namespace jace;

#include "jace/proxy/types/JBoolean.h"
#include "jace/proxy/types/JByte.h"
#include "jace/proxy/types/JChar.h"
#include "jace/proxy/types/JDouble.h"
#include "jace/proxy/types/JFloat.h"
#include "jace/proxy/types/JInt.h"
#include "jace/proxy/types/JLong.h"
#include "jace/proxy/types/JShort.h"
#include "jace/proxy/types/JVoid.h"
using namespace jace::proxy::types;

#include "jace/proxy/java/lang/String.h"
using namespace jace::proxy::java::lang;

#include "jace/proxy/java/io/IOException.h"
using namespace jace::proxy::java::io;

#include "jace/proxy/loci/formats/AWTImageTools.h"
#include "jace/proxy/loci/formats/AxisGuesser.h"
#include "jace/proxy/loci/formats/ChannelFiller.h"
#include "jace/proxy/loci/formats/ChannelMerger.h"
#include "jace/proxy/loci/formats/ChannelSeparator.h"
#include "jace/proxy/loci/formats/ClassList.h"
//#include "jace/proxy/loci/formats/CoreMetadata.h"
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
using namespace jace::proxy::loci::formats;

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
using namespace jace::proxy::loci::formats::cache;

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
using namespace jace::proxy::loci::formats::codec;

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
using namespace jace::proxy::loci::formats::gui;

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
using namespace jace::proxy::loci::formats::in;

#include "jace/proxy/loci/formats/meta/AggregateMetadata.h"
#include "jace/proxy/loci/formats/meta/DummyMetadata.h"
#include "jace/proxy/loci/formats/meta/FilterMetadata.h"
#include "jace/proxy/loci/formats/meta/IMetadata.h"
#include "jace/proxy/loci/formats/meta/IMinMaxStore.h"
#include "jace/proxy/loci/formats/meta/MetadataConverter.h"
#include "jace/proxy/loci/formats/meta/MetadataRetrieve.h"
#include "jace/proxy/loci/formats/meta/MetadataStore.h"
using namespace jace::proxy::loci::formats::meta;

#include "jace/proxy/loci/formats/ome/OmeisException.h"
#include "jace/proxy/loci/formats/ome/OmeisImporter.h"
#include "jace/proxy/loci/formats/ome/OMEXML2003FCMetadata.h"
#include "jace/proxy/loci/formats/ome/OMEXML200706Metadata.h"
#include "jace/proxy/loci/formats/ome/OMEXML200802Metadata.h"
#include "jace/proxy/loci/formats/ome/OMEXML200809Metadata.h"
#include "jace/proxy/loci/formats/ome/OMEXMLMetadata.h"
using namespace jace::proxy::loci::formats::ome;

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
using namespace jace::proxy::loci::formats::out;

#include "jace/proxy/loci/formats/tools/EditTiffG.h"
#include "jace/proxy/loci/formats/tools/ImageConverter.h"
#include "jace/proxy/loci/formats/tools/ImageInfo.h"
#include "jace/proxy/loci/formats/tools/TiffComment.h"
#include "jace/proxy/loci/formats/tools/XMLIndent.h"
#include "jace/proxy/loci/formats/tools/XMLValidate.h"
using namespace jace::proxy::loci::formats::tools;

#endif
