/*
 * #%L
 * OME-QTWIDGETS C++ library for display of Bio-Formats pixel data and metadata.
 * %%
 * Copyright © 2014 - 2015 Open Microscopy Environment:
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

#include <QtGui/QOpenGLContext>

#include <ome/qtwidgets/gl/Util.h>

#include <iostream>

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {

      void check_gl(std::string const& message)
      {
        GLenum err = GL_NO_ERROR;
        while ((err = glGetError()) != GL_NO_ERROR)
          {
            std::cerr << "GL error (" << message << ") :";
            switch(err)
              {
              case GL_INVALID_ENUM:
                std::cerr << "Invalid enum";
                break;
              case GL_INVALID_VALUE:
                std::cerr << "Invalid value";
                break;
              case GL_INVALID_OPERATION:
                std::cerr << "Invalid operation";
                break;
              case GL_INVALID_FRAMEBUFFER_OPERATION:
                std::cerr << "Invalid framebuffer operation";
                break;
              case GL_OUT_OF_MEMORY:
                std::cerr << "Out of memory";
                break;
              case GL_STACK_UNDERFLOW:
                std::cerr << "Stack underflow";
                break;
              case GL_STACK_OVERFLOW:
                std::cerr << "Stack overflow";
                break;
              default:
                std::cerr << "Unknown (" << err << ')';
                break;
              }
            std::cerr << std::endl;
          }
      }

    }
  }
}
