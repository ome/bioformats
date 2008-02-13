// TestC2.cpp : Defines the entry point for the console application.
//

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

