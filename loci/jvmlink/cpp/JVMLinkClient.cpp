//
// JVMLinkClient.cpp
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

#include "StdAfx.h"
#include "JVMLinkClient.h"
#include <Windows.h>

#define DEFAULT_PORT 20345
#define MAX_PACKET_SIZE 65536

//TODO: clear memory at appropriate points.

JVMLinkClient::JVMLinkClient(void)
{

}


void JVMLinkClient::startJava(int arg_port, CString serverPath, CString classpath) { //make second argument optional
	if (arg_port != NULL) port = arg_port;
	else port = DEFAULT_PORT;
	CString command;
	command.Format("-cp %s;%s Server %d", serverPath, classpath, port);
	std::cout << command << std::endl;
	//ShellExecute(NULL, "open", "java.exe" , command, "", SW_SHOW);
}

int JVMLinkClient::establishConnection() {
	WSADATA wsaData;
	struct hostent *hp;
	unsigned int addr;
	struct sockaddr_in server;
	CString servername = "127.0.0.1"; 

	int wsaret=WSAStartup(0x101,&wsaData);
	if(wsaret)	
		return 1;
	std::cout << "Initialized WinSock" << std::endl;
	
	conn=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
	if(conn==INVALID_SOCKET)
		return 2;
	std::cout << "SOCKET created" << std::endl;

	if(inet_addr(servername)==INADDR_NONE)
	{
		hp=gethostbyname(servername);
	}
	else
	{
		addr=inet_addr(servername);
		hp=gethostbyaddr((char*)&addr,sizeof(addr),AF_INET);
	}
	if(hp==NULL)
	{
		closesocket(conn);
		return 3;
	}
	std::cout << "hostname/ipaddress resolved" << std::endl;

	server.sin_addr.s_addr=*((unsigned long*)hp->h_addr);
	server.sin_family=AF_INET;
	server.sin_port=htons(JVMLinkClient::port);
	if(connect(conn,(struct sockaddr*)&server,sizeof(server)))
	{
		closesocket(conn);
		return 4;	
	}
	std::cout << "Connected to server : " << servername << std::endl;
	return 0;
}

void JVMLinkClient::shutJava() {
	sendMessage(255);
}

int JVMLinkClient::sendMessage(CString message) {
	char* buff = new char[message.GetLength()];
	sprintf_s(buff,message.GetLength()+2,message+"\n");
	send(conn,buff,strlen(buff),0);
	//delete[](buff);
	return 0;
}

int JVMLinkClient::sendMessage(int message) {
	//char* buff = new char[4];
	//memcpy(buff, &message, 4);

	char* buff = (char*) (&message);

/*	buff[0] = (message & 0xff);
	buff[1] = ((message >> 8) & 0xff);
	buff[2] = ((message >> 16) & 0xff);
	buff[3] = ((message >> 24) & 0xff); */
	send(conn,buff,4,0);
	//delete[](buff);
	return 0;
}

CString JVMLinkClient::readMessage() {
	char* buff = new char[512];
	int size = recv(conn,buff,512,0);
	CString retval;
	retval.Format("%s", buff);
	retval = retval.Left(size);
	return retval;
	delete(buff);
}

void* JVMLinkClient::readMessage(int size) {
	char* buff = (char*) malloc(size);
	recv(conn,buff,size,0); //todo: check if everything is received.
	return ((void*) buff);
}

JVMLinkObject* JVMLinkClient::getVar(CString name) {
	JVMLinkObject* obj = new JVMLinkObject(name);
	sendMessage(1);
	sendMessage(name);
	obj->type = (Type) (int) *((void**) readMessage(4));
	if (obj->type == ARRAY_TYPE) {
		obj->insideType = (Type) (int) *((void**) readMessage(4));
		obj->length = (Type) (int) *((void**) readMessage(4));
		obj->size = (int) *((void**) readMessage(4));
		obj->data = readMessage(obj->size * obj->length);
	}
	else if (obj->type == STRING_TYPE) {	
		int len = (int) *((void**) readMessage(4));
		std::cout << "length is string to be received is " << len << std::endl;
		char* buff = new char[len+1];
		int size = recv(conn,buff,len,0);
		buff[len] = '\0';
		obj->data = buff;
		obj->size = len;
		std::cout << "returning from getVar with received string " << buff << std::endl;
	}
	else {
		int size = (int) *((void**) readMessage(4));	
		obj->data = readMessage(size);
		obj->size = size;
		obj->insideType = (Type) -1;
	}
	return obj;
}

