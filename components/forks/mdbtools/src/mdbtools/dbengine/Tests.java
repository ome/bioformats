/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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

package mdbtools.dbengine;

import mdbtools.dbengine.functions.Aggregate;
import mdbtools.dbengine.functions.ConCat;
import mdbtools.dbengine.functions.Count;
import mdbtools.dbengine.functions.Function;
import mdbtools.dbengine.functions.Length;
import mdbtools.dbengine.functions.Lower;
import mdbtools.dbengine.functions.Max;
import mdbtools.dbengine.functions.Min;
import mdbtools.dbengine.functions.Upper;
import mdbtools.dbengine.sql.Condition;
import mdbtools.dbengine.sql.Equation;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.FunctionDef;
import mdbtools.dbengine.sql.Join;
import mdbtools.dbengine.sql.OrderBy;
import mdbtools.dbengine.sql.Select;

import java.sql.SQLException;

/**
 * Tests cases for the engine itself
 */
public class Tests
{
  private Test[] tests = new Test[38];

  public static void main(String[] args)
  {
    new Tests().go();
  }

  private void go()
  {
    try
    {
      buildTestCases();

    runAllTests();
//      runTests(0,29);
//      System.out.println(runTest(29)?"passed":"failed");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private boolean runTest(int index)
    throws SQLException
  {
    Test test = tests[index];
    System.out.println("executing: " + test.sql.toString());

    Engine engine = new Engine(/*test.dataSource*/);
    Data data = engine.execute(test.sql);
    for (int i = 0; i < test.data.length; i++)
    {
      data.next();
      for (int j = 0; j < test.data[i].length; j++)
      {
        if (test.data[i][j].equals(data.get(j)) == false)
        {
          System.out.println("failed at: " + i + "," + j + "," + test.data[i][j] + "," + data.get(j));
          return false;  // wrong data
        }
      }
    }
    if (data.next())
    {
      return false;  // too many rows returned
    }
    return true;  // everything checks out
  }

  private void runAllTests()
    throws SQLException
  {
    runTests(0,tests.length-1);
  }

  private void runTests(int from, int to)
    throws SQLException
  {
    // run all test cases
    int failureCount = 0;
    for (int i = from; i <= to; i++)
    {
      boolean status;
      try
      {
        status = runTest(i);
      }
      catch(RuntimeException e)
      {
        e.printStackTrace();
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

    DataSource simpleDS = buildDB();

    //
    // test simple queries
    //

    // select model from cars
    tests[0].sql = buildSelect(new Object[]{
          new FQColumn(0,FIELD_CARS_MODEL)},
          new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[0].data = new Object[][]
    {
      {"contour"},
      {"viper"},
      {"stratus"},
      {"ram"},
      {"F-150"}
    };

    // select * from cars
    tests[1].sql = buildSelect(new Object[]{
      new FQColumn(0,FIELD_CARS_ID),new FQColumn(0,FIELD_CARS_MAKE),
      new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[1].data = new Object[][]
    {
      {new Integer(1),"ford","contour"},
      {new Integer(2),"dodge","viper"},
      {new Integer(3),"dodge","stratus"},
      {new Integer(4),"dodge","ram"},
      {new Integer(5),"ford","F-150"}
    };

    // select model, 1 from cars
    tests[2].sql = buildSelect(new Object[]{
      new FQColumn(0,FIELD_CARS_MODEL),new Integer(1)},
          new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[2].data = new Object[][]
    {
      {"contour",new Integer(1)},
      {"viper",new Integer(1)},
      {"stratus",new Integer(1)},
      {"ram",new Integer(1)},
      {"F-150",new Integer(1)}
    };

    //
    // test functions
    //

    // select length(model) from cars
    tests[3].sql = buildSelect(new Object[]
      {buildFunction(new Length(),new FQColumn(0,FIELD_CARS_MODEL))},
          new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[3].data = new Object[][]
    {
      {new Integer(7)},
      {new Integer(5)},
      {new Integer(7)},
      {new Integer(3)},
      {new Integer(5)}
    };

    // select make,length(model) from cars
    tests[4].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),
        buildFunction(new Length(),new FQColumn(0,FIELD_CARS_MODEL))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[4].data = new Object[][]
    {
      {"ford",new Integer(7)},
      {"dodge",new Integer(5)},
      {"dodge",new Integer(7)},
      {"dodge",new Integer(3)},
      {"ford",new Integer(5)}
    };

    // select id,make,upper(model) from cars
    tests[5].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),
        buildFunction(new Upper(),new FQColumn(0,FIELD_CARS_MODEL))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[5].data = new Object[][]
    {
      {new Integer(1),"ford","CONTOUR"},
      {new Integer(2),"dodge","VIPER"},
      {new Integer(3),"dodge","STRATUS"},
      {new Integer(4),"dodge","RAM"},
      {new Integer(5),"ford","F-150"}
    };

    // select id,make,lower(model) from cars
    tests[6].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),
        buildFunction(new Lower(),new FQColumn(0,FIELD_CARS_MODEL))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[6].data = new Object[][]
    {
      {new Integer(1),"ford","contour"},
      {new Integer(2),"dodge","viper"},
      {new Integer(3),"dodge","stratus"},
      {new Integer(4),"dodge","ram"},
      {new Integer(5),"ford","f-150"}
    };

    //
    // test aggregates
    //

    // select count(*) from cars
    tests[7].sql = buildSelect(new Object[]
      {buildFunction(new Count(),new Integer(1))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[7].data = new Object[][]
    {
      {new Integer(5)}
    };

    // select count(*), 1 from cars
    tests[8].sql = buildSelect(new Object[]
      {buildFunction(new Count(),new Integer(1)),new Integer(1)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[8].data = new Object[][]
    {
      {new Integer(5),new Integer(1)}
    };

    // select max(model) from cars
    tests[9].sql = buildSelect(new Object[]
      {buildFunction(new Max(),new FQColumn(0,FIELD_CARS_MODEL))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[9].data = new Object[][]
    {
      {"viper"}
    };

    // select max(id) from cars
    tests[10].sql = buildSelect(new Object[]
      {buildFunction(new Max(),new FQColumn(0,FIELD_CARS_ID))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[10].data = new Object[][]
    {
      {new Integer(5)}
    };

    // select min(id) from cars
    tests[11].sql = buildSelect(new Object[]
      {buildFunction(new Min(),new FQColumn(0,FIELD_CARS_ID))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[11].data = new Object[][]
    {
      {new Integer(1)}
    };

    // select min(model) from cars
    tests[12].sql = buildSelect(new Object[]
      {buildFunction(new Min(),new FQColumn(0,FIELD_CARS_MODEL))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[12].data = new Object[][]
    {
      {"F-150"}
    };

    // select min(lower(model)) from cars
    tests[13].sql = buildSelect(new Object[]
      {buildFunction(new Min(),buildFunction(new Lower(),new FQColumn(0,FIELD_CARS_MODEL)))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[13].data = new Object[][]
    {
      {"contour"}
    };

    // select max(length(model)) from cars
    tests[14].sql = buildSelect(new Object[]
      {buildFunction(new Max(),buildFunction(new Length(),new FQColumn(0,FIELD_CARS_MODEL)))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[14].data = new Object[][]
    {
      {new Integer(7)}
    };

    // select min(length(model)) from cars
    tests[15].sql = buildSelect(new Object[]
      {buildFunction(new Min(),buildFunction(new Length(),new FQColumn(0,FIELD_CARS_MODEL)))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
          null,
          null,
          null);
    tests[15].data = new Object[][]
    {
      {new Integer(3)}
    };

    //
    // test group by
    //

    // select count(*),make from cars group by make
    tests[16].sql = buildSelect(new Object[]
      {buildFunction(new Count(),new Integer(1)),new FQColumn(0,FIELD_CARS_MAKE)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      new FQColumn[]{new FQColumn(0,FIELD_CARS_MAKE)},null);
    tests[16].data = new Object[][]
    {
      {new Integer(3),"dodge"},
      {new Integer(2),"ford"}
    };

    // select count(*),length(make) from cars group by make;
    // select sum(length(model)),make from cars group by make;

    //
    // test order by
    //

    //  select make,model from cars order by 1 asc;
    tests[17].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new Integer(1),new Boolean(true)}});
    tests[17].data = new Object[][]
    {
      {"dodge","viper"},
      {"dodge","stratus"},
      {"dodge","ram"},
      {"ford","contour"},
      {"ford","F-150"}
    };

    //  select make,model from cars order by 1,2;
    tests[18].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new Integer(1),new Boolean(true)},
                     {new Integer(2),new Boolean(true)}});
    tests[18].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","F-150"},
      {"ford","contour"}
    };

    //  select make,lower(model) from cars order by 1,2;
    tests[19].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),buildFunction(new Lower(),new FQColumn(0,FIELD_CARS_MODEL))},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new Integer(1),new Boolean(true)},
                     {new Integer(2),new Boolean(true)}});
    tests[19].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","contour"},
      {"ford","f-150"}
    };

    // select make,model from cars order by make,model;
    tests[20].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(true)},
                     {new FQColumn(0,FIELD_CARS_MODEL),new Boolean(true)}});
    tests[20].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","F-150"},
      {"ford","contour"},
    };

    // select length(cars.make) from cars order by cars.model
    tests[21].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(true)},
                     {new FQColumn(0,FIELD_CARS_MODEL),new Boolean(true)}});
    tests[21].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","F-150"},
      {"ford","contour"}
    };

    // select id,cars.make,model from cars order by length(cars.model);
    tests[22].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(true)},
                     {new FQColumn(0,FIELD_CARS_MODEL),new Boolean(true)}});
    tests[22].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","F-150"},
      {"ford","contour"}
    };

