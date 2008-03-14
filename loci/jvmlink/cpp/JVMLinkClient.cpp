//
// JVMLinkClient.cpp
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
#include <Windows.h>

#define DEFAULT_PORT 20345
#define MAX_PACKET_SIZE 65536

//TODO: clear memory at appropriate points.

JVMLinkClient::JVMLinkClient(void)
{
}

JVMLinkClient::~JVMLinkClient(void)
{
}

// -- Public API methods --

void JVMLinkClient::startJava(int arg_port, CString classpath) {
	port = arg_port == NULL ? DEFAULT_PORT : arg_port;
	CString command;
	// NB: Toggle comments to control debugging output for the server.
	//command.Format("-cp %s loci.jvmlink.JVMLinkServer %d", classpath, port);
	command.Format("-cp %s loci.jvmlink.JVMLinkServer -debug %d", classpath, port);
	debug("java " << command);
	//ShellExecute(NULL, "open", "javaw.exe" , command, "", SW_SHOW);
	ShellExecute(NULL, "open", "java.exe" , command, "", SW_SHOW);
}

void JVMLinkClient::shutJava() {
	debug("Terminating JVMLink server");
	sendInt(EXIT_CMD);
}

JVMLinkClient::ConnectionCode JVMLinkClient::establishConnection() {
	WSADATA wsaData;
	struct hostent *hp;
	unsigned int addr;
	struct sockaddr_in server;
	CString servername = "127.0.0.1"; 

	int wsaret=WSAStartup(0x101,&wsaData);
	if (wsaret) return WINSOCK_ERR;
	debug("Initialized WinSock");

	conn=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
	if (conn==INVALID_SOCKET) return SOCKET_ERR;
	debug("Socket created");

	if (inet_addr(servername)==INADDR_NONE) {
		hp=gethostbyname(servername);
	}
	else {
		addr=inet_addr(servername);
		hp=gethostbyaddr((char*)&addr,sizeof(addr),AF_INET);
	}
	if (hp == NULL) {
		closesocket(conn);
		debug("Could not resolve network address: " << servername);
		return RESOLVE_ERR;
	}
	debug("Network address resolved");

	server.sin_addr.s_addr=*((unsigned long*)hp->h_addr);
	server.sin_family=AF_INET;
	server.sin_port=htons(port);
	if (connect(conn,(struct sockaddr*)&server,sizeof(server))) {
		closesocket(conn);
		debug("No server response on port " << port);
		return RESPONSE_ERR;	
	}
	debug("Connected to server: " << servername);
	return CONNECTION_SUCCESS;
}

int JVMLinkClient::closeConnection() {
	debug("Closing connection");
	shutdown(conn, SD_SEND);
	closesocket(conn);
	debug("Socket closed");
	WSACleanup();
	debug("De-initialized WinSock");
	return CONNECTION_SUCCESS;
}

JVMLinkObject* JVMLinkClient::getVar(CString name) {
	debug("getVar: requesting " << name);
	JVMLinkObject* obj = new JVMLinkObject(name);
	sendInt(GETVAR_CMD);
	sendMessage(name);
	obj->type = (Type) readInt();
	if (obj->type == ARRAY_TYPE) {
		obj->insideType = (Type) readInt();
		obj->length = (Type) readInt();
		obj->size = readInt();
		obj->data = readMessage(obj->size * obj->length);
		debug("getVar: got array: length=" << obj->length << ", type=" << obj->insideType);
	}
	else if (obj->type == STRING_TYPE) {	
		int len = readInt();
		char* buff = new char[len+1];
		int size = recv(conn,buff,len,0);
		buff[len] = '\0';
		obj->data = buff;
		obj->size = len;
		debug("getVar: got string: length=" << len << ", value=" << buff);
	}
	else {
		int size = readInt();
		obj->data = readMessage(size);
		obj->size = size;
		obj->insideType = NULL_TYPE;
		debug("getVar: got object: type=" << obj->type << ", size=" << obj->size);
	}
	return obj;
}

