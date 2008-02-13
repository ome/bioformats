package loci.jvmlink;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.Charset;

import loci.formats.ReflectException;
import loci.formats.ReflectedUniverse;
import loci.formats.DataTools;

//TODO: Communicating exceptions ..

public class ConnThread extends Thread implements Runnable
{
	public static final int MAX_PACKET_SIZE = 65536;
	
	public static final int ARRAY_TYPE = 0;
	public static final int INT_TYPE = 1;
	public static final int STRING_TYPE = 2;
	public static final int BYTE_TYPE = 3;
	public static final int CHAR_TYPE = 4;
	public static final int FLOAT_TYPE = 5;
	public static final int BOOLEAN_TYPE = 6;
	public static final int DOUBLE_TYPE = 7;
	public static final int LONG_TYPE = 8;
	public static final int SHORT_TYPE = 9;

	public static final int SETVAR = 0;
	public static final int GETVAR = 1;
	public static final int EXEC = 2;
	public static final int EXIT = 255;

	Socket server;
    int number;
    private ReflectedUniverse r;
    JVMLinkServer listener;
    
    ConnThread(Socket server, int threadNumber, JVMLinkServer listener) throws Exception
    {
    	number = threadNumber;
    	this.server = server;
    	this.listener = listener;
    	r = new ReflectedUniverse();
    	this.start();
    }
    
    public void run(){
    	try {
    		handleConnection(server);
    	}
    	catch (Exception e) {
    		System.out.println("Exception: Error handling the connection");
    		System.out.println(e.getMessage());
    	}
    }

    public void handleConnection(Socket server) throws IOException, ReflectException {
    	DataInputStream inStream = new DataInputStream(new BufferedInputStream(server.getInputStream()));
    	DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
    	
    	while (true) {
    		int command = DataTools.read4SignedBytes(inStream, true);
    		System.out.println("received command "+command);
    		if (command == EXIT) break;
    		processCommand(command, inStream, outStream);
    	}

    	System.out.println("Thread no."+number+" exiting..");
    	listener.shutServer();
    	server.close();
    }

    /* New syntax
     * 
     * First thing on the stream is an integer:
     *  255 - exit Server
     *  0   - setVar
     *  1   - getVar
     *  2   - exec
     *  
     * Then, a newline-delimited string, specifying:
     *   setVar, getVar : identifier in question
     *   exec : command to be executed
	 *
     * Then, according to which branch:
     *  setVar:
     *   0 - array (followed by another integer specifying one of the following types, 
     *    then an integer specifying length of array
     *   1-9 - int, string, byte, char, float, bool, double, long, short (in order)
     *   
     *  getVar (sends):
     *   0 - array (followed by another integer specifying one of the following types, then length
     *   1-9 - int, string, byte, char, float, bool, double, long, short (in order)
     *   
     *   Then 
     *    size - number of bytes (per item).
     *     In case of strings (not strings in arrays though) length of string.
     *     
     *  Then, for setVar, the data.
     *  
     */

