//
// itkBioFormatsIOPlugin.h
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

See slicer-license.txt for Slicer3's licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms
*/

#ifndef __itkBioFormatsIOPlugin_h
#define __itkBioFormatsIOPlugin_h

#include "itkObjectFactoryBase.h"

#ifdef WIN32
#ifdef BioFormatsIOPlugin_EXPORTS
#define BioFormatsIOPlugin_EXPORT __declspec(dllexport)
#else
#define BioFormatsIOPlugin_EXPORT __declspec(dllimport)
#endif
#else
#define BioFormatsIOPlugin_EXPORT
#endif

/**
 * Routine that is called when the shared library is loaded by
 * itk::ObjectFactoryBase::LoadDynamicFactories().
 *
 * itkLoad() is C (not C++) function.
 */
extern "C" {
  BioFormatsIOPlugin_EXPORT itk::ObjectFactoryBase* itkLoad();
}
#endif
