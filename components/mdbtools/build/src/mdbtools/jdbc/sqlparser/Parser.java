package mdbtools.jdbc.sqlparser;

import java.util.ArrayList;

/**
 * Entry point for parsing sql
 * the sql is parsed into a parse tree
 */
public class Parser
{
  private static final int MAX_LOOKAHEAD = 10;
  private Token[] lookAhead = new Token[MAX_LOOKAHEAD];
  private ArrayList tokens = new ArrayList();
  private int currentToken = 0;

  public Parser(String s)
  {
    // create the lookahead
    for (int i = 0; i < lookAhead.length; i++)
      lookAhead[i] = new Token();
    // completly scan the text
    Scanner scanner;
    scanner = new Scanner(s);
    Token t;
    while ((t = scanner.next()).type != Token.EOF)
    {
      if (t.type != Token.SPACE)
      {
        Token t2 = new Token();
        t2.image = t.image;
        t2.type = t.type;
        tokens.add(t2);
      }
    }
    t = new Token();
    t.type = Token.EOF;
    tokens.add(t);
  }

  public Object parse()
  {
    Token t;
    t = getToken(Token.SELECT);
    return parseSelect();
  }

  /*
   * Format of select:
   * select column_list from table_list where expression_list
   *   order by column_list
   */
  private Select parseSelect()
  {
    Token t;
    Select select = new Select();
    // parse column list
    columnList(select.columns);
    // from
    t = getToken(Token.FROM);
    // table list
    tableList(select);
    lookAhead(1);
    if (lookAhead[0].type == Token.WHERE)
    {
      t = getToken(Token.WHERE);
      select.setWhere(whereClause());
      lookAhead(1);
    }
    else if (lookAhead[0].type == Token.ORDER)
    {
      // order by
      getToken(Token.ORDER);
      getToken(Token.BY);
      select.orderBy = new OrderBy();
      orderbyList(select.orderBy);
    }
    t = getToken(Token.EOF);
    return select;
  }

  private void orderbyList(OrderBy ob)
  {
    Token t;
    do
    {
      t = getToken(Token.STRING);
      String orderBy = t.image;
      ob.add(orderBy);
    }
    while (lookAhead(1) == true &&
           lookAhead[0].type == Token.COMMA &&
           getToken(Token.COMMA).type == Token.COMMA);
    if (lookAhead[0].type == Token.ASC)
    {
      getToken(Token.ASC);
      ob.setType(OrderBy.ASC);
    }
    else if (lookAhead[0].type == Token.DESC)
    {
      getToken(Token.DESC);
      ob.setType(OrderBy.ASC);
    }
  }

  private void tableList(Select select)
  {
    // String | String alias
    Token t;
    do
    {
      t = getToken(Token.STRING);
      String tableName = t.image;
      lookAhead(1);
      Object o;
      if (lookAhead[0].type == Token.STRING)
      {
        t = nextToken();
        // alias
        Alias alias = new Alias();
        alias.setSubject(tableName);
        alias.setAlias(t.image);
        o = alias;
      }
      else
        o = tableName;
      if (lookAhead(2))
      {
        if (lookAhead[0].type == Token.INNER &&
            lookAhead[1].type == Token.JOIN)
        {
          // handle joins
          Join join = new Join();
          join.setLeft(o);
          getToken(Token.INNER);  // inner
          getToken(Token.JOIN); // join
          join.setType(Join.INNER);
          t = getToken(Token.STRING); // right
          join.setRight(t.image);
          getToken(Token.ON);
          Equation eq = simpleEquation();
          join.setEquation(eq);
          o = join;
        }
      }
      select.addTable(o);
      // look ahead for the while check
      lookAhead(1);
    }
    while (lookAhead[0].type == Token.COMMA &&
           getToken(Token.COMMA).type == Token.COMMA);
  }

