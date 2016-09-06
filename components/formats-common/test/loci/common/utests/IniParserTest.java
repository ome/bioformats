/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C)  2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.common.utests;

import loci.common.IniParser;
import loci.common.IniList;
import loci.common.IniTable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link loci.common.IniParser}.
 */
public class IniParserTest {
  private final IniParser parser = new IniParser();
  private final Charset utf8charset = Charset.forName("UTF-8");

  public BufferedReader stringToBufferedReader(String s) {
    InputStream stream = new ByteArrayInputStream(s.getBytes(utf8charset));
    return new BufferedReader(new InputStreamReader(stream));
  }

  @DataProvider(name = "simplekeyvalue")
  public Object[][] createSimpleKeyValuePair() {
    return new Object[][] {
      {"key=value"},
      {"key = value"},         // whitespaces around equal sign
      {"key=value  "},         // trailing whitespace
      {"  key=value"},         // leading whitespace
      {"key=value#comment"},   // comment without whitespaces
      {"key=value # comment"}, // comment with whitespaces
    };
  }
  
  @Test(dataProvider="simplekeyvalue")
  public void testSimpleKeyValue(String s) throws IOException {
    BufferedReader reader = stringToBufferedReader(s);
    
    IniTable table = new IniTable();
    table.put(IniTable.HEADER_KEY, IniTable.DEFAULT_HEADER);
    table.put("key", "value");
    IniList list = new IniList();
    list.add(table);

    assertEquals(parser.parseINI(reader), list);
  }

  @DataProvider(name = "simpleheader")
  public Object[][] createSimpleHeader() {
    return new Object[][] {
      {"key=value", IniTable.DEFAULT_HEADER},
      {"[\nkey=value", IniTable.DEFAULT_HEADER},
      {"{\nkey=value", IniTable.DEFAULT_HEADER},
      {"{chapter}\nkey=value", IniTable.DEFAULT_HEADER},
      {"{}\nkey=value", IniTable.DEFAULT_HEADER},
      {"[]\nkey=value", ""},
      {"[header]\nkey=value", "header"},
      {"[ header ]\nkey=value", " header "},
      {"[header\nkey=value", "header"},
      {"[[header]]\nkey=value", "[header]"},
      {"{chapter}\n[header]\nkey=value", "chapter: header"},
      {"{chapter\n[header\nkey=value", "chapter: header"},
    };
  }

  @Test(dataProvider="simpleheader")
  public void testHeader(String s, String header) throws IOException {
    BufferedReader reader = stringToBufferedReader(s);

    IniTable table = new IniTable();
    table.put(IniTable.HEADER_KEY, header);
    table.put("key", "value");
    IniList list = new IniList();
    list.add(table);

    assertEquals(parser.parseINI(reader), list);
  }

  @DataProvider(name = "invalidheader")
  public Object[][] createSingleString() {
    return new Object[][] {
      {"="}, {"["}, {"{"}
    };
  }

  @Test(dataProvider="invalidheader")
  public void testInvalidHeader(String s) throws IOException {
    BufferedReader reader = stringToBufferedReader(s);

    IniTable table = new IniTable();
    table.put(IniTable.HEADER_KEY, IniTable.DEFAULT_HEADER);
    IniList list = new IniList();
    list.add(table);

    assertEquals(parser.parseINI(reader), list);
  }

  public void testEmptyString() throws IOException {
    BufferedReader reader = stringToBufferedReader("");
    assertEquals(parser.parseINI(reader), new IniTable());
  }

  public void testNull() throws IOException {
    BufferedReader reader = stringToBufferedReader(null);
    assertEquals(parser.parseINI(reader), new IniTable());
  }

  @Test
  public void testMultiValueINI() throws IOException {
    String s = "key1=value1 # comment\n\n" +
      "key2=line1\\\nline2\n" +
      "ignored line\n" +
      "key3 = value3";
    BufferedReader reader = stringToBufferedReader(s);

    IniTable table = new IniTable();
    table.put(IniTable.HEADER_KEY, IniTable.DEFAULT_HEADER);
    table.put("key1", "value1");
    table.put("key2", "line1 line2");
    table.put("key3", "value3");
    IniList list = new IniList();
    list.add(table);

    assertEquals(parser.parseINI(reader), list);
  }

  @Test
  public void testMultiHeaderINI() throws IOException {
    String s = "key0=value0\n" +
      "[header1]\nkey1=value1\n" +
      "{chapter}\n[header2]\nkey2=value2\n" +
      "[header3]\nkey3=value3\n";
    BufferedReader reader = stringToBufferedReader(s);

    IniTable table0 = new IniTable();
    table0.put(IniTable.HEADER_KEY, IniTable.DEFAULT_HEADER);
    table0.put("key0", "value0");
    IniTable table1 = new IniTable();
    table1.put(IniTable.HEADER_KEY, "header1");
    table1.put("key1", "value1");
    IniTable table2 = new IniTable();
    table2.put(IniTable.HEADER_KEY, "chapter: header2");
    table2.put("key2", "value2");
    IniTable table3 = new IniTable();
    table3.put(IniTable.HEADER_KEY, "chapter: header3");
    table3.put("key3", "value3");
    IniList list = new IniList();
    list.add(table0);
    list.add(table1);
    list.add(table2);
    list.add(table3);

    assertEquals(parser.parseINI(reader), list);
  }
}
