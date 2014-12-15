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

#include <ome/bioformats/FormatException.h>
#include <ome/bioformats/MetadataTools.h>

#include <ome/internal/version.h>

#include <ome/xml/meta/OMEXMLMetadataRoot.h>

#include <ome/xml/model/Image.h>
#include <ome/xml/model/MetadataOnly.h>
#include <ome/xml/model/OMEModel.h>
#include <ome/xml/model/Pixels.h>
#include <ome/xml/model/primitives/Timestamp.h>

using ome::xml::meta::Metadata;
using ome::xml::meta::MetadataStore;
using ome::xml::meta::MetadataRoot;
using ome::xml::meta::OMEXMLMetadata;
using ome::xml::meta::OMEXMLMetadataRoot;

using ome::xml::model::Image;
using ome::xml::model::MetadataOnly;
using ome::xml::model::Pixels;
using ome::xml::model::OMEModel;
using ome::xml::model::primitives::Timestamp;
using ome::xml::model::primitives::PositiveInteger;

namespace
{

  /// Use default creation date?
  bool defaultCreationDate = false;

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

    std::shared_ptr< ::ome::xml::meta::Metadata>
    createOMEXMLMetadata(const std::string& document)
    {
      /// @todo Parse OME-XML document.
      return std::shared_ptr< ::ome::xml::meta::Metadata>();
    }

    std::shared_ptr<Metadata>
    createOMEXMLMetadata(const FormatReader& reader,
                         bool                doPlane,
                         bool                doImageName)
    {
      std::shared_ptr<Metadata> metadata(std::make_shared<OMEXMLMetadata>());
      std::shared_ptr<MetadataStore> store(std::static_pointer_cast<MetadataStore>(metadata));
      fillMetadata(*store, reader, doPlane, doImageName);
      return metadata;
    }

    std::shared_ptr< ::ome::xml::meta::MetadataRoot>
    createOMEXMLRoot(const std::string& document)
    {
      /// @todo Implement model transforms.

      // OMEModel model;
      // std::shared_ptr<OMEXMLMetadataRoot> root(new OMEXMLMetadataRoot(xml, model));
      // model.resolveReferences();
      // return root;

      return std::shared_ptr< ::ome::xml::meta::MetadataRoot>();
    }

    std::shared_ptr< ::ome::xml::meta::Metadata>
    getOMEXMLMetadata(std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve)
    {
      std::shared_ptr<Metadata> ret;

      if (retrieve)
        {
          std::shared_ptr<OMEXMLMetadata> omexml(std::dynamic_pointer_cast<OMEXMLMetadata>(retrieve));
          if (omexml)
            {
              ret = std::static_pointer_cast<Metadata>(omexml);
            }
          else
            {
              ret = std::shared_ptr<Metadata>(new OMEXMLMetadata());
              // @todo Implement convertMetadata.
              // convertMetadata(metadata, ret);
            }
        }

      return ret;
    }

    std::string
    getOMEXML(::ome::xml::meta::OMEXMLMetadata& omexml)
    {
      /// @todo Implement OME-XML conversion to string.

      return omexml.dumpXML();

      /// @todo Implement validation and root namespace setting.
    }

    bool
    validateOMEXML(const std::string& document)
    {
      /// @todo Implement validation.
      return false;
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

          const boost::optional<std::string>& cfile(reader.getCurrentFile());

          std::ostringstream nos;
          if (doImageName && !!cfile)
            {
              nos << *cfile;
              if (reader.getSeriesCount() > 1)
                nos << " #" << (s + 1);
            }
          std::string imageName = nos.str();

          std::string pixelType = reader.getPixelType();

          if (!imageName.empty())
            store.setImageID(createID("Image", s), s);
          if (!!cfile)
            setDefaultCreationDate(store, s, boost::filesystem::path(*cfile));

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
                    dimension_size_type               series)
    {
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
    getModulo(const ::ome::xml::meta::OMEXMLMetadata& omexml,
              const std::string&                      tag,
              dimension_size_type                     image)
    {
      // @todo Implement Modulo retrieval.

      throw std::runtime_error("Unimplemented");
      return Modulo(tag); // Incorrect placeholder.
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

  }
}
