package mdbtools.jdbc.sqlparser;

public class QuotedString
{
  private String subject;

  public QuotedString()
  {
  }

  public boolean equals(Object o)
  {
    if (o instanceof QuotedString)
    {
      QuotedString qs = (QuotedString)o;
      return subject.equals(qs.subject);
    }
    else
      return false;
  }

  public String getSubject()
  {
    return subject;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

}