void JVMLinkClient::exec(CString command) {
	debug("exec: " << command);
	sendInt(EXEC_CMD);
	sendMessage(command);
}

void JVMLinkClient::setVar(JVMLinkObject* obj) {
	sendInt(SETVAR_CMD);
	sendMessage(obj->name);
	sendInt((int) obj->type);
	if (obj->type == ARRAY_TYPE) {
		sendInt((int) obj->insideType);
		sendInt(obj->length);
		if (obj->insideType == STRING_TYPE) {
			CString* s = (CString*) obj->data;
			for (int i=0; i<obj->length; i++) sendMessage(s[i]);
		}
		else {
			int sentBytes = 0;
			int totalBytes = obj->size * obj->length;
			char* dataPointer = (char*) obj->data;
			while (sentBytes < totalBytes) {
				char* buff = (char*) (dataPointer + sentBytes);
				int packetSize = MAX_PACKET_SIZE;
				if (sentBytes + MAX_PACKET_SIZE > totalBytes) {
					packetSize = totalBytes - sentBytes;
				}
				send(conn, dataPointer, packetSize, 0);
				sentBytes += packetSize;
			}
		}
	}
	else {
		if (obj->type == STRING_TYPE) sendMessage(*(CString*) obj->data);
		else send(conn, (char*) obj->data, obj->size, 0);
	}
}

void JVMLinkClient::setVar(CString argname, int obj) {
	debug("setVar: " << argname << " = " << obj << " (int)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, INT_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, int* obj, int length) {
	debug("setVar: " << argname << " (int array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, INT_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, CString obj) {
	debug("setVar: " << argname << " = " << obj << " (string)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, STRING_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, CString* obj, int length) {
	debug("setVar: " << argname << " (string array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, STRING_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, char obj) {
	debug("setVar: " << argname << " = " << obj << " (char)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, CHAR_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, char* obj, int length) {
	debug("setVar: " << argname << " (char array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, CHAR_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, Byte obj) {
	debug("setVar: " << argname << " = " << obj.data << " (byte)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, BYTE_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, Byte* obj, int length) {
	debug("setVar: " << argname << " (byte array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, BYTE_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, float obj) {
	debug("setVar: " << argname << " = " << obj << " (float)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, FLOAT_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, float* obj, int length) {
	debug("setVar: " << argname << " (float array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, FLOAT_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, bool obj) {
	debug("setVar: " << argname << " = " << obj << " (bool)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, BOOL_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, bool* obj, int length) {
	debug("setVar: " << argname << " (bool array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, BOOL_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, double obj) {
	debug("setVar: " << argname << " = " << obj << " (double)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, DOUBLE_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, double* obj, int length) {
	debug("setVar: " << argname << " (double array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, DOUBLE_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, long long obj) {
	debug("setVar: " << argname << " = " << obj << " (long)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, LONG_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, long long* obj, int length) {
	debug("setVar: " << argname << " (long array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, LONG_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, short obj) {
	debug("setVar: " << argname << " = " << obj << " (short)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, SHORT_TYPE, &obj);
	setVar(jvmObj);
	delete jvmObj;
}

void JVMLinkClient::setVar(CString argname, short* obj, int length) {
	debug("setVar: " << argname << " (short array)");
	JVMLinkObject* jvmObj = new JVMLinkObject(argname, SHORT_TYPE, length, obj);
	setVar(jvmObj);
	delete jvmObj;
}

// -- Private methods --

int JVMLinkClient::sendMessage(CString message) {
    sendInt(message.GetLength());
	return send(conn,(LPCTSTR)message,message.GetLength(),0);
}

int JVMLinkClient::sendInt(int value) {
	char* buff = (char*) (&value);
	return send(conn,buff,4,0);
}

void* JVMLinkClient::readMessage(int size) {
	char* buff = (char*) malloc(size);
	recv(conn, buff, size, 0); //TODO: check if everything is received.
	return (void*) buff;
}

int JVMLinkClient::readInt() {
	return *(int*) readMessage(4);
}