/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2016 Open Microscopy Environment:
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

#ifndef OME_XML_MODEL_OMETRANSFORM_H
#define OME_XML_MODEL_OMETRANSFORM_H

#include <ome/compat/regex.h>
#include <ome/common/xml/dom/Document.h>
#include <ome/common/xsl/Transformer.h>
#include <ome/xml/OMETransformResolver.h>
#include <ome/xml/OMEEntityResolver.h>

namespace ome
{
  namespace xml
  {

    /**
     * Determine the model schema version used in an XML document.
     *
     * @param document the document to check.  This may be any
     * supported input type (file path, string, stream).
     * @returns the schema version in use, or an empty string if the
     * version could not be determined.
     */
    inline
    std::string
    getModelVersion(ome::common::xml::dom::Document& document)
    {
      ome::common::xml::dom::Element docroot(document.getDocumentElement());

      std::string ns = common::xml::String(docroot->getNamespaceURI());

      ome::compat::smatch found;
      static const ome::compat::regex schema_match("^http://www.openmicroscopy.org/Schemas/OME/(.*)$");

      if (ome::compat::regex_match(ns, found, schema_match))
        {
          return found[1];
        }
      else if (ns == "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd")
        {
          return "2003-FC";
        }

      return "";
    }

    /**
     * Transform OME-XML to a different model schema version.
     *
     * @param target_schema the schema version to transform the input to.
     * @param input the source XML.  This may be any supported input
     * type (file path, string, stream).
     * @param output the destination for the transformed XML.  This
     * may be any supported output type (file path, string, stream).
     * @param entity_resolver the entity resolver to use for
     * validation.
     * @param transform_resolver the transform resolver to use for
     * determining the transform sequence between schema versions.
     */
    template<typename Input, typename Output>
    void
    transform(const std::string&                target_schema,
              const Input&                      input,
              Output&                           output,
              ome::common::xml::EntityResolver& entity_resolver,
              OMETransformResolver&             transform_resolver)
    {
      ome::common::xml::dom::Document inputdoc;
      try
        {
          inputdoc = ome::common::xml::dom::createDocument(input, entity_resolver);
        }
      catch (const std::runtime_error&) // retry without strict validation
        {
          ome::common::xml::dom::ParseParameters params;
          params.doSchema = false;
          params.validationSchemaFullChecking = false;
          inputdoc = ome::common::xml::dom::createDocument(input, entity_resolver, params);
        }

      const std::string source_schema(getModelVersion(inputdoc));

      if (source_schema.empty())
        throw std::runtime_error("Impossible to determine model schema version");

      typedef std::pair<std::vector<OMETransformResolver::Transform>, OMETransformResolver::Quality> transform_list;
      transform_list transforms(transform_resolver.transform_order(source_schema, target_schema));

      /// @todo: Logging of transforms being applied.

      if (transforms.first.empty()) // No transformation required
        {
          ome::common::xml::dom::writeDocument(inputdoc, output);
        }
      else
        {
          ome::common::xsl::Transformer transformer;
          transformer.setEntityResolver(&entity_resolver);

          // For loading and storing intermediate schema content;
          std::istringstream is;
          std::ostringstream os;

          ome::common::xml::dom::writeDocument(inputdoc, os);
          is.str(os.str());
          is.clear();
          os.str("");
          os.clear();

          for (transform_list::first_type::const_iterator i = transforms.first.begin();
               i != transforms.first.end();
               ++i)
            {
              // Note that validation can trigger asserts inside the
              // Xalan library, so it's safest to disable it and
              // validate the end result.
              if (i + 1 == transforms.first.end()) // Use output type
                {
                  transformer.setUseValidation(false);
                  transformer.transform(i->file, is, output);
                }
              else // Use output stringstream
                {
                  transformer.setUseValidation(false);
                  transformer.transform(i->file, is, os);
                  is.str(os.str());
                  is.clear();
                  os.str("");
                  os.clear();
                }
            }
        }
    }

    /**
     * Transform OME-XML to a different model schema version.
     *
     * An OMETransformResolver will be created on the fly; use the
     * function taking an OMETransformResolver as a parameter if
     * applying multiple transforms to avoid the overhead of scanning
     * the filesystem for available transforms multiple times.  This
     * function is intended only for convenience when applying a
     * single transform.
     *
     * @param target_schema the schema version to transform the input to.
     * @param input the source XML.  This may be any supported input
     * type (file path, string, stream).
     * @param output the destination for the transformed XML.  This
     * may be any supported output type (file path, string, stream).
     * @param entity_resolver the entity resolver to use for
     * validation.
     */
    template<typename Input, typename Output>
    void
    transform(const std::string&                target_schema,
              const Input&                      input,
              Output&                           output,
              ome::common::xml::EntityResolver& entity_resolver)
    {
      OMETransformResolver transform_resolver;
      transform(target_schema, input, output,
                entity_resolver, transform_resolver);
    }

  }
}

#endif // OME_XML_MODEL_OMETRANSFORM_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
