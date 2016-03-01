/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools.libmdb;

import mdbtools.publicapi.RandomAccess;

import java.io.IOException;

public class file
{
  private static MdbHandle _mdb_open(RandomAccess file /*, boolean writable */)
    throws IOException
  {
    MdbHandle mdb;
    int key[] = {0x86, 0xfb, 0xec, 0x37, 0x5d, 0x44, 0x9c, 0xfa, 0xc6, 0x5e, 0x28, 0xe6, 0x13, 0xb6};
    int j,pos;
    int bufsize;
    MdbFile f;

    mdb = mem.mdb_alloc_handle();
    /* need something to bootstrap with, reassign after page 0 is read */
    mdb.fmt = MdbFormatConstants.MdbJet3Constants;
    mdb.f = mem.mdb_alloc_file();
    f = mdb.f;
//    f.filename = filename;
//    if (writable)
//    {
//      throw new RuntimeException("not ported yet");
//      f->writable = TRUE;
//      f->fd = open(f->filename,O_RDWR);
//    }
//    else
//    {
//      f.fd = new RandomAccess(f.filename);
//      int i = 1;
//      if (i == 1)
//        throw new RuntimeException("implement me");
//    }

    f.fd = file;

    f.refs++;
    if (mdb_read_pg(mdb, 0) == 0)
    {
      throw new IOException("Couldn't read first page.");
    }

    f.jet_version = mdb_get_int32(mdb, 0x14);
    if (macros.IS_JET4(mdb))
    {
      mdb.fmt = MdbFormatConstants.MdbJet4Constants;
    }
    else
    {
      mdb.fmt = MdbFormatConstants.MdbJet3Constants;
    }

    /* get the db encryption key and xor it back to clear text */
    f.db_key = mdb_get_int32(mdb, 0x3e);
    f.db_key ^= 0xe15e01b9;

    /* get the db password located at 0x42 bytes into the file */
    for (pos=0;pos<14;pos++)
    {
      j = mdb_get_int32(mdb,0x42+pos);
      j ^= key[pos];
      if (j != 0)
        f.db_passwd[pos] = (char)j;
      else
        f.db_passwd[pos] = '\0';
    }

    return mdb;
  }

  public static MdbHandle mdb_open(RandomAccess file)
    throws IOException
  {
    return _mdb_open(file);
  }

  /**
   * mdb_read a wrapper for read that bails if anything is wrong
   */
  public static long mdb_read_pg(MdbHandle mdb, long pg)
    throws IOException
  {
    long len;

    len = _mdb_read_pg(mdb, mdb.pg_buf, pg);
    /* kan - reset the cur_pos on a new page read */
    mdb.cur_pos = 0; /* kan */
    return len;
  }

  private static long _mdb_read_pg(MdbHandle mdb, byte[] pg_buf, long pg)
    throws IOException
  {
    long len;
    long offset = pg * mdb.fmt.pg_size;
//System.out.println("read page");
    if (mdb.f.fd.length() < offset)
    {
      //throw new RuntimeException("offset " + offset + " is beyond EOF");
//      return 0;
      pg = (mdb.f.fd.length() - mdb.fmt.pg_size) / mdb.fmt.pg_size;
      offset = pg * mdb.fmt.pg_size;
      if (mdb.f.fd.length() < offset) {
        throw new RuntimeException("offset " + offset + " is beyond EOF");
      }
    }

    if (mdb.stats != null && mdb.stats.collect)
      mdb.stats.pg_reads++;

    if (offset < 0) return 0;
    mdb.f.fd.seek(offset);
    len = mdb.f.fd.read(pg_buf,0,mdb.fmt.pg_size);
//    System.out.println("page was read, offset: "+offset);
//    System.out.println("[14]: " + unsign(pg_buf[14]) + ", [15]: " + unsign(pg_buf[15]));
    if (len==-1)
    {
      throw new RuntimeException("read error");
    }
    else if (len < mdb.fmt.pg_size)
    {
      throw new RuntimeException("EOF reached");
      /* fprintf(stderr,"EOF reached %d bytes returned.\n",len, mdb->fmt->pg_size); */
//      return 0;
    }
    mdb.cur_pg = (short) pg;
    return len;
  }

  public static int _mdb_get_int32(byte[] buf, int offset)
  {
    /** @todo convert this */
    /*
    int l;
    int c;

    c = buf[offset];
    l =c[3]; l<<=8;
    l+=c[2]; l<<=8;
    l+=c[1]; l<<=8;
    l+=c[0];

    return l;
    */
    int b1, b2, b3, b4;
    int pos = offset;

    // Get the component bytes;  b4 is most significant, b1 least.
    b1 = buf[pos++];
    b2 = buf[pos++];
    b3 = buf[pos++];
    b4 = buf[pos];

    // Bytes are signed.  Convert [-128, -1] to [128, 255].
    if (b1 < 0) { b1 += 256; }
    if (b2 < 0) { b2 += 256; }
    if (b3 < 0) { b3 += 256; }
    if (b4 < 0) { b4 += 256; }

    // Put the bytes in their proper places in an int.
    b2 <<= 8;
    b3 <<= 16;
    b4 <<= 24;

    // Return their sum.
    return (b1 + b2 + b3 + b4);
  }

