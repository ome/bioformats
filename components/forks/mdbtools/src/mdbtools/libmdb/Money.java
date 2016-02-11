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

/*
 * these routines are copied from the freetds project which does something
 * very similiar
 */
public class Money
{
  private static int MAXPRECISION = 20;

  public static String mdb_money_to_string(MdbHandle mdb, int start)
  {
    String s;
    int[] multiplier = new int[MAXPRECISION];
    int[] temp = new int[MAXPRECISION];
    int[] product = new int[MAXPRECISION];
    int money;
    int num_bytes = 8;
    int i;
    int pos;
    int neg=0;

    multiplier[0] = 1;

    money = start;

    if ((mdb.pg_buf[money+7] & 0x01) != 0)
    {
      /* negative number -- preform two's complement */
      neg = 1;
      for (i=0;i<num_bytes;i++)
        mdb.pg_buf[money+i] = (byte)~mdb.pg_buf[money+i];
      for (i=0; i<num_bytes; i++)
      {
        mdb.pg_buf[money+i] += 1;
        if (mdb.pg_buf[money+i]!=0)
          break;
      }
    }

    mdb.pg_buf[money+7] = 0;
    for (pos=0;pos<num_bytes;pos++)
    {
      multiply_byte(product, file.unsign(mdb.pg_buf[money+pos]), multiplier);

      System.arraycopy(multiplier,0,temp,0,MAXPRECISION);
//      memcpy(temp, multiplier, MAXPRECISION);
      for (i = 0; i < MAXPRECISION; i++)
        multiplier[i] = 0;
//      memset(multiplier,0,MAXPRECISION);
      multiply_byte(multiplier, 256, temp);
    }
    if (neg != 0)
    {
      s = "-" + array_to_string(product, 4);
    }
    else
    {
      s = array_to_string(product, 4);
    }
    return s;
  }

  private static int multiply_byte(int[] product, int num,
                                   int[] multiplier)
  {
    int[] number = new int[3];
    int i, top, j, start;

    number[0] = num%10;
    number[1] = (num/10)%10;
    number[2] = (num/100)%10;

    for (top=MAXPRECISION-1;top>=0 &&
                            multiplier[top] == 0;top--);
    start=0;
    for (i = 0;i <= top;i++)
    {
      for (j = 0;j < 3;j++)
      {
        product[j+start] += multiplier[i] * number[j];
      }
      do_carry(product);
      start++;
    }
    return 0;
  }

  private static int do_carry(int[] product)
  {
    int j;

    for (j = 0;j < MAXPRECISION;j++)
    {
      if (product[j] > 9)
      {
        product[j+1] += product[j]/10;
        product[j] = product[j]%10;
      }
    }
    return 0;
  }

  static String array_to_string(int[] array, int scale)
  {
    int top, i, j;

    for (top = MAXPRECISION-1; top >= 0 &&
                               top>scale &&
                               array[top] == 0;top--);

    if (top == -1)
    {
      return "0";
    }

    j = 0;
    char[] s = new char[100];  /** @todo find a better number */
    for (i = top;i >= 0;i--)
    {
      if (top+1-j == scale)
        s[j++]='.';
      s[j++] = (char)(array[i] + '0');
    }
    return new String(s,0,j);
  }
}
