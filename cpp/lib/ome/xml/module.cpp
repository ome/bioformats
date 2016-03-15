/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2016 Open Microscopy Environment:
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

#include <ome/xml/config-internal.h>

#define OME_COMMON_MODULE_INTROSPECTION 1
#include <ome/common/module.h>
#include <ome/xml/module.h>

namespace
{

  using ome::common::RegisterModule;

  void register_paths()
  {
    // OME-XML package-specific paths.
    static RegisterModule omexml_pkgdata
      ("ome-xml-data",
       "OME_XML_PKGDATADIR",
       "OME_XML_HOME",
       "OME_HOME",
       OME_XML_INSTALL_FULL_PKGDATADIR,
       OME_XML_INSTALL_PKGDATADIR,
       OME_XML_INSTALL_PREFIX,
       OME_XML_SHLIBDIR,
       module_path);

    static RegisterModule omexml_schema
      ("ome-xml-schema",
       "OME_XML_SCHEMADIR",
       "OME_XML_HOME",
       "OME_HOME",
       OME_XML_INSTALL_FULL_SCHEMADIR,
       OME_XML_INSTALL_SCHEMADIR,
       OME_XML_INSTALL_PREFIX,
       OME_XML_SHLIBDIR,
       module_path);

    static RegisterModule omexml_transform
      ("ome-xml-transform",
       "OME_XML_TRANSFORMDIR",
       "OME_XML_HOME",
       "OME_HOME",
       OME_XML_INSTALL_FULL_TRANSFORMDIR,
       OME_XML_INSTALL_TRANSFORMDIR,
       OME_XML_INSTALL_PREFIX,
       OME_XML_SHLIBDIR,
       module_path);

    static RegisterModule omexml_sample
      ("ome-xml-sample",
       "OME_XML_SAMPLEDIR",
       "OME_XML_HOME",
       "OME_HOME",
       OME_XML_INSTALL_FULL_SAMPLEDIR,
       OME_XML_INSTALL_SAMPLEDIR,
       OME_XML_INSTALL_PREFIX,
       OME_XML_SHLIBDIR,
       module_path);
  }

  struct AutoRegister
  {
    AutoRegister()
    {
      register_paths();
    }
  };

  AutoRegister path_register;

}

namespace ome
{
  namespace xml
  {

    void
    register_module_paths()
    {
      ome::common::register_module_paths();
      register_paths();
    }

  }
}