    // select id,cars.make,model from cars order by length(cars.model),id;
    tests[23].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(true)},
                     {new FQColumn(0,FIELD_CARS_MODEL),new Boolean(true)}});
    tests[23].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","F-150"},
      {"ford","contour"}
    };

    // select cars.id,cars.make,cars.model from cars order by cars.make,cars.model;
    tests[24].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),new FQColumn(0,FIELD_CARS_MAKE),
          new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(true)},
                     {new FQColumn(0,FIELD_CARS_MODEL),new Boolean(true)}});
    tests[24].data = new Object[][]
    {
      {new Integer(4),"dodge","ram"},
      {new Integer(3),"dodge","stratus"},
      {new Integer(2),"dodge","viper"},
      {new Integer(5),"ford","F-150"},
      {new Integer(1),"ford","contour"}
    };

    // select id from cars order by cars.make;
    tests[25].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(true)}});
    tests[25].data = new Object[][]
    {
      {new Integer(2)},
      {new Integer(3)},
      {new Integer(4)},
      {new Integer(1)},
      {new Integer(5)}
    };

    //  select make,model from cars order by 1,model;
    tests[26].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new Integer(1),new Boolean(true)},
                     {new FQColumn(0,FIELD_CARS_MODEL),new Boolean(true)}});
    tests[26].data = new Object[][]
    {
      {"dodge","ram"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"ford","F-150"},
      {"ford","contour"}
    };

    //  select make,model from cars order by model desc;
    tests[27].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MODEL),new Boolean(false)}});
    tests[27].data = new Object[][]
    {
      {"dodge","viper"},
      {"dodge","stratus"},
      {"dodge","ram"},
      {"ford","contour"},
      {"ford","F-150"}
    };

    //  select make,model from cars order by make desc, owner asc;
    tests[28].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      new Object[][]{{new FQColumn(0,FIELD_CARS_MAKE),new Boolean(false)},
                     {new FQColumn(0,FIELD_CARS_OWNER),new Boolean(true)}});
    tests[28].data = new Object[][]
    {
      {"ford","contour"},
      {"ford","F-150"},
      {"dodge","stratus"},
      {"dodge","viper"},
      {"dodge","ram"}
    };

    // select count(make),make from cars group by make order by make desc;
    tests[29].sql = buildSelect(new Object[]
      {buildFunction(new Count(),new Integer(1)),new FQColumn(0,FIELD_CARS_MAKE)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      new FQColumn[]{new FQColumn(0,FIELD_CARS_MAKE)},
      new Object[][]{{new Integer(2),new Boolean(false)}});
    tests[29].data = new Object[][]
    {
      {new Integer(2),"ford"},
      {new Integer(3),"dodge"}
    };

    //
    // test where clause
    //

    // select id,make,model from cars where make = 'dodge';
    tests[30].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildEquation(new FQColumn(0,FIELD_CARS_MAKE),Equation.EQUALS,"dodge"),
      null,
      null);
    tests[30].data = new Object[][]
    {
      {new Integer(2),"dodge","viper"},
      {new Integer(3),"dodge","stratus"},
      {new Integer(4),"dodge","ram"},
    };

    // select id,make,model from cars where make <> 'dodge'
    tests[31].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildEquation(new FQColumn(0,FIELD_CARS_MAKE),Equation.NOT_EQUALS,"dodge"),
      null,
      null);
    tests[31].data = new Object[][]
    {
      {new Integer(1),"ford","contour"},
      {new Integer(5),"ford","F-150"}
    };

    // select id,make,model from cars where 1 = 0
    tests[32].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildEquation(new Integer(1),Equation.EQUALS,new Integer(0)),
      null,
      null);
    tests[32].data = new Object[][]
    {
    };

    // select id,make,model from cars where 10 < 9
    tests[33].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildEquation(new Integer(10),Equation.LESS_THAN,new Integer(9)),
      null,
      null);
    tests[33].data = new Object[][]
    {
    };

    // select id,make,model from cars where 1 > 4
    tests[34].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildEquation(new Integer(1),Equation.GREATER_THAN,new Integer(4)),
      null,
      null);
    tests[34].data = new Object[][]
    {
    };

    // select id,make,model from cars where make = 'dodge' and model = 'stratus'
    tests[35].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildCondition(
        buildEquation(new FQColumn(0,FIELD_CARS_MAKE),Equation.EQUALS,"dodge"),
      Condition.AND,
        buildEquation(new FQColumn(0,FIELD_CARS_MODEL),Equation.EQUALS,"stratus")),
      null,
      null);
    tests[35].data = new Object[][]
    {
      {new Integer(3),"dodge","stratus"},
    };

    // select id,make,model from cars where make = 'ford' or model = 'stratus'
    tests[36].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL)},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      buildCondition(
        buildEquation(new FQColumn(0,FIELD_CARS_MAKE),Equation.EQUALS,"ford"),
      Condition.OR,
        buildEquation(new FQColumn(0,FIELD_CARS_MODEL),Equation.EQUALS,"stratus")),
      null,
      null);
    tests[36].data = new Object[][]
    {
      {new Integer(1),"ford","contour"},
      {new Integer(3),"dodge","stratus"},
      {new Integer(5),"ford","F-150"}
    };

    //
    // multi-argument functions
    //

    // select id,concat(make,model) from cars
    tests[37].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_ID),
        buildFunction(new ConCat(),new FQColumn[]{
          new FQColumn(0,FIELD_CARS_MAKE),
          new FQColumn(0,FIELD_CARS_MODEL)})},
      new Object[]{simpleDS.getTable(TABLE_CARS)},
      null,
      null,
      null);
    tests[37].data = new Object[][]
    {
      {new Integer(1),"fordcontour"},
      {new Integer(2),"dodgeviper"},
      {new Integer(3),"dodgestratus"},
      {new Integer(4),"dodgeram"},
      {new Integer(5),"fordF-150"}
    };

    //
    // test joins
    //
