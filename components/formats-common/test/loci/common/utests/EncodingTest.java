/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import loci.common.Constants;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for string encoding.
 *
 */
public class EncodingTest {

  // -- Encodings --
  private static final String MAC = getMacEncoding();
  private static final String WINDOWS = "Windows-1252";

  // -- Test strings --
  private static final String ENGLISH_ALPHABET =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String UMLAUTS = "äëüöïÄËÖÜÏ";
  private static final String ESZETT = "ß";
  private static final String ASSORTED_LETTERS = "åÅñÑçÇøØ";
  private static final String ACUTE_ACCENTS = "áéúíóÁÉÍÓÚ";
  private static final String SUPERSCRIPTS = "¹²³";
  private static final String MATH = "+=-×÷";
  private static final String FRACTIONS = "¼½¾";
  private static final String CURRENCY = "€$¥£¢";
  private static final String PUNCTUATION = ",.?':;\"¶¿“”\\!˝¸-˘‘’`ˇ§";
  private static final String SQUARE_BRACKETS = "[]";
  private static final String CURLY_BRACKETS = "{}";
  private static final String ANGLE_BRACKETS = "<>";
  private static final String ROUND_BRACKETS = "()";
  private static final String DOUBLE_ANGLE_BRACKETS = "«»";

  static String getMacEncoding() {
    if (Charset.isSupported("MacRoman")) {
      return "MacRoman";
    }
    if (Charset.isSupported("x-MacRoman")) {
      return "x-MacRoman";
    }
    return "macintosh";
  }

  // -- Tests --

  @Test
  public void testAlphabetMac() {
    try {
      assertTrue(ENGLISH_ALPHABET.equals(
        new String(ENGLISH_ALPHABET.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAlphabetWindows() {
    try {
      assertTrue(ENGLISH_ALPHABET.equals(
        new String(ENGLISH_ALPHABET.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testUmlautsMac() {
    try {
      assertTrue(UMLAUTS.equals(
        new String(UMLAUTS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testUmlautsWindows() {
    try {
      assertTrue(UMLAUTS.equals(
        new String(UMLAUTS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testEszettLettersMac() {
    try {
      assertTrue(ESZETT.equals(
        new String(ESZETT.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testEszettLettersWindows() {
    try {
      assertTrue(ESZETT.equals(
        new String(ESZETT.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAssortedLettersMac() {
    try {
      assertTrue(ASSORTED_LETTERS.equals(
        new String(ASSORTED_LETTERS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAssortedLettersWindows() {
    try {
      assertTrue(ASSORTED_LETTERS.equals(
        new String(ASSORTED_LETTERS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAcuteAccentsMac() {
    try {
      assertTrue(ACUTE_ACCENTS.equals(
        new String(ACUTE_ACCENTS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAcuteAccentsWindows() {
    try {
      assertTrue(ACUTE_ACCENTS.equals(
        new String(ACUTE_ACCENTS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testSuperscriptsMac() {
    try {
      assertTrue(SUPERSCRIPTS.equals(
        new String(SUPERSCRIPTS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testSuperscriptsWindows() {
    try {
      assertTrue(SUPERSCRIPTS.equals(
        new String(SUPERSCRIPTS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testMathMac() {
    try {
      assertTrue(MATH.equals(
        new String(MATH.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testMathWindows() {
    try {
      assertTrue(MATH.equals(
        new String(MATH.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testFractionsMac() {
    try {
      assertTrue(FRACTIONS.equals(
        new String(FRACTIONS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testFractionsWindows() {
    try {
      assertTrue(FRACTIONS.equals(
        new String(FRACTIONS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testCurrencyMac() {
    try {
      assertTrue(CURRENCY.equals(
        new String(CURRENCY.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testCurrencyWindows() {
    try {
      assertTrue(CURRENCY.equals(
        new String(CURRENCY.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testPunctuationMac() {
    try {
      assertTrue(PUNCTUATION.equals(
        new String(PUNCTUATION.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testPunctuationWindows() {
    try {
      assertTrue(PUNCTUATION.equals(
        new String(PUNCTUATION.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testSquareBracketsMac() {
    try {
      assertTrue(SQUARE_BRACKETS.equals(
        new String(SQUARE_BRACKETS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testSquareBracketsWindows() {
    try {
      assertTrue(SQUARE_BRACKETS.equals(
        new String(SQUARE_BRACKETS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testCurlyBracketsMac() {
    try {
      assertTrue(CURLY_BRACKETS.equals(
        new String(CURLY_BRACKETS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testCurlyBracketsWindows() {
    try {
      assertTrue(CURLY_BRACKETS.equals(
        new String(CURLY_BRACKETS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAngleBracketsMac() {
    try {
      assertTrue(ANGLE_BRACKETS.equals(
        new String(ANGLE_BRACKETS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testAngleBracketsWindows() {
    try {
      assertTrue(ANGLE_BRACKETS.equals(
        new String(ANGLE_BRACKETS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testDoubleAngleBracketsMac() {
    try {
      assertTrue(DOUBLE_ANGLE_BRACKETS.equals(
        new String(DOUBLE_ANGLE_BRACKETS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testDoubleAngleBracketsWindows() {
    try {
      assertTrue(DOUBLE_ANGLE_BRACKETS.equals(new String(
        DOUBLE_ANGLE_BRACKETS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testRoundBracketsMac() {
    try {
      assertTrue(ROUND_BRACKETS.equals(
        new String(ROUND_BRACKETS.getBytes(MAC), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

  @Test
  public void testRoundBracketsWindows() {
    try {
      assertTrue(ROUND_BRACKETS.equals(
        new String(ROUND_BRACKETS.getBytes(WINDOWS), Constants.ENCODING)));
    }
    catch (UnsupportedEncodingException e) { }
  }

}
