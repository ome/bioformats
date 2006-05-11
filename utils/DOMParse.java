import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class DOMParse {
  public static void main(String[] args) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //factory.setValidating(true);
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(args[0]));
    System.out.println(document);
  }
}