  /**
   * Format:
   * *
   * table.*
   * column_name, column_name, table.column_name...
   * function(column_list)
   */
  private void columnList(ArrayList list)
  {
    Token t;

    lookAhead(1);
    if (lookAhead[0].type == Token.STAR)
    {
      getToken(Token.STAR);
      list.add("*");
    }
    else
    {
      lookAhead(3);
      if (lookAhead[0].type == Token.STRING &&
          lookAhead[1].type == Token.PERIOD &&
          lookAhead[2].type == Token.STAR)
      {
        FQColumn fqColumn = new FQColumn();
        t = getToken(Token.STRING);
        fqColumn.setTableName(t.image);
        getToken(Token.PERIOD);
        t = getToken(Token.STAR);
        fqColumn.setColumnName(t.image);
        list.add(fqColumn);
      }
      else
        nameList(list);
    }
  }

  // a single column
  private static final int[] column_string_integer_quotedString_openParen = new int[]
    {Token.STRING,Token.INTEGER,Token.QUOTED_STRING,Token.OPEN_PAREN,
     Token.OPEN_SQUIGLY,Token.QUESTION_MARK};
  private Object column()
  {
    Object o;
    Token t = getToken(column_string_integer_quotedString_openParen);
    if (t.type == Token.STRING)
    {
      String columnName = t.image;
      lookAhead(1);
      if (lookAhead[0].type == Token.PERIOD)
      {
        getToken(Token.PERIOD);
        t = getToken(Token.STRING);
        FQColumn fqColumn = new FQColumn();
        fqColumn.setTableName(columnName);
        fqColumn.setColumnName(t.image);
        o = fqColumn;
      }
      else if (lookAhead[0].type == Token.OPEN_PAREN)
      {
        getToken(Token.OPEN_PAREN);
        Function function = new Function(columnName);
        columnList(function.arguments);
        t = getToken(Token.CLOSE_PAREN);
        o = function;
      }
      else
        o = columnName;
    }
    else if (t.type == Token.INTEGER)
    {
      // check if this is really a number.number (a double)
      String s = t.image;
      lookAhead(2);
      if (lookAhead[0].type == Token.PERIOD && lookAhead[1].type == Token.INTEGER)
      {
        // double
       getToken(Token.PERIOD);
       t = getToken(Token.INTEGER);
       String afterPeriod = t.image;
       Double d = new Double(s+'.'+afterPeriod);
       o = d;
      }
      else
      {
        // integer
        Integer i = new Integer(s);
        o = i;
      }
    }
    else if (t.type == Token.QUESTION_MARK)
    {
      o = new Parameter();
    }
    else if (t.type == Token.QUOTED_STRING)
    {
      QuotedString qs = new QuotedString();
      String s = unQuote(t.image);
      qs.setSubject(s);
      o = qs;
    }
    else if (t.type == Token.OPEN_PAREN)
    {
      Paren p = new Paren();
      p.setSubject(column());
      t = getToken(Token.CLOSE_PAREN);
      o = p;
    }
    else if (t.type == Token.OPEN_SQUIGLY)
    {
      getToken(Token.TS);
      Date date = new Date();
      date.setYear(Integer.parseInt(getToken(Token.INTEGER).image));
      getToken(Token.MINUS);
      date.setMonth(Integer.parseInt(getToken(Token.INTEGER).image));
      getToken(Token.MINUS);
      date.setDay(Integer.parseInt(getToken(Token.INTEGER).image));

      date.setHour(Integer.parseInt(getToken(Token.INTEGER).image));
      getToken(Token.COLON);
      date.setMinute(Integer.parseInt(getToken(Token.INTEGER).image));
      getToken(Token.COLON);
      date.setSecond(Integer.parseInt(getToken(Token.INTEGER).image));
      getToken(Token.PERIOD);
      date.setMillsecond(Integer.parseInt(getToken(Token.INTEGER).image));
      getToken(Token.CLOSE_SQUIGLY);
      o = date;
    }
    else
      throw new RuntimeException("unhandled type: " + t.type);
    // check for alias
    lookAhead(1);
    if (lookAhead[0].type == Token.STRING)
    {
      t = getToken(Token.STRING);
      Alias alias = new Alias();
      alias.setSubject(o);
      alias.setAlias(t.image);
      return alias;
    }
    else
      return o;
  }

