#include <ome/xml/model/primitives/Color.h>

#include <cassert>
#include <sstream>
#include <iostream>
#include <stdexcept>

using ome::xml::model::primitives::Color;

void
verify (Color::component_type r,
        Color::component_type g,
        Color::component_type b,
        Color::component_type a,
        Color::composed_type  uval,
        Color::signed_type    sval,
        std::string const&    str)
{
  Color c1(r, g, b, a);
  assert(c1 == uval);
  assert(uval == c1);
  assert(c1 == sval);
  assert(sval == c1);
  assert(c1.getValue() == sval);

  Color::composed_type ct = c1;
  assert(ct == uval);
  Color::signed_type st = c1;
  assert(st == sval);

  assert(c1.getRed() == r);
  assert(c1.getGreen() == g);
  assert(c1.getBlue() == b);
  assert(c1.getAlpha() == a);

  Color c2(str);
  assert(c1 == c2);

  Color c3(uval);
  assert(c1 == c3);

  Color c4(sval);
  assert(c1 == c4);

  std::ostringstream os;
  os << c1;
  assert(os.str() == str);
}

int
main ()
{
  Color c1(0xFE438B12U);
  assert(c1.getRed() == 0xFE);
  assert(c1.getGreen() == 0x43);
  assert(c1.getBlue() == 0x8B);
  assert(c1.getAlpha() == 0x12);
  assert(c1 == 0xFE438B12U);
  assert(0xFE438B12U == c1);

  Color c2(43,23,53,1);
  assert(c1 != c2);

  try
    {
      Color c3("4265839378");
      assert(0);
    }
  catch (const std::invalid_argument& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  try
    {
      Color c3("7453894265839378", false);
      assert(0);
    }
  catch (const std::invalid_argument& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  Color c3("4265839378", false);
  assert(c3 == 0xFE438B12U);
  assert(c1 == c3);
  assert(c2 != c3);

  try
    {
      Color c4("invalid");
      assert(0);
    }
  catch (const std::invalid_argument& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  Color c5(0x01020304U);
  c5.setRed(0x20);
  c5.setGreen(0x21);
  c5.setBlue(0x22);
  c5.setAlpha(0x23);
  assert(c5 == 0x20212223U);

  Color black(0, 0, 0);
  Color black2;
  assert(black == black2);

  verify(255,   0,   0, 255, 0xFF0000FFU,  -16776961,  "-16776961"); // Red
  verify(  0, 255,   0, 255, 0x00FF00FFU,   16711935,   "16711935"); // Green
  verify(  0,   0, 255, 255, 0x0000FFFFU,      65535,      "65535"); // Blue
  verify(  0, 255, 255, 255, 0x00FFFFFFU,   16777215,   "16777215"); // Cyan
  verify(255,   0, 255, 255, 0xFF00FFFFU,  -16711681,  "-16711681"); // Magenta
  verify(255, 255,   0, 255, 0xFFFF00FFU,     -65281,     "-65281"); // Yellow
  verify(  0,   0,   0, 255, 0x000000FFU,        255,        "255"); // Black
  verify(255, 255, 255, 255, 0xFFFFFFFFU,         -1,         "-1"); // White
  verify(  0,   0,   0, 127, 0x0000007FU,        127,        "127"); // Transparent black
  verify(127, 127, 127, 127, 0x7F7F7F7FU, 2139062143, "2139062143"); // Grey

  std::cout << "OK" << std::endl;
}
