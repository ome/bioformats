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

#include <ome/bioformats/MetadataTools.h>

#include <ome/internal/version.h>

#include <ome/test/test.h>

using ome::bioformats::createID;

TEST(MetadataToolsTest, CreateID1)
{
  std::string e1(createID("Instrument", 0));
  ASSERT_EQ(std::string("Instrument:0"), e1);

  std::string e2(createID("Instrument", 2));
  ASSERT_EQ(std::string("Instrument:2"), e2);

  std::string i1(createID("Image", 4));
  ASSERT_EQ(std::string("Image:4"), i1);
}

TEST(MetadataToolsTest, CreateID2)
{
  std::string d1(createID("Detector", 0, 0));
  ASSERT_EQ(std::string("Detector:0:0"), d1);

  std::string d2(createID("Detector", 2, 5));
  ASSERT_EQ(std::string("Detector:2:5"), d2);

  std::string i1(createID("Shape", 4, 3));
  ASSERT_EQ(std::string("Shape:4:3"), i1);
}

TEST(MetadataToolsTest, CreateID3)
{
  std::string m1(createID("Mask", 0, 0, 0));
  ASSERT_EQ(std::string("Mask:0:0:0"), m1);

  std::string m2(createID("Mask", 3, 5, 6));
  ASSERT_EQ(std::string("Mask:3:5:6"), m2);

  std::string m3(createID("Mask", 92, 329, 892));
  ASSERT_EQ(std::string("Mask:92:329:892"), m3);
}

TEST(MetadataToolsTest, CreateID4)
{
  std::string u1(createID("Unknown", 0, 0, 0, 0));
  ASSERT_EQ(std::string("Unknown:0:0:0:0"), u1);

  std::string u2(createID("Unknown", 5, 23, 6, 3));
  ASSERT_EQ(std::string("Unknown:5:23:6:3"), u2);

  std::string u3(createID("Unknown", 9, 2, 4, 2));
  ASSERT_EQ(std::string("Unknown:9:2:4:2"), u3);
}

TEST(MetadataToolsTest, ModelVersion)
{
  ASSERT_EQ(std::string(OME_MODEL_VERSION), ome::bioformats::getModelVersion());
}
