//
// itkBioFormatsIOWin32Header.h
//

/*
OME Bio-Formats ITK plugin for calling Bio-Formats from the Insight Toolkit.
Copyright (c) 2008-@year@, UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the OME Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
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
#ifndef BioFormatsImageIO_EXPORT
#define BioFormatsImageIO_EXPORT __declspec( dllexport )
#endif
#else
#define BioFormatsImageIO_EXPORT
#endif

#endif
