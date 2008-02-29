//
// JVMLinkObject.h
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

#pragma once

class JVMLinkClient;

enum Command {
	BYTE_ORDER_CMD = 0,
	SETVAR_CMD = 1,
	GETVAR_CMD = 2,
	EXEC_CMD = 3,
	EXIT_CMD = 255
};

enum Type {
	ARRAY_TYPE = 0,
	INT_TYPE,
	STRING_TYPE,
	BYTE_TYPE,
	CHAR_TYPE,
	FLOAT_TYPE,
	BOOL_TYPE,
	DOUBLE_TYPE,
	LONG_TYPE,
	SHORT_TYPE,
	NULL_TYPE = -1
};

struct Byte {
	char data;
};

class JVMLinkObject {
public:
	JVMLinkObject(void);
	~JVMLinkObject(void);

	JVMLinkObject(CString);
	void* data;
	CString name;
	int size, length;
	Type type, insideType;

	int getDataAsInt();
	int* getDataAsIntArray();
	CString getDataAsString();
	CString* getDataAsStringArray();
	char getDataAsChar();
	char* getDataAsCharArray();
	Byte getDataAsByte();
	Byte* getDataAsByteArray();
	float getDataAsFloat();
	float* getDataAsFloatArray();
	bool getDataAsBool();
	bool* getDataAsBoolArray();
	double getDataAsDouble();
	double* getDataAsDoubleArray();
	long long getDataAsLong();
	long long* getDataAsLongArray();
	short getDataAsShort();
	short* getDataAsShortArray();
};
