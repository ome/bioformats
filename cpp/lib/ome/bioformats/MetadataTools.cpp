/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#include <string>

#include <boost/format.hpp>

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/XMLTools.h>

#include <ome/compat/regex.h>

#include <ome/internal/version.h>

#include <ome/common/xml/Platform.h>
#include <ome/common/xml/String.h>
#include <ome/common/xml/dom/Document.h>
#include <ome/common/xml/dom/Element.h>
#include <ome/common/xml/dom/NodeList.h>

#include <ome/xml/Document.h>

#include <ome/xml/meta/Convert.h>
#include <ome/xml/meta/MetadataException.h>
#include <ome/xml/meta/OMEXMLMetadataRoot.h>

#include <ome/xml/model/Annotation.h>
#include <ome/xml/model/Channel.h>
#include <ome/xml/model/Image.h>
#include <ome/xml/model/MetadataOnly.h>
#include <ome/xml/model/ModelException.h>
#include <ome/xml/model/OMEModel.h>
#include <ome/xml/model/OriginalMetadataAnnotation.h>
#include <ome/xml/model/Pixels.h>
#include <ome/xml/model/StructuredAnnotations.h>
#include <ome/xml/model/XMLAnnotation.h>
#include <ome/xml/model/primitives/Timestamp.h>

#include <xercesc/framework/MemBufInputSource.hpp>
#include <xercesc/sax/SAXException.hpp>
#include <xercesc/sax2/DefaultHandler.hpp>
#include <xercesc/sax2/SAX2XMLReader.hpp>
#include <xercesc/sax2/XMLReaderFactory.hpp>

// Include last due to side effect of MPL vector limit setting which can change the default
#include <boost/lexical_cast.hpp>

using boost::format;

using ome::xml::meta::Metadata;
using ome::xml::meta::MetadataException;
using ome::xml::meta::MetadataStore;
using ome::xml::meta::MetadataRoot;
using ome::xml::meta::OMEXMLMetadata;
using ome::xml::meta::OMEXMLMetadataRoot;

using ome::xml::model::Image;
using ome::xml::model::MetadataOnly;
using ome::xml::model::ModelException;
using ome::xml::model::OMEModel;
using ome::xml::model::OriginalMetadataAnnotation;
using ome::xml::model::Pixels;
using ome::xml::model::StructuredAnnotations;
using ome::xml::model::XMLAnnotation;
using ome::xml::model::primitives::Timestamp;
using ome::xml::model::primitives::PositiveInteger;

namespace
{

  /// Use default creation date?
  bool defaultCreationDate = false;

  template<typename T>
  void parseNodeValue(::ome::common::xml::dom::Node& node,
                      T&                             value)
  {
    if (node)
      {
        try
          {
            value = boost::lexical_cast<T>(node.getNodeValue());
          }
        catch (boost::bad_lexical_cast const&)
          {
            /// @todo Warn if parsing fails.
          }
      }
  }

  const ome::compat::regex schema_match("^http://www.openmicroscopy.org/Schemas/OME/(.*)$");

  class OMEXMLVersionParser : public xercesc::DefaultHandler
  {
  public:
    OMEXMLVersionParser():
      xercesc::DefaultHandler()
    {}

    virtual ~OMEXMLVersionParser() {}

    void
    startElement(const XMLCh* const         uri,
                 const XMLCh* const         localname,
                 const XMLCh* const         qname,
                 const xercesc::Attributes& attrs)
    {
      if (ome::common::xml::String(localname) == "OME")
        {
          std::string ns = ome::common::xml::String(uri);

          ome::compat::smatch found;

          if (ome::compat::regex_match(ns, found, schema_match))
            {
              version = found[1];
              throw xercesc::SAXException(ome::common::xml::String("Found schema version"));
            }
        }
    }

    std::string
    getVersion() const
    {
      return version;
    }

  private:
    std::string version;
  };

}

namespace ome
{
  namespace bioformats
  {

    std::string
    createID(std::string const&  type,
             dimension_size_type idx)
    {
      static boost::format fmt("%1%:%2%");
      fmt.clear();
      fmt % type % idx;
      return fmt.str();
    }

    std::string
    createID(std::string const&  type,
             dimension_size_type idx1,
             dimension_size_type idx2)
    {
      static boost::format fmt("%1%:%2%:%3%");
      fmt.clear();
      fmt % type % idx1 % idx2;
      return fmt.str();
    }

    std::string
    createID(std::string const&  type,
             dimension_size_type idx1,
             dimension_size_type idx2,
             dimension_size_type idx3)
    {
      static boost::format fmt("%1%:%2%:%3%:%4%");
      fmt.clear();
      fmt % type % idx1 % idx2 % idx3;
      return fmt.str();
    }

