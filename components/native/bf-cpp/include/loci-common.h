//
// loci-common.h
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (c) 2008-2010, UW-Madison LOCI.
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
"BSD-style" license, it requires the Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via JaceHeaderAutogen on Jun 25, 2010 9:14:29 AM CDT
 *
 *-----------------------------------------------------------------------------
 */

#ifndef LOCI_COMMON_H
#define LOCI_COMMON_H

#include "jace.h"

#include "jace/proxy/loci/common/AbstractNIOHandle.h"
#include "jace/proxy/loci/common/BZip2Handle.h"
#include "jace/proxy/loci/common/ByteArrayHandle.h"
#include "jace/proxy/loci/common/CBZip2InputStream.h"
#include "jace/proxy/loci/common/CRC.h"
#include "jace/proxy/loci/common/DataTools.h"
#include "jace/proxy/loci/common/DateTools.h"
#include "jace/proxy/loci/common/DebugTools.h"
#include "jace/proxy/loci/common/FileHandle.h"
#include "jace/proxy/loci/common/GZipHandle.h"
#include "jace/proxy/loci/common/HandleException.h"
#include "jace/proxy/loci/common/IRandomAccess.h"
#include "jace/proxy/loci/common/IniList.h"
#include "jace/proxy/loci/common/IniParser.h"
#include "jace/proxy/loci/common/IniTable.h"
#include "jace/proxy/loci/common/Location.h"
#include "jace/proxy/loci/common/NIOByteBufferProvider.h"
#include "jace/proxy/loci/common/NIOFileHandle.h"
#include "jace/proxy/loci/common/NIOInputStream.h"
#include "jace/proxy/loci/common/RandomAccessInputStream.h"
#include "jace/proxy/loci/common/RandomAccessOutputStream.h"
#include "jace/proxy/loci/common/ReflectException.h"
#include "jace/proxy/loci/common/ReflectedUniverse.h"
#include "jace/proxy/loci/common/Region.h"
#include "jace/proxy/loci/common/StatusEvent.h"
#include "jace/proxy/loci/common/StatusListener.h"
#include "jace/proxy/loci/common/StatusReporter.h"
#include "jace/proxy/loci/common/StreamHandle.h"
#include "jace/proxy/loci/common/URLHandle.h"
#include "jace/proxy/loci/common/ZipHandle.h"
//using namespace jace::proxy::loci::common;

#include "jace/proxy/loci/common/enumeration/CodedEnum.h"
#include "jace/proxy/loci/common/enumeration/EnumException.h"
//using namespace jace::proxy::loci::common::enumeration;

#include "jace/proxy/loci/common/services/AbstractService.h"
#include "jace/proxy/loci/common/services/DependencyException.h"
#include "jace/proxy/loci/common/services/OMENotesService.h"
#include "jace/proxy/loci/common/services/Service.h"
#include "jace/proxy/loci/common/services/ServiceException.h"
#include "jace/proxy/loci/common/services/ServiceFactory.h"
//using namespace jace::proxy::loci::common::services;

#include "jace/proxy/loci/common/xml/MetadataHandler.h"
#include "jace/proxy/loci/common/xml/ValidationErrorHandler.h"
#include "jace/proxy/loci/common/xml/ValidationSAXHandler.h"
#include "jace/proxy/loci/common/xml/XMLTools.h"
//using namespace jace::proxy::loci::common::xml;

#endif
