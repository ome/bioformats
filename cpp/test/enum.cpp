#include <ome/xml/model/enums/EnumerationException.h>
#include <ome/xml/model/enums/LaserType.h>
#include <ome/xml/model/enums/PixelType.h>

#include <cassert>
#include <cstdlib>
#include <sstream>
#include <iostream>

using ome::xml::model::enums::LaserType;
using ome::xml::model::enums::PixelType;
using ome::xml::model::enums::EnumerationException;

template<typename E>
void
verify (E&                     et,
	typename E::enum_value valuepos,
        typename E::enum_value valueneg,
	const std::string&     namepos,
	const std::string&     nameneg)
{
  assert (static_cast<typename E::enum_value>(et) == valuepos);
  assert (static_cast<typename E::enum_value>(et) != valueneg);

  assert (namepos == static_cast<const std::string&>(et));
  assert (nameneg != static_cast<const std::string&>(et));

  typename E::enum_value etv = et;
  assert(etv == static_cast<typename E::enum_value>(et));
  assert(etv == valuepos);
  assert(etv != valueneg);

  std::string ets = et;
  assert (ets == static_cast<const std::string&>(et));
  assert (ets == namepos);
  assert (ets != nameneg);

  assert(et == valuepos);
  assert(et != valueneg);
  assert(valuepos == et);
  assert(valueneg != et);

  assert(et == namepos);
  assert(et != nameneg);
  assert(namepos == et);
  assert(nameneg != et);

  std::ostringstream os;
  os << et;
  assert(os.str() == namepos);
  assert(os.str() != nameneg);
}

int
main ()
{
  // Construction by value.

  LaserType lv(LaserType::METALVAPOR);

  verify(lv, LaserType::METALVAPOR, LaserType::EXCIMER, "MetalVapor", "Excimer");

  LaserType lv2(LaserType::METALVAPOR);
  assert(lv == lv2);
  verify(lv2, LaserType::METALVAPOR, LaserType::EXCIMER, "MetalVapor", "Excimer");

  LaserType lv3(LaserType::EXCIMER);
  assert(lv != lv3);
  verify(lv3, LaserType::EXCIMER, LaserType::GAS, "Excimer", "Gas");
  lv3=lv;
  verify(lv3, LaserType::METALVAPOR, LaserType::EXCIMER, "MetalVapor", "Excimer");

  // Construction by name, using correct, lower and mixed case.

  LaserType ln("SolidState");
  verify(ln, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  assert(lv == lv2);
  assert(lv == lv3);
  assert(lv != ln);

  try
    {
      LaserType ln2("solidstate");
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  try
    {
      LaserType ln2("solidstate", true);
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  LaserType ln2("solidstate", false);
  assert(ln == ln2);
  assert(lv != ln2);
  verify(ln2, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  try
    {
      LaserType ln3("soLidsTaTe");
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  LaserType ln3("soLidsTaTe", false);
  assert(ln == ln3);
  assert(ln2 == ln3);
  assert(lv != ln3);
  verify(ln3, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  try
    {
      LaserType ln4("    \tsolidstate", true);
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  LaserType ln4("    \tsolidstate", false);
  assert(ln == ln4);
  assert(ln2 == ln4);
  assert(lv != ln4);
  verify(ln4, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  try
    {
      LaserType ln5("SolidState      \n", true);
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  LaserType ln5("SolidState      \n", false);
  assert(ln == ln5);
  assert(ln2 == ln5);
  assert(lv != ln5);
  verify(ln5, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  try
    {
      LaserType ln6("  \f\f  solidstate    \v", true);
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  LaserType ln6("  \f\f  solidstate    \v", false);
  assert(ln == ln6);
  assert(ln2 == ln6);
  assert(lv != ln6);
  verify(ln6, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  lv2 = ln5;
  assert(lv2 == ln5);
  assert(lv2 != lv);
  verify(lv2, LaserType::SOLIDSTATE, LaserType::FREEELECTRON, "SolidState", "FreeElectron");

  // Fallback to other.

  try
    {
      LaserType linv("--invalid--", true);
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  LaserType linv("--invalid--", false);
  assert(lv != linv);
  verify(linv, LaserType::OTHER, LaserType::FREEELECTRON, "Other", "FreeElectron");

  LaserType linv2("invalid 2", false);
  assert(linv == linv2);
  assert(lv != linv2);
  verify(linv2, LaserType::OTHER, LaserType::FREEELECTRON, "Other", "FreeElectron");

  try
    {
      LaserType linv(static_cast<LaserType::enum_value>(50));
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }


  PixelType p1(PixelType::UINT16);
  verify(p1, PixelType::UINT16, PixelType::DOUBLE, "uint16", "double");

  PixelType p2(PixelType::INT32);
  assert(p2 != p1);
  verify(p2, PixelType::INT32, PixelType::DOUBLE, "int32", "double");

  PixelType p3("uint16");
  assert(p1 == p3);
  assert(p1 == p3);
  assert(p2 != p3);
  verify(p3, PixelType::UINT16, PixelType::DOUBLE, "uint16", "double");

  try
    {
      PixelType p4("UInt16");
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  PixelType p4("UInt16", false);
  assert(p1 == p4);
  assert(p3 == p4);
  verify(p4, PixelType::UINT16, PixelType::DOUBLE, "uint16", "double");

  // No fallback to other.
  try
    {
      PixelType p5("Invalid");
      assert(0);
    }
  catch (const EnumerationException& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  std::cout << "OK" << std::endl;
}
