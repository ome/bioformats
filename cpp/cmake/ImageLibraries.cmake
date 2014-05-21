# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2014 Open Microscopy Environment:
#   - Massachusetts Institute of Technology
#   - National Institutes of Health
#   - University of Dundee
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# #L%

include(CheckCSourceCompiles)
include(FindTIFF)

if(NOT TIFF_FOUND)
  message(FATAL_ERROR "libtiff is required (tiff >= 4.0.0 from ftp://ftp.remotesensing.org/pub/libtiff/)")
endif(NOT TIFF_FOUND)

set(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})
set(CMAKE_REQUIRED_LIBRARIES ${TIFF_LIBRARIES})
check_c_source_compiles("#include <tiffio.h>

int main(void)
{
  TIFF *tiff = TIFFOpen(\"foo\", \"r\");
}
" TIFF_HAVE_OPEN)

if(NOT TIFF_HAVE_OPEN)
  message(FATAL_ERROR "libtiff does not appear to be functional (failed to include and link TIFFOpen)")
endif(NOT TIFF_HAVE_OPEN)

check_c_source_compiles("#include <tiffio.h>

int main(void)
{
  TIFF *tiff = TIFFOpen(\"foo\", \"r\");
  TIFFFieldInfo *info = TIFFFindField(tiff, TIFFTAG_IMAGEDESCRIPTION, TIFF_ANY);
  const char *name = TIFFFieldName(info);
  TIFFDataType type = TIFFFieldDataType(info);
  int pc = TIFFFieldPassCount(info);
  int rc = TIFFFieldReadCount(info);
  int wc = TIFFFieldWriteCount(info);
}
" TIFF_HAVE_FIELDINFO)

if(NOT TIFF_HAVE_FIELDINFO)
  message(WARNING "libtiff does not have TIFFFieldInfo (probably libtiff <= 4.0.2)")
endif(NOT TIFF_HAVE_FIELDINFO)

check_c_source_compiles("#include <tiffio.h>

int main(void)
{
  TIFFFieldInfo *info;
  TIFF *tiff;
  TIFFMergeFieldInfo(tiff, info, 0);
}
" TIFF_HAVE_MERGEFIELDINFO)
set(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})
