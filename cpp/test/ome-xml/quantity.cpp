/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2015 - 2016 Open Microscopy Environment:
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

#include "quantity.h"

using namespace ome::xml::model::enums;
using ome::xml::model::primitives::Quantity;
using ome::xml::model::primitives::convert;

namespace
{

  test_map
  read_test_data()
  {
    const boost::filesystem::path testdatafile(PROJECT_SOURCE_DIR "/cpp/test/ome-xml/data/units");

    boost::iostreams::stream<boost::iostreams::file_descriptor> is(testdatafile);
    is.imbue(std::locale::classic());

    test_map ret;

    std::string line;
    while (std::getline(is, line))
      {
        if (line.length() == 0) // Empty line; do nothing.
          {
          }
        else if (line[0] == '#') // Comment line
          {
          }
        else
          {
            std::vector<std::string> tokens;
            boost::split(tokens, line, boost::is_any_of("\t"), boost::token_compress_off);

            if (tokens.size() == 9)
              {
                try
                  {
                    std::string from_unit = tokens[0];
                    std::string to_unit = tokens[1];
                    double to_value, from_value;
                    try
                      {
                        from_value = boost::lexical_cast<double>(tokens[2]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail [f2]: " << tokens[2] << '\n';
                        throw;
                      }
                    try
                      {
                        to_value = boost::lexical_cast<double>(tokens[3]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail [f3]: " << tokens[3] << '\n';
                        throw;
                      }
                    std::string to_output = tokens[4];
                    std::string from_symbol = tokens[5];
                    std::string to_symbol = tokens[6];
                    std::string model_output = tokens[7];
                    double precision;
                    try
                      {
                        precision = boost::lexical_cast<double>(tokens[8]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail [f8]: " << tokens[8] << '\n';
                        throw;
                      }

                    test_map::key_type k(from_symbol, to_symbol);
                    test_op v = { from_value, to_value, to_output, from_symbol, to_symbol, model_output, std::pow(10.0, precision) };

                    test_map::iterator i = ret.find(k);
                    if (i != ret.end())
                      {
                        i->second.push_back(v);
                      }
                    else
                      {
                        test_map::mapped_type vec;
                        vec.push_back(v);
                        ret.insert(test_map::value_type(k, vec));
                      }
                  }
                catch (const std::exception& e)
                  {
                    std::cerr << "Bad line " << line << ": " << e.what() << '\n';
                  }
              }
            else
              {
                std::cerr << "Bad line with "
                          << tokens.size() << " tokens: "
                          << line << '\n';
              }

          }
      }

    std::cerr << "Created " << ret.size() << " unique test sets\n";

    return ret;
  }

}

test_map test_data(read_test_data());
