/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include <iostream>

#include <ome/compat/memory.h>

#include <ome/common/filesystem.h>
#include <ome/common/xml/Platform.h>
#include <ome/common/xml/dom/Document.h>

#include <ome/xml/Document.h>

#include <ome/xml/model/OME.h>
#include <ome/xml/model/OMEModel.h>
#include <ome/xml/model/detail/OMEModel.h>

using boost::filesystem::path;
using ome::compat::shared_ptr;
using ome::compat::make_shared;
namespace xml = ome::common::xml;
namespace model = ome::xml::model;

namespace
{

  shared_ptr<model::OME>
  readModel(const path& filename)
  {
    /* read-example-start */
    // XML DOM tree containing parsed file content
    xml::dom::Document inputdoc(ome::xml::createDocument(filename));
    // OME Model (needed only during parsing to track model object references)
    model::detail::OMEModel model;
    // OME Model root object
    shared_ptr<model::OME> modelroot(make_shared<model::OME>());
    // Fill OME model object tree from XML DOM tree
    modelroot->update(inputdoc.getDocumentElement(), model);
    /* read-example-end */

    return modelroot;
  }

  void
  writeModel(shared_ptr<model::OME>& modelroot,
             std::ostream&           stream)
  {
    /* write-example-start */
    // Schema version to use
    const std::string schema("http://www.openmicroscopy.org/Schemas/OME/2013-06");
    // XML DOM tree (initially containing an empty OME root element)
    xml::dom::Document outputdoc(xml::dom::createEmptyDocument(schema, "OME"));
    // Fill output DOM document from OME-XML model
    modelroot->asXMLElement(outputdoc);
    // Dump DOM tree as text to stream
    xml::dom::writeDocument(outputdoc, stream);
    /* write-example-end */
    stream << '\n';
  }

}

int
main(int argc, char *argv[])
{
  try
    {
      if (argc > 1)
        {
          // XML platform (required by Xerces)
          xml::Platform xmlplat;

          // Portable path
          path filename(argv[1]);

          // Read XML file content into OME-XML model object tree
          shared_ptr<model::OME> model(readModel(filename));

          // Write XML content from OME-XML model object tree to stream
          writeModel(model, std::cout);
        }
      else
        {
          std::cerr << "Usage: " << argv[0] << " ome-xml.xml\n";
          std::exit(1);
        }
    }
  catch (const std::exception& e)
    {
      std::cerr << "Caught exception: " << e.what() << '\n';
      std::exit(1);
    }
  catch (...)
    {
      std::cerr << "Caught unknown exception\n";
      std::exit(1);
    }
}
