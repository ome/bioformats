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

#include <iostream>
#include <fstream>

#include <boost/filesystem.hpp>

#include <ome/xml/OMETransformResolver.h>

#include <ome/test/test.h>

class TransformQueryTest : public ::testing::Test
{
public:
  ome::xml::OMETransformResolver resolver;

  TransformQueryTest():
    ::testing::Test(),
    resolver()
  {
  }

  void SetUp()
  {
    std::cout << "Processed schema versions:\n";

    std::set<std::string> versions = resolver.schema_versions();
    for (std::set<std::string>::const_iterator v = versions.begin();
         v != versions.end();
         ++v)
      std::cout << "  " << *v << '\n';
  }

  void
  write_ome_transforms(const ome::xml::OMETransformResolver& resolver,
                       std::ostream&                         xmlstream)
  {
  const std::string header =
    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n"
    "<!--\n"
    "	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
    "	#\n"
    "	# Copyright (C) 2012 - 2016 Open Microscopy Environment\n"
    "	#       Massachusetts Institute of Technology,\n"
    "	#       National Institutes of Health,\n"
    "	#       University of Dundee,\n"
    "	#       University of Wisconsin at Madison\n"
    "	#\n"
    "	#    This library is free software; you can redistribute it and/or\n"
    "	#    modify it under the terms of the GNU Lesser General Public\n"
    "	#    License as published by the Free Software Foundation; either\n"
    "	#    version 2.1 of the License, or (at your option) any later version.\n"
    "	#\n"
    "	#    This library is distributed in the hope that it will be useful,\n"
    "	#    but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
    "	#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n"
    "	#    Lesser General Public License for more details.\n"
    "	#\n"
    "	#    You should have received a copy of the GNU Lesser General Public\n"
    "	#    License along with this library; if not, write to the Free Software\n"
    "	#    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA\n"
    "	#\n"
    "	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
    "-->\n"
    "\n"
    "<!--\n"
    "	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
    "	# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk\n"
    "	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
    "-->\n"
  "\n";

  xmlstream << header;

  std::set<std::string> versions = resolver.schema_versions();
  if(!versions.empty())
    {
      xmlstream << "<ome-transforms current=\"" << *versions.rbegin() << "\">\n";

      for (std::set<std::string>::const_reverse_iterator from = versions.rbegin();
           from != versions.rend();
           ++from)
        {
          bool downgrade = false;

          xmlstream << "\t<source schema=\"" << *from << "\">\n"
                    << "\t\t<upgrades>\n";

          for (std::set<std::string>::const_reverse_iterator to = versions.rbegin();
               to != versions.rend();
               ++to)
            {
              if (*to < *from && !downgrade)
                {
                  xmlstream << "\t\t</upgrades>\n"
                            << "\t\t<downgrades>\n";
                  downgrade = true;
                }

              std::pair<std::vector<ome::xml::OMETransformResolver::Transform>,
                        ome::xml::OMETransformResolver::Quality> order =
                resolver.transform_order(*from, *to);

              if (*to == *from)
                {
                  if (order.first.size() > 0)
                    std::cerr << "Transforms exist for same source and target schema!";
                  continue;
                }

              xmlstream << "\t\t\t<target schema=\"" << *to
                        << "\" quality=\"" << order.second << "\">\n";
              for (std::vector<ome::xml::OMETransformResolver::Transform>::const_iterator o
                     = order.first.begin();
                   o != order.first.end();
                   ++o)
                {
                  xmlstream << "\t\t\t\t<transform file=\""
                          << o->file.filename().string() << "\"/>\n";
                }
              xmlstream << "\t\t\t</target>\n";
            }
          if (!downgrade)
            {
              xmlstream << "\t\t</upgrades>\n"
                      << "\t\t<downgrades>\n";
            }
          xmlstream << "\t\t</downgrades>\n"
                    << "\t</source>\n";
        }

      xmlstream << "</ome-transforms>\n";
    }
  }
};

TEST_F(TransformQueryTest, GraphViz)
{
  boost::filesystem::path dotfile = PROJECT_BINARY_DIR "/cpp/test/ome-xml/data/ome-transforms.dot";
  boost::filesystem::path dotdir = dotfile.parent_path();
  if (!exists(dotdir) && !is_directory(dotdir) && !create_directories(dotdir))
    throw std::runtime_error("Data directory unavailable and could not be created");

  std::ofstream dotstream(dotfile.string().c_str());

  resolver.write_graphviz(dotstream);
  resolver.write_graphviz(std::cout);
}

TEST_F(TransformQueryTest, OmeTransformsXML)
{
  boost::filesystem::path xmlfile = PROJECT_BINARY_DIR "/cpp/test/ome-xml/data/ome-transforms.xml";
  boost::filesystem::path xmldir = xmlfile.parent_path();
  if (!exists(xmldir) && !is_directory(xmldir) && !create_directories(xmldir))
    throw std::runtime_error("Data directory unavailable and could not be created");

  std::ofstream xmlstream(xmlfile.string().c_str());

  write_ome_transforms(resolver, xmlstream);
  write_ome_transforms(resolver, std::cout);
}
