
package ome.metakit;

public class Column {

  // -- Fields --

  private String name = null;
  private String typeString = null;

  // -- Constructors --

  public Column(String definition) {
    int separator = definition.indexOf(":");
    name = definition.substring(0, separator);
    typeString = definition.substring(separator + 1);
  }

  // -- Column API methods --

  public String getName() {
    return name;
  }

  public String getTypeString() {
    return typeString;
  }

  public Class getType() {
    char typeChar = typeString.charAt(0);

    switch (typeChar) {
      case 'S':
        return String.class;
      case 'I':
        return Integer.class;
      case 'F':
        return Float.class;
      case 'D':
        return Double.class;
      case 'B':
        return Byte.class;
      case 'L':
        return Long.class;
    }
    return null;
  }

}
