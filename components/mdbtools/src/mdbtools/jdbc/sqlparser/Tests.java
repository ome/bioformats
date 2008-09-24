package mdbtools.jdbc.sqlparser;

import java.util.ArrayList;

/**
 * Regression test the parser
 */
public class Tests
{
  private Test[] tests = new Test[59];

  public static void main(String[] args)
  {
    (new Tests()).go();
  }

  public void go()
  {
    // build the test cases
    buildTestCases();

//    runAllTests();
    System.out.println(runTest(58)?"passed":"failed");
  }

  private boolean runTest(int testNumber)
  {
    if (tests[testNumber].sql == null)
    {
      System.out.println("skipping test: " + testNumber);
      return true;
    }

    Parser parser = new Parser(tests[testNumber].sql);
    Object parsedTree = parser.parse();
    if (tests[testNumber].parseTree.equals(parsedTree) == false)
      return false;
    return true;
  }

  private void runAllTests()
  {
    // run all test cases
    int failureCount = 0;
    for (int i = 0; i < tests.length; i++)
    {
      boolean status;
      try
      {
        status = runTest(i);
      }
      catch(RuntimeException e)
      {
        status = false;
      }
      if (status == false)
      {
        failureCount++;
        System.out.println("failed test: " + i);
      }
    }
    if (failureCount == 0)
      System.out.println("all tests passed");
    else
      System.out.println(failureCount + " tests failed");
  }

  private void buildTestCases()
  {
    for (int i = 0; i < tests.length; i++)
      tests[i] = new Test();

    //
    // test simple fields
    //
    tests[0].sql = "select * from me";
    tests[0].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"*"}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[1].sql = "select tbl.* from me tbl";
    tests[1].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFQColumn("tbl","*")}),
        buildTableList(new Object[]
          {buildAlias("me","tbl")}),
        null,
        null);

    tests[2].sql = "select a from me";
    tests[2].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"a"}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[3].sql = "select {ts 2004-07-12 08:24:26.588} from me";
    tests[3].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildDate(2004,7,12,8,24,26,588)}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[4].sql = "select id,you from me";
    tests[4].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id","you"}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[5].sql = "select id a from me";
    tests[5].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildAlias("id","a")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[6].sql = "select me.id,you.id from me,you";
    tests[6].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFQColumn("me","id"),buildFQColumn("you","id")}),
        buildTableList(new Object[]
          {"me","you"}),
        null,
        null);

    //
    // test quoted field names
    //
    // select id from 'a-table'

    //
    // test number fields
    //
    tests[7].sql = "select 2 from me";
    tests[7].parseTree = buildSelect(
        buildColumnList(new Object[]
          {new Integer(2)}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[8].sql = "select 23.4 from me";
    tests[8].parseTree = buildSelect(
        buildColumnList(new Object[]
          {new Double(23.4)}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[9].sql = "select 9 h from me";
    tests[9].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildAlias(new Integer(9),"h")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    //
    // test quoted fields
    //
    tests[10].sql = "select 'hi' from me";
    tests[10].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildQuotedString("hi")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[11].sql = "select 'bye',id from me";
    tests[11].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildQuotedString("bye"),"id"}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[12].sql = "select 'why' a, id b from me";
    tests[12].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildAlias(buildQuotedString("why"),"a"),buildAlias("id","b")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[13].sql = "select '' from me";
    tests[13].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildQuotedString("")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[14].sql = "select 'testing house''s' from me";
    tests[14].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildQuotedString("testing house's")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    //
    // test field functions
    //
    tests[15].sql = "select count(*) from me";
    tests[15].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFunction("count",new Object[]{"*"})}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[16].sql = "select count(id) from me";
    tests[16].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFunction("count",new Object[]{"id"})}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[17].sql = "select sum(id) from me";
    tests[17].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFunction("sum",new Object[]{"id"})}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[18].sql = "select id, q(a,b) from me";
    tests[18].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id",buildFunction("q",new Object[]{"a","b"})}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[19].sql = "select id, q(a,b) a from me";
    tests[19].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id",buildAlias(buildFunction("q",new Object[]{"a","b"}),"a")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

      //
      // test tables
      //
    tests[20].sql = "select id from me, you";
    tests[20].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me","you"}),
        null,
        null);

    tests[21].sql = "select id from me you";
    tests[21].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {buildAlias("me","you")}),
        null,
        null);

    //
    // test where
    //
    tests[22].sql = "select id from me where i = a";
    tests[22].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.EQUALS,"a"),
        null);

    tests[23].sql = "select id from me where i < a";
    tests[23].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.LESS_THAN,"a"),
        null);

    tests[24].sql = "select id from me where i > a";
    tests[24].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.GREATER_THAN,"a"),
        null);

    tests[25].sql = "select id from me where i <> a";
    tests[25].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.NOT_EQUALS,"a"),
        null);

    tests[26].sql = "select id from me where i >= a";
    tests[26].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.GREATER_THAN_OR_EQUALS,"a"),
        null);

    tests[27].sql = "select id from me where i <= a";
    tests[27].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.LESS_THAN_OR_EQUALS,"a"),
        null);

    tests[28].sql = "select id from me where i = 'c'";
    tests[28].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.EQUALS,buildQuotedString("c")),
        null);

    tests[29].sql = "select id from me where 'c' = 'a'";
    tests[29].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation(buildQuotedString("c"),Equation.EQUALS,buildQuotedString("a")),
        null);

    tests[30].sql = "select id from me where i = 9";
    tests[30].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.EQUALS,new Integer(9)),
        null);

    tests[31].sql = "select id from me where i = 9.2";
    tests[31].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.EQUALS,new Double(9.2)),
        null);

    tests[32].sql = "select id from me where 9 = i";
    tests[32].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation(new Integer(9),Equation.EQUALS,"i"),
        null);

    tests[33].sql = "select id from me where 9.2 = i";
    tests[33].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation(new Double(9.2),Equation.EQUALS,"i"),
        null);

      //
      // test joins
      //
    tests[34].sql = "SELECT make.name, model.name FROM MODEL inner join MAKE on MODEL.makeId=Make.ID";
    tests[34].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFQColumn("make","name"),buildFQColumn("model","name")}),
        buildTableList(new Object[]
          {buildJoin("MODEL",Join.INNER,"MAKE",
                     buildEquation(buildFQColumn("MODEL","makeId"),
                                   Equation.EQUALS,
                                   buildFQColumn("Make","ID")))}),
        null,
        null);


    tests[35].sql = "SELECT make.name, model.name FROM MODEL inner join MAKE on MODEL.makeId=Make.ID, person";
    tests[35].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildFQColumn("make","name"),buildFQColumn("model","name")}),
        buildTableList(new Object[]
          {buildJoin("MODEL",Join.INNER,"MAKE",
                     buildEquation(buildFQColumn("MODEL","makeId"),
                                   Equation.EQUALS,
                                   buildFQColumn("Make","ID"))),"person"}),
        null,
        null);


