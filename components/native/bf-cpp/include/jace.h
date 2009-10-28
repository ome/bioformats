//
// jace.h
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (C) 2008-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

#ifndef JACE_H
#define JACE_H

#include "jace/JNIHelper.h"

#include "jace/JArray.h"
#include "jace/JNIException.h"
#include "jace/OptionList.h"
#include "jace/StaticVmLoader.h"
//using namespace jace;

#include "jace/proxy/types/JBoolean.h"
#include "jace/proxy/types/JByte.h"
#include "jace/proxy/types/JChar.h"
#include "jace/proxy/types/JDouble.h"
#include "jace/proxy/types/JFloat.h"
#include "jace/proxy/types/JInt.h"
#include "jace/proxy/types/JLong.h"
#include "jace/proxy/types/JShort.h"
#include "jace/proxy/types/JVoid.h"
//using namespace jace::proxy::types;

typedef jace::JArray<jace::proxy::types::JBoolean> BooleanArray;
typedef jace::JArray<BooleanArray> BooleanArray2D;
typedef jace::JArray<BooleanArray2D> BooleanArray3D;
typedef jace::JArray<BooleanArray3D> BooleanArray4D;
typedef jace::JArray<jace::proxy::types::JByte> ByteArray;
typedef jace::JArray<ByteArray> ByteArray2D;
typedef jace::JArray<ByteArray2D> ByteArray3D;
typedef jace::JArray<ByteArray3D> ByteArray4D;
typedef jace::JArray<jace::proxy::types::JChar> CharArray;
typedef jace::JArray<CharArray> CharArray2D;
typedef jace::JArray<CharArray2D> CharArray3D;
typedef jace::JArray<CharArray3D> CharArray4D;
typedef jace::JArray<jace::proxy::types::JDouble> DoubleArray;
typedef jace::JArray<DoubleArray> DoubleArray2D;
typedef jace::JArray<DoubleArray2D> DoubleArray3D;
typedef jace::JArray<DoubleArray3D> DoubleArray4D;
typedef jace::JArray<jace::proxy::types::JFloat> FloatArray;
typedef jace::JArray<FloatArray> FloatArray2D;
typedef jace::JArray<FloatArray2D> FloatArray3D;
typedef jace::JArray<FloatArray3D> FloatArray4D;
typedef jace::JArray<jace::proxy::types::JInt> IntArray;
typedef jace::JArray<IntArray> IntArray2D;
typedef jace::JArray<IntArray2D> IntArray3D;
typedef jace::JArray<IntArray3D> IntArray4D;
typedef jace::JArray<jace::proxy::types::JLong> LongArray;
typedef jace::JArray<LongArray> LongArray2D;
typedef jace::JArray<LongArray2D> LongArray3D;
typedef jace::JArray<LongArray3D> LongArray4D;
typedef jace::JArray<jace::proxy::types::JShort> ShortArray;
typedef jace::JArray<ShortArray> ShortArray2D;
typedef jace::JArray<ShortArray2D> ShortArray3D;
typedef jace::JArray<ShortArray3D> ShortArray4D;

#include "jace/proxy/java/lang/Boolean.h"
#include "jace/proxy/java/lang/Byte.h"
#include "jace/proxy/java/lang/Character.h"
#include "jace/proxy/java/lang/Double.h"
#include "jace/proxy/java/lang/Float.h"
#include "jace/proxy/java/lang/Integer.h"
#include "jace/proxy/java/lang/Long.h"
#include "jace/proxy/java/lang/Short.h"
#include "jace/proxy/java/lang/String.h"
//using namespace jace::proxy::java::lang;

typedef jace::JArray<jace::proxy::java::lang::String> StringArray;
typedef jace::JArray<StringArray> StringArray2D;
typedef jace::JArray<StringArray2D> StringArray3D;
typedef jace::JArray<StringArray3D> StringArray4D;

#include "jace/proxy/java/io/IOException.h"
//using namespace jace::proxy::java::io;

#include "jace/proxy/java/util/Hashtable.h"
//using namespace jace::proxy::java::util;

#endif
