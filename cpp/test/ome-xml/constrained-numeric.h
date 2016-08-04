/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2015 Open Microscopy Environment:
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

#include <ome/test/test.h>

#include <cmath>
#include <sstream>
#include <vector>

enum operation
  {
    EQUAL,
    NOT_EQUAL,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    ADD,
    ADD_ASSIGN,
    SUBTRACT,
    SUBTRACT_ASSIGN,
    MULTIPLY,
    MULTIPLY_ASSIGN,
    DIVIDE,
    DIVIDE_ASSIGN,
    MODULO,
    MODULO_ASSIGN,
    INCREMENT,
    DECREMENT
  };

template <typename T>
class NumericTest : public ::testing::Test
{
public:
  struct test_str
  {
    std::string            v1;
    typename T::value_type v2;
    bool                   v1pass;
    bool                   v2pass;
  };

  struct test_op
  {
    typedef T numeric_type;
    typename T::value_type v1;
    typename T::value_type v2;
    typename T::value_type expected;
    operation              op;
    bool                   pass;
    bool                   except;
    bool                   rhs_except;
  };

  static const std::vector<test_str>  strings;
  static const std::vector<test_op>   ops;
  static const typename T::value_type error;
  static const T                      safedefault;
};

TYPED_TEST_CASE_P(NumericTest);

template<typename T>
struct CompareEqual
{
  bool compare(T lhs, T rhs) { return lhs == rhs; }
  bool compare(T lhs, typename T::value_type rhs) { return lhs == rhs; }
  bool compare(typename T::value_type lhs, T rhs) { return lhs == rhs; }
};

template<typename T>
struct CompareNotEqual
{
  bool compare(T lhs, T rhs) { return lhs != rhs; }
  bool compare(T lhs, typename T::value_type rhs) { return lhs != rhs; }
  bool compare(typename T::value_type lhs, T rhs) { return lhs != rhs; }
};

template<typename T>
struct CompareLess
{
  bool compare(T lhs, T rhs) { return lhs < rhs; }
  bool compare(T lhs, typename T::value_type rhs) { return lhs < rhs; }
  bool compare(typename T::value_type lhs, T rhs) { return lhs < rhs; }
};

template<typename T>
struct CompareLessOrEqual
{
  bool compare(T lhs, T rhs) { return lhs <= rhs; }
  bool compare(T lhs, typename T::value_type rhs) { return lhs <= rhs; }
  bool compare(typename T::value_type lhs, T rhs) { return lhs <= rhs; }
};

template<typename T>
struct CompareGreater
{
  bool compare(T lhs, T rhs) { return lhs > rhs; }
  bool compare(T lhs, typename T::value_type rhs) { return lhs > rhs; }
  bool compare(typename T::value_type lhs, T rhs) { return lhs > rhs; }
};

template<typename T>
struct CompareGreaterOrEqual
{
  bool compare(T lhs, T rhs) { return lhs >= rhs; }
  bool compare(T lhs, typename T::value_type rhs) { return lhs >= rhs; }
  bool compare(typename T::value_type lhs, T rhs) { return lhs >= rhs; }
};

template<typename Comparison, typename Test>
void
compare_test(const Test&                   /* fixture */,
             const typename Test::test_op& test)
{
  typedef typename Test::test_op::numeric_type NumericType;

  Comparison c;
  NumericType val(test.v1);

  if (test.pass)
    {
      ASSERT_TRUE(c.compare(val, NumericType(test.v2)));
      ASSERT_TRUE(c.compare(val, test.v2));
    }
  else
    {
      ASSERT_FALSE(c.compare(val, NumericType(test.v2)));
      ASSERT_FALSE(c.compare(val, test.v2));
    }
}

template<typename T>
struct OperationAdd
{
  T eval(T lhs,                      T rhs) { return lhs + rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs + rhs; }
};

template<typename T>
struct OperationAddAssign
{
  T eval(T lhs,                      T rhs) { return lhs += rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs += rhs; }
};

template<typename T>
struct OperationSubtract
{
  T eval(T lhs,                      T rhs) { return lhs - rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs - rhs; }
};

template<typename T>
struct OperationSubtractAssign
{
  T eval(T lhs,                      T rhs) { return lhs -= rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs -= rhs; }
};

