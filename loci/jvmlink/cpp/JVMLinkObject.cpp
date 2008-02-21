//
// JVMLinkObject.cpp
//

/*
JVMLink client/server architecture for communicating between Java and
non-Java programs using sockets.
Copyright (c) 2008 Hidayath Ansari and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#include "stdafx.h"
#include "JVMLinkObject.h"

JVMLinkObject::JVMLinkObject(void)
{
}

JVMLinkObject::~JVMLinkObject(void)
{
}

JVMLinkObject::JVMLinkObject(CString argname) {
	length = 0;
	name = argname;
}

int JVMLinkObject::getDataAsInt() {
	int retval = (int) *(void**) data;
	return retval;
}

int* JVMLinkObject::getDataAsIntArray() {
	int* retval = (int*) data;
	return retval;
}

CString JVMLinkObject::getDataAsString() {
	CString* retval = new CString((char*)data);
	return *retval;
}

Byte JVMLinkObject::getDataAsByte() {
	Byte retval;
	retval.data = (char) *(void**) data;
	return retval;
}

Byte* JVMLinkObject::getDataAsByteArray() {
	Byte* retval = (Byte*) data;
	return retval;
}

char JVMLinkObject::getDataAsChar() {
	char retval = (char) *(void**) data;
	return retval;
}

char* JVMLinkObject::getDataAsCharArray() {
	char* retval = (char*) data;
	return retval;
}

float JVMLinkObject::getDataAsFloat() {
	float retval = (float) (int) *(void**) data;
	return retval;
}

float* JVMLinkObject::getDataAsFloatArray() {
	float* retval = (float*) data;
	return retval;
}

bool JVMLinkObject::getDataAsBool() {
	bool retval = (bool) *(void**) data;
	return retval;
}

bool* JVMLinkObject::getDataAsBoolArray() {
	bool* retval = (bool*) data;
	return retval;
}

double JVMLinkObject::getDataAsDouble() {
	double retval = (double) (long) *(void**) data;
	return retval;
}

double* JVMLinkObject::getDataAsDoubleArray() {
	double* retval = (double*) data;
	return retval;
}

long JVMLinkObject::getDataAsLong() {
	long retval = (long) *(void**) data;
	return retval;
}

long* JVMLinkObject::getDataAsLongArray() {
	long* retval = (long*) data;
	return retval;
}

short JVMLinkObject::getDataAsShort() {
	short retval = (short) *(void**) data;
	return retval;
}

short* JVMLinkObject::getDataAsShortArray() {
	short* retval = (short*) data;
	return retval;
}