    private void processCommand(int command, DataInputStream inStream, DataOutputStream out) throws IOException {
		String name = inStream.readLine();
		int type;
    	switch(command) {
    	case SETVAR:
    		type = DataTools.read4SignedBytes(inStream, true);
    		if (type == ARRAY_TYPE) { 
    			int insideType = DataTools.read4SignedBytes(inStream, true);
    			int arrayLength = DataTools.read4SignedBytes(inStream, true);
    			int size = DataTools.read4SignedBytes(inStream, true);
    			System.out.println("in array type for variable "+name+" insidetype, length, size:"+insideType +","+arrayLength+","+size);
    			Object theArray = null;
    			if (insideType == INT_TYPE) {
    				int[] intArray = new int[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize/4; i++) {
        					intArray[i + (readBytes/4)] = DataTools.bytesToInt(b, 4*i, true);
        				}
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = intArray;
        		}
    			else if (insideType == STRING_TYPE) {
    				String[] stringArray = new String[arrayLength];
        			for (int i=0; i<arrayLength; i++) {
        				stringArray[i] = inStream.readLine();
        			}
        			theArray = stringArray;
        		}
    			else if (insideType == BYTE_TYPE) {
    				byte[] byteArray = new byte[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
    		    		inStream.readFully(byteArray, readBytes, packetSize);
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = byteArray;
        		}
    			else if (insideType == CHAR_TYPE) {
    				char[] charArray = new char[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize; i++) {
        					charArray[i + readBytes] = (char) ((0x00 << 8) | (b[i] & 0xff));
        				}
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = charArray;
        			//System.out.println("recvd char array is "+  new String(charArray));
        		}
    			else if (insideType == FLOAT_TYPE) {
    				float[] floatArray = new float[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize/4; i++) {
        					floatArray[i + (readBytes/4)] = Float.intBitsToFloat(DataTools.bytesToInt(b, 4*i, true));
        				}
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = floatArray;
        		}
    			else if (insideType == BOOLEAN_TYPE) {
    				boolean[] boolArray = new boolean[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize; i++) {
        					boolArray[i + readBytes] = (b[i] == 0) ? Boolean.FALSE : Boolean.TRUE;
        				}
    		    		readBytes += packetSize;
    		    	}
        			theArray = boolArray;
    			}
    			else if (insideType == DOUBLE_TYPE) {
    				double[] doubleArray = new double[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize/8; i++) {
        					doubleArray[i + (readBytes/8)] = Double.longBitsToDouble(DataTools.bytesToLong(b, 8*i, true));
        				}
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = doubleArray;
        		}
    			else if (insideType == LONG_TYPE) {
    				long[] longArray = new long[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize/8; i++) {
        					longArray[i + (readBytes/8)] = DataTools.bytesToLong(b, 8*i, true);
        				}
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = longArray;
        		}
    			else if (insideType == SHORT_TYPE) {
    				short[] shortArray = new short[arrayLength];
    		    	int readBytes = 0, totalBytes = size*arrayLength;
    		    	while (readBytes < totalBytes) {
    		    		int packetSize = MAX_PACKET_SIZE;
    		    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
        		    	byte[] b = new byte[packetSize];
    		    		inStream.readFully(b, 0, packetSize);
    		    		for (int i=0; i<packetSize/4; i++) {
        					shortArray[i + (readBytes/4)] = DataTools.bytesToShort(b, 4*i, true);
        				}
    		    		readBytes += packetSize;
    		    	}
    		    	theArray = shortArray;
        		}
    			r.setVar(name, theArray);
    		}
    		else if (type == INT_TYPE) {
    			int value = DataTools.read4SignedBytes(inStream, true);
    			System.out.println("just setvarred "+name+" as "+value);
    			r.setVar(name, value);
    		}
    		else if (type == STRING_TYPE) {
    			r.setVar(name, inStream.readLine());
    		}
    		else if (type == BYTE_TYPE) {
				r.setVar(name, inStream.readByte());
    		}
    		else if (type == CHAR_TYPE) {
    			System.out.println("In char type ..");
    			byte readByte = inStream.readByte();
    			char readChar = (char) ((0x00 << 8) | (readByte & 0xff));
    			System.out.println("Read "+readChar);
				r.setVar(name, readChar);
    		}
    		else if (type == FLOAT_TYPE) {
				r.setVar(name, inStream.readFloat());
    		}
    		else if (type == BOOLEAN_TYPE) {
				r.setVar(name, inStream.readBoolean());
    		}
    		else if (type == DOUBLE_TYPE) {
				r.setVar(name, inStream.readDouble());
    		}
    		else if (type == LONG_TYPE) {
				r.setVar(name, inStream.readLong());
    		}
    		else if (type == SHORT_TYPE) {
				r.setVar(name, inStream.readShort());
    		}
    		break;
    	case GETVAR:
    		int insideType = 0;
    		type=0;
    		Object var = null;
    		try {
    			var = r.getVar(name);
    			if (var instanceof int[]) {type = ARRAY_TYPE; insideType = INT_TYPE;}
    			else if (var instanceof String[]) {type = ARRAY_TYPE; insideType = STRING_TYPE;}
    			else if (var instanceof byte[]) {type = ARRAY_TYPE; insideType = BYTE_TYPE;}
    			else if (var instanceof char[]) {type = ARRAY_TYPE; insideType = CHAR_TYPE;} 
    			else if (var instanceof float[]) {type = ARRAY_TYPE; insideType = FLOAT_TYPE;}
    			else if (var instanceof boolean[]) {type = ARRAY_TYPE; insideType = BOOLEAN_TYPE;}
    			else if (var instanceof double[]) {type = ARRAY_TYPE; insideType = DOUBLE_TYPE;}
    			else if (var instanceof long[]) {type = ARRAY_TYPE; insideType = LONG_TYPE;}
    			else if (var instanceof short[]) {type = ARRAY_TYPE; insideType = SHORT_TYPE;}

    			else if (var instanceof Integer) type = INT_TYPE;
    			else if (var instanceof String) type = STRING_TYPE;
    			else if (var instanceof Byte) type = BYTE_TYPE;
    			else if (var instanceof Character) type = CHAR_TYPE;
    			else if (var instanceof Float) type = FLOAT_TYPE;
    			else if (var instanceof Boolean) type = BOOLEAN_TYPE;
    			else if (var instanceof Double) type = DOUBLE_TYPE;
    			else if (var instanceof Long) type = LONG_TYPE;
    			else if (var instanceof Short) type = SHORT_TYPE;
    			else type = INT_TYPE; //default
    		} catch (ReflectException e) {
    			System.out.println("Exception thrown while retrieving variable: "+e.getMessage());
    			e.printStackTrace();
    		}

    		out.writeInt(DataTools.swap(type)); out.flush();
    		//TODO: still need to swap most of what is sent.
    		if (type == ARRAY_TYPE) {
    			Object theArray = var;
    			out.writeInt(DataTools.swap(insideType)); out.flush();
    			int arrayLen = Array.getLength(theArray);
    			out.writeInt(DataTools.swap(arrayLen)); out.flush();
    			if (insideType == INT_TYPE) {
    				out.writeInt(DataTools.swap(4)); out.flush();
    				int[] intArray = (int[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeInt(DataTools.swap(intArray[i]));
    				if (arrayLen > 10000) {
    					System.out.println("Last two elements are "+intArray[arrayLen-1]+" and "+intArray[arrayLen-2]);
    				}
    			}
    			else if (insideType == STRING_TYPE) {
    				//untested. probably quite messed up.
    				out.writeInt(DataTools.swap(4)); out.flush();
    				String[] sArray = (String[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.write(sArray[i].getBytes(Charset.forName("UTF-16")));
    			}
    			else if (insideType == BYTE_TYPE) {
    				out.writeInt(DataTools.swap(1)); out.flush();
    				byte[] bArray = (byte[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeByte(bArray[i]);
    			}
    			else if (insideType == CHAR_TYPE) {
    				//need to fix this to send only one byte.
    				out.writeInt(DataTools.swap(1)); out.flush();
    				char[] cArray = (char[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeByte((byte)cArray[i]);
    				//for (int i=0; i<arrayLen; i++) out.writeChar(DataTools.swap(cArray[i]));
    			}
    			else if (insideType == FLOAT_TYPE) {
    				out.writeInt(DataTools.swap(4)); out.flush();
    				float[] fArray = (float[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeInt(DataTools.swap(Float.floatToIntBits(fArray[i])));
    			}
    			else if (insideType == BOOLEAN_TYPE) {
    				out.writeInt(DataTools.swap(1)); out.flush();
    				boolean[] bArray = (boolean[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeBoolean(bArray[i]);
    			}
    			else if (insideType == DOUBLE_TYPE) {
    				out.writeInt(DataTools.swap(8)); out.flush();
    				double[] dArray = (double[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeLong(DataTools.swap(Double.doubleToLongBits(dArray[i])));
    			}
    			else if (insideType == LONG_TYPE) {
    				out.writeInt(DataTools.swap(8)); out.flush();
    				long[] lArray = (long[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeLong(DataTools.swap(lArray[i]));
    			}
    			else if (insideType == SHORT_TYPE) {
    				out.writeInt(DataTools.swap(2)); out.flush();
    				short[] sArray = (short[]) theArray;
    				for (int i=0; i<arrayLen; i++) out.writeShort(DataTools.swap(sArray[i]));
    			}
    			out.flush();
    		}
    		else if (type == INT_TYPE) {
    			out.writeInt(DataTools.swap(4));
    			out.writeInt(DataTools.swap(((Integer)var).intValue()));
    		}
    		else if (type == STRING_TYPE) {
    			out.writeInt(DataTools.swap(((String)var).length()));
    			System.out.println("Number of bytes="+((String)var).length());
    			out.writeBytes((String)var);
    			//out.write(((String)r.getVar(name)).getBytes(Charset.forName("UTF-16")));
    			//System.out.println("Returning string: "+((String)r.getVar(name)).getBytes(Charset.forName("UTF-16")));
    		}
    		else if (type == BYTE_TYPE) {
    			out.writeInt(DataTools.swap(1)); out.flush();
    			out.writeByte(((Byte)var).byteValue());
    		}
    		else if (type == CHAR_TYPE) {
    			//fix this to write one byte only
    			out.writeInt(DataTools.swap(2)); out.flush();
    			out.writeChar(DataTools.swap(((Character)var).charValue()));
    		}
    		else if (type == FLOAT_TYPE) {
    			out.writeInt(DataTools.swap(4)); out.flush();
    			float floatVal = ((Float)var).floatValue();
    			out.writeInt(DataTools.swap(Float.floatToIntBits(floatVal)));
    		}
    		else if (type == BOOLEAN_TYPE) {
    			out.writeInt(DataTools.swap(1)); out.flush();
    			out.writeBoolean(((Boolean)var).booleanValue());}
    		else if (type == DOUBLE_TYPE) {
    			out.writeInt(DataTools.swap(8)); out.flush();
    			double doubleVal = ((Double)var).doubleValue();
    			out.writeLong(DataTools.swap(Double.doubleToLongBits(doubleVal)));
    		}
    		else if (type == LONG_TYPE) {
    			out.writeInt(DataTools.swap(8)); out.flush();
    			out.writeLong(DataTools.swap(((Long)var).longValue()));
    		}
    		else if (type == SHORT_TYPE) {
    			out.writeInt(DataTools.swap(2)); out.flush();
    			out.writeShort(DataTools.swap(((Short)var).shortValue()));
    		}
    		out.flush();
    		break;
    	case EXEC:
    		System.out.println("exec command received: "+name);
    		try {
    			r.exec(name);
    		}
    		catch (ReflectException re) {
    			System.out.println("Exec Exception: "+re.getMessage());
    			re.printStackTrace();
    		}
    		break;
     	}
	}
    
/*    private byte[] readArray(DataInputStream inStream, int size, int arrayLength) throws IOException {
    	int readBytes = 0, totalBytes = size*arrayLength;
    	byte[] b = new byte[totalBytes];
    	while (readBytes < totalBytes) {
    		int packetSize = MAX_PACKET_SIZE;
    		if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
    		int readThisTime = inStream.read(b, readBytes, packetSize);
    		while (readThisTime < packetSize) {
        		System.out.println("in loop: didn't read fully");
    			int additional = inStream.read(b, readBytes+readThisTime, packetSize-readThisTime);
    			readThisTime += additional;
    		}
    		readBytes += packetSize;
    	}
    	return b;
    }
*/
}

/* Old code:
int[] intArray = new int[arrayLength];
int readBytes = 0, totalBytes = size*arrayLength;
while (readBytes < totalBytes) {
	int packetSize = MAX_PACKET_SIZE;
	if (readBytes+MAX_PACKET_SIZE > totalBytes) packetSize = totalBytes - readBytes;
	byte[] b = new byte[packetSize];
	int readThisTime = inStream.read(b, 0, packetSize);
	while (readThisTime < packetSize) {
		System.out.println("in loop: didn't read fully");
		int additional = inStream.read(b, readThisTime, packetSize-readThisTime);
		readThisTime += additional;
	}
	for (int i=0; i<packetSize/4; i++) {
		intArray[i + (readBytes/4)] = DataTools.bytesToInt(b, 4*i, true);
	}
	readBytes += packetSize;
}
theArray = intArray;
*/