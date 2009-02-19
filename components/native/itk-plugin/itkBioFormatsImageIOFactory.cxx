//
// itkBioFormatsImageIOFactory.cxx
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*
Adapted from the Slicer3 project: http://www.slicer.org/
http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/

See slicer-license.txt for licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms
*/

#include "itkBioFormatsImageIOFactory.h"
#include "itkCreateObjectFunction.h"
#include "itkBioFormatsImageIO.h"
#include "itkVersion.h"

namespace itk
{

  BioFormatsImageIOFactory::BioFormatsImageIOFactory()
  {
    this->RegisterOverride("itkImageIOBase",
      "itkBioFormatsImageIO",
      "BioFormats Image IO",
      1,
      CreateObjectFunction<BioFormatsImageIO>::New());
  }

  BioFormatsImageIOFactory::~BioFormatsImageIOFactory()
  {
  }

  const char*
  BioFormatsImageIOFactory::GetITKSourceVersion(void) const
  {
    return ITK_SOURCE_VERSION;
  }

  const char*
  BioFormatsImageIOFactory::GetDescription() const
  {
    return "Bio-Formats ImageIO Factory, allows the loading of Bio-Formats-compatible images into Insight; see http://www.loci.wisc.edu/ome/formats.html";
  }

} // end namespace itk
