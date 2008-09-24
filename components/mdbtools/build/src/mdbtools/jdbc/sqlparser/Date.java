package mdbtools.jdbc.sqlparser;

public class Date
{
  private int year;
  private int month;
  private int day;
  private int hour;
  private int minute;
  private int second;
  private int millsecond;

  public boolean equals(Object o)
  {
    if (o instanceof Date)
    {
      Date d = (Date)o;
      return (year == d.year &&
              month == d.month &&
              day == d.day &&
              hour == d.hour &&
              minute == d.minute &&
              second == d.second &&
              millsecond == d.millsecond);
    }
    else
      return false;
  }

  public int getDay()
  {
    return day;
  }

  public void setDay(int day)
  {
    this.day = day;
  }

  public void setHour(int hour)
  {
    this.hour = hour;
  }

  public int getHour()
  {
    return hour;
  }

  public int getMillsecond()
  {
    return millsecond;
  }

  public void setMillsecond(int millsecond)
  {
    this.millsecond = millsecond;
  }

  public void setMinute(int minute)
  {
    this.minute = minute;
  }

  public int getMinute()
  {
    return minute;
  }

  public int getMonth()
  {
    return month;
  }

  public void setMonth(int month)
  {
    this.month = month;
  }

  public void setSecond(int second)
  {
    this.second = second;
  }

  public int getSecond()
  {
    return second;
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }
}
