//
// TestC2.cpp
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
#include "JVMLinkClient.h"
#include <ctime> // for time()
#include <cstdlib> // for srand() and rand()

// Tests the JVMLink API.
int _tmain(int argc, _TCHAR* argv[])
{
	JVMLinkClient *p = new JVMLinkClient();
	p->startJava(20345, "jvmlink.jar");
	while (p->establishConnection() != JVMLinkClient::CONNECTION_SUCCESS) Sleep(250);

	srand((int) time(0));

	// bool variables
	bool myBool = rand() == 0;
	p->setVar("myBool", myBool);
	std::cout << "TestC2: setVar: myBool = " << myBool << std::endl;
	JVMLinkObject* jvmBool = p->getVar("myBool");
	std::cout << "TestC2: getVar: myBool = " << jvmBool->getDataAsBool() << std::endl;

	// Byte variables
	Byte myByte;
	myByte.data = (int) (rand() % 128);
	p->setVar("myByte", myByte);
	std::cout << "TestC2: setVar: myByte = " << myByte.data << std::endl;
	JVMLinkObject* jvmByte = p->getVar("myByte");
	std::cout << "TestC2: getVar: myByte = " << jvmByte->getDataAsByte().data << std::endl;

	// char variables
	char myChar = (char) (rand() % (127 - 33 + 1)) + 33;
	p->setVar("myChar", myChar);
	std::cout << "TestC2: setVar: myChar = " << myChar << std::endl;
	JVMLinkObject* jvmChar = p->getVar("myChar");
	std::cout << "TestC2: getVar: myChar = " << jvmChar->getDataAsChar() << std::endl;

	// double variables
	double myDouble = (double) rand();
	p->setVar("myDouble", myDouble);
	std::cout << "TestC2: setVar: myDouble = " << myDouble << std::endl;
	JVMLinkObject* jvmDouble = p->getVar("myDouble");
	std::cout << "TestC2: getVar: myDouble = " << jvmDouble->getDataAsDouble() << std::endl;

	// float variables
	float myFloat = (float) rand();
	p->setVar("myFloat", myFloat);
	std::cout << "TestC2: setVar: myFloat = " << myFloat << std::endl;
	JVMLinkObject* jvmFloat = p->getVar("myFloat");
	std::cout << "TestC2: getVar: myFloat = " << jvmFloat->getDataAsFloat() << std::endl;

	// int variables
	int myInt = rand();
	p->setVar("myInt", myInt);
	std::cout << "TestC2: setVar: myInt = " << myInt << std::endl;
	JVMLinkObject* jvmInt = p->getVar("myInt");
	std::cout << "TestC2: getVar: myInt = " << jvmInt->getDataAsInt() << std::endl;
	//for (int i=0; i<first->length; i++) std::cout << a[i] << ", ";
	//std::cout << "Last two elements are " << data[first->length - 2] << " and " << data[first->length - 1] << std::endl;

	// long variables
	long long myLong = (long long) rand() + 0xffffffffL;
	p->setVar("myLong", myLong);
	std::cout << "TestC2: setVar: myLong = " << myLong << std::endl;
	JVMLinkObject* jvmLong = p->getVar("myLong");
	std::cout << "TestC2: getVar: myLong = " << jvmLong->getDataAsLong() << std::endl;

	// short variables
	short myShort = (short) rand();
	p->setVar("myShort", myShort);
	std::cout << "TestC2: setVar: myShort = " << myShort << std::endl;
	JVMLinkObject* jvmShort = p->getVar("myShort");
	std::cout << "TestC2: getVar: myShort = " << jvmShort->getDataAsShort() << std::endl;

	// CString variables
	CString myString = "<< The quick brown fox jumps over the lazy dog. >>";
	p->setVar("myString", myString);
	std::cout << "TestC2: setVar: myString = " << myString << std::endl;
	JVMLinkObject* jvmString = p->getVar("myString");
	std::cout << "TestC2: getVar: myString = " << jvmString->getDataAsString() << std::endl;

	// TODO - test arrays

	// exec commands
	std::cout << "TestC2: trying some exec calls" << std::endl;
	p->exec("import java.awt.BorderLayout");
	p->exec("import javax.swing.JButton");
	p->exec("import javax.swing.JFrame");
	p->exec("import javax.swing.JPanel");
	p->exec("frame = new JFrame(\"My Java Frame\")");
	p->exec("pane = new JPanel()");
	p->exec("frame.setContentPane(pane)");
	p->exec("layout = new BorderLayout()");
	p->exec("pane.setLayout(layout)");
	p->exec("button = new JButton(\"Hello world!\")");
	p->exec("pane.add(button, BorderLayout.CENTER)");
	p->exec("frame.setBounds(100, 100, 500, 400)");
	p->exec("frame.setVisible(true)");

	fprintf( stdout, "\n\nPress enter to shut down the server and exit...\n");
	_fgetchar();

	// free Java resources
	p->exec("frame.dispose()");

	// order the server to shut down
	p->shutJava();

	// close our connection to the server
	p->closeConnection();

	// free local resources
	delete(p);

	return 0;
}
