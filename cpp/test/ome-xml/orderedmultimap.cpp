/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2016 Open Microscopy Environment:
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

#include <set>

#include <ome/xml/model/primitives/OrderedMultimap.h>

#include <ome/test/test.h>

using ome::xml::model::primitives::OrderedMultimap;
using ome::xml::model::primitives::order_index;
using ome::xml::model::primitives::key_index;
typedef OrderedMultimap OM;

TEST(OrderedMultimap, IndexAccess)
{
  OM o;
  // Insert out of (key) order.
  o.get<1>().insert(OM::value_type("instrument", "Leica"));
  o.get<1>().insert(OM::value_type("user",       "Neil"));
  o.get<1>().insert(OM::value_type("timelapse",  "true"));
  o.get<1>().insert(OM::value_type("sample",     "MCF-7"));
  o.get<1>().insert(OM::value_type("live",       "false"));
  o.get<1>().insert(OM::value_type("instrument", "Olympus"));
  o.get<1>().insert(OM::value_type("user",       "Kate"));
  o.get<1>().insert(OM::value_type("timelapse",  "true"));
  o.get<1>().insert(OM::value_type("sample",     "MIA PaCa-2"));

  EXPECT_EQ(9U, o.size());

  // Preservation of insertion order
  EXPECT_EQ(std::string("instrument"), o.get<0>().at(0).first);
  EXPECT_EQ(std::string("Leica"), o.get<0>().at(0).second);
  EXPECT_EQ(std::string("user"), o.get<0>().at(1).first);
  EXPECT_EQ(std::string("Neil"), o.get<0>().at(1).second);
  EXPECT_EQ(std::string("timelapse"), o.get<0>().at(2).first);
  EXPECT_EQ(std::string("true"), o.get<0>().at(2).second);
  EXPECT_EQ(std::string("sample"), o.get<0>().at(3).first);
  EXPECT_EQ(std::string("MCF-7"), o.get<0>().at(3).second);
  EXPECT_EQ(std::string("live"), o.get<0>().at(4).first);
  EXPECT_EQ(std::string("false"), o.get<0>().at(4).second);
  EXPECT_EQ(std::string("instrument"), o.get<0>().at(5).first);
  EXPECT_EQ(std::string("Olympus"), o.get<0>().at(5).second);
  EXPECT_EQ(std::string("user"), o.get<0>().at(6).first);
  EXPECT_EQ(std::string("Kate"), o.get<0>().at(6).second);
  EXPECT_EQ(std::string("timelapse"), o.get<0>().at(7).first);
  EXPECT_EQ(std::string("true"), o.get<0>().at(7).second);
  EXPECT_EQ(std::string("sample"), o.get<0>().at(8).first);
  EXPECT_EQ(std::string("MIA PaCa-2"), o.get<0>().at(8).second);

  EXPECT_THROW(o.get<0>().at(9), std::logic_error);

  EXPECT_EQ(std::string("instrument"), o.get<order_index>().at(0).first);
  EXPECT_EQ(std::string("Leica"), o.get<order_index>().at(0).second);
  EXPECT_EQ(std::string("user"), o.get<order_index>().at(1).first);
  EXPECT_EQ(std::string("Neil"), o.get<order_index>().at(1).second);
  EXPECT_EQ(std::string("timelapse"), o.get<order_index>().at(2).first);
  EXPECT_EQ(std::string("true"), o.get<order_index>().at(2).second);
  EXPECT_EQ(std::string("sample"), o.get<order_index>().at(3).first);
  EXPECT_EQ(std::string("MCF-7"), o.get<order_index>().at(3).second);
  EXPECT_EQ(std::string("live"), o.get<order_index>().at(4).first);
  EXPECT_EQ(std::string("false"), o.get<order_index>().at(4).second);
  EXPECT_EQ(std::string("instrument"), o.get<order_index>().at(5).first);
  EXPECT_EQ(std::string("Olympus"), o.get<order_index>().at(5).second);
  EXPECT_EQ(std::string("user"), o.get<order_index>().at(6).first);
  EXPECT_EQ(std::string("Kate"), o.get<order_index>().at(6).second);
  EXPECT_EQ(std::string("timelapse"), o.get<order_index>().at(7).first);
  EXPECT_EQ(std::string("true"), o.get<order_index>().at(7).second);
  EXPECT_EQ(std::string("sample"), o.get<order_index>().at(8).first);
  EXPECT_EQ(std::string("MIA PaCa-2"), o.get<order_index>().at(8).second);

  EXPECT_THROW(o.get<order_index>().at(9), std::logic_error);

  // Access via hashed name
  typedef OM::index<key_index>::type key_index_type;
  key_index_type::iterator i;

  // Observed and expected value sets.
  std::multiset<std::string> obs, exp;

  exp.clear();
  obs.clear();
  i = o.get<1>().find("instrument");
  EXPECT_NE(o.get<1>().end(), i);
  while(i != o.get<1>().end() &&
        i->first == "instrument")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("Leica");
  exp.insert("Olympus");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<1>().find("user");
  EXPECT_NE(o.get<1>().end(), i);
  while(i != o.get<1>().end() &&
        i->first == "user")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("Neil");
  exp.insert("Kate");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<1>().find("sample");
  EXPECT_NE(o.get<1>().end(), i);
  while(i != o.get<1>().end() &&
        i->first == "sample")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("MCF-7");
  exp.insert("MIA PaCa-2");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<1>().find("sample");
  EXPECT_NE(o.get<1>().end(), i);
  while(i != o.get<1>().end() &&
        i->first == "sample")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("MCF-7");
  exp.insert("MIA PaCa-2");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<1>().find("timelapse");
  EXPECT_NE(o.get<1>().end(), i);
  while(i != o.get<1>().end() &&
        i->first == "timelapse")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("true");
  exp.insert("true");
  EXPECT_EQ(exp, obs);

  i = o.get<1>().find("invalid");
  EXPECT_EQ(o.get<1>().end(), i);

  exp.clear();
  obs.clear();
  i = o.get<key_index>().find("instrument");
  EXPECT_NE(o.get<key_index>().end(), i);
  while(i != o.get<key_index>().end() &&
        i->first == "instrument")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("Leica");
  exp.insert("Olympus");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<key_index>().find("user");
  EXPECT_NE(o.get<key_index>().end(), i);
  while(i != o.get<key_index>().end() &&
        i->first == "user")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("Neil");
  exp.insert("Kate");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<key_index>().find("sample");
  EXPECT_NE(o.get<key_index>().end(), i);
  while(i != o.get<key_index>().end() &&
        i->first == "sample")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("MCF-7");
  exp.insert("MIA PaCa-2");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<key_index>().find("sample");
  EXPECT_NE(o.get<key_index>().end(), i);
  while(i != o.get<key_index>().end() &&
        i->first == "sample")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("MCF-7");
  exp.insert("MIA PaCa-2");
  EXPECT_EQ(exp, obs);

  exp.clear();
  obs.clear();
  i = o.get<key_index>().find("timelapse");
  EXPECT_NE(o.get<key_index>().end(), i);
  while(i != o.get<key_index>().end() &&
        i->first == "timelapse")
    {
      obs.insert(i->second);
      ++i;
    }
  exp.insert("true");
  exp.insert("true");
  EXPECT_EQ(exp, obs);

  i = o.get<key_index>().find("invalid");
  EXPECT_EQ(o.get<key_index>().end(), i);
}
