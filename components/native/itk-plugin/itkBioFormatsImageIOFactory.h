//
// itkBioFormatsImageIOFactory.h
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

#ifndef H_ITK_BIO_FORMATS_IMAGE_IO_FACTORY_H
#define H_ITK_BIO_FORMATS_IMAGE_IO_FACTORY_H

#include "itkObjectFactoryBase.h"
#include "itkImageIOBase.h"

#include "itkBioFormatsIOWin32Header.h"

namespace itk
{

  class BioFormatsImageIO_EXPORT BioFormatsImageIOFactory : public ObjectFactoryBase
  {
  public:
    /** Standard class typedefs **/
    typedef BioFormatsImageIOFactory  Self;
    typedef ObjectFactoryBase         Superclass;
    typedef SmartPointer<Self>        Pointer;
    typedef SmartPointer<const Self>  ConstPointer;

    /** Class methods used to interface with the registered factories **/
    virtual const char* GetITKSourceVersion(void) const;
    virtual const char* GetDescription(void) const;

    /** Method for class instantiation **/
    itkFactorylessNewMacro(Self);

    /** RTTI (and related methods) **/
    itkTypeMacro(BioFormatsImageIOFactory, ObjectFactoryBase);

    /** Register one factory of this type **/
    static void RegisterOneFactory(void)
    {
      BioFormatsImageIOFactory::Pointer BioFormatsFactory =
        BioFormatsImageIOFactory::New();
      ObjectFactoryBase::RegisterFactory(BioFormatsFactory);
    }

  protected:
    BioFormatsImageIOFactory();
    ~BioFormatsImageIOFactory();

  private:
    BioFormatsImageIOFactory(const Self&); // purposely not implemented
    void operator=(const Self&); // purposely not implemented

  }; // end class BioFormatsImageIOFactory

} // end namespace itk

#endif // H_ITK_BIO_FORMATS_IMAGE_IO_FACTORY_H