/*

    // select cars.make, cars.model,person.name from cars, person;
    tests[36].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL),
       new FQColumn(1,FIELD_PERSON_NAME)},
      new Object[]{simpleDS.getTable(TABLE_CARS),
                   simpleDS.getTable(TABLE_PERSON)},
      null,
      null,
      null);
    tests[36].data = new Object[][]
    {
      {"ford","contour","billy"},
      {"ford","contour","george"},
      {"ford","contour","susan"},
      {"ford","contour","mary"},
      {"dodge","viper","billy"},
      {"dodge","viper","george"},
      {"dodge","viper","susan"},
      {"dodge","viper","mary"},
      {"dodge","stratus","billy"},
      {"dodge","stratus","george"},
      {"dodge","stratus","susan"},
      {"dodge","stratus","mary"},
      {"dodge","ram","billy"},
      {"dodge","ram","george"},
      {"dodge","ram","susan"},
      {"dodge","ram","mary"},
      {"ford","F-150","billy"},
      {"ford","F-150","george"},
      {"ford","F-150","susan"},
      {"ford","F-150","mary"}
    };

    // select cars.make, cars.model,person.name from person,cars;
    tests[37].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL),
       new FQColumn(1,FIELD_PERSON_NAME)},
      new Object[]{simpleDS.getTable(TABLE_CARS),
                   simpleDS.getTable(TABLE_PERSON)},
      null,
      null,
      null);
    tests[37].data = new Object[][]
    {
      {"ford","contour","billy"},
      {"dodge","viper","billy"},
      {"dodge","stratus","billy"},
      {"dodge","ram","billy"},
      {"ford","F-150","billy"},
      {"ford","contour","george"},
      {"dodge","viper","george"},
      {"dodge","stratus","george"},
      {"dodge","ram","george"},
      {"ford","F-150","george"},
      {"ford","contour","susan"},
      {"dodge","viper","susan"},
      {"dodge","stratus","susan"},
      {"dodge","ram","susan"},
      {"ford","F-150","susan"},
      {"ford","contour","mary"},
      {"dodge","viper","mary"},
      {"dodge","stratus","mary"},
      {"dodge","ram","mary"},
      {"ford","F-150","mary"}
    };

    // select cars.make, cars.model,person.name
    // from cars inner join person on cars.owner = person.id
    tests[38].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL),
       new FQColumn(1,FIELD_PERSON_NAME)},
      new Object[]{buildJoin(simpleDS.getTable(TABLE_CARS),
          Join.INNER,simpleDS.getTable(TABLE_PERSON),
          buildEquation(new FQColumn(0,FIELD_CARS_OWNER),
                        Equation.EQUALS,
                        new FQColumn(1,FIELD_PERSON_ID)))},
      null,
      null,
      null);
    tests[38].data = new Object[][]
    {
      {"ford","contour","billy"},
      {"dodge","viper","susan"},
      {"dodge","stratus","george"},
      {"dodge","ram","susan"},
      {"ford","F-150","billy"}
    };

  // select a.s, cars.make, cars.model,person.name
  // from a,cars inner join person on cars.owner = person.id
    tests[39].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_A_S),
       new FQColumn(1,FIELD_CARS_MAKE),new FQColumn(1,FIELD_CARS_MODEL),
       new FQColumn(2,FIELD_PERSON_NAME)},
      new Object[]{simpleDS.getTable(TABLE_A),
          buildJoin(simpleDS.getTable(TABLE_CARS),
          Join.INNER,simpleDS.getTable(TABLE_PERSON),
          buildEquation(new FQColumn(1,FIELD_CARS_OWNER),
                        Equation.EQUALS,
                        new FQColumn(2,FIELD_PERSON_ID)))},
      null,
      null,
      null);
    tests[39].data = new Object[][]
    {
      {"testing","ford","contour","billy"},
      {"testing","dodge","viper","susan"},
      {"testing","dodge","stratus","george"},
      {"testing","dodge","ram","susan"},
      {"testing","ford","F-150","billy"},
      {"hi","ford","contour","billy"},
      {"hi","dodge","viper","susan"},
      {"hi","dodge","stratus","george"},
      {"hi","dodge","ram","susan"},
      {"hi","ford","F-150","billy"}
    };

    // select cars.make, cars.model,person.name,location.city,location.state
    // from cars
    // inner join person on cars.owner = person.id
    // inner join location on person.location = location.id
    // order by person.name
    tests[40].sql = buildSelect(new Object[]
      {new FQColumn(0,FIELD_CARS_MAKE),new FQColumn(0,FIELD_CARS_MODEL),
       new FQColumn(1,FIELD_PERSON_NAME),
       new FQColumn(2,FIELD_LOCATION_CITY),new FQColumn(2,FIELD_LOCATION_STATE)},
      new Object[]{
        buildJoin(
          buildJoin(
            simpleDS.getTable(TABLE_CARS),
          Join.INNER,
              simpleDS.getTable(TABLE_PERSON),
          buildEquation(
            new FQColumn(1,FIELD_CARS_OWNER),
          Equation.EQUALS,
            new FQColumn(2,FIELD_PERSON_ID))),
          Join.INNER,
        simpleDS.getTable(TABLE_LOCATION),
          buildEquation(
            new FQColumn(1,FIELD_PERSON_LOCATION),
          Equation.EQUALS,
            new FQColumn(2,FIELD_LOCATION_ID)))},
      null,
      null,
      null);
    tests[41].data = new Object[][]
    {
      {"ford","contour","billy","salt lake","UT"},
      {"ford","F-150","billy","salt lake","UT"},
      {"dodge","stratus","george","new york","NY"},
      {"dodge","viper","susan","new york","NY"},
      {"dodge","ram","susan","new york","NY"}
    };
*/
  }

  private static final int TABLE_CARS = 0;
  private static final int FIELD_CARS_ID = 0;
  private static final int FIELD_CARS_MAKE = 1;
  private static final int FIELD_CARS_MODEL = 2;
  private static final int FIELD_CARS_OWNER = 3;

  private static final int TABLE_PERSON = 1;
  private static final int FIELD_PERSON_ID = 0;
  private static final int FIELD_PERSON_NAME = 1;
  private static final int FIELD_PERSON_LOCATION = 3;

  private static final int TABLE_A = 2;
  private static final int FIELD_A_ID = 0;
  private static final int FIELD_A_S = 1;

  private static final int TABLE_LOCATION = 3;
  private static final int FIELD_LOCATION_ID = 0;
  private static final int FIELD_LOCATION_CITY = 1;
  private static final int FIELD_LOCATION_STATE = 2;

  private DataSource buildDB()
  {
    SimpleDataSource ds = new SimpleDataSource();
    ds.tables = new SimpleDataSourceTable[4];

    // cars
    ds.tables[0] = new SimpleDataSourceTable();
    ds.tables[0].name = "cars";
    ds.tables[0].columnNames = new String[]
      {"id","make","model","owner"};
    ds.tables[0].data = new Object[][]
      {
        {new Integer(1),"ford","contour",new Integer(1)},
        {new Integer(2),"dodge","viper",new Integer(3)},
        {new Integer(3),"dodge","stratus",new Integer(2)},
        {new Integer(4),"dodge","ram",new Integer(3)},
        {new Integer(5),"ford","F-150",new Integer(1)}
      };

    // person
    ds.tables[1] = new SimpleDataSourceTable();
    ds.tables[1].name = "person";
    ds.tables[1].columnNames = new String[]
      {"id","name","location"};
    ds.tables[1].data = new Object[][]
      {
        {new Integer(1),"billy",new Integer(1)},
        {new Integer(2),"george",new Integer(2)},
        {new Integer(3),"susan",new Integer(2)},
        {new Integer(4),"mary",new Integer(3)}
      };

    // a
    ds.tables[2] = new SimpleDataSourceTable();
    ds.tables[2].name = "a";
    ds.tables[2].columnNames = new String[]
      {"id","s"};
    ds.tables[2].data = new Object[][]
      {
        {new Integer(1),"testing"},
        {new Integer(2),"hi"},
      };

    // location
    ds.tables[3] = new SimpleDataSourceTable();
    ds.tables[3].name = "location";
    ds.tables[3].columnNames = new String[]
      {"id","city","state"};
    ds.tables[3].data = new Object[][]
      {
        {"1","salt lake","UT"},
        {"2","new york","NY"},
        {"3","vegas","NV"}
      };

    return ds;
  }

  private class Test
  {
    Select sql;
    Object[][] data;
  }

  private class SimpleDataSource implements DataSource
  {
    private SimpleDataSourceTable[] tables;

    public int getTableCount()
    {
      return tables.length;
    }

    public Table getTable(int index)
    {
      return tables[index];
    }
  }

  private class SimpleDataSourceTable implements Table
  {
    private String name;
    String[] columnNames;
    Object[][] data;

    public String getName()
    {
      return name;
    }

    public int getColumnCount()
    {
      return columnNames.length;
    }

    public String getColumnName(int index)
    {
      return columnNames[index];
    }

    public Data getData()
    {
      return new SimpleData(data);
    }

    public String toString()
    {
      return name;
    }
  }

  private class SimpleData implements Data
  {
    int currentRow = -1;
    Object[][] data;

    public SimpleData(Object[][] data)
    {
      this.data = data;
    }

    public boolean next()
    {
      if (currentRow+1 < data.length)
      {
        currentRow++;
        return true;
      }
      return false;
    }

    public Object get(int index)
    {
      return data[currentRow][index];
    }
  }

  private Select buildSelect(Object[] columnList, Object[] tableList,
                             Object where,FQColumn[] groupBy,Object[][] orderBy)
  {
    Select result = new Select();
    for (int i = 0; i < columnList.length; i++)
      result.addColumn(columnList[i]);
    for (int i = 0; i < tableList.length; i++)
      result.addTable(tableList[i]);
    if (where != null)
      result.setWhere(where);
    if (groupBy != null)
    {
      for (int i = 0; i < groupBy.length; i++)
        result.addGroupBy(groupBy[i]);
    }
    if (orderBy != null)
    {
      for (int i = 0; i < orderBy.length; i++)
      {
        OrderBy ob = new OrderBy();
        ob.setSort(orderBy[i][0]);
        ob.setAscending(((Boolean)orderBy[i][1]).booleanValue());
        result.addOrderBy(ob);
      }
    }
    return result;
  }

  public FunctionDef buildFunction(Function function,Object argument)
  {
    FunctionDef result = new FunctionDef();
    result.setFunction(function);
    result.setArgument(argument);
    return result;
  }

  public FunctionDef buildFunction(Aggregate function,Object argument)
  {
    FunctionDef result = new FunctionDef();
    result.setFunction(function);
    result.setArgument(argument);
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

  private Condition buildCondition(Object left, int operator, Object right)
  {
    Condition c = new Condition();
    c.setLeft(left);
    c.setOperator(operator);
    c.setRight(right);
    return c;
  }

  private Join buildJoin(Object left,int type, Table right,Equation eq)
  {
    Join join = new Join();
    join.setLeft(left);
    join.setType(type);
    join.setRight(right);
    join.setEquation(eq);
    return join;
  }
}