  public static int mdb_get_int32(MdbHandle mdb, int offset)
  {
    int l;

    if (offset < 0 || offset + 4 > mdb.fmt.pg_size)
      return -1;

    l = _mdb_get_int32(mdb.pg_buf, offset);
    mdb.cur_pos+=4;
    return l;
  }

  private static int _mdb_get_int16(byte[] buf, int offset)
  {
    return unsign(buf[offset+1])*256+unsign(buf[offset]);
/*
    short b1 , b2;

    // Get the component bytes;
    b1 = buf[offset++];
    b2 = buf[offset];

    // Bytes are signed.  Convert [-128, -1] to [128, 255].
    if ( b1 < 0 )
      b1 += 256;
    if ( b2 < 0 )
      b2 += 256;

    // Put the bytes in their proper places in a short
    b2 <<= 8;

    // Return their sum.
    return (short) ( b1 + b2 );
 */
  }

  public static int mdb_get_int16(MdbHandle mdb, int offset)
  {
    int i;

    if (offset < 0 || offset + 2 > mdb.fmt.pg_size)
      return -1;

    i = _mdb_get_int16(mdb.pg_buf, offset);

    mdb.cur_pos+=2;
    return i;
  }

  public static long mdb_read_alt_pg(MdbHandle mdb, long pg)
    throws IOException
  {
    long len;
    len = _mdb_read_pg(mdb, mdb.alt_pg_buf, pg);
    return len;
  }
/* -- not correct - if needed re-port
  public static int mdb_get_int24_msb(MdbHandle mdb, int offset)
  {
    int l;
    byte[] c;

    if (offset < 0 || offset + 3 > mdb.fmt.pg_size)
      return -1;

    c = mdb.pg_buf;
    l =c[offset+2];
    l<<=8;
    l+=c[offset+1];
    l<<=8;
    l+=c[offset+0];

    mdb.cur_pos+=3;
    return l;
  }
*/
  public static int mdb_get_int24(MdbHandle mdb, int offset)
  {
    int l;
    byte[] c;

    if (offset < 0 || offset + 3 > mdb.fmt.pg_size)
      return -1;

    c = mdb.pg_buf;
    l = unsign(c[offset+2]);
    l <<= 8;
    l += unsign(c[offset+1]);
    l <<= 8;
    l += unsign(c[offset+0]);

    mdb.cur_pos += 3;

//System.out.println("mdb_get_int_24 [0]: " + unsign(mdb.pg_buf[offset]) +
//                                 " [1]: " + unsign(mdb.pg_buf[offset+1]) +
//                                 " [2]: " + unsign(mdb.pg_buf[offset+2]) +
//                                 " = " + l);
    return l;
  }

  public static void mdb_swap_pgbuf(MdbHandle mdb)
  {
    byte[] tmpbuf = new byte[MdbHandle.MDB_PGSIZE];

//    memcpy(tmpbuf,mdb->pg_buf, MDB_PGSIZE);
    System.arraycopy(mdb.pg_buf,0,tmpbuf,0,MdbHandle.MDB_PGSIZE);

//    memcpy(mdb->pg_buf,mdb->alt_pg_buf, MDB_PGSIZE);
    System.arraycopy(mdb.alt_pg_buf,0,mdb.pg_buf,0,MdbHandle.MDB_PGSIZE);

//    memcpy(mdb->alt_pg_buf,tmpbuf,MDB_PGSIZE);
    System.arraycopy(tmpbuf,0,mdb.alt_pg_buf,0,MdbHandle.MDB_PGSIZE);
  }

  public static int unsign(byte b)
  {
    int i = b;
    if ( i < 0 )
      i += 256;
    return i;
  }

  public static double mdb_get_double(MdbHandle mdb, int offset)
  {
    double d;

    if (offset <0 || offset+4 > mdb.fmt.pg_size)
      return -1;

    int currentByte = offset;
    long accum = 0;
    for ( int shiftBy = 0; shiftBy < 64; shiftBy +=8 )
    {
      // must cast to long or shift done modulo 32
      accum |= ( (long)(mdb.pg_buf[currentByte++] & 0xff)) << shiftBy;
    }
    d = Double.longBitsToDouble (accum);
    mdb.cur_pos+=8;

    return d;
  }

  // return unsigned
  public static int mdb_get_byte(MdbHandle mdb, int offset)
  {
    byte c;

    c = mdb.pg_buf[offset];
    mdb.cur_pos++;
    return unsign(c);
  }

  public static float mdb_get_single(MdbHandle mdb, int offset)
  {
    float f;

    if (offset < 0 || offset+4 > mdb.fmt.pg_size)
      return -1;

    int currentByte = offset;
    int accum = 0;
    for ( int shiftBy=0; shiftBy<32; shiftBy+=8 )
    {
      accum |= ( mdb.pg_buf[currentByte++] & 0xff ) << shiftBy;
    }
    f = Float.intBitsToFloat(accum);

    mdb.cur_pos+=4;
    return f;
  }
}