  /**
   * Format:
   *   name,
   *  name = number
   *  name = string
   *  name = function(nameList)
   * @param select
   */
  private void nameList(ArrayList list)
  {
    // String | String alias | String.String | String.String alias
    // String ( columnlist) | String ( columnlist) alias
    // number | number alias
    // ( String )
    Token t;
    Object o;
    do
    {
      o = column();
      list.add(o);
      // this look ahead is for the while check
      lookAhead(1);
    }
    while (lookAhead[0].type == Token.COMMA &&
           getToken(Token.COMMA).type == Token.COMMA);
  }

  // format:
  // equation
  private Object whereClause()
  {
    Object result = condition(null);
    return result;
  }

  private static final int[] condition_and_or = new int[]
    {Token.AND,Token.OR};
  private Object condition(Object left)
  {
    Token t;
    Object result;

    Mark m = new Mark();
    try
    {
      if (left == null)
        result = simpleWhere();
      else
        result = left;
      while(lookAhead(1) == true &&
           (lookAhead[0].type == Token.AND ||
            lookAhead[0].type == Token.OR))
      {
        t = getToken(condition_and_or);
        Condition c = new Condition();
        c.setLeft(result);
        if (t.type == Token.AND)
          c.setOperator(Condition.AND);
        else
          c.setOperator(Condition.OR);
        Object right;
        Mark m2 = new Mark();
        try
        {
          right = simpleWhere();
          m2.clear();
        }
        catch(InvalidTokenException e)
        {
          m2.rewind();
          right = condition(null);
        }
        c.setRight(right);
        result = c;
      }
      m.clear();
    }
    catch(InvalidTokenException e)
    {
      m.rewind();
      getToken(Token.OPEN_PAREN);
      Paren p = new Paren();
      p.setSubject(condition(null));
      getToken(Token.CLOSE_PAREN);
      result = p;
      result = condition(result);
    }
    return result;
  }

  private Object simpleWhere()
  {
    Object result;
    Mark m = new Mark();
    try
    {
      result = simpleEquation();
      m.clear();
    }
    catch(InvalidTokenException e)
    {
      m.rewind();
      getToken(Token.OPEN_PAREN);
      Paren p = new Paren();
      p.setSubject(simpleWhere());
      getToken(Token.CLOSE_PAREN);
      result = p;
    }
//    |
//      LOOKAHEAD(In())
//      o = In()
//    |
//      LOOKAHEAD(Between())
//      o = Between()
//    |
    return result;
  }

  private static final int[] oneSideOfEquation_allowed = new int[]
    {Token.STRING,Token.QUOTED_STRING,Token.INTEGER};
  private Object oneSideOfEquation()
  {
    return column();
  }

  private Object equation()
  {
    // simple equation | equation
    lookAhead(1);
    if (lookAhead[0].type == Token.OPEN_PAREN)
      return parenEquation();
    else
      return simpleEquation();
  }

  private Paren parenEquation()
  {
    getToken(Token.OPEN_PAREN);
    Paren paren = new Paren();
    paren.setSubject(simpleEquation());
    getToken(Token.CLOSE_PAREN);
    return paren;
  }