//SELECT make.name, model.name FROM MODEL inner join MAKE on MODEL.makeId=Make.ID you
//SELECT make.name, model.name FROM MODEL join MAKE on MODEL.make=Make.ID
//SELECT make.name, model.name FROM MODEL left join MAKE on MODEL.make=Make.ID
//SELECT make.name, model.name FROM MODEL right join MAKE on MODEL.make=Make.ID
//SELECT make.name, model.name FROM MODEL left outer join MAKE on MODEL.make=Make.ID
//SELECT make.name, model.name FROM MODEL right outer join MAKE on MODEL.make=Make.ID
//SELECT make.name, model.name FROM MODEL INNER JOIN MAKE ON MODEL.make = Make.ID,
//        person INNER JOIN product ON person.id = product.ID

    //
    // test parenthesis
    //
    tests[36].sql = "select (id) from me";
    tests[36].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildParen("id")}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[37].sql = "SELECT ((id)) FROM me";
    tests[37].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildParen(buildParen("id"))}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[38].sql = "SELECT (9) FROM me";
    tests[38].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildParen(new Integer(9))}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[39].sql = "SELECT (9.4) FROM me";
    tests[39].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildParen(new Double(9.4))}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[40].sql = "SELECT ('hi') FROM me";
    tests[40].parseTree = buildSelect(
        buildColumnList(new Object[]
          {buildParen(buildQuotedString("hi"))}),
        buildTableList(new Object[]
          {"me"}),
        null,
        null);

    tests[41].sql = "select id from me where (i) > a";
    tests[41].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation(buildParen("i"),Equation.GREATER_THAN,"a"),
        null);

    tests[42].sql = "select id from me where i > (a)";
    tests[42].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation("i",Equation.GREATER_THAN,buildParen("a")),
        null);

    tests[43].sql = "select id from me where (i) > (a)";
    tests[43].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildEquation(buildParen("i"),Equation.GREATER_THAN,buildParen("a")),
        null);

    //
    // test conditions
    //
    tests[44].sql = "select id from me where i = a and b = c";
    tests[44].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildCondition(
          buildEquation("i",Equation.EQUALS,"a"),
        Condition.AND,
          buildEquation("b",Equation.EQUALS,"c")),
        null);

    tests[45].sql = "select id from me where i = a and b < c";
    tests[45].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildCondition(
        buildEquation("i",Equation.EQUALS,"a"),
        Condition.AND,
        buildEquation("b",Equation.LESS_THAN,"c")),
        null);

    tests[46].sql = "select id from me where i = a and b > c";
    tests[46].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildCondition(
        buildEquation("i",Equation.EQUALS,"a"),
        Condition.AND,
        buildEquation("b",Equation.GREATER_THAN,"c")),
        null);

    tests[47].sql = "select id from me where i = a and b > c and c = 4";
    tests[47].parseTree = buildSelect(
        buildColumnList(new Object[]
          {"id"}),
        buildTableList(new Object[]
          {"me"}),
        buildCondition(
          buildCondition(
          buildEquation("i",Equation.EQUALS,"a"),
          Condition.AND,
          buildEquation("b",Equation.GREATER_THAN,"c")),
        Condition.AND,
          buildEquation("c",Equation.EQUALS,new Integer(4))),
        null);

      tests[48].sql = "select id from me where (i = a) and (b > c)";
      tests[48].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildCondition(
            buildParen(buildEquation("i",Equation.EQUALS,"a")),
            Condition.AND,
            buildParen(buildEquation("b",Equation.GREATER_THAN,"c"))),
        null);

      tests[49].sql = "select id from me where (i = a) or (b > c)";
      tests[49].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildCondition(
            buildParen(buildEquation("i",Equation.EQUALS,"a")),
            Condition.OR,
            buildParen(buildEquation("b",Equation.GREATER_THAN,"c"))),
        null);

      tests[50].sql = "select id from me where (i = a or b > c)";
      tests[50].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildParen(
            buildCondition(
            buildEquation("i",Equation.EQUALS,"a"),
            Condition.OR,
            buildEquation("b",Equation.GREATER_THAN,"c"))),
        null);

      tests[51].sql = "select id from me where (i = a or b > c) and (i = a or b > c)";
      tests[51].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildCondition(
            buildParen(
              buildCondition(
              buildEquation("i",Equation.EQUALS,"a"),
              Condition.OR,
              buildEquation("b",Equation.GREATER_THAN,"c"))),
          Condition.AND,
            buildParen(
              buildCondition(
              buildEquation("i",Equation.EQUALS,"a"),
              Condition.OR,
              buildEquation("b",Equation.GREATER_THAN,"c")))),
        null);

      tests[52].sql = "select id from me where (i = a or b > c) and 9 = 2";
      tests[52].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildCondition(
            buildParen(
              buildCondition(
              buildEquation("i",Equation.EQUALS,"a"),
              Condition.OR,
              buildEquation("b",Equation.GREATER_THAN,"c"))),
          Condition.AND,
            buildEquation(new Integer(9),Equation.EQUALS,new Integer(2))),
        null);

      tests[53].sql = "select id from me where (i = a or b > c) and 9 = 2 or (b = 9 or b = 7)";
      tests[53].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildCondition(
            buildCondition(
              buildParen(
                buildCondition(
                  buildEquation("i",Equation.EQUALS,"a"),
                Condition.OR,
                  buildEquation("b",Equation.GREATER_THAN,"c"))),
            Condition.AND,
              buildEquation(new Integer(9),Equation.EQUALS,new Integer(2))),
          Condition.OR,
            buildParen(
              buildCondition(
                buildEquation("b",Equation.EQUALS,new Integer(9)),
              Condition.OR,
                buildEquation("b",Equation.EQUALS,new Integer(7))))),
        null);

      tests[54].sql = "select id from me where ((i = a) or (b > c)) and 9 = 2";
      tests[54].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          buildCondition(
            buildParen(
              buildCondition(
                buildParen(
                  buildEquation("i",Equation.EQUALS,"a")),
              Condition.OR,
                buildParen(
                  buildEquation("b",Equation.GREATER_THAN,"c")))),
          Condition.AND,
              buildEquation(new Integer(9),Equation.EQUALS,new Integer(2))),
        null);

      // test orderby
      tests[55].sql = "select id from me order by id";
      tests[55].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          null,
          buildOrderBy(new String[]
            {"id"},null));

      tests[56].sql = "select id from me order by id, a";
      tests[56].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          null,
          buildOrderBy(new String[]
            {"id","a"},null));

      tests[57].sql = "select id from me order by id asc";
      tests[57].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          null,
          buildOrderBy(new String[]
            {"id","a"},"asc"));

      tests[58].sql = "select id from me order by id, a desc";
      tests[58].parseTree = buildSelect(
          buildColumnList(new Object[]
            {"id"}),
          buildTableList(new Object[]
            {"me"}),
          null,
          buildOrderBy(new String[]
            {"id","a"},"desc"));