template<typename T>
struct OperationMultiply
{
  T eval(T lhs,                      T rhs) { return lhs * rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs * rhs; }
};

template<typename T>
struct OperationMultiplyAssign
{
  T eval(T lhs,                      T rhs) { return lhs *= rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs *= rhs; }
};

template<typename T>
struct OperationDivide
{
  T eval(T lhs,                      T rhs) { return lhs / rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs / rhs; }
};

template<typename T>
struct OperationDivideAssign
{
  T eval(T lhs, T rhs) { return lhs /= rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs /= rhs; }
};

template<typename T>
struct OperationModulo
{
  T eval(T lhs,                      T rhs) { return lhs % rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs % rhs; }
};

template<typename T>
struct OperationModuloAssign
{
  T eval(T lhs,                      T rhs) { return lhs %= rhs; }
  T eval(T lhs, typename T::value_type rhs) { return lhs %= rhs; }
};

template<typename T>
struct OperationIncrement
{
  T eval(T lhs,                      T /* rhs */) { return ++lhs; }
  T eval(T lhs, typename T::value_type /* rhs */) { return ++lhs; }
};

template<typename T>
struct OperationDecrement
{
  T eval(T lhs,                      T /* rhs */) { return --lhs; }
  T eval(T lhs, typename T::value_type /* rhs */) { return --lhs; }
};

template<typename Operation, typename Test>
void
operation_test(const Test&                   /* fixture */,
               const typename Test::test_op& test)
{
  typedef typename Test::test_op::numeric_type NumericType;
  typedef typename Test::test_op::numeric_type::value_type ValueType;

  Operation op;
  NumericType val(test.v1);
  CompareEqual<NumericType> eq;
  CompareNotEqual<NumericType> ne;

  if (test.except)
    {
      if(!test.rhs_except)
          ASSERT_THROW(op.eval(NumericType(test.v1), NumericType(test.v2)), std::invalid_argument);
      ASSERT_THROW(op.eval(NumericType(test.v1), test.v2), std::invalid_argument);
    }
  else
    {
      if(!test.rhs_except)
        ASSERT_NO_THROW(op.eval(NumericType(test.v1), NumericType(test.v2)));
      ASSERT_NO_THROW(op.eval(NumericType(test.v1), test.v2));
      if (test.pass)
        {
          if(!test.rhs_except)
            {
              if (Test::error > 0)
                {
                  ValueType val = op.eval(NumericType(test.v1), NumericType(test.v2));
                  ASSERT_LT(std::abs(val - test.expected), Test::error);
                }
              else
                {
                  ASSERT_TRUE(eq.compare(op.eval(NumericType(test.v1), NumericType(test.v2)), test.expected));
                  ASSERT_TRUE(eq.compare(op.eval(NumericType(test.v1), NumericType(test.v2)), NumericType(test.expected)));
                }
            }
          if (Test::error > 0)
            {
              ValueType val = op.eval(NumericType(test.v1), test.v2);
              ASSERT_LT(std::abs(val - test.expected), Test::error);
            }
          else
            {
              ASSERT_TRUE(eq.compare(op.eval(NumericType(test.v1), test.v2), test.expected));
              ASSERT_TRUE(eq.compare(op.eval(NumericType(test.v1), test.v2), NumericType(test.expected)));
            }
        }
      else
        {
          if(!test.rhs_except)
            {
              if (Test::error > 0)
                {
                  ValueType val = op.eval(NumericType(test.v1), NumericType(test.v2));
                  ASSERT_GE(std::abs(val - test.expected), Test::error);
                }
              else
                {
                  ASSERT_TRUE(ne.compare(op.eval(NumericType(test.v1), NumericType(test.v2)), test.expected));
                  ASSERT_TRUE(ne.compare(op.eval(NumericType(test.v1), NumericType(test.v2)), NumericType(test.expected)));
                }
            }
          if (Test::error > 0)
            {
              ValueType val = op.eval(NumericType(test.v1), test.v2);
              ASSERT_GE(std::abs(val - test.expected), Test::error);
            }
          else
            {
              ASSERT_TRUE(ne.compare(op.eval(NumericType(test.v1), test.v2), test.expected));
              ASSERT_TRUE(ne.compare(op.eval(NumericType(test.v1), test.v2), NumericType(test.expected)));
            }
        }
    }
}

