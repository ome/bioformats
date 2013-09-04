/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#ifndef TEST_CONSTRAINED_NUMERIC_H
#define TEST_CONSTRAINED_NUMERIC_H

#include <iostream>

template<typename T>
struct constrained_numeric_test
{
  std::string string_val;
  typename T::value_type pos_val;
  typename T::value_type less_val;
  typename T::value_type op_val;
  typename T::value_type add_result;
  typename T::value_type sub_result;
  typename T::value_type mul_result;
  typename T::value_type div_result;
  bool exception_expected;
  bool op_exception_expected;
  bool exact;
};

template<typename T>
void
verify(const constrained_numeric_test<T>& test)
{

  try{
    T str_val(test.string_val);
    T pos_val(test.pos_val);
    T less_val(test.less_val);
    T op_val(test.op_val);

    if (test.exception_expected)
      assert(0);

    try
      {
	// Test values
	assert(str_val == test.pos_val);
	assert(pos_val == test.pos_val);
	assert(less_val == test.less_val);
	assert(op_val == test.op_val);
	assert(str_val == pos_val);

	// Test operators

        // Note that exact equality comparisons should be avoided for
        // floating point types.

        if (pos_val != less_val)
          {
            assert(pos_val > less_val);
            assert(pos_val >= less_val);
            assert(pos_val > test.less_val);
            assert(pos_val >= test.less_val);

            assert(less_val < pos_val);
            assert(less_val <= pos_val);
            assert(less_val < test.pos_val);
            assert(less_val <= test.pos_val);
          }

        if (pos_val != less_val)
          {
            T plus_val(pos_val);
            plus_val = pos_val + test.op_val;
            assert(plus_val > pos_val);
            T plus_val2(pos_val);
            plus_val2 += test.op_val;
            if (test.exact)
              {
                assert(plus_val == test.add_result);
                assert(plus_val2 == test.add_result);
                assert(plus_val == plus_val2);
              }
            T plus_val3(pos_val);
            plus_val3 = pos_val + op_val;
            assert(plus_val3 > pos_val);
            T plus_val4(pos_val);
            plus_val4 += op_val;
            if (test.exact)
              {
                assert(plus_val3 == test.add_result);
                assert(plus_val4 == test.add_result);
                assert(plus_val == plus_val4);
              }

            T minus_val(pos_val);
            minus_val = pos_val - test.op_val;
           assert(minus_val < pos_val);
           T minus_val2(pos_val);
           minus_val2 -= test.op_val;
           if (test.exact)
              {
                assert(minus_val == test.sub_result);
                assert(minus_val2 == test.sub_result);
                assert(minus_val == minus_val2);
              }

            T minus_val3(pos_val);
            minus_val3 = pos_val - op_val;
            assert(minus_val3 < pos_val);
            T minus_val4(pos_val);
            minus_val4 -= op_val;
            if (test.exact)
              {
                assert(minus_val3 == test.sub_result);
                assert(minus_val4 == test.sub_result);
                assert(minus_val == minus_val4);
              }
          }

	T mul_val(pos_val);
	mul_val = pos_val * test.op_val;
	T mul_val2(pos_val);
	mul_val2 *= test.op_val;
        if (test.exact)
          {
            assert(mul_val == test.mul_result);
            assert(mul_val2 == test.mul_result);
            assert(mul_val == mul_val2);
          }

	T mul_val3(pos_val);
	mul_val3 = pos_val * op_val;
	T mul_val4(pos_val);
	mul_val4 *= op_val;
        if (test.exact)
          {
            assert(mul_val3 == test.mul_result);
            assert(mul_val4 == test.mul_result);
            assert(mul_val == mul_val4);
          }

	T div_val(pos_val);
	div_val = pos_val / test.op_val;
	T div_val2(pos_val);
	div_val2 /= test.op_val;
        if (test.exact)
          {
            assert(div_val == test.div_result);
            assert(div_val2 == test.div_result);
            assert(div_val == div_val2);
          }

	T div_val3(pos_val);
	div_val3 = pos_val / op_val;
	T div_val4(pos_val);
	div_val4 /= op_val;
        if (test.exact)
          {
            assert(div_val3 == test.div_result);
            assert(div_val4 == test.div_result);
            assert(div_val == div_val4);
          }

        if (test.op_exception_expected)
          assert(0);
      }
    catch(const std::invalid_argument& e)
      {
      if (test.op_exception_expected)
	std::cout << "Caught expected exception (1): " << e.what() << std::endl;
      else
	assert(0);
      }
    catch(...)
      {
	std::cout << "Caught unexpected exception (1)" << std::endl;
	assert(0);
      }

  }
  catch (const std::invalid_argument& e)
    {
      if (test.exception_expected)
	std::cout << "Caught expected exception (2): " << e.what() << std::endl;
      else
	assert(0);
    }

  try
    {
      T pos_val(test.pos_val);
      T less_val(test.less_val);

      // Test serialisation (in)
      std::istringstream is(test.string_val);
      T istr_val(less_val);
      is >> istr_val;
      assert(pos_val == istr_val);

      // Test serialisation (out)
      std::ostringstream os;
      os << pos_val;
      assert(os.str() == test.string_val);
    }
  catch (const std::invalid_argument& e)
    {
      std::cout << "Caught unexpected exception (2): " << e.what() << std::endl;
    }

}

#endif // TEST_CONSTRAINED_NUMERIC_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
