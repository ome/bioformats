package mdbtools.jdbc.sqlparser;

/**
 * Token defines what valid tokens are for the paser
 *
 */
class Token
{
  public String image;  // the actual token as a string
  public int type;      // the type of token

  // types
  public static final int UNKNOWN = -1;

  static void copy(Token src, Token dest)
  {
    dest.image = src.image;
    dest.type = src.type;
  }

  // the first match that works in used
  public static final char[] SYMBOLS = new char[]
  {
    // special characters
    ' ', // space
    ',', // comma
    '*', // star
    '.', // period
    '(', // open parenthese
    ')', // close parenthese
    '=', // equals
    '<', // less than
    '>', // greater than
    '{', // open squigly
    '}', // close squigly
    '-', // minus
    ':', // colon
    '?'  // question
  };

  public static final String[] KEYWORDS = new String[]
  {
    // keywords
    "SELECT",
    "FROM",
    "WHERE",
    "INNER",
    "JOIN",
    "ON",
    "<>",
    ">=",
    "<=",
    "AND",
    "OR",
    "TS",
    "ORDER",
    "BY",
    "ASC",
    "DESC"
  };

  // these should be in the same order as the above
  public static final int SPACE = 0;
  public static final int COMMA = 1;
  public static final int STAR = 2;
  public static final int PERIOD = 3;
  public static final int OPEN_PAREN = 4;
  public static final int CLOSE_PAREN = 5;
  public static final int EQUALS = 6;
  public static final int LESS_THAN = 7;
  public static final int GREATER_THAN = 8;
  public static final int OPEN_SQUIGLY = 9;
  public static final int CLOSE_SQUIGLY = 10;
  public static final int MINUS = 11;
  public static final int COLON = 12;
  public static final int QUESTION_MARK = 13;
  public static final int SELECT = 14;
  public static final int FROM = 15;
  public static final int WHERE = 16;
  public static final int INNER = 17;
  public static final int JOIN = 18;
  public static final int ON = 19;
  public static final int NOT_EQUALS = 20;
  public static final int GREATER_THAN_OR_EQUAL = 21;
  public static final int LESS_THAN_OR_EQUAL = 22;
  public static final int AND = 23;
  public static final int OR = 24;
  public static final int TS = 25;
  public static final int ORDER = 26;
  public static final int BY = 27;
  public static final int ASC = 28;
  public static final int DESC = 29;
  public static final int STRING = 30;
  public static final int INTEGER = 31;
  public static final int DOUBLE = 32;
  public static final int EOF = 33;
  public static final int QUOTED_STRING = 34;

  public static final String[] tokenNames = new String[]
    {
      "space",
      "comma",
      "star",
      "period",
      "open_paren",
      "close_paren",
      "equals",
      "less_than",
      "greater_than",
      "open_squigly",
      "close_squigly",
      "minus",
      "colon",
      "select",
      "from",
      "where",
      "inner",
      "join",
      "on",
      "not_equals",
      "greater_than_or_equal",
      "less_than_or_equal",
      "and",
      "or",
      "ts",
      "order",
      "by",
      "asc",
      "desc",
      "string",
      "integer",
      "double",
      "eof",
      "quoted_string"
    };
}