// test between
//select id from me where id between 4 and 6
//select id from me where id not between 4 and 6
//select id from me where id not between 4 and 6 and you = 9

// test field math
//select a + 2 from me
//select a * 2 from me
//select a / 2 from me
//select a - 2 from me
//select (a + 2) from me
//select (a - 2) from me
//select a + 2 - from me
//select a + 2 - 8 - 9 from me
//select (a + 2) - (8 - 9) from me
//select (a + 2) - b - 9 from me
//select ((a + 2)) from me
//select ((a + 2)) as you from me
//select ((a + 2 as you)) from me
//select '9' + 3 from me
//select id from me where i > (3 + 3)
//select id from me where i > 3 + 3
//select id from me where (i * 2) > (2 + 3)
//select id from me where (i * 2 + 6) > a
//select id from me where 2 + 6 > a

// test in
//select id from me where id in (1,2,3,4)
//select id from me where name in ('joe','sam','fred')
//select id from me where name in (joe,sam,fred)
//select id from me where name not in ('joe','sam','fred')
//select id from me where id in (1,(select a from b),3)
//select id from me where id in (1,(select a,b from b),3)
//select id from me where id in (1,count(you),7)
//select id from me where id in (1,2,3,4) and i= 3

// test sub-selects
//select (select id from me) as id from me
//select (select id as i from me) id from me
//select (select id, a from me) id from me
//select id from (select id from you)
//select id from (select id from you) as you
//select id from (select id, you from you) as you
//SELECT make.name, model.name FROM (SELECT id, name, make FROM model) AS model INNER JOIN MAKE ON MODEL.make = Make.ID",
//SELECT make.name, model.name FROM (SELECT id, name, make FROM model) AS model INNER JOIN (SELECT id, name FROM make) AS make ON MODEL.make = Make.ID",
//select id from me where i = (select id from me)
//select id from me where id = (select sum(id) from me)
//select id from me where id in ((select id from me))
//select id from me where id not in ((select id from me))

