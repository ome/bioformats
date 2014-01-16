/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2013 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_OME_OMEXMLMETADATAROOT_H
#define OME_BIOFORMATS_OME_OMEXMLMETADATAROOT_H

#include <ome/compat/cstdint.h>

#include <ome/xml/model/OME.h>
#include <ome/bioformats/meta/MetadataRoot.h>

namespace ome
{
  namespace bioformats
  {
    namespace ome
    {

      /**
       * OME-XML metadata root node.
       */
      class OMEXMLMetadataRoot : public ::ome::xml::model::OME,
				 public ::ome::bioformats::meta::MetadataRoot
      {
      public:
        /// Constructor.
        OMEXMLMetadataRoot();

	/**
         * Construct OME-XML model recursively from an XML DOM tree.
         *
         * @param element root of the XML DOM tree to from which to
         * construct the model object graph.
         * @param model handler for the OME model used to track
         * instances and references seen during the update.
         * @throws EnumerationException if there is an error
         * instantiating an enumeration during model object creation.
         */
        OMEXMLMetadataRoot(::ome::xerces::dom::Element& element,
			   ::ome::xml::model::OMEModel& model);

	/// Copy constructor.
	OMEXMLMetadataRoot(const OMEXMLMetadataRoot& copy);

	/// Copy constructor.
	OMEXMLMetadataRoot(const xml::model::OME& copy);

      public:
        /// Destructor.
        virtual
        ~OMEXMLMetadataRoot();
      };

    }
  }
}

#endif // OME_BIOFORMATS_OME_OMEXMLMETADATAROOT_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
