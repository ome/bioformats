#include "StdAfx.h"
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
