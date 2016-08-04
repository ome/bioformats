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

#ifndef OME_BIOFORMATS_XMLTOOLS_H
#define OME_BIOFORMATS_XMLTOOLS_H

#include <ome/common/xml/dom/Document.h>

namespace ome
{
  namespace bioformats
  {

    /*
     * Utility functions for XML processing.  This is an equivalent to
     * the Java XMLtools implementation, with the following
     * differences:
     *
     * createBuilder: Not required; handled internally by common::xml::dom.
     * createDocument: Use ome::common::xml::dom::createDocument(qualifiedName).
     * parseDOM(file): Use ome::common::xml::dom::createDocument(file)
     * parseDOM(string): Use ome::common::xml::dom::createDocument(string)
     * parseDOM(stream): Use ome::common::xml::dom::createDocument(steam)
     *
     * dumpXML(): Use ome::common::xml::dom::writeDocument(doc, string);
     * writeXML(stream): Use ome::common::xml::dom::writeDocument(doc, stream);
     *
     * validateXML(doc): Use ome::common::xml::dom::validate(doc)
     *
     * All parseXML SAX methods are currently unimplemented.
     *
     * XSLT transforms:
     * getStylesheet currently unimplemented.
     * avoidUndeclaredNamespaces currently unimplemented.
     * transformXML currently unimplemented.
     *
     * Helpers:
     * checkUTF8: May not be required if xerces can handle it.
     * error/fatalError/warning: Not implemented, may not be required here.
     */

    /**
     * Replace special characters with XML entities in an XML string.
     *
     * @param s the string to escape.
     * @returns the escaped string.
     */
    std::string
    escapeXML(const std::string& s);

    /**
     * Filter control codes and invalid sequences in an XML string.
     *
     * Remove all control codes except for LF, HT and CR.  Also
     * replace @c &# with @c #.
     *
     * @param s the string to filter.
     * @returns the filtered string.
     */
    std::string
    sanitizeXML(const std::string& s);

    /**
     * Validate XML in an XML string.
     *
     * @param s the string to validate.
     * @param loc the file location or other descriptive text for the
     * string; used for error reporting only.
     * @returns @c true if valid, @c false if invalid.
     */
    bool
    validateXML(const std::string& s,
                const std::string& loc = "XML");

  }
}

#endif // OME_BIOFORMATS_XMLTOOLS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
