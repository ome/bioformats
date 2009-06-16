//
// itkBioFormatsIOWin32Header.h
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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

See slicer-license.txt for Slicer3's licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms
*/

// .NAME itkBioFormatsImageIOWin32Header - manage Windows system differences
// .SECTION Description
// The itkBioFormatsImageIOWin32Header captures some system differences
// between Unix and Windows operating systems.

#ifndef __itkBioFormatsImageIOWin32Header_h
#define __itkBioFormatsImageIOWin32Header_h

#include <itkBioFormatsImageIOConfigure.h>

#if defined(WIN32) && !defined(BIO_FORMATS_IO_STATIC)
#if defined(BIO_FORMATS_IO_EXPORTS)
#define BioFormatsImageIO_EXPORT __declspec( dllexport )
#else
#define BioFormatsImageIO_EXPORT __declspec( dllexport )
#endif
#else
#define BioFormatsImageIO_EXPORT
#endif

#endif
