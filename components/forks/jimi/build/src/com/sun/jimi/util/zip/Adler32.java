package com.sun.jimi.util.zip;

public class Adler32 implements Checksum
{
  private long value;
  public Adler32()
  {
    value = 1;
  }
  public long getValue()
  {
    return value;
  }
  public void reset()
  {
    value = 1;
  }
  public void update(byte[] buf, int bufoff, int len)
  {
    long s1 = value & 0xffff;
    long s2 = (value >> 16) & 0xffff;
    int k;

    if(buf == null)
      return;

    while(len > 0)
      {
	k = len < 5552? len: 5552;
	len -= k;
	while(k >= 16)
	  {
            {s1 += (int)(0xff & buf[bufoff]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 2]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 2 + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 4]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 4 + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 4 + 2]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 4 + 2 + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 2]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 2 + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 4]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 4 + 1]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 4 + 2]); s2 += s1;}
	    {s1 += (int)(0xff & buf[bufoff + 8 + 4 + 2 + 1]); s2 += s1;}

	    bufoff+=16;
	    k -= 16;
	  }
	if(k != 0)
	  do
	    {
	      s1 += (int)(0xff & buf[bufoff++]);
	      s2 += s1;
	    } while (--k != 0);
	s1 %= 65521;
	s2 %= 65521;
      }
    value = ((s2 << 16) | s1);
  }
  public void update(int b)
  {
    byte[] a = new byte[1];
    a[0] = (byte)b;
    update(a,0,1);
  }
}