    std::string
    createID(std::string const&  type,
             dimension_size_type idx1,
             dimension_size_type idx2,
             dimension_size_type idx3,
             dimension_size_type idx4)
    {
      static boost::format fmt("%1%:%2%:%3%:%4%:%5%");
      fmt.clear();
      fmt % type % idx1 % idx2 % idx3 % idx4;
      return fmt.str();
    }

    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(ome::common::xml::dom::Document& document)
    {
      ome::common::xml::dom::Element docroot(document.getDocumentElement());

      ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(ome::compat::make_shared< ::ome::xml::meta::OMEXMLMetadata>());
      ome::xml::model::detail::OMEModel model;
      ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(meta->getRoot()));
      root->update(docroot, model);

      return meta;
    }

    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const boost::filesystem::path& file)
    {
      // Parse OME-XML into DOM Document.
      ome::common::xml::Platform xmlplat;
      ome::common::xml::dom::Document doc;
      try
        {
          doc = ome::xml::createDocument(file);
        }
      catch (const std::runtime_error&)
        {
          ome::common::xml::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          doc = ome::xml::createDocument(file, params);
        }
      return createOMEXMLMetadata(doc);
    }

    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const std::string& text)
    {
      // Parse OME-XML into DOM Document.
      ome::common::xml::Platform xmlplat;
      ome::common::xml::dom::Document doc;
      try
        {
          doc = ome::xml::createDocument(text, ome::common::xml::dom::ParseParameters(),
                                                 "OME-XML");
        }
      catch (const std::runtime_error&)
        {
          ome::common::xml::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          doc = ome::xml::createDocument(text, params, "Broken OME-XML");
        }
      return createOMEXMLMetadata(doc);
    }

    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(std::istream& stream)
    {
      // Parse OME-XML into DOM Document.
      ome::common::xml::Platform xmlplat;
      ome::common::xml::dom::Document doc;
      try
        {
          doc = ome::xml::createDocument(stream, ome::common::xml::dom::ParseParameters(),
                                                 "OME-XML");
        }
      catch (const std::runtime_error&)
        {
          ome::common::xml::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          doc = ome::xml::createDocument(stream, params, "Broken OME-XML");
        }
      return createOMEXMLMetadata(doc);
    }

    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const FormatReader& reader,
                         bool                doPlane,
                         bool                doImageName)
    {
      ome::compat::shared_ptr<OMEXMLMetadata> metadata(ome::compat::make_shared<OMEXMLMetadata>());
      ome::compat::shared_ptr<MetadataStore> store(ome::compat::static_pointer_cast<MetadataStore>(metadata));
      fillMetadata(*store, reader, doPlane, doImageName);
      return metadata;
    }

    ome::compat::shared_ptr< ::ome::xml::meta::MetadataRoot>
    createOMEXMLRoot(const std::string& document)
    {
      /// @todo Implement model transforms.

      ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(ome::compat::dynamic_pointer_cast< ::ome::xml::meta::OMEXMLMetadata>(createOMEXMLMetadata(document)));
      return meta ? meta->getRoot() : ome::compat::shared_ptr< ::ome::xml::meta::MetadataRoot>();
    }

    ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    getOMEXMLMetadata(ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve)
    {
      ome::compat::shared_ptr<OMEXMLMetadata> ret;

      if (retrieve)
        {
          ome::compat::shared_ptr<OMEXMLMetadata> omexml(ome::compat::dynamic_pointer_cast<OMEXMLMetadata>(retrieve));
          if (omexml)
            {
              ret = omexml;
            }
          else
            {
              ret = ome::compat::shared_ptr<OMEXMLMetadata>(ome::compat::make_shared<OMEXMLMetadata>());
              ome::xml::meta::convert(*retrieve, *ret);
            }
        }

      return ret;
    }

    std::string
    getOMEXML(::ome::xml::meta::OMEXMLMetadata& omexml,
              bool                              validate)
    {
      std::string xml(omexml.dumpXML());

      if (validate && !validateOMEXML(xml))
        throw std::runtime_error("Invalid OME-XML");

      return xml;
    }

    bool
    validateOMEXML(const std::string& document)
    {
      return validateXML(document, "OME-XML");
    }

    bool
    validateModel(::ome::xml::meta::Metadata& meta,
                  bool                        correct)
    {
      bool valid = true;

      const dimension_size_type seriesCount = meta.getImageCount();

      for (dimension_size_type series = 0U; series < seriesCount; ++series)
        {
          const dimension_size_type sizeX = meta.getPixelsSizeX(series);
          const dimension_size_type sizeY = meta.getPixelsSizeY(series);
          const dimension_size_type sizeZ = meta.getPixelsSizeZ(series);
          const dimension_size_type sizeT = meta.getPixelsSizeT(series);
          const dimension_size_type sizeC = meta.getPixelsSizeC(series);
          if (!sizeX || !sizeY || !sizeZ || !sizeT)
            {
              valid = false;
              if (!correct)
                break;

              boost::format fmt("Invalid image dimensionality for Image %1%: SizeX=%2%, SizeY=%3%, SizeZ=%4%, SizeT=%5%, SizeC=%6%");
              fmt % series % sizeX % sizeY % sizeZ % sizeT % sizeC;
              throw FormatException(fmt.str());
            }
          
          // If no Channel objects are defined, create with 1
          // SamplePerPixel.
          if (meta.getChannelCount(series) == 0 && sizeC)
            {
              valid = false;
              if (!correct)
                {
                  break;
                }
              else
                {
                  for (dimension_size_type c = 0; c < sizeC; ++c)
                    meta.setChannelSamplesPerPixel(1U, series, c);
                }
            }

          const dimension_size_type effC = meta.getChannelCount(series);
          // Sum of all set SamplesPerPixel
          dimension_size_type realSizeC = 0U;
          std::vector<dimension_size_type> badChannels;
          for (dimension_size_type c = 0; c < effC; ++c)
            {
              dimension_size_type samples = 1U;

              try
                {
                  samples = meta.getChannelSamplesPerPixel(series, c);
                  realSizeC += samples;
                }
              catch (const MetadataException&)
                {
                  badChannels.push_back(c);
                }
            }

          // If all subchannels add up to SizeC then the Channel
          // metadata is correct; do nothing.
          if (sizeC && realSizeC &&
              sizeC == realSizeC &&
              badChannels.empty())
            continue;          

          valid = false;
          if (!correct)
            break;

          if (sizeC == 0 && realSizeC == 0)
            {
              // No channels or subchannels defined; default to one
              // subchannel per channel.
              meta.setPixelsSizeC(effC, series);
              for (dimension_size_type c = 0; c < effC; ++c)
                meta.setChannelSamplesPerPixel(1U, series, c);
            }
          else if (realSizeC > 0 &&
                   badChannels.empty())
            {
              // All subchannels set and no bad channels; update SizeC
              // to reflect the subchannel total.
              meta.setPixelsSizeC(realSizeC, series);
            }
          else if (sizeC > 0 &&
                   sizeC >= realSizeC &&
                   !badChannels.empty())
            {
              // Some or all channels are unset.  If the unallocated
              // subchannels are evenly divisible between the unset
              // channels, assign.
              const dimension_size_type allocSamples = realSizeC;
              const dimension_size_type unallocSamples = sizeC >= realSizeC ? sizeC - allocSamples : 0U;
              const dimension_size_type splitSamples = unallocSamples / badChannels.size();
              const dimension_size_type badSamples = unallocSamples % badChannels.size();

              // No point guessing since we can't make a sensible
              // subchannel allocation; bail out now.
              if (!splitSamples || badSamples || sizeC < realSizeC)
                {
                  boost::format fmt("Unable to correct invalid ChannelSamplesPerPixel in Image #%1%; %2% channel(s) set, %3% channel(s) unset, %4% subchannel(s) unallocated");
                  fmt % series;
                  fmt % (effC - badChannels.size());
                  fmt % badChannels.size();
                  fmt % (sizeC - realSizeC);
                  throw FormatException(fmt.str());
                }

              for (std::vector<dimension_size_type>::const_iterator i = badChannels.begin();
                   i != badChannels.end();
                   ++i)
                meta.setChannelSamplesPerPixel(splitSamples, series, *i);
            }
        }

      return valid;
    }

    void
    fillMetadata(::ome::xml::meta::MetadataStore& store,
                 const FormatReader&              reader,
                 bool                             doPlane,
                 bool                             doImageName)
    {
      dimension_size_type oldseries = reader.getSeries();

      for (dimension_size_type s = 0; s < reader.getSeriesCount(); ++s)
        {
          reader.setSeries(s);

          const boost::optional<boost::filesystem::path>& cfile(reader.getCurrentFile());

          std::ostringstream nos;
          if (doImageName && !!cfile)
            {
              nos << (*cfile).string();
              if (reader.getSeriesCount() > 1)
                nos << " #" << (s + 1);
            }
          std::string imageName = nos.str();

          std::string pixelType = reader.getPixelType();

          if (!imageName.empty())
            store.setImageID(createID("Image", s), s);
          if (!!cfile)
            setDefaultCreationDate(store, s, *cfile);

          fillPixels(store, reader);

          try
            {
              OMEXMLMetadata& omexml(dynamic_cast<OMEXMLMetadata&>(store));
              addMetadataOnly(omexml, s);
            }
          catch (const std::bad_cast&)
            {
            }

          if (doPlane)
            {
              for (dimension_size_type p = 0; p < reader.getImageCount(); ++p)
                {
                  ome::compat::array<dimension_size_type, 3> coords = reader.getZCTCoords(p);
                  // The cast to int here is nasty, but the data model
                  // isn't using unsigned types…
                  store.setPlaneTheZ(static_cast<int>(coords[0]), s, p);
                  store.setPlaneTheC(static_cast<int>(coords[1]), s, p);
                  store.setPlaneTheT(static_cast<int>(coords[2]), s, p);
                }
            }

        }

      reader.setSeries(oldseries);
    }

    void
    fillMetadata(::ome::xml::meta::MetadataStore&                          store,
                 const std::vector<ome::compat::shared_ptr<CoreMetadata> > seriesList,
                 bool                                                      doPlane)
    {
      dimension_size_type s = 0U;
      for (std::vector<ome::compat::shared_ptr<CoreMetadata> >::const_iterator i = seriesList.begin();
           i != seriesList.end();
           ++i, ++s)
        {
          std::string pixelType = (*i)->pixelType;

          store.setImageID(createID("Image", s), s);

          fillPixels(store, **i, s);

          try
            {
              OMEXMLMetadata& omexml(dynamic_cast<OMEXMLMetadata&>(store));
              addMetadataOnly(omexml, s);
            }
          catch (const std::bad_cast&)
            {
            }

          if (doPlane)
            {
              for (dimension_size_type p = 0; p < (*i)->imageCount; ++p)
                {
                  dimension_size_type sizeZT = (*i)->sizeZ * (*i)->sizeT;
                  dimension_size_type effSizeC = 1U;
                  if (sizeZT)
                    effSizeC = (*i)->imageCount / sizeZT;

                  ome::compat::array<dimension_size_type, 3> coords =
                    getZCTCoords((*i)->dimensionOrder,
                                 (*i)->sizeZ,
                                 effSizeC,
                                 (*i)->sizeT,
                                 (*i)->imageCount,
                                 p);
                  // The cast to int here is nasty, but the data model
                  // isn't using unsigned types…
                  store.setPlaneTheZ(static_cast<int>(coords[0]), s, p);
                  store.setPlaneTheC(static_cast<int>(coords[1]), s, p);
                  store.setPlaneTheT(static_cast<int>(coords[2]), s, p);
                }
            }
        }
    }

    void
    fillAllPixels(::ome::xml::meta::MetadataStore& store,
                  const FormatReader&              reader)
    {
      dimension_size_type oldseries = reader.getSeries();
      for (dimension_size_type s = 0; s < reader.getSeriesCount(); ++s)
        {
          reader.setSeries(s);
          fillPixels(store, reader);
        }
      reader.setSeries(oldseries);
    }

    void
    fillPixels(::ome::xml::meta::MetadataStore& store,
               const FormatReader&              reader)
    {
      dimension_size_type series = reader.getSeries();

      store.setPixelsID(createID("Pixels", series), series);
      store.setPixelsBigEndian(!reader.isLittleEndian(), series);
      store.setPixelsSignificantBits(reader.getBitsPerPixel(), series);
      store.setPixelsDimensionOrder(reader.getDimensionOrder(), series);
      store.setPixelsInterleaved(reader.isInterleaved(), series);
      store.setPixelsType(reader.getPixelType(), series);

      // The cast to int here is nasty, but the data model isn't using
      // unsigned types…
      store.setPixelsSizeX(static_cast<PositiveInteger::value_type>(reader.getSizeX()), series);
      store.setPixelsSizeY(static_cast<PositiveInteger::value_type>(reader.getSizeY()), series);
      store.setPixelsSizeZ(static_cast<PositiveInteger::value_type>(reader.getSizeZ()), series);
      store.setPixelsSizeT(static_cast<PositiveInteger::value_type>(reader.getSizeT()), series);
      store.setPixelsSizeC(static_cast<PositiveInteger::value_type>(reader.getSizeC()), series);

      dimension_size_type effSizeC = reader.getEffectiveSizeC();
      for (dimension_size_type c = 0; c < effSizeC; ++c)
        {
          store.setChannelID(createID("Channel", series, c), series, c);
          store.setChannelSamplesPerPixel(static_cast<PositiveInteger::value_type>(reader.getRGBChannelCount(c)), series, c);
        }

    }

    void
    fillPixels(::ome::xml::meta::MetadataStore& store,
               const CoreMetadata&              seriesMetadata,
               dimension_size_type              series)
    {
      store.setPixelsID(createID("Pixels", series), series);
      store.setPixelsBigEndian(!seriesMetadata.littleEndian, series);
      store.setPixelsSignificantBits(seriesMetadata.bitsPerPixel, series);
      store.setPixelsDimensionOrder(seriesMetadata.dimensionOrder, series);
      store.setPixelsInterleaved(seriesMetadata.interleaved, series);
      store.setPixelsType(seriesMetadata.pixelType, series);

      // The cast to int here is nasty, but the data model isn't using
      // unsigned types…
      store.setPixelsSizeX(static_cast<PositiveInteger::value_type>(seriesMetadata.sizeX), series);
      store.setPixelsSizeY(static_cast<PositiveInteger::value_type>(seriesMetadata.sizeY), series);
      store.setPixelsSizeZ(static_cast<PositiveInteger::value_type>(seriesMetadata.sizeZ), series);
      store.setPixelsSizeT(static_cast<PositiveInteger::value_type>(seriesMetadata.sizeT), series);
      store.setPixelsSizeC(static_cast<PositiveInteger::value_type>
                           (std::accumulate(seriesMetadata.sizeC.begin(), seriesMetadata.sizeC.end(),
                                            dimension_size_type(0))), series);

      dimension_size_type effSizeC = seriesMetadata.sizeC.size();

      for (dimension_size_type c = 0; c < effSizeC; ++c)
        {
          dimension_size_type rgbC = seriesMetadata.sizeC.at(c);

          store.setChannelID(createID("Channel", series, c), series, c);
          store.setChannelSamplesPerPixel(static_cast<PositiveInteger::value_type>(rgbC), series, c);
        }

    }

    void
    addMetadataOnly(::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type               series,
                    bool                              resolve)
    {
      if (resolve)
        omexml.resolveReferences();
      ome::compat::shared_ptr<MetadataRoot> root(omexml.getRoot());
      ome::compat::shared_ptr<OMEXMLMetadataRoot> omexmlroot(ome::compat::dynamic_pointer_cast<OMEXMLMetadataRoot>(root));
      if (omexmlroot)
        {
          ome::compat::shared_ptr<Image> image = omexmlroot->getImage(series);
          if (image)
            {
              ome::compat::shared_ptr<Pixels> pixels = image->getPixels();
              if (pixels)
                {
                  ome::compat::shared_ptr<MetadataOnly> meta(ome::compat::make_shared<MetadataOnly>());
                  pixels->setMetadataOnly(meta);
                }
            }
        }
    }

    Modulo
    getModuloAlongZ(const ::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type                     image)
    {
      return getModulo(omexml, "ModuloAlongZ", image);
    }

    Modulo
    getModuloAlongT(const ::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type                     image)
    {
      return getModulo(omexml, "ModuloAlongT", image);
    }

    Modulo
    getModuloAlongC(const ::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type                     image)
    {
      return getModulo(omexml, "ModuloAlongC", image);
    }

    Modulo
    getModulo(const ::ome::xml::meta::OMEXMLMetadata& omexml,
              const std::string&                      tag,
              dimension_size_type                     image)
    {
      // @todo Implement Modulo retrieval.
      ::ome::xml::meta::OMEXMLMetadata& momexml(const_cast< ::ome::xml::meta::OMEXMLMetadata&>(omexml));

      ome::compat::shared_ptr<OMEXMLMetadataRoot> root =
        ome::compat::dynamic_pointer_cast<OMEXMLMetadataRoot>(momexml.getRoot());
      if (!root) // Should never occur
        throw std::logic_error("OMEXMLMetadata does not have an OMEXMLMetadataRoot");

      ome::compat::shared_ptr< ::ome::xml::model::Image> mimage(root->getImage(image));
      if (!mimage)
        throw std::runtime_error("Image does not exist in OMEXMLMetadata");

      for (::ome::xml::meta::MetadataStore::index_type i = 0;
           i < mimage->sizeOfLinkedAnnotationList();
           ++i)
        {
          ome::compat::shared_ptr< ::ome::xml::model::Annotation> annotation(mimage->getLinkedAnnotation(i));
          ome::compat::shared_ptr< ::ome::xml::model::XMLAnnotation> xmlannotation(ome::compat::dynamic_pointer_cast< ::ome::xml::model::XMLAnnotation>(annotation));
          if (xmlannotation)
            {
              try
                {
                  ome::common::xml::Platform xmlplat;
                  ::ome::common::xml::dom::Document xmlroot(::ome::xml::createDocument(xmlannotation->getValue()));
                  ::ome::common::xml::dom::NodeList nodes(xmlroot.getElementsByTagName(tag));

                  Modulo m(tag.substr(tag.size() ? tag.size() - 1 : 0));

                  if (nodes.size() > 0)
                    {
                      ::ome::common::xml::dom::Element modulo(nodes.at(0));
                      ::ome::common::xml::dom::NamedNodeMap attrs(modulo.getAttributes());


                      ::ome::common::xml::dom::Node start = attrs.getNamedItem("Start");
                      ::ome::common::xml::dom::Node end = attrs.getNamedItem("End");
                      ::ome::common::xml::dom::Node step = attrs.getNamedItem("Step");
                      ::ome::common::xml::dom::Node type = attrs.getNamedItem("Type");
                      ::ome::common::xml::dom::Node typeDescription = attrs.getNamedItem("TypeDescription");
                      ::ome::common::xml::dom::Node unit = attrs.getNamedItem("Unit");

                      parseNodeValue(start, m.start);
                      parseNodeValue(end, m.end);
                      parseNodeValue(step, m.step);
                      parseNodeValue(type, m.type);
                      parseNodeValue(typeDescription, m.typeDescription);
                      parseNodeValue(unit, m.unit);

                      ::ome::common::xml::dom::NodeList labels = modulo.getElementsByTagName("Label");
                      if (labels && !labels.empty())
                        {
                          for (::ome::common::xml::dom::NodeList::iterator i = labels.begin();
                               i != labels.end();
                               ++i)
                            m.labels.push_back(i->getTextContent());
                        }

                    }
                  return m;
                }
              catch (...)
                {
                  throw std::runtime_error("Error parsing Modulo annotation");
                }
            }
        }

      throw std::runtime_error("Modulo annotation does not exist in OMEXMLMetadata");
    }

    void
    verifyMinimum(::ome::xml::meta::MetadataRetrieve& retrieve,
                  dimension_size_type                 series)
    {
      // The Java equivalent of this function checks whether various
      // properties are null.  In the C++ implementation, most of the
      // properties checked can not be null (they are plain value
      // types) so the checks are not performed for these cases.  For
      // the string values, these may be empty (if unset), so this is
      // checked for in place of being null.

      try
        {
          MetadataStore& store(dynamic_cast<MetadataStore&>(retrieve));
          if (!store.getRoot())
            throw FormatException("Metadata object has null root; call createRoot() first");
        }
      catch (const std::bad_cast&)
        {
        }

      if (retrieve.getImageID(series).empty())
        {
          boost::format fmt("Image ID #%1% is empty");
          fmt % series;
          throw FormatException(fmt.str());
        }

      if (retrieve.getPixelsID(series).empty())
        {
          boost::format fmt("Pixels ID #%1% is empty");
          fmt % series;
          throw FormatException(fmt.str());
        }

      for (Metadata::index_type channel = 0;
           channel < retrieve.getChannelCount(series);
           ++channel)
        {
          if (retrieve.getChannelID(series, channel).empty())
            {
              boost::format fmt("Channel ID #%1% in Image #%2% is empty");
              fmt % channel % series;
              throw FormatException(fmt.str());
            }
        }
    }

    void
    removeBinData(::ome::xml::meta::OMEXMLMetadata& omexml)
    {
      omexml.resolveReferences();
      ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          std::vector<ome::compat::shared_ptr<ome::xml::model::Image> >& images(root->getImageList());
          for(std::vector<ome::compat::shared_ptr<ome::xml::model::Image> >::const_iterator image = images.begin();
              image != images.end();
              ++image)
            {
              ome::compat::shared_ptr<ome::xml::model::Pixels> pixels((*image)->getPixels());
              if (pixels)
                {
                  // Note a copy not a reference to avoid iterator
                  // invalidation during removal.
                  std::vector<ome::compat::shared_ptr<ome::xml::model::BinData> > binData(pixels->getBinDataList());
                  for (std::vector<ome::compat::shared_ptr<ome::xml::model::BinData> >::iterator bin = binData.begin();
                   bin != binData.end();
                       ++bin)
                    {
                      pixels->removeBinData(*bin);
                    }
                  ome::compat::shared_ptr<ome::xml::model::MetadataOnly> metadataOnly;
                  pixels->setMetadataOnly(metadataOnly);
                }
            }
        }
    }

    void
    removeChannels(::ome::xml::meta::OMEXMLMetadata& omexml,
                   dimension_size_type               image,
                   dimension_size_type               sizeC)
    {
      omexml.resolveReferences();
      ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          ome::compat::shared_ptr<ome::xml::model::Image>& imageref(root->getImage(image));
          if (image)
            {
              ome::compat::shared_ptr<ome::xml::model::Pixels> pixels(imageref->getPixels());
              if (pixels)
                {
                  std::vector<ome::compat::shared_ptr<ome::xml::model::Channel> > channels(pixels->getChannelList());
                  for (Metadata::index_type c = 0U; c < channels.size(); ++c)
                    {
                      ome::compat::shared_ptr<ome::xml::model::Channel> channel(channels.at(c));
                      if (channel->getID().empty() || c >= sizeC)
                        pixels->removeChannel(channel);
                    }
                }
            }
        }
    }

    MetadataMap
    getOriginalMetadata(::ome::xml::meta::OMEXMLMetadata& omexml)
    {
      MetadataMap map;

      ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          ome::compat::shared_ptr<StructuredAnnotations> sa(root->getStructuredAnnotations());
          if (sa)
            {
              for (OMEXMLMetadata::index_type i = 0; i < sa->sizeOfXMLAnnotationList(); ++i)
                {
                  // Check if this is an OriginalMetadataAnnotation object.
                  ome::compat::shared_ptr<XMLAnnotation> annotation(sa->getXMLAnnotation(i));
                  ome::compat::shared_ptr<OriginalMetadataAnnotation> original(ome::compat::dynamic_pointer_cast<OriginalMetadataAnnotation>(annotation));
                  if (original)
                    {
                      const OriginalMetadataAnnotation::metadata_type kv(original->getMetadata());
                      map.set(kv.first, kv.second);
                      continue;
                    }

                  // Fall back to parsing by hand.
                  try
                    {
                      std::string wrappedValue("<wrapped>");
                      wrappedValue += annotation->getValue();
                      wrappedValue += "</wrapped>";

                      common::xml::Platform xmlplat;
                      common::xml::dom::ParseParameters params;
                      params.validationScheme = xercesc::XercesDOMParser::Val_Never;
                      common::xml::dom::Document doc(ome::xml::createDocument(wrappedValue));

                      std::vector<common::xml::dom::Element> OriginalMetadataValue_nodeList = ome::xml::model::detail::OMEModelObject::getChildrenByTagName(doc.getDocumentElement(), "OriginalMetadata");
                      if (OriginalMetadataValue_nodeList.size() > 1)
                        {
                          format fmt("Value node list size %1% != 1");
                          fmt % OriginalMetadataValue_nodeList.size();
                          throw ModelException(fmt.str());
                        }
                      else if (OriginalMetadataValue_nodeList.size() != 0)
                        {
                          OriginalMetadataAnnotation::metadata_type kv;
                          std::vector<common::xml::dom::Element> Key_nodeList = ome::xml::model::detail::OMEModelObject::getChildrenByTagName(OriginalMetadataValue_nodeList.at(0), "Key");
                          if (Key_nodeList.size() > 1)
                            {
                              format fmt("Key node list size %1% != 1");
                              fmt % Key_nodeList.size();
                              throw ModelException(fmt.str());
                            }
                          else if (Key_nodeList.size() != 0)
                            {
                              kv.first = Key_nodeList.at(0).getTextContent();
                            }
                          std::vector<common::xml::dom::Element> Value_nodeList = ome::xml::model::detail::OMEModelObject::getChildrenByTagName(OriginalMetadataValue_nodeList.at(0), "Value");
                          if (Value_nodeList.size() > 1)
                            {
                              format fmt("Value node list size %1% != 1");
                              fmt % Value_nodeList.size();
                              throw ModelException(fmt.str());
                            }
                          else if (Value_nodeList.size() != 0)
                            {
                              kv.second = Value_nodeList.at(0).getTextContent();
                            }
                          map.set(kv.first, kv.second);
                          continue;
                        }
                    }
                  catch (const std::exception&)
                    {
                      /// @todo log error
                    }
                }
            }
        }

      return map;
    }

    void
    fillOriginalMetadata(::ome::xml::meta::OMEXMLMetadata& omexml,
                         const MetadataMap&                metadata)
    {
      omexml.resolveReferences();

      if (metadata.empty())
        return;

      MetadataMap flat(metadata.flatten());

      ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          ome::compat::shared_ptr<StructuredAnnotations> sa(root->getStructuredAnnotations());
          if (!sa)
            sa = ome::compat::make_shared<StructuredAnnotations>();
          OMEXMLMetadata::index_type annotationIndex = sa->sizeOfXMLAnnotationList();
          OMEXMLMetadata::index_type idIndex = sa->sizeOfXMLAnnotationList();

          std::set<std::string> ids;
          for (OMEXMLMetadata::index_type i = 0; i < annotationIndex; ++i)
            {
              // Already in metadata store
              ids.insert(omexml.getXMLAnnotationID(i));
            }

          for (MetadataMap::const_iterator i = flat.begin();
               i != flat.end();
               ++i, ++annotationIndex)
            {
              std::string id;
              do
                {
                  id = createID("Annotation", idIndex);
                  ++idIndex;
                }
              while (ids.find(id) != ids.end());

              std::ostringstream value;
              boost::apply_visitor(::ome::bioformats::detail::MetadataMapValueTypeOStreamVisitor(value), i->second);

              ome::compat::shared_ptr<OriginalMetadataAnnotation> orig(ome::compat::make_shared<OriginalMetadataAnnotation>());
              orig->setID(id);
              orig->setMetadata(OriginalMetadataAnnotation::metadata_type(i->first, value.str()));
              ome::compat::shared_ptr<XMLAnnotation> xmlorig(ome::compat::static_pointer_cast<XMLAnnotation>(orig));
              sa->addXMLAnnotation(xmlorig);
            }

          root->setStructuredAnnotations(sa);
        }
    }

    

    std::string
    getModelVersion()
    {
      return OME_MODEL_VERSION;
    }

    std::string
    getModelVersion(ome::common::xml::dom::Document& document)
    {
      ome::common::xml::dom::Element docroot(document.getDocumentElement());

      std::string ns = common::xml::String(docroot->getNamespaceURI());

      ome::compat::smatch found;

      if (ome::compat::regex_match(ns, found, schema_match))
        {
          return found[1];
        }
      return "";
    }

    std::string
    getModelVersion(const std::string& document)
    {
      ome::common::xml::Platform xmlplat;

      ome::compat::shared_ptr<xercesc::SAX2XMLReader> parser(xercesc::XMLReaderFactory::createXMLReader());
      // We only want to get the schema version, so disable checking
      // of schema etc.  If there are problems with the XML, they'll
      // be picked up when we parse it for real.  Here, we'll only
      // read the first element if it's a valid OME-XML document.
      parser->setFeature(xercesc::XMLUni::fgSAX2CoreValidation, false);
      parser->setFeature(xercesc::XMLUni::fgXercesSchemaFullChecking, false);
      parser->setFeature(xercesc::XMLUni::fgXercesLoadSchema, false);
      // Needed to get the schema namespace.
      parser->setFeature(xercesc::XMLUni::fgSAX2CoreNameSpaces, true);

      xercesc::MemBufInputSource source(reinterpret_cast<const XMLByte *>(document.c_str()),
                                        static_cast<XMLSize_t>(document.size()),
                                        common::xml::String("OME-XML model version"));

      OMEXMLVersionParser handler;
      parser->setContentHandler(&handler);

      try
        {
          parser->parse(source);
        }
      catch (const xercesc::XMLException& e)
        {
          ome::common::xml::String message(e.getMessage());
          std::cerr << "XMLException parsing schema version: " << message << '\n';
          return "";
        }
      catch (const xercesc::SAXParseException& e)
        {
          ome::common::xml::String message(e.getMessage());
          std::cerr << "SAXParseException parsing schema version: " << message << '\n';
          return "";
        }
      catch (const xercesc::SAXException&)
        {
          // Early termination (expected).
        }
      catch (...)
        {
          std::cerr << "Unexpected Exception parsing schema version\n";
          return "";
        }

      return handler.getVersion();
    }

    std::string
    transformToLatestModelVersion(const std::string& document)
    {
      /// @todo Implement model transforms.
      return document;
    }

    bool
    defaultCreationDateEnabled()
    {
      return defaultCreationDate;
    }

    void
    defaultCreationDateEnabled(bool enabled)
    {
      defaultCreationDate = enabled;
    }

    void
    setDefaultCreationDate(::ome::xml::meta::MetadataStore& store,
                           dimension_size_type              series,
                           const boost::filesystem::path&   id)
    {
      if (defaultCreationDateEnabled())
        {
          Timestamp cdate;
          if (exists(id))
            cdate = Timestamp(boost::posix_time::from_time_t(boost::filesystem::last_write_time(id)));
          store.setImageAcquisitionDate(cdate, series);
        }
    }

    ome::xml::model::enums::DimensionOrder
    createDimensionOrder(const std::string& order)
    {
      // A set could be used here, but given the tiny string length, a
      // linear scan is quicker than an index lookup.

      static const std::string validchars("XYZTC");

      std::string validorder;

      for (std::string::const_iterator i = order.begin();
           i != order.end();
           ++i)
        {
          if (validchars.find_first_of(*i) != std::string::npos &&
              validorder.find_first_of(*i) == std::string::npos)
              validorder += *i;
        }

      for (std::string::const_iterator i = validchars.begin();
           i != validchars.end();
           ++i)
        {
          if (validorder.find_first_of(*i) == std::string::npos)
            validorder += *i;
        }

      return ome::xml::model::enums::DimensionOrder(validorder);
    }

    storage_size_type
    pixelSize(const ::ome::xml::meta::MetadataRetrieve& meta,
              dimension_size_type                       series)
    {
      dimension_size_type x = meta.getPixelsSizeX(series);
      dimension_size_type y = meta.getPixelsSizeY(series);
      dimension_size_type z = meta.getPixelsSizeZ(series);
      dimension_size_type t = meta.getPixelsSizeT(series);
      dimension_size_type c = meta.getPixelsSizeC(series);

      storage_size_type size = bytesPerPixel(meta.getPixelsType(series));
      size *= x;
      size *= y;
      size *= z;
      size *= t;
      size *= c;

      return size;
    }

    storage_size_type
    pixelSize(const ::ome::xml::meta::MetadataRetrieve& meta)
    {
      storage_size_type size = 0;

      for (dimension_size_type  s = 0;
           s < meta.getImageCount();
           ++s)
        {
          size += pixelSize(meta, s);
        }

      return size;
    }

    storage_size_type
    significantPixelSize(const ::ome::xml::meta::MetadataRetrieve& meta,
                         dimension_size_type                       series)
    {
      dimension_size_type x = meta.getPixelsSizeX(series);
      dimension_size_type y = meta.getPixelsSizeY(series);
      dimension_size_type z = meta.getPixelsSizeZ(series);
      dimension_size_type t = meta.getPixelsSizeT(series);
      dimension_size_type c = meta.getPixelsSizeC(series);

      storage_size_type size = significantBitsPerPixel(meta.getPixelsType(series));
      size *= x;
      size *= y;
      size *= z;
      size *= t;
      size *= c;

      return size;
    }

    storage_size_type
    significantPixelSize(const ::ome::xml::meta::MetadataRetrieve& meta)
    {
      storage_size_type size = 0;

      for (dimension_size_type  s = 0;
           s < meta.getImageCount();
           ++s)
        {
          size += pixelSize(meta, s);
        }

      return size;
    }

  }
}