  private static final int[] equation_operator = new int[]
    {Token.EQUALS,Token.LESS_THAN,Token.GREATER_THAN,Token.NOT_EQUALS,
     Token.GREATER_THAN_OR_EQUAL,Token.LESS_THAN_OR_EQUAL};
  private Equation simpleEquation()
  {
    Equation eq = new Equation();
    eq.setLeft(oneSideOfEquation());
    Token t = getToken(equation_operator);
    switch(t.type)
    {
      case Token.EQUALS:
        eq.setOperator(Equation.EQUALS);
      break;
      case Token.LESS_THAN:
        eq.setOperator(Equation.LESS_THAN);
      break;
      case Token.GREATER_THAN:
        eq.setOperator(Equation.GREATER_THAN);
      break;
      case Token.NOT_EQUALS:
        eq.setOperator(Equation.NOT_EQUALS);
      break;
      case Token.GREATER_THAN_OR_EQUAL:
        eq.setOperator(Equation.GREATER_THAN_OR_EQUALS);
      break;
      case Token.LESS_THAN_OR_EQUAL:
        eq.setOperator(Equation.LESS_THAN_OR_EQUALS);
      break;
      default:
        throw new RuntimeException("unknown operator");
    }
    eq.setRight(oneSideOfEquation());
    return eq;
  }

  // get the next token, must be of the given type
  private Token getToken(int type)
  {
    Token t = nextToken();
    if (t.type == type)
      return t;
    // expecting ... found ...
    throw new InvalidTokenException(type,t.type);
  }

  // get the next token, must be one of the given types
  private Token getToken(int[] types)
  {
    Token t = nextToken();
    for (int i = 0; i < types.length; i++)
      if (t.type == types[i])
        return t;
    throw new InvalidTokenException(types,t.type);
  }

  private Token nextToken()
  {
    if (currentMark != null)
      return currentMark.nextToken();
    return (Token)tokens.get(currentToken++);
  }

  private boolean lookAhead(int count)
  {
    Mark m = new Mark();
    for (int i = 0; i < count; i++)
    {
      Token t = nextToken();
      lookAhead[i].type = t.type;
      lookAhead[i].image = t.image;
      if (t.type == t.EOF)
      {
        /** @todo make this work somehow */
        // make sure an exception is thrown if false is returned and
        // it's ignored
//          lookAhead = null;
        m.rewind();
        if (i == count-1)
          return true;
        else
          return false;
      }
    }
    m.rewind();
    return true;
  }

  // replace all '' with '
  private String unQuote(String s)
  {
    char[] ca = new char[s.length()];
    int index = 0;
    int length = s.length() - 1;
    for (int i = 1; i < length; i++)
    {
      char c = s.charAt(i);
      if (c == '\'')
        i++; // skip one
      ca[index++] = c;
    }
    return new String(ca,0,index);
  }

  // used to intercept parse errors
  private class InvalidTokenException extends RuntimeException
  {
    InvalidTokenException(int expecting, int found)
    {
      super("expecting: " + Token.tokenNames[expecting] + " found " + Token.tokenNames[found]);
    }

    InvalidTokenException(int[] types, int found)
    {
      super("expecting one of: " + types + " found " + Token.tokenNames[found]);
    }
  }

  // mark the current location so that a rewind to here may be done
  private Mark currentMark = null;
  class Mark
  {
    int markedAt;     // where the mark started
    boolean valid; // valid?
    Mark parent;   // the parent maker
    int currentMarkedToken;

    Mark()
    {
      if (currentMark != null)
        markedAt = currentMark.currentMarkedToken;
      else
        markedAt = currentToken;

      currentMarkedToken = markedAt;
      parent = currentMark;
      currentMark = this;
      valid = true;
    }

    private boolean isValid()
    {
      return valid == true &&
             (parent == null || parent.isValid());
    }

    public Token nextToken()
    {
      if (isValid() == false)
        throw new RuntimeException("marker is not valid");
      return (Token)tokens.get(currentMarkedToken++);
    }

    private void invalidate()
    {
      if (isValid() == false)
        throw new RuntimeException("marker is not valid");
      currentMark = parent;
      valid = false;
    }

    // allow the tokens to be regotten
    void rewind()
    {
      invalidate();
    }

    // this marker is no longer valid
    void clear()
    {
      if (parent != null)
        parent.currentMarkedToken = currentMarkedToken;
      else
        currentToken = currentMarkedToken;
      invalidate();
    }
  }
}
