//
// TestC2.cpp
//

/*
JVMLink package for communicating between Java and non-Java programs via IP.
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

// TestC2.cpp : Defines the entry point for the console application.

#include "stdafx.h"
#include "JVMLinkClient.h"

int _tmain(int argc, _TCHAR* argv[])
{
	JVMLinkClient *p = new JVMLinkClient();
	p->startJava(20345, "jar\\ij.jar;jar\\loci_tools.jar");
	while (p->establishConnection() != 0) Sleep(250);

	JVMLinkObject* b = new JVMLinkObject("first");
/*

	int arrayLen = 100000;
	b->size = sizeof(double);
	b->length = arrayLen;
	b->insideType = DOUBLE_TYPE;
	b->type = ARRAY_TYPE;

	double* a = new double[arrayLen];
	for (int i=0; i<arrayLen; i++) {
		a[i] = i/100.0 + 0.005;
	}

	a[arrayLen-1] = 3.78765;
	b->data = a;
*/

	int testValue= 47;
	p->setVar("first", testValue);
	JVMLinkObject* first = p->getVar("first");
	std::cout << first->name << ": type is " << first->type << ", insideType is " << first->insideType << ", length is " <<
		first->length << ", size is " << first->size << std::endl;
	int data = first->getDataAsInt();
	std::cout << "Returned value is " << data << std::endl;
	//for (int i=0; i<first->length; i++) std::cout << a[i] << ", ";
	//std::cout << "Last two elements are " << data[first->length - 2] << " and " << data[first->length - 1] << std::endl;

	//p->shutJava();
	p->closeConnection();
	delete(p);

	fprintf( stdout, "\n\nHit any key to exit...\n");
	_fgetchar();
	return 0;
}

