/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

#include <jni.h>
#include "loci_formats_in_LegacyND2Reader.h"
#include <stdio.h>
#include "jp2sdk.h"

#define UNICODE
#define _UNICODE

IJp2InputFile* nd2File = NULL;

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    openFile
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_loci_formats_in_LegacyND2Reader_openFile
  (JNIEnv *env, jobject obj, jstring filename)
{
  // convert filename to UTF-8
  int length = env->GetStringLength(filename);
  const jchar *str = env->GetStringChars(filename, 0);
  nd2File = CreateJp2InputFile(str);
  env->ReleaseStringChars(filename, str);
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getNumSeries
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getNumSeries
  (JNIEnv *env, jobject obj)
{
  if (nd2File == NULL) return -1;
  int numDimensions = nd2File->GetDimensionCount();
  for (int i=0; i<numDimensions; i++) {
    if (nd2File->GetSeqType(i) == nd2File->Multipoint) {
      return nd2File->GetSeqCount(i);
    }
  }
  return 1;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getWidth
  (JNIEnv *env, jobject obj, jint series)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  unsigned long int width;
  unsigned long int extra;
  nd2File->GetImageAttributes(uid, width, extra, extra, extra, extra);
  return (int) width;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getHeight
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getHeight
  (JNIEnv *env, jobject obj, jint series)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  unsigned long int height;
  unsigned long int extra;
  nd2File->GetImageAttributes(uid, extra, extra, height, extra, extra);
  return (int) height;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getZSlices
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getZSlices
  (JNIEnv *env, jobject obj, jint series)
{
  if (nd2File == NULL) return -1;
  int numDimensions = nd2File->GetDimensionCount();
  for (int i=0; i<numDimensions; i++) {
    if (nd2File->GetSeqType(i) == nd2File->ZSeries) {
      return nd2File->GetSeqCount(i);
    }
  }
  return 1;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getTFrames
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getTFrames
  (JNIEnv *env, jobject obj, jint series)
{
  if (nd2File == NULL) return -1;
  int numDimensions = nd2File->GetDimensionCount();
  for (int i=0; i<numDimensions; i++) {
    if (nd2File->GetSeqType(i) == nd2File->Timelapse) {
      return nd2File->GetSeqCount(i);
	}
  }
  return 1;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getChannels
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getChannels
  (JNIEnv *env, jobject obj, jint series)
{
  if (nd2File == NULL) return -1;
  int numDimensions = nd2File->GetDimensionCount();
  for (int i=0; i<numDimensions; i++) {
    if (nd2File->GetSeqType(i) == nd2File->Wavelength) {
      return nd2File->GetSeqCount(i);
    }
  }

  return 1;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getBytesPerPixel
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getBytesPerPixel
  (JNIEnv *env, jobject obj, jint series)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  unsigned long bytes;
  unsigned long width;
  unsigned long int extra;
  nd2File->GetImageAttributes(uid, width, bytes, extra, extra, extra);
  return bytes / width;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getImage
 * Signature: ([BIIII)[B
 */
JNIEXPORT jbyteArray JNICALL Java_loci_formats_in_LegacyND2Reader_getImage
  (JNIEnv *env, jobject obj, jbyteArray buffer, jint series, jint z, jint c,
  jint t)
{
  if (nd2File != NULL) {
    int numDimensions = nd2File->GetDimensionCount();
    unsigned long int *pos = new unsigned long int[numDimensions];
    for (int i=0; i<numDimensions; i++) {
      switch (nd2File->GetSeqType(i)) {
		case nd2File->Multipoint:
          pos[i] = series;
          break;
		case nd2File->ZSeries:
          pos[i] = z;
          break;
		case nd2File->Wavelength:
          pos[i] = c;
          break;
		case nd2File->Timelapse:
          pos[i] = t;
          break;
        default:
          pos[i] = 0;
      }
    }
    unsigned long int uid = nd2File->GetImageID(pos);
    unsigned long bytes;
    unsigned long width;
    unsigned long height;
    unsigned long channels;
	unsigned long extra;
    nd2File->GetImageAttributes(uid, width, bytes, height, channels, extra);

	bytes /= (width * channels);

	unsigned long len = width * height * channels;

    if (bytes == 1) {
      unsigned char *buf = new unsigned char[len];
      nd2File->GetImageData(uid, buf);

      jbyte *body = env->GetByteArrayElements(buffer, 0);
      for (int i=0; i<len; i++) {
        body[i] = buf[i];
      }
      env->ReleaseByteArrayElements(buffer, body, 0);
    }
    else if (bytes == 2) {
      unsigned short *buf = new unsigned short[len];
      nd2File->GetImageData(uid, buf);

	  jbyte *body = env->GetByteArrayElements(buffer, 0);
	  for (int i=0; i<len; i++) {
		body[i*2 + 1] = (buf[i] >> 8) & 0xff;
		body[i*2] = buf[i] & 0xff;
	  }
	  env->ReleaseByteArrayElements(buffer, body, 0);
	}
	else if (bytes == 4)  {
	  unsigned long *buf = new unsigned long[len];
	  nd2File->GetImageData(uid, buf);
	
	  jbyte *body = env->GetByteArrayElements(buffer, 0);
	  for (unsigned long i=0; i<len; i++) {
		body[i*4 + 3] = (buf[i] >> 24) & 0xff;
		body[i*4 + 2] = (buf[i] >> 16) & 0xff;
		body[i*4 + 1] = (buf[i] >> 8) & 0xff;
		body[i*4] = buf[i] & 0xff;
	  }
	  env->ReleaseByteArrayElements(buffer, body, 0);
	}
  }
  return buffer;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getDX
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getDX
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  double extra;
  double rtn;
  nd2File->GetImageCoordinates(uid, rtn, extra, extra);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getDY
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getDY
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  double extra;
  double rtn;
  nd2File->GetImageCoordinates(uid, extra, rtn, extra);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getDZ
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getDZ
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  double extra;
  double rtn;
  nd2File->GetImageCoordinates(uid, extra, extra, rtn);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getDT
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getDT
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  double rtn;
  nd2File->GetImageTime(uid, rtn);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getWavelength
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getWavelength
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  unsigned char extra;
  double rtn;
  nd2File->GetImageWavelength(uid, rtn, NULL, 0, extra, extra, extra);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getChannelName
 * Signature: (IIII)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_loci_formats_in_LegacyND2Reader_getChannelName
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return NULL;
  unsigned long int uid = getUID(series);
  unsigned short int *name = new unsigned short int[512];
  unsigned char extra;
  double wavelength;
  nd2File->GetImageWavelength(uid, wavelength, name, 0, extra, extra, extra);
  return env->NewString(name, 512);
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getMagnification
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getMagnification
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  double extra;
  double rtn;
  nd2File->GetImageObjectiveInfo(uid, NULL, 0, rtn, extra, extra);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getNA
 * Signature: (IIII)D
 */
JNIEXPORT jdouble JNICALL Java_loci_formats_in_LegacyND2Reader_getNA
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  double extra;
  double rtn;
  nd2File->GetImageObjectiveInfo(uid, NULL, 0, extra, rtn, extra);
  return rtn;
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getObjectiveName
 * Signature: (IIII)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_loci_formats_in_LegacyND2Reader_getObjectiveName
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return NULL;
  unsigned long int uid = getUID(series);
  unsigned short int *name = new unsigned short int[512];
  double extra;
  nd2File->GetImageObjectiveInfo(uid, name, 512, extra, extra, extra);
  return env->NewString(name, 512);
}

/*
 * Class:     loci_formats_in_LegacyND2Reader
 * Method:    getModality
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_loci_formats_in_LegacyND2Reader_getModality
  (JNIEnv *env, jobject obj, jint series, jint z, jint c, jint t)
{
  if (nd2File == NULL) return -1;
  unsigned long int uid = getUID(series);
  IJp2InputFile::eModality modality;
  nd2File->GetImageModality(uid, modality);
  return modality;
}

// -- Helper methods --

unsigned long int getUID(int series) {
  int numDimensions = nd2File->GetDimensionCount();
  unsigned long int *pos = new unsigned long int[numDimensions];
  for (int i=0; i<numDimensions; i++) {
    if (nd2File->GetSeqType(i) == nd2File->Multipoint) {
      pos[i] = series;
    }
    else pos[i] = 0;
  }
  return nd2File->GetImageID(pos);
}
