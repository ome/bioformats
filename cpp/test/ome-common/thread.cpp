/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include <boost/thread.hpp>

#include <ome/test/test.h>

namespace
{

  void
  threadtest1()
  {
  }

  class threadtest2
  {
  private:
    int a;
    int b;
    int value;
    boost::mutex value_guard;

  public:
    threadtest2(int a, int b):
      a(a),
      b(b),
      value(0),
      value_guard()
    {}

    void operator() ()
    {
      boost::lock_guard<boost::mutex> lock(value_guard);
      value = a + b;
    }

    int result()
    {
      boost::lock_guard<boost::mutex> lock(value_guard);
      return value;
    }
  };

}

TEST(Mutex, LockGuard)
{
  boost::mutex m;
  boost::lock_guard<boost::mutex> lock(m);
}

// Create thread from bare function.  Note: Could also have been a
// static class method.
TEST(Thread, Function)
{
  boost::thread foo(threadtest1);
}

// Create thread from function object.  Check state after join and use
// mutexes for testing purposes.
TEST(Thread, Object)
{
  threadtest2 t(4,55);

  // Note that boost::ref avoids a copy of the functor so the result
  // is set in the same object.
  boost::thread foo(boost::ref(t));
  foo.join();

  ASSERT_EQ(59, t.result());
}