// misc
//select id from me where id between 4 and 6 and id in (5,9)

  }

  // a single test case
  private class Test
  {
    String sql;
    Select parseTree;
  }

  private Select buildSelect(ArrayList columnList, ArrayList tableList,
                             Object where,OrderBy orderBy)
  {
    Select result = new Select();
    result.columns = columnList;
    result.tables = tableList;
    result.setWhere(where);
    result.orderBy = orderBy;
    return result;
  }

  private ArrayList buildColumnList(Object[] columns)
  {
    ArrayList result = new ArrayList(columns.length);
    for (int i = 0; i < columns.length; i++)
      result.add(columns[i]);
    return result;
  }

  private ArrayList buildTableList(Object[] tables)
  {
    ArrayList result = new ArrayList(tables.length);
    for (int i = 0; i < tables.length; i++)
      result.add(tables[i]);
    return result;
  }

  private OrderBy buildOrderBy(String[] orderBy,String type)
  {
    OrderBy result = new OrderBy();
    for (int i = 0; i < orderBy.length; i++)
      result.add(orderBy[i]);
    if (type != null)
    {
      if (type.equalsIgnoreCase("asc"))
        result.setType(OrderBy.ASC);
      else if (type.equalsIgnoreCase("desc"))
        result.setType(OrderBy.DESC);
      else
        throw new RuntimeException("unknown order by type");
    }
    return result;
  }

  private Function buildFunction(String name,Object[] paramters)
  {
    Function result = new Function(name);
    for (int i = 0; i < paramters.length; i++)
      result.arguments.add(paramters[i]);
    return result;
  }

  private Alias buildAlias(Object subject, String alias)
  {
    Alias result = new Alias();
    result.setSubject(subject);
    result.setAlias(alias);
    return result;
  }

  private FQColumn buildFQColumn(String table, String column)
  {
    FQColumn result = new FQColumn();
    result.setTableName(table);
    result.setColumnName(column);
    return result;
  }

  private Equation buildEquation(Object left,int operator, Object right)
  {
    Equation result = new Equation();
    result.setLeft(left);
    result.setOperator(operator);
    result.setRight(right);
    return result;
  }

  private QuotedString buildQuotedString(String s)
  {
    QuotedString qs = new QuotedString();
    qs.setSubject(s);
    return qs;
  }

  private Paren buildParen(Object o)
  {
    Paren p = new Paren();
    p.setSubject(o);
    return p;
  }

  private Join buildJoin(Object left, int type, Object right,Equation eq)
  {
    Join join = new Join();
    join.setLeft(left);
    join.setType(type);
    join.setRight(right);
    join.setEquation(eq);
    return join;
  }

  private Condition buildCondition(Object left, int operator, Object right)
  {
    Condition c = new Condition();
    c.setLeft(left);
    c.setOperator(operator);
    c.setRight(right);
    return c;
  }

  private Date buildDate(int year,int month,int day,int hour,int minute,
                         int second,int millsecond)
  {
    Date date = new Date();

    date.setYear(year);
    date.setMonth(month);
    date.setDay(day);

    date.setHour(hour);
    date.setMinute(minute);
    date.setSecond(second);
    date.setMillsecond(millsecond);

    return date;
  }
}