TYPED_TEST_P(NumericTest, DefaultConstruct)
{
  TypeParam v1;

  ASSERT_EQ(TypeParam::default_value, v1);
}

TYPED_TEST_P(NumericTest, Stream)
{
  for (typename std::vector<typename TestFixture::test_str>::const_iterator i = this->strings.begin();
       i != this->strings.end();
       ++i)
    {
      if (i->v1pass)
        ASSERT_NO_THROW(TypeParam(i->v1));
      else
        ASSERT_THROW(TypeParam(i->v1), std::invalid_argument);

      if (i->v2pass)
        ASSERT_NO_THROW(TypeParam(i->v2));
      else
        ASSERT_THROW(TypeParam(i->v2), std::invalid_argument);

      if (!i->v1pass || !i->v2pass) // deliberate failure
        continue;

      TypeParam v1(i->v1);
      TypeParam v2(i->v2);

      CompareEqual<TypeParam> c;

      ASSERT_TRUE(c.compare(v1, v2));
      ASSERT_TRUE(c.compare(v2, v2));
      ASSERT_TRUE(c.compare(v1, i->v2));
      ASSERT_TRUE(c.compare(v2, i->v2));

      std::istringstream is(i->v1);
      TypeParam v3(TestFixture::safedefault);
      ASSERT_NO_THROW(is >> v3);
      ASSERT_TRUE(c.compare(i->v2, v3));

      std::ostringstream os;
      ASSERT_NO_THROW(os << v1);
      ASSERT_EQ(i->v1, os.str());
    }
}

TYPED_TEST_P(NumericTest, OperatorEqual)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == EQUAL)
      compare_test<CompareEqual<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorNotEqual)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == NOT_EQUAL)
      compare_test<CompareNotEqual<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorLess)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == LESS)
      compare_test<CompareLess<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorLessOrEqual)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == LESS_OR_EQUAL)
      compare_test<CompareLessOrEqual<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorGreater)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == GREATER)
      compare_test<CompareGreater<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorGreaterOrEqual)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == GREATER_OR_EQUAL)
      compare_test<CompareGreaterOrEqual<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorAdd)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == ADD)
      operation_test<OperationAdd<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorAddAssign)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == ADD_ASSIGN)
      operation_test<OperationAddAssign<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorSubtract)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == SUBTRACT)
      operation_test<OperationSubtract<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorSubtractAssign)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == SUBTRACT_ASSIGN)
      operation_test<OperationSubtractAssign<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorMultiply)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == MULTIPLY)
      operation_test<OperationMultiply<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorMultiplyAssign)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == MULTIPLY_ASSIGN)
      operation_test<OperationMultiplyAssign<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorDivide)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == DIVIDE)
      operation_test<OperationDivide<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorDivideAssign)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == DIVIDE_ASSIGN)
      operation_test<OperationDivideAssign<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorModulo)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == MODULO)
      operation_test<OperationModulo<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorModuloAssign)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == MODULO_ASSIGN)
      operation_test<OperationModuloAssign<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorIncrement)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == INCREMENT)
      operation_test<OperationIncrement<TypeParam> >(*this, *i);
}

TYPED_TEST_P(NumericTest, OperatorDecrement)
{
  for (typename std::vector<typename TestFixture::test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    if (i->op == DECREMENT)
      operation_test<OperationDecrement<TypeParam> >(*this, *i);
}

REGISTER_TYPED_TEST_CASE_P(NumericTest,
                           DefaultConstruct,
                           Stream,
                           OperatorEqual,
                           OperatorNotEqual,
                           OperatorLess,
                           OperatorLessOrEqual,
                           OperatorGreater,
                           OperatorGreaterOrEqual,
                           OperatorAdd,
                           OperatorAddAssign,
                           OperatorSubtract,
                           OperatorSubtractAssign,
                           OperatorMultiply,
                           OperatorMultiplyAssign,
                           OperatorDivide,
                           OperatorDivideAssign,
                           OperatorModulo,
                           OperatorModuloAssign,
                           OperatorIncrement,
                           OperatorDecrement);

#endif // TEST_CONSTRAINED_NUMERIC_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
