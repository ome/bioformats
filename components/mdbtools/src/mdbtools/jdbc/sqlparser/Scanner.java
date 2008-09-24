package mdbtools.jdbc.sqlparser;

/**
 * scans the sql one character at a time creating tokens
 */
public class Scanner
{
  private String s;
  private int index;
  private Token token;  // a cached token so as to limit creations
  private boolean atEnd;

  public Scanner(String s)
  {
System.out.println("scanning: " + s);
    this.s = s;
    token = new Token();
  }

  // get the next token
  public Token next()
  {
    char c;

    if (atEnd == true)
    {
      token.type = Token.EOF;
      token.image = null;
      return token;
    }
    // scan till end of current token
    String current;
    current = scan(s);
    if (checkQuotedString(current) ||
        checkKeywords(current) ||
        checkSymbols(current) ||
        checkNumber(current) ||
        checkString(current))
    {
      if (index == s.length())
        atEnd = true;
      return token;
    }
    else
      /** @todo index is after the scan, need to report at start of scan */
      throw new RuntimeException("can't parse at: " + index);
  }

  private String scan(String s)
  {
    int i;
    char c;

    c = s.charAt(index);
    if(c == '\'')
    {
      // quoted string - scan until hit end quote
      for (i = index + 1; i < s.length(); i++)
      {
        c = s.charAt(i);
        if (c == '\'')
        {
          // check if an escape
          if ((i+1) < (s.length()-1)  && s.charAt(i+1) == '\'')
          {
            i++;
          }
          else
          {
            i++;
            break;
          }
        }
      }
    }
    else
    {
      // not quoted scan until hit non-number, non-character, non-allowed character
      for (i = index; i < s.length(); i++)
      {
        c = s.charAt(i);
        if ((c == '<' && s.charAt(i+1) == '>') ||
            (c == '>' && s.charAt(i+1) == '=') ||
            (c == '<' && s.charAt(i+1) == '=') )
        {
          i += 2;
          break;
        }
        else
        {
          if (Character.isDigit(c) == false &&
              Character.isLetter(c) == false &&
              c != '_')
            break;
        }
      }
    }
    String current;
    if (index == i)
    {
      current = s.substring(index,i+1);
      index++;
    }
    else
    {
      current = s.substring(index,i);
      index = i;
    }
    return current;
  }

  private boolean checkSymbols(String current)
  {
    if (current.length() != 1)
      return false;
    char c = current.charAt(0);
    for (int i = 0; i < Token.SYMBOLS.length; i++)
    {
      if (c == Token.SYMBOLS[i])
      {
        token.image = "" + token.SYMBOLS[i];
        token.type = i;
        return true;
      }
    }
    return false;  // not found
  }

  private boolean checkKeywords(String current)
  {
    current = current.toUpperCase();  // all keywords are uppercase
    for (int i = 0; i < Token.KEYWORDS.length; i++)
    {
      if (current.equals(Token.KEYWORDS[i]))
      {
        token.image = token.KEYWORDS[i];
        token.type = Token.SYMBOLS.length + i;
        return true;
      }
    }
    return false;  // not found
  }

  private boolean checkNumber(String current)
  {
    // check if a number
    int length = current.length();
    for (int i = 0; i < length; i++)
    {
      if (Character.isDigit(current.charAt(i)) == false)
        return false;
    }
    token.image = current;
    token.type = Token.INTEGER;
    return true;
  }

  private boolean checkString(String current)
  {
    // check if a string
    // string = letter followed by letters and numbers
    // other things allowed are: _
    char c = current.charAt(0);
    if (Character.isDigit(c) || Character.isLetter(c) || c == '_')
    {
      // verify that the rest of the string are nothing but letters and numbers
      for (int i = 1; i < current.length(); i++)
      {
        c = current.charAt(i);
        if (Character.isDigit(c) == false &&
            Character.isLetter(c) == false &&
            c != '_')
          return false; // found something other than a letter or number
      }
      token.type = Token.STRING;
      token.image = current;
      return true;
    }
    else
      return false;
  }

  private boolean checkQuotedString(String s)
  {
    if (s.charAt(0) == '\'' && s.charAt(s.length()-1) == '\'')
    {
      token.type = Token.QUOTED_STRING;
      token.image = s;
      return true;
    }
    else
      return false;
  }
}
