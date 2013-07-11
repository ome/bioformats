#include <ome/xerces/string.h>
#include <ome/xerces/platform.h>
#include <ome/xerces/error_reporter.h>

// #include <ome/xerces/dom/document.h>
// #include <ome/xerces/dom/node.h>
// #include <ome/xerces/dom/element.h>
// #include <ome/xerces/dom/node_list.h>

#include <xercesc/dom/DOM.hpp>

#include <xercesc/framework/StdOutFormatTarget.hpp>
#include <xercesc/framework/LocalFileFormatTarget.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/OutOfMemoryException.hpp>
#include <xercesc/util/XMLException.hpp>

#include <iostream>
#include <cstring>
#include <cstdlib>
#include <stdexcept>


void fatal(const char *err, const char *reason = 0)
{
  std::cerr << "E: " << err;
  if (reason)
    std::cerr << ": " << reason;
  std::cerr << std::endl;
  std::exit(1);
}

namespace xml = ome::xerces;


int main (int argc, char *argv[])
{
  if (argc != 2)
    fatal("Usage: testx file.xml");

  const char *filename = argv[1];

  try
    {
      xml::platform xmlplat;

      try
	{
	  xercesc::XercesDOMParser::ValSchemes vscheme = xercesc::XercesDOMParser::Val_Auto;  // Val_Always;
	  bool do_ns = true;
	  bool do_schema = true;
	  //bool do_valid = false;
	  bool do_fullcheck = true;
	  bool do_create = true;

	  xercesc::XercesDOMParser parser;
	  parser.setValidationScheme(vscheme);
	  parser.setDoNamespaces(do_ns);
	  parser.setDoSchema(do_schema);
	  parser.setHandleMultipleImports (true);
	  parser.setValidationSchemaFullChecking(do_fullcheck);
	  parser.setCreateEntityReferenceNodes(do_create);

	  xml::error_reporter er;
	  parser.setErrorHandler(&er);

	  std::cerr << "Set up parser\n";

	  parser.parse(filename);

	  if (er)
	    throw std::runtime_error("Parse error");

	  std::cerr << "Parsed " << filename << "\n";

	  return 0;
	}
      catch (const xercesc::XMLException &e)
	{
	  fatal("XML parse error", xml::string(e.getMessage()).str().c_str());
	}
      catch (const xercesc::DOMException &e)
	{
	  const unsigned int maxc = 2047;
	  XMLCh error[maxc + 1];

	  if (xercesc::DOMImplementation::loadDOMExceptionMsg(e.code, error, maxc))
	    fatal("XML DOM parse error", xml::string(error).str().c_str());
	  else
	    fatal("XML DOM parse error (no error message)");
	}
      catch (const xercesc::OutOfMemoryException&)
	{
	  fatal("Out of memory");
	}
      catch (const std::exception&e)
	{
	  fatal(e.what());
	}
      catch (...)
	{
	  fatal("Unknown exception");
	}
    }
  catch (const xercesc::XMLException &e)
    {
      fatal("XML parse error", xml::string(e.getMessage()).str().c_str());
    }
  return 1;
}
