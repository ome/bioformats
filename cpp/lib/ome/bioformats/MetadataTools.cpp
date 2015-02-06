/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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
#include <boost/lexical_cast.hpp>

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/XMLTools.h>

#include <ome/internal/version.h>

#include <ome/xerces/Platform.h>
#include <ome/xerces/dom/Document.h>
#include <ome/xerces/dom/Element.h>
#include <ome/xerces/dom/NodeList.h>

#include <ome/xml/meta/Convert.h>
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

using boost::format;

using ome::xml::meta::Metadata;
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
  void parseNodeValue(::ome::xerces::dom::Node& node,
                      T&                        value)
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

    std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(ome::xerces::dom::Document& document)
    {
      ome::xerces::dom::Element docroot(document.getDocumentElement());

      std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(std::make_shared< ::ome::xml::meta::OMEXMLMetadata>());
      ome::xml::model::detail::OMEModel model;
      std::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(std::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(meta->getRoot()));
      root->update(docroot, model);

      return meta;
    }

    std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const boost::filesystem::path& file)
    {
      // Parse OME-XML into DOM Document.
      ome::xerces::Platform xmlplat;
      ome::xerces::dom::Document doc;
      try
        {
          doc = ome::xerces::dom::createDocument(file);
        }
      catch (const std::runtime_error&)
        {
          ome::xerces::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          doc = ome::xerces::dom::createDocument(file, params);
        }
      return createOMEXMLMetadata(doc);
    }

    std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const std::string& text)
    {
      // Parse OME-XML into DOM Document.
      ome::xerces::Platform xmlplat;
      ome::xerces::dom::Document doc;
      try
        {
          doc = ome::xerces::dom::createDocument(text, ome::xerces::dom::ParseParameters(),
                                                 "OME-XML");
        }
      catch (const std::runtime_error&)
        {
          ome::xerces::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          doc = ome::xerces::dom::createDocument(text, params, "Broken OME-XML");
        }
      return createOMEXMLMetadata(doc);
    }

    std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(std::istream& stream)
    {
      // Parse OME-XML into DOM Document.
      ome::xerces::Platform xmlplat;
      ome::xerces::dom::Document doc;
      try
        {
          doc = ome::xerces::dom::createDocument(stream, ome::xerces::dom::ParseParameters(),
                                                 "OME-XML");
        }
      catch (const std::runtime_error&)
        {
          ome::xerces::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          doc = ome::xerces::dom::createDocument(stream, params, "Broken OME-XML");
        }
      return createOMEXMLMetadata(doc);
    }

    std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    createOMEXMLMetadata(const FormatReader& reader,
                         bool                doPlane,
                         bool                doImageName)
    {
      std::shared_ptr<OMEXMLMetadata> metadata(std::make_shared<OMEXMLMetadata>());
      std::shared_ptr<MetadataStore> store(std::static_pointer_cast<MetadataStore>(metadata));
      fillMetadata(*store, reader, doPlane, doImageName);
      return metadata;
    }

    std::shared_ptr< ::ome::xml::meta::MetadataRoot>
    createOMEXMLRoot(const std::string& document)
    {
      /// @todo Implement model transforms.

      std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata> meta(std::dynamic_pointer_cast< ::ome::xml::meta::OMEXMLMetadata>(createOMEXMLMetadata(document)));
      return meta ? meta->getRoot() : std::shared_ptr< ::ome::xml::meta::MetadataRoot>();
    }

    std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>
    getOMEXMLMetadata(std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve)
    {
      std::shared_ptr<OMEXMLMetadata> ret;

      if (retrieve)
        {
          std::shared_ptr<OMEXMLMetadata> omexml(std::dynamic_pointer_cast<OMEXMLMetadata>(retrieve));
          if (omexml)
            {
              ret = omexml;
            }
          else
            {
              ret = std::shared_ptr<OMEXMLMetadata>(std::make_shared<OMEXMLMetadata>());
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

      if (!validateOMEXML(xml))
        throw std::runtime_error("Invalid OME-XML");

      return xml;
    }

    bool
    validateOMEXML(const std::string& document)
    {
      return validateXML(document, "OME-XML");
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
              nos << (*cfile).native();
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
          catch (const std::bad_cast& e)
            {
            }

          if (doPlane)
            {
              for (dimension_size_type p = 0; p < reader.getImageCount(); ++p)
                {
                  std::array<dimension_size_type, 3> coords = reader.getZCTCoords(p);
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
          store.setChannelSamplesPerPixel(static_cast<PositiveInteger::value_type>(reader.getRGBChannelCount()), series, c);
        }

    }

    void
    addMetadataOnly(::ome::xml::meta::OMEXMLMetadata& omexml,
                    dimension_size_type               series,
                    bool                              resolve)
    {
      if (resolve)
        omexml.resolveReferences();
      std::shared_ptr<MetadataRoot> root(omexml.getRoot());
      std::shared_ptr<OMEXMLMetadataRoot> omexmlroot(std::dynamic_pointer_cast<OMEXMLMetadataRoot>(root));
      if (omexmlroot)
        {
          std::shared_ptr<Image> image = omexmlroot->getImage(series);
          if (image)
            {
              std::shared_ptr<Pixels> pixels = image->getPixels();
              if (pixels)
                {
                  std::shared_ptr<MetadataOnly> meta(std::make_shared<MetadataOnly>());
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

      std::shared_ptr<OMEXMLMetadataRoot> root =
        std::dynamic_pointer_cast<OMEXMLMetadataRoot>(momexml.getRoot());
      if (!root) // Should never occur
        throw std::logic_error("OMEXMLMetadata does not have an OMEXMLMetadataRoot");

      std::shared_ptr< ::ome::xml::model::Image> mimage(root->getImage(image));
      if (!mimage)
        throw std::runtime_error("Image does not exist in OMEXMLMetadata");

      for (::ome::xml::meta::MetadataStore::index_type i = 0;
           i < mimage->sizeOfLinkedAnnotationList();
           ++i)
        {
          std::shared_ptr< ::ome::xml::model::Annotation> annotation(mimage->getLinkedAnnotation(i));
          std::shared_ptr< ::ome::xml::model::XMLAnnotation> xmlannotation(std::dynamic_pointer_cast< ::ome::xml::model::XMLAnnotation>(annotation));
          if (xmlannotation)
            {
              try
                {
                  ome::xerces::Platform xmlplat;
                  ::ome::xerces::dom::Document xmlroot(::ome::xerces::dom::createDocument(xmlannotation->getValue()));
                  ::ome::xerces::dom::NodeList nodes(xmlroot.getElementsByTagName(tag));

                  Modulo m(tag.substr(tag.size() ? tag.size() - 1 : 0));

                  if (nodes.size() > 0)
                    {
                      ::ome::xerces::dom::Element modulo(nodes.at(0));
                      ::ome::xerces::dom::NamedNodeMap attrs(modulo.getAttributes());


                      ::ome::xerces::dom::Node start = attrs.getNamedItem("Start");
                      ::ome::xerces::dom::Node end = attrs.getNamedItem("End");
                      ::ome::xerces::dom::Node step = attrs.getNamedItem("Step");
                      ::ome::xerces::dom::Node type = attrs.getNamedItem("Type");
                      ::ome::xerces::dom::Node typeDescription = attrs.getNamedItem("TypeDescription");
                      ::ome::xerces::dom::Node unit = attrs.getNamedItem("Unit");

                      parseNodeValue(start, m.start);
                      parseNodeValue(end, m.end);
                      parseNodeValue(step, m.step);
                      parseNodeValue(type, m.type);
                      parseNodeValue(typeDescription, m.typeDescription);
                      parseNodeValue(unit, m.unit);

                      ::ome::xerces::dom::NodeList labels = modulo.getElementsByTagName("Label");
                      if (labels && !labels.empty())
                        {
                          for (::ome::xerces::dom::NodeList::iterator i = labels.begin();
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
      catch (const std::bad_cast& e)
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
      std::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(std::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          std::vector<std::shared_ptr<ome::xml::model::Image> >& images(root->getImageList());
          for(std::vector<std::shared_ptr<ome::xml::model::Image> >::const_iterator image = images.begin();
              image != images.end();
              ++image)
            {
              std::shared_ptr<ome::xml::model::Pixels> pixels((*image)->getPixels());
              if (pixels)
                {
                  // Note a copy not a reference to avoid iterator
                  // invalidation during removal.
                  std::vector<std::shared_ptr<ome::xml::model::BinData> > binData(pixels->getBinDataList());
                  for (std::vector<std::shared_ptr<ome::xml::model::BinData> >::iterator bin = binData.begin();
                   bin != binData.end();
                       ++bin)
                    {
                      pixels->removeBinData(*bin);
                    }
                  std::shared_ptr<ome::xml::model::MetadataOnly> metadataOnly;
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
      std::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(std::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          std::shared_ptr<ome::xml::model::Image>& imageref(root->getImage(image));
          if (image)
            {
              std::shared_ptr<ome::xml::model::Pixels> pixels(imageref->getPixels());
              if (pixels)
                {
                  std::vector<std::shared_ptr<ome::xml::model::Channel> > channels(pixels->getChannelList());
                  for (Metadata::index_type c = 0U; c < channels.size(); ++c)
                    {
                      std::shared_ptr<ome::xml::model::Channel> channel(channels.at(c));
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

      std::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(std::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(omexml.getRoot()));
      if (root)
        {
          std::shared_ptr<StructuredAnnotations> sa(root->getStructuredAnnotations());
          if (sa)
            {
              for (OMEXMLMetadata::index_type i = 0; i < sa->sizeOfXMLAnnotationList(); ++i)
                {
                  // Check if this is an OriginalMetadataAnnotation object.
                  std::shared_ptr<XMLAnnotation> annotation(sa->getXMLAnnotation(i));
                  std::shared_ptr<OriginalMetadataAnnotation> original(std::dynamic_pointer_cast<OriginalMetadataAnnotation>(annotation));
                  if (original)
                    {
                      const OriginalMetadataAnnotation::metadata_type kv(original->getMetadata());
                      map.set(kv.first, kv.second);
                      continue;
                    }

                  // Fall back to parsing by hand.
                  try
                    {
                      xerces::Platform xmlplat;
                      xerces::dom::ParseParameters params;
                      params.validationScheme = xercesc::XercesDOMParser::Val_Never;
                      xerces::dom::Document doc(ome::xerces::dom::createDocument(annotation->getValue()));

                      std::vector<xerces::dom::Element> OriginalMetadataValue_nodeList = ome::xml::model::detail::OMEModelObject::getChildrenByTagName(doc, "OriginalMetadata");
                      if (OriginalMetadataValue_nodeList.size() > 1)
                        {
                          format fmt("Value node list size %1% != 1");
                          fmt % OriginalMetadataValue_nodeList.size();
                          throw ModelException(fmt.str());
                        }
                      else if (OriginalMetadataValue_nodeList.size() != 0)
                        {
                          OriginalMetadataAnnotation::metadata_type kv;
                          std::vector<xerces::dom::Element> Key_nodeList = ome::xml::model::detail::OMEModelObject::getChildrenByTagName(OriginalMetadataValue_nodeList.at(0), "Key");
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
                          std::vector<xerces::dom::Element> Value_nodeList = ome::xml::model::detail::OMEModelObject::getChildrenByTagName(OriginalMetadataValue_nodeList.at(0), "Value");
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
                  catch (const std::exception& /* e */)
                    {
                      /// @todo log error
                    }
                }
            }
        }

      return map;
    }

    std::string
    getModelVersion()
    {
      return OME_MODEL_VERSION;
    }

    std::string
    getModelVersion(const std::string& document)
    {
      /// @todo Parse model version.

      return getModelVersion();
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

  }
}
