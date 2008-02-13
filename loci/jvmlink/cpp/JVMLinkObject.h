#pragma once

class JVMLinkClient;

enum Type :int {ARRAY_TYPE, INT_TYPE, STRING_TYPE, BYTE_TYPE, CHAR_TYPE, FLOAT_TYPE, BOOL_TYPE, DOUBLE_TYPE, LONG_TYPE, SHORT_TYPE};

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
	long getDataAsLong();
	long* getDataAsLongArray();
	short getDataAsShort();
	short* getDataAsShortArray();
};