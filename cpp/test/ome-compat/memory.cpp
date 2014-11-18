/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#include <ome/compat/memory.h>

#include <ome/test/test.h>

class base
{
public:
  virtual ~base() {}
};

class inh : public base
{
public:
  virtual ~inh() {}
};

class fail : public base
{
public:
  virtual ~fail() {}
};

class cshared : public std::enable_shared_from_this<cshared>
{
public:
public:
  virtual ~cshared() {}

  std::shared_ptr<cshared>
  getptr()
  { return shared_from_this(); }
};

TEST(Memory, CreateShared)
{
  std::shared_ptr<inh> p(std::make_shared<inh>());
  ASSERT_TRUE(static_cast<bool>(p));
}

TEST(Memory, CreateWeak)
{
  std::shared_ptr<inh> p(std::make_shared<inh>());
  ASSERT_TRUE(static_cast<bool>(p));

  std::shared_ptr<inh> w(p);
  std::shared_ptr<inh> p2(w);
  ASSERT_TRUE(static_cast<bool>(p2));
}

TEST(Memory, StaticPointerCast)
{
  std::shared_ptr<inh> p(std::make_shared<inh>());
  ASSERT_TRUE(static_cast<bool>(p));

  std::shared_ptr<base> b(std::static_pointer_cast<base>(p));
  ASSERT_TRUE(static_cast<bool>(b));
}

TEST(Memory, DynamicPointerCast)
{
  std::shared_ptr<base> b(std::make_shared<inh>());
  ASSERT_TRUE(static_cast<bool>(b));

  std::shared_ptr<inh> p = std::dynamic_pointer_cast<inh>(b);
  ASSERT_TRUE(static_cast<bool>(p));
}

TEST(Memory, DynamicPointerCastFail)
{
  std::shared_ptr<base> b(std::make_shared<inh>());
  ASSERT_TRUE(static_cast<bool>(b));

  std::shared_ptr<fail> f = std::dynamic_pointer_cast<fail>(b);
  ASSERT_FALSE(static_cast<bool>(f));
}

TEST(Memory, ConstPointerCast)
{
  std::shared_ptr<base> b(std::make_shared<inh>());
  ASSERT_TRUE(static_cast<bool>(b));

  std::shared_ptr<const base> c = std::const_pointer_cast<base>(b);
  ASSERT_TRUE(static_cast<bool>(c));
}

TEST(Memory, EnableSharedFromThis)
{
  std::shared_ptr<cshared> c(std::make_shared<cshared>());
  ASSERT_TRUE(static_cast<bool>(c));
  ASSERT_EQ(c.use_count(), 1);

  {
    std::shared_ptr<cshared> c2 = c->getptr();
    ASSERT_TRUE(static_cast<bool>(c2));
    ASSERT_EQ(c.use_count(), 2);
    ASSERT_EQ(c2.use_count(), 2);
  }

  ASSERT_EQ(c.use_count(), 1);
}
