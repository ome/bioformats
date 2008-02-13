#pragma once
#include "JVMLinkObject.h"

class JVMLinkClient
{
private:
	int port;
	SOCKET conn;
public:
	JVMLinkClient(void);
	void startJava(int, CString);
	void shutJava();
	int establishConnection();
	int closeConnection();
	int sendMessage(CString);
	int sendMessage(int);
	CString readMessage();
	void* readMessage(int);
	JVMLinkObject* getVar(CString);
	void setVar(JVMLinkObject*);
	void setVar(CString, int);
	void setVar(CString, CString);
	void setVar(CString, char);
	void setVar(CString, Byte);
	void setVar(CString, float);
	void setVar(CString, bool);
	void setVar(CString, double);
	void setVar(CString, long);
	void setVar(CString, short);
	void exec(CString);

	~JVMLinkClient(void);
};

