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

#include <algorithm>
#include <functional>
#include <cmath>

#include <ome/bioformats/Modulo.h>

namespace ome
{
  namespace bioformats
  {

    Modulo::Modulo(std::string dimension):
      parentDimension(dimension),
      start(0.0),
      step(1.0),
      end(0.0),
      parentType(),
      type(),
      typeDescription(),
      unit(),
      labels()
    {
    }

    Modulo::size_type
    Modulo::size() const
    {
      if (!labels.empty())
        return labels.size();

      /**
       * @todo Use proper rounding (compat function for round(3)).
       */
      return std::max(static_cast<size_type>(floor(((end - start) / step) + 0.5) + 1.0),
                      size_type(1U));
    }

   std::string
   Modulo::toXMLAnnotation() const
   {
     std::ostringstream os;

     // NOTE: This lowercasing operation only works with 7-bit ASCII, not UTF-8.
     std::string ltype(type);
     std::transform(ltype.begin(), ltype.end(), ltype.begin(), std::ptr_fun(::tolower));

     os << "<ModuloAlong" << parentDimension
        << " Type=\"" << ltype << "\"";
     if (!typeDescription.empty())
       os << " TypeDescription=\"" << typeDescription << "\"";
     if (!unit.empty())
       os << " Unit=\"" << unit << "\"";
     if (end > start)
       {
         os << " Start=\"" << start
            << "\" Step=\"" << step
               << "\" End=\"" << end << "\"";
       }
     if (labels.size() > 0)
       {
         os << ">";
         for (std::vector<std::string>::const_iterator label = labels.begin();
              label != labels.end();
              ++label)
           {
             os << "\n<Label>" << *label << "</Label>";
           }
         os << "\n</ModuloAlong" << parentDimension << ">";
       }
     else
       {
         os << "/>";
       }

     return os.str();
   }

  }
}
