/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2015 Open Microscopy Environment:
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

#ifndef OME_XML_MODEL_DOCUMENT_H
#define OME_XML_MODEL_DOCUMENT_H

#include <ome/common/xml/EntityResolver.h>
#include <ome/common/xml/dom/Document.h>

namespace ome
{
  namespace xml
  {

    /**
     * Construct a Document from the content of a file.
     *
     * This is the same as ome::common::xml::dom::createDocument(const
     * boost::filesystem::path&, EntityResolver&, const
     * ParseParameters&) but uses OMEEntityResolver to resolve all OME
     * schemas from the local catalog.
     *
     * @param file the file to read.
     * @param params XML parser parameters.
     * @returns the new Document.
     */
    ome::common::xml::dom::Document
    createDocument(const boost::filesystem::path&                file,
                   const ome::common::xml::dom::ParseParameters& params = ome::common::xml::dom::ParseParameters());

    /**
     * Construct a Document from the content of a string.
     *
     * This is the same as ome::common::xml::dom::createDocument(const
     * std::string&, EntityResolver&, const ParseParameters&, const
     * std::string&) but uses OMEEntityResolver to resolve all OME
     * schemas from the local catalog.
     *
     * @param text the string to use.
     * @param params XML parser parameters.
     * @param id document filename (for error reporting only).
     * @returns the new Document.
     */
    ome::common::xml::dom::Document
    createDocument(const std::string&                            text,
                   const ome::common::xml::dom::ParseParameters& params = ome::common::xml::dom::ParseParameters(),
                   const std::string&                            id = "membuf");

    /**
     * Construct a Document from the content of an input stream.
     *
     * This is the same as
     * ome::common::xml::dom::createDocument(std::istream&,
     * EntityResolver&, const ParseParameters&, const std::string&)
     * but uses OMEEntityResolver to resolve all OME schemas from the
     * local catalog.
     *
     * @param stream the stream to read.
     * @param params XML parser parameters.
     * @param id document filename (for error reporting only).
     * @returns the new Document.
     */
    ome::common::xml::dom::Document
    createDocument(std::istream&                                 stream,
                   const ome::common::xml::dom::ParseParameters& params = ome::common::xml::dom::ParseParameters(),
                   const std::string&                            id = "streambuf");

  }
}

#endif // OME_XML_MODEL_DOCUMENT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