void JVMLinkClient::exec(CString command) {
	sendMessage(2);
	sendMessage(command);
}

void JVMLinkClient::setVar(JVMLinkObject* obj) {
	sendMessage(0);
	sendMessage(obj->name);
	sendMessage((int) obj->type);
	if (obj->type == ARRAY_TYPE) {
		sendMessage((int) obj->insideType);
		sendMessage(obj->length);
		sendMessage(obj->size);

		int sentBytes = 0;
		int totalBytes = (obj->size)*(obj->length);
		char* dataPointer = (char*) obj->data;
		while (sentBytes < totalBytes) {
			char* buff = (char*) (dataPointer + sentBytes);
			int packetSize = MAX_PACKET_SIZE;
			if (sentBytes + MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - sentBytes;
			send(conn,buff,packetSize,0);
			sentBytes += packetSize;
		}
	}
}

void JVMLinkClient::setVar(CString argname, int obj) {
	sendMessage(0);
	sendMessage(argname);
	sendMessage(1);
	sendMessage(obj);
}

void JVMLinkClient::setVar(CString argname, CString obj) {
	std::cout << "setVar string called with " << argname << " and " << obj << std::endl;
	sendMessage(0);
	sendMessage(argname);
	sendMessage(2);
	sendMessage(obj);
}

void JVMLinkClient::setVar(CString argname, char obj) {
	std::cout << "setVar char called with " << argname << " and " << obj << std::endl;
	sendMessage(0);
	sendMessage(argname);
	sendMessage(4);

	char buff[3];
	buff[0] = 0;
	buff[1] = obj;
	buff[2] = '\0';
	send(conn,buff,2,0);
}

void JVMLinkClient::setVar(CString argname, Byte obj) {
	std::cout << "setVar Byte called with " << argname << " and " << obj.data << std::endl;
	sendMessage(0);
	sendMessage(argname);
	sendMessage(3);

	char* buff = (char*) (&obj);
	send(conn,buff,1,0);
}

void JVMLinkClient::setVar(CString argname, float obj) {
	sendMessage(0);
	sendMessage(argname);
	sendMessage(5);

	char* buff = (char*) (&obj);
	send(conn,buff,sizeof(obj),0);
}

void JVMLinkClient::setVar(CString argname, bool obj) {
	std::cout << "setVar bool called with " << argname << " and " << obj << std::endl;
	sendMessage(0);
	sendMessage(argname);
	sendMessage(6);

	char* buff = (char*) (&obj);
	send(conn,buff,sizeof(obj),0);
}

void JVMLinkClient::setVar(CString argname, double obj) {
	sendMessage(0);
	sendMessage(argname);
	sendMessage(7);

	char* buff = (char*) (&obj);
	send(conn,buff,sizeof(obj),0);
}

void JVMLinkClient::setVar(CString argname, long obj) {
	sendMessage(0);
	sendMessage(argname);
	sendMessage(8);

	char* buff = (char*) (&obj);
	send(conn,buff,sizeof(obj),0);
}

void JVMLinkClient::setVar(CString argname, short obj) {
	sendMessage(0);
	sendMessage(argname);
	sendMessage(9);

	char* buff = (char*) (&obj);
	send(conn,buff,sizeof(obj),0);
}

int JVMLinkClient::closeConnection() {
	closesocket(conn);
	std::cout << "SOCKET closed" << std::endl;
	WSACleanup();
	std::cout << "De-Initialized WinSock" << std::endl;
	return 0;
}
JVMLinkClient::~JVMLinkClient(void)
{
}
